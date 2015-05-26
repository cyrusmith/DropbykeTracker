package com.dropbyke.tracker.api;

import android.content.Context;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import com.dropbyke.tracker.AssertUtils;
import com.dropbyke.tracker.AsyncTaskResult;
import com.dropbyke.tracker.AuthResponseDTO;
import com.dropbyke.tracker.Constants;
import com.google.gson.Gson;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AuthTask extends AsyncTask<AuthDTO, Void, AsyncTaskResult<TokenDTO>> {

    private final Context context;
    private final String username;
    private final String password;

    private final Gson gson = new Gson();

    public AuthTask(Context context, String username, String password) {
        AssertUtils.notNull(context);
        AssertUtils.notNull(username);
        AssertUtils.notNull(password);
        this.context = context;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Sending data to server", Toast.LENGTH_LONG).show();
    }

    @Override
    protected AsyncTaskResult<TokenDTO> doInBackground(AuthDTO... params) {
        AssertUtils.state(params.length > 0);
        AuthDTO dto = params[0];

        HttpURLConnection urlConnection = null;
        InputStream is = null;
        try {
            URL url = new URL(Constants.BASE_URL + "/auth");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setConnectTimeout(10000);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            String json = gson.toJson(dto);
            Log.d(Constants.LOG, "Sending " + json);
            out.write(json.getBytes());
            out.flush();
            out.close();

            int code = urlConnection.getResponseCode();
            Log.d(Constants.LOG, "Received code " + code);

            boolean isSuccess = code > 199 && code < 300;

            if (isSuccess) {
                is = urlConnection.getInputStream();
            } else {
                is = urlConnection.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Log.d(Constants.LOG, "Received " + sb);

            if (isSuccess) {
                AuthResponseDTO authResponseDTO = gson.fromJson(sb.toString(), AuthResponseDTO.class);
                if (authResponseDTO.data instanceof TokenDTO) {
                    return new AsyncTaskResult<TokenDTO>(authResponseDTO.data);
                }
                return new AsyncTaskResult<TokenDTO>(new Exception("Could not receive auth token"));
            } else {
                ResponseDTO responseDTO = gson.fromJson(sb.toString(), ResponseDTO.class);
                return new AsyncTaskResult<TokenDTO>(new Exception(responseDTO.message != null ? responseDTO.message : "Response " + code));
            }

        } catch (IOException e) {
            Log.e(Constants.LOG, "Error sending request: " + e.getLocalizedMessage());
        } finally {
            if (urlConnection != null)
                try {
                    urlConnection.disconnect();
                } catch (RuntimeException e) {
                    Log.e(Constants.LOG, "Error disconnectiong: " + e.getMessage());
                }

        }

        return new AsyncTaskResult<TokenDTO>(new Exception("Could not authenticate"));
    }

}

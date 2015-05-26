package com.dropbyke.tracker.api;

import android.util.Log;

import com.dropbyke.tracker.Constants;
import com.dropbyke.tracker.api.UpdateDTO;
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
public class UpdateUploader {

    public static boolean upload(String token, String trackerId, UpdateDTO updateDTO) {
        final Gson gson = new Gson();
        HttpURLConnection urlConnection = null;
        InputStream is;
        try {
            URL url = new URL(Constants.BASE_URL + "/trackers/" + trackerId + "/tracks");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setConnectTimeout(10000);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            String json = String.format("[%s]", gson.toJson(updateDTO));
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

            return isSuccess;

        } catch (IOException e) {
            Log.e(Constants.LOG, "Error sending update: " + e.getLocalizedMessage());
            return false;
        } finally {
            if (urlConnection != null)
                try {
                    urlConnection.disconnect();
                } catch (RuntimeException e) {
                    Log.e(Constants.LOG, "Error disconnectiong: " + e.getMessage());
                }

        }
    }

}

package com.dropbyke.tracker;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AuthTask extends AsyncTask<AuthDTO, Void, TokenDTO> {

    private final Context context;

    public AuthTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Sending data to server", Toast.LENGTH_LONG).show();
    }

    @Override
    protected TokenDTO doInBackground(AuthDTO... params) {

        //TODO
        return new TokenDTO("121212112");
    }

}

package com.dropbyke.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private EditText txtTrackerId;
    private EditText txtBikeSerial;
    private EditText txtShepardPhone;
    private EditText txtShepardPassword;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTrackerId = (EditText) findViewById(R.id.txtTrackerId);
        txtBikeSerial = (EditText) findViewById(R.id.txtBikeSerial);
        txtShepardPhone = (EditText) findViewById(R.id.txtShepardPhone);
        txtShepardPassword = (EditText) findViewById(R.id.txtShepardPassword);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btnUpdate) return;
        final String trackerId = txtTrackerId.getText().toString();
        final String bikeSerial = txtBikeSerial.getText().toString();
        final String shepardPhone = txtShepardPhone.getText().toString();
        final String shepardPassword = txtShepardPassword.getText().toString();

        List<String> errors = new ArrayList<>();
        if (TextUtils.isEmpty(trackerId)) errors.add("Tracker id not set");
        if (TextUtils.isEmpty(bikeSerial)) errors.add("Bike Serial not set");
        if (TextUtils.isEmpty(shepardPhone)) errors.add("Shepard Phone not set");
        if (TextUtils.isEmpty(shepardPassword)) errors.add("Shepard Password not set");

        if (errors.size() > 0) {
            Toast.makeText(this, TextUtils.join("\\n ", errors), Toast.LENGTH_LONG).show();
            return;
        }

        new AuthTask(this) {
            @Override
            protected void onPostExecute(TokenDTO tokenDTO) {
                super.onPostExecute(tokenDTO);
                Log.d("Tracker", tokenDTO.toString());
                if (TextUtils.isEmpty(tokenDTO.getToken())) {
                    Toast.makeText(MainActivity.this, "Could not authenticate. Check you credentials", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(MainActivity.this, "Updating tracker", Toast.LENGTH_LONG).show();
                    txtTrackerId.setText("");
                    txtBikeSerial.setText("");
                    txtShepardPhone.setText("");
                    txtShepardPassword.setText("");
                }
                Intent intent = new Intent(MainActivity.this, MotionService.class);
                intent.putExtra("token", tokenDTO.getToken());
                startService(intent);
            }
        }.execute(new AuthDTO(
                trackerId,
                bikeSerial,
                shepardPhone,
                shepardPassword
        ));
    }
}

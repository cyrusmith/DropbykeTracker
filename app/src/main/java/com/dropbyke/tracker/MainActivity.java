package com.dropbyke.tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

    private ProgressDialog dialog;

    private LocationManager mLocationManager;

    private Location mLocation = null;

    final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(final Location loc) {
            if (loc == null) return;
            mLocation = loc;
            hideDialog();
            mLocationManager.removeUpdates(locationListener);
        }

        public void onStatusChanged(final String provider, final int status, Bundle extras) {
        }

        public void onProviderEnabled(final String provider) {
        }

        public void onProviderDisabled(final String provider) {
            Log.e(Constants.LOG, "onProviderDisabled:" + provider);
            hideDialog();
            toast("Failed to get your location. Reload the app and try again.", Toast.LENGTH_LONG);
        }
    };


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

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast("GPS is disbled!!! Check your device settings", Toast.LENGTH_LONG);
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLocation == null) {
            showProgress("Getting your location");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, locationListener);
        }

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

    private void showProgress(String msg) {
        if (dialog != null) dialog.dismiss();
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();

    }

    private void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btnUpdate) return;

        if (mLocation == null) {
            toast("Your location is unknown. Reload the app.", Toast.LENGTH_LONG);
            return;
        }

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
            Toast.makeText(this, TextUtils.join(", ", errors), Toast.LENGTH_LONG).show();
            return;
        }

        new AuthTask(this, shepardPhone, shepardPassword) {

            protected void onPreExecute() {
                showProgress("Uploading...");
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TokenDTO> result) {
                super.onPostExecute(result);
                hideDialog();
                if (result.getError() != null) {
                    toast(result.getError().getMessage(), Toast.LENGTH_LONG);
                    return;
                }

                TokenDTO tokenDTO = result.getResult();

                if (tokenDTO == null) {
                    Log.d("Tracker", "tokenDTO is null");
                    return;
                }

                Log.d("Tracker", tokenDTO.toString());

                if (TextUtils.isEmpty(tokenDTO.getToken())) {
                    toast("Could not authenticate. Check you credentials", Toast.LENGTH_LONG);
                    return;
                } else {
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
                shepardPassword,
                new UpdateDTO(
                        mLocation.getLatitude(),
                        mLocation.getLongitude(),
                        DeviceInfo.getBatteryLevel(MainActivity.this),
                        0.0,
                        System.currentTimeMillis()
                )));
    }

    private void toast(final String text, final int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, length).show();
            }
        });
    }

}

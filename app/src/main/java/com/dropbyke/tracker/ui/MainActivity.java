package com.dropbyke.tracker.ui;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbyke.tracker.Constants;
import com.dropbyke.tracker.MotionService;
import com.dropbyke.tracker.R;
import com.dropbyke.tracker.api.TrackerDTO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<TrackerDTO>> {

    private Button mRestartBtn;
    private TextView mActiveTrackerText;

    private ProgressDialog dialog;

    private LocationManager mLocationManager;

    private Location mLocation = null;

    private TrackersSpinnerAdapter mTrackersAdapter = null;

    private Spinner mSpinner;

    private MotionService mMotionService;

    private boolean mMotionServiceBound;

    private ServiceConnection mMotionServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MotionService.MotionServiceBinder binder = (MotionService.MotionServiceBinder) service;
            mMotionService = binder.getService();
            mMotionServiceBound = true;
            mRestartBtn.setEnabled(!mMotionService.isListening());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mMotionServiceBound = false;
        }
    };

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
        mRestartBtn = (Button) findViewById(R.id.restart_button);
        mActiveTrackerText = (TextView) findViewById(R.id.active_tracker_text);
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(Constants.LOCATION_PROVIDER)) {
            toast("GPS is disbled!!! Check your device settings", Toast.LENGTH_LONG);
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        mLocation = mLocationManager.getLastKnownLocation(Constants.LOCATION_PROVIDER);
        if (mLocation == null) {
            showProgress("Getting your location");
            mLocationManager.requestLocationUpdates(Constants.LOCATION_PROVIDER, 3000, 1, locationListener);
        }

        mRestartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocation == null) {
                    Toast.makeText(MainActivity.this, "Did not get location yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                startService(new Intent(getApplicationContext(), MotionService.class));
                mRestartBtn.setEnabled(false);
            }
        });

        mSpinner = (Spinner) findViewById(R.id.spinner);

        mTrackersAdapter = new TrackersSpinnerAdapter(this, android.R.layout.simple_spinner_item, new ArrayList<TrackerDTO>());

        mTrackersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(mTrackersAdapter);

        getSupportLoaderManager().initLoader(0, null, this);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TrackerDTO trackerDTO = mTrackersAdapter.getItem(position);
                getApplicationContext()
                        .getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                        .edit()
                        .putString(Constants.TRACKER_ID, trackerDTO.getId())
                        .commit();

                mActiveTrackerText.setText(trackerDTO.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        bindService(new Intent(this, MotionService.class), mMotionServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMotionServiceBound) {
            unbindService(mMotionServiceConnection);
            mMotionServiceBound = false;
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
        int id = item.getItemId();
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

    private void toast(final String text, final int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, length).show();
            }
        });
    }

    @Override
    public Loader<List<TrackerDTO>> onCreateLoader(int id, Bundle args) {
        return new TrackerLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<TrackerDTO>> loader, List<TrackerDTO> data) {
        mTrackersAdapter.clear();
        String trackerId = getTrackerId();
        int pos = 0;
        for (TrackerDTO trackerDTO : data) {
            mTrackersAdapter.add(trackerDTO);
            if (!TextUtils.isEmpty(trackerId)
                    && !TextUtils.isEmpty(trackerDTO.getId())
                    && trackerId.equals(trackerDTO.getId())) {
                mSpinner.setSelection(pos);
                mActiveTrackerText.setText(trackerDTO.getName());
            }
            pos++;
        }
        mTrackersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<TrackerDTO>> loader) {
        mTrackersAdapter.clear();
        mTrackersAdapter.notifyDataSetChanged();
    }

    private String getTrackerId() {
        return getApplicationContext()
                .getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                .getString(Constants.TRACKER_ID, null);
    }

}

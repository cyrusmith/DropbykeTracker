package com.dropbyke.tracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.dropbyke.tracker.event.LocationChangeEvent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MotionService extends Service implements SensorEventListener {

    private static final int NOTIFICATION_ID = 1;

    private LocationManager mLocationManager;
    private SensorManager mSm;

    private boolean mIsListening = false;

    private String token;

    private Executor mUploadExecutor = Executors.newSingleThreadExecutor();

    private GetLocationTask {}

    private final Runnable uploadTask = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(token)) return;

            FutureTask<Location> task = new FutureTask<Location>(new Callable<Location>() {
                @Override
                public Location call() throws Exception {
                    return null;
                }
            });

            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    };

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(final Location location) {
            if (location == null) return;
            new AsyncTask<Location, Void, Long>() {
                @Override
                protected Long doInBackground(Location... params) {
                    final Location loc = params[0];
                    return DropbykeTrackerApplication.db().appendEvent(new LocationChangeEvent(loc.getLatitude(), loc.getLongitude()));
                }
            }.execute(location);
        }

        public void onStatusChanged(final String provider, final int status, Bundle extras) {
        }

        public void onProviderEnabled(final String provider) {
            if (Log.isLoggable("Dropbyke", Log.DEBUG))
                Log.d("Dropbyke", "onProviderEnabled:" + provider);

            startForeground(NOTIFICATION_ID, buildNotificationOk());
        }

        public void onProviderDisabled(final String provider) {
            if (Log.isLoggable("Dropbyke", Log.DEBUG)) {
                Log.d("Dropbyke", "onProviderDisabled:" + provider);
            }
            startForeground(NOTIFICATION_ID, buildNotificationFail());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mSm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String token = extras.getString("token");
                if (!TextUtils.isEmpty(token)) {
                    this.token = token;
                }
            }
        }

        if (!mIsListening) {
            //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, locationListener);
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, locationListener);
            mSm.registerListener(this,
                    mSm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);

            mIsListening = true;
        }

        return Service.START_STICKY;
    }

    private Notification buildNotificationOk() {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_tracking)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0))
                .setContentTitle("Tracking")
                .setContentText("GPS and accelerometer tracking is active").build();
    }

    private Notification buildNotificationFail() {
        return new NotificationCompat.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0))
                .setSmallIcon(R.drawable.ic_gps_off)
                .setContentTitle("GPS is disabled")
                .setContentText("Enable GPS in device settings").build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private float[] gravity = new float[3];

    public static final int MIN_UPDATE_INTERVAL = 30;

    private long lastUploadTimestamp = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {

        final float alpha = 0.8F;

        float[] linear_acceleration = new float[3];

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        double x = linear_acceleration[0];
        double y = linear_acceleration[1];
        double z = linear_acceleration[2];
        double a = Math.round(Math.sqrt(Math.pow(x, 2) +
                Math.pow(y, 2) +
                Math.pow(z, 2)));

        if (a > 1.0) {
            if (System.currentTimeMillis() - lastUploadTimestamp > MIN_UPDATE_INTERVAL * 1000) {
                lastUploadTimestamp = System.currentTimeMillis();
                if (!TextUtils.isEmpty(token))
                    mUploadExecutor.execute(uploadTask);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

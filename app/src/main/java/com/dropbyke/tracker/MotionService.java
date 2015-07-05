package com.dropbyke.tracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dropbyke.tracker.api.UpdateDTO;
import com.dropbyke.tracker.api.API;
import com.dropbyke.tracker.ui.MainActivity;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MotionService extends Service implements SensorEventListener {

    private static final int NOTIFICATION_ID = 1;

    private static final int LOCATION_TIMEOUT = 10;

    public static final int MIN_UPDATE_INTERVAL = 5;

    public class MotionServiceBinder extends Binder {
        public MotionService getService() {
            return MotionService.this;
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private LocationManager mLocationManager;
    private SensorManager mSm;

    private boolean mIsListening = false;

    private final Gson gson = new Gson();

    private float[] gravity = new float[3];

    private long lastUploadTimestamp = 0;

    private double locationAwaitTimeout = LOCATION_TIMEOUT;

    private boolean isUploading;

    private ExecutorService mUploadPrepareExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService mLocationGetExecutor = Executors.newSingleThreadExecutor();

    private final Callable<Location> mGetLocationCallable = new Callable<Location>() {
        @Override
        public Location call() throws Exception {

            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<Location> locRef = new AtomicReference<>();

            final LocationListener locationListener = new LocationListener() {

                public void onLocationChanged(final Location location) {
                    if (location == null) return;
                    locRef.set(location);
                    latch.countDown();
                }

                public void onStatusChanged(final String provider, final int status, Bundle extras) {
                }

                public void onProviderEnabled(final String provider) {
                }

                public void onProviderDisabled(final String provider) {
                    Log.e(Constants.LOG, "onProviderDisabled:" + provider);
                    latch.countDown();
                }
            };

            mLocationManager.requestLocationUpdates(Constants.LOCATION_PROVIDER, 3000, 1, locationListener, Looper.getMainLooper());

            try {
                latch.await((long) locationAwaitTimeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e(Constants.LOG, "Could not get location, thread interrupted.");
            }
            if (locRef.get() == null) {
                locationAwaitTimeout = Math.ceil(locationAwaitTimeout * 1.5);
                Log.d(Constants.LOG, "New locationAwaitTimeout = " + locationAwaitTimeout);
            } else {
                locationAwaitTimeout = LOCATION_TIMEOUT;
            }
            mLocationManager.removeUpdates(locationListener);

            return locRef.get();

        }
    };

    private class UploadTask implements Callable<Boolean> {

        private final String trackerId;

        public UploadTask(String trackerId) {
            AssertUtils.notNull(trackerId);
            this.trackerId = trackerId;
        }

        @Override
        public Boolean call() throws Exception {

            Location location = null;
            try {
                location = mLocationGetExecutor.submit(mGetLocationCallable).get();
            } catch (Exception e) {
                StackTraceElement[] els = e.getStackTrace();
                for (StackTraceElement el : els) {
                    Log.e(Constants.LOG, el.getClassName() + " " + el.getMethodName() + " " + el.getLineNumber());
                }
                Log.e(Constants.LOG, "Error getting location: " + e.getLocalizedMessage());
            }

            if (location == null) {
                Log.e(Constants.LOG, "Location is null");
                return false;
            }

            final UpdateDTO updateDTO = new UpdateDTO(
                    location.getLatitude(),
                    location.getLongitude(),
                    DeviceInfo.getBatteryLevel(MotionService.this));

            if (!TextUtils.isEmpty(trackerId) && DeviceInfo.isOnline(MotionService.this)) {
                return API.upload(this.trackerId, updateDTO);
            }

            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mSm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(NOTIFICATION_ID, buildNotificationOk());

        if (!mLocationManager.isProviderEnabled(Constants.LOCATION_PROVIDER)) {
            toast("GPS is disbled!!! Check your device settings", Toast.LENGTH_LONG);
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            stopSelf(startId);
            return Service.START_STICKY;
        }

        if (!mIsListening) {

            mSm.registerListener(this,
                    mSm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);


            mIsListening = true;
        }

        return Service.START_STICKY;
    }

    public boolean isListening() {
        return mIsListening;
    }

    private void toast(final String text, final int length) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MotionService.this, text, length).show();
            }
        });
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
        return new MotionServiceBinder();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isUploading) return;

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

        Log.d(Constants.LOG, "onSensorChanged a = " + a + " time = " + (System.currentTimeMillis() - lastUploadTimestamp) + " tracker_id = " + readTrackerId());

        if (a > 1.0) {

            if (System.currentTimeMillis() - lastUploadTimestamp > MIN_UPDATE_INTERVAL * 1000) {
                lastUploadTimestamp = System.currentTimeMillis();
                final String trackerId = readTrackerId();
                if (!TextUtils.isEmpty(trackerId)) {
                    isUploading = true;
                    final Future<Boolean> uploadFuture = mUploadPrepareExecutor.submit(new UploadTask(trackerId));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                uploadFuture.get();
                            } catch (InterruptedException e) {
                                Log.e(Constants.LOG, e.getLocalizedMessage());
                            } catch (ExecutionException e) {
                                Log.e(Constants.LOG, e.getLocalizedMessage());
                            } finally {
                                Log.d(Constants.LOG, "Unlock uploading");
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        isUploading = false;
                                    }
                                });
                            }
                        }
                    }).start();

                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String readTrackerId() {
        return getApplicationContext()
                .getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                .getString(Constants.TRACKER_ID, null);
    }

}

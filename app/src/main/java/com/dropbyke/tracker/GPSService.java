package com.dropbyke.tracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dropbyke.tracker.event.LocationChangeEvent;

public class GPSService extends Service {

    private static final int NOTIFICATION_ID = 1;

    private LocationManager mLocationManager;

    private boolean mIsListening = false;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mIsListening) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, locationListener);
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, locationListener);
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


}

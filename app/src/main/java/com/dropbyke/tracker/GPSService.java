package com.dropbyke.tracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class GPSService extends Service {

    private static final int NOTIFICATION_ID = 1;

    Handler handler = new Handler(Looper.getMainLooper());

    private LocationManager mLocationManager;

    private boolean mIsListening = false;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(final Location location) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GPSService.this, "Updated location: " + location.getLatitude() + " " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void onStatusChanged(final String provider, final int status, Bundle extras) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GPSService.this, "onStatusChanged: " + provider + " " + status, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void onProviderEnabled(final String provider) {
            startForeground(NOTIFICATION_ID, buildNotificationOk());
        }

        public void onProviderDisabled(final String provider) {
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
            mIsListening = true;
        }

        return Service.START_STICKY;
    }

    private Notification buildNotificationOk() {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_tracking)
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

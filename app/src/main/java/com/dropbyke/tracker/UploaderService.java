package com.dropbyke.tracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UploaderService extends Service {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture updateHandler;

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            if (Log.isLoggable("Dropbyke", Log.DEBUG))
                Log.d("Dropbyke", "**Upload data **");
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateHandler = scheduler.scheduleAtFixedRate(updateTask, 5, 5, TimeUnit.SECONDS);
        startForeground(2, new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_upload)
                .setContentTitle("Uploading")
                .setContentText("Dropbyke tracker is uploading data").build());

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateHandler != null) {
            updateHandler.cancel(true);
        }
        if (!scheduler.isShutdown())
            scheduler.shutdownNow();
    }
}

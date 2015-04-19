package com.dropbyke.tracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AccelerometerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package com.dropbyke.tracker;

import android.app.Application;

public class DropbykeTrackerApplication extends Application {

    private static DBHelper dbHelper;

    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (lock) {
            dbHelper = new DBHelper(this);
        }
    }

    public static DBHelper db() {
        synchronized (lock) {
            return dbHelper;
        }
    }

}

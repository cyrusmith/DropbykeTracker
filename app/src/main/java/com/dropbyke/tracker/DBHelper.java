package com.dropbyke.tracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.dropbyke.tracker.event.TrackingEvent;

/**
 * Created by cyrusmith on 19.04.15.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "dropbyketracker.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS events (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY," +
                "type TEXT," +
                "body TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void appendEvent(TrackingEvent trackingEvent) {

    }

}

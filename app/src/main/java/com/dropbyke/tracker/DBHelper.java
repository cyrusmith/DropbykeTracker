package com.dropbyke.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.dropbyke.tracker.event.TrackingEvent;
import com.google.gson.Gson;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "dropbyketracker.db";

    private static final String TABLE_EVENTS = "events";

    private final Gson gson = new Gson();

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY," +
                "type TEXT," +
                "body TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long appendEvent(TrackingEvent trackingEvent) {
        if (Log.isLoggable("Dropbyke", Log.DEBUG)) {
            Log.d("Dropbyke", "appendEvent :" + trackingEvent);
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            final String type = trackingEvent.getClass().getSimpleName();
            final String body = gson.toJson(trackingEvent);

            if (Log.isLoggable("Dropbyke", Log.DEBUG)) {
                Log.d("Dropbyke", "type: " + type + ", body: " + body);
            }

            ContentValues values = new ContentValues();
            values.put("type", type);
            values.put("body", body);

            long eventId = db.insert(TABLE_EVENTS, null, values);
            if (eventId > 0) {
                if (Log.isLoggable("Dropbyke", Log.DEBUG)) {
                    Log.d("Dropbyke", "Saved event with id " + eventId);
                }
                db.setTransactionSuccessful();
                return eventId;
            }

        } catch (Exception e) {
            Log.e("Dropbyke", e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        return -1;

    }

}

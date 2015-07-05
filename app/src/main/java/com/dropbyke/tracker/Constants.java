package com.dropbyke.tracker;

import android.location.LocationManager;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class Constants {

    public static final String LOG = "Tracker";

    public static final String PREFS = "trackerprefs";
    public static final String TRACKER_ID = "tracker_id";

    public static final String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;
    //public static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    //public static final String BASE_URL = "http://api.dropbyke.com:8071";
    public static final String BASE_URL = "http://192.168.1.102:8071";

}

package com.dropbyke.tracker.event;

public class LocationChangeEvent extends TrackingEvent {

    private final double lat;
    private final double lng;

    public LocationChangeEvent(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}

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

    @Override
    public String toString() {
        return new StringBuffer()
                .append("LocationChangeEvent")
                .append("{")
                .append("created").append(":").append(" ").append(getCreated())
                .append(", ").append(" ")
                .append("lat").append(":").append(" ").append(lat)
                .append(", ").append(" ")
                .append("lng").append(":").append(" ").append(lng)
                .append("}").toString();
    }
}

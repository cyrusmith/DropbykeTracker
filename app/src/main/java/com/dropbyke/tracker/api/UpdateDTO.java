package com.dropbyke.tracker.api;

import java.io.Serializable;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class UpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double lat;
    private final double lng;
    private final int charge;

    public UpdateDTO(double lat,
                     double lng,
                     int charge) {
        this.lat = lat;
        this.lng = lng;
        this.charge = charge;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getCharge() {
        return charge;
    }

    @Override
    public String toString() {
        return "UpdateDTO{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", charge=" + charge +
                '}';
    }
}

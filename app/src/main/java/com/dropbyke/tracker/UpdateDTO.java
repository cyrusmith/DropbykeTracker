package com.dropbyke.tracker;

import java.io.Serializable;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class UpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double lat;
    private final double lng;
    private final int charge;
    private final double accel;
    private final long ts;

    public UpdateDTO(double lat,
                     double lng,
                     int charge,
                     double accel,
                     long ts) {
        this.lat = lat;
        this.lng = lng;
        this.charge = charge;
        this.accel = accel;
        this.ts = ts;
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

    public double getAccel() {
        return accel;
    }

    public long getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "UpdateDTO{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", charge=" + charge +
                ", accel=" + accel +
                ", ts=" + ts +
                '}';
    }
}

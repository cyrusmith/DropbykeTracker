package com.dropbyke.tracker;

import java.io.Serializable;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AuthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String trackerId;
    private final String bikeSerial;
    private final String shepardPhone;
    private final String shepardPassword;

    public AuthDTO(String trackerId,
                   String bikeSerial,
                   String shepardPhone,
                   String shepardPassword) {
        AssertUtils.notNull(trackerId);
        AssertUtils.notNull(bikeSerial);
        AssertUtils.notNull(shepardPhone);
        AssertUtils.notNull(shepardPassword);
        this.trackerId = trackerId;
        this.bikeSerial = bikeSerial;
        this.shepardPhone = shepardPhone;
        this.shepardPassword = shepardPassword;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public String getBikeSerial() {
        return bikeSerial;
    }

    public String getShepardPhone() {
        return shepardPhone;
    }

    public String getShepardPassword() {
        return shepardPassword;
    }
}

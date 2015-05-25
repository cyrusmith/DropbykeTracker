package com.dropbyke.tracker;

import java.io.Serializable;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AuthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String trackerId;
    private final String bikeSerial;
    private final String username;
    private final String password;

    private final UpdateDTO update;

    public AuthDTO(String trackerId,
                   String bikeSerial,
                   String shepardPhone,
                   String shepardPassword, UpdateDTO update) {
        AssertUtils.notNull(trackerId);
        AssertUtils.notNull(bikeSerial);
        AssertUtils.notNull(shepardPhone);
        AssertUtils.notNull(shepardPassword);
        AssertUtils.notNull(update);
        this.trackerId = trackerId;
        this.bikeSerial = bikeSerial;
        this.username = shepardPhone;
        this.password = shepardPassword;
        this.update = update;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public String getBikeSerial() {
        return bikeSerial;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UpdateDTO getUpdate() {
        return update;
    }
}

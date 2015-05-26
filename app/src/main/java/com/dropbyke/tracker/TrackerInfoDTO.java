package com.dropbyke.tracker;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by cyrusmith on 26.05.15.
 */
public class TrackerInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String token;
    private final String trackerId;

    public TrackerInfoDTO(String token, String trackerId) {
        AssertUtils.state(!TextUtils.isEmpty(token));
        AssertUtils.state(!TextUtils.isEmpty(trackerId));
        this.token = token;
        this.trackerId = trackerId;
    }

    public String getToken() {
        return token;
    }

    public String getTrackerId() {
        return trackerId;
    }

    @Override
    public String toString() {
        return "TrackerInfoDTO{" +
                "token='" + token + '\'' +
                ", trackerId='" + trackerId + '\'' +
                '}';
    }
}

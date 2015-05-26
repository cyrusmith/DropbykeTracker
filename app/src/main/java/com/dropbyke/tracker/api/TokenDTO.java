package com.dropbyke.tracker.api;

import java.io.Serializable;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class TokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String token;

    public TokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "TokenDTO{" +
                "token='" + token + '\'' +
                '}';
    }
}

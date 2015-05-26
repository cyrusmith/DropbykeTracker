package com.dropbyke.tracker;

import com.dropbyke.tracker.api.TokenDTO;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AuthResponseDTO {

    public boolean status;
    public String message;
    public TokenDTO data;


    @Override
    public String toString() {
        return "AuthResponseDTO{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", token=" + data +
                '}';
    }
}

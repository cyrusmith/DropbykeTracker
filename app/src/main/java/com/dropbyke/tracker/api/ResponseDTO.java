package com.dropbyke.tracker.api;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class ResponseDTO {

    public boolean status;
    public String message;

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}

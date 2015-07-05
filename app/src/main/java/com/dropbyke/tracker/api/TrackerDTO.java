package com.dropbyke.tracker.api;

import java.io.Serializable;

/**
 * Created by cyrusmith on 05.07.15.
 */
public class TrackerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;

    public TrackerDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}

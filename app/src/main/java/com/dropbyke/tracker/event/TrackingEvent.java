package com.dropbyke.tracker.event;

import java.util.Date;

public class TrackingEvent {

    private final Date created;

    public TrackingEvent() {
        this.created = new Date();
    }

    public Date getCreated() {
        return created;
    }
}

package com.dropbyke.tracker.event;

public class TrackingEvent {

    private final long created;

    public TrackingEvent() {
        this.created = System.currentTimeMillis();
    }

    public long getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append(getClass().getSimpleName())
                .append("{")
                .append("created").append(":").append(" ").append(getCreated())
                .append("}").toString();
    }

}

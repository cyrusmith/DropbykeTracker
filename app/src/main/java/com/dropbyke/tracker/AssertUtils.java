package com.dropbyke.tracker;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AssertUtils {

    public static final void notNull(Object o) {
        if (o == null) throw new IllegalArgumentException("cannot be null");
    }
}

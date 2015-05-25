package com.dropbyke.tracker;

/**
 * Created by cyrusmith on 25.05.15.
 */
public class AsyncTaskResult<T> {

    private final T result;
    private final Throwable error;

    public AsyncTaskResult(T result, Throwable error) {
        this.result = result;
        this.error = error;
    }

    public AsyncTaskResult(T result) {
        this(result, null);
    }

    public AsyncTaskResult(Throwable error) {
        this(null, error);
    }

    public T getResult() {
        return result;
    }

    public Throwable getError() {
        return error;
    }
}

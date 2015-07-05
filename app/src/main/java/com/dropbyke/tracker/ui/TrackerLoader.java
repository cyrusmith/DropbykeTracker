package com.dropbyke.tracker.ui;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.dropbyke.tracker.api.API;
import com.dropbyke.tracker.api.TrackerDTO;

import java.util.List;

/**
 * Created by cyrusmith on 05.07.15.
 */
public class TrackerLoader extends AsyncTaskLoader<List<TrackerDTO>> {

    public TrackerLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<TrackerDTO> loadInBackground() {
        final List<TrackerDTO> dtos = API.loadTrackers();
        dtos.add(0, new TrackerDTO(null, "--Not set--"));
        return dtos;
    }

}

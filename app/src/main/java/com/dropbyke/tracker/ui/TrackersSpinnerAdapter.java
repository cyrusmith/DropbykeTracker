package com.dropbyke.tracker.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dropbyke.tracker.api.TrackerDTO;

import java.util.List;

public class TrackersSpinnerAdapter extends ArrayAdapter<TrackerDTO> {

    private final Context context;
    private final List<TrackerDTO> trackers;

    public TrackersSpinnerAdapter(Context context, int resource, List<TrackerDTO> objects) {
        super(context, resource, objects);
        this.context = context;
        this.trackers = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(trackers.get(position).getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setPadding(10, 20, 10, 20);
        label.setText(trackers.get(position).getName());
        return label;
    }

    public int getCount() {
        return trackers.size();
    }

    public TrackerDTO getItem(int position) {
        return trackers.get(position);
    }

    public long getItemId(int position) {
        return getItem(position).getId() != null ? Long.parseLong(getItem(position).getId()) : 0L;
    }
}

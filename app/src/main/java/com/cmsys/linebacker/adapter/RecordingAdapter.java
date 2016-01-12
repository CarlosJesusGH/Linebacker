package com.cmsys.linebacker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.RecordingBean;
import java.util.ArrayList;

/**
 * Created by cj on 27/11/15.
 */
public class RecordingAdapter extends ArrayAdapter<RecordingBean> implements Filterable {
    private ArrayList<RecordingBean> mRecordings;
    private ArrayList<RecordingBean> mFilteredRecordings;

    public RecordingAdapter(Context context, ArrayList<RecordingBean> recordings) {
        super(context, 0, recordings);
        mRecordings = recordings;
        mFilteredRecordings = recordings;
    }

    @Override
    public int getCount() {
        return mFilteredRecordings.size();
    }

    @Override
    public RecordingBean getItem(int position) {
        return mFilteredRecordings.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RecordingBean recording = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_recording_log_item, parent, false);
        }
        // Lookup view for data population
        TextView tvPhoneNumber = (TextView) convertView.findViewById(R.id.tvPhoneNumber);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        ImageView ivCheck = (ImageView) convertView.findViewById(R.id.ivCheck);

        // Populate the data into the template view using the data object
        tvPhoneNumber.setText(recording.getPhoneNumber());
        tvDate.setText(recording.getDatetimeString());
        tvTime.setVisibility(View.GONE);
        if(recording.isOnCase())
            ivCheck.setImageResource(R.mipmap.ic_check_blue);
        else
            ivCheck.setImageResource(R.mipmap.ic_check_gray);
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //arrayListNames = (List<String>) results.values;
                mFilteredRecordings = (ArrayList<RecordingBean>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<RecordingBean> FilteredArrayNames = new ArrayList<RecordingBean>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (RecordingBean iterator : mRecordings) {
                    if (iterator.getPhoneNumber().toLowerCase().contains(constraint.toString())
                            || iterator.getDatetimeString().contains(constraint.toString())){
                        FilteredArrayNames.add(iterator);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }
}
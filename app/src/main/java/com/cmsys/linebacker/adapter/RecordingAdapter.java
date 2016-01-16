package com.cmsys.linebacker.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;

import java.io.InputStream;
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
        RelativeLayout rlRecordingItem = (RelativeLayout) convertView.findViewById(R.id.rlRecordingItem);
        TextView tvPhoneNumber = (TextView) convertView.findViewById(R.id.tvPhoneNumber);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        ImageView ivContact = (ImageView) convertView.findViewById(R.id.ivContact);
        ImageView ivCheck = (ImageView) convertView.findViewById(R.id.ivCheck);

        // Populate the data into the template view using the data object
        if (recording.wasAlreadyPlayed())
            rlRecordingItem.setBackgroundResource(0);//R.color.colorPlayedRecording);
        else
            rlRecordingItem.setBackgroundResource(android.R.color.white);

        tvPhoneNumber.setText(recording.getPhoneNumber());
        tvDate.setText(recording.getDateString());
        tvTime.setText(recording.getTimeString() + " (" + recording.getDuration() + ")");
        if(recording.isOnCase())
            ivCheck.setImageResource(R.mipmap.ic_check_blue);
        else
            ivCheck.setImageResource(R.mipmap.ic_check_gray);
        // Set contactt image if exists
        if (recording.isContact()) {
            // Hide check image
            ivCheck.setVisibility(View.GONE);
            // Load contact name and image
            Long contactId = PhoneContactUtils.getContactIdByPhone(getContext(), recording.getPhoneNumber());
            if (contactId != null) {
                // Set contact name
                String contactName = PhoneContactUtils.getDisplayNameById(getContext(), contactId);
                if (!TextUtils.isEmpty(contactName)) {
                    tvPhoneNumber.setText(contactName);
                    recording.setContactName(contactName);
                }
                // Set contact image
                InputStream inputStream = PhoneContactUtils.getThumbnailPhotoById(getContext(), contactId);
                if (inputStream != null)
                    ivContact.setImageDrawable(Drawable.createFromStream(inputStream, ""));
                else
                    ivContact.setImageResource(R.drawable.ic_help_24dp);
            }
        } else {
            ivContact.setImageResource(R.mipmap.ic_launcher);
            ivCheck.setVisibility(View.VISIBLE);
        }
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

                // Perform your search here using the searchConstraint String.

                //boolean onlyContacts = false, onlyInCase = false, onlyNotInCase = false;
                //if()
                String lowerConstraint = constraint.toString().toLowerCase();
                for (RecordingBean iterator : mRecordings) {
                    if(constraint.equals(CONSTANTS.FIREBASE_FIELD_ISCONTACT + "=true")
                            && iterator.isContact()){
                        FilteredArrayNames.add(iterator);
                    } else if(constraint.equals(CONSTANTS.FIREBASE_FIELD_ISCONTACT + "=false")
                            && !iterator.isContact()){
                        FilteredArrayNames.add(iterator);
                    } else if(constraint.equals(CONSTANTS.FIREBASE_FIELD_ISONCASE + "=true")
                            && iterator.isOnCase()){
                        FilteredArrayNames.add(iterator);
                    } else if(constraint.equals(CONSTANTS.FIREBASE_FIELD_ISONCASE + "=false")
                            && !iterator.isOnCase()){
                        FilteredArrayNames.add(iterator);
                    } else if (iterator.getPhoneNumber().toLowerCase().contains(lowerConstraint.toString())
                            || iterator.getDatetimeString().contains(lowerConstraint.toString())){
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
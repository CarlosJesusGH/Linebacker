package com.cmsys.linebacker.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;

public class RecordingDetailsActivity extends AppCompatActivity {
    private LinearLayout llRecordingDetails;
    private RecordingBean mRecordingBean;
    private Button bReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_details);
        // Get recording from bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mRecordingBean = (RecordingBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_RECORDING);
        }
        if(mRecordingBean != null) {
            // Set Home/Up button
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Activity Views Setup --------------------------------------------------------------------
            setupViews();
        } else {
            finish();
        }
    }

    private void setupViews() {
        llRecordingDetails = (LinearLayout) findViewById(R.id.llRecordingDetails);
        bReport = (Button) findViewById(R.id.bReport);

        addViewToLayout(getString(R.string.recording_phone_number), mRecordingBean.getPhoneNumber());
        addViewToLayout(getString(R.string.recording_date), mRecordingBean.getDatetime().substring(0, mRecordingBean.getDatetime().indexOf(" ")));
        addViewToLayout(getString(R.string.recording_time), mRecordingBean.getDatetime().substring(mRecordingBean.getDatetime().indexOf(" ")));
        addViewToLayout(getString(R.string.recording_audio_duration), mRecordingBean.getDuration());
        addViewToLayout(getString(R.string.recording_is_audio_on_case), mRecordingBean.isOnCase() ? getString(R.string.yes) : getString(R.string.no));

        final Activity activity = this;
        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MessageUtils mu = new MessageUtils(activity, getString(R.string.report_this_event), getString(R.string.are_you_sure), 0, false);
                mu.setOnClickListenerYes(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageUtils.toast(v.getContext(), "Go to report activity...", false);
                        mu.cancel();
                    }
                });
                mu.setOnClickListenerNo(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mu.cancel();
                    }
                });
                mu.getBAccept().setVisibility(View.GONE);
                mu.show();
            }
        });
    }

    private void addViewToLayout(String pTitle, String pText) {
        LayoutInflater inflater = getLayoutInflater();
        View vInflated = (View) inflater.inflate(R.layout.activity_recording_details_item, null);
        ((TextView) vInflated.findViewById(R.id.tvTitle)).setText(pTitle);
        ((TextView) vInflated.findViewById(R.id.tvText)).setText(pText);
        //
        llRecordingDetails.addView(vInflated);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle click on the Home/Up button
        if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.cmsys.linebacker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.CaseBean;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RecordingDetailsActivity extends AppCompatActivity {
    private LinearLayout llRecordingDetails;
    private RecordingBean mRecordingBean;
    private Button bReport;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_details);
        // Get recording from bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mRecordingBean = (RecordingBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_RECORDING);
        }
        // Check if user is logged in
        mUserId = SharedPreferencesUtils.getUserIdFromPreferences(this, getString(R.string.pref_key_user_id));
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

        if(mRecordingBean.isOnCase())
            bReport.setText(getString(R.string.button_show_log));

        final Activity activity = this;
        final Context context = this;
        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Button) v).getText().equals(getString(R.string.button_report))) {
                    final MessageUtils mu = new MessageUtils(activity, getString(R.string.report_this_event), getString(R.string.are_you_sure), 0, false);
                    mu.setOnClickListenerYes(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create new case object
                            CaseBean caseBean = new CaseBean(mRecordingBean);
                            // Get save changes to Firebase
                            Firebase.setAndroidContext(context);
                            final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                            //fbRef.child(CONSTANTS.FIREBASE_DOC_CASES + File.separator + mUserId + File.separator + caseBean.getKey())
                            Map<String, Object> firebaseTrans = new HashMap<String, Object>();
                            firebaseTrans.put(CONSTANTS.FIREBASE_DOC_CASES + File.separator + mUserId + File.separator + caseBean.getKey(), caseBean.getObjectMap());
                            firebaseTrans.put(CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId + File.separator + mRecordingBean.getKey() + File.separator + CONSTANTS.FIREBASE_FIELD_ISONCASE, true);
                            fbRef.updateChildren(firebaseTrans, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError != null) {
                                        MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                    } else {
                                        MessageUtils.toast(context, "New Firebase object saved", false);
                                        bReport.setText(getString(R.string.button_show_log));
                                    }
                                }
                            });
                            /*fbRef.child(CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId + File.separator +
                                    mRecordingBean.getKey() + File.separator + CONSTANTS.FIREBASE_FIELD_ISONCASE)
                                    .setValue(true, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            if (firebaseError != null) {
                                                MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                            } else {

                                            }
                                        }
                                    });*/

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
                } else{
                    //MessageUtils.toast(v.getContext(), "Go to case logger activity...", false);
                    Intent intent = new Intent(v.getContext(), CaseDetailsActivity.class);
                    intent.putExtra(CONSTANTS.BUNDLE_EXTRA_RECORDING, mRecordingBean);
                    startActivity(intent);
                }
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

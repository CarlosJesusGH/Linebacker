package com.cmsys.linebacker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.CaseBean;
import com.cmsys.linebacker.bean.LogBean;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.util.AudioUtils;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RecordingDetailsActivity extends AppCompatActivity {
    private LinearLayout llRecordingDetails;
    private RecordingBean mRecordingBean;
    private UserBean mUserBean;
    private Button bReport, bPlay;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_details);
        // Get recording from bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mRecordingBean = (RecordingBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_RECORDING);
            mUserBean = (UserBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_USER);
        }
        // Check if user is logged in
        mUserId = UserAuthUtils.getUserId(this);
        if(mRecordingBean != null && mUserId != null) {
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
        bPlay = (Button) findViewById(R.id.bPlayAudio);

        fillRecordingData();

        if (mRecordingBean.isContact())
            bReport.setVisibility(View.GONE);

        if (mRecordingBean.isOnCase())
            bReport.setText(getString(R.string.button_show_log));

        final Activity activity = this;
        final Context context = this;
        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Button) v).getText().equals(getString(R.string.button_report))) {
                    final MessageUtils mu1 = new MessageUtils(activity, getString(R.string.report_this_event), getString(R.string.are_you_sure), 0, false);
                    mu1.setOnClickListenerYes(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Check if user has specific address assigned
                            if (TextUtils.isEmpty(mUserBean.getAddress())) {
                                final MessageUtils mu2 = new MessageUtils(activity, getString(R.string.add_new_address),
                                        getString(R.string.add_new_address_message), 0, false);
                                mu2.showEditTextAndSoftKeyboard();
                                mu2.getTilInput().setHint(getString(R.string.type_address_here));
                                mu2.setOnClickListenerAccept(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Context context = v.getContext();
                                        // Check if text is filled
                                        if (!mu2.getEtInput().getText().toString().equals("")) {
                                            mu2.getProgressBar().setVisibility(View.VISIBLE);
                                            mu2.getBAccept().setVisibility(View.GONE);
                                            mu2.getBCancel().setVisibility(View.GONE);
                                            // Get address string
                                            final String addressString = mu2.getEtInput().getText().toString();
                                            // Connect to Firebase
                                            Firebase.setAndroidContext(context);
                                            final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_USER +
                                                    File.separator + mUserId + File.separator + CONSTANTS.FIREBASE_FIELD_USERADDRESS);
                                            fbRef.setValue(addressString, new Firebase.CompletionListener() {
                                                @Override
                                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                    if (firebaseError != null) {
                                                        MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                                        mu2.getProgressBar().setVisibility(View.GONE);
                                                        mu2.getBAccept().setVisibility(View.VISIBLE);
                                                        mu2.getBCancel().setVisibility(View.VISIBLE);
                                                    } else {
                                                        MessageUtils.toast(context, getString(R.string.new_address_added), false);
                                                        mUserBean.setAddress(addressString);
                                                        reportCase(context);
                                                        mu1.cancel();
                                                        mu2.cancel();
                                                    }
                                                }
                                            });
                                        } else {
                                            MessageUtils.toast(context, getString(R.string.error_address_empty), false);
                                        }
                                    }
                                });
                                mu2.setOnClickListenerCancel(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mu2.cancel();
                                        mu1.cancel();
                                    }
                                });
                                mu2.show();
                                //------------------------------------------------------------------
                            } else {    // User has already an specific address
                                // Check if user has premium account
                                if (mUserBean.isUserLevelPremium()) {
                                    reportCase(context);
                                } else {
                                    Uri uri = Uri.parse(getString(R.string.web_link_upgrade)); // missing 'http://' will cause crashed
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                                mu1.cancel();
                            }
                        }
                    });
                    mu1.setOnClickListenerNo(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mu1.cancel();
                        }
                    });
                    mu1.getBAccept().setVisibility(View.GONE);
                    mu1.show();
                } else{
                    //MessageUtils.toast(v.getContext(), "Go to case logger activity...", false);
                    Intent intent = new Intent(v.getContext(), CaseDetailsActivity.class);
                    intent.putExtra(CONSTANTS.BUNDLE_EXTRA_RECORDING, mRecordingBean);
                    startActivity(intent);
                }
            }
        });

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordingBean.wasAlreadyPlayed()) {
                    AudioUtils.streamOnDefaultPlayer(activity, mRecordingBean.getAudioFileUrl().replace("https", "http"));
                } else {
                    bPlay.setEnabled(false);
                    // Update wasAlreadyPlayed field
                    Firebase.setAndroidContext(context);
                    final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId + File.separator + mRecordingBean.getKey() + File.separator + CONSTANTS.FIREBASE_FIELD_WASALREADYPLAYED);
                    fbRef.setValue(true, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                            } else {
                                AudioUtils.streamOnDefaultPlayer(activity, mRecordingBean.getAudioFileUrl().replace("https", "http"));
                                mRecordingBean.setWasAlreadyPlayed(true);
                            }
                            bPlay.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void reportCase(final Context context) {
        // Create new case object
        CaseBean caseBean = new CaseBean(mRecordingBean);
        // Get save changes to Firebase
        Firebase.setAndroidContext(context);
        final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
        //fbRef.child(CONSTANTS.FIREBASE_DOC_CASES + File.separator + mUserId + File.separator + caseBean.getKey())
        String logUniqueId = fbRef.push().getKey();
        Map<String, Object> firebaseTrans = new HashMap<String, Object>();
        firebaseTrans.put(CONSTANTS.FIREBASE_DOC_CASES + File.separator + mUserId + File.separator + caseBean.getKey(), caseBean.getObjectMap());
        firebaseTrans.put(CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId + File.separator + mRecordingBean.getKey() + File.separator + CONSTANTS.FIREBASE_FIELD_ISONCASE, true);
        firebaseTrans.put(CONSTANTS.FIREBASE_DOC_CASE_LOGS + File.separator + mRecordingBean.getKey() + File.separator + logUniqueId, new LogBean(ServerValue.TIMESTAMP, 0).getObjectMap());
        fbRef.updateChildren(firebaseTrans, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                } else {
                    bReport.setText(getString(R.string.button_show_log));
                    mRecordingBean.setIsOnCase(true);
                    fillRecordingData();
                }
            }
        });
    }

    private void fillRecordingData(){
        llRecordingDetails.removeAllViews();
        if(mRecordingBean.isContact())
            addViewToLayout(getString(R.string.recording_contact_name), mRecordingBean.getContactName());
        addViewToLayout(getString(R.string.recording_phone_number), mRecordingBean.getPhoneNumber());
        addViewToLayout(getString(R.string.recording_date), mRecordingBean.getDateString());
        addViewToLayout(getString(R.string.recording_time), mRecordingBean.getTimeString());
        addViewToLayout(getString(R.string.recording_audio_duration), mRecordingBean.getDuration());
        if(!mRecordingBean.isContact())
        addViewToLayout(getString(R.string.recording_is_audio_on_case), mRecordingBean.isOnCase() ? getString(R.string.yes) : getString(R.string.no));
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

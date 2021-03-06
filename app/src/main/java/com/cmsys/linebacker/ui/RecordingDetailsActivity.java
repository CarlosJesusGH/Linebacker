package com.cmsys.linebacker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.CaseBean;
import com.cmsys.linebacker.bean.LogBean;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.util.AudioUtils;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.CheckInputDataUtils;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.LogUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.RestfulUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordingDetailsActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(LoginActivity.class);
    private LinearLayout llRecordingDetails;
    private RecordingBean mRecordingBean;
    private UserBean mUserBean;
    private Button bReport, bPlay, bRemove;
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
        bRemove = (Button) findViewById(R.id.bRemoveLog);

        fillRecordingData();

        if (mRecordingBean.isContact())
            bReport.setVisibility(View.GONE);

        if (mRecordingBean.isOnCase()) {
            bReport.setText(getString(R.string.button_show_log));
            bReport.setVisibility(View.GONE);
        }

        final Activity activity = this;
        final Context context = this;
        //bReport.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        if(((Button) v).getText().equals(getString(R.string.button_report))) {
        //            final MessageUtils mu1 = new MessageUtils(activity, getString(R.string.report_this_event), getString(R.string.are_you_sure), 0, false);
        //            mu1.setOnClickListenerYes(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    // Check if user has specific address assigned
        //                    if (TextUtils.isEmpty(mUserBean.getAddress())) {
        //                        final MessageUtils mu2 = new MessageUtils(activity, getString(R.string.add_new_address),
        //                                getString(R.string.add_new_address_message), 0, false);
        //                        mu2.showEditTextAndSoftKeyboard();
        //                        mu2.getTilInput().setHint(getString(R.string.type_address_here));
        //                        mu2.setOnClickListenerAccept(new View.OnClickListener() {
        //                            @Override
        //                            public void onClick(View v) {
        //                                final Context context = v.getContext();
        //                                // Check if text is filled
        //                                if (!mu2.getEtInput().getText().toString().equals("")) {
        //                                    mu2.getProgressBar().setVisibility(View.VISIBLE);
        //                                    mu2.getBAccept().setVisibility(View.GONE);
        //                                    mu2.getBCancel().setVisibility(View.GONE);
        //                                    // Get address string
        //                                    final String addressString = mu2.getEtInput().getText().toString();
        //                                    // Connect to Firebase
        //                                    Firebase.setAndroidContext(context);
        //                                    final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_USER +
        //                                            File.separator + mUserId + File.separator + CONSTANTS.FIREBASE_FIELD_USERADDRESS);
        //                                    fbRef.setValue(addressString, new Firebase.CompletionListener() {
        //                                        @Override
        //                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        //                                            if (firebaseError != null) {
        //                                                MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
        //                                                mu2.getProgressBar().setVisibility(View.GONE);
        //                                                mu2.getBAccept().setVisibility(View.VISIBLE);
        //                                                mu2.getBCancel().setVisibility(View.VISIBLE);
        //                                            } else {
        //                                                MessageUtils.toast(context, getString(R.string.new_address_added), false);
        //                                                mUserBean.setAddress(addressString);
        //                                                reportCase(context);
        //                                                mu1.cancel();
        //                                                mu2.cancel();
        //                                            }
        //                                        }
        //                                    });
        //                                } else {
        //                                    MessageUtils.toast(context, getString(R.string.error_address_empty), false);
        //                                }
        //                            }
        //                        });
        //                        mu2.setOnClickListenerCancel(new View.OnClickListener() {
        //                            @Override
        //                            public void onClick(View v) {
        //                                mu2.cancel();
        //                                mu1.cancel();
        //                            }
        //                        });
        //                        mu2.show();
        //                        //------------------------------------------------------------------
        //                    } else {    // User has already an specific address
        //                        // Check if user has premium account
        //                        if (mUserBean.isUserLevelPremium()) {
        //                            reportCase(context);
        //                        } else {
        //                            Uri uri = Uri.parse(getString(R.string.web_link_upgrade)); // missing 'http://' will cause crashed
        //                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //                            startActivity(intent);
        //                        }
        //                        mu1.cancel();
        //                    }
        //                }
        //            });
        //            mu1.setOnClickListenerNo(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    mu1.cancel();
        //                }
        //            });
        //            mu1.getBAccept().setVisibility(View.GONE);
        //            mu1.show();
        //        } else{
        //            //MessageUtils.toast(v.getContext(), "Go to case logger activity...", false);
        //            Intent intent = new Intent(v.getContext(), CaseDetailsActivity.class);
        //            intent.putExtra(CONSTANTS.BUNDLE_EXTRA_RECORDING, mRecordingBean);
        //            startActivity(intent);
        //        }
        //    }
        //}
        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MessageUtils mu = new MessageUtils(activity, getString(R.string.report_case_info), "", R.layout.custom_dialog_report_case_info, false);
                mu.setOnClickListenerAccept(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Context context = getApplicationContext();
                        final EditText etStreet = (EditText) mu.getConvertView().findViewById(R.id.etStreet);
                        final EditText etCompanyName = (EditText) mu.getConvertView().findViewById(R.id.etCompanyName);
                        final EditText etTelemarketingService = (EditText) mu.getConvertView().findViewById(R.id.etTelemarketingService);
                        final EditText etTelemarketingAgent = (EditText) mu.getConvertView().findViewById(R.id.etTelemarketingAgent);
                        final EditText etComments = (EditText) mu.getConvertView().findViewById(R.id.etComments);
                        //
                        List<EditText> editTextList = new ArrayList<EditText>();
                        editTextList.add(etStreet);
                        editTextList.add(etCompanyName);
                        editTextList.add(etTelemarketingService);
                        editTextList.add(etTelemarketingAgent);
                        editTextList.add(etComments);

                        // Check if text is filled
                        if (CheckInputDataUtils.areAllFieldsFilled(editTextList)) {
                            MessageUtils.showProgressBarAndHideButtons(mu);
                            // Connect to Api
                            new AsyncTask<Void, Void, RestMessageBean>() {
                                HashMap<String, String> postDataParams;

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    postDataParams = new HashMap<String, String>();
                                    postDataParams.put("id_userAcc", mUserId);
                                    postDataParams.put("id_call", mRecordingBean.getKey());
                                    postDataParams.put("street", etStreet.getText().toString());
                                    postDataParams.put("company_name", etCompanyName.getText().toString());
                                    postDataParams.put("telemakerting_service", etTelemarketingService.getText().toString());
                                    postDataParams.put("telemakerting_agent_supervisor", etTelemarketingAgent.getText().toString());
                                    postDataParams.put("comments_adicional_info", etComments.getText().toString());
                                }

                                @Override
                                protected RestMessageBean doInBackground(Void... params) {
                                    RestMessageBean restMessageBean = null;
                                    try {
                                        restMessageBean = RestfulUtils.readRestfulPostAndParseToObject(CONSTANTS.SYNC_WS_REPORT_CASE_API, RestMessageBean.class, postDataParams);
                                        //Log.i(TAG,"Perform here connection to api");
                                    } catch (Exception e) {
                                        ExceptionUtils.printExceptionToFile(e);
                                    }
                                    return restMessageBean;
                                }

                                @Override
                                protected void onPostExecute(RestMessageBean restMessageBean) {
                                    super.onPostExecute(restMessageBean);
                                    if (restMessageBean != null && restMessageBean.getErrorId() == 0) {
                                        if (!TextUtils.isEmpty(restMessageBean.getErrorMessage()))
                                            MessageUtils.toast(getApplicationContext(), restMessageBean.getErrorMessage(), true);
                                        else {
                                            MessageUtils.toast(getApplicationContext(), getString(R.string.report_case_reported_successful), false);
                                        }
                                        bReport.setVisibility(View.GONE);
                                        mRecordingBean.setIsOnCase(true);
                                        fillRecordingData();
                                        mu.cancel();
                                    } else if (restMessageBean != null) {
                                        if (!TextUtils.isEmpty(restMessageBean.getErrorMessage()))
                                            MessageUtils.toast(getApplicationContext(), restMessageBean.getErrorMessage(), false);
                                    } else
                                        MessageUtils.toast(getApplicationContext(), getString(R.string.error_api_connect), false);
                                    MessageUtils.hideProgressBarAndShowAcceptButtons(mu);
                                }
                            }.execute();
                        } else {
                            if (!CheckInputDataUtils.areAllFieldsFilled(editTextList))
                                MessageUtils.toast(context, getString(R.string.error_all_fields_required), false);
                        }
                    }
                });
                mu.setOnClickListenerCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mu.cancel();
                    }
                });
                mu.show();
            }
        });
        //bReport.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        MessageUtils.toast(context, "Not working yet", false);
        //    }
        //});

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

        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MessageUtils mu = new MessageUtils(RecordingDetailsActivity.this, getString(R.string.action_remove_log), getString(R.string.are_you_sure), 0, false);
                mu.setOnClickListenerYes(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mu.getProgressBar().setVisibility(View.VISIBLE);
                        mu.getBYes().setVisibility(View.GONE);
                        mu.getBNo().setVisibility(View.GONE);
                        // Connection to web service, it must be performed in a new thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final RestMessageBean restMessageBean;
                                String toastMsg = "";
                                try {
                                    restMessageBean = RestfulUtils.readRestfulAndParseToObject(CONSTANTS.SYNC_WS_ASTERISk_REMOVE_LOG + mRecordingBean.getDatetimeLong() + "/" + UserAuthUtils.getUserId(context), RestMessageBean.class);
                                    if (restMessageBean != null && restMessageBean.getErrorId() == 0) {
                                        toastMsg = getString(R.string.log_remove_successful);
                                        mu.cancel();
                                        finish();
                                    } else {
                                        if (restMessageBean.getErrorMessage() != null && !restMessageBean.getErrorMessage().equals(""))
                                            toastMsg = restMessageBean.getErrorMessage();
                                        else
                                            toastMsg = getString(R.string.log_remove_error);
                                    }

                                } catch (Exception e) {
                                    ExceptionUtils.printExceptionToFile(e);
                                    ExceptionUtils.displayExceptionMessage(context, e);
                                }
                                final String finalToastMsg = toastMsg;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!finalToastMsg.equals(""))
                                            MessageUtils.toast(context, finalToastMsg, false);
                                        mu.getBYes().setVisibility(View.VISIBLE);
                                        mu.getBNo().setVisibility(View.VISIBLE);
                                        mu.getProgressBar().setVisibility(View.GONE);
                                    }
                                });
                            }
                        }).start();
                    }
                });
                mu.setOnClickListenerNo(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mu.cancel();
                    }
                });
                mu.show();
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

        // add phone number link
        // addViewToLayout(getString(R.string.recording_phone_number), mRecordingBean.getPhoneNumber());
        LayoutInflater inflater = getLayoutInflater();
        View vInflated = (View) inflater.inflate(R.layout.activity_recording_details_item, null);
        ((TextView) vInflated.findViewById(R.id.tvTitle)).setText(getString(R.string.recording_phone_number));
        TextView tvLink = ((TextView) vInflated.findViewById(R.id.tvText));
        // Make TextView look like link
        final SpannableString spannableString = new SpannableString(mRecordingBean.getPhoneNumber());
        spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLink.setText(spannableString, TextView.BufferType.SPANNABLE);
        // set action when click
        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MessageUtils.toast(getApplicationContext(), "call phone", true);
                PhoneCallUtils.dialNumber(getApplicationContext(), mRecordingBean.getPhoneNumber());
            }
        });
        llRecordingDetails.addView(vInflated);

        // add other fields
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

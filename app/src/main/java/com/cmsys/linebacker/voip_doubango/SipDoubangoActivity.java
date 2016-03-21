package com.cmsys.linebacker.voip_doubango;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.PhoneCompanyBean;
import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.bean.RestResultAsteriskData;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.HashMapUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.RestfulUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.util.StringUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by @CarlosJesusGH on 08/03/16.
 */
public class SipDoubangoActivity extends AppCompatActivity {
    private DoubangoUtils mDoubango;
    private TextView mTvInfo;
    private Button mBtSignInOut, mBtCall, mBtGetExtension, mBtVoiceMailConfigNumbers, mBtTestVoicemailSetup;
    private EditText mEtSignInOut, mEtCall, mEtExternalPhoneNr, mEtPassword;
    private LinearLayout llCallingNumbers;
    private RelativeLayout rlExtensionData;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip_doubango);
        //
        setupViews();
        //
        mDoubango = new DoubangoUtils(this);
        mDoubango.Init();
    }

    private void setupViews() {
        mTvInfo = (TextView) findViewById(R.id.tvInfo);
        mEtSignInOut = (EditText) findViewById(R.id.etSignInOut);
        mEtExternalPhoneNr = (EditText) findViewById(R.id.etExternalPhoneNr);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mBtSignInOut = (Button) findViewById(R.id.btSignInOut);
        mEtCall = (EditText) findViewById(R.id.etCall);
        mBtCall = (Button) findViewById(R.id.btCall);
        mBtGetExtension = (Button) findViewById(R.id.btGetExtension);
        mBtVoiceMailConfigNumbers = (Button) findViewById(R.id.btVoiceMailConfigNumbers);
        mBtTestVoicemailSetup = (Button) findViewById(R.id.btTestVoiceMailSetup);
        llCallingNumbers = (LinearLayout) findViewById(R.id.llCallingNumbers);
        rlExtensionData = (RelativeLayout) findViewById(R.id.rlExtensionData);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mBtSignInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoubango.serverSignInOut(mEtSignInOut.getText().toString(), mEtPassword.getText().toString());
            }
        });
        mBtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoubango.makeVoiceCall(mEtCall.getText().toString());
            }
        });
        mBtGetExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Connect to WebService
                new AsyncTask<Void, Void, RestMessageBean>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mProgressDialog.show();
                    }

                    @Override
                    protected RestMessageBean doInBackground(Void... params) {
                        RestMessageBean restMessageBean = null;
                        try {
                            Thread.sleep(5000);
                            restMessageBean = RestfulUtils.readRestfulAndParseToObject
                                    (CONSTANTS.SYNC_WS_ASTERISk_GET_USER_DATA, RestMessageBean.class);

                        } catch (Exception e) {
                            ExceptionUtils.printExceptionToFile(e);
                            e.printStackTrace();
                            return null;
                        }
                        return restMessageBean;
                    }

                    @Override
                    protected void onPostExecute(RestMessageBean restMessageBean) {
                        super.onPostExecute(restMessageBean);
                        if (restMessageBean != null) {
                            // Write WebService results con text fields
                            if (restMessageBean.getErrorId() != null && restMessageBean.getErrorId() <= 0) {
                                try {
                                    //RestResultAsteriskData resultData = (RestResultAsteriskData) restMessageBean.getResultObject(); //new Gson().fromJson(restMessageBean.getResultObject(), RestResultAsteriskData.class);
                                    RestResultAsteriskData resultData = new Gson().fromJson(restMessageBean.getResultObject().toString(), RestResultAsteriskData.class);
                                    //
                                    mEtSignInOut.setText(resultData.getExtensionNumber());
                                    mEtPassword.setText(resultData.getExtensionPassword());
                                    if (!TextUtils.isEmpty(resultData.getExternalPhoneNumber())) {
                                        mEtExternalPhoneNr.setText(resultData.getExternalPhoneNumber());
                                        mBtVoiceMailConfigNumbers.setVisibility(View.VISIBLE);
                                    }
                                    MessageUtils.toast(getApplicationContext(), getString(R.string.get_extension_successful), true);
                                } catch (Exception e) {
                                    ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
                                }
                            } else {
                                MessageUtils.toast(getApplicationContext(), "ERROR: " + restMessageBean.getErrorMessage(), true);
                            }
                        }
                        mProgressDialog.dismiss();
                        rlExtensionData.setVisibility(View.VISIBLE);
                    }
                }.execute();
            }
        });

        mBtTestVoicemailSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Connect to WebService
                new AsyncTask<Void, Void, RestMessageBean>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mProgressDialog.show();
                    }

                    @Override
                    protected RestMessageBean doInBackground(Void... params) {
                        RestMessageBean restMessageBean = null;
                        try {
                            Thread.sleep(15000);
                            restMessageBean = RestfulUtils.readRestfulAndParseToObject
                                    (CONSTANTS.SYNC_WS_ASTERISk_TEST_VOICE_MAIL, RestMessageBean.class);

                        } catch (Exception e) {
                            ExceptionUtils.printExceptionToFile(e);
                            e.printStackTrace();
                            return null;
                        }
                        return restMessageBean;
                    }

                    @Override
                    protected void onPostExecute(RestMessageBean restMessageBean) {
                        super.onPostExecute(restMessageBean);
                        if (restMessageBean != null) {
                            // Visual changes after results
                            if (restMessageBean.getErrorId() != null && restMessageBean.getErrorId() <= 0) {
                                MessageUtils.toast(getApplicationContext(), getString(R.string.test_voicemail_successful), true);
                            } else {
                                MessageUtils.toast(getApplicationContext(), "ERROR: " + restMessageBean.getErrorMessage(), true);
                            }
                        }
                        mProgressDialog.dismiss();
                    }
                }.execute();
            }
        });

        mBtVoiceMailConfigNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlExtensionData.setVisibility(View.GONE);
                final Context context = getApplicationContext();
                Activity activity = getParent();
                final MessageUtils mu = new MessageUtils(SipDoubangoActivity.this, context.getString(R.string.phone_company), null, 0, false);
                mu.getBAccept().setVisibility(View.GONE);
                mu.getBCancel().setVisibility(View.VISIBLE);
                mu.show();
                // Start loading phone companies list
                mu.getProgressBar().setVisibility(View.VISIBLE);
                // Reading Data Once
                Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_PHONECOMPANY);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            mu.getProgressBar().setVisibility(View.GONE);
                            mu.getListView().setVisibility(View.VISIBLE);

                            //List<PhoneCompanyBean> phoneCompanies = (List<PhoneCompanyBean>) dataSnapshot.getValue();
                            //HashMap<String, Object> users = (HashMap<String, Object>) snapshot.getValue();
                            //final HashMap<String, PhoneCompanyBean> phoneCompanies = (HashMap<String, PhoneCompanyBean>) dataSnapshot.getValue(PhoneCompanyBean.class);

                            final HashMap<String, PhoneCompanyBean> phoneCompanies = new HashMap<String, PhoneCompanyBean>();
                            final HashMap<String, String> phoneCompanyNames = new HashMap<String, String>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                PhoneCompanyBean phoneCompanyBean = (PhoneCompanyBean) child.getValue(PhoneCompanyBean.class);
                                phoneCompanyNames.put(phoneCompanyBean.getCompanyName(), child.getKey());
                                phoneCompanies.put(child.getKey(), phoneCompanyBean);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    //android.R.layout.simple_list_item_1,
                                    //(new TextView(getApplicationContext())),
                                    R.layout.util_cust_dialog_listview_item,
                                    //R.layout.activity_case_details_comments_item,
                                    R.id.textView,
                                    HashMapUtils.getIdsStrings(phoneCompanyNames));
                            // Assign adapter to ListView
                            mu.getListView().setAdapter(adapter);
                            mu.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //HashMap<String, String> companyName = (HashMap<String, String>) mu.getListView().getItemAtPosition(position);
                                    String companyName = (String) mu.getListView().getItemAtPosition(position);
                                    String companyId = phoneCompanyNames.get(companyName);
                                    PhoneCompanyBean phoneCompanyBean = phoneCompanies.get(companyId);
                                    mu.cancel();

                                    // Show CallingNumbers LinearLayout and set title
                                    llCallingNumbers.setVisibility(View.VISIBLE);
                                    ((TextView) llCallingNumbers.findViewById(R.id.tvCompanyName)).setText(phoneCompanyBean.getCompanyName());

                                    // Set every Activate or Deactivate TextViews and hide it if necessary
                                    String number1 = StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationPrefix())
                                            + mEtExternalPhoneNr.getText()
                                            + StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationSuffix());
                                    ((TextView) llCallingNumbers.findViewById(R.id.tvActivate1)).setText(number1);

                                    if (!TextUtils.isEmpty(phoneCompanyBean.getActivationPrefix()))
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate1)).setVisibility(View.VISIBLE);
                                    else
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate1)).setVisibility(View.GONE);
                                    //
                                    String number2 = StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationPrefix2())
                                            + mEtExternalPhoneNr.getText();
                                    ((TextView) llCallingNumbers.findViewById(R.id.tvActivate2)).setText(number2);

                                    if (!TextUtils.isEmpty(phoneCompanyBean.getActivationPrefix2()))
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate2)).setVisibility(View.VISIBLE);
                                    else
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate2)).setVisibility(View.GONE);
                                    //
                                    String number3 = StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationPrefix3())
                                            + mEtExternalPhoneNr.getText();
                                    ((TextView) llCallingNumbers.findViewById(R.id.tvActivate3)).setText(number3);

                                    if (!TextUtils.isEmpty(phoneCompanyBean.getActivationPrefix3()))
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate3)).setVisibility(View.VISIBLE);
                                    else
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate3)).setVisibility(View.GONE);
                                    //
                                    if (!TextUtils.isEmpty(phoneCompanyBean.getDeactivationNumber())) {
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate1)).setText(phoneCompanyBean.getDeactivationNumber());
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate1)).setVisibility(View.VISIBLE);
                                    } else
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate1)).setVisibility(View.GONE);
                                    //
                                    if (!TextUtils.isEmpty(phoneCompanyBean.getDeactivationNumber2())) {
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate2)).setText(phoneCompanyBean.getDeactivationNumber2());
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate2)).setVisibility(View.VISIBLE);
                                    } else
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate2)).setVisibility(View.GONE);
                                    //
                                    if (!TextUtils.isEmpty(phoneCompanyBean.getSpecialInstructions())) {
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvSpecialInstructions)).setText(phoneCompanyBean.getSpecialInstructions());
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llSpecialInstructions)).setVisibility(View.VISIBLE);
                                    } else
                                        ((LinearLayout) llCallingNumbers.findViewById(R.id.llSpecialInstructions)).setVisibility(View.GONE);

                                    // Setup Calling Buttons
                                    ((Button) llCallingNumbers.findViewById(R.id.btActivate1)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvActivate1)).getText().toString());
                                    ((Button) llCallingNumbers.findViewById(R.id.btActivate2)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvActivate2)).getText().toString());
                                    ((Button) llCallingNumbers.findViewById(R.id.btActivate3)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvActivate3)).getText().toString());
                                    ((Button) llCallingNumbers.findViewById(R.id.btDeactivate1)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate1)).getText().toString());
                                    ((Button) llCallingNumbers.findViewById(R.id.btDeactivate2)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate2)).getText().toString());
                                    //
                                    View.OnClickListener onClickListener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            PhoneCallUtils.dialNumber(v.getContext(), v.getTag().toString());
                                        }
                                    };
                                    //
                                    ((Button) llCallingNumbers.findViewById(R.id.btActivate1)).setOnClickListener(onClickListener);
                                    ((Button) llCallingNumbers.findViewById(R.id.btActivate2)).setOnClickListener(onClickListener);
                                    ((Button) llCallingNumbers.findViewById(R.id.btActivate3)).setOnClickListener(onClickListener);
                                    ((Button) llCallingNumbers.findViewById(R.id.btDeactivate1)).setOnClickListener(onClickListener);
                                    ((Button) llCallingNumbers.findViewById(R.id.btDeactivate2)).setOnClickListener(onClickListener);
                                }
                            });

                        } else {
                            MessageUtils.toast(getApplicationContext(), getString(R.string.error_no_info_to_show), true);
                            mu.cancel();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }
        });
    }

    /*@Override
    protected void onDestroy() {
        // Stops the engine
        if(mDoubango.getEngine().isStarted()){
            mDoubango.getEngine().stop();
        }
        // release the listener
        if (mDoubango.getSipBroadCastRecv() != null) {
            unregisterReceiver(mDoubango.getSipBroadCastRecv());
            mDoubango.setSipBroadCastRecv(null);
        }
        super.onDestroy();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // Starts the engine
        if (!mDoubango.getEngine().isStarted()) {
            if (mDoubango.getEngine().start()) {
                mTvInfo.setText("Engine started :)");
            } else {
                mTvInfo.setText("Failed to start the engine :(");
            }
        }
        // Check sha
        if (SharedPreferencesUtils.checkIfContainsKey(this, getString(R.string.pref_key_voip_extension))) {
            String extension = SharedPreferencesUtils.getString(this, getString(R.string.pref_key_voip_extension), null);
            String password = SharedPreferencesUtils.getString(this, getString(R.string.pref_key_voip_password), null);
            mTvInfo.setText(mTvInfo.getText().toString() + " - Last Ext: " + extension);
            mEtSignInOut.setText(extension);
            mEtPassword.setText(password);
        }
    }
}

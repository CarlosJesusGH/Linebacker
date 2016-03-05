package com.cmsys.linebacker.voip_doubango;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.PhoneCompanyBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.HashMapUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SipDoubangoActivity extends AppCompatActivity {
    private DoubangoUtils mDoubango;
    private TextView mTvInfo;
    private Button mBtSignInOut, mBtCall, mBtGetExtension, mBtVoiceMailConfigNumbers;
    private EditText mEtSignInOut, mEtCall, mEtPassword;

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
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mBtSignInOut = (Button) findViewById(R.id.btSignInOut);
        mEtCall = (EditText) findViewById(R.id.etCall);
        mBtCall = (Button) findViewById(R.id.btCall);
        mBtGetExtension = (Button) findViewById(R.id.btGetExtension);
        mBtVoiceMailConfigNumbers = (Button) findViewById(R.id.btVoiceMailConfigNumbers);

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
            public void onClick(View v) {
                // Connect to WebService
                //
                // Write WebService results con text fields
                mEtSignInOut.setText("750");
                mEtPassword.setText("6bc0def00bd9ac5b1bc6f23d5d890568");
            }
        });

        mBtVoiceMailConfigNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                Activity activity = getParent();
                final MessageUtils mu = new MessageUtils(SipDoubangoActivity.this, "SELECT YOUR PHONE COMPANY", null, 0, false);
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
                            HashMap<String, PhoneCompanyBean> phoneCompanies = (HashMap<String, PhoneCompanyBean>) dataSnapshot.getValue();


                            HashMap<String, String> phoneCompanyNames = new HashMap<String, String>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                PhoneCompanyBean phoneCompanyBean = (PhoneCompanyBean) child.getValue(PhoneCompanyBean.class);
                                phoneCompanyNames.put(child.getKey(), phoneCompanyBean.getCompanyName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    //android.R.layout.simple_list_item_1,
                                    //(new TextView(getApplicationContext())),
                                    R.layout.util_cust_dialog_listview_item,
                                    //R.layout.activity_case_details_comments_item,
                                    R.id.textView,
                                    HashMapUtils.getValuesStrings(phoneCompanyNames));
                            // Assign adapter to ListView
                            mu.getListView().setAdapter(adapter);

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

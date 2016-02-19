package com.cmsys.linebacker.voip_doubango;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.SharedPreferencesUtils;

public class SipDoubangoActivity extends AppCompatActivity {
    private DoubangoUtils mDoubango;
    private TextView mTvInfo;
    private Button mBtSignInOut, mBtCall, mBtGetExtension;
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

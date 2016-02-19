package com.cmsys.linebacker.voip_siprtc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.MessageUtils;

import java.net.URISyntaxException;
import java.text.ParseException;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SipRtcActivity extends AppCompatActivity {
    public SipManager mSipManager = null;
    public SipProfile mSipProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip_rtc);
        //
        //wsConnection();
        //
        sipConnection();
        //
        setupViews();
    }

    private void setupViews() {
        Button bCall = (Button) findViewById(R.id.bCall);
        bCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SipAudioCall.Listener listener = new SipAudioCall.Listener() {

                    @Override
                    public void onCallEstablished(SipAudioCall call) {
                        call.startAudio();
                        call.setSpeakerMode(true);
                        call.toggleMute();
                        //...
                        MessageUtils.toast(getApplicationContext(), "Call started", false);
                    }

                    @Override
                    public void onCallEnded(SipAudioCall call) {
                        // Do something.
                        MessageUtils.toast(getApplicationContext(), "Call ended", false);
                    }
                };
                //...
                String sipAddress = "750";
                try {
                    SipAudioCall call = mSipManager.makeAudioCall(mSipProfile.getUriString(), sipAddress, listener, 30);
                } catch (SipException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeLocalProfile();
    }

    private void sipConnection() {

        boolean bool = getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SIP);

        // Creating a SipManager
        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(this);
            if (mSipManager == null)
                MessageUtils.toast(this, "mSipManager is NULL", false);
        }

        // Creating a SipProfile
        String username = "751", password = "Linebacker2016*", domain = "voip.mylinebacker.net";
        SipProfile.Builder builder = null;
        try {
            builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            mSipProfile = builder.build();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
        }

        // Creating a SipRegistrationListener
//        SipRegistrationListener sipRegistrationListener = new SipRegistrationListener() {
//
//            public void onRegistering(String localProfileUri) {
//                MessageUtils.toast(getApplicationContext(), "Registering with SIP Server...", false);
//            }
//
//            public void onRegistrationDone(String localProfileUri, long expiryTime) {
//                MessageUtils.toast(getApplicationContext(), "Ready", false);
//            }
//
//            public void onRegistrationFailed(String localProfileUri, int errorCode,
//                                             String errorMessage) {
//                MessageUtils.toast(getApplicationContext(), "Registration failed.  Please check settings.", false);
//            }
//        };

        // Registering with a SIP Server
        Intent intent = new Intent();
        intent.setAction("android.SipDemo.INCOMING_CALL");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
        try {
            mSipManager.open(mSipProfile, pendingIntent, null);
            //mSipManager.open(mSipProfile, pendingIntent, sipRegistrationListener);
        } catch (SipException e) {
            e.printStackTrace();
        } catch (Exception e) {
            ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
        }

        // Setting a SipRegistrationListener
        try {
            mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {

                public void onRegistering(String localProfileUri) {
                    MessageUtils.toast(getApplicationContext(), "Registering with SIP Server...", false);
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    MessageUtils.toast(getApplicationContext(), "Ready", false);
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    MessageUtils.toast(getApplicationContext(), "Registration failed.  Please check settings.", false);
                }
            });
        } catch (SipException e) {
            ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
        } catch (Exception e) {
            ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
        }
    }

    public void closeLocalProfile() {
        if (mSipManager == null) {
            return;
        }
        try {
            if (mSipProfile != null) {
                mSipManager.close(mSipProfile.getUriString());
            }
        } catch (Exception ee) {
            Log.d("onDestroy", "Failed to close local profile.", ee);
            ExceptionUtils.displayExceptionMessage(getApplicationContext(), ee);
        }
    }

    private void wsConnection() {
        Socket client = null;
        String host = "http://voip.mylinebacker.net:8080/ws";   //ports 8080 and 8089
        String token = "";
        IO.Options opts = new IO.Options();
        opts.reconnection = true;
        opts.query = "token=" + token;
        try {
            //client = IO.socket(host, opts);
            client = IO.socket(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Socket finalClient = client;
        final Context context = this;
        client.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Manager lastIO = finalClient.io();
                //MessageUtils.toast(context, "Connected", false);
                Log.e("TAG", "Connected");
            }
        });
        client.on("connect_error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //MessageUtils.toast(context, "Unable to connect Socket.IO", false);
                Log.e("TAG", "Unable to connect Socket.IO");
            }
        });
        client.connect();
    }
}

package com.cmsys.linebacker.service;

/**
 * Created by cj on 02/12/15.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cmsys.linebacker.util.PhoneCallUtils;

public class PhoneStateService extends Service {
    private CallStateListener mCallStateListener = new CallStateListener();
    private TelephonyManager mTelephonyManager;
    private int mCallState;

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mCallState = mTelephonyManager.getCallState();
        mTelephonyManager.listen(mCallStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "onDestroy");
        mTelephonyManager.listen(mCallStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; //-- not a bound service--
    }

    private final class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (mCallState) {

                case TelephonyManager.CALL_STATE_IDLE:
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        Log.d("state", "idle --> off hook = new outgoing call");
                        // idle --> off hook = new outgoing call
                        //triggerSenses(Sense.CallEvent.OUTGOING);
                    } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                        Log.d("state", "idle --> ringing = new incoming call");
                        // idle --> ringing = new incoming call
                        //triggerSenses(Sense.CallEvent.INCOMING);
                        PhoneCallUtils.endCall(getApplicationContext());
                    }
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        Log.d("state", "off hook --> idle  = disconnected");
                        // off hook --> idle  = disconnected
                        //triggerSenses(Sense.CallEvent.ENDED);
                    } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                        Log.d("state", "off hook --> ringing = another call waiting");
                        // off hook --> ringing = another call waiting
                        //triggerSenses(Sense.CallEvent.WAITING);
                    }
                    Log.d("CALL_STATE_OFFHOOK", String.valueOf(state));
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        Log.d("state", "ringing --> off hook = received");
                        // ringing --> off hook = received
                        //triggerSenses(Sense.CallEvent.RECEIVED);
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        Log.d("state", "ringing --> idle = missed call");
                        // ringing --> idle = missed call
                        //triggerSenses(Sense.CallEvent.MISSED);
                    }
                    break;
            }

            mCallState = state;
        }
    }

    public static void init(Context c) {
        c.startService(new Intent(c, PhoneStateService.class));
        Log.d("Service enabled","Service enabled: " + true);
    }
}
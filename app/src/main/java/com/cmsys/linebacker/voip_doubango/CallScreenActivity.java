package com.cmsys.linebacker.voip_doubango;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;
import org.doubango.ngn.utils.NgnStringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.MessageUtils;

public class CallScreenActivity extends AppCompatActivity {
    private static final String TAG = CallScreenActivity.class.getCanonicalName();

    public static final int ACTION_NONE = 0;
    public static final int ACTION_RESTORE_LAST_STATE = 1;
    public static final int ACTION_SHOW_AVSCREEN = 2;
    public static final int ACTION_SHOW_CONTSHARE_SCREEN = 3;
    public static final int ACTION_SHOW_SMS = 4;
    public static final int ACTION_SHOW_CHAT_SCREEN = 5;

    //private final NgnEngine mEngine;
    private final Engine mEngine;
    private TextView mTvInfo;
    private TextView mTvRemote;
    private Button mBtHangUp;
    private Button mBtPickUp;
    private LinearLayout mLlOptions;

    private NgnAVSession mSession;
    private BroadcastReceiver mSipBroadCastRecv;

    public CallScreenActivity() {
        super();
        //mEngine = NgnEngine.getInstance();
        mEngine = (Engine) Engine.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSession = NgnAVSession.getSession(extras.getLong(DoubangoUtils.EXTRAT_SIP_SESSION_ID));
        }

        if (mSession == null) {
            String mId = getIntent().getStringExtra("session-id");
            if (NgnStringUtils.isNullOrEmpty(mId)) {
                Log.e(TAG, "Invalid audio/video session");
                finish();
                return;
            }
            mSession = NgnAVSession.getSession(NgnStringUtils.parseLong(mId, -1));
            if (mSession == null) {
                Log.e(TAG, String.format("Cannot find audio/video session with id=%s", mId));
                finish();
                return;
            }
            mSession.incRef();
            mSession.setContext(this);
        }

        if (mSession == null) {
            Log.e(TAG, "Null session");
            finish();
            return;
        }
        mSession.incRef();
        mSession.setContext(this);

        // listen for audio/video session state
        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleSipEvent(intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        registerReceiver(mSipBroadCastRecv, intentFilter);

        setupViews();

        //
        // Wake the screen and ignore "face touches"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);
    }

    private void setupViews() {
        mTvInfo = (TextView) findViewById(R.id.call_screen_textView_info);
        mTvRemote = (TextView) findViewById(R.id.callscreen_textView_remote);
        mBtHangUp = (Button) findViewById(R.id.callscreen_button_hangup);
        mBtPickUp = (Button) findViewById(R.id.callscreen_button_pickup);
        mLlOptions = (LinearLayout) findViewById(R.id.callscreen_ll_options);

        mBtHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.hangUpCall();
                    mLlOptions.setVisibility(View.GONE);
                }
            }
        });

        mBtPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.acceptCall();
                    mLlOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        mTvRemote.setText(mSession.getRemotePartyDisplayName());
        mTvInfo.setText(getStateDesc(mSession.getState()));

        // Setup DTMF buttons
        loadKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (mSession != null) {
            final InviteState callState = mSession.getState();
            mTvInfo.setText(getStateDesc(callState));
            if (callState == InviteState.TERMINATING || callState == InviteState.TERMINATED) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (mSipBroadCastRecv != null) {
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }

        if (mSession != null) {
            mSession.setContext(null);
            mSession.decRef();
        }
        super.onDestroy();
    }

    private String getStateDesc(InviteState state) {
        switch (state) {
            case NONE:
            default:
                return "Unknown";
            case INCOMING:
                return "Incoming";
            case INPROGRESS:
                return "Inprogress";
            case REMOTE_RINGING:
                return "Ringing";
            case EARLY_MEDIA:
                return "Early media";
            case INCALL:
                return "In Call";
            case TERMINATING:
                return "Terminating";
            case TERMINATED:
                return "termibated";
        }
    }

    private void handleSipEvent(Intent intent) {
        if (mSession == null) {
            Log.e(TAG, "Invalid session object");
            return;
        }
        final String action = intent.getAction();
        if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
            NgnInviteEventArgs args = intent.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);
            if (args == null) {
                Log.e(TAG, "Invalid event args");
                return;
            }
            if (args.getSessionId() != mSession.getId()) {
                return;
            }

            final InviteState callState = mSession.getState();
            mTvInfo.setText(getStateDesc(callState));
            switch (callState) {
                case REMOTE_RINGING:
                    mEngine.getSoundService().startRingBackTone();
                    break;
                case INCOMING:
                    mEngine.getSoundService().startRingTone();
                    break;
                case EARLY_MEDIA:
                case INCALL:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    mSession.setSpeakerphoneOn(false);
                    break;
                case TERMINATING:
                case TERMINATED:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private View.OnClickListener mOnKeyboardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSession != null) {
                mSession.sendDTMF(NgnStringUtils.parseInt(v.getTag().toString(), -1));
                MessageUtils.toast(v.getContext(), "DTMF pulse sent", false);
            }
        }
    };

    private void loadKeyboard() {
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_0), "0", "+", DialerUtils.TAG_0, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_1), "1", "", DialerUtils.TAG_1, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_2), "2", "ABC", DialerUtils.TAG_2, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_3), "3", "DEF", DialerUtils.TAG_3, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_4), "4", "GHI", DialerUtils.TAG_4, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_5), "5", "JKL", DialerUtils.TAG_5, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_6), "6", "MNO", DialerUtils.TAG_6, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_7), "7", "PQRS", DialerUtils.TAG_7, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_8), "8", "TUV", DialerUtils.TAG_8, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_9), "9", "WXYZ", DialerUtils.TAG_9, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_star), "*", "", DialerUtils.TAG_STAR, mOnKeyboardClickListener);
//        DialerUtils.setDialerTextButton(view.findViewById(R.id.view_dialer_buttons_sharp), "#", "", DialerUtils.TAG_SHARP, mOnKeyboardClickListener);
    }

}

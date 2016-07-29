package com.cmsys.linebacker.voip_doubango;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.anim.IncomingCallAnimation;
import com.cmsys.linebacker.ui.SettingsActivity;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.ColorUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;
import com.cmsys.linebacker.util.RoundedImageView;
import com.cmsys.linebacker.util.ViewUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.InputStream;

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
    private String mRemoteCallerId;
    private TextView mTvInfo;
    private TextView mTvRemote;
    private ImageButton mBtSpeaker, mBtShowKeyboard;
    private Button mBtHangUp, mBtPickUp, mBtOptionsAccept, mBtOptionsMail, mBtOptionsReject;
    private Button mBtVmDefaultPassword, mBtVmAlreadyLogged, mBtVmUserName, mBtVmMainGreeting, mBtVmBusyGreeting;
    private LinearLayout mLlOptions, mLlKeyboard, mLlVmPassword, mLlVmSetup, mLlAcceptRejectOptions;
    private RoundedImageView mRivCallerImage;
    private EditText mEtKeyboardText;
    private ImageButton mBtKeyboardBackspace;
    private ImageView mIvAnimCircle1, mIvAnimCircle2;
    private IncomingCallAnimation mIncomingCallAnimation1, mIncomingCallAnimation2;

    private NgnAVSession mSession;
    private BroadcastReceiver mSipBroadCastRecv;
    private boolean mShowInterstitial = false;
    private boolean mShowVmSetupOptions;

    // https://developers.google.com/admob/android/interstitial#creating_the_adlistener
    private InterstitialAd mInterstitialAd;

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
            String callingActivity = extras.getString(CONSTANTS.BUNDLE_EXTRA_CALLING_ACTIVITY);
            if (callingActivity != null && callingActivity.equals(SettingsActivity.class.getName()))
                mShowVmSetupOptions = true;
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
        mTvInfo = (TextView) findViewById(R.id.callscreen_textView_info);
        mTvRemote = (TextView) findViewById(R.id.callscreen_textView_remote);
        mBtHangUp = (Button) findViewById(R.id.callscreen_button_hangup);
        mBtPickUp = (Button) findViewById(R.id.callscreen_button_pickup);
        mBtSpeaker = (ImageButton) findViewById(R.id.callscreen_button_speaker);
        //mBtSpeaker.setText(getString(R.string.speaker) + " " + getString(R.string.on));
        mLlOptions = (LinearLayout) findViewById(R.id.callscreen_ll_options);
        mLlKeyboard = (LinearLayout) findViewById(R.id.callscreen_ll_keyboard);
        mLlVmPassword = (LinearLayout) findViewById(R.id.llVmPassword);
        mLlVmSetup = (LinearLayout) findViewById(R.id.llVmSetup);
        mLlAcceptRejectOptions = (LinearLayout) findViewById(R.id.llAcceptRejectOptions);
        mBtKeyboardBackspace = (ImageButton) findViewById(R.id.call_screen_keyboard_backspace);
        mBtShowKeyboard = (ImageButton) findViewById(R.id.callscreen_show_keyboard);
        mRivCallerImage = (RoundedImageView) findViewById(R.id.callscreen_caller_image);
        mEtKeyboardText = (EditText) findViewById(R.id.callscreen_keyboard_edittext);
        mBtOptionsAccept = (Button) findViewById(R.id.view_dialer_buttons_accept);
        mBtOptionsMail = (Button) findViewById(R.id.view_dialer_buttons_mail);
        mBtOptionsReject = (Button) findViewById(R.id.view_dialer_buttons_reject);
        mBtVmDefaultPassword = (Button) findViewById(R.id.bVmDefaultPassword);
        mBtVmAlreadyLogged = (Button) findViewById(R.id.bVmAlreadyLogged);
        mBtVmUserName = (Button) findViewById(R.id.bVmUserName);
        mBtVmMainGreeting = (Button) findViewById(R.id.bVmMainGreeting);
        mBtVmBusyGreeting = (Button) findViewById(R.id.bVmBusyGreeting);
        mIvAnimCircle1 = (ImageView) findViewById(R.id.iv_anim_circle_1);
        mIvAnimCircle2 = (ImageView) findViewById(R.id.iv_anim_circle_2);

        // Animation 1
        mIncomingCallAnimation1 = new IncomingCallAnimation(
                mIvAnimCircle1,
                300,
                150
        );
        mIncomingCallAnimation1.setDuration(3000);
        mIncomingCallAnimation1.setRepeatCount(Animation.INFINITE);
        mIvAnimCircle1.startAnimation(mIncomingCallAnimation1);
        // Animation 2
        mIncomingCallAnimation2 = new IncomingCallAnimation(
                mIvAnimCircle2,
                300,
                150
        );
        mIncomingCallAnimation2.setDuration(2000);
        mIncomingCallAnimation2.setRepeatCount(Animation.INFINITE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvAnimCircle2.startAnimation(mIncomingCallAnimation2);
            }
        }, 1000);

        // Set listeners
        mBtHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.hangUpCall();
                    mShowInterstitial = true;
                    mLlOptions.setVisibility(View.GONE);
                }
            }
        });

        mBtPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.acceptCall();
                    mShowInterstitial = true;
                    mBtPickUp.setVisibility(View.GONE);
                    mLlOptions.setVisibility(View.VISIBLE);
                    mBtHangUp.setText(getString(R.string.call_screen_end));
                    // Shrink contact's image
                    mRivCallerImage.getLayoutParams().width /= 2;
                    mRivCallerImage.getLayoutParams().height /= 2;
                    // Remove animation
                    mIvAnimCircle1.clearAnimation();
                    mIvAnimCircle1.setVisibility(View.GONE);
                    mIvAnimCircle2.clearAnimation();
                    mIvAnimCircle2.setVisibility(View.GONE);
                    ((FrameLayout) findViewById(R.id.call_screen_frame_layout))
                            .getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        });

        mBtSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.setSpeakerphoneOn(!mSession.isSpeakerOn());
                    //mBtSpeaker.setText(getString(R.string.speaker) + " " + (mSession.isSpeakerOn() ? getString(R.string.off) : getString(R.string.on)));
                    if (mSession.isSpeakerOn())
                        //mBtSpeaker.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        mBtSpeaker.setColorFilter(Color.GREEN);
                    else
                        //mBtSpeaker.setBackgroundResource(R.drawable.util_button_pressed_action_transp);
                        mBtSpeaker.setColorFilter(Color.BLACK);
                }
            }
        });

        mBtShowKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLlKeyboard.getVisibility() == View.VISIBLE) {
                    //mLlKeyboard.setVisibility(View.GONE);
                    ViewUtils.collapse(mLlKeyboard);
                    //mBtShowKeyboard.setText(getString(R.string.call_screen_show_keyboard));
                    mBtShowKeyboard.setImageResource(R.drawable.ic_expand_more_24dp);
                    mBtOptionsAccept.setVisibility(View.VISIBLE);
                    mBtOptionsMail.setVisibility(View.VISIBLE);
                    mBtOptionsReject.setVisibility(View.VISIBLE);
                } else {
                    //mLlKeyboard.setVisibility(View.VISIBLE);
                    ViewUtils.expand(mLlKeyboard);
                    //mBtShowKeyboard.setText(getString(R.string.call_screen_hide_keyboard));
                    mBtShowKeyboard.setImageResource(R.drawable.ic_expand_less_24dp);
                    mEtKeyboardText.setText("");
                    mBtOptionsAccept.setVisibility(View.GONE);
                    mBtOptionsMail.setVisibility(View.GONE);
                    mBtOptionsReject.setVisibility(View.GONE);
                }
            }
        });

        mBtKeyboardBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = mEtKeyboardText.getText().toString();
                if (currentText.length() > 0)
                    mEtKeyboardText.setText(currentText.substring(0, currentText.length() - 1));
            }
        });

        mBtVmDefaultPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        MessageUtils.toast(getApplicationContext(), "start", false);
                        try {
                            mSession.sendDTMF(NgnStringUtils.parseInt("1", -1));
//                            MessageUtils.toast(getApplicationContext(), "1", false);
                            Thread.sleep(100);
                            mSession.sendDTMF(NgnStringUtils.parseInt("2", -1));
//                            MessageUtils.toast(getApplicationContext(), "2", false);
                            Thread.sleep(100);
                            mSession.sendDTMF(NgnStringUtils.parseInt("3", -1));
//                            MessageUtils.toast(getApplicationContext(), "3", false);
                            Thread.sleep(100);
                            mSession.sendDTMF(NgnStringUtils.parseInt("4", -1));
//                            MessageUtils.toast(getApplicationContext(), "4", false);
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                        }
                    }
                }).start();
//                mEtKeyboardText.setText(mEtKeyboardText.getText() + "12345");
                mLlVmSetup.setVisibility(View.VISIBLE);
//                mLlVmPassword.setVisibility(View.GONE);
            }
        });

        mBtVmAlreadyLogged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlVmSetup.setVisibility(View.VISIBLE);
                mLlVmPassword.setVisibility(View.GONE);
            }
        });

        mBtVmUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.sendDTMF(NgnStringUtils.parseInt("0", -1));
                mSession.sendDTMF(NgnStringUtils.parseInt("3", -1));
            }
        });

        mBtVmMainGreeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.sendDTMF(NgnStringUtils.parseInt("0", -1));
                mSession.sendDTMF(NgnStringUtils.parseInt("1", -1));
            }
        });

        mBtVmBusyGreeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.sendDTMF(NgnStringUtils.parseInt("0", -1));
                mSession.sendDTMF(NgnStringUtils.parseInt("2", -1));
            }
        });

        //--------------------------------------------------------

        if (mShowVmSetupOptions) {
            mLlVmPassword.setVisibility(View.VISIBLE);
            mLlAcceptRejectOptions.setVisibility(View.GONE);
            mBtPickUp.performClick();
            mBtShowKeyboard.performClick();
        }

        //--------------------------------------------------------

        mRemoteCallerId = mSession.getRemotePartyDisplayName();
        if (mRemoteCallerId != null) {
            mTvRemote.setText(mRemoteCallerId);
            // Get contact id if exists
            Long contactId = PhoneContactUtils.getContactIdByPhone(getApplicationContext(), mRemoteCallerId);
            // Load contact name and image if exists
            if (contactId != null) {
                mTvInfo.setText(getString(R.string.call_screen_info_when_known));
                // Set contact name
                String contactName = PhoneContactUtils.getDisplayNameById(getApplicationContext(), contactId);
                if (!TextUtils.isEmpty(contactName)) {
                    mTvRemote.setText(contactName);
                }
                // Set contact image
                InputStream inputStream = PhoneContactUtils.getThumbnailPhotoById(getApplicationContext(), contactId);
                if (inputStream != null) {
                    mRivCallerImage.setRoundedDisabled(false);
                    mRivCallerImage.setImageDrawable(Drawable.createFromStream(inputStream, ""));
                } else {
                    mRivCallerImage.setRoundedDisabled(true);
                    mRivCallerImage.setImageResource(R.drawable.ic_account_circle_24dp);
                    mRivCallerImage.setColorFilter(ColorUtils.RandomColor.getRandomColor());
                }
            }
        }
        // Setup DTMF buttons
        loadKeyboard();

        // Load Interstitial Ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // DO SOMETHING
//                requestNewInterstitial();
                MessageUtils.toast(getApplicationContext(), "INTERSTITIAL AD CLOSED", false);
            }
        });
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (mSession != null) {
            final InviteState callState = mSession.getState();
            //mTvInfo.setText(getStateDesc(callState));
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

        // CJG edited 20160623
        // Show Interstitial Ad
        if (mInterstitialAd != null && mInterstitialAd.isLoaded() && mShowInterstitial) {
            mInterstitialAd.show();
        } else {
            MessageUtils.toast(this, "INTERSTITIAL AD NOT READY", false);
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
            //mTvInfo.setText(getStateDesc(callState));
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
                String pressedKey = v.getTag().toString();
                mSession.sendDTMF(NgnStringUtils.parseInt(pressedKey, -1));
                //MessageUtils.toast(v.getContext(), "DTMF pulse sent - " + pressedKey, false);
                mEtKeyboardText.setText(mEtKeyboardText.getText() + pressedKey);
            }
        }
    };

    private void loadKeyboard() {
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_accept), "1", "", DialerUtils.TAG_1, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_mail), "2", "ABC", DialerUtils.TAG_2, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_reject), "3", "DEF", DialerUtils.TAG_3, mOnKeyboardClickListener);
        //
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_0), "0", "+", DialerUtils.TAG_0, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_1), "1", "", DialerUtils.TAG_1, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_2), "2", "ABC", DialerUtils.TAG_2, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_3), "3", "DEF", DialerUtils.TAG_3, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_4), "4", "GHI", DialerUtils.TAG_4, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_5), "5", "JKL", DialerUtils.TAG_5, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_6), "6", "MNO", DialerUtils.TAG_6, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_7), "7", "PQRS", DialerUtils.TAG_7, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_8), "8", "TUV", DialerUtils.TAG_8, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_9), "9", "WXYZ", DialerUtils.TAG_9, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_star), "*", "", DialerUtils.TAG_STAR, mOnKeyboardClickListener);
        DialerUtils.setDialerTextButton(findViewById(R.id.view_dialer_buttons_sharp), "#", "", DialerUtils.TAG_SHARP, mOnKeyboardClickListener);
    }

}

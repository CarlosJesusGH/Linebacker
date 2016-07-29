package com.cmsys.linebacker.voip_doubango;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.DateUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;

/**
 * Created by CarlosJesusGH on 09/02/16.
 */
public class DoubangoUtils {
    private static String TAG = DoubangoUtils.class.getCanonicalName();

    private Context mContext;
    private BroadcastReceiver mSipBroadCastRecv;
    //private final NgnEngine mEngine;
    private final Engine mEngine;
    private final INgnConfigurationService mConfigurationService;
    private final INgnSipService mSipService;
    private final static String SIP_DOMAIN = "voip.mylinebacker.net";
    private final static String SIP_SERVER_HOST = "voip.mylinebacker.net";
    private final static int SIP_SERVER_PORT = 5060;
    private static String SIP_USERNAME = null;
    private static String SIP_PASSWORD = null; //"Linebacker2016*";
    public final static String EXTRAT_SIP_SESSION_ID = "SipSession";

    public DoubangoUtils(Context context) {
        mContext = context;
        //
        //mEngine = NgnEngine.getInstance();
        mEngine = (Engine) Engine.getInstance();
        mConfigurationService = mEngine.getConfigurationService();
        mSipService = mEngine.getSipService();
    }

    public NgnEngine getEngine() {
        return mEngine;
    }

    public BroadcastReceiver getSipBroadCastRecv() {
        return mSipBroadCastRecv;
    }

    public void setSipBroadCastRecv(BroadcastReceiver broadcastReceiver) {
        mSipBroadCastRecv = broadcastReceiver;
    }

    public void Init() {
        // Subscribe for registration state changes
        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Event
                if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                    NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e(TAG, "Invalid event args");
                        return;
                    }
                    switch (args.getEventType()) {
                        case REGISTRATION_NOK:
                            MessageUtils.toast(mContext, "Failed to register", false);
                            break;
                        case UNREGISTRATION_OK:
                            MessageUtils.toast(mContext, "You are now unregistered", false);
                            //MessageUtils.notification(mContext, "Unregistered: " + SIP_USERNAME, DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                            // Save SharedPreferences (Not necessary) TODO: check if necessary
//                            SharedPreferencesUtils.removeKey(mContext, context.getString(R.string.pref_key_voip_extension));
//                            SharedPreferencesUtils.removeKey(mContext, context.getString(R.string.pref_key_voip_password));
                            break;
                        case REGISTRATION_OK:
                            MessageUtils.toast(mContext, "You are now registered", false);
                            //MessageUtils.notification(mContext, "Registered: " + SIP_USERNAME, DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                            // Save SharedPreferences
//                            SharedPreferencesUtils.putOrEditString(mContext, context.getString(R.string.pref_key_voip_extension), SIP_USERNAME);
//                            SharedPreferencesUtils.putOrEditString(mContext, context.getString(R.string.pref_key_voip_password), SIP_PASSWORD);
                            break;
                        case REGISTRATION_INPROGRESS:
                            MessageUtils.toast(mContext, "Trying to register...", false);
                            break;
                        case UNREGISTRATION_INPROGRESS:
                            MessageUtils.toast(mContext, "Trying to unregister...", false);
                            break;
                        case UNREGISTRATION_NOK:
                            MessageUtils.toast(mContext, "Failed to unregister", false);
                            break;
                    }
                    //mBtSignInOut.setText(mSipService.isRegistered() ? "Sign Out" : "Sign In");
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        mContext.registerReceiver(mSipBroadCastRecv, intentFilter);
    }

    public boolean serverSignInOut(String userName, String password) {
        SIP_USERNAME = userName;
        SIP_PASSWORD = password;

        if (mEngine.isStarted()) {
            if (!mSipService.isRegistered()) {
                // Set credentials
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, SIP_USERNAME);
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", SIP_USERNAME, SIP_DOMAIN));
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, SIP_PASSWORD);
                mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, SIP_SERVER_HOST);
                mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, SIP_SERVER_PORT);
                mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, SIP_DOMAIN);

                //CarlosJesusGH
                mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G, true);
                mConfigurationService.putBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART, true);
                //mConfigurationService.putString(NgnConfigurationEntry.QOS_SIP_CALLS_TIMEOUT, "10");
                //mConfigurationService.putString(NgnConfigurationEntry.QOS_SIP_SESSIONS_TIMEOUT, "10");
                // You may want to leave the registration timeout to the default 1700 seconds
                //mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, 10);
                mConfigurationService.putString(NgnConfigurationEntry.QOS_REFRESHER, "uas"); //"uac");

                // VERY IMPORTANT: Commit changes
                mConfigurationService.commit();
                // register (log in)
                mSipService.register(mContext);
            } else {
                // unregister (log out)
                mSipService.unRegister();
            }
        } else {
            MessageUtils.toast(mContext, "Engine not started yet", false);
        }
        return true;
    }

    public boolean isSipServiceRegistered() {
        if (mEngine.isStarted())
            if (mSipService.isRegistered())
                return true;
        return false;
    }

    public boolean makeVoiceCall(String phoneNumber, Pair<String, String> paramExtra) {
        final String validUri = NgnUriUtils.makeValidSipUri(String.format("sip:%s@%s", phoneNumber, SIP_DOMAIN));
        if (validUri == null) {
            MessageUtils.toast(mContext, "failed to normalize sip uri '" + phoneNumber + "'", false);
            return false;
        }
        NgnAVSession avSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);

        Intent i = new Intent();
        i.setClass(mContext, CallScreenActivity.class);
        i.putExtra(EXTRAT_SIP_SESSION_ID, avSession.getId());
        if (paramExtra != null)
            i.putExtra(paramExtra.first, paramExtra.second);
        mContext.startActivity(i);

        return avSession.makeCall(validUri);
    }

    public static boolean bringToFront(int action, String[]... args) {
        Intent intent = new Intent(IMSDroid.getContext(), CallScreenActivity.class);
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("action", action);
            for (String[] arg : args) {
                if (arg.length != 2) {
                    continue;
                }
                intent.putExtra(arg[0], arg[1]);
            }
            IMSDroid.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean receiveCall(NgnAVSession avSession) {
        bringToFront(CallScreenActivity.ACTION_SHOW_AVSCREEN,
                new String[]{"session-id", Long.toString(avSession.getId())}
        );
        return true;
    }
}

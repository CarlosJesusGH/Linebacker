package com.cmsys.linebacker.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.ui.RecordingLogActivity;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.LogUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.voip_doubango.Engine;
import com.cmsys.linebacker.voip_doubango.NativeService;

import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import java.util.ArrayList;
import java.util.Calendar;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;


/**
 * Created by cj on 02/12/15.
 */
public class CallBlockReceiver extends BroadcastReceiver {
    private static final String TAG = makeLogTag(CallBlockReceiver.class);

    public static final String PREF_KEY_PREVIOUS_STATE = "previousState";
    private String mPhoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        //PhoneCallUtils.end(context);
        //PhoneCallUtils.blockTheCall();
        //super.onReceive(context, intent);


        // Get some SharedPreferences for settings value
        String previousState = SharedPreferencesUtils.getString(context, PREF_KEY_PREVIOUS_STATE, TelephonyManager.EXTRA_STATE_IDLE);
        mPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        LogUtils.LOGI(TAG, intent.getStringExtra(TelephonyManager.EXTRA_STATE));
        //
        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String currentState = "";
        switch (event) {
            case "RINGING":
                if ((previousState.equals(TelephonyManager.EXTRA_STATE_IDLE)) || (previousState.equals("FIRST_CALL_RINGING")))
                    currentState = "FIRST_CALL_RINGING";
                if ((previousState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) || (previousState.equals("SECOND_CALL_RINGING")))
                    currentState = "SECOND_CALL_RINGING";
                break;
            case "OFFHOOK":
                if ((previousState.equals(TelephonyManager.EXTRA_STATE_IDLE)) || (previousState.equals("FIRST_CALL_RINGING")) || previousState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
                    currentState = TelephonyManager.EXTRA_STATE_OFFHOOK;
                if (previousState.equals("SECOND_CALL_RINGING"))
                    currentState = TelephonyManager.EXTRA_STATE_OFFHOOK;
                break;
            case "IDLE":
                if ((previousState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) || (previousState.equals("SECOND_CALL_RINGING")) || (previousState.equals(TelephonyManager.EXTRA_STATE_IDLE)))
                    currentState = TelephonyManager.EXTRA_STATE_IDLE;
                if (previousState.equals("FIRST_CALL_RINGING"))
                    currentState = TelephonyManager.EXTRA_STATE_IDLE;
                break;
        }
        SharedPreferencesUtils.putOrEditString(context, PREF_KEY_PREVIOUS_STATE, currentState);
        //
        if (event.equals(TelephonyManager.EXTRA_STATE_RINGING) && currentState.equals("FIRST_CALL_RINGING")) {
            MessageUtils.toast(context, "Current State = " + currentState, true);
            //First mute ringing
            AudioManager audioManager = PhoneCallUtils.muteRinging(context);
            //PhoneCallUtils.setSoundOffVibrateOn(context);
            //
            // If phone number is empty, get extra number.
            if (mPhoneNumber == null)
                mPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            boolean phoneNumberExists = PhoneContactUtils.contactPhoneExists(context, mPhoneNumber);
            // Check if blockCalls is enabled
            boolean blockCalls = SharedPreferencesUtils.getBoolean(context, context.getString(R.string.pref_key_setting_block_calls), true);
            MessageUtils.toast(context, mPhoneNumber + "\nPhone Number " + (phoneNumberExists ? "" : "DOESN'T ")
                    + "Exists. \nCallBlock is " + (blockCalls ? "Enabled" : "Disabled"), true);
            // End phone call if number doesn't exists and blockCalls is enabled
            if (!phoneNumberExists) {
                if (blockCalls) {
                    PhoneCallUtils.endCall(context);
                    //PhoneCallUtils.disconnectCall(context);
                    // Show notification ---------
                    // Create unique id
                    int notificationId = (int) Calendar.getInstance().getTimeInMillis();
                    ArrayList<NotificationCompat.Action> actions = new ArrayList<>();
                    //
                    // Create Intent for notification
                    Intent callBackIntent = new Intent(context, NotificationButtonReceiver.class);
                    callBackIntent.putExtra(CONSTANTS.NOTIFICATION_ID, notificationId);
                    callBackIntent.putExtra(CONSTANTS.ACTION_ID, CONSTANTS.ACTION_CALL_BACK);
                    callBackIntent.putExtra(CONSTANTS.PHONE_NUMBER_ID, mPhoneNumber);
                    // Create PendingIntent for notification
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId + 0,  // Id must be different for every action button
                            callBackIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    // Create Notification Action
                    NotificationCompat.Action action = new NotificationCompat.Action
                            .Builder(R.drawable.ic_call_24dp, "Call Back", pendingIntent).build();
                    // Add Action to array
                    actions.add(action);
                    // Show Notification
                    MessageUtils.notification(context, "LINEBACKER Handled Call", "Incoming Number: " + mPhoneNumber, notificationId, RecordingLogActivity.class, actions, true, null, true);
                    //PhoneCallUtils.setSoundOnVibrateOff(context);
                }
                registerOnPbx(context);
            } else {    // Phone number does exist
                Engine mEngine = (Engine) Engine.getInstance();
                INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();
                INgnSipService mSipService = mEngine.getSipService();
                //
                if (mEngine.isStarted())
                    if (mSipService.isRegistered()) {
                        if (mSipService.unRegister())
                            //MessageUtils.notification(context, "Unregistered: Linebacker", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                            //Log.i(TAG, "Sip session unregister successful");
                            MessageUtils.toast(context, "Sip session unregister successful", false);
                    }
            }
            // Un-mute incoming calls
            PhoneCallUtils.unMuteRinging(context, audioManager);
        }
        // Reconnect to SIP PBX even when incoming call is SecondCall
        else if (event.equals(TelephonyManager.EXTRA_STATE_RINGING) && currentState.equals("SECOND_CALL_RINGING")) {
            // If phone number is empty, get extra number.
            if (mPhoneNumber == null)
                mPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            boolean phoneNumberExists = PhoneContactUtils.contactPhoneExists(context, mPhoneNumber);
            if (!phoneNumberExists) {
                registerOnPbx(context);
            }
        }
    }

    private void registerOnPbx(Context context) {
        // Reconnect to Sip server in case it isn't
        SharedPreferences settingsNGN = context.getSharedPreferences(NgnConfigurationEntry.SHARED_PREF_NAME, 0);
        if (settingsNGN != null && settingsNGN.getBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART.toString(), NgnConfigurationEntry.DEFAULT_GENERAL_AUTOSTART)) {
            Intent i = new Intent(context, NativeService.class);
            i.putExtra("autostarted", true);
            context.startService(i);
        }
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneCallStateListener customPhoneListener = new PhoneCallStateListener(context);
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public class PhoneCallStateListener extends PhoneStateListener {

        private Context context;
        public PhoneCallStateListener(Context context){
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);

            switch (state) {

                case TelephonyManager.CALL_STATE_RINGING:

                    //String block_number = prefs.getString("block_number", null);
                    String block_number = "81386";
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    //Turn ON the mute
                    audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        Toast.makeText(context, "in"+block_number, Toast.LENGTH_LONG).show();
                        Class clazz = Class.forName(telephonyManager.getClass().getName());
                        Method method = clazz.getDeclaredMethod("getITelephony");
                        method.setAccessible(true);
                        ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                        //Checking incoming call number
                        System.out.println("Call "+block_number);

                        //if (incomingNumber.equalsIgnoreCase("+91"+block_number)) {
                        if (incomingNumber.contains("81386")) {
                            //telephonyService.silenceRinger();//Security exception problem
                            telephonyService = (ITelephony) method.invoke(telephonyManager);
                            telephonyService.silenceRinger();
                            System.out.println(" in  "+block_number);
                            telephonyService.endCall();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    //Turn OFF the mute
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    break;
                case PhoneStateListener.LISTEN_CALL_STATE:

            }
            super.onCallStateChanged(state, incomingNumber);
        }}*/
}
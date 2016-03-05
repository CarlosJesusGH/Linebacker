package com.cmsys.linebacker.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.ui.RecordingLogActivity;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
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
import java.util.Date;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;


/**
 * Created by cj on 02/12/15.
 */
public class CallBlockReceiver extends BroadcastReceiver {
    private static final String TAG = makeLogTag(CallBlockReceiver.class);

    public static final String LISTEN_ENABLED = "ListenEnabled";
    private String mPhoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        //PhoneCallUtils.end(context);
        //PhoneCallUtils.blockTheCall();
        //super.onReceive(context, intent);


        // Get some SharedPreferences for settings values
        SharedPreferences settings = context.getSharedPreferences(LISTEN_ENABLED, 0);
        boolean silent = settings.getBoolean("silentMode", true);
        mPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        LogUtils.LOGI(TAG, intent.getStringExtra(TelephonyManager.EXTRA_STATE));
        //
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            //First mute ringing
            AudioManager audioManager = PhoneCallUtils.muteRinging(context);
            //PhoneCallUtils.endCall(context);
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
            if(!phoneNumberExists && blockCalls) {
                PhoneCallUtils.endCall(context);
                // Show notification ---------
                // Create unique id
                int notificationId = (int) Calendar.getInstance().getTimeInMillis();
                ArrayList<NotificationCompat.Action> actions = new ArrayList<>();
                //
                // Create Intent
                Intent callBackIntent = new Intent(context, NotificationButtonReceiver.class);
                callBackIntent.putExtra(CONSTANTS.NOTIFICATION_ID, notificationId);
                callBackIntent.putExtra(CONSTANTS.ACTION_ID, CONSTANTS.ACTION_CALL_BACK);
                callBackIntent.putExtra(CONSTANTS.PHONE_NUMBER_ID, mPhoneNumber);
                // Create PendingIntent
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

                // Reconnect to Sip server in case it isn't
                SharedPreferences settingsNGN = context.getSharedPreferences(NgnConfigurationEntry.SHARED_PREF_NAME, 0);
                if (settingsNGN != null && settingsNGN.getBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART.toString(), NgnConfigurationEntry.DEFAULT_GENERAL_AUTOSTART)) {
                    Intent i = new Intent(context, NativeService.class);
                    i.putExtra("autostarted", true);
                    context.startService(i);
                }
            } else { // Phone does exist or blockCalls disabled
                Engine mEngine = (Engine) Engine.getInstance();
                INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();
                INgnSipService mSipService = mEngine.getSipService();
                //
                if (mEngine.isStarted())
                    if (mSipService.isRegistered()) {
                        if (mSipService.unRegister())
                            //MessageUtils.notification(context, "Unregistered: Linebacker", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                            Log.i(TAG, "Sip session unregister successful");
                    }
                // Un-mute incoming calls
                PhoneCallUtils.unMuteRinging(context, audioManager);     // TODO: place this line again
            }
        }
    }
}
package com.cmsys.linebacker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.cmsys.linebacker.ui.RecordingLogActivity;
import com.cmsys.linebacker.util.LogUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;

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
            MessageUtils.toast(context, mPhoneNumber +"\nPhone Number " + (phoneNumberExists ? "" : "DOESN'T ") + "Exists", true);
            // End phone call if number doesn't exists
            if(!phoneNumberExists) {
                PhoneCallUtils.endCall(context);
                // Show notification
                Date now = new Date();
                // Use date to generate an unique id to differentiate the notifications.
                int mNotificationId = (int) now.getTime();
                MessageUtils.notification(context, "LINEBACKER Handled Call", "Incoming Number: " + mPhoneNumber, mNotificationId, RecordingLogActivity.class);
                //PhoneCallUtils.setSoundOnVibrateOff(context);
            }
            // Un-mute incoming calls
            PhoneCallUtils.unMuteRinging(context, audioManager);
        }
    }
}
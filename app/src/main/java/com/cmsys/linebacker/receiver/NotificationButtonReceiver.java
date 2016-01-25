package com.cmsys.linebacker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;

/**
 * Created by @CarlosJesusGH on 2016/01/20.
 */
public class NotificationButtonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(CONSTANTS.NOTIFICATION_ID, 0);
        int actionId = intent.getIntExtra(CONSTANTS.ACTION_ID, 0);
        //
        switch (actionId) {
            case CONSTANTS.ACTION_DISMISS:
                //MessageUtils.toast(context, "Dismiss", false);
                break;
            case CONSTANTS.ACTION_CALL_BACK:
                String phoneNumber = intent.getStringExtra(CONSTANTS.PHONE_NUMBER_ID);
                if (!TextUtils.isEmpty(phoneNumber))
                    PhoneCallUtils.dialNumber(context, phoneNumber);
                context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                break;
        }
        // Dismiss notification from id
        MessageUtils.dismissNotification(context, notificationId);
    }
}

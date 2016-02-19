package com.cmsys.linebacker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.voip_doubango.DoubangoUtils;
import com.cmsys.linebacker.voip_doubango.NativeService;

import org.doubango.ngn.utils.NgnConfigurationEntry;

import java.awt.font.TextAttribute;

/**
 * Created by CarlosJesusGH on 18/02/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Sample code starting service after boot completed
        //Intent startServiceIntent = new Intent(context, MyService.class);
        //context.startService(startServiceIntent);

        /*MessageUtils.toast(context, "BootCompletedReceiver", true);
        MessageUtils.notification(context, "Linebacker - Boot", "", 1, null, null, false, null, true);
        if(SharedPreferencesUtils.checkIfContainsKey(context, context.getString(R.string.pref_key_voip_extension))){

        } else{
            MessageUtils.toast(context, "No sip extension saved", true);
        }*/

        SharedPreferences settings = context.getSharedPreferences(NgnConfigurationEntry.SHARED_PREF_NAME, 0);
        if (settings != null && settings.getBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART.toString(), NgnConfigurationEntry.DEFAULT_GENERAL_AUTOSTART)) {
            Intent i = new Intent(context, NativeService.class);
            i.putExtra("autostarted", true);
            context.startService(i);
        }
    }
}
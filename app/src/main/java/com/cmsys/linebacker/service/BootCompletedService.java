package com.cmsys.linebacker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.voip_doubango.DoubangoUtils;

/**
 * Created by cj on 18/02/16.
 */
public class BootCompletedService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //
        /*new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();*/

        /*Context context = getApplicationContext();
        // Init Doubango utils
        DoubangoUtils mDoubango = new DoubangoUtils(context);
        mDoubango.Init();
        // Start Doubango Engine
        if(!mDoubango.getEngine().isStarted()){
            if(mDoubango.getEngine().start()){
                MessageUtils.toast(context, "Engine started", true);
            }
            else{
                MessageUtils.toast(context, "Failed to start the engine", true);
            }
        }
        // Doubango login
        String extension = SharedPreferencesUtils.getString(context, context.getString(R.string.pref_key_voip_extension), null);
        String password = SharedPreferencesUtils.getString(context, context.getString(R.string.pref_key_voip_password), null);
        if(!TextUtils.isEmpty(extension) && !TextUtils.isEmpty(password))
            mDoubango.serverSignInOut(extension, password);
        else
            MessageUtils.toast(context, "Extension or Password invalid", true);*/
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

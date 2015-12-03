package com.cmsys.linebacker.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

/**
 * Created by cj on 02/12/15.
 */
public class PhoneCallUtils {
    private static final String TAG = makeLogTag(PhoneCallUtils.class);

    public static void endCall(Context context){
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            // Try finishing call
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
        }
    }

    public static void rootBlockCall(){
        try {
            Thread.sleep(800);
            Runtime.getRuntime().exec(new String[]{"su", "-c", "input keyevent 6"});

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSoundOffVibrateOn(Context context){
      AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public static void setSoundOnVibrateOff(Context context){
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        //
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
    }

    public static AudioManager muteRinging(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Change the stream to your stream of choice.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        }
        return audioManager;
    }

    public static void unMuteRinging(Context context, AudioManager audioManagerX){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Change the stream to your stream of choice.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        }
    }
}

package com.cmsys.linebacker.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
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

    public static void endCall(Context context) {
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
            //telephonyService.endCall();
            // Try finishing call by subscription id
            int subscriberId = (int) Long.parseLong(tm.getSubscriberId());
            MessageUtils.toast(context, "SubscriberId: " + tm.getSubscriberId() + " (int)= " + subscriberId, true);
            //telephonyService.endCallForSubscriber(13454934);//subscriberId);
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
            ExceptionUtils.displayExceptionMessage(context, e);
        }
    }

    /*public static void disconnectCall(Context context){
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
//            e.printStackTrace();
//            Log.error(DialerActivity.this,
//                    "FATAL ERROR: could not connect to telephony subsystem");
//            Log.error(DialerActivity.this, "Exception object: " + e);
            ExceptionUtils.displayExceptionMessage(context, e);
        }
    }*/

    public static void rootBlockCall() {
        try {
            Thread.sleep(800);
            Runtime.getRuntime().exec(new String[]{"su", "-c", "input keyevent 6"});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSoundOffVibrateOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public static void setSoundOnVibrateOff(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        //
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
    }

    public static AudioManager muteRinging(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Change the stream to your stream of choice.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        }
        return audioManager;
    }

    public static void unMuteRinging(Context context, AudioManager audioManagerX) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Change the stream to your stream of choice.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        }
    }

    public static void callNumber(Context context, String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } else {
            MessageUtils.toast(context, "NOT ALLOWED", false);
        }
    }

    public static void dialNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}

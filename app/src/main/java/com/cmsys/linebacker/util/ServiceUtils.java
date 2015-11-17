package com.cmsys.linebacker.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by cj on 16/11/15.
 */
public class ServiceUtils {

    public static boolean isMyServiceRunning(Class<?> serviceClass, Activity pActivity) {
        ActivityManager manager = (ActivityManager) pActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

package com.cmsys.linebacker.util;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by cj on 24/11/15.
 */
public class PermissionUtils {

    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static boolean checkIfPermissionEnabled(String pPermission, Context pContext, boolean showToast){
        int res = pContext.checkCallingOrSelfPermission(pPermission);
        if(showToast)
            MessageUtils.toast(pContext, "Permission not enabled:\n" + pPermission, true);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkIfPermissionArrayEnabled(List<String> pPermissions, Context pContext, boolean showToast){
        boolean allEnabled = true;
        for (String permission: pPermissions) {
            checkIfPermissionEnabled(permission, pContext, showToast);
        }
        return allEnabled;
    }

}

package com.cmsys.linebacker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by cj on 22/11/15.
 */
public class SharedPreferencesUtils {
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor preferencesEditor;

    public static String getString(Context pContext, String pKey, String pDefaultValue){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        //
        return sharedPref.getString(pKey, pDefaultValue);
    }

    public static boolean getBoolean(Context pContext, String pKey, Boolean pDefaultValue){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        //
        return sharedPref.getBoolean(pKey, pDefaultValue);
    }

    public static boolean putOrEditString(Context pContext, String pKey, String pValue){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        preferencesEditor = sharedPref.edit();
        preferencesEditor.remove(pKey);
        preferencesEditor.putString(pKey, pValue);
        preferencesEditor.commit();
        //
        return true;
    }

    public static boolean putOrEditString(Context pContext, String pKey, boolean pValue){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        preferencesEditor = sharedPref.edit();
        preferencesEditor.remove(pKey);
        preferencesEditor.putBoolean(pKey, pValue);
        preferencesEditor.commit();
        //
        return true;
    }

    public static boolean putOrEditString(Context pContext, String pKey, int pValue){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        preferencesEditor = sharedPref.edit();
        preferencesEditor.remove(pKey);
        preferencesEditor.putInt(pKey, pValue);
        preferencesEditor.commit();
        //
        return true;
    }

    public static boolean removeKey(Context pContext, String pKey){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        preferencesEditor = sharedPref.edit();
        preferencesEditor.remove(pKey);
        preferencesEditor.commit();
        //
        return true;
    }

    public static boolean removeAll(Context pContext) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        preferencesEditor = sharedPref.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
        //
        return true;
    }

    public static String getUserIdFromPreferences(Context pContext, String pKey){
        String userId = SharedPreferencesUtils.getString(pContext, pKey, null);
        //
        return userId;
    }

    public static boolean checkIfContainsKey(Context pContext, String pKey){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        return sharedPref.contains(pKey);
    }
}

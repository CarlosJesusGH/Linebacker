package com.cmsys.linebacker.util;

import android.content.Context;

import com.cmsys.linebacker.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by cj on 17/12/15.
 */
public class UserAuthUtils {
    public static final String TYPE_CUSTOM = "custom";
    public static final String TYPE_FIREBASE_EMAIL = "firebase-email";
    public static final String TYPE_FIREBASE_GOOGLE = "firebase-google+";
    public static final String TYPE_GOOGLE = "google+";
    public static String AUTH_TYPE = "firebase-email";

    public static boolean isUserLogged() {
        switch (AUTH_TYPE) {
            case TYPE_CUSTOM:
                break;
            case TYPE_FIREBASE_EMAIL:
                break;
        }
        return false;
    }

    /**
     * Get unique id from logged in user, or NULL if it doesn't exists.
     * @return UserId or NULL
     */
    public static String getUserId(Context context) {
        switch (AUTH_TYPE) {
            case TYPE_CUSTOM:
                return SharedPreferencesUtils.getUserIdFromPreferences(context, context.getString(R.string.pref_key_user_id));
                //break;
            case TYPE_FIREBASE_EMAIL:
                // Firebase access
                Firebase.setAndroidContext(context);
                Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                AuthData authData = ref.getAuth();
                if (authData != null) {
                    // user authenticated
                    return authData.getUid();
                } else {
                    // no user authenticated
                }
                break;
        }
        return null;
    }

    public static String logUserIn() {
        return "";
    }

    public static boolean logUserOut(Context context) {
        switch (AUTH_TYPE) {
            case TYPE_CUSTOM:
                SharedPreferencesUtils.removeKey(context, context.getString(R.string.pref_key_user_id));
                break;
            case TYPE_FIREBASE_EMAIL:
                // Firebase access
                Firebase.setAndroidContext(context);
                Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                ref.unauth();
                break;
        }
        return  false;
    }
}

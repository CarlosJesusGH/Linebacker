package com.cmsys.linebacker.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by CarlosJesusGH on 31/12/15.
 */
public class GcmUtils {

    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Tag used on log messages.
     */
    static final String TAG = GcmUtils.class.getSimpleName();

    /**
     * Name of the key for the shared preferences to access the current device
     * registration id for GCM.
     */
    public static final String PROPERTY_REG_ID = "registrationId";

    /**
     * Name of the key for the shared preferences to access the current
     * application version, to see if GCM registration id needs to be updated.
     */
    private static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * * Log output.
     */
    private static final Logger LOG = Logger.getLogger(GcmUtils.class.getName());

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     * @return a boolean indicating if the Google Play Services are available.
     */
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     * @param applicationContext the Application context.
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(final Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(applicationContext);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * Returns the application version.
     * @param context the Application context.
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @param applicationContext the Application context.
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getGCMPreferences(final Context applicationContext) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the registration ID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this
     * demo since the device sends upstream messages to a server that echoes
     * back the message using the 'from' address in the message.
     */
    public static void sendRegistrationIdToFirebaseBackend(final Context context, final String regId, String userId) {
        try {
            // Get save changes to Firebase
            Firebase.setAndroidContext(context);
            final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_USER + File.separator + userId + File.separator + CONSTANTS.FIREBASE_FIELD_GCM_REGISTRATION_ID);
            fbRef.setValue(regId, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regId);
                }
            });
        } catch (Exception e) {
            LOG.warning("Exception when sending registration ID to the "
                    + "backend = "+ e.getMessage());
            // If there is an error, we will try again to register the
            // device with GCM the next time the MainActivity starts.
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     * @param applicationContext application's context.
     * @param registrationId     registration ID
     */
    public static void storeRegistrationId(final Context applicationContext,
                                     final String registrationId) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        int appVersion = getAppVersion(applicationContext);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }
}

package com.cmsys.linebacker.util;

import android.content.Context;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.SettingsBean;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.IOException;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

/**
 * Created by cj on 19/11/15.
 */
public class AppInitialSetupUtils {
    private static final String TAG = makeLogTag(AppInitialSetupUtils.class);

    public static  void createAppFolders(){
        // File route_sd = Environment.getRootDirectory();
        File route_sd = Environment.getExternalStorageDirectory();
        Log.i(TAG, route_sd.toString());
        //CONSTANTS.PATH_CASAGRI_EXTERNAL_INTERNAL = route_sd.toString();
        // getAssets().open(fileName);
        File fileApp = new File(route_sd, File.separator + CONSTANTS.FOLDER_NAME_ROOT);
        // Validates if folder exists /AppName
        if (!fileApp.exists()) {
            // if not, it is created
            if (fileApp.mkdir()) {
                Log.i(TAG, "Folder is created:" + fileApp.toString());
            }
        }

        File filePathImages = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_IMAGES);
        if (!filePathImages.exists()) {
            if (filePathImages.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathImages.toString());
            }
        }

        File filePathDocuments = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_DOCUMENTS);
        if (!filePathDocuments.exists()) {
            if (filePathDocuments.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathDocuments.toString());
            }
        }

        /*File fileDatabase = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_DATABASE);
        if (!fileDatabase.exists()) {
            if (fileDatabase.mkdir()) {
                Log.i(TAG, "Folder is created:" + fileDatabase.toString());
            }
        }*/

        File filePathAudio = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_AUDIOS);
        if (!filePathAudio.exists()) {
            if (filePathAudio.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathAudio.toString());
            }
        }

        File filePathLog = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_LOGS);
        if (!filePathLog.exists()) {
            if (filePathLog.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathLog.toString());
            }
        }
    }

    public static void getFirebaseSettings(final Context context, String mUserId){
        // Get settings from Firebase
        Firebase.setAndroidContext(context);
        final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_SETTINGS + File.separator + mUserId);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SettingsBean mSettings = (SettingsBean) dataSnapshot.getValue(SettingsBean.class);
                    SharedPreferencesUtils.putOrEditString(context, context.getString(R.string.pref_key_setting_block_calls), mSettings.isBlockCalls());
                    SharedPreferencesUtils.putOrEditString(context, context.getString(R.string.pref_key_setting_mobile_notification), mSettings.isMobileNotification());
                    SharedPreferencesUtils.putOrEditString(context, context.getString(R.string.pref_key_setting_email_notification), mSettings.isEmailNotification());
                    SharedPreferencesUtils.putOrEditString(context, context.getString(R.string.pref_key_setting_delete_weeks), mSettings.getDeleteAudiosEveryWeeks());
                    MessageUtils.toast(context, "Firebase settings read successfully", false);
                }
            }
            @Override public void onCancelled(FirebaseError firebaseError) {}
        });
    }

}

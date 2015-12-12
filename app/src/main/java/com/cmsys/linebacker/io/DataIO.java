package com.cmsys.linebacker.io;

import android.content.Context;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.SettingsBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;

/**
 * Created by cj on 26/11/15.
 */
public class DataIO {

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

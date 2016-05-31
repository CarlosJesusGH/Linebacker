package com.cmsys.linebacker.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.view.View;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;
import com.cmsys.linebacker.util.RestfulUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.File;
import java.util.HashMap;

/**
 * Created by @CarlosJesusGH on 10/03/16.
 * more info: http://stackoverflow.com/questions/1401280/how-to-listen-for-changes-in-contact-database
 */
public class PhoneContactsObserver extends ContentObserver {
    Context mContext;
    String mUserId;

    public PhoneContactsObserver(Context context, String userId) {
        super(null);
        mContext = context;
        mUserId = userId;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //
        //MessageUtils.notification(mContext, "PhoneContactObserver", "onChange", (int) System.currentTimeMillis(), null, null, false, null, true);
//        MessageUtils.toast(mContext, "PhoneContactObserver - onChange", false);

        // Upload new contacts to Linebacker DB
        // New Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //mu.setProgressBarHorizontalProgress(30);
                // Get phone contacts info
                HashMap<String, HashMap<String, Object>> hmContacts = PhoneContactUtils.getPhoneContactsHashMap(mContext);
                final int hmSize = hmContacts.size();
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mu.getTvMessage().setText(getString(R.string.uploading_contacts) + "\n" + String.format(getString(R.string.number_contacts), hmSize));
                    }
                });*/

                //mu.setProgressBarHorizontalProgress(60);
                // Save contacts to Firebase
                final Context context = mContext;
                Firebase.setAndroidContext(context);
                Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                fbRef = fbRef.child(CONSTANTS.FIREBASE_DOC_CONTACTS + File.separator + mUserId);
                fbRef.setValue(hmContacts, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        //mu.setProgressBarHorizontalProgress(90);
                        if (firebaseError != null) {
//                            MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
//                            MessageUtils.notification(mContext, "PhoneContactObserver", context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), (int) System.currentTimeMillis(), null, null, false, null, true);
                        } else {
//                            MessageUtils.toast(context, context.getString(R.string.upload_successful), false);
//                            MessageUtils.notification(mContext, "PhoneContactObserver", context.getString(R.string.upload_successful), (int) System.currentTimeMillis(), null, null, false, null, true);
                            RestMessageBean restMessageBean = null;
                            try {
                                restMessageBean = RestfulUtils.readRestfulAndParseToObject
                                        (CONSTANTS.SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER + UserAuthUtils.getUserId(context), RestMessageBean.class);
                            } catch (Exception e) {
                                ExceptionUtils.printExceptionToFile(e);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean deliverSelfNotifications() {
        MessageUtils.notification(mContext, "PhoneContactObserver", "deliverSelfNotifications", (int) System.currentTimeMillis(),
                null, null, false, null, true);
        MessageUtils.toast(mContext, "PhoneContactObserver - deliverSelfNotifications", false);
        return super.deliverSelfNotifications();
    }
}
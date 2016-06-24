package com.cmsys.linebacker.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.bean.RestResultAsteriskData;
import com.cmsys.linebacker.bean.RestResultLoginBean;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by cj on 17/12/15.
 */
public class UserAuthUtils {
    public static final String TYPE_CUSTOM = "custom";
    public static final String TYPE_FIREBASE_EMAIL = "firebase-email";
    public static final String TYPE_FIREBASE_GOOGLE = "firebase-google+";
    public static final String TYPE_GOOGLE = "google+";
    public static final String TYPE_INTERNAL_API = "internal-api";
    public static String AUTH_TYPE = TYPE_INTERNAL_API;

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
            case TYPE_INTERNAL_API:
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

    public static void logUserInInternalApi_old(final Context context, final HashMap<String, String> postDataParams) {
        // Connect to WebService
        new AsyncTask<Void, Void, RestMessageBean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected RestMessageBean doInBackground(Void... params) {
                RestMessageBean restMessageBean = null;
                try {
                    restMessageBean = RestfulUtils.readRestfulPostAndParseToObject(CONSTANTS.SYNC_WS_LOGIN_API, RestMessageBean.class, postDataParams);
                } catch (Exception e) {
                    ExceptionUtils.printExceptionToFile(e);
                    e.printStackTrace();
                    return null;
                }
                return restMessageBean;
            }

            @Override
            protected void onPostExecute(RestMessageBean restMessageBean) {
                super.onPostExecute(restMessageBean);
                if (restMessageBean != null) {
                    // Write WebService results con text fields
                    if (restMessageBean.getErrorId() != null && restMessageBean.getErrorId() <= 0) {
                        try {
                            //RestResultAsteriskData resultData = (RestResultAsteriskData) restMessageBean.getResultObject(); //new Gson().fromJson(restMessageBean.getResultObject(), RestResultAsteriskData.class);
                            RestResultLoginBean resultData = new Gson().fromJson(restMessageBean.getResultObject().toString(), RestResultLoginBean.class);
                            //
//                            mEtSignInOut.setText(resultData.getExtensionNumber());
//                            mEtPassword.setText(resultData.getExtensionPassword());
//                            if (!TextUtils.isEmpty(resultData.getExternalPhoneNumber())) {
//                                mEtExternalPhoneNr.setText(resultData.getExternalPhoneNumber());
//                                mBtVoiceMailConfigNumbers.setVisibility(View.VISIBLE);
//                            }
                            MessageUtils.toast(context, context.getString(R.string.get_extension_successful), true);
                        } catch (Exception e) {
                            ExceptionUtils.displayExceptionMessage(context, e);
                        }
                    } else {
                        MessageUtils.toast(context, "ERROR: " + restMessageBean.getErrorMessage(), true);
                    }
                }
            }
        }.execute();
    }

    public static RestMessageBean logUserInInternalApi(final Context context, final HashMap<String, String> postDataParams) {
        // Connect to WebService
        RestMessageBean restMessageBean = null;
        try {
            restMessageBean = RestfulUtils.readRestfulPostAndParseToObject(CONSTANTS.SYNC_WS_LOGIN_API, RestMessageBean.class, postDataParams);
            if (restMessageBean != null) {
                // Write WebService results con text fields
                if (restMessageBean.getErrorId() != null && restMessageBean.getErrorId() <= 0) {
                    try {
                        RestResultLoginBean resultData = new Gson().fromJson(restMessageBean.getResultObject().toString(), RestResultLoginBean.class);
                        if (resultData.getAccount() != null && !resultData.getAccount().equals("")) {
                            SharedPreferencesUtils.putOrEditString(context, context.getString(R.string.pref_key_user_id), resultData.getAccount());
                            //MessageUtils.toast(context, context.getString(R.string.get_extension_successful), true);

                        } else {
//                            restMessageBean.setErrorId(123);
//                            restMessageBean.setErrorMessage("API response error");
                            SharedPreferencesUtils.putOrEditString(context, context.getString(R.string.pref_key_user_id), "3dbf18b4-3f32-42de-bf55-cf103650fe01");
                        }
                        return restMessageBean;
                    } catch (Exception e) {
                        ExceptionUtils.displayExceptionMessage(context, e);
                    }
                } else {
                    //MessageUtils.toast(context, "ERROR: " + restMessageBean.getErrorMessage(), true);
                    return restMessageBean;
                }
            }
        } catch (Exception e) {
            ExceptionUtils.printExceptionToFile(e);
            e.printStackTrace();
        }
        return null;
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

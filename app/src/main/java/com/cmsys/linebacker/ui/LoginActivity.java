package com.cmsys.linebacker.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.bean.RestMessageCreatePbxBean;
import com.cmsys.linebacker.bean.RestMessageRegisterBean;
import com.cmsys.linebacker.bean.RestMessageValidateBean;
import com.cmsys.linebacker.bean.RestResultLoginBean;
import com.cmsys.linebacker.bean.SettingsBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.util.AppInfoUtils;
import com.cmsys.linebacker.util.AppLinkIndexing;
import com.cmsys.linebacker.util.AppPermissionsUtils;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.CallLogUtils;
import com.cmsys.linebacker.util.CheckInputDataUtils;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.RestfulUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;


import static android.Manifest.permission.READ_CONTACTS;
import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = makeLogTag(LoginActivity.class);
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Activity mActivity;
    private String mUserId;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailView.getText().toString().equals("root:delete_user")) {
                    ((Button) findViewById(R.id.delete_user)).setVisibility(View.VISIBLE);
                    MessageUtils.toast(getApplicationContext(), "DeleteUser button is now visible", false);
                } else
                    attemptLogin();
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button mPasswordRecovery = (Button) findViewById(R.id.password_recovery);
        mPasswordRecovery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPasswordRecovery();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Button mGiveAppPermissions = (Button) findViewById(R.id.give_app_permissions);
            mGiveAppPermissions.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptGivePermissions();
                }
            });
            mGiveAppPermissions.setVisibility(View.VISIBLE);
        }

        Button mDeleteUser = (Button) findViewById(R.id.delete_user);
        mDeleteUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptDeleteUser();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Load Ads banner
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Show version code and name
        Context c = getApplicationContext();
        MessageUtils.toast(c, "Version Name: " + AppInfoUtils.getAppVersionName(c) + "\nVersion Code: " + AppInfoUtils.getAppVersionCode(c), true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String validationUrl = AppLinkIndexing.checkIfIsLinkIndexing(getIntent());
        if (validationUrl != null) {
            MessageUtils.toast(this, "Validating account\nId: " + validationUrl, true);
            // Connect to Api
            new AsyncTask<Void, Void, RestMessageValidateBean>() {
                @Override
                protected RestMessageValidateBean doInBackground(Void... params) {
                    RestMessageValidateBean restMessageBean = null;
                    try {
                        restMessageBean = RestfulUtils.readRestfulAndParseToObject(validationUrl, RestMessageValidateBean.class);

                    } catch (Exception e) {
                        ExceptionUtils.printExceptionToFile(e);
                    }
                    return restMessageBean;
                }

                @Override
                protected void onPostExecute(RestMessageValidateBean restMessageBean) {
                    super.onPostExecute(restMessageBean);
                    if (restMessageBean != null && restMessageBean.isConfirmed()) {
                        MessageUtils.toast(getApplicationContext(), restMessageBean.getMessage(), false);
                    } else if (restMessageBean != null)
                        MessageUtils.toast(getApplicationContext(), restMessageBean.getMessage(), false);
                    else
                        MessageUtils.toast(getApplicationContext(), getString(R.string.error_api_connect), false);
                }
            }.execute();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        MessageUtils.toast(this, getString(R.string.message_click_back_again_to_exit), false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
        switch (requestCode) {
            case CONSTANTS.MY_PERMISSIONS_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MessageUtils.toast(getApplicationContext(), "permission granted", false);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    MessageUtils.toast(getApplicationContext(), "permission denied", false);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if (email.equals(password) && email.equals("development")){
            CONSTANTS.SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER = CONSTANTS.SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER_DEV;
            CONSTANTS.SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER = CONSTANTS.SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER_DEV;
            CONSTANTS.SYNC_WS_LOGIN_API = CONSTANTS.SYNC_WS_LOGIN_API_DEV;
            CONSTANTS.SYNC_WS_REGISTER_API = CONSTANTS.SYNC_WS_REGISTER_API_DEV;
            CONSTANTS.SYNC_WS_PBX_ACCOUNT_API = CONSTANTS.SYNC_WS_PBX_ACCOUNT_API_DEV;
            CONSTANTS.SYNC_WS_ASTERISk_REMOVE_LOG = CONSTANTS.SYNC_WS_ASTERISk_REMOVE_LOG_DEV;
            CONSTANTS.SYNC_WS_UPLOAD_AUDIO_API = CONSTANTS.SYNC_WS_UPLOAD_AUDIO_API_DEV;
            CONSTANTS.SYNC_WS_REPORT_CASE_API = CONSTANTS.SYNC_WS_REPORT_CASE_API_DEV;
            CONSTANTS.FIREBASE_APP_NAME = CONSTANTS.FIREBASE_APP_NAME_DEV;
            MessageUtils.toast(getApplicationContext(), "Mode change to DEVELOPMENT", true);
            return;
        } else if (email.equals(password) && email.equals("production")){
            CONSTANTS.SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER = CONSTANTS.SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER_PROD;
            CONSTANTS.SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER = CONSTANTS.SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER_PROD;
            CONSTANTS.SYNC_WS_LOGIN_API = CONSTANTS.SYNC_WS_LOGIN_API_PROD;
            CONSTANTS.SYNC_WS_REGISTER_API = CONSTANTS.SYNC_WS_REGISTER_API_PROD;
            CONSTANTS.SYNC_WS_PBX_ACCOUNT_API = CONSTANTS.SYNC_WS_PBX_ACCOUNT_API_PROD;
            CONSTANTS.SYNC_WS_ASTERISk_REMOVE_LOG = CONSTANTS.SYNC_WS_ASTERISk_REMOVE_LOG_PROD;
            CONSTANTS.SYNC_WS_UPLOAD_AUDIO_API = CONSTANTS.SYNC_WS_UPLOAD_AUDIO_API_PROD;
            CONSTANTS.SYNC_WS_REPORT_CASE_API = CONSTANTS.SYNC_WS_REPORT_CASE_API_PROD;
            CONSTANTS.FIREBASE_APP_NAME = CONSTANTS.FIREBASE_APP_NAME_PROD;
            MessageUtils.toast(getApplicationContext(), "Mode change to PRODUCTION", true);
            return;
        }

        //if (!AppPermissionsUtils.checkPermissionsAndRequest(this)){
        //    MessageUtils.toast(getApplicationContext(), "Please grant app permissions", true);
        //    return;
        //}

        //if(!AppPermissionsUtils.mayRequestContacts(this)) {
        //    MessageUtils.toast(getApplicationContext(), "Please grant app permissions", true);
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //        requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        //    }
        //    return;
        //}

        if(!AppPermissionsUtils.checkPermissionsAndRequest(this)) {
            MessageUtils.toast(getApplicationContext(), "Please grant app permissions", true);
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private void attemptCreatePbxAccount(final String serverId) {
        final MessageUtils mu = new MessageUtils(mActivity, getString(R.string.create_pbx_account), "", R.layout.activity_login_create_pbx_account, false);
        mu.setOnClickListenerAccept(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getApplicationContext();
                final EditText etFirstName = (EditText) mu.getConvertView().findViewById(R.id.etInputFirstName);
                final EditText etLastName = (EditText) mu.getConvertView().findViewById(R.id.etInputLastName);
                final EditText etBirthday = (EditText) mu.getConvertView().findViewById(R.id.etInputBirthday);
                final EditText etPhoneNumber = (EditText) mu.getConvertView().findViewById(R.id.etInputPhoneNumber);
                final EditText etPhoneNumber2 = (EditText) mu.getConvertView().findViewById(R.id.etInputPhoneNumber2);
                //final EditText etState = (EditText) mu.getConvertView().findViewById(R.id.etInputState);
                final EditText etCity = (EditText) mu.getConvertView().findViewById(R.id.etInputCity);
                final EditText etZipCode = (EditText) mu.getConvertView().findViewById(R.id.etInputZipCode);
                final EditText etAddress = (EditText) mu.getConvertView().findViewById(R.id.etInputAddress);
                //final EditText etEmail = (EditText) mu.getConvertView().findViewById(R.id.etInputEmail);
                //final EditText etPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputPassword);
                //final EditText etRepeatPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputRepeatPassword);
                //
                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(etFirstName);
                editTextList.add(etLastName);
                editTextList.add(etPhoneNumber);
                //editTextList.add(etPhoneNumber2);
                editTextList.add(etZipCode);
                editTextList.add(etCity);
                //editTextList.add(etAddress);
                //editTextList.add(etBirthday);
                //editText                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              List.add(etEmail); editTextList.add(etPassword); editTextList.add(etRepeatPassword); //editTextList.add(etState);
                //CheckInputDataUtils.fillAllFieldsSampleData(editTextList);
                // Check if text is filled
                if (CheckInputDataUtils.areAllFieldsFilled(editTextList)) {
                    MessageUtils.showProgressBarAndHideButtons(mu);
                    // Connect to Api
                    new AsyncTask<Void, Void, RestMessageCreatePbxBean>() {
                        HashMap<String, String> postDataParams;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            postDataParams = new HashMap<String, String>();
                            postDataParams.put("id", serverId);
                            postDataParams.put("id_membership", "1");
                            postDataParams.put("id_city", etZipCode.getText().toString());
                            postDataParams.put("city", etCity.getText().toString());
                            postDataParams.put("first_name", etFirstName.getText().toString());
                            postDataParams.put("last_name", etLastName.getText().toString());
                            //postDataParams.put("address", etAddress.getText().toString());
                            //postDataParams.put("birthday", etBirthday.getText().toString());
                            postDataParams.put("phone_number", etPhoneNumber.getText().toString());
                            //postDataParams.put("second_phone", etPhoneNumber2.getText().toString());
                        }

                        @Override
                        protected RestMessageCreatePbxBean doInBackground(Void... params) {
                            RestMessageCreatePbxBean restMessageBean = null;
                            try {
                                restMessageBean = RestfulUtils.readRestfulPostAndParseToObject(CONSTANTS.SYNC_WS_PBX_ACCOUNT_API, RestMessageCreatePbxBean.class, postDataParams);
                            } catch (Exception e) {
                                ExceptionUtils.printExceptionToFile(e);
                            }
                            return restMessageBean;
                        }

                        @Override
                        protected void onPostExecute(RestMessageCreatePbxBean restMessageBean) {
                            super.onPostExecute(restMessageBean);
                            if (restMessageBean != null && restMessageBean.getErrorId() == 0) {
                                if (restMessageBean.getErrorMessage() != null && restMessageBean.getErrorMessage().size() > 0)
                                    MessageUtils.toast(getApplicationContext(), restMessageBean.getErrorMessage().get(0), false);
                                else
                                    MessageUtils.toast(getApplicationContext(), getString(R.string.pbx_account_create_successful), false);
                                mu.cancel();
                            } else if (restMessageBean != null)
                                if (restMessageBean.getErrorMessage() != null && restMessageBean.getErrorMessage().size() > 0)
                                    MessageUtils.toast(getApplicationContext(), restMessageBean.getErrorMessage().get(0), false);
                                else
                                    MessageUtils.toast(getApplicationContext(), getString(R.string.error_api_connect), false);
                            MessageUtils.hideProgressBarAndShowAcceptButtons(mu);
                        }
                    }.execute();
                } else {
                    if (!CheckInputDataUtils.areAllFieldsFilled(editTextList))
                        MessageUtils.toast(context, getString(R.string.error_all_fields_required), false);
                }
            }
        });
        mu.setOnClickListenerCancel(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mu.cancel();
            }
        });
        mu.show();
    }

    private void attemptRegister() {
        final MessageUtils mu = new MessageUtils(mActivity, getString(R.string.register_new_user), "", R.layout.activity_login_register_new_user, false);
        mu.setOnClickListenerAccept(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getApplicationContext();
                final EditText etFirstName = (EditText) mu.getConvertView().findViewById(R.id.etInputFirstName);
                final EditText etLastName = (EditText) mu.getConvertView().findViewById(R.id.etInputLastName);
                //final EditText etPhoneNumber = (EditText) mu.getConvertView().findViewById(R.id.etInputPhoneNumber);
                //final EditText etState = (EditText) mu.getConvertView().findViewById(R.id.etInputState);
                //final EditText etCity = (EditText) mu.getConvertView().findViewById(R.id.etInputCity);
                //final EditText etZipCode = (EditText) mu.getConvertView().findViewById(R.id.etInputZipCode);
                //final EditText etAddress = (EditText) mu.getConvertView().findViewById(R.id.etInputAddress);
                final EditText etEmail = (EditText) mu.getConvertView().findViewById(R.id.etInputEmail);
                final EditText etPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputPassword);
                final EditText etRepeatPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputRepeatPassword);
                //
                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(etFirstName);
                editTextList.add(etLastName); //editTextList.add(etPhoneNumber);
                //editTextList.add(etZipCode); //editTextList.add(etState); editTextList.add(etCity); editTextList.add(etAddress);
                editTextList.add(etEmail);
                editTextList.add(etPassword);
                editTextList.add(etRepeatPassword);
                //CheckInputDataUtils.fillAllFieldsSampleData(editTextList);
                // Check if text is filled
                if (CheckInputDataUtils.areAllFieldsFilled(editTextList)
                        && etPassword.getText().toString().equals(etRepeatPassword.getText().toString())
                        && CheckInputDataUtils.isValidEmail(etEmail.getText())) {
                    MessageUtils.showProgressBarAndHideButtons(mu);
                    // Connect to Api
                    new AsyncTask<Void, Void, RestMessageRegisterBean>() {
                        HashMap<String, String> postDataParams;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            postDataParams = new HashMap<String, String>();
                            postDataParams.put("email", etEmail.getText().toString());
                            postDataParams.put("name", etFirstName.getText().toString() + " " + etLastName.getText().toString());
                            postDataParams.put("password", etPassword.getText().toString());
                            postDataParams.put("password_confirmation", etRepeatPassword.getText().toString());
                        }

                        @Override
                        protected RestMessageRegisterBean doInBackground(Void... params) {
                            RestMessageRegisterBean restMessageBean = null;
                            try {

                                restMessageBean = RestfulUtils.readRestfulPostAndParseToObject(CONSTANTS.SYNC_WS_REGISTER_API, RestMessageRegisterBean.class, postDataParams);
                            } catch (Exception e) {
                                ExceptionUtils.printExceptionToFile(e);
                            }
                            return restMessageBean;
                        }

                        @Override
                        protected void onPostExecute(RestMessageRegisterBean restMessageBean) {
                            super.onPostExecute(restMessageBean);
                            if (restMessageBean != null && restMessageBean.isCreated()) {
                                MessageUtils.toast(getApplicationContext(), restMessageBean.getMessage(), false);
                                mu.cancel();
                            } else if (restMessageBean != null)
                                if (restMessageBean.getErrors() != null && restMessageBean.getErrors().size() > 0)
                                    MessageUtils.toast(getApplicationContext(), restMessageBean.getErrors().get(0), false);
                                else
                                    MessageUtils.toast(getApplicationContext(), getString(R.string.error_api_connect), false);
                            MessageUtils.hideProgressBarAndShowAcceptButtons(mu);
                        }
                    }.execute();
                } else {
                    if (!CheckInputDataUtils.areAllFieldsFilled(editTextList))
                        MessageUtils.toast(context, getString(R.string.error_all_fields_required), false);
                    else if (!CheckInputDataUtils.isValidEmail(etEmail.getText()))
                        MessageUtils.toast(context, getString(R.string.error_invalid_email), false);
                    else if (!etPassword.getText().toString().equals(etRepeatPassword.getText().toString()))
                        MessageUtils.toast(context, getString(R.string.error_passwords_not_match), false);
                }
            }
        });
        mu.setOnClickListenerCancel(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mu.cancel();
            }
        });
        mu.show();
    }

    private void attemptRegister_oldUsingWebSite() {
        Uri uri = Uri.parse(getString(R.string.web_link_register)); // missing 'http://' will cause crash
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void attemptRegister_oldUsingFirebase() {
        final MessageUtils mu = new MessageUtils(mActivity, getString(R.string.register_new_user), "", R.layout.activity_login_register_new_user, false);
        mu.setOnClickListenerAccept(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getApplicationContext();
                final EditText etFirstName = (EditText) mu.getConvertView().findViewById(R.id.etInputFirstName);
                final EditText etLastName = (EditText) mu.getConvertView().findViewById(R.id.etInputLastName);
                final EditText etPhoneNumber = (EditText) mu.getConvertView().findViewById(R.id.etInputPhoneNumber);
                //final EditText etState = (EditText) mu.getConvertView().findViewById(R.id.etInputState);
                //final EditText etCity = (EditText) mu.getConvertView().findViewById(R.id.etInputCity);
                final EditText etZipCode = (EditText) mu.getConvertView().findViewById(R.id.etInputZipCode);
                //final EditText etAddress = (EditText) mu.getConvertView().findViewById(R.id.etInputAddress);
                final EditText etEmail = (EditText) mu.getConvertView().findViewById(R.id.etInputEmail);
                final EditText etPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputPassword);
                EditText etRepeatPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputRepeatPassword);
                //
                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(etFirstName); editTextList.add(etLastName); editTextList.add(etPhoneNumber);
                editTextList.add(etZipCode); //editTextList.add(etState); editTextList.add(etCity); editTextList.add(etAddress);
                editTextList.add(etEmail); editTextList.add(etPassword); editTextList.add(etRepeatPassword);
                //CheckInputDataUtils.fillAllFieldsSampleData(editTextList);
                // Check if text is filled
                if (CheckInputDataUtils.areAllFieldsFilled(editTextList)
                        && etPassword.getText().toString().equals(etRepeatPassword.getText().toString())
                        && CheckInputDataUtils.isValidEmail(etEmail.getText())) {
                    MessageUtils.showProgressBarAndHideButtons(mu);
                    // Connect to Firebase
                    Firebase.setAndroidContext(context);
                    final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                    // Check if zipCode exists
                    fbRef.child(CONSTANTS.FIREBASE_DOC_ZIPCODE + File.separator + etZipCode.getText())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        fbRef.createUser(etEmail.getText().toString(), etPassword.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                                            @Override
                                            public void onSuccess(Map<String, Object> result) {
                                                //System.out.println("Successfully created user account with uid: " + result.get("uid"));
                                                mUserId = (String) result.get("uid");
                                                MessageUtils.toast(context, getString(R.string.message_new_user_registered) + mUserId, false);
                                                MessageUtils.toast(context, "NOW CREATING USER SERVER DATA", false);
                                                UserBean userBean = new UserBean(mUserId, etFirstName.getText().toString(),
                                                        etLastName.getText().toString(), etPhoneNumber.getText().toString(),
                                                        //etState.getText().toString(), etCity.getText().toString(), etAddress.getText().toString(),
                                                        etZipCode.getText().toString(),
                                                        etEmail.getText().toString(), "0");

                                                Map<String, Object> firebaseTrans = new HashMap<String, Object>();
                                                firebaseTrans.put(CONSTANTS.FIREBASE_DOC_USER + File.separator + mUserId, userBean.getObjectMap());
                                                firebaseTrans.put(CONSTANTS.FIREBASE_DOC_SETTINGS + File.separator + mUserId, (new SettingsBean().setAllDefaults()).getObjectMap());
                                                // Add test recorded audios TODO remove next line at the end
                                                //firebaseTrans.put(CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId, RecordingBean.getTestRecordingsMap(mUserId));

                                                fbRef.updateChildren(firebaseTrans, new Firebase.CompletionListener() {
                                                    @Override
                                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                        if (firebaseError != null) {
                                                            MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                                        } else {
                                                            mEmailView.setText(etEmail.getText());
                                                            mPasswordView.setText("");
                                                            MessageUtils.toast(context, context.getString(R.string.message_firebase_data_created_successfully), false);
                                                            mu.cancel();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(FirebaseError firebaseError) {
                                                // there was an error
                                                MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), true);
                                                MessageUtils.hideProgressBarAndShowAcceptButtons(mu);
                                            }
                                        });
                                    } else {
                                        MessageUtils.toast(context, "ZIP CODE NOT FOUND, PLEASE INSERT A VALID ONE", true);
                                        MessageUtils.hideProgressBarAndShowAcceptButtons(mu);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                } else {
                    if(!CheckInputDataUtils.areAllFieldsFilled(editTextList))
                        MessageUtils.toast(context, getString(R.string.error_all_fields_required), false);
                    else if(!CheckInputDataUtils.isValidEmail(etEmail.getText()))
                        MessageUtils.toast(context, getString(R.string.error_invalid_email), false);
                    else if(!etPassword.getText().toString().equals(etRepeatPassword.getText().toString()))
                        MessageUtils.toast(context, getString(R.string.error_passwords_not_match), false);
                }
            }
        });
        mu.setOnClickListenerCancel(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mu.cancel();
            }
        });
        mu.show();
        // Try getting phone number from SIM card
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        if(!TextUtils.isEmpty(phoneNumber))
            ((EditText) mu.getConvertView().findViewById(R.id.etInputPhoneNumber)).setText(phoneNumber);
    }

    void attemptPasswordRecovery(){
        final MessageUtils mu = new MessageUtils(mActivity, getString(R.string.password_recovery), "", R.layout.activity_login_password_recovery, false);
        mu.setOnClickListenerAccept(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etEmail = (EditText) mu.getConvertView().findViewById(R.id.etInputEmail);
                EditText etReceivedEmailPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputReceivedEmailPassword);
                EditText etNewPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputNewPassword);
                EditText etRepeatNewPassword = (EditText) mu.getConvertView().findViewById(R.id.etInputRepeatNewPassword);
                //
                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(etEmail); editTextList.add(etReceivedEmailPassword);
                editTextList.add(etNewPassword); editTextList.add(etRepeatNewPassword);
                // Check if text is filled
                if (CheckInputDataUtils.areAllFieldsFilled(editTextList)
                        && etNewPassword.getText().toString().equals(etRepeatNewPassword.getText().toString())
                        && CheckInputDataUtils.isValidEmail(etEmail.getText())) {
                    // Show progress bar
                    mu.getProgressBar().setVisibility(View.VISIBLE);
                    mu.getBAccept().setVisibility(View.GONE);
                    mu.getBCancel().setVisibility(View.GONE);
                    // Firebase change user password
                    Firebase.setAndroidContext(getApplicationContext());
                    Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                    ref.changePassword(etEmail.getText().toString(), etReceivedEmailPassword.getText().toString(),
                            etNewPassword.getText().toString(), new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            // password changed
                            MessageUtils.toast(getApplicationContext(), getString(R.string.message_firebase_user_password_changed), false);
                            mu.cancel();
                            mEmailView.setText(etEmail.getText());
                            mPasswordView.setText("");
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // error encountered
                            MessageUtils.toast(getApplicationContext(), getApplicationContext().getString(R.string.error_firebase_connect) + firebaseError.getMessage(), true);
                            mu.getProgressBar().setVisibility(View.GONE);
                            mu.getBAccept().setVisibility(View.VISIBLE);
                            mu.getBCancel().setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    if(!CheckInputDataUtils.areAllFieldsFilled(editTextList))
                        MessageUtils.toast(getApplicationContext(), getString(R.string.error_all_fields_required), false);
                    else if(!CheckInputDataUtils.isValidEmail(etEmail.getText()))
                        MessageUtils.toast(getApplicationContext(), getString(R.string.error_invalid_email), false);
                    else if(!etNewPassword.getText().toString().equals(etRepeatNewPassword.getText().toString()))
                        MessageUtils.toast(getApplicationContext(), getString(R.string.error_passwords_not_match), false);
                }
            }
        });
        mu.setOnClickListenerCancel(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mu.cancel();
            }
        });
        TextView tvSendNewEmailPassword = (TextView) mu.getConvertView().findViewById(R.id.tvSendNewEmailPassword);
        tvSendNewEmailPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final MessageUtils mu2 = new MessageUtils(mActivity, getString(R.string.send_new_password), getString(R.string.are_you_sure), 0, false);
                mu2.setOnClickListenerYes(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText etEmail = (EditText) mu.getConvertView().findViewById(R.id.etInputEmail);
                        if (CheckInputDataUtils.isValidEmail(etEmail.getText())) {
                            mu2.cancel();
                            // Show progress bar
                            mu.getProgressBar().setVisibility(View.VISIBLE);
                            mu.getBAccept().setVisibility(View.GONE);
                            mu.getBCancel().setVisibility(View.GONE);
                            // Send Firebase Password Reset Email
                            Firebase.setAndroidContext(getApplicationContext());
                            Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                            ref.resetPassword(etEmail.getText().toString(), new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    // password reset email sent
                                    MessageUtils.toast(getApplicationContext(), getApplicationContext().getString(R.string.message_firebase_email_password_sent), false);
                                    mu.getProgressBar().setVisibility(View.GONE);
                                    mu.getBAccept().setVisibility(View.VISIBLE);
                                    mu.getBCancel().setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    // error encountered
                                    MessageUtils.toast(getApplicationContext(), getApplicationContext().getString(R.string.error_firebase_connect) + firebaseError.getMessage(), true);
                                    mu.getProgressBar().setVisibility(View.GONE);
                                    mu.getBAccept().setVisibility(View.VISIBLE);
                                    mu.getBCancel().setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            MessageUtils.toast(getApplicationContext(), getString(R.string.error_invalid_email), false);
                            mu2.cancel();
                        }
                    }
                });
                mu2.setOnClickListenerNo(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mu2.cancel();
                    }
                });
                mu2.getBAccept().setVisibility(View.GONE);
                mu2.show();
            }
        });
        mu.show();
    }

    void attemptDeleteUser(){
        if(!TextUtils.isEmpty(mEmailView.getText()) && !TextUtils.isEmpty(mPasswordView.getText())){
            // Setup Firebase connection
            Firebase.setAndroidContext(getApplicationContext());
            final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
            // Get user unique ID
            Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // Authenticated successfully with payload authData
                    //MessageUtils.toast(getApplicationContext(), "UserID: " + authData.getUid(), false);
                    mUserId = authData.getUid();
                    // Remove user data on Firebase
                    Map<String, Object> firebaseTrans = new HashMap<String, Object>();
                    firebaseTrans.put(CONSTANTS.FIREBASE_DOC_USER + File.separator + mUserId, null);
                    firebaseTrans.put(CONSTANTS.FIREBASE_DOC_SETTINGS + File.separator + mUserId, null);
                    firebaseTrans.put(CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId, null);
                    firebaseTrans.put(CONSTANTS.FIREBASE_DOC_CASES + File.separator + mUserId, null);
                    firebaseTrans.put(CONSTANTS.FIREBASE_DOC_CONTACTS + File.separator + mUserId, null);
                    fbRef.updateChildren(firebaseTrans, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                MessageUtils.toast(getApplicationContext(), firebaseError.getMessage(), false);
                            } else {
                                MessageUtils.toast(getApplicationContext(), getString(R.string.message_firebase_data_removed_successfully), false);
                                // Remove Firebase user
                                fbRef.removeUser(mEmailView.getText().toString(), mPasswordView.getText().toString(),
                                        new Firebase.ResultHandler() {
                                            @Override
                                            public void onSuccess() {
                                                // user removed
                                                MessageUtils.toast(getApplicationContext(), getString(R.string.message_firebase_user_removed), true);
                                            }

                                            @Override
                                            public void onError(FirebaseError firebaseError) {
                                                // error encountered
                                                MessageUtils.toast(getApplicationContext(), getString(R.string.error_firebase_connect) + firebaseError.getMessage(), true);
                                            }
                                        });
                            }
                        }
                    });
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // Authenticated failed with error firebaseError
                    MessageUtils.toast(getApplicationContext(), firebaseError.getMessage(), true);
                }
            };
            fbRef.authWithPassword(mEmailView.getText().toString(), mPasswordView.getText().toString(), authResultHandler);
        } else {
            MessageUtils.toast(getApplicationContext(), getString(R.string.error_all_fields_required), false);
        }
    }

    void attemptGivePermissions(){
        final MessageUtils msg = new MessageUtils(this,"Give permissions", "In the following screen, go to " +
                "\"Permissions\" and accept all the items.\nLater press back to return to Linebacker.", 0, false);
        msg.getBAccept().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                msg.dismiss();
            }
        });
        msg.getBCancel().setVisibility(View.VISIBLE);
        msg.show();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true; //password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        private final String mEmail;
        private final String mPassword;
        private boolean mUserExists;
        private String mUserId;

        UserLoginTask(String email, String password, Context pContext) {
            mEmail = email;
            mPassword = password;
            mContext = pContext;
            mUserExists = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAuthTask = null;
            /*if(UserAuthUtils.AUTH_TYPE == UserAuthUtils.TYPE_CUSTOM) {
                // Firebase access
                Firebase.setAndroidContext(mContext);
                Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_USER);
                // Reading Data Once
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(mEmail)) {
                            mUserExists = true;
                            mUserId = mEmail;
                            SharedPreferencesUtils.putOrEditString(mContext, getString(R.string.pref_key_user_id), mUserId);
                            finish();
                        } else {
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                        showProgress(false);
                    }

                    @Override public void onCancelled(FirebaseError firebaseError) {}
                });
            }*/
            //
            if (UserAuthUtils.AUTH_TYPE == UserAuthUtils.TYPE_INTERNAL_API) {
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("email", mEmail);
                postDataParams.put("password", mPassword);
                // Try login
                final RestMessageBean loginMsg = UserAuthUtils.logUserInInternalApi(mContext, postDataParams);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loginMsg != null && loginMsg.getErrorId() == 0) {
                            Intent intent = new Intent(getApplicationContext(), RecordingLogActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            MessageUtils.toast(mContext, getString(R.string.login_successful), true);
                        } else if (loginMsg != null) {
                            if (loginMsg.getErrorId() == UserAuthUtils.ERROR_NOT_PBX_ACCOUNT) {
                                MessageUtils.toast(getApplicationContext(), getString(R.string.error_should_create_pbx_account), true);
                                if(loginMsg.getResultObject() != null) {
                                    RestResultLoginBean resultData = new Gson().fromJson(loginMsg.getResultObject().toString(), RestResultLoginBean.class);
                                    if (resultData.getId() != null && !resultData.getId().equals(""))
                                        attemptCreatePbxAccount(resultData.getId());
                                }
                            } else
                                MessageUtils.toast(mContext, loginMsg.getErrorMessage(), true);
                        } else {
                            MessageUtils.toast(mContext, getString(R.string.login_error), true);
                        }
                        showProgress(false);
                    }
                });
            }
            //
            if(UserAuthUtils.AUTH_TYPE == UserAuthUtils.TYPE_FIREBASE_EMAIL) {
                // Firebase access
                Firebase.setAndroidContext(mContext);
                Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                // Create a handler to handle the result of the authentication
                Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // Authenticated successfully with payload authData
                        //MessageUtils.toast(getApplicationContext(), "UserID: " + authData.getUid(), false);
                        Intent intent = new Intent(getApplicationContext(), RecordingLogActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        showProgress(false);
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // Authenticated failed with error firebaseError
                        if (firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST){
                            MessageUtils.toast(getApplicationContext(), getApplicationContext().getString(R.string.error_user_does_not_exist), false);
                        } else if (firebaseError.getCode() == FirebaseError.INVALID_PASSWORD){
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        } else {
                            MessageUtils.toast(getApplicationContext(), firebaseError.getMessage(), true);
                        }
                        //MessageUtils.toast(getApplicationContext(), firebaseError.getMessage(), false);
                        showProgress(false);
                    }
                };
                //
                // Or with an email/password combination
                ref.authWithPassword(mEmail, mPassword, authResultHandler);
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


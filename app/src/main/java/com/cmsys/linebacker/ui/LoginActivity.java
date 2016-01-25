package com.cmsys.linebacker.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
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
import com.cmsys.linebacker.bean.SettingsBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.CheckInputDataUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

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

        Button mDeleteUser = (Button) findViewById(R.id.delete_user);
        mDeleteUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptDeleteUser();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

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

    private void attemptRegister() {
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
                                                // Add test recorded audios TODO remove this at the end
                                                firebaseTrans.put(CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS + File.separator + mUserId, RecordingBean.getTestRecordingsMap(mUserId));
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


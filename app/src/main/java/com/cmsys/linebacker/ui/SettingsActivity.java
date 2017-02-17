package com.cmsys.linebacker.ui;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.PhoneCompanyBean;
import com.cmsys.linebacker.bean.SettingsBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.util.AudioUtils;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.HashMapUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.util.StringUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.cmsys.linebacker.voip_doubango.DoubangoUtils;
import com.cmsys.linebacker.voip_doubango.SipDoubangoActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.doubango.ngn.events.NgnRegistrationEventArgs;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static String mUserId;
    private static SettingsBean mSettings;
    private static UserBean mUserBean;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        // Get user id
        mUserId = UserAuthUtils.getUserId(this);
        if(mUserId == null){
            finish();
        }
        // Get user info from bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && mUserBean == null){
            mUserBean = (UserBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_USER);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || VoicemailPreferenceFragment.class.getName().equals(fragmentName);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            ///bindPreferenceSummaryToValue(findPreference("example_text"));
            ///bindPreferenceSummaryToValue(findPreference("example_list"));
            //
            //
            //
//            final ProgressDialog progressDialog = ProgressDialog.show(this.getActivity().getApplicationContext(), "", "Cargando, por favor espere...", true);
            final Context context = this.getActivity().getApplicationContext();
            final ProgressDialog mProgressDialog;
            mProgressDialog = new ProgressDialog(this.getActivity());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            // Get settings from Firebase
            Firebase.setAndroidContext(context);
            final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_SETTINGS + File.separator + mUserId);
            fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mSettings = (SettingsBean) dataSnapshot.getValue(SettingsBean.class);
                        SharedPreferencesUtils.putOrEditString(context, getString(R.string.pref_key_setting_block_calls), mSettings.isBlockCalls());
                        SharedPreferencesUtils.putOrEditString(context, getString(R.string.pref_key_setting_mobile_notification), mSettings.isMobileNotification());
                        SharedPreferencesUtils.putOrEditString(context, getString(R.string.pref_key_setting_email_notification), mSettings.isEmailNotification());
                        SharedPreferencesUtils.putOrEditString(context, getString(R.string.pref_key_setting_delete_weeks), mSettings.getDeleteAudiosEveryWeeks());
                        //
                        addPreferencesFromResource(R.xml.pref_general);
                        CheckBoxPreference blockCalls = (CheckBoxPreference) findPreference(getString(R.string.pref_key_setting_block_calls));
                        CheckBoxPreference mobileNotification = (CheckBoxPreference) findPreference(getString(R.string.pref_key_setting_mobile_notification));
                        CheckBoxPreference emailNotification = (CheckBoxPreference) findPreference(getString(R.string.pref_key_setting_email_notification));
                        ListPreference deleteAudiosEveryWeeks = (ListPreference) findPreference(getString(R.string.pref_key_setting_delete_weeks));
                        deleteAudiosEveryWeeks.setSummary(mSettings.getDeleteAudiosEveryWeeks() + " week" + (Integer.parseInt(mSettings.getDeleteAudiosEveryWeeks()) > 1 ? "s" : ""));
                        //
                        Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
                            @Override
                            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                                fbRef.child(preference.getKey()).setValue(newValue, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError != null) {
                                            MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                        } else {
                                            if (preference instanceof SwitchPreference) {
                                                SwitchPreference switchPref = (SwitchPreference) preference;
                                                switchPref.setChecked((Boolean) newValue);
                                            }else if (preference instanceof CheckBoxPreference) {
                                                CheckBoxPreference checkBoxPref = (CheckBoxPreference) preference;
                                                checkBoxPref.setChecked((Boolean) newValue);
                                            }else if (preference instanceof ListPreference) {
                                                ListPreference listPref = (ListPreference) preference;
                                                listPref.setValue((String) newValue);
                                                listPref.setSummary(listPref.getEntry());
                                            }
                                        }
                                    }
                                });
                                return false;
                            }
                        };
                        //
                        blockCalls.setOnPreferenceChangeListener(onPreferenceChangeListener);
                        mobileNotification.setOnPreferenceChangeListener(onPreferenceChangeListener);
                        emailNotification.setOnPreferenceChangeListener(onPreferenceChangeListener);
                        deleteAudiosEveryWeeks.setOnPreferenceChangeListener(onPreferenceChangeListener);
                        //
                        mProgressDialog.dismiss();
                    }
                }
                @Override public void onCancelled(FirebaseError firebaseError) {}
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static void tryPbxAutoSignIn(DoubangoUtils mDoubango, final Preference preference, Activity activity){
        mDoubango.serverSignIn(mUserBean.getAsteriskExtension(), mUserBean.getAsteriskExtensionPass());
        MessageUtils.toast(preference.getContext(), "Trying again auto sign in", false);

        // Subscribe for registration state changes
        final DoubangoUtils finalMDoubango = mDoubango;
        BroadcastReceiver mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                // Registration Event
                if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                    if (finalMDoubango.isSipServiceRegistered()) {
                        MessageUtils.toast(preference.getContext(), "PBX auto-login successful", true);
                    } else {
                        MessageUtils.toast(preference.getContext(), "PBX service unregistered", true);
                    }
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        activity.registerReceiver(mSipBroadCastRecv, intentFilter);
        // Check again after 5 seconds
        Handler handler = new Handler();
        final DoubangoUtils finalMDoubango1 = mDoubango;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finalMDoubango1.isSipServiceRegistered()) {
                    MessageUtils.toast(preference.getContext(), "PBX +5secs check successful", true);
                } else {
                    MessageUtils.toast(preference.getContext(), "PBX +5secs check unregistered", true);
                }
            }
        }, 5000);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class VoicemailPreferenceFragment extends PreferenceFragment {
        //private static UserBean mUserBean;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_voicemail);

            //// Get user info from bundle
            //Bundle bundle = getArguments();
            //if(bundle != null && mUserBean == null){
            //    mUserBean = (UserBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_USER);
            //}

//            Preference buttonSignIn = (Preference) findPreference(getString(R.string.pref_key_setting_voicemail_signin));
//            buttonSignIn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    //code for what you want it to do
//                    MessageUtils.toast(preference.getContext(), "Not working yet", true);
//                    return true;
//                }
//            });

            Preference buttonSetup = (Preference) findPreference(getString(R.string.pref_key_setting_voicemail_setup));
            buttonSetup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DoubangoUtils mDoubango;
                    mDoubango = new DoubangoUtils(preference.getContext());
                    mDoubango.Init();
                    if (mDoubango.isSipServiceRegistered()) {
                        mDoubango.makeVoiceCall("*97", new Pair<String, String>(CONSTANTS.BUNDLE_EXTRA_CALLING_ACTIVITY, SettingsActivity.class.getName()), true);
                    } else {
                        MessageUtils.toast(preference.getContext(), "Not signed in, go to registration status", true);
                    }
                    return true;
                }
            });

            Preference buttonConfirm = (Preference) findPreference(getString(R.string.pref_key_setting_voicemail_confirm));
            buttonConfirm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Context context = preference.getContext();
                    final String externalPhoneNumber = SharedPreferencesUtils.getString(context, getString(R.string.pref_key_voip_did), "");
                    final MessageUtils mu = new MessageUtils(getActivity(), context.getString(R.string.confirm_activation), context.getString(R.string.confirm_activation_msg), 0, false);
                    mu.getBAccept().setVisibility(View.VISIBLE);
                    mu.getBCancel().setVisibility(View.VISIBLE);
                    mu.show();
                    mu.getBAccept().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PhoneCallUtils.dialNumber(context, externalPhoneNumber);
                            mu.dismiss();
                        }
                    });
                    return true;
                }
            });

            Preference buttonRecord = (Preference) findPreference(getString(R.string.pref_key_setting_voicemail_record));
            buttonRecord.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(preference.getContext(), AudioRecordActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference buttonCheckConnection = (Preference) findPreference(getString(R.string.pref_key_setting_check_voicemail_connection));
            buttonCheckConnection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    DoubangoUtils mDoubango;
                    mDoubango = new DoubangoUtils(preference.getContext());
                    mDoubango.Init();
                    if (mDoubango.isSipServiceRegistered()) {
                        MessageUtils.toast(preference.getContext(), "Already connected to pbx", true);
                    }
                    else {
                        MessageUtils.toast(preference.getContext(), "Not signed in, trying to reconnect", true);
                    }
                    //
                    // Try pbx auto sign-in
                    if (mDoubango == null)
                        mDoubango = new DoubangoUtils(preference.getContext());
                    if(!mDoubango.Init()){
                        MessageUtils.toast(preference.getContext(), "Problem initiating Doubango services", false);
                    } else{
                        // Starts the engine
                        if (!mDoubango.getEngine().isStarted()) {
                            if (mDoubango.getEngine().start()) {
                                MessageUtils.toast(preference.getContext(), "Engine started", false);
                                if(mUserBean != null && mUserBean.getAsteriskExtension() != null) {
                                    tryPbxAutoSignIn(mDoubango, preference, getActivity());
                                }
                            } else {
                                MessageUtils.toast(preference.getContext(), "Failed to start the engine", false);
                            }
                        } else{
                            MessageUtils.toast(preference.getContext(), "Engine was already started\nTrying to signin again", false);
                            if(mUserBean != null && mUserBean.getAsteriskExtension() != null)
                                tryPbxAutoSignIn(mDoubango, preference, getActivity());
                            else
                                MessageUtils.toast(preference.getContext(), "User info not available", false);
                        }
                    }
                    //
                    return true;
                }
            });

            Preference buttonActivate = (Preference) findPreference(getString(R.string.pref_key_setting_voicemail_activate));
            buttonActivate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
//                    rlExtensionData.setVisibility(View.GONE);
                    final Context context = preference.getContext();
                    final EditText mEtExternalPhoneNr = new EditText(context);
                    mEtExternalPhoneNr.setText(SharedPreferencesUtils.getString(context, getString(R.string.pref_key_voip_did), ""));
//                    Activity activity = getParent();
                    final MessageUtils mu = new MessageUtils(getActivity(), context.getString(R.string.phone_company), null, 0, false);
                    mu.getBAccept().setVisibility(View.GONE);
                    mu.getBCancel().setVisibility(View.VISIBLE);
                    mu.show();
                    // Start loading phone companies list
                    mu.getProgressBar().setVisibility(View.VISIBLE);
                    // Reading Data Once
                    Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_PHONECOMPANY);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                mu.getProgressBar().setVisibility(View.GONE);
                                mu.getListView().setVisibility(View.VISIBLE);

                                //List<PhoneCompanyBean> phoneCompanies = (List<PhoneCompanyBean>) dataSnapshot.getValue();
                                //HashMap<String, Object> users = (HashMap<String, Object>) snapshot.getValue();
                                //final HashMap<String, PhoneCompanyBean> phoneCompanies = (HashMap<String, PhoneCompanyBean>) dataSnapshot.getValue(PhoneCompanyBean.class);

                                final HashMap<String, PhoneCompanyBean> phoneCompanies = new HashMap<String, PhoneCompanyBean>();
                                final HashMap<String, String> phoneCompanyNames = new HashMap<String, String>();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    PhoneCompanyBean phoneCompanyBean = (PhoneCompanyBean) child.getValue(PhoneCompanyBean.class);
                                    phoneCompanyNames.put(phoneCompanyBean.getCompanyName(), child.getKey());
                                    phoneCompanies.put(child.getKey(), phoneCompanyBean);
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                        //android.R.layout.simple_list_item_1,
                                        //(new TextView(getApplicationContext())),
                                        R.layout.util_cust_dialog_listview_item,
                                        //R.layout.activity_case_details_comments_item,
                                        R.id.textView,
                                        HashMapUtils.getIdsStrings(phoneCompanyNames));
                                // Assign adapter to ListView
                                mu.getListView().setAdapter(adapter);
                                mu.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //HashMap<String, String> companyName = (HashMap<String, String>) mu.getListView().getItemAtPosition(position);
                                        String companyName = (String) mu.getListView().getItemAtPosition(position);
                                        String companyId = phoneCompanyNames.get(companyName);
                                        PhoneCompanyBean phoneCompanyBean = phoneCompanies.get(companyId);
                                        mu.cancel();
                                        // Show buttons to call
                                        final MessageUtils mu = new MessageUtils(getActivity(), context.getString(R.string.phone_company_options), null, R.layout.activity_settings_activate_company_numbers, false);
                                        mu.getBAccept().setVisibility(View.GONE);
                                        mu.getBCancel().setVisibility(View.VISIBLE);
                                        mu.show();
                                        //
                                        // Show CallingNumbers LinearLayout and set title
//                                        llCallingNumbers.setVisibility(View.VISIBLE);
                                        View convertView = mu.getConvertView();
                                        LinearLayout llCallingNumbers = (LinearLayout) convertView.findViewById(R.id.llCallingNumbers);
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvCompanyName)).setText(phoneCompanyBean.getCompanyName());

                                        // Set every Activate or Deactivate TextViews and hide it if necessary
                                        String number1 = StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationPrefix())
                                                + mEtExternalPhoneNr.getText()
                                                + StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationSuffix());
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvActivate1)).setText(number1);

                                        if (!TextUtils.isEmpty(phoneCompanyBean.getActivationPrefix()))
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate1)).setVisibility(View.VISIBLE);
                                        else
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate1)).setVisibility(View.GONE);
                                        //
                                        String number2 = StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationPrefix2())
                                                + mEtExternalPhoneNr.getText();
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvActivate2)).setText(number2);

                                        if (!TextUtils.isEmpty(phoneCompanyBean.getActivationPrefix2()))
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate2)).setVisibility(View.VISIBLE);
                                        else
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate2)).setVisibility(View.GONE);
                                        //
                                        String number3 = StringUtils.returnEmptyIfNull(phoneCompanyBean.getActivationPrefix3())
                                                + mEtExternalPhoneNr.getText();
                                        ((TextView) llCallingNumbers.findViewById(R.id.tvActivate3)).setText(number3);

                                        if (!TextUtils.isEmpty(phoneCompanyBean.getActivationPrefix3()))
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate3)).setVisibility(View.VISIBLE);
                                        else
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llActivate3)).setVisibility(View.GONE);
                                        //
                                        if (!TextUtils.isEmpty(phoneCompanyBean.getDeactivationNumber())) {
                                            ((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate1)).setText(phoneCompanyBean.getDeactivationNumber());
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate1)).setVisibility(View.VISIBLE);
                                        } else
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate1)).setVisibility(View.GONE);
                                        //
                                        if (!TextUtils.isEmpty(phoneCompanyBean.getDeactivationNumber2())) {
                                            ((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate2)).setText(phoneCompanyBean.getDeactivationNumber2());
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate2)).setVisibility(View.VISIBLE);
                                        } else
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llDeactivate2)).setVisibility(View.GONE);
                                        //
                                        if (!TextUtils.isEmpty(phoneCompanyBean.getSpecialInstructions())) {
                                            ((TextView) llCallingNumbers.findViewById(R.id.tvSpecialInstructions)).setText(phoneCompanyBean.getSpecialInstructions());
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llSpecialInstructions)).setVisibility(View.VISIBLE);
                                        } else
                                            ((LinearLayout) llCallingNumbers.findViewById(R.id.llSpecialInstructions)).setVisibility(View.GONE);

                                        // Setup Calling Buttons
                                        ((Button) llCallingNumbers.findViewById(R.id.btActivate1)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvActivate1)).getText().toString());
                                        ((Button) llCallingNumbers.findViewById(R.id.btActivate2)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvActivate2)).getText().toString());
                                        ((Button) llCallingNumbers.findViewById(R.id.btActivate3)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvActivate3)).getText().toString());
                                        ((Button) llCallingNumbers.findViewById(R.id.btDeactivate1)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate1)).getText().toString());
                                        ((Button) llCallingNumbers.findViewById(R.id.btDeactivate2)).setTag(((TextView) llCallingNumbers.findViewById(R.id.tvDeactivate2)).getText().toString());
                                        //
                                        View.OnClickListener onClickListener = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                PhoneCallUtils.dialNumber(v.getContext(), v.getTag().toString());
                                                mu.dismiss();
                                            }
                                        };
                                        //
                                        ((Button) llCallingNumbers.findViewById(R.id.btActivate1)).setOnClickListener(onClickListener);
                                        ((Button) llCallingNumbers.findViewById(R.id.btActivate2)).setOnClickListener(onClickListener);
                                        ((Button) llCallingNumbers.findViewById(R.id.btActivate3)).setOnClickListener(onClickListener);
                                        ((Button) llCallingNumbers.findViewById(R.id.btDeactivate1)).setOnClickListener(onClickListener);
                                        ((Button) llCallingNumbers.findViewById(R.id.btDeactivate2)).setOnClickListener(onClickListener);
                                    }
                                });

                            } else {
                                MessageUtils.toast(context, getString(R.string.error_no_info_to_show), true);
                                mu.cancel();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    return true;
                }
            });

//            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

package com.cmsys.linebacker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.adapter.RecordingAdapter;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.gcm.GcmRegistrationAsyncTask;
import com.cmsys.linebacker.io.DataIO;
import com.cmsys.linebacker.util.AppInitialSetupUtils;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.GcmUtils;
import com.cmsys.linebacker.util.IntentUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.cmsys.linebacker.util.ViewUtils;
import com.cmsys.linebacker.voip_doubango.SipDoubangoActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

public class RecordingLogActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = makeLogTag(RecordingLogActivity.class);
    private static final int REQUEST_CODE = 0;
    private static final int PICK_CONTACT = 1;
    private NavigationView navigationView;
    private UserBean mUserBean;
    private String mUserId;
    private ListView listView;
    private RecordingAdapter mRecordingAdapter;
    private ProgressBar progressBar;
    private boolean doubleBackToExitPressedOnce = false;
    private MenuItem mSearchAction;
    private Boolean isSearchOpened = false;
    private boolean isFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        // https://developers.facebook.com/docs/android/getting-started
        FacebookSdk.sdkInitialize(getApplicationContext());
        //IntentUtils.generateFacebookKeyHash(this);
        //
        setContentView(R.layout.activity_recording_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_activity_recording_log));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initial App Setup -----------------------------------------------------------------------
        AppInitialSetupUtils.createAppFolders();
        // Activity Views Setup --------------------------------------------------------------------
        setupViews();
        // Check if user is logged in --------------------------------------------------------------
        mUserId = UserAuthUtils.getUserId(this);
        // Register device on Google Cloud Messaging backend
        // Check device for Play Services APK. If check succeeds, proceed
        // with GCM registration.
        if (true//GcmUtils.checkPlayServices(this)
                && mUserId != null) {
            String regId = GcmUtils.getRegistrationId(this);
            if (regId.isEmpty()) {
                Log.i(TAG, "Not registered with GCM.");

                // Register GCM id in the background
                new GcmRegistrationAsyncTask(this, mUserId).execute();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isSearchOpened || isFiltered){
            isSearchOpened = ViewUtils.handleMenuSearch(this, mRecordingAdapter, true, mSearchAction);
            if(isFiltered){
                resetNavigationDrawerFilters();
            }
        }
        // Check if user is logged in --------------------------------------------------------------
        if(mUserId != null){
            // Get Firebase settings if SharedPreference doesn't exists
            if(!SharedPreferencesUtils.checkIfContainsKey(this, getString(R.string.pref_key_setting_block_calls))){
                DataIO.getFirebaseSettings(this, mUserId);
            }
            if(mUserBean == null){
                // Access Firebase user data
                Firebase ref = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_USER + File.separator + mUserId);
                // Reading Data Once
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {
                                mUserBean = (UserBean) snapshot.getValue(UserBean.class);
                                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderTitle))
                                        .setText(mUserBean.getFirstName() + " " + mUserBean.getLastName());
                                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderText1))
                                        .setText(mUserBean.getPhoneNumber());
                                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderText2))
                                        .setText(mUserBean.getUserLevel() == 0 ? "Free Account" : "Premium Account");
                            } catch (Exception e) {
                                ExceptionUtils.displayExceptionMessage(getApplicationContext(), e);
                            }
                        } else {
                            MessageUtils.toast(getApplicationContext(), "USER DATA IS NOT CREATED\n" +
                                    "PLEASE WRITE US AN EMAIL", true);
                        }
                    }

                    @Override public void onCancelled(FirebaseError firebaseError) {}
                });
            }
            getDataFromFirebase();
        } else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void setupViews() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mUserBean != null) {
                    RecordingBean recording = (RecordingBean) listView.getItemAtPosition(position);
                    Intent intent = new Intent(view.getContext(), RecordingDetailsActivity.class);
                    intent.putExtra(CONSTANTS.BUNDLE_EXTRA_RECORDING, recording);
                    intent.putExtra(CONSTANTS.BUNDLE_EXTRA_USER, mUserBean);
                    startActivity(intent);
                } else {
                    MessageUtils.toast(getApplicationContext(), getString(R.string.wait_still_loading), false);
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void resetNavigationDrawerFilters() {
        navigationView.getMenu().findItem(R.id.nav_show_all).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_contacts_only).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_blocked_only).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_in_case_only).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_not_in_case_only).setChecked(false);
    }

    private void getDataFromFirebase(){
        // Firebase Setup --------------------------------------------------------------------------
        //
        // Create a new Adapter
        mRecordingAdapter = new RecordingAdapter(this, new ArrayList<RecordingBean>());
        // Assign adapter to ListView
        listView.setAdapter(mRecordingAdapter);
        // Set Firebase Context.
        Firebase.setAndroidContext(this);
        // Get Firebase Reference
        Firebase firebaseRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_RECORDED_AUDIOS
                + File.separator + mUserId);
        //Firebase ref = new Firebase("https://docs-examples.firebaseio.com/web/saving-data/fireblog/posts");
        firebaseRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                //adapter.add((String) dataSnapshot.child("AudioId").getValue());
                //System.out.println(snapshot.getValue());
                RecordingBean newRecording = snapshot.getValue(RecordingBean.class);
                newRecording.setKey(snapshot.getKey());
                mRecordingAdapter.add(newRecording);
                ViewUtils.hideProgressBar(progressBar);
                resetNavigationDrawerFilters();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //adapter.remove((String) dataSnapshot.child("AudioId").getValue());
                mRecordingAdapter.remove(dataSnapshot.getValue(RecordingBean.class));
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(FirebaseError firebaseError) {}
        });
        //
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    ViewUtils.hideProgressBar(progressBar);
                    MessageUtils.toast(getApplicationContext(), getApplicationContext().getString(R.string.no_recordings), false);
                }
            }
            @Override public void onCancelled(FirebaseError error) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        MessageUtils.toast(getApplicationContext(), name, false);
                    }
                }
                break;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Default created methods for back and menu buttons -------------------------------------------
    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording_log, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            resetNavigationDrawerFilters();
            if(progressBar.getVisibility() == View.VISIBLE) {
                MessageUtils.toast(getApplicationContext(), getString(R.string.wait_still_loading), false);
            } else {
                //mSearchAction = item;
                isSearchOpened = ViewUtils.handleMenuSearch(this, mRecordingAdapter, isSearchOpened, mSearchAction);
            }
        }
        if (id == R.id.action_settings) {
            //MessageUtils mu = new MessageUtils(this, "Settings", "Go to settings activity", 0, true);
            //MessageUtils.toast(this, "Go to settings activity...", false);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_upload_contacts) {
            final MessageUtils mu = new MessageUtils(this, getString(R.string.action_upload_contacts), getString(R.string.are_you_sure), 0, false);
            mu.setOnClickListenerYes(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    mu.getProgressBarHorizontal().setVisibility(View.VISIBLE);
                    mu.getBYes().setVisibility(View.GONE);
                    mu.getBNo().setVisibility(View.GONE);
                    mu.getTvMessage().setText(getString(R.string.uploading_contacts) + "\n"
                            + getString(R.string.reading_contacts));
                    //MessageUtils.toast(getApplicationContext(), getString(R.string.uploading_contacts), false);
                    mu.setProgressBarHorizontalProgress(10);
                    // New Thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mu.setProgressBarHorizontalProgress(30);
                            // Get phone contacts info
                            HashMap<String, HashMap<String, Object>> hmContacts = PhoneContactUtils.getPhoneContactsHashMap(getApplicationContext());
                            final int hmSize = hmContacts.size();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mu.getTvMessage().setText(getString(R.string.uploading_contacts)
                                            + "\n" + String.format(getString(R.string.number_contacts), hmSize));
                                }
                            });

                            mu.setProgressBarHorizontalProgress(60);
                            // Save contacts to Firebase
                            final Context context = getApplicationContext();
                            Firebase.setAndroidContext(context);
                            Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                            fbRef = fbRef.child(CONSTANTS.FIREBASE_DOC_CONTACTS + File.separator + mUserId);
                            fbRef.setValue(hmContacts, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    mu.setProgressBarHorizontalProgress(90);
                                    if (firebaseError != null) {
                                        MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                    } else {
                                        MessageUtils.toast(context, getString(R.string.upload_successful), false);
                                        listView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    mu.cancel();
                                }
                            });
                        }
                    }).start();
                }
            });
            mu.setOnClickListenerNo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mu.cancel();
                }
            });
            mu.show();
        }
        if (id == R.id.action_logout) {
            UserAuthUtils.logUserOut(this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            //
            /*int notificationId = (int) Calendar.getInstance().getTimeInMillis();
            ArrayList<NotificationCompat.Action> actions = new ArrayList<>();
            //
            // Create Intent
            Intent intent = new Intent(this, NotificationButtonReceiver.class);
            intent.putExtra(CONSTANTS.NOTIFICATION_ID, notificationId);
            intent.putExtra(CONSTANTS.ACTION_ID, CONSTANTS.ACTION_CALL_BACK);
            intent.putExtra(CONSTANTS.PHONE_NUMBER_ID, "123456789");
            // Create PendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId + 0,  // Id must be different for every action button
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            // Create Notification Action
            NotificationCompat.Action action = new NotificationCompat.Action
                    .Builder(R.drawable.ic_call_24dp, "Call Back", pendingIntent).build();
            // Add Action to array
            actions.add(action);
            //
            MessageUtils.notification(getApplicationContext(), "LINEBACKER Handled Call", "Incoming Number: 123456789", notificationId, null, actions, true);*/
            return true;
        }
        if (id == R.id.action_voip) {
            Intent intent = new Intent(this, SipDoubangoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // Compare id
        // Filters ----------------------------------------------------------
        if (id == R.id.nav_show_all){
            newFilter(null);
        } else if (id == R.id.nav_contacts_only) {
            newFilter(CONSTANTS.FIREBASE_FIELD_ISCONTACT + "=true");
        } else if (id == R.id.nav_blocked_only) {
            newFilter(CONSTANTS.FIREBASE_FIELD_ISCONTACT + "=false");
        } else if (id == R.id.nav_in_case_only) {
            newFilter(CONSTANTS.FIREBASE_FIELD_ISONCASE + "=true");
        } else if (id == R.id.nav_not_in_case_only) {
            newFilter(CONSTANTS.FIREBASE_FIELD_ISONCASE + "=false");
        }
        // Options ----------------------------------------------------------
        else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_phone_contacts) {
            //Intent intent= new Intent(Intent.ACTION_PICK,  Contacts.CONTENT_URI);
            //startActivityForResult(intent, PICK_CONTACT);
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        } else if (id == R.id.nav_upgrade) {
            Uri uri = Uri.parse("http://clouditapp.com/dev/linebacker/en/index.html#"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            Uri uri = Uri.parse("http://clouditapp.com/dev/linebacker/en/index.html#"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        // Share ------------------------------------------------------------
        else if (id == R.id.nav_share_whatsapp) {
            String message = getString(R.string.message_share_whatsapp) + "\n"
                    + getString(R.string.message_share_url);
            IntentUtils.shareMessageToWhatsapp(this, message);
        } else if (id == R.id.nav_share_facebook) {
            IntentUtils.shareMessageToFacebook(this, getString(R.string.app_name),
                    getString(R.string.message_share_facebook),
                    getString(R.string.message_share_url));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void newFilter(String filter){
        if(progressBar.getVisibility() == View.VISIBLE) {
            MessageUtils.toast(getApplicationContext(), getString(R.string.wait_still_loading), false);
        } else {
            isSearchOpened = ViewUtils.handleMenuSearch(this, mRecordingAdapter, true, mSearchAction);
            if(filter != null) {
                mRecordingAdapter.getFilter().filter(filter);
                isFiltered = true;
            }
        }
    }


}

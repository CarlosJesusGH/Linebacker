package com.cmsys.linebacker.ui;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.adapter.RecordingAdapter;
import com.cmsys.linebacker.bean.LogBean;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.io.DataIO;
import com.cmsys.linebacker.util.AppInitialSetupUtils;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.PhoneCallUtils;
import com.cmsys.linebacker.util.PhoneContactUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.util.ViewUtils;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

public class RecordingLogActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = makeLogTag(RecordingLogActivity.class);
    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private UserBean mUser;     // Check if necessary
    private String mUserId;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initial App Setup -----------------------------------------------------------------------
        AppInitialSetupUtils.createAppFolders();
        // Activity Views Setup --------------------------------------------------------------------
        setupViews();
        // Test code
        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is logged in --------------------------------------------------------------
        mUserId = SharedPreferencesUtils.getUserIdFromPreferences(this, getString(R.string.pref_key_user_id));
        if(mUserId != null){
            // Get Firebase settings if SharedPreference doesn't exists
            if(!SharedPreferencesUtils.checkIfContainsKey(this, getString(R.string.pref_key_setting_block_calls))){
                DataIO.getFirebaseSettings(this, mUserId);
            }
            getDataFromFirebase();
        } else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setupViews() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String selectedFromList =(String) (listView.getItemAtPosition(position));
                RecordingBean recording = (RecordingBean) listView.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), RecordingDetailsActivity.class);
                intent.putExtra(CONSTANTS.BUNDLE_EXTRA_RECORDING, recording);
                startActivity(intent);
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void getDataFromFirebase(){
        // Firebase Setup --------------------------------------------------------------------------
        //
        // Create a new Adapter
        final RecordingAdapter adapter = new RecordingAdapter(this, new ArrayList<RecordingBean>());
        // Assign adapter to ListView
        listView.setAdapter(adapter);
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
                adapter.add(newRecording);
                ViewUtils.hideProgressBar(progressBar);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //adapter.remove((String) dataSnapshot.child("AudioId").getValue());
                adapter.remove(dataSnapshot.getValue(RecordingBean.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            Log.i(TAG, "Request code1 = " + requestCode);
            //Intent intent = new Intent(AudioRecordsActivity.this, TService.class);
            //startService(intent);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Default created methods for back and menu buttons -------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //MessageUtils mu = new MessageUtils(this, "Settings", "Go to settings activity", 0, true);
            //MessageUtils.toast(this, "Go to settings activity...", false);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferencesUtils.removeKey(this, getString(R.string.pref_key_user_id));
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_upload_contacts) {
            //PhoneContactUtils.getPhoneContacts(getApplicationContext());
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            MessageUtils.toast(getApplicationContext(), getString(R.string.uploading_contacts), false);
            // New Thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Get phone contacts info
                    HashMap<String, HashMap<String, Object>> hmContacts = PhoneContactUtils.getPhoneContactsHashMap(getApplicationContext());
                    // Save contacts to Firebase
                    final Context context = getApplicationContext();
                    Firebase.setAndroidContext(context);
                    Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                    fbRef = fbRef.child(CONSTANTS.FIREBASE_DOC_CASE_CONTACTS + File.separator + mUserId);
                    fbRef.setValue(hmContacts, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                            } else {
                                MessageUtils.toast(context, getString(R.string.upload_successful), false);
                                listView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }).start();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_send) {

        } else*/ if (id == R.id.nav_manage) {
            MessageUtils.toast(this, "Go to settings activity...", false);
        } else if (id == R.id.nav_share) {
            MessageUtils.toast(this, "Go to share activity...", false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.cmsys.linebacker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.bean.CaseBean;
import com.cmsys.linebacker.bean.CommentBean;
import com.cmsys.linebacker.bean.LogBean;
import com.cmsys.linebacker.bean.RecordingBean;
import com.cmsys.linebacker.bean.UserBean;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.cmsys.linebacker.util.ViewUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.io.File;

public class CaseDetailsActivity extends AppCompatActivity {
    private RecordingBean mRecordingBean;
    private RelativeLayout rlCaseDetails, rlCaseLogs, rlCaseComments;
    private LinearLayout llCaseDetailsContent, llCaseLogsContent, llCaseCommentsContent;
    private ImageView ivCaseDetails, ivCaseLogs, ivCaseComments;
    private ProgressBar pbCaseDetails, pbCaseLogs, pbCaseComments;
    private Firebase ref;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_details);
        //
        // Get recording from bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            mRecordingBean = (RecordingBean) bundle.getSerializable(CONSTANTS.BUNDLE_EXTRA_RECORDING);
        // Check if user is logged in
        mUserId = UserAuthUtils.getUserId(this);
        if(mRecordingBean != null && mUserId != null) {
            // Set Home/Up button
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Activity Views Setup --------------------------------------------------------------------
            setupViews();
        } else {
            finish();
        }
    }

    private void setupViews() {
        // Firebase access
        Firebase.setAndroidContext(this);
        // Setup Floating Action Button
        final Activity activity = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, getString(R.string.add_new_comment), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                final MessageUtils mu = new MessageUtils(activity, getString(R.string.add_new_comment), "", 0, false);
                mu.getEtInput().setVisibility(View.VISIBLE);
                //mu.getEtInput().setHint(getString(R.string.type_comment_here));
                mu.getTilInput().setHint(getString(R.string.type_comment_here));
                mu.setOnClickListenerAccept(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Context context = view.getContext();
                        // Check if text is filled
                        if (!mu.getEtInput().getText().toString().equals("")) {
                            mu.getProgressBar().setVisibility(View.VISIBLE);
                            mu.getBAccept().setVisibility(View.GONE);
                            mu.getBCancel().setVisibility(View.GONE);
                            // Create new comment object
                            CommentBean commentBean = new CommentBean(mUserId, ServerValue.TIMESTAMP, mu.getEtInput().getText().toString());
                            // Connect to Firebase
                            Firebase.setAndroidContext(context);
                            final Firebase fbRef = new Firebase(CONSTANTS.FIREBASE_APP_URL + CONSTANTS.FIREBASE_DOC_CASE_COMMENTS + File.separator + mRecordingBean.getKey());
                            fbRef.push().setValue(commentBean.getObjectMap(), new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError != null) {
                                        MessageUtils.toast(context, context.getString(R.string.error_firebase_save) + firebaseError.getMessage(), false);
                                        mu.getProgressBar().setVisibility(View.GONE);
                                        mu.getBAccept().setVisibility(View.VISIBLE);
                                        mu.getBCancel().setVisibility(View.VISIBLE);
                                    } else {
                                        MessageUtils.toast(context, getString(R.string.new_comment_added), false);
                                        if(llCaseCommentsContent.getVisibility() == View.VISIBLE) {
                                            ViewUtils.collapse(llCaseCommentsContent);
                                            ivCaseComments.setImageResource(android.R.drawable.arrow_down_float);
                                        }
                                        mu.cancel();
                                    }
                                }
                            });
                        } else {
                            MessageUtils.toast(context, getString(R.string.error_comment_empty), false);
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
        });
        //
        rlCaseDetails = (RelativeLayout) findViewById(R.id.rlCaseDetails);
        rlCaseLogs = (RelativeLayout) findViewById(R.id.rlCaseLogs);
        rlCaseComments = (RelativeLayout) findViewById(R.id.rlCaseComments);
        llCaseDetailsContent = (LinearLayout) findViewById(R.id.llCaseDetailsContent);
        llCaseLogsContent = (LinearLayout) findViewById(R.id.llCaseLogsContent);
        llCaseCommentsContent = (LinearLayout) findViewById(R.id.llCaseCommentsContent);
        ivCaseDetails = (ImageView) findViewById(R.id.ivCaseDetails);
        ivCaseLogs = (ImageView) findViewById(R.id.ivCaseLogs);
        ivCaseComments = (ImageView) findViewById(R.id.ivCaseComments);
        pbCaseDetails = (ProgressBar) findViewById(R.id.pbCaseDetails);
        pbCaseLogs = (ProgressBar) findViewById(R.id.pbCaseLogs);
        pbCaseComments = (ProgressBar) findViewById(R.id.pbCaseComments);
        //
        rlCaseDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llCaseDetailsContent.getVisibility() == View.VISIBLE) {
                    ViewUtils.collapse(llCaseDetailsContent);
                    ivCaseDetails.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    pbCaseDetails.setVisibility(View.VISIBLE);
                    // Reading Data Once
                    ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                    ref.child(CONSTANTS.FIREBASE_DOC_CASES + File.separator + mUserId + File.separator + mRecordingBean.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            CaseBean caseBean = (CaseBean) snapshot.getValue(CaseBean.class);
                            if (caseBean != null) {
                                llCaseDetailsContent.removeAllViews();
                                addViewToLayout(getString(R.string.case_marketing_phone_number), caseBean.getMarketingPhone(), llCaseDetailsContent, R.layout.activity_case_details_item);
                                addViewToLayout(getString(R.string.case_user_phone_number), caseBean.getPhoneNumber(), llCaseDetailsContent, R.layout.activity_case_details_item);
                                addViewToLayout(getString(R.string.recording_date), caseBean.getDatetimeString().substring(0, caseBean.getDatetimeString().indexOf(" ")), llCaseDetailsContent, R.layout.activity_case_details_item);
                                addViewToLayout(getString(R.string.recording_time), caseBean.getDatetimeString().substring(caseBean.getDatetimeString().indexOf(" ")), llCaseDetailsContent, R.layout.activity_case_details_item);
                                ref.child(File.separator + CONSTANTS.FIREBASE_DOC_CASE_STATUS + File.separator
                                        + caseBean.getStatusId() + File.separator + CONSTANTS.FIREBASE_FIELD_STATUSNAME)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists())
                                                    addViewToLayout(getString(R.string.case_status), (String) dataSnapshot.getValue(), llCaseDetailsContent, R.layout.activity_case_details_item);
                                                // Hide ProgressBar and expand Layout
                                                pbCaseDetails.setVisibility(View.GONE);
                                                ViewUtils.expand(llCaseDetailsContent);
                                                ivCaseDetails.setImageResource(android.R.drawable.arrow_up_float);
                                            }

                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {
                                            }
                                        });
                            } else {
                                MessageUtils.toast(getApplicationContext(), getString(R.string.error_no_info_to_show), true);
                                // Hide ProgressBar
                                pbCaseDetails.setVisibility(View.GONE);
                            }
                        }

                        @Override public void onCancelled(FirebaseError firebaseError) {}
                    });
                }
            }
        });
        rlCaseLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llCaseLogsContent.getVisibility() == View.VISIBLE) {
                    ViewUtils.collapse(llCaseLogsContent);
                    ivCaseLogs.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    pbCaseLogs.setVisibility(View.VISIBLE);
                    // Reading Data Once
                    ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                    ref.child(CONSTANTS.FIREBASE_DOC_CASE_LOGS + File.separator + mRecordingBean.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    llCaseLogsContent.removeAllViews();
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        int counter = 0;
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            final LogBean logBean = (LogBean) child.getValue(LogBean.class);
                                            final boolean lastIter = (++counter == dataSnapshot.getChildrenCount()) ? true : false;
                                            ref.child(File.separator + CONSTANTS.FIREBASE_DOC_CASE_STATUS + File.separator + logBean.getStatusId()
                                                    + File.separator + CONSTANTS.FIREBASE_FIELD_STATUSNAME)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            addViewToLayout(logBean.getDatetimeString(), (String) dataSnapshot.getValue(), llCaseLogsContent, R.layout.activity_case_details_logs_item);
                                                            if(lastIter){
                                                                // Hide ProgressBar and expand Layout
                                                                pbCaseLogs.setVisibility(View.GONE);
                                                                ViewUtils.expand(llCaseLogsContent);
                                                                ivCaseLogs.setImageResource(android.R.drawable.arrow_up_float);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(FirebaseError firebaseError) {
                                                        }
                                                    });
                                        }
                                    } else {
                                        MessageUtils.toast(getApplicationContext(), getString(R.string.error_no_info_to_show), true);
                                        // Hide ProgressBar
                                        pbCaseLogs.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                }
            }
        });
        rlCaseComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llCaseCommentsContent.getVisibility() == View.VISIBLE) {
                    ViewUtils.collapse(llCaseCommentsContent);
                    ivCaseComments.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    pbCaseComments.setVisibility(View.VISIBLE);
                    // Reading Data Once
                    ref = new Firebase(CONSTANTS.FIREBASE_APP_URL);
                    ref.child(CONSTANTS.FIREBASE_DOC_CASE_COMMENTS + File.separator + mRecordingBean.getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            llCaseCommentsContent.removeAllViews();
                            if (dataSnapshot.getChildrenCount() > 0) {
                                int counter = 0;
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    final CommentBean commentBean = (CommentBean) child.getValue(CommentBean.class);
                                    final boolean lastIter = (++counter == dataSnapshot.getChildrenCount()) ? true : false;
                                    ref.child(CONSTANTS.FIREBASE_DOC_USER + File.separator + mUserId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    UserBean userBean = (UserBean) dataSnapshot.getValue(UserBean.class);
                                                    if (userBean != null)
                                                        addViewToLayout(userBean.getFirstName() + "\n" + userBean.getLastName(), commentBean.getCommentText(), commentBean.getDatetimeString(), llCaseCommentsContent, R.layout.activity_case_details_comments_item);
                                                    //addViewToLayout(logBean.getDatetimeString(), (String) dataSnapshot.getValue(), llCaseLogsContent, R.layout.activity_case_details_logs_item);
                                                    if (lastIter) {
                                                        // Hide ProgressBar and expand Layout
                                                        pbCaseComments.setVisibility(View.GONE);
                                                        ViewUtils.expand(llCaseCommentsContent);
                                                        ivCaseComments.setImageResource(android.R.drawable.arrow_up_float);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {
                                                }
                                            });
                                }
                            } else {
                                MessageUtils.toast(getApplicationContext(), getString(R.string.error_no_info_to_show), true);
                                // Hide ProgressBar
                                pbCaseComments.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            }
        });
    }

    private void addViewToLayout(String pTextView1, String pTextView2, String pTextView3, LinearLayout pLayout, int pLayoutId) {
        LayoutInflater inflater = getLayoutInflater();
        View vInflated = (View) inflater.inflate(pLayoutId, null);
        ((TextView) vInflated.findViewById(R.id.textView1)).setText(pTextView1);
        ((TextView) vInflated.findViewById(R.id.textView2)).setText(pTextView2);
        if(pTextView3 != null)
            ((TextView) vInflated.findViewById(R.id.textView3)).setText(pTextView3);
        //
        pLayout.addView(vInflated);
    }

    private void addViewToLayout(String pTextView1, String pTextView2, LinearLayout pLayout, int pLayoutId) {
        addViewToLayout(pTextView1, pTextView2, null, pLayout, pLayoutId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle click on the Home/Up button
        if (id == android.R.id.home){
            finish();
            return true;
        } else if (id == R.id.action_home) {
            Intent intent = new Intent(getApplicationContext(), RecordingLogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_details, menu);
        return true;
    }
}

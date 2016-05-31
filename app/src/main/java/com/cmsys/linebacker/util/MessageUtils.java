package com.cmsys.linebacker.util;


//import com.carrental.cj.androidcarrental.ManageContractsActivity;
import com.cmsys.linebacker.R;
import com.cmsys.linebacker.receiver.NotificationButtonReceiver;
import com.cmsys.linebacker.ui.SettingsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageUtils extends AlertDialog{
	public static Toast sToast;
	private Context mContext;
	private AlertDialog mThis;
	private int mIdContentView;
	private TextView tvTitle, tvMessage;
	private EditText etInput;
	private TextInputLayout tilInput;
	private Button bYes, bNo, bAccept, bCancel, bOk;
	private ProgressBar progressBar;
    private ProgressBar progressBarHorizontal;
	private ListView listView;
	private View convertView;

	public MessageUtils(Context pContext){
		super(pContext);
		mContext = pContext;
	}

	public MessageUtils(Activity pActivity, String pTitle, String pMessage, int pContentView, boolean isJustMessage){
		super(pActivity);
		pContentView = (pContentView == 0)? R.layout.util_cust_dialog : pContentView;
		setupViews(pActivity, pTitle, pMessage, pContentView);
		this.setView(convertView, 0, 0, 0, 0);
		this.setCanceledOnTouchOutside(false);
		if(isJustMessage){
			bOk.setVisibility(View.VISIBLE);
			bAccept.setVisibility(View.GONE);
			this.show();
		}
	}

	public void setAsFinishingActivityAlert(final Activity pActivity){
		this.setOnClickListenerYes(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                pActivity.finish();
            }
        });

		this.setOnClickListenerNo(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

	private void setupViews(Context pContext, String pTitle, String pMessage, int pContentView){
		mContext = pContext;
		mThis = this;
		mIdContentView = pContentView;
		LayoutInflater inflater = getLayoutInflater();
		convertView = inflater.inflate(pContentView, null);
		tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
		tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
		etInput = (EditText) convertView.findViewById(R.id.etInput);
		tilInput = (TextInputLayout) convertView.findViewById(R.id.tilInput);
		listView = (ListView) convertView.findViewById(R.id.listView);
		bYes = (Button) convertView.findViewById(R.id.bYes);
		bNo = (Button) convertView.findViewById(R.id.bNo);
		bAccept = (Button) convertView.findViewById(R.id.bAccept);
		bCancel = (Button) convertView.findViewById(R.id.bCancel);
		bOk = (Button) convertView.findViewById(R.id.bOk);
		progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        progressBarHorizontal = (ProgressBar) convertView.findViewById(R.id.progressBarHorizontal);

		// Set Title and Message text
		tvTitle.setText(pTitle);
		if (!TextUtils.isEmpty(pMessage))
			tvMessage.setText(pMessage);
		else
			tvMessage.setVisibility(View.GONE);

		View.OnClickListener defaultOCL = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mThis.dismiss();
			}
		};

		bYes.setOnClickListener(defaultOCL);
		bNo.setOnClickListener(defaultOCL);
		bAccept.setOnClickListener(defaultOCL);
		bCancel.setOnClickListener(defaultOCL);
		bOk.setOnClickListener(defaultOCL);
	}

	public void setOnClickListenerYes(View.OnClickListener onClickListener){
		bYes.setVisibility(View.VISIBLE);
		bYes.setOnClickListener(onClickListener);
		// Hide ACCEPT button if using YES instead
		if(bAccept.getVisibility() == View.VISIBLE)
			bAccept.setVisibility(View.GONE);
	}

	public void setOnClickListenerNo(View.OnClickListener onClickListener){
		bNo.setVisibility(View.VISIBLE);
		bNo.setOnClickListener(onClickListener);
	}

	public void setOnClickListenerAccept(View.OnClickListener onClickListener){
		bAccept.setVisibility(View.VISIBLE);
		bAccept.setOnClickListener(onClickListener);
	}

	public void setOnClickListenerCancel(View.OnClickListener onClickListener){
		bCancel.setVisibility(View.VISIBLE);
		bCancel.setOnClickListener(onClickListener);
	}

	public void setOnClickListenerOk(View.OnClickListener onClickListener){
		bOk.setVisibility(View.VISIBLE);
		bOk.setOnClickListener(onClickListener);
	}

	public static void toast(final Context context, final String message, final boolean isLong) {
		// Set this up in the UI thread.

//		new Handler(Looper.getMainLooper()) {
//			@Override public void handleMessage(Message msg) {}
//		};

		sToast = Toast.makeText(context, "", isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		//sToast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		sToast.setText(message);
		sToast.show();

	}

	public void toast(String message, boolean isLong){
		sToast = Toast.makeText(mContext, "", isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		//sToast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		sToast.setText(message);
		sToast.show();
	}

	public static void defaultConfirmDialog(Context pContext, String pTitle, OnClickListener pPositiveClickListenter, OnClickListener pNegativeClickListenter){
		Builder builder = new Builder(pContext);
		builder.setCancelable(true);
		//builder.setIcon(R.drawable.dialog_question);
		builder.setTitle(pTitle);
		///builder.setInverseBackgroundForced(true);	// Removed 20151129
		builder.setPositiveButton("Yes", pPositiveClickListenter);
		builder.setNegativeButton("No", pNegativeClickListenter);
		AlertDialog alert = builder.create();
		alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        alert.show();
	}

	public Button getBYes() {
		return bYes;
	}

	public Button getBNo() {
		return bNo;
	}

	public Button getBAccept() {
		return bAccept;
	}

	public Button getBCancel() {
		return bCancel;
	}

	public Button getBOk() {
		return bOk;
	}

	public EditText getEtInput() {
		return etInput;
	}

	public void showEditTextAndSoftKeyboard() {
		getEtInput().setVisibility(View.VISIBLE);
		getEtInput().requestFocus();
		ViewUtils.showSoftKeyboardOnAlertDialog(this);
	}

	public TextInputLayout getTilInput() {
		return tilInput;
	}

	public ProgressBar getProgressBar(){
		return progressBar;
	}

    public ProgressBar getProgressBarHorizontal() {
        return progressBarHorizontal;
    }

    public void setProgressBarHorizontalProgress(final int progress) {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                progressBarHorizontal.setProgress(progress);
            }
        }).start();*/
        progressBarHorizontal.setProgress(progress);
    }

    public TextView getTvMessage() {
        return tvMessage;
	}

	public ListView getListView() {
		return listView;
	}

	public View getConvertView(){
		return convertView;
	}
	// Notifications section -----------------------------------------------------------------------

	public static NotificationCompat.Builder notification(Context pContext, String pTitle, String pText, int pNotificationId,
														  Class<?> pClassToStart, ArrayList<NotificationCompat.Action> actions,
														  boolean addDismissButton, Integer iconDrawableId, boolean showNotification) {

		try {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(pContext)
					.setSmallIcon(iconDrawableId == null ? R.mipmap.ic_helmet : iconDrawableId)
					.setContentTitle(pTitle)
					.setContentText(pText)
					.setAutoCancel(true);

			// Add action buttons
			int actionCounter = 0;
			if (actions != null && actions.size() > 0) {
				for (NotificationCompat.Action action : actions) {
					mBuilder.addAction(action);
					actionCounter++;
				}
			}

            if (addDismissButton) {
                // Create Intent
                Intent dismissIntent = new Intent(pContext, NotificationButtonReceiver.class);
                dismissIntent.putExtra(CONSTANTS.NOTIFICATION_ID, pNotificationId);
                dismissIntent.putExtra(CONSTANTS.ACTION_ID, CONSTANTS.ACTION_DISMISS);
                // Create PendingIntent
                PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(pContext, pNotificationId + actionCounter,
                        dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                // Create Notification Action
                NotificationCompat.Action dismissAction = new NotificationCompat
                        .Action(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent);
                // Add action to builder
                mBuilder.addAction(dismissAction);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setColor(Color.alpha(R.color.colorPrimary));
            }

			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent();
			PendingIntent resultPendingIntent = PendingIntent.getActivity(pContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


			if (pClassToStart != null) {
				resultIntent = new Intent(pContext, pClassToStart);

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    stackBuilder = TaskStackBuilder.create(pContext);

					// Adds the back stack for the Intent (but not the Intent itself)
					stackBuilder.addParentStack(pClassToStart);
					// Adds the Intent that starts the Activity to the top of the stack
					stackBuilder.addNextIntent(resultIntent);
					resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				}
			}

			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);

			if (showNotification) {
				// pNotificationId allows you to update the notification later on.
				mNotificationManager.notify(pNotificationId, mBuilder.build());
			}
			return mBuilder;
		} catch (Exception e){
			ExceptionUtils.displayExceptionMessage(pContext, e);
			ExceptionUtils.printExceptionToFile(e);
		}
		return null;
	}

    public static void dismissNotification(Context context, int notificationId) {
        //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.cancel(notificationId);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    public static void showProgressBarAndHideButtons(MessageUtils mu) {
        mu.getProgressBar().setVisibility(View.VISIBLE);
        mu.getBAccept().setVisibility(View.GONE);
        mu.getBCancel().setVisibility(View.GONE);
        mu.getBYes().setVisibility(View.GONE);
        mu.getBNo().setVisibility(View.GONE);
        mu.getBOk().setVisibility(View.GONE);
    }

    public static void hideProgressBarAndShowAcceptButtons(MessageUtils mu) {
        mu.getProgressBar().setVisibility(View.GONE);
        mu.getBAccept().setVisibility(View.VISIBLE);
        mu.getBCancel().setVisibility(View.VISIBLE);
    }
}
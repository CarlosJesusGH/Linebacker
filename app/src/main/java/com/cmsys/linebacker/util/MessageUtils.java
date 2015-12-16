package com.cmsys.linebacker.util;


//import com.carrental.cj.androidcarrental.ManageContractsActivity;
import com.cmsys.linebacker.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MessageUtils extends AlertDialog{
	public static Toast sToast;
	private Context mContext;
	private AlertDialog mThis;
	private int mIdContentView;
	private TextView tvTitle, tvMessage;
	private EditText etInput;
	private Button bYes, bNo, bAccept, bCancel, bOk;
	private ProgressBar progressBar;
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
		bYes = (Button) convertView.findViewById(R.id.bYes);
		bNo = (Button) convertView.findViewById(R.id.bNo);
		bAccept = (Button) convertView.findViewById(R.id.bAccept);
		bCancel = (Button) convertView.findViewById(R.id.bCancel);
		bOk = (Button) convertView.findViewById(R.id.bOk);
		progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

		tvTitle.setText(pTitle);
		tvMessage.setText(pMessage);

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

	public static void toast(Context context, String message, boolean isLong){
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

	public ProgressBar getProgressBar(){
		return progressBar;
	}

	public View getConvertView(){
		return convertView;
	}
	// Notifications section -----------------------------------------------------------------------

	public static void notification(Context pContext, String pTitle, String pText, int pNotificationId,
									Class<?> pClassToStart){

		try {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(pContext)
					.setSmallIcon(R.mipmap.ic_launcher)
					.setContentTitle(pTitle)
					.setContentText(pText)
					.setAutoCancel(true);

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
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
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

			// mId allows you to update the notification later on.
//        mNotificationManager.notify(mId, mBuilder.build());
			mNotificationManager.notify(pNotificationId, mBuilder.build());
		} catch (Exception e){
			ExceptionUtils.displayExceptionMessage(pContext, e);
			ExceptionUtils.printExceptionToFile(pContext, e);
		}
	}//
}
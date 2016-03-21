package com.cmsys.linebacker.service;

/*
 *  Copyright 2012 Kobi Krasnoff
 *
 * This file is part of Call recorder For Android.

    Call recorder For Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Call recorder For Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Call recorder For Android.  If not, see <http://www.gnu.org/licenses/>
 */

import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

        import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
        import android.media.MediaRecorder;
import android.os.Environment;
        import android.os.IBinder;
        import android.text.format.DateFormat;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.ui.RecordingLogActivity;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.firebase.client.Firebase;

public class RecordService extends Service {

    public static final String LISTEN_ENABLED = "ListenEnabled";
    public static final String FILE_DIRECTORY = CONSTANTS.FOLDER_NAME_ROOT + File.separator + CONSTANTS.FOLDER_NAME_AUDIOS;
    private MediaRecorder recorder = new MediaRecorder();
    private String phoneNumber = null;
    private boolean recordstarted = false;
    public static final int STATE_INCOMING_NUMBER = 0;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;

    private NotificationManager manger;
    private String myFileName;
    public static boolean wasRinging = false;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {



        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int commandType = intent.getIntExtra("commandType", STATE_INCOMING_NUMBER);

        if (commandType == STATE_INCOMING_NUMBER)
        {
            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");
        }
        else if (commandType == STATE_CALL_START)
        {

            /*try {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                //Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:0414-5458521"));
                intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentCall.addFlags(Intent.FLAG_FROM_BACKGROUND);
                startActivity(intentCall);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");


            try {
                //recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);  // Original (This should be the selected option, but returns error on Nexus5) (E/MediaRecorder: start failed: -2147483648)
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Original
                //recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);    // Original
                //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                myFileName = getFilename();
                recorder.setOutputFile(myFileName);
            }
            catch (IllegalStateException e) {
                //Log.e("Call recorder IllegalStateException: ", "");
                terminateAndEraseFile();
                ExceptionUtils.printExceptionToFile(e);
            }
            catch (Exception e) {
                //Log.e("Call recorder Exception: ", "");
                terminateAndEraseFile();
                ExceptionUtils.printExceptionToFile(e);
            }

            /*OnErrorListener errorListener = new OnErrorListener() {

                public void onError(MediaRecorder arg0, int arg1, int arg2) {
                    //Log.e("Call recorder OnErrorListener: ", arg1 + "," + arg2);
                    arg0.stop();
                    arg0.reset();
                    arg0.release();
                    arg0 = null;
                    terminateAndEraseFile();
                }

            };
            recorder.setOnErrorListener(errorListener);
            OnInfoListener infoListener = new OnInfoListener() {

                public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
                    //Log.e("Call recorder OnInfoListener: ", arg1 + "," + arg2);
                    arg0.stop();
                    arg0.reset();
                    arg0.release();
                    arg0 = null;
                    terminateAndEraseFile();
                }

            };
            recorder.setOnInfoListener(infoListener);*/


            try {
                recorder.prepare();
            } catch (IllegalStateException e) {
                //Log.e("Call recorder IllegalStateException: ", "");
                terminateAndEraseFile();
                e.printStackTrace();
                ExceptionUtils.printExceptionToFile(e);
            } catch (IOException e) {
                //Log.e("Call recorder IOException: ", "");
                terminateAndEraseFile();
                e.printStackTrace();
                ExceptionUtils.printExceptionToFile(e);
            }
            catch (Exception e) {
                //Log.e("Call recorder Exception: ", "");
                terminateAndEraseFile();
                e.printStackTrace();
                ExceptionUtils.printExceptionToFile(e);
            }

            try {
                recorder.start();   // TODO Check sometimes gives a problem here (This is beacause recorder parameters setAudioSource, OutputFormat and AudioEncoder)
                recordstarted = true;
                Toast.makeText(this, this.getString(R.string.receiver_start_call), Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                ExceptionUtils.printExceptionToFile(e);
            }
        }
        else if (commandType == STATE_CALL_END)
        {
            try {
                if(recordstarted) {
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recordstarted = false;
                    //
                    Toast toast = Toast.makeText(this, this.getString(R.string.receiver_end_call), Toast.LENGTH_SHORT);
                    toast.show();
                    Firebase.setAndroidContext(this);
                    new Firebase(CONSTANTS.FIREBASE_APP_URL + "RecordedAudiosByUser/CarlosJesusGH")
                            .push()
                            .child("AudioId")
                            .setValue(myFileName);

                    // Show notification
                    Date now = new Date();
                    int mNotificationId = (int) now.getTime();//use date to generate an unique id to differentiate the notifications.
                    MessageUtils.notification(this, this.getString(R.string.notification_new_audio_recorded),
                            myFileName, mNotificationId, RecordingLogActivity.class, new ArrayList<NotificationCompat.Action>(), false, null, true);
                }
                recorder = null;
                wasRinging = false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                ExceptionUtils.displayExceptionMessage(this, e);
                ExceptionUtils.printExceptionToFile(e);
            }

            try {
                if (manger != null)
                    manger.cancel(0);
                stopForeground(true);
                this.stopSelf();
            } catch (Exception e) {
                ExceptionUtils.displayExceptionMessage(this, e);
                ExceptionUtils.printExceptionToFile(e);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * in case it is impossible to record
     */
    private void terminateAndEraseFile()
    {
        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            Toast toast = Toast.makeText(this, this.getString(R.string.receiver_end_call), Toast.LENGTH_SHORT);
            toast.show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        File file = new File(myFileName);

        if (file.exists()) {
            file.delete();

        }
        Toast toast = Toast.makeText(this, this.getString(R.string.record_impossible), Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * returns absolute file directory
     *
     * @return
     */
    private String getFilename() {
        //String filepath = getFilesDir().getAbsolutePath();    //This option is more secure because files cannot be externally accessed
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, FILE_DIRECTORY);

        if (!file.exists()) {
            file.mkdirs();
        }

        String myDate = new String();
        myDate = (String) DateFormat.format("yyyyMMdd_kkmmss", new Date());
        return (file.getAbsolutePath() + "/" + myDate + "_" + phoneNumber + ".mp3");
        //return (file.getAbsolutePath() + "/" + myDate + "_" + phoneNumber + ".amr");
    }

}

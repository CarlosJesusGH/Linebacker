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

import android.widget.Toast;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.util.Date;

        import android.app.AlertDialog;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.media.MediaRecorder;
        import android.media.MediaRecorder.OnErrorListener;
        import android.media.MediaRecorder.OnInfoListener;
        import android.os.Environment;
        import android.os.IBinder;
        import android.text.format.DateFormat;
        import android.util.Log;
        import android.widget.Toast;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.ui.AudioRecordsActivity;
import com.cmsys.linebacker.util.MessageUtils;
import com.firebase.client.Firebase;

public class RecordService extends Service {

    public static final String LISTEN_ENABLED = "ListenEnabled";
    public static final String FILE_DIRECTORY = "Linebacker/Audio";
    private MediaRecorder recorder = new MediaRecorder();
    private String phoneNumber = null;;
    public static final int STATE_INCOMING_NUMBER = 0;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;

    private NotificationManager manger;
    private String myFileName;


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
            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");


            try {
                //recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);  // Original
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
            }
            catch (Exception e) {
                //Log.e("Call recorder Exception: ", "");
                terminateAndEraseFile();
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
            } catch (IOException e) {
                //Log.e("Call recorder IOException: ", "");
                terminateAndEraseFile();
                e.printStackTrace();
            }
            catch (Exception e) {
                //Log.e("Call recorder Exception: ", "");
                terminateAndEraseFile();
                e.printStackTrace();
            }
                recorder.start();   // TODO Check sometimes gives a problem here
                Toast.makeText(this, this.getString(R.string.reciever_start_call), Toast.LENGTH_LONG).show();
        }
        else if (commandType == STATE_CALL_END)
        {
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                Toast toast = Toast.makeText(this, this.getString(R.string.reciever_end_call), Toast.LENGTH_SHORT);
                toast.show();
                new Firebase("https://linebacker.firebaseio.com/todoItems")
                        .push()
                        .child("text")
                        .setValue("value: " + myFileName);

                // Show notification
                Date now = new Date();
                int mNotificationId = (int) now.getTime();//use date to generate an unique id to differentiate the notifications.
                MessageUtils.notification(this, this.getString(R.string.notification_new_audio_recorded), myFileName, mNotificationId, AudioRecordsActivity.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (manger != null)
                manger.cancel(0);
            stopForeground(true);
            this.stopSelf();
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
            Toast toast = Toast.makeText(this, this.getString(R.string.reciever_end_call), Toast.LENGTH_SHORT);
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

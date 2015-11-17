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
//package com.call.recorder;
package com.cmsys.linebacker.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.ui.AudioRecordsActivity;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

public class RecordService extends Service {

    private static final String TAG = makeLogTag(RecordService.class);

    MediaRecorder recorder;
    File audiofile;
    String name, phonenumber;
    String audio_format;
    public String Audio_Type;
    //int audioSource;
    //Context context;
    private Handler handler;
    Timer timer;
    Boolean offHook = false, ringing = false;
    //Toast toast;
    Boolean isOffHook = false;
    private boolean recordstarted = false;
    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

    Bundle bundle;
    String state;
    String inCall, outCall;
    public boolean wasRinging = false;

    //-----------------------------------------------------


    //public static final String LISTEN_ENABLED = "ListenEnabled";
    //public static final String FILE_DIRECTORY = "recordedCalls";
    //private MediaRecorder recorder = new MediaRecorder();
    private String phoneNumber = null;;
    public static final int STATE_INCOMING_NUMBER = 0;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;

    //private NotificationManager manger;
    //private String myFileName;


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
        //android.os.Debug.waitForDebugger();
        /*if (intent.getAction().equals(ACTION_IN)) {
            if ((bundle = intent.getExtras()) != null) {
                state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    wasRinging = true;
                    Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (wasRinging == true) {

                        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();

                        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1");
                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }
                        String file_name = "Record";
                        try {
                            audiofile = File.createTempFile(file_name, ".amr", sampleDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                        recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

                        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(audiofile.getAbsolutePath());
                        try {
                            recorder.prepare();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        recorder.start();
                        recordstarted = true;
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    wasRinging = false;
                    Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
                    if (recordstarted) {
                        recorder.stop();
                        recordstarted = false;
                    }
                }
            }
        } else if (intent.getAction().equals(ACTION_OUT)) {
            if ((bundle = intent.getExtras()) != null) {
                outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
            }
        }

        return 0;*/


        int commandType = intent.getIntExtra("commandType", STATE_INCOMING_NUMBER);

        if (commandType == STATE_INCOMING_NUMBER){
            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");
        }
        else if (commandType == STATE_CALL_START){
            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");

            Toast.makeText(this, "LOG-BeforeStartRecording", Toast.LENGTH_LONG).show();

            String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
            File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1");
            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
            }

            String file_name = "Record";
            try {
                audiofile = File.createTempFile(file_name, ".amr", sampleDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            //recorder.setAudioSource(MediaRecorder.AudioSource.);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(audiofile.getAbsolutePath());
            // Increase incoming audio volume
            //audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            try {
                recorder.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
            recordstarted = true;
            /*try {
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                myFileName = getFilename();
                recorder.setOutputFile(myFileName);
            }
            catch (IllegalStateException e) {
                Log.e(TAG, "Call recorder IllegalStateException: ");
                terminateAndEraseFile();
            }
            catch (Exception e) {
                Log.e(TAG, "Call recorder Exception: ");
                terminateAndEraseFile();
            }

            OnErrorListener errorListener = new OnErrorListener() {

                public void onError(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e(TAG, "Call recorder OnErrorListener: ");
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
                    Log.e(TAG, "Call recorder OnInfoListener: ");
                    arg0.stop();
                    arg0.reset();
                    arg0.release();
                    arg0 = null;
                    terminateAndEraseFile();
                }

            };
            recorder.setOnInfoListener(infoListener);


            try {
                recorder.prepare();
                recorder.start();
                Toast toast = Toast.makeText(this, this.getString(R.string.reciever_start_call), Toast.LENGTH_SHORT);
                toast.show();

                manger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification(R.drawable.ic_menu_send, this.getString(R.string.notification_ticker), System.currentTimeMillis());
                notification.flags = Notification.FLAG_NO_CLEAR;

                Intent intent2 = new Intent(this, AudioRecordsActivity.class);
                intent2.putExtra("RecordStatus", true);

                PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent2, 0);
                //notification.setLatestEventInfo(this, this.getString(R.string.notification_title), this.getString(R.string.notification_text), contentIntent);
                //manger.notify(0, notification);

                startForeground(1337, notification);

            } catch (IllegalStateException e) {
                Log.e(TAG, "Call recorder IllegalStateException: ");
                terminateAndEraseFile();
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Call recorder IOException: ");
                terminateAndEraseFile();
                e.printStackTrace();
            }
            catch (Exception e) {
                Log.e(TAG, "Call recorder Exception: ");
                terminateAndEraseFile();
                e.printStackTrace();
            }

*/
        }
        else if (commandType == STATE_CALL_END) {
            /*try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                Toast toast = Toast.makeText(this, this.getString(R.string.reciever_end_call), Toast.LENGTH_SHORT);
                toast.show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (manger != null)
                manger.cancel(0);
            stopForeground(true);
            this.stopSelf();*/


            wasRinging = false;
            //Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
            if (recordstarted) {
                recorder.stop();
                recordstarted = false;
            }

            Toast.makeText(this, "LOG-AfterFinishRecording", Toast.LENGTH_LONG).show();


        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * in case it is impossible to record
     */
    /*private void terminateAndEraseFile()
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
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * returns absolute file directory
     *
     * @return
     */
    /*private String getFilename() {
        String filepath = getFilesDir().getAbsolutePath();
        File file = new File(filepath, FILE_DIRECTORY);

        if (!file.exists()) {
            file.mkdirs();
        }

        String myDate = new String();
        myDate = (String) DateFormat.format("yyyyMMddkkmmss", new Date());

        return (file.getAbsolutePath() + "/d" + myDate + "p" + phoneNumber + ".mp3");
    }*/

}

package com.cmsys.linebacker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.File;
import java.io.IOException;

// packages for audio record/plat sample activity
import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.IOException;

/**
 * Created by cj on 22/12/15.
 */
public class AudioUtils {

    public static void playAudioFile(String path, String fileName) {
        // Set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(path + File.separator + fileName);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playAudioRaw(Context context, int rawId){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, rawId);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    public static void streamAndPlayOnLocalApp(String url){
        //String url = "http://........"; // your URL here
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public static void streamOnDefaultPlayer(Activity activity, String url) {
        try {
            Uri myUri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(myUri, "audio/*");
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */
    public class AudioRecordTest extends Activity
    {
        private static final String LOG_TAG = "AudioRecordTest";
        private String mFileName = null;

        private RecordButton mRecordButton = null;
        private MediaRecorder mRecorder = null;

        private PlayButton   mPlayButton = null;
        private MediaPlayer   mPlayer = null;

        private void onRecord(boolean start) {
            if (start) {
                startRecording();
            } else {
                stopRecording();
            }
        }

        private void onPlay(boolean start) {
            if (start) {
                startPlaying();
            } else {
                stopPlaying();
            }
        }

        private void startPlaying() {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        }

        private void stopPlaying() {
            mPlayer.release();
            mPlayer = null;
        }

        private void startRecording() {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            mRecorder.start();
        }

        private void stopRecording() {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        class RecordButton extends Button {
            boolean mStartRecording = true;

            OnClickListener clicker = new OnClickListener() {
                public void onClick(View v) {
                    onRecord(mStartRecording);
                    if (mStartRecording) {
                        setText("Stop recording");
                    } else {
                        setText("Start recording");
                    }
                    mStartRecording = !mStartRecording;
                }
            };

            public RecordButton(Context ctx) {
                super(ctx);
                setText("Start recording");
                setOnClickListener(clicker);
            }
        }

        class PlayButton extends Button {
            boolean mStartPlaying = true;

            OnClickListener clicker = new OnClickListener() {
                public void onClick(View v) {
                    onPlay(mStartPlaying);
                    if (mStartPlaying) {
                        setText("Stop playing");
                    } else {
                        setText("Start playing");
                    }
                    mStartPlaying = !mStartPlaying;
                }
            };

            public PlayButton(Context ctx) {
                super(ctx);
                setText("Start playing");
                setOnClickListener(clicker);
            }
        }

        public void setOutputFileName() {
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/audiorecordtest.3gp";
        }

        public AudioRecordTest(){

        }

        @Override
        public void onCreate(Bundle icicle) {
            super.onCreate(icicle);

            setOutputFileName();

            LinearLayout ll = new LinearLayout(this);
            mRecordButton = new RecordButton(this);
            ll.addView(mRecordButton,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            0));
            mPlayButton = new PlayButton(this);
            ll.addView(mPlayButton,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            0));
            setContentView(ll);
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
            }

            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
        }
    }
}

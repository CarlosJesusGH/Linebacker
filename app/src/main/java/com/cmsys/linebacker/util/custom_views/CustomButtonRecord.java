package com.cmsys.linebacker.util.custom_views;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.MessageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CarlosJesusGH on 2/12/16.
 */
/**
    // Use it like this
    cbRecord = (CustomButtonRecord) findViewById(R.id.customButtonRecord);
    cbRecord.mFileName = CONSTANTS.PATH_ROOT_APP_FOLDER + CONSTANTS.FOLDER_NAME_AUDIOS +  "/recordedAudio.3gp";
    // Close it on Pause
     if (cbRecord.mRecorder != null) {
     cbRecord.mRecorder.release();
     cbRecord.mRecorder = null;
     }
 */
public class CustomButtonRecord extends Button {
    private static final String LOG_TAG = "CustomButtonRecord";
    private String mFileName = null;
    private String startText = "start", stopText = "stop";
    private boolean mStartRecording = true;
    public MediaRecorder mRecorder = null;
    private List<View> viewsToDisable;

    public void addViewToHide(View view){
        viewsToDisable.add(view);
    }

    private void changeViewsVisible(int newState){
        for (View iter: viewsToDisable) {
            try {
                iter.setVisibility(newState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public void setStartText(String startText) {
        this.startText = startText;
        setText(startText);
    }

    public void setStopText(String stopText) {
        this.stopText = stopText;
    }

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                setText(stopText);
                changeViewsVisible(View.INVISIBLE);
            } else {
                setText(startText);
                changeViewsVisible(View.VISIBLE);
            }
            mStartRecording = !mStartRecording;
        }
    };

    public CustomButtonRecord(Context ctx) {
        super(ctx);
        setText(startText);
        setOnClickListener(clicker);
        viewsToDisable = new ArrayList<>();
    }

    public CustomButtonRecord(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setText(startText);
        setOnClickListener(clicker);
        viewsToDisable = new ArrayList<>();
    }

    private void onRecord(boolean start) {
        if (start) {
            playBeep();
            MessageUtils.toast(getContext(), getContext().getString(R.string.message_after_beep), false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRecording();
                }
            }, 1000);

        } else {
            stopRecording();
        }
    }

    private void playBeep() {
        try {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
            toneG.startTone(ToneGenerator.TONE_DTMF_0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // Some post say that using OutputFormat.MPEG_4, AudioEncoder.AAC and OutputFileName=***.mp3
        // the output file is MP3 or something the system understands as MP3.
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
}

package com.cmsys.linebacker.util.custom_views;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

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
            } else {
                setText(startText);
            }
            mStartRecording = !mStartRecording;
        }
    };

    public CustomButtonRecord(Context ctx) {
        super(ctx);
        setText(startText);
        setOnClickListener(clicker);
    }

    public CustomButtonRecord(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setText(startText);
        setOnClickListener(clicker);
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
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

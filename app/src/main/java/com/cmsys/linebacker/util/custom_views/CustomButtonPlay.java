package com.cmsys.linebacker.util.custom_views;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by CarlosJesusGH on 2/12/16.
 */

/*
* Use it like this:
  cbPlay = (CustomButtonPlay) findViewById(R.id.customButtonPlay);
  cbPlay.mFileName = CONSTANTS.PATH_ROOT_APP_FOLDER + CONSTANTS.FOLDER_NAME_AUDIOS +  "/recordedAudio.3gp";
  // Close it on Pause
     if (cbPlay.mPlayer != null) {
     cbPlay.mPlayer.release();
     cbPlay.mPlayer = null;
     }
* */
public class CustomButtonPlay extends Button {
    private static final String LOG_TAG = "CustomButtonPlay";
    private String mFileName = null;
    private String startText = "start", stopText = "stop";
    private boolean mStartPlaying = true;
    public MediaPlayer mPlayer = null;

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
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                setText(stopText);
            } else {
                setText(startText);
            }
            mStartPlaying = !mStartPlaying;
        }
    };

    public CustomButtonPlay(Context ctx) {
        super(ctx);
        setText(startText);
        setOnClickListener(clicker);
    }

    public CustomButtonPlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setText(startText);
        setOnClickListener(clicker);
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
            // Play on earpiece CJG
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);    // For speaker
            //mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL); // For earpiece
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    CustomButtonPlay.this.callOnClick();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
}

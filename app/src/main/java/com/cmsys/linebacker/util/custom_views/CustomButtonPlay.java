package com.cmsys.linebacker.util.custom_views;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                setText(stopText);
                changeViewsVisible(View.INVISIBLE);
            } else {
                setText(startText);
                changeViewsVisible(View.VISIBLE);
            }
            mStartPlaying = !mStartPlaying;
        }
    };

    public CustomButtonPlay(Context ctx) {
        super(ctx);
        setText(startText);
        setOnClickListener(clicker);
        viewsToDisable = new ArrayList<>();
    }

    public CustomButtonPlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setText(startText);
        setOnClickListener(clicker);
        viewsToDisable = new ArrayList<>();
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

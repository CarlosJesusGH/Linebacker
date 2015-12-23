package com.cmsys.linebacker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
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

    public static void streamLocally(String url){
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
}

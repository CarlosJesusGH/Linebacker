package com.cmsys.linebacker.util.custom_views;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.upload_file.UtilUploader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CarlosJesusGH on 2/12/16.
 */

public class CustomButtonSendFile extends Button {
    private static final String LOG_TAG = "CustomButtonSendFile";
    private String mFilePath;
    private String mServerUrl;
    private HashMap<String, String> mPostParams;
    private boolean mStartPlaying = true;
    public MediaPlayer mPlayer = null;

    public void setPostParams(HashMap<String, String> pPostParams){
        mPostParams = pPostParams;
    }

    public void addPostParams(String key, String value){
        if(mPostParams == null)
            mPostParams = new HashMap<>();
        mPostParams.put(key, value);
    }

    public void setFilePath(String pFilePath) {
        mFilePath = pFilePath;
    }

    public void setServerUrl(String pServerUrl) {
        mServerUrl = pServerUrl;
    }

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            MessageUtils.toast(getContext(), "Trying to upload", true);
            UtilUploader.onUploadButtonClick(v.getContext(), mServerUrl, mFilePath, mPostParams);
        }
    };

    public CustomButtonSendFile(Context ctx) {
        super(ctx);
        setOnClickListener(clicker);
    }

    public CustomButtonSendFile(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(clicker);
    }
}

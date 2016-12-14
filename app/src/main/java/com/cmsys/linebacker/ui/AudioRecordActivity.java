package com.cmsys.linebacker.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.UserAuthUtils;
import com.cmsys.linebacker.util.custom_views.CustomButtonPlay;
import com.cmsys.linebacker.util.custom_views.CustomButtonRecord;
import com.cmsys.linebacker.util.custom_views.CustomButtonSendFile;

import java.io.File;

public class AudioRecordActivity extends AppCompatActivity {
    CustomButtonPlay cbPlay;
    CustomButtonRecord cbRecord;
    CustomButtonSendFile cbSendFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        // Set Home/Up button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Setup views
        cbPlay = (CustomButtonPlay) findViewById(R.id.customButtonPlay);
        cbPlay.setStartText(this.getString(R.string.button_play_greeting_start));
        cbPlay.setStopText(this.getString(R.string.button_play_greeting_stop));
        cbPlay.setFileName(CONSTANTS.PATH_ROOT_APP_FOLDER + CONSTANTS.FOLDER_NAME_AUDIOS + File.separator + CONSTANTS.AUDIO_GREETING_FILE_NAME);
        //
        cbRecord = (CustomButtonRecord) findViewById(R.id.customButtonRecord);
        cbRecord.setStartText(this.getString(R.string.button_record_greeting_start));
        cbRecord.setStopText(this.getString(R.string.button_record_greeting_stop));
        cbRecord.setFileName(CONSTANTS.PATH_ROOT_APP_FOLDER + CONSTANTS.FOLDER_NAME_AUDIOS + File.separator + CONSTANTS.AUDIO_GREETING_FILE_NAME);
        //
        cbSendFile = (CustomButtonSendFile) findViewById(R.id.customButtonSendFile);
        cbSendFile.setFilePath(CONSTANTS.PATH_ROOT_APP_FOLDER + CONSTANTS.FOLDER_NAME_AUDIOS + File.separator + CONSTANTS.AUDIO_GREETING_FILE_NAME);
        cbSendFile.setServerUrl(CONSTANTS.SYNC_WS_UPLOAD_AUDIO_API);
        cbSendFile.addPostParams("id", UserAuthUtils.getUserId(getApplicationContext()));
        cbSendFile.addPostParams("name", "greet");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cbRecord.mRecorder != null) {
            cbRecord.mRecorder.release();
            cbRecord.mRecorder = null;
        }

        if (cbPlay.mPlayer != null) {
            cbPlay.mPlayer.release();
            cbPlay.mPlayer = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle click on the Home/Up button
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

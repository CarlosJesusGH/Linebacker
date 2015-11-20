package com.cmsys.linebacker.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

/**
 * Created by cj on 19/11/15.
 */
public class AppInitialSetupUtils {
    private static final String TAG = makeLogTag(AppInitialSetupUtils.class);

    public static  void createAppFolders(){
        // File route_sd = Environment.getRootDirectory();
        File route_sd = Environment.getExternalStorageDirectory();
        Log.i(TAG, route_sd.toString());
        //CONSTANTS.PATH_CASAGRI_EXTERNAL_INTERNAL = route_sd.toString();
        // getAssets().open(fileName);
        File fileApp = new File(route_sd, File.separator + CONSTANTS.FOLDER_NAME_ROOT);
        // Validates if folder exists /AppName
        if (!fileApp.exists()) {
            // if not, it is created
            if (fileApp.mkdir()) {
                Log.i(TAG, "Folder is created:" + fileApp.toString());
            }
        }

        File filePathImages = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_IMAGES);
        if (!filePathImages.exists()) {
            if (filePathImages.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathImages.toString());
            }
        }

        File filePathDocuments = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_DOCUMENTS);
        if (!filePathDocuments.exists()) {
            if (filePathDocuments.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathDocuments.toString());
            }
        }

        /*File fileDatabase = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_DATABASE);
        if (!fileDatabase.exists()) {
            if (fileDatabase.mkdir()) {
                Log.i(TAG, "Folder is created:" + fileDatabase.toString());
            }
        }*/

        File filePathAudio = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_AUDIOS);
        if (!filePathAudio.exists()) {
            if (filePathAudio.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathAudio.toString());
            }
        }

        File filePathLog = new File(fileApp, File.separator + CONSTANTS.FOLDER_NAME_LOGS);
        if (!filePathLog.exists()) {
            if (filePathLog.mkdir()) {
                Log.i(TAG, "Folder is created:" + filePathLog.toString());
            }
        }
    }

}

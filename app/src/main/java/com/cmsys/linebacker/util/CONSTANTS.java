package com.cmsys.linebacker.util;

import android.content.Context;

import com.cmsys.linebacker.R;

/**
 * Created by cj on 19/11/15.
 */

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class CONSTANTS {

    //---DB names----------------
    public static int DB_VERSION = 1;
    public static String DB_NAME = "cmsys_db.sqlite";
    public static String DB_PATH = "/data/data/com.cmsys/databases/";

    //---Shared Preferences------
    public static String PREFS_NAME = "MyPrefsFile";

    //---Bundle Extras ----------
    public static final String BUNDLE_EXTRA_USER = "user";
    public static final String BUNDLE_EXTRA_RECORDING = "recording";

    //---WS Sync-----------------
    public static String SYNC_WS_IP = "cj-website.ddns.net";
    public static String SYNC_WS_PORT = "8080";
    public static String SYNC_WS_COMMON_PATH = "http://" + SYNC_WS_IP + ":" + SYNC_WS_PORT + "/HelloWorld/jaxrs/";
    public static String SYNC_WS_METHOD_UPLOAD = "CmsysUploadFile/upload";

    //---Firebase------------------
    public static String FIREBASE_APP_NAME = "linebacker";
    public static String FIREBASE_APP_URL = "https://" + CONSTANTS.FIREBASE_APP_NAME + ".firebaseio.com/";
    public static String FIREBASE_DOC_USER = "user";
    public static String FIREBASE_DOC_RECORDED_AUDIOS = "recordedAudiosByUser";
    public static String FIREBASE_DOC_SETTINGS = "setting";
    public static String FIREBASE_DOC_CASES = "casesByUser";
    public static String FIREBASE_DOC_CASE_STATUS = "caseStatus";
    public static String FIREBASE_DOC_CASE_LOGS = "logsByCase";
    public static String FIREBASE_DOC_CASE_COMMENTS = "commentsByCase";
    public static String FIREBASE_DOC_CASE_CONTACTS = "contactsByUser";

    public static String FIREBASE_FIELD_ISONCASE = "isOnCase";
    public static String FIREBASE_FIELD_AUDIOID = "audioId";
    public static String FIREBASE_FIELD_CASEID = "caseId";
    public static String FIREBASE_FIELD_DATETIME = "datetime";
    public static String FIREBASE_FIELD_MARKETINGPHONE = "marketingPhone";
    public static String FIREBASE_FIELD_STATUSID = "statusId";
    public static String FIREBASE_FIELD_USERPHONE = "userPhone";
    public static String FIREBASE_FIELD_STATUSNAME = "statusName";

    //-----------------------------

    public static String FOLDER_NAME_LOGS = "Log";
    public static String FOLDER_NAME_IMAGES = "Image";
    public static String FOLDER_NAME_AUDIOS = "Audio";
    public static String FOLDER_NAME_TEMP_FILES = "Temporal";
    public static String FOLDER_NAME_DOCUMENTS = "Document";
    public static String FOLDER_NAME_DATABASES = "Database";
    public static String FOLDER_NAME_ROOT = "Linebacker";
    public static String PATH_ROOT_APP_FOLDER = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME_ROOT + File.separator;
    public static String PDF_FILE_NAME = "pdf.pdf";
    public static String LOG_FILE_NAME = "log.txt";

    //-----------------------------

    public static int URL_CONNECT_TIMEOUT = 1000;
}


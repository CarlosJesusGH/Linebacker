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

    //--- Notification Ids -----
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String ACTION_ID = "actionId";
    public static final String PHONE_NUMBER_ID = "phoneNumber";
    public static final int ACTION_DISMISS = 101;
    public static final int ACTION_CALL_BACK = 102;

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
    public static String FIREBASE_DOC_CONTACTS = "contactsByUser";
    public static String FIREBASE_DOC_ZIPCODE = "zipCode";
    public static String FIREBASE_DOC_PHONECOMPANY = "phoneCompany";

    public static String FIREBASE_FIELD_USERID = "userId";
    public static String FIREBASE_FIELD_GCM_REGISTRATION_ID = "gcmRegistrationId";
    public static String FIREBASE_FIELD_USERFIRSTNAME = "firstName";
    public static String FIREBASE_FIELD_USERLASTNAME = "lastName";
    public static String FIREBASE_FIELD_PHONENUMBER = "phoneNumber";
    public static String FIREBASE_FIELD_USERSTATE = "state";
    public static String FIREBASE_FIELD_USERCITY = "city";
    public static String FIREBASE_FIELD_USERZIPCODE = "zipCode";
    public static String FIREBASE_FIELD_USERADDRESS = "address";
    public static String FIREBASE_FIELD_EMAIL = "email";
    public static String FIREBASE_FIELD_USERCREATIONDATE = "creationDate";
    public static String FIREBASE_FIELD_USERLASTCONNECTION = "lastConnection";
    public static String FIREBASE_FIELD_USERBIRTHDAY = "birthday";
    public static String FIREBASE_FIELD_USERLEVEL = "userLevel  ";
    //
    public static String FIREBASE_FIELD_SETTINGBLOCKCALLS = "blockCalls";
    public static String FIREBASE_FIELD_SETTINGDELETEEVERY = "deleteAudiosEveryWeeks";
    public static String FIREBASE_FIELD_SETTINGEMAILNOTIF = "emailNotification";
    public static String FIREBASE_FIELD_SETTINGMOBILENOTIF = "mobileNotification";
    //
    public static String FIREBASE_FIELD_ISONCASE = "isOnCase";
    public static String FIREBASE_FIELD_ISCONTACT = "isContact";
    public static String FIREBASE_FIELD_WASALREADYPLAYED = "wasAlreadyPlayed";
    public static String FIREBASE_FIELD_DURATION = "duration";
    public static String FIREBASE_FIELD_AUDIOID = "audioId";
    public static String FIREBASE_FIELD_AUDIOFILEURL = "audioFileUrl";
    //
    public static String FIREBASE_FIELD_CASEID = "caseId";
    public static String FIREBASE_FIELD_DATETIME = "datetime";
    public static String FIREBASE_FIELD_MARKETINGPHONE = "marketingPhone";
    public static String FIREBASE_FIELD_STATUSID = "statusId";
    public static String FIREBASE_FIELD_STATUSNAME = "statusName";
    public static String FIREBASE_FIELD_COMMENTTEXT = "commentText";

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


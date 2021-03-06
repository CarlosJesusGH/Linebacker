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
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "cmsys_db.sqlite";
    public static final String DB_PATH = "/data/data/com.cmsys/databases/";

    //---App permission request constants-----
    public static final int MY_PERMISSIONS_REQUEST_ID = 101;

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
    public final static String BUNDLE_EXTRA_CALLING_ACTIVITY = "calling_activity";
    public final static String BUNDLE_EXTRA_SIP_IS_INTERNAL_CALL = "isInternalCall";

    //---WS Sync-----------------
    public static String SYNC_WS_IP = "cj-website.ddns.net";
    public static String SYNC_WS_PORT = "8080";
    public static String SYNC_WS_DNS = "linebacker.firebaseio.com";
    public static String SYNC_WS_COMMON_PATH = "https://" + SYNC_WS_DNS + "/sampleRestfulWS/";
    public static String SYNC_WS_ASTERISk_GET_USER_DATA = SYNC_WS_COMMON_PATH + "asteriskGetNewUserData.json";
    public static String SYNC_WS_ASTERISk_TEST_VOICE_MAIL = SYNC_WS_COMMON_PATH + "asteriskTestVoicemailSetup.json";
    //    public static String SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER = SYNC_WS_COMMON_PATH + "asteriskUpdateContactsTrigger.json";
    public static String PBX_SIP_DOMAIN = "voip.mylinebacker.net";
    public static String PBX_SIP_SERVER_HOST = "voip.mylinebacker.net";
    public static int PBX_SIP_SERVER_PORT = 5060;
    //
    public static String SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER = "http://www.privacyprotector.org/api/contactsByUser/store/all/";
    public static String SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER = "http://www.privacyprotector.org/api/recordedAudiosByUser/audio/";
    public static String SYNC_WS_LOGIN_API = "http://www.privacyprotector.org/api/login";
    public static String SYNC_WS_REGISTER_API = "http://www.privacyprotector.org/api/register";
    public static String SYNC_WS_PBX_ACCOUNT_API = "http://www.privacyprotector.org/api/account";
    public static String SYNC_WS_ASTERISk_REMOVE_LOG = "http://www.privacyprotector.org/api/deleteAudio/";
    public static String SYNC_WS_UPLOAD_AUDIO_API = "http://www.privacyprotector.org/api/voicemail";
    public static String SYNC_WS_REPORT_CASE_API = "http://www.privacyprotector.org/api/filingacase";
    //
    public static String SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER_DEV = "http://linebacker.privacyprotector.org/api/contactsByUser/store/all/";
    public static String SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER_DEV = "http://linebacker.privacyprotector.org/api/recordedAudiosByUser/audio/";
    public static String SYNC_WS_LOGIN_API_DEV = "http://linebacker.privacyprotector.org/api/login";
    public static String SYNC_WS_REGISTER_API_DEV = "http://linebacker.privacyprotector.org/api/register";
    public static String SYNC_WS_PBX_ACCOUNT_API_DEV = "http://linebacker.privacyprotector.org/api/account";
    public static String SYNC_WS_ASTERISk_REMOVE_LOG_DEV = "http://linebacker.privacyprotector.org/api/deleteAudio/";
    public static String SYNC_WS_UPLOAD_AUDIO_API_DEV = "http://linebacker.privacyprotector.org/api/voicemail";
    public static String SYNC_WS_REPORT_CASE_API_DEV = "http://linebacker.privacyprotector.org/api/filingacase";
    //
    public static String SYNC_WS_ASTERISk_UPDATE_CONTACTS_TRIGGER_PROD = "http://www.privacyprotector.org/api/contactsByUser/store/all/";
    public static String SYNC_WS_ASTERISk_UPDATE_RECORDINGS_TRIGGER_PROD = "http://www.privacyprotector.org/api/recordedAudiosByUser/audio/";
    public static String SYNC_WS_LOGIN_API_PROD = "http://www.privacyprotector.org/api/login";
    public static String SYNC_WS_REGISTER_API_PROD = "http://www.privacyprotector.org/api/register";
    public static String SYNC_WS_PBX_ACCOUNT_API_PROD = "http://www.privacyprotector.org/api/account";
    public static String SYNC_WS_ASTERISk_REMOVE_LOG_PROD = "http://www.privacyprotector.org/api/deleteAudio/";
    public static String SYNC_WS_UPLOAD_AUDIO_API_PROD = "http://www.privacyprotector.org/api/voicemail";
    public static String SYNC_WS_REPORT_CASE_API_PROD = "http://www.privacyprotector.org/api/filingacase";

    //---Firebase------------------
    public static String FIREBASE_APP_NAME = "linebacker";
    public static String FIREBASE_APP_NAME_DEV = "linebacker-dev";
    public static String FIREBASE_APP_NAME_PROD = "linebacker";
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
    public static String AUDIO_FILE_NAME = "unavail.mp3";
    public static String AUDIO_GREETING_FILE_NAME = "recordedAudio.mp3";

    //-----------------------------

    public static int URL_CONNECT_TIMEOUT = 1000;
}


package com.cmsys.linebacker.util.upload_file;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//import util.project.CONSTANTS;
//import util.project.upload.ContentType;
//import util.project.upload.ServiceUpload;
//import util.project.upload.UploadRequest;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.DateUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.upload_file.UploadRequest;
import com.cmsys.linebacker.util.upload_file.ContentType;
import com.cmsys.linebacker.util.upload_file.ServiceUpload;
import com.cmsys.linebacker.util.CONSTANTS;

/**
 * Created by CarlosJesus on 2014-08-20.
 */
public class UtilUploader {

    public static void onUploadButtonClick(Context pContext, String pUrl, String filePath, HashMap<String,String> pPostParameters){
//        final String serverUrlString = serverUrl.getText().toString();
//        final String fileToUploadPath = fileToUpload.getText().toString();
//        final String paramNameString = parameterName.getText().toString();

//        final String serverUrlString = "http://192.168.0.104:8080/HelloWorld/jaxrs/CarrentalUploadPdf/upload";
//        final String serverUrlString = "http://cj-website.ddns.net:8080/HelloWorld/jaxrs/CarrentalUploadPdf/upload";
//        final String serverUrlString = CONSTANTS.SYNC_WS_COMMON_PATH + CONSTANTS.SYNC_WS_METHOD_UPLOAD;
        final String serverUrlString = pUrl;
        final String fileNameOnServer = DateUtils.getDateTimeString(DateUtils.getNow()).replace("-","").replace(":","").replace(" ","_") + "_" + CONSTANTS.PDF_FILE_NAME;
        //final String fileToUploadPath = CONSTANTS.PATH_ROOT_APP_FOLDER + File.separator + CONSTANTS.FOLDER_NAME_DOCUMENTS + File.separator + CONSTANTS.PDF_FILE_NAME;
        final String fileToUploadPath = filePath;
        final String paramNameString = "file";

        if (false)//!userInputIsValid(serverUrlString, fileToUploadPath, paramNameString))
            return;
        final UploadRequest request = new UploadRequest(pContext, UUID.randomUUID().toString(), serverUrlString);
        request.addFileToUpload(fileToUploadPath, paramNameString, fileNameOnServer, ContentType.APPLICATION_OCTET_STREAM);
        if(pPostParameters != null && pPostParameters.size() > 0) {
            for (Map.Entry<String, String> entry : pPostParameters.entrySet()) {
                request.addParameter(entry.getKey(), entry.getValue());
            }
        }

//        request.setNotificationConfig(R.drawable.ic_launcher, getString(R.string.app_name),getString(R.string.uploading), getString(R.string.upload_success), getString(R.string.upload_error), false);
        request.setNotificationConfig(R.mipmap.ic_helmet, pContext.getString(R.string.app_name),"Uploading", "Upload successful", "Error while uploading", false);
        try {
            ServiceUpload.startUpload(request);
            //IntentServiceUpload.startUpload(request, (ImageView) fView);

//            Intent intent = new Intent(pContext, ServiceTest.class);
//            pContext.startService(intent);

//            final Intent intent = new Intent(mContext, UploadService.class);
//            mContext.startService(intent);
        } catch (Exception exc) {
            //Toast.makeText(this, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            MessageUtils.toast(pContext, "Malformed upload request. ", false);
        }
    }

}

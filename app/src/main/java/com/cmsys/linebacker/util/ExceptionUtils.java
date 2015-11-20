package com.cmsys.linebacker.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

/**
 * Created by cj on 19/11/15.
 */
public class ExceptionUtils {

    // Show error message
    public static void displayExceptionMessage(Activity pActivity, Exception pE){
        try{
            LogUtils.LOGD("TAG", "MSG", pE);
            MessageUtils um = new MessageUtils(pActivity, "Exception Message", pE.getMessage(), 0, true);
        } catch (Exception e){
            LogUtils.LOGD("TAG", "MSG", pE);
            MessageUtils um = new MessageUtils(pActivity, "CAN'T Show Exception", e.getMessage(), 0, true);
        }
    }

    // Show error message
    public static void displayExceptionMessage(Context pContext, Exception pE){
        try{
            LogUtils.LOGD("TAG", "MSG", pE);
            MessageUtils.toast(pContext, pE.getMessage(), true);
        } catch (Exception e){
            LogUtils.LOGD("TAG", "MSG", pE);
            MessageUtils.toast(pContext, e.getMessage(), true);
        }
    }

    // Print to file
    public static void printExceptionToFile(Context pContext, Exception pE){
        PrintStream ps = null;
        try{
            File file = new File(CONSTANTS.PATH_ROOT_APP_FOLDER + File.separator + CONSTANTS.FOLDER_NAME_LOGS + File.separator + CONSTANTS.LOG_FILE_NAME);
            if(!file.exists()){
                file.createNewFile();
            }
            ps= new PrintStream(file);
            pE.printStackTrace(ps);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(ps!=null) ps.close();
    }

}

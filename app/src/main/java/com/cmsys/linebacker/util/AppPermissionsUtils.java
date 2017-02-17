package com.cmsys.linebacker.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.cmsys.linebacker.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by cj on 29/12/16.
 */

public class AppPermissionsUtils {

    //Manifest.permission.READ_CONTACTS
    //Manifest.permission.WRITE_CALENDAR

    public static boolean checkPermissions(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    public static boolean checkPermissionsAndRequest(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(activity, Manifest.permission.READ_CONTACTS);
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(activity, Manifest.permission.RECORD_AUDIO);
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(activity, Manifest.permission.READ_PHONE_STATE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    public static void requestPermission(Activity activity, String permission){
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                activity.requestPermissions(new String[]{permission}, CONSTANTS.MY_PERMISSIONS_REQUEST_ID);
        } else {
            // No explanation needed, we can request the permission.

            //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, CONSTANTS.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                activity.requestPermissions(new String[]{permission}, CONSTANTS.MY_PERMISSIONS_REQUEST_ID);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

    }

    public static boolean mayRequestContacts(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (activity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (activity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            //Snackbar.make(viewForPermMsg, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
            //        .setAction(android.R.string.ok, new View.OnClickListener() {
            //            @Override
            //            @TargetApi(Build.VERSION_CODES.M)
            //            public void onClick(View v) {
            //                activity.requestPermissions(new String[]{READ_CONTACTS}, CONSTANTS.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            //            }
            //        });
        } else {
            activity.requestPermissions(new String[]{READ_CONTACTS}, CONSTANTS.MY_PERMISSIONS_REQUEST_ID);
        }
        return false;
    }
}

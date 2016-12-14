package com.cmsys.linebacker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.cmsys.linebacker.R;
//import com.facebook.share.model.ShareLinkContent;
//import com.facebook.share.widget.ShareDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by CarlosJesusGH on 2016-01-13.
 */
public class IntentUtils {
    public static final String PACKAGE_WHATSAPP = "com.whatsapp";
    public static final String PACKAGE_FACEBOOK = "";
    public static final String PACKAGE_LINEBACKER = "com.cmsys.linebacker";

    public static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    //public static void generateFacebookKeyHash(Context context) {
    //    try {
    //        PackageInfo info = context.getPackageManager().getPackageInfo(PACKAGE_LINEBACKER, PackageManager.GET_SIGNATURES);
    //        for (Signature signature : info.signatures) {
    //            MessageDigest md = MessageDigest.getInstance("SHA");
    //            md.update(signature.toByteArray());
    //            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
    //        }
    //    } catch (PackageManager.NameNotFoundException e) {
    //        e.printStackTrace();
    //    } catch (NoSuchAlgorithmException e) {
    //        e.printStackTrace();
    //    }
    //}

    public static void shareMessageToApp(Context context, String message, String packageApp){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        if(IntentUtils.isAppInstalled(context, packageApp))
            sendIntent.setPackage(packageApp);
        else
            MessageUtils.toast(context, context.getString(R.string.error_app_not_installed), false);
        context.startActivity(sendIntent);
    }

    public static void shareMessageToWhatsapp(Context context, String message){
        shareMessageToApp(context, message, PACKAGE_WHATSAPP);
    }

    //public static void shareMessageToFacebook(Activity activity, String title, String description, String url){
    //    ShareLinkContent content = new ShareLinkContent.Builder()
    //            .setContentTitle(title)
    //            .setContentDescription(description)
    //            .setContentUrl(Uri.parse(url))
    //            //.setImageUrl(Uri.parse("android.resource://" + PACKAGE_LINEBACKER + " / " + R.mipmap.ic_launcher))    // Didn't work
    //            .setImageUrl(Uri.parse("https://dl.dropboxusercontent.com/u/18586179/Linebacker/ic_linebacker.png"))
    //            .build();
    //    ShareDialog shareDialog = new ShareDialog(activity);
    //    shareDialog.show(content);
    //}
}

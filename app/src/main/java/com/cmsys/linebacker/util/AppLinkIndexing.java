package com.cmsys.linebacker.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by @CarlosJesusGH on 30/06/16.
 */
public class AppLinkIndexing {

    public static String checkIfIsLinkIndexing(Intent intent) {
        // AppIndexing get url
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String lastPathSegment = data.getLastPathSegment();
            String returnValue = data.toString();
            // Clear all intent's data
            intent.replaceExtras(new Bundle());
            intent.setAction("");
            intent.setData(null);
            intent.setFlags(0);
            // Return only data value.
            return returnValue;
        }
        return null;
    }

}

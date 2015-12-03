package com.cmsys.linebacker.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by cj on 01/12/15.
 */
public class PhoneContactUtils {

    public static boolean contactPhoneExists(Context pContext, String pPhoneNumber) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(pPhoneNumber));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        // Get cursor query
        Cursor cur = pContext.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }



}

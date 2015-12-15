package com.cmsys.linebacker.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        Cursor cur = pContext.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
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

    public static void getPhoneContacts(Context pContext) {
//        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(pPhoneNumber));
//        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        // Get cursor query
        ContentResolver cr = pContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString( cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString( cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Do something
                }
            }
        }
    }

    public static HashMap<String, HashMap<String, List<String>>> getPhoneContactsHashMap(Context pContext) {
        // Get cursor query
        HashMap<String, HashMap<String, List<String>>> hmContacts = new HashMap<>();
        ContentResolver cr = pContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString( cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString( cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    List<String> phoneNumbers = getPhoneNumbersById(pContext, id);
                    HashMap<String, List<String>> hmPhones = new HashMap<>();
                    hmPhones.put("phones", phoneNumbers);
                    hmContacts.put(id, hmPhones);
                }
            }
        }
        cur.close();
        return hmContacts;
    }

    public static List<String> getPhoneNumbersById(Context pContext, String id) {
        // Get phone numbers
        ContentResolver cr = pContext.getContentResolver();
        Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
        List<String> list = new ArrayList<>();
        if (phoneCur.getCount() > 0) {
            while (phoneCur.moveToNext()) {
                String number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                if (number != null)
                    list.add(number);
            }
        }
        phoneCur.close();
        return list;
    }

    public static List<String> getEmailsById(Context pContext, String id) {
        // Get emails
        ContentResolver cr = pContext.getContentResolver();
        Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
        List<String> list = new ArrayList<>();
        if (emailCur.getCount() > 0) {
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                if (email != null)
                    list.add(email);
            }
        }
        emailCur.close();
        return list;
    }

    public static List<String> getListAllPhoneNumbers(Context pContext) {
        // Get phone numbers
        ContentResolver cr = pContext.getContentResolver();
        Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        List<String> list = new ArrayList<>();
        if (phoneCur.getCount() > 0) {
            while (phoneCur.moveToNext()) {
                String number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                if (number != null)
                    list.add(number);
            }
        }
        phoneCur.close();
        return list;
    }

    public static List<String> getListOfAllEmails(Context pContext) {
        // Get emails
        ContentResolver cr = pContext.getContentResolver();
        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                //ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", //new String[]{id}
                null, null, null);
        List<String> list = new ArrayList<>();
        if (emailCur.getCount() > 0) {
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                if (email != null)
                    list.add(email);
            }
        }
        emailCur.close();
        return list;
    }

}

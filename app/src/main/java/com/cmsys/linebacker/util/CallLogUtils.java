package com.cmsys.linebacker.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import java.util.Date;

/**
 * Created by CarlosJesusGH on 5/12/16.
 */

public class CallLogUtils {

    public static String getCallDetails(Context pContext) {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = pContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        return sb.toString();
    }

    public static boolean existInOutgoingCalls(Context context, String phoneNumber) {
        String selection = CallLog.Calls.TYPE + " =? AND " + CallLog.Calls.NUMBER + " LIKE ?";
        String[] selectionArgs = new String[]{Integer.toString(CallLog.Calls.OUTGOING_TYPE), phoneNumber};
        // Perform query using selection arguments
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                selection, selectionArgs, null);
        // Check if any result otherwise return null
        if (!(cursor.moveToFirst()) || cursor.getCount() ==0)
            return false;
        return true;
    }
}

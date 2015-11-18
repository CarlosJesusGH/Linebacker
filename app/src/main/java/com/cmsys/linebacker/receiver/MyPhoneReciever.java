package com.cmsys.linebacker.receiver;

/*
 *  Copyright 2012 Kobi Krasnoff
 *
 * This file is part of Call recorder For Android.

    Call recorder For Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Call recorder For Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Call recorder For Android.  If not, see <http://www.gnu.org/licenses/>
 */
//package com.call.recorder;

import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.cmsys.linebacker.service.RecordService;


public class MyPhoneReciever extends BroadcastReceiver { //extends DeviceAdminReceiver{//

    public static final String LISTEN_ENABLED = "ListenEnabled";
    //public static final String FILE_DIRECTORY = "recordedCalls";
    private String phoneNumber;
    public static final int STATE_INCOMING_NUMBER = 0;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;
    //private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    //private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    //
    public boolean wasRinging = false;

    /*@Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }*/

    /*public void onEnabled(Context context, Intent intent) {};

    public void onDisabled(Context context, Intent intent) {};*/

    @Override
    public void onReceive(Context context, Intent intent) {
        //super.onReceive(context, intent);
        //
        SharedPreferences settings = context.getSharedPreferences(LISTEN_ENABLED, 0);
        boolean silent = settings.getBoolean("silentMode", true);
        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);


        //if (silent && MainActivity.updateExternalStorageState() == MainActivity.MEDIA_MOUNTED)
        //if(intent.getAction().equals(ACTION_IN)){
        if(true){
            if (phoneNumber == null){
                if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    //if (wasRinging == true) {
                        Toast.makeText(context, "Answered: " + phoneNumber, Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(context, RecordService.class);
                        myIntent.putExtra("commandType", STATE_CALL_START);
                        myIntent.putExtra("phoneNumber", phoneNumber);
                        context.startService(myIntent);
                    //}
                }
                else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    Toast.makeText(context, "Idle", Toast.LENGTH_LONG).show();
                    //wasRinging = false;
                    Intent myIntent = new Intent(context, RecordService.class);
                    myIntent.putExtra("commandType", STATE_CALL_END);
                    context.startService(myIntent);
                }
                else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    //wasRinging = true;
                    if (phoneNumber == null)
                        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Toast.makeText(context, "Ringing: " + phoneNumber, Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(context, RecordService.class);
                    myIntent.putExtra("commandType", STATE_INCOMING_NUMBER);
                    myIntent.putExtra("phoneNumber", phoneNumber);
                    context.startService(myIntent);
                }
            } else{
                Toast.makeText(context, "IncomingNumber!=null: ", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(context, RecordService.class);
                myIntent.putExtra("commandType", TelephonyManager.EXTRA_INCOMING_NUMBER);
                myIntent.putExtra("phoneNumber", phoneNumber);
                context.startService(myIntent);
            }

        } /*else if(intent.getAction().equals(ACTION_OUT)){
            Toast.makeText(context, "Outgoing call", Toast.LENGTH_LONG).show();
        }*/
    }
}
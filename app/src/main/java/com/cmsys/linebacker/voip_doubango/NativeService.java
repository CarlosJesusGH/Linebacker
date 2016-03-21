/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.cmsys.linebacker.voip_doubango;

//import org.doubango.imsdroid.Screens.ScreenAV;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnNativeService;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.events.NgnMsrpEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventTypes;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.model.NgnHistorySMSEvent;
import org.doubango.ngn.model.NgnHistoryEvent.StatusType;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnMsrpSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnDateTimeUtils;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.cmsys.linebacker.R;
import com.cmsys.linebacker.util.DateUtils;
import com.cmsys.linebacker.util.MessageUtils;
import com.cmsys.linebacker.util.SharedPreferencesUtils;
import com.cmsys.linebacker.voip_doubango.Engine;
import com.cmsys.linebacker.voip_doubango.IMSDroid;

public class NativeService extends NgnNativeService {
    private final static String TAG = NativeService.class.getCanonicalName();
    public static final String ACTION_STATE_EVENT = TAG + ".ACTION_STATE_EVENT";

    private PowerManager.WakeLock mWakeLock;
    private BroadcastReceiver mBroadcastReceiver;
    private Engine mEngine;

    public NativeService() {
        super();
        mEngine = (Engine) Engine.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && mWakeLock == null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.ON_AFTER_RELEASE
                    | PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart()");
        //MessageUtils.notification(getApplicationContext(), "NativeService - Started", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
        // register()
        mBroadcastReceiver = new BroadcastReceiver() {
            @SuppressWarnings("incomplete-switch")
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Events
                if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                    NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    final NgnRegistrationEventTypes type;
                    if (args == null) {
                        Log.e(TAG, "Invalid event args");
                        return;
                    }
                    switch ((type = args.getEventType())) {
                        case REGISTRATION_OK:
                        case REGISTRATION_NOK:
                        case REGISTRATION_INPROGRESS:
                        case UNREGISTRATION_INPROGRESS:
                        case UNREGISTRATION_OK:
                        case UNREGISTRATION_NOK:
                        default:
                            final boolean bTrying = (type == NgnRegistrationEventTypes.REGISTRATION_INPROGRESS || type == NgnRegistrationEventTypes.UNREGISTRATION_INPROGRESS);
                            if (mEngine.getSipService().isRegistered()) {
                                mEngine.showAppNotif(bTrying ? R.drawable.icon : R.drawable.icon, null);
                                IMSDroid.acquirePowerLock();
                            } else {
                                mEngine.showAppNotif(bTrying ? R.drawable.icon : R.drawable.icon, null);
                                IMSDroid.releasePowerLock();
                            }
                            break;
                    }
                }

                // PagerMode Messaging Events
                if (NgnMessagingEventArgs.ACTION_MESSAGING_EVENT.equals(action)) {
                    NgnMessagingEventArgs args = intent.getParcelableExtra(NgnMessagingEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e(TAG, "Invalid event args");
                        return;
                    }
                    switch (args.getEventType()) {
                        case INCOMING:
                            String dateString = intent.getStringExtra(NgnMessagingEventArgs.EXTRA_DATE);
                            String remoteParty = intent.getStringExtra(NgnMessagingEventArgs.EXTRA_REMOTE_PARTY);
                            if (NgnStringUtils.isNullOrEmpty(remoteParty)) {
                                remoteParty = NgnStringUtils.nullValue();
                            }
                            remoteParty = NgnUriUtils.getUserName(remoteParty);
                            NgnHistorySMSEvent event = new NgnHistorySMSEvent(remoteParty, StatusType.Incoming);
                            event.setContent(new String(args.getPayload()));
                            event.setStartTime(NgnDateTimeUtils.parseDate(dateString).getTime());
                            mEngine.getHistoryService().addEvent(event);
                            mEngine.showSMSNotif(R.drawable.icon, "New message");
                            break;
                    }
                }

                // MSRP chat Events
                // For performance reasons, file transfer events will be handled by the owner of the context
                if (NgnMsrpEventArgs.ACTION_MSRP_EVENT.equals(action)) {
                    NgnMsrpEventArgs args = intent.getParcelableExtra(NgnMsrpEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e(TAG, "Invalid event args");
                        return;
                    }
                    switch (args.getEventType()) {
                        case DATA:
                            final NgnMsrpSession session = NgnMsrpSession.getSession(args.getSessionId());
                            if (session == null) {
                                Log.e(TAG, "Failed to find MSRP session with id=" + args.getSessionId());
                                return;
                            }
                            final byte[] content = intent.getByteArrayExtra(NgnMsrpEventArgs.EXTRA_DATA);
                            NgnHistorySMSEvent event = new NgnHistorySMSEvent(NgnUriUtils.getUserName(session.getRemotePartyUri()), StatusType.Incoming);
                            event.setContent(content == null ? NgnStringUtils.nullValue() : new String(content));
                            mEngine.getHistoryService().addEvent(event);
                            mEngine.showSMSNotif(R.drawable.icon, "New message");
                            break;
                    }
                }

                // Invite Events
                else if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
                    NgnInviteEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e(TAG, "Invalid event args");
                        return;
                    }

                    final NgnMediaType mediaType = args.getMediaType();

                    switch (args.getEventType()) {
                        case TERMWAIT:
                        case TERMINATED:
                            if (NgnMediaType.isAudioVideoType(mediaType)) {
                                mEngine.refreshAVCallNotif(R.drawable.icon);
                                mEngine.getSoundService().stopRingBackTone();
                                mEngine.getSoundService().stopRingTone();
                            } else if (NgnMediaType.isFileTransfer(mediaType)) {
                                mEngine.refreshContentShareNotif(R.drawable.icon);
                            } else if (NgnMediaType.isChat(mediaType)) {
                                mEngine.refreshChatNotif(R.drawable.icon);
                            }
                            break;

                        case INCOMING:
                            if (NgnMediaType.isAudioVideoType(mediaType)) {
                                final NgnAVSession avSession = NgnAVSession.getSession(args.getSessionId());
                                if (avSession != null) {
                                    mEngine.showAVCallNotif(R.drawable.icon, getString(R.string.string_call_incoming));
                                    //ScreenAV.receiveCall(avSession);
                                    DoubangoUtils.receiveCall(avSession);
                                    if (mWakeLock != null && !mWakeLock.isHeld()) {
                                        mWakeLock.acquire(10);
                                    }
                                    mEngine.getSoundService().startRingTone();
                                } else {
                                    Log.e(TAG, String.format("Failed to find session with id=%ld", args.getSessionId()));
                                }
                            } else if (NgnMediaType.isFileTransfer(mediaType)) {
                                mEngine.refreshContentShareNotif(R.drawable.icon);
                                if (mWakeLock != null && !mWakeLock.isHeld()) {
                                    mWakeLock.acquire(10);
                                }
                            } else if (NgnMediaType.isChat(mediaType)) {
                                mEngine.refreshChatNotif(R.drawable.icon);
                                if (mWakeLock != null && !mWakeLock.isHeld()) {
                                    mWakeLock.acquire(10);
                                }
                            }
                            break;

                        case INPROGRESS:
                            if (NgnMediaType.isAudioVideoType(mediaType)) {
                                mEngine.showAVCallNotif(R.drawable.icon, getString(R.string.string_call_outgoing));
                            } else if (NgnMediaType.isFileTransfer(mediaType)) {
                                mEngine.refreshContentShareNotif(R.drawable.icon);
                            } else if (NgnMediaType.isChat(mediaType)) {
                                mEngine.refreshChatNotif(R.drawable.icon);
                            }
                            break;

                        case RINGING:
                            if (NgnMediaType.isAudioVideoType(mediaType)) {
                                mEngine.getSoundService().startRingBackTone();
                            } else if (NgnMediaType.isFileTransfer(mediaType)) {
                                mEngine.refreshContentShareNotif(R.drawable.icon);
                            } else if (NgnMediaType.isChat(mediaType)) {
                                mEngine.refreshChatNotif(R.drawable.icon);
                            }
                            break;

                        case CONNECTED:
                        case EARLY_MEDIA:
                            if (NgnMediaType.isAudioVideoType(mediaType)) {
                                mEngine.showAVCallNotif(R.drawable.icon, getString(R.string.string_incall));
                                mEngine.getSoundService().stopRingBackTone();
                                mEngine.getSoundService().stopRingTone();
                            } else if (NgnMediaType.isFileTransfer(mediaType)) {
                                mEngine.refreshContentShareNotif(R.drawable.icon);
                            } else if (NgnMediaType.isChat(mediaType)) {
                                mEngine.refreshChatNotif(R.drawable.icon);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
        intentFilter.addAction(NgnMsrpEventArgs.ACTION_MSRP_EVENT);
        registerReceiver(mBroadcastReceiver, intentFilter);

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.getBoolean("autostarted")) {
                // TestCJ (not necessary) //if (bundle != null && bundle.getBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART.toString(), NgnConfigurationEntry.DEFAULT_GENERAL_AUTOSTART)) {
                if (mEngine.start()) {
                    if (mEngine.getSipService().register(null))
                        //MessageUtils.notification(getApplicationContext(), "NativeService - Sip service registered succeed", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                        MessageUtils.toast(getApplicationContext(), "NativeService - Sip service registered succeed", false);
                    else
                        MessageUtils.notification(getApplicationContext(), "NativeService - Problem while sip registering", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                } else
                    MessageUtils.notification(getApplicationContext(), "NativeService - Problem while Engine start", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
            } /*else { // bundle == null // TestCJ (always reconnecting battery drops)
                MessageUtils.notification(getApplicationContext(), "NativeService - Bundle==null or without autostart", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                if(SharedPreferencesUtils.checkIfContainsKey(getApplicationContext(), getString(R.string.pref_key_voip_extension))){
                    if (mEngine.start()) {
                        if (mEngine.getSipService().register(null))
                            MessageUtils.notification(getApplicationContext(), "NativeService - Sip service registered succeed", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                        else
                            MessageUtils.notification(getApplicationContext(), "NativeService - Problem while sip registering", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                    } else
                        MessageUtils.notification(getApplicationContext(), "NativeService - Problem while Engine start", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                }
            }*/
        } /*else { // intent == null
            MessageUtils.notification(getApplicationContext(), "NativeService - Intent==null", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
            if(SharedPreferencesUtils.checkIfContainsKey(getApplicationContext(), getString(R.string.pref_key_voip_extension))){
                if (mEngine.start()) {
                    if (mEngine.getSipService().register(null))
                        MessageUtils.notification(getApplicationContext(), "NativeService - Sip service registered succeed", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                    else
                        MessageUtils.notification(getApplicationContext(), "NativeService - Problem while sip registering", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
                } else
                    MessageUtils.notification(getApplicationContext(), "NativeService - Problem while Engine start", DateUtils.getDateTimeString(System.currentTimeMillis()), (int) System.currentTimeMillis(), null, null, false, null, true);
            }
        }*/

        // alert()
        final Intent i = new Intent(ACTION_STATE_EVENT);
        i.putExtra("started", true);
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
                mWakeLock = null;
            }
        }
        super.onDestroy();
    }
}

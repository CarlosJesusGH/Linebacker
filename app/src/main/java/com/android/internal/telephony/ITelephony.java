package com.android.internal.telephony;

/**
 * Created by cj on 02/12/15.
 */
public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();

    boolean endCallForSubscriber(int subId);
}
package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
import com.cmsys.linebacker.util.ExceptionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CarlosJesusGH on 27/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class RecordingBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String audioId;
    private String phoneNumber;
    private Object datetime;
    private String duration;
    private String audioFileUrl;
    private String contactName;
    private boolean isOnCase;
    private boolean isContact;
    private boolean wasAlreadyPlayed;

    public RecordingBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_PHONENUMBER, this.getPhoneNumber());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DATETIME, this.getDatetime());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DURATION, this.getDuration());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_ISONCASE, this.isOnCase());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_ISCONTACT, this.isContact());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_AUDIOFILEURL, this.getAudioFileUrl());
        return fieldsMap;
    }

    @JsonIgnore
    public static Map<String, Map<String, Object>> getTestRecordingsMap(String userId) {
        Map<String, Map<String, Object>> objectsMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            //Map<String, Object> fieldsMap = new HashMap<>();
            RecordingBean recordingBean = new RecordingBean();
            recordingBean.setKey("test_audio_" + Integer.toString(i) + "_" + userId);
            recordingBean.setPhoneNumber(Integer.toString(i) + Integer.toString(i) + Integer.toString(i) + "-" + Integer.toString(i) + Integer.toString(i) + Integer.toString(i));
            recordingBean.setDatetime(ServerValue.TIMESTAMP);
            recordingBean.setDuration("00:05:00");
            recordingBean.setIsOnCase(false);
            recordingBean.setIsContact(false);
            recordingBean.setAudioFileUrl("http://dl.dropboxusercontent.com/u/18586179/Linebacker/animal_" + Integer.toString(i) + ".mp3");
            objectsMap.put(recordingBean.getKey(), recordingBean.getObjectMap());
        }
        return objectsMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Object getDatetime() {
        return datetime;
    }

    public long getDatetimeLong() {
        if (datetime instanceof Double)
            return (long) ((double) datetime);
        if (datetime instanceof String)
            return (long) Double.parseDouble((String) datetime);
        if (datetime instanceof Long)
            return (long) datetime;
        return (long) datetime;
    }

    public String getDatetimeString() {
        try {
            if (datetime instanceof Long)
                return DateUtils.getDateTimeString((long) datetime);
            if (datetime instanceof Double)
                return DateUtils.getDateTimeString((long) ((double) datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) datetime;
    }

    public String getDateString() {
        try {
            if (datetime instanceof Long)
                return DateUtils.getDateString((long) datetime);
            if (datetime instanceof Double)
                return DateUtils.getDateString((long) ((double) datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) datetime;
    }

    public String getTimeString() {
        try {
            if (datetime instanceof Long)
                return DateUtils.getTimeString((long) datetime);
            if (datetime instanceof Double)
                return DateUtils.getTimeString((long) ((double) datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) datetime;
    }

    public void setDatetime(Object datetime) {
        this.datetime = datetime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAudioFileUrl() {
        return audioFileUrl;
    }

    public void setAudioFileUrl(String audioFileUrl) {
        this.audioFileUrl = audioFileUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isOnCase() {
        return isOnCase;
    }

    public void setIsOnCase(boolean isOnCase) {
        this.isOnCase = isOnCase;
    }

    public boolean isContact() {
        return isContact;
    }

    public void setIsContact(boolean isContact) {
        this.isContact = isContact;
    }

    public boolean wasAlreadyPlayed() {
        return wasAlreadyPlayed;
    }

    public void setWasAlreadyPlayed(boolean wasAlreadyPlayed) {
        this.wasAlreadyPlayed = wasAlreadyPlayed;
    }
}

package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CarlosJesusGH on 04/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class CaseBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String audioId;
    private String caseId;
    private Object datetime;
    private String marketingPhone;
    private String statusId;
    private String phoneNumber;

    public CaseBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public CaseBean(RecordingBean recordingBean){
        this.key = recordingBean.getKey();
        this.audioId = recordingBean.getKey();
        this.caseId = recordingBean.getKey();
        this.datetime = recordingBean.getDatetime();
        this.marketingPhone = recordingBean.getPhoneNumber();
        this.statusId = "0";
        this.phoneNumber = "UserPhoneNumber";
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_AUDIOID, this.getAudioId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_CASEID, this.getCaseId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DATETIME, this.getDatetime());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_MARKETINGPHONE, this.getMarketingPhone());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_STATUSID, this.getStatusId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_PHONENUMBER, this.getPhoneNumber());
        return fieldsMap;
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

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getMarketingPhone() {
        return marketingPhone;
    }

    public void setMarketingPhone(String marketingPhone) {
        this.marketingPhone = marketingPhone;
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

    public String getDatetimeString() {
        try {
            return DateUtils.getDateTimeString(Long.parseLong((String) datetime));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return (String) datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
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

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}

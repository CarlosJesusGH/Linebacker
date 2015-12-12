package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cj on 04/12/15.
 */
// @JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class CaseBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String audioId;
    private String caseId;
    private String datetime;
    private String marketingPhone;
    private String statusId;
    private String userPhone;

    public CaseBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public CaseBean(RecordingBean recordingBean){
        this.key = recordingBean.getAudioId();
        this.audioId = recordingBean.getAudioId();
        this.caseId = recordingBean.getAudioId();
        this.datetime = recordingBean.getDatetime();
        this.marketingPhone = recordingBean.getPhoneNumber();
        this.statusId = "0";
        this.userPhone = "UserPhoneNumber";
    }

    @JsonIgnore
    public Map<String, String> getObjectMap(){
        Map<String, String> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_AUDIOID, this.getAudioId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_CASEID, this.getCaseId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DATETIME, this.getDatetime());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_MARKETINGPHONE, this.getMarketingPhone());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_STATUSID, this.getStatusId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERPHONE, this.getUserPhone());
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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}

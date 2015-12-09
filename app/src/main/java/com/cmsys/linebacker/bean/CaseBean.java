package com.cmsys.linebacker.bean;

import java.io.Serializable;

/**
 * Created by cj on 04/12/15.
 */
// @JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class CaseBean implements Serializable {
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

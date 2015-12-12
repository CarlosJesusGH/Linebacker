package com.cmsys.linebacker.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by cj on 27/11/15.
 */
// @JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class RecordingBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String audioId;
    private String phoneNumber;
    private String datetime;
    private String duration;
    private boolean isOnCase;

    public RecordingBean(){
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

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isOnCase() {
        return isOnCase;
    }

    public void setIsOnCase(boolean isOnCase) {
        this.isOnCase = isOnCase;
    }
}

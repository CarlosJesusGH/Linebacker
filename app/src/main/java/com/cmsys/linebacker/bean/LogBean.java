package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cj on 04/12/15.
 */
// @JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class LogBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private Object datetime;
    private Object statusId;

    public LogBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public LogBean(Object timeStamp, Object statusId) {
        this.datetime = timeStamp;
        this.statusId = statusId;
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DATETIME, this.getDatetime());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_STATUSID, this.getStatusId());
        return fieldsMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Object getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}

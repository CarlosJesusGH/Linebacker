package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cj on 04/12/15.
 */
// @JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class LogBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String datetime;
    private String statusId;

    public LogBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DATETIME, DateUtils.getDateTimeString(DateUtils.getNow()));
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_STATUSID, 0);
        return fieldsMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

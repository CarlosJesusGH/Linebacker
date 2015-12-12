package com.cmsys.linebacker.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

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

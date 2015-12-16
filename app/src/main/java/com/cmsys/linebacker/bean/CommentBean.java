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
public class CommentBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String commentText;
    private Object datetime;
    private String userId;

    public CommentBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public CommentBean(String userId, Object datetime, String commentText) {
        this.userId = userId;
        this.datetime = datetime;
        this.commentText = commentText;
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_DATETIME, this.getDatetime());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERID, this.getUserId());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_COMMENTTEXT, this.getCommentText());
        return fieldsMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

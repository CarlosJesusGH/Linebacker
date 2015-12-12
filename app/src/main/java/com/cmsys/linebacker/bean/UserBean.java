package com.cmsys.linebacker.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by cj on 22/11/15.
 */
public class UserBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String mUserId;
    private String mUserEmail;
    private String mUserUsername;
    private String mUserPassword;
    private String mUserFirstName;
    private String mUserLastName;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public String getUserUsername() {
        return mUserUsername;
    }

    public String getUserPassword() {
        return mUserPassword;
    }

    public String getUserFirstName() {
        return mUserFirstName;
    }

    public String getUserLastName() {
        return mUserLastName;
    }
}

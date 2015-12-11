package com.cmsys.linebacker.bean;

import java.io.Serializable;

/**
 * Created by cj on 09/12/15.
 */
// @JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class SettingsBean implements Serializable {
    private String key;
    private boolean blockCalls;
    private String deleteAudiosEveryWeeks;
    private boolean emailNotification;
    private boolean mobileNotification;

    public SettingsBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isBlockCalls() {
        return blockCalls;
    }

    public void setBlockCalls(boolean blockCalls) {
        this.blockCalls = blockCalls;
    }

    public String getDeleteAudiosEveryWeeks() {
        return deleteAudiosEveryWeeks;
    }

    public void setDeleteAudiosEveryWeeks(String deleteAudiosEveryWeeks) {
        this.deleteAudiosEveryWeeks = deleteAudiosEveryWeeks;
    }

    public boolean isEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public boolean isMobileNotification() {
        return mobileNotification;
    }

    public void setMobileNotification(boolean mobileNotification) {
        this.mobileNotification = mobileNotification;
    }
}

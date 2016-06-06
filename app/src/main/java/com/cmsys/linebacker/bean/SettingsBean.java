package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CarlosJesusGH on 09/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class SettingsBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;
    private boolean blockCalls;
    private String deleteAudiosEveryWeeks;
    private boolean emailNotification;
    private boolean mobileNotification;

    public SettingsBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_SETTINGBLOCKCALLS, this.isBlockCalls());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_SETTINGEMAILNOTIF, this.isEmailNotification());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_SETTINGMOBILENOTIF, this.isMobileNotification());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_SETTINGDELETEEVERY, this.getDeleteAudiosEveryWeeks());
        return fieldsMap;
    }

    @JsonIgnore
    public SettingsBean setAllDefaults(){
        this.blockCalls = true;
        this.emailNotification = false;
        this.mobileNotification = true;
        this.deleteAudiosEveryWeeks = "4";
        return this;
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

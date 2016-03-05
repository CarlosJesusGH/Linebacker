package com.cmsys.linebacker.bean;

import android.text.TextUtils;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CarlosJesusGH on 22/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)    // Use if necessary
public class UserBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String address;
    private String birthday;
    private String city;
    private Object creationDate;
    private String email;
    private String firstName;
    private Object lastConnection;
    private String lastName;
    private String phoneNumber;
    private String state;
    private int userLevel;  // 0=Free, 1=Premium
    private String zipCode;

    public UserBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public UserBean(String uId, String firstName, String lastName, String phoneNumber, String zipCode, String email, String birthday) {
        this.key = uId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.zipCode = zipCode;
        this.email = email;
        this.birthday = birthday;
        this.creationDate = ServerValue.TIMESTAMP;
        this.lastConnection = ServerValue.TIMESTAMP;
        this.userLevel = 0;
    }

    @JsonIgnore
    public Map<String, Object> getObjectMap(){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERFIRSTNAME, this.getFirstName());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERLASTNAME, this.getLastName());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_PHONENUMBER, this.getPhoneNumber());
        if(!TextUtils.isEmpty(this.getState()))
            fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERSTATE, this.getState());
        if(!TextUtils.isEmpty(this.getCity()))
            fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERCITY, this.getCity());
        if(!TextUtils.isEmpty(this.getAddress()))
            fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERADDRESS, this.getAddress());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERZIPCODE, this.getZipCode());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_EMAIL, this.getEmail());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERBIRTHDAY, this.getBirthday());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERCREATIONDATE, this.getCreationDate());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERLASTCONNECTION, this.getLastConnection());
        return fieldsMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Object getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        try {
            return DateUtils.getDateTimeString((Long) creationDate);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) creationDate;
    }

    public void setCreationDate(Object creationDate) {
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Object getLastConnection() {
        return lastConnection;
    }

    public String getLastConnectionString() {
        try {
            return DateUtils.getDateTimeString((Long) lastConnection);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) lastConnection;
    }

    public void setLastConnection(Object lastConnection) {
        this.lastConnection = lastConnection;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return 0=Free, 1=Premium
     */
    public int getUserLevel() {
        return userLevel;
    }

    public boolean isUserLevelPremium() {
        if (userLevel == 1)
            return true;
        return false;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}

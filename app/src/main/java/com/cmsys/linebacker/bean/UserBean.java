package com.cmsys.linebacker.bean;

import com.cmsys.linebacker.util.CONSTANTS;
import com.cmsys.linebacker.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cj on 22/11/15.
 */
public class UserBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;

    private String address;
    private String birthday;
    private Object creationDate;
    private String email;
    private String firstName;
    private Object lastConnection;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private int userLevel;

    public UserBean(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public UserBean(String uId, String firstName, String middleName, String lastName, String phoneNumber, String address, String email, String birthday) {
        this.key = uId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
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
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERMIDDLENAME, this.getMiddleName());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERLASTNAME, this.getLastName());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_PHONENUMBER, this.getPhoneNumber());
        fieldsMap.put(CONSTANTS.FIREBASE_FIELD_USERADDRESS, this.getAddress());
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }
}

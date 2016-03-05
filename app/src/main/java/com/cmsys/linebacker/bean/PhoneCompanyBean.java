package com.cmsys.linebacker.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by CarlosJesusGH on 03/03/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)    // Use if necessary
public class PhoneCompanyBean implements Serializable {
    @JsonIgnore     // Ignore this field when converting to json object
    private String key;
    private String activationPrefix;
    private String activationPrefix2;
    private String activationPrefix3;
    private String companyName;
    private String deactivationNumber;
    private String deactivationNumber2;
    private String specialInstructions;

    public PhoneCompanyBean() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog class
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getActivationPrefix() {
        return activationPrefix;
    }

    public void setActivationPrefix(String activationPrefix) {
        this.activationPrefix = activationPrefix;
    }

    public String getActivationPrefix2() {
        return activationPrefix2;
    }

    public void setActivationPrefix2(String activationPrefix2) {
        this.activationPrefix2 = activationPrefix2;
    }

    public String getActivationPrefix3() {
        return activationPrefix3;
    }

    public void setActivationPrefix3(String activationPrefix3) {
        this.activationPrefix3 = activationPrefix3;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDeactivationNumber() {
        return deactivationNumber;
    }

    public void setDeactivationNumber(String deactivationNumber) {
        this.deactivationNumber = deactivationNumber;
    }

    public String getDeactivationNumber2() {
        return deactivationNumber2;
    }

    public void setDeactivationNumber2(String deactivationNumber2) {
        this.deactivationNumber2 = deactivationNumber2;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}

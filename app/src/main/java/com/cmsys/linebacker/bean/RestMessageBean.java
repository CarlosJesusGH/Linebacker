package com.cmsys.linebacker.bean;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by @CarlosJesusGH on 08/03/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)    // Use if necessary
public class RestMessageBean implements Serializable {
    Integer errorId;
    String errorMessage;
    Object resultObject;

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public String getErrorMessage() {
        if (!TextUtils.isEmpty(errorMessage))
            return errorMessage;
        return "";
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
}

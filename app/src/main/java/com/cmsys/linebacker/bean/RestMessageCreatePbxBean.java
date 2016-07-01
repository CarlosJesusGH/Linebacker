package com.cmsys.linebacker.bean;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by @CarlosJesusGH on 01/07/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)    // Use if necessary
public class RestMessageCreatePbxBean implements Serializable {
    Integer errorId;
    ArrayList<String> errorMessage;
    Object resultObject;

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public ArrayList<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ArrayList<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
}

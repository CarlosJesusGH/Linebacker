package com.cmsys.linebacker.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by @CarlosJesusGH on 29/06/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)    // Use if necessary
public class RestMessageValidateBean {
    boolean confirmed;
    String message;
    String error;

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

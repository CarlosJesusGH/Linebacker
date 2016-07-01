package com.cmsys.linebacker.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import java.util.ArrayList;

/**
 * Created by @CarlosJesusGH on 29/06/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)    // Use if necessary
public class RestMessageRegisterBean {
    boolean created;
    String message;
    ArrayList<String> errors;

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }
}

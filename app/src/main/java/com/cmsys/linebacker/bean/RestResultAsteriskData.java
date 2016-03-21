package com.cmsys.linebacker.bean;

import com.google.gson.Gson;

/**
 * Created by @CarlosJesusGH on 08/03/16.
 */
public class RestResultAsteriskData {
    String externalPhoneNumber;
    String extensionNumber;
    String extensionPassword;

    public String getExternalPhoneNumber() {
        return externalPhoneNumber;
    }

    public void setExternalPhoneNumber(String externalPhoneNumber) {
        this.externalPhoneNumber = externalPhoneNumber;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getExtensionPassword() {
        return extensionPassword;
    }

    public void setExtensionPassword(String extensionPassword) {
        this.extensionPassword = extensionPassword;
    }

    @Override
    public String toString() {
        //return super.toString();
        return new Gson().toJson(this);
    }
}

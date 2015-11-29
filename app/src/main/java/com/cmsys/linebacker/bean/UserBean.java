package com.cmsys.linebacker.bean;

import java.io.Serializable;

/**
 * Created by cj on 22/11/15.
 */
public class UserBean implements Serializable {
    private String mUserId;
    private String mUserEmail;
    private String mUserUsername;
    private String mUserPassword;
    private String mUserFirstName;
    private String mUserLastName;


    /*public BeanUser(ModelBean pModelBean){
        mUserId = pModelBean.getTv1();
        mUserEmail = pModelBean.getTv2();
        mUserUsername = pModelBean.getTv3();
        mUserPassword = pModelBean.getTv4();
        mUserFirstName = pModelBean.getTv5();
        mUserLastName = pModelBean.getTv6();
    }*/

    public String getUserId() {
        return mUserId;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public String getUserUsername() {
        return mUserUsername;
    }

    public String getUserPassword() {
        return mUserPassword;
    }

    public String getUserFirstName() {
        return mUserFirstName;
    }

    public String getUserLastName() {
        return mUserLastName;
    }
}

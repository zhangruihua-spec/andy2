package com.util;

/**
 * Created by WangYi on 2017/5/5.
 */

public class LoginUtil {
    public String mUid;
    public String mToken;
    public static final LoginUtil instance = new LoginUtil();

    public static LoginUtil getInstance() {
        return instance;
    }

    public void login(String uid, String token) {

        mUid = uid;
        mToken = token;
    }
    public String getUid() {

        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public void logOut() {
        mUid = null;
        mToken = null;
    }
}

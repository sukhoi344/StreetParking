package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by Chau Thai on 8/7/15.
 */

@ParseClassName("User")
public class User extends ParseUser {

    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL_VERIFIED = "emailVerified";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_MOBILE_VERIFIED = "mobileVerified";


    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRST_NAME, firstName);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public void setLastName(String lastName) {
        put(KEY_LAST_NAME, lastName);
    }

    public String getAvatar() {
        return getString(KEY_AVATAR);
    }

    public void setAvatar(String avatar) {
        put(KEY_AVATAR, avatar);
    }

    public boolean getEmailVerified() {
        return getBoolean(KEY_EMAIL_VERIFIED);
    }

    public void setEmailVerified(boolean emailVerified) {
        put(KEY_EMAIL_VERIFIED, emailVerified);
    }

    public String getMobile() {
        return getString(KEY_MOBILE);
    }

    public void setMobile(String mobile) {
        put(KEY_MOBILE, mobile);
    }

    public boolean getMobileVerified() {
        return getBoolean(KEY_MOBILE_VERIFIED);
    }

    public void setMobileVerified(boolean mobileVerified) {
        put(KEY_MOBILE_VERIFIED, mobileVerified);
    }

}

package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Chau Thai on 8/7/15.
 */

@ParseClassName("_User")
public class User extends ParseUser {

    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL_VERIFIED = "emailVerified";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_MOBILE_VERIFIED = "mobileVerified";
    private static final String KEY_CREDIT_CARD = "creditCard";


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

    public ParseFile getAvatar() {
        return getParseFile(KEY_AVATAR);
    }

    public void setAvatar(ParseFile avatar) {
        put(KEY_AVATAR, avatar);
    }

    public boolean getEmailVerified() {
        return getBoolean(KEY_EMAIL_VERIFIED);
    }

//    public void setEmailVerified(boolean emailVerified) {
//        put(KEY_EMAIL_VERIFIED, emailVerified);
//    }

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

    // TODO: change List<Credit> to just one Credit object, on need to have an array (also in the backend).
    public List<Credit> getCredits() {
        return getList(KEY_CREDIT_CARD);
    }

}

package chau.streetparking.datamodels;

import java.util.Date;

/**
 * Created by Chau Thai on 7/26/2015.
 */
public class Users extends BackendObject {
    private String  avatar;
    private String  email;
    private String  firstName;
    private String  lastName;
    private Date    lastLogin;
    private String  mobile;
    private String  password;
    private String  socialAccount;
    private String  userStatus;

    @Override
    public String toString() {
        return "Users{" +
                "avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", lastLogin=" + lastLogin +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", socialAccount='" + socialAccount + '\'' +
                ", userStatus='" + userStatus + '\'' +
                '}';
    }
}

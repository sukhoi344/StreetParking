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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSocialAccount() {
        return socialAccount;
    }

    public void setSocialAccount(String socialAccount) {
        this.socialAccount = socialAccount;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

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

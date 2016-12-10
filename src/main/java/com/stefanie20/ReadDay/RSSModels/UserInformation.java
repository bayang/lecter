package com.stefanie20.ReadDay.RSSModels;

/**
 *  A UserInformation represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/user-info">User information</a>
 */

public class UserInformation {

    private String userId;
    private String userName;
    private String userProfileId;
    private String userEmail;
    private boolean isBloggerUser;
    private long signupTimeSec;
    private boolean isMultiLoginEnabled;

    public UserInformation(String userId, String userName, String userProfileId, String userEmail, boolean isBloggerUser, long signupTimeSec, boolean isMultiLoginEnabled) {
        this.userId = userId;
        this.userName = userName;
        this.userProfileId = userProfileId;
        this.userEmail = userEmail;
        this.isBloggerUser = isBloggerUser;
        this.signupTimeSec = signupTimeSec;
        this.isMultiLoginEnabled = isMultiLoginEnabled;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isBloggerUser() {
        return isBloggerUser;
    }

    public long getSignupTimeSec() {
        return signupTimeSec;
    }

    public boolean isMultiLoginEnabled() {
        return isMultiLoginEnabled;
    }
}

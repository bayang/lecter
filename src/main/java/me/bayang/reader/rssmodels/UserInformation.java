package me.bayang.reader.rssmodels;

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
    
    public UserInformation() {
        super();
    }

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
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setBloggerUser(boolean isBloggerUser) {
        this.isBloggerUser = isBloggerUser;
    }

    public void setSignupTimeSec(long signupTimeSec) {
        this.signupTimeSec = signupTimeSec;
    }

    public void setMultiLoginEnabled(boolean isMultiLoginEnabled) {
        this.isMultiLoginEnabled = isMultiLoginEnabled;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserInformation [userId=").append(userId)
                .append(", userName=").append(userName)
                .append(", userProfileId=").append(userProfileId)
                .append(", userEmail=").append(userEmail)
                .append(", isBloggerUser=").append(isBloggerUser)
                .append(", signupTimeSec=").append(signupTimeSec)
                .append(", isMultiLoginEnabled=").append(isMultiLoginEnabled)
                .append("]");
        return builder.toString();
    }
    
}

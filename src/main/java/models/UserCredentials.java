package models;

/**
 * Credentials of uploading user.
 */
public class UserCredentials {
    private final String username;
    private final String phoneNumber;

    public UserCredentials(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

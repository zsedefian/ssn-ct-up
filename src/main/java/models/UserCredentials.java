package models;

public class UserCredentials {
    private final String cognitoId;
    private final String phoneNumber;

    public UserCredentials(String cognitoId, String phoneNumber) {
        this.cognitoId = cognitoId;
        this.phoneNumber = phoneNumber;
    }

    public String getCognitoId() {
        return cognitoId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import models.UserCredentials;
import org.json.JSONObject;

/**
 * Determines user credentials.
 */
public class UserCredentialsService {

    /**
     * Decodes JWT token in order to parse cognito username and phone number of user.
     *
     * @param jwtToken JWT token
     * @return User credentials object
     */
    public UserCredentials getUserCredentials(String jwtToken) {
        DecodedJWT decodedJwt = JWT.decode(jwtToken);
        String payload = decodedJwt.getPayload();
        JSONObject json = new JSONObject(payload);
        String username = json.getString("cognito:username");
        String phoneNumber = json.getString("phone-number");
        if (username == null || phoneNumber == null) {
            throw new IllegalStateException("Username and phone number must be provided. " +
                    "username: " + username + " phoneNumber: " + phoneNumber);
        }
        return new UserCredentials(username, phoneNumber);
    }
}

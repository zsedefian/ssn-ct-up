package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import models.UserCredentials;
import org.json.JSONObject;

public class UserCredentialsService {
    
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

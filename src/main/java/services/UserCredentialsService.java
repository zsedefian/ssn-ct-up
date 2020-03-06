package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import models.UserCredentials;

import java.util.Map;

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
        Map<String, Claim> claims = JWT.decode(trimPrefix(jwtToken)).getClaims();
        Claim username = claims.get("cognito:username");
        Claim phoneNumber = claims.get("phone_number");
        if (username.isNull() || phoneNumber.isNull()) {
            throw new IllegalStateException("Username and phone number must be provided.");
        }
        return new UserCredentials(username.asString(), phoneNumber.asString());
    }

    private String trimPrefix(String jwtToken) {
        String prefix = "Bearer ";
        return jwtToken.startsWith(prefix) ? jwtToken.substring(prefix.length()) : jwtToken;
    }
}

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
        Map<String, Claim> claims = JWT.decode(jwtToken.substring("Bearer ".length())).getClaims();
        Claim usernameClaim = claims.get("cognito:username");
        Claim phoneNumber = claims.get("phone_number");
        if (usernameClaim.isNull() || phoneNumber.isNull()) {
            throw new IllegalStateException("Username and phone number must be provided.");
        }
        return new UserCredentials(usernameClaim.asString(), phoneNumber.asString());
    }
}

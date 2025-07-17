package dataaccess;

import Model.AuthData;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class AuthDAO implements IAuthDAO {
    private final Map<String, AuthData> authDataMap = new HashMap<>();

    @Override
    public String createAuth(String username) throws DataAccessException {
        String newAuthToken = createAuthToken();
        AuthData authData = new AuthData(username, newAuthToken);
        authDataMap.put(newAuthToken, authData);
        return newAuthToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)){
            throw new DataAccessException("Cannot delete AuthData, AuthToken: " + authToken + " does not exist.");
        }
        authDataMap.remove(authToken);
    }
    private String createAuthToken() {
        return UUID.randomUUID().toString();
    }

    public void clearAllAuthTokens() {
        authDataMap.clear();
    }
}

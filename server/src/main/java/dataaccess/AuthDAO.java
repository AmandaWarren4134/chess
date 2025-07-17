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
    public AuthData getAuth(String AuthToken) throws DataAccessException {
        if (authDataMap.get(AuthToken) == null) {
            throw new DataAccessException("Cannot get AuthData, AuthToken: " + AuthToken + " does not exist.");
        }
        return authDataMap.get(AuthToken);
    }

    @Override
    public void deleteAuth(String AuthToken) throws DataAccessException {
        if (!authDataMap.containsKey(AuthToken)){
            throw new DataAccessException("Cannot delete AuthData, AuthToken: " + AuthToken + " does not exist.");
        }
        authDataMap.remove(AuthToken);
    }
    private String createAuthToken() {
        return UUID.randomUUID().toString();
    }

    public void clearAllAuthTokens() {
        authDataMap.clear();
    }
}

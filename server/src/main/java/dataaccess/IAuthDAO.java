package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface IAuthDAO {
    String createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clearAllAuthTokens() throws DataAccessException;
}

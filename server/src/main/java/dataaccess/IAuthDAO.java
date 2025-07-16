package dataaccess;

import Model.AuthData;

public interface IAuthDAO {
    String createAuth(String username) throws DataAccessException;
    AuthData getAuth(String AuthToken) throws DataAccessException;
    void deleteAuth(String AuthToken) throws DataAccessException;
}

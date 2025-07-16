package dataaccess;

import Model.UserData;

public interface IUserDAO {
    void createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}

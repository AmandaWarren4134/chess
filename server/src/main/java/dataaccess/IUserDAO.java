package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface IUserDAO {
    void createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clearAllUsers();
}

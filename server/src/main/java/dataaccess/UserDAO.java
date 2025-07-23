package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.UserData;
import java.util.Map;
import java.util.HashMap;

public class UserDAO implements IUserDAO {
    private final Map<String, UserData> userDataMap = new HashMap<>();

    @Override
    public void createUser(String username, String password, String email) throws AlreadyTakenException {
        if (userDataMap.containsKey(username)) {
            throw new AlreadyTakenException("This username: " + username + " is already taken.");
        }
        UserData userData = new UserData(username, password, email);
        userDataMap.put(username, userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new DataAccessException("This user: " + username + " does not exist.");
        }
        return userDataMap.get(username);
    }

    @Override
    public void clearAllUsers() throws DataAccessException {
        userDataMap.clear();
        if (!userDataMap.isEmpty()) {
            throw new DataAccessException("Failed to clear user data.");
        }
    }
}

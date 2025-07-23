package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public class MySqlAuth implements IAuthDAO {
    @Override
    public String createAuth(String username) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}

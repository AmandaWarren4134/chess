package service;

import dataaccess.AuthDAO;
import dataaccess.MySqlAuth;
import dataaccess.exceptions.DataAccessException;

public class AuthService {
        private final MySqlAuth authDAO;

        public AuthService(MySqlAuth authDAO) {
            this.authDAO = authDAO;
        }

        public void clearAuthData() throws DataAccessException {
            authDAO.clearAllAuthTokens();
        }
}

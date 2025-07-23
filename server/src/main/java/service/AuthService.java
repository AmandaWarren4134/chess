package service;

import dataaccess.AuthDAO;
import dataaccess.exceptions.DataAccessException;

public class AuthService {
        private final AuthDAO authDAO;

        public AuthService(AuthDAO authDAO) {
            this.authDAO = authDAO;
        }

        public void clearAuthData() throws DataAccessException {
            authDAO.clearAllAuthTokens();
        }
}

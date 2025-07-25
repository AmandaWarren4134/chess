package service;

import dataaccess.IAuthDAO;
import dataaccess.exceptions.DataAccessException;

public class AuthService {
        private final IAuthDAO authDAO;

        public AuthService(IAuthDAO authDAO) {
            this.authDAO = authDAO;
        }

        public void clearAuthData() throws DataAccessException {
            authDAO.clearAllAuthTokens();
        }
}

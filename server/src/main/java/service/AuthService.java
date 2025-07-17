package service;

import dataaccess.AuthDAO;

public class AuthService {
        private final AuthDAO authDAO;

        public AuthService() {
            this.authDAO = new AuthDAO();
        }

        public void clearAuthData() {
            authDAO.clearAllAuthTokens();
        }
}

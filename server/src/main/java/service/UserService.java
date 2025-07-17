package service;

import dataaccess.*;
import service.request.RegisterRequest;
import service.response.RegisterResult;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.authDAO = new AuthDAO();
        this.gameDAO = new GameDAO();
    }

    public void validateRegisterRequest(RegisterRequest registerRequest) throws BadRequestException {
        // Validate request fields
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null || registerRequest.username().isBlank() || registerRequest.password().isBlank() || registerRequest.email().isBlank()){
            throw new BadRequestException("Error: bad request - one or more fields are missing.");
        }
    }
    /***
     * Accepts a RegisterRequest and returns the RegisterResult
     *
     * @param registerRequest
     * @return
     */
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        // Create user
        userDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());

        // Generate auth token
        String newAuthToken = authDAO.createAuth(registerRequest.username());

        // Return RegisterResult
        return new RegisterResult(registerRequest.username(), newAuthToken);
    }

//    public LoginResult login(LoginRequest loginRequest) {
//
//    }
//    public void logout(LogoutRequest logoutRequest) {}
    public void clearUserData() {
        userDAO.clearAllUsers();
    }
}

package service;

import Model.UserData;
import dataaccess.*;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.response.LogoutResult;
import service.response.RegisterResult;
import service.response.LoginResult;
import service.request.LoginRequest;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    /***
     * Accepts a RegisterRequest and returns the RegisterResult
     *
     * @param registerRequest
     * @return
     */
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        // Validate Request
        validateRegisterRequest(registerRequest);

        // Create user
        userDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());

        // Generate auth token
        String newAuthToken = authDAO.createAuth(registerRequest.username());

        // Return RegisterResult
        return new RegisterResult(registerRequest.username(), newAuthToken);
    }

    private void validateRegisterRequest(RegisterRequest registerRequest) throws BadRequestException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null || registerRequest.username().isBlank() || registerRequest.password().isBlank() || registerRequest.email().isBlank()){
            throw new BadRequestException("Error: bad request - one or more fields are missing.");
        }
    }

    /***
     * Accepts LoginRequest and returns LoginResult
     *
     * @param loginRequest
     * @return
     * @throws DataAccessException
     */
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        // Validate Request
        validateLoginRequest(loginRequest);

        // Get User
        UserData userData;
        try {
            userData = userDAO.getUser(loginRequest.username());
        } catch (DataAccessException ex) {
            throw new UnauthorizedException("Error: username does not exist.");
        }

        // Validate Password
        validatePassword(userData, loginRequest);

        // Generate auth token
        String newAuthToken = authDAO.createAuth(loginRequest.username());

        // Return LoginResult
        return new LoginResult(userData.username(), newAuthToken);
    }

    private void validateLoginRequest(LoginRequest request) throws BadRequestException {
        if (request.username() == null || request.password() == null || request.username().isBlank() || request.password().isBlank()){
            throw new BadRequestException("Error: bad request - one or more fields are missing.");
        }
    }

    private void validatePassword(UserData userData, LoginRequest request) throws InvalidPasswordException {
        if (!userData.password().equals(request.password())) {
            throw new InvalidPasswordException("Error: unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        // Validate Request
        validateLogoutRequest(logoutRequest);

        // Get AuthData
        try {
            authDAO.deleteAuth(logoutRequest.authToken());
        } catch (DataAccessException ex) {
            throw new UnauthorizedException("Error: authToken does not exist.");
        }

        return new LogoutResult();
    }

    private void validateLogoutRequest(LogoutRequest request) throws BadRequestException {
        if (request == null || request.authToken() == null || request.authToken().isBlank()) {
            throw new BadRequestException("Error: bad request - one or more fields are missing.");
        }
    }

    public void clearUserData() {
        userDAO.clearAllUsers();
    }
}

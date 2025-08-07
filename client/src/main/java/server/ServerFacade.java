package server;

import com.google.gson.Gson;
import exception.ResponseException;
import response.*;
import request.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    private static final Gson Gson = new Gson();

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public void logout() throws ResponseException {
        if (this.authToken == null || this.authToken.isBlank()) {
            throw new ResponseException(400, "No auth token set");
        }
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }

    public ListResult list(ListRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, request, ListResult.class);
    }

    public CreateResult create(CreateRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, request, CreateResult.class);
    }

    public void join(JoinRequest request) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, request, null);
    }

    public String getServerUrl() {
        return serverUrl;
    }

    /***
     * Generic method to make an HTTP request
     * @param method
     * @param path
     * @param request
     * @param responseClass
     * @return
     * @param <T>
     * @throws ResponseException
     */
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (method.equals("POST") || method.equals("PUT")) {
                http.setDoOutput(true);
            } else {
                http.setDoOutput(false);
            }

            if (authToken != null && !authToken.isBlank()) {
                http.setRequestProperty("authorization", authToken);
            }

            if ((method.equals("POST") || method.equals("PUT")) && request != null) {
                writeBody(request,http);
            }

            http.connect();
            throwIfNotSuccessful(http);

            int status = http.getResponseCode();

            InputStream stream = (status >= 200 && status < 300) ? http.getInputStream() : http.getErrorStream();
            String rawJson = new String(stream.readAllBytes());

            if (responseClass != null && !rawJson.isBlank()) {
                return Gson.fromJson(rawJson, responseClass);
            }
            return null;

        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = Gson.toJson(request);
            System.out.println("Request JSON: " + reqData); // DEBUG
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private boolean isSuccessful (int status) {
        return status / 100 == 2;
    }
}

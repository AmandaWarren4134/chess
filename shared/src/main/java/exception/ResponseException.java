package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    public final int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);

        Object statusObj = map.get("status");
        int status = (statusObj instanceof Number) ? ((Number) statusObj).intValue() : 500;

        Object messageObj = map.get("message");
        String message = (messageObj != null) ? messageObj.toString() : "Unknown error";

        return new ResponseException(status, message);
    }

    public int statusCode() {
        return statusCode;
    }

}

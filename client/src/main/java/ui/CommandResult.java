package ui;

public class CommandResult {
    public final boolean success;
    public final String message;
    public final boolean goForward;
    public final boolean quit;
    public final String authToken;
    public final String username;

    public CommandResult(
            boolean success,
            String message,
            boolean goToNextLoop,
            boolean quit
    ) {
        this(success, message, goToNextLoop, quit, null, null);
    }

    public CommandResult(boolean success, String message, boolean goToNextLoop, boolean quit, String authToken, String username) {
        this.success = success;
        this.message = message;
        this.goForward = goToNextLoop;
        this.quit = quit;
        this.authToken = authToken;
        this.username = username;
    }

    public String getMessage() {return message;}
    public boolean isQuit() {return quit;}
    public String getAuthToken() {return authToken;}
    public String getUsername() { return username;}
}

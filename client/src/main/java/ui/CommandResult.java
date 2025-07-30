package ui;

public class CommandResult {
    public final boolean success;
    public final String message;
    public final boolean goToPostLogin;
    public final boolean quit;

    public CommandResult(
            boolean success,
            String message,
            boolean goToPostLogin,
            boolean quit
    ) {
        this.success = success;
        this.message = message;
        this.goToPostLogin = goToPostLogin;
        this.quit = quit;
    }

    public String getMessage() {return message;}
    public boolean isSuccess() {return success;}
    public boolean goToPostLogin() { return goToPostLogin;}
    public boolean isQuit() {return quit;}
}

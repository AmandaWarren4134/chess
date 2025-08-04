package ui;

public class CommandResult {
    public final boolean success;
    public final String message;
    public final boolean goForward;
    public final boolean quit;

    public CommandResult(
            boolean success,
            String message,
            boolean goToNextLoop,
            boolean quit
    ) {
        this.success = success;
        this.message = message;
        this.goForward = goToNextLoop;
        this.quit = quit;
    }

    public String getMessage() {return message;}
    public boolean isQuit() {return quit;}
}

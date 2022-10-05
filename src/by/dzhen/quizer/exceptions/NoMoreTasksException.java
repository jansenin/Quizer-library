package by.dzhen.quizer.exceptions;

public class NoMoreTasksException extends IllegalStateException {
    public NoMoreTasksException() {
        super("There are no more tasks");
    }
}

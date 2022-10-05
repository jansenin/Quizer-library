package by.dzhen.quizer.exceptions;

public class CannotGetMarkException extends RuntimeException {
    public CannotGetMarkException() {
        super("You can get mark only after solving all questions in the quiz.");
    }
}

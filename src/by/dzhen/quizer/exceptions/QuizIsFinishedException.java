package by.dzhen.quizer.exceptions;

public class QuizIsFinishedException extends RuntimeException {
    public QuizIsFinishedException() {
        super("There is no tasks left in this quiz.");
    }
}

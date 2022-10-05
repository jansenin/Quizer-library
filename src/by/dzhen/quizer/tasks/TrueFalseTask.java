package by.dzhen.quizer.tasks;

import by.dzhen.quizer.Result;

public class TrueFalseTask implements Task {
    private String text;
    private boolean answer;

    public TrueFalseTask(String text, boolean answer) {
        this.text = text;
        this.answer = answer;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Result validate(String answer) {
        answer = answer.toLowerCase();
        if (!isCorrectLowercaseInput(answer)) {
            return Result.INCORRECT_INPUT;
        }
        boolean isCorrect = answer.toLowerCase().equals(Boolean.toString(this.answer));
        return isCorrect ? Result.OK : Result.WRONG;
    }

    private boolean isCorrectLowercaseInput(String s) {
        return s.equals("true") || s.equals("false");
    }
}

package by.dzhen.quizer.tasks;

import by.dzhen.quizer.Result;

/**
 * Задание с заранее заготовленным текстом.
 */
public class TextTask implements Task {
    private String text;
    private String answer;

    public TextTask(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Result validate(String answer) {
        return this.answer.equals(answer) ? Result.OK : Result.WRONG;
    }
}

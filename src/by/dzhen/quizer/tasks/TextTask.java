package by.dzhen.quizer.tasks;

import by.dzhen.quizer.Result;

/**
 * Задание с заранее заготовленным текстом.
 */
public class TextTask implements Task {
    private String text;
    private String answer;
    private boolean ignoreCase;

    public TextTask(String text, String answer) {
        this.text = text;
        this.answer = answer;
        this.ignoreCase = true;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    @Override
    public Result validate(String answer) {
        return isCorrect(answer) ? Result.OK : Result.WRONG;
    }

    private boolean isCorrect(String answer) {
        if (ignoreCase) {
            return this.answer.equalsIgnoreCase(answer);
        } else {
            return this.answer.equals(answer);
        }
    }
}

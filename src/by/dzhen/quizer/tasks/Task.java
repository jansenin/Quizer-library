package by.dzhen.quizer.tasks;

import by.dzhen.quizer.Result;

public interface Task {
    interface Generator {
        Task generate();
    }

    /**
     * @return текст задания
     */
    String getText();

    Result validate(String answer);
}

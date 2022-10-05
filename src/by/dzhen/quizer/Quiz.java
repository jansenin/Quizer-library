package by.dzhen.quizer;

import by.dzhen.quizer.exceptions.CannotGetMarkException;
import by.dzhen.quizer.exceptions.QuizIsFinishedException;
import by.dzhen.quizer.tasks.Task;

public class Quiz {
    private int correctAnswerNumber;
    private int wrongAnswerNumber;
    private int incorrectInputNumber;

    private Task.Generator taskGenerator;
    private Task currentTask;
    private boolean answeredOnCurrentTask;
    private int tasksLeft;
    private int taskCount;

    public Quiz(Task.Generator generator, int taskCount) {
        if (taskCount < 0) {
            throw new IllegalArgumentException("tasksCount (which is " + taskCount + ") must be >= 0.");
        }

        this.taskGenerator = generator;
        this.currentTask = null;
        this.answeredOnCurrentTask = false;
        this.tasksLeft = taskCount;
        this.taskCount = taskCount;

        this.correctAnswerNumber = 0;
        this.wrongAnswerNumber = 0;
        this.incorrectInputNumber = 0;
    }

    /**
     * @return задание, повторный вызов вернет следующее
     */
    public Task nextTask() {
        exceptionIfNoTasksLeft();
        createNewTaskIfNecessary();
        return currentTask;
    }

    private void createNewTaskIfNecessary() {
        if ((currentTask != null && !answeredOnCurrentTask) || tasksLeft == 0) {
            return;
        }
        currentTask = taskGenerator.generate();
        answeredOnCurrentTask = false;
    }

    /**
     * Предоставить ответ ученика. Если результат {@link Result#INCORRECT_INPUT}, то счетчик неправильных
     * ответов не увеличивается.
     */
    public Result provideAnswer(String answer) {
        exceptionIfNoTasksLeft();

        Result result = currentTask.validate(answer);

        switch (result) {
            case INCORRECT_INPUT -> incorrectInputNumber++;
            case OK -> correctAnswerNumber++;
            case WRONG -> wrongAnswerNumber++;
        }

        if (result == Result.OK || result == Result.WRONG) {
            answeredOnCurrentTask = true;
            tasksLeft--;
        }

        return result;
    }

    private void exceptionIfNoTasksLeft() {
        if (tasksLeft == 0) {
            throw new QuizIsFinishedException();
        }
    }

    public boolean isFinished() {
        return tasksLeft == 0;
    }

    public int getCorrectAnswerNumber() {
        return correctAnswerNumber;
    }

    public int getWrongAnswerNumber() {
        return wrongAnswerNumber;
    }

    public int getIncorrectInputNumber() {
        return incorrectInputNumber;
    }

    /**
     * @return оценка, которая является отношением количества правильных ответов к количеству всех вопросов.
     *         Оценка выставляется только в конце!
     */
    public double getMark() {
        if (tasksLeft != 0) {
            throw new CannotGetMarkException();
        }
        return ((double) getCorrectAnswerNumber()) / taskCount;
    }
}

package by.dzhen.quizer;

import by.dzhen.quizer.task_generators.CSEasyQuestionsTaskGenerator;
import by.dzhen.quizer.task_generators.GroupTaskGenerator;
import by.dzhen.quizer.task_generators.PoolTaskGenerator;
import by.dzhen.quizer.tasks.math_tasks.EquationTask;
import by.dzhen.quizer.tasks.math_tasks.ExpressionTask;
import by.dzhen.quizer.tasks.math_tasks.MathTask.Generator.Operation;
import by.dzhen.quizer.tasks.Task;
import by.dzhen.quizer.tasks.TextTask;

import java.util.EnumSet;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner in;
    private static Map<String, Quiz> quizes;
    private static Quiz quiz;

    public static void main(String[] args) {
        init();
        quiz = quizes.get(enterValidQuizName());

        while (!quiz.isFinished()) {
            Result result = dealWithNextTaskUntilCorrectInputAndGetResult();
            processResultFromCorrectInput(result);
        }

        printMark();
    }

    private static void init() {
        quizes = getQuizMap();
        in = new Scanner(System.in);
    }

    private static String enterValidQuizName() {
        System.out.print("Enter quiz name: ");
        String quizName = in.nextLine();
        while (!quizes.containsKey(quizName)) {
            System.out.println("There is no quiz with this name.");
            System.out.print("Enter quiz name: ");
            quizName = in.nextLine();
        }
        return quizName;
    }

    private static Result dealWithNextTaskUntilCorrectInputAndGetResult() {
        Task task = quiz.nextTask();
        String taskText = task.getText();

        System.out.println(taskText);

        String answer = in.nextLine();
        Result result = quiz.provideAnswer(answer);

        while (result == Result.INCORRECT_INPUT) {
            System.out.println("Incorrect input.");
            System.out.println(taskText);

            answer = in.nextLine();
            result = quiz.provideAnswer(answer);
        }

        return result;
    }

    private static void processResultFromCorrectInput(Result result) {
        switch (result) {
            case OK -> System.out.println("Correct!");
            case WRONG -> System.out.println("Incorrect!");
            case INCORRECT_INPUT -> {
                assert false;
            }
        }
    }

    private static void printMark() {
        System.out.println("Mark: " + quiz.getMark());
    }

    /**
     * @return тесты в {@link Map}, где
     * ключ - название теста {@link String}
     * значение - сам тест {@link Quiz}
     */
    static Map<String, Quiz> getQuizMap() {
        return Map.of(
                "EquationsQuiz", makeEquationQuiz(),
                "ExpressionsQuiz", makeExpressionQuiz(),
                "TextQuizWithDuplicates", makeTextQuizWithDuplicates(),
                "TextQuizWithoutDuplicates", makeTextQuizWithoutDuplicates(),
                "CSQuiz", makeCsQuestionsQuiz(),
                "AllInOneQuiz", makeAllInOneQuiz()
        );
    }

    private static Quiz makeEquationQuiz() {
        return new Quiz(makeEquationGenerator(), 5);
    }

    private static Quiz makeExpressionQuiz() {
        return new Quiz(makeExpressionGenerator(), 5);
    }

    private static Quiz makeTextQuizWithDuplicates() {
        return new Quiz(makePoolGeneratorWithDuplicates(), 5);
    }

    private static Quiz makeTextQuizWithoutDuplicates() {
        return new Quiz(makePoolGeneratorWithoutDuplicates(), 2);
    }

    private static Quiz makeCsQuestionsQuiz() {
        return new Quiz(makeCsQuestionsTaskGenerator(), 5);
    }

    private static Quiz makeAllInOneQuiz() {
        return new Quiz(makeAllInOneGroupGenerator(), 20);
    }

    private static Task.Generator makeEquationGenerator() {
        return new EquationTask.Generator(-10, 10, EnumSet.allOf(Operation.class));
    }

    private static Task.Generator makeExpressionGenerator() {
        return new ExpressionTask.Generator(-10, 10, EnumSet.allOf(Operation.class));
    }

    private static Task.Generator makePoolGeneratorWithDuplicates() {
        return new PoolTaskGenerator(true,
                new TextTask("Yes?", "Yes"),
                new TextTask("No?", "No"));
    }

    private static Task.Generator makePoolGeneratorWithoutDuplicates() {
        return new PoolTaskGenerator(false,
                new TextTask("Yes?", "Yes"),
                new TextTask("No?", "No"));
    }

    private static Task.Generator makeCsQuestionsTaskGenerator() {
        return new CSEasyQuestionsTaskGenerator();
    }

    private static Task.Generator makeAllInOneGroupGenerator() {
        return new GroupTaskGenerator(
                makeEquationGenerator(),
                makeExpressionGenerator(),
                makePoolGeneratorWithDuplicates(),
                makePoolGeneratorWithoutDuplicates(),
                makeCsQuestionsTaskGenerator()
        );
    }
}

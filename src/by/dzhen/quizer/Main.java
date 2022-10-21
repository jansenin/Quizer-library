package by.dzhen.quizer;

import by.dzhen.quizer.json_task_parsers.FileTextTasksParser;
import by.dzhen.quizer.task_generators.CSEasyQuestionsTaskGenerator;
import by.dzhen.quizer.task_generators.GroupTaskGenerator;
import by.dzhen.quizer.task_generators.PoolTaskGenerator;
import by.dzhen.quizer.tasks.math_tasks.EquationTask;
import by.dzhen.quizer.tasks.math_tasks.ExpressionTask;
import by.dzhen.quizer.tasks.math_tasks.MathTask.Generator.Operation;
import by.dzhen.quizer.tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static Scanner in;
    private static Map<String, Quiz> quizzes;
    private static Quiz quiz;

    public static void main(String[] args) {
        init();

        informAboutAvailableQuizzes();

        quiz = quizzes.get(enterValidQuizName());

        while (!quiz.isFinished()) {
            Result result = dealWithNextTaskUntilCorrectInputAndGetResult();
            processResultFromCorrectInput(result);
        }

        printMark();
    }

    private static void init() {
        quizzes = getQuizMap();
        in = new Scanner(System.in);
    }

    private static void informAboutAvailableQuizzes() {
        System.out.println("List of available quizzes:");
        quizzes.forEach((name, quiz) -> System.out.println(name));
        System.out.println();
    }

    private static String enterValidQuizName() {
        System.out.print("Enter quiz name: ");
        String quizName = in.nextLine();
        while (!quizzes.containsKey(quizName)) {
            System.out.println("There is no quiz with this name.");
            System.out.print("Enter quiz name: ");
            quizName = in.nextLine();
        }
        System.out.println();
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
        System.out.println();
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
        Map<String, Quiz> result = new HashMap<>() {{
            put("EquationsQuiz", makeEquationQuiz());
            put("ExpressionsQuiz", makeExpressionQuiz());
            put("TextQuizWithDuplicates", makeTextQuizWithDuplicates());
            put("TextQuizWithoutDuplicates", makeTextQuizWithoutDuplicates());
            put("CSQuiz", makeCsQuestionsQuiz());
            put("AllInOneQuiz", makeAllInOneQuiz());
        }};
        try {
            result.putAll(loadAllTextQuizzes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getNameWithoutExtension(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? file : file.substring(0, dotIndex);
    }

    private static Map<String, Quiz> loadAllTextQuizzes() throws IOException {
        class TasksPack {
            private Path path;

            public TasksPack(Path path) {
                this.path = path;
            }

            public String getName() {
                return getNameWithoutExtension(path.getFileName().toString()) + "Quiz";
            }

            public Quiz loadAndMakeQuiz() {
                List<Task> tasks = new FileTextTasksParser(path.toString()).parse();
                PoolTaskGenerator generator = new PoolTaskGenerator(false, tasks);
                return new Quiz(generator, tasks.size());
            }
        }

        return Files.list(Paths.get("text_tasks"))
                .map(TasksPack::new)
                .parallel()
                .collect(Collectors.toMap(TasksPack::getName, TasksPack::loadAndMakeQuiz));
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
        String path = Paths.get("text_tasks", "Negotiation.json").toString();
        List<Task> tasks = new FileTextTasksParser(path).parse();
        return new PoolTaskGenerator(true, tasks);
    }

    private static Task.Generator makePoolGeneratorWithoutDuplicates() {
        String path = Paths.get("text_tasks", "Negotiation.json").toString();
        List<Task> tasks = new FileTextTasksParser(path).parse();
        return new PoolTaskGenerator(false, tasks);
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

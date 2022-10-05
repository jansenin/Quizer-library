package by.dzhen.quizer.task_generators;

import by.dzhen.quizer.tasks.Task;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GroupTaskGenerator implements Task.Generator {
    private List<Task.Generator> taskGenerators;

    public GroupTaskGenerator(Task.Generator... generators) {
        taskGenerators = List.of(generators);
    }

    public GroupTaskGenerator(Collection<Task.Generator> generators) {
        taskGenerators = List.copyOf(generators);
    }

    /**
     * @return результат метода degenerate() случайного генератора из списка.
     *         Если этот генератор выбросил исключение в методе generate(), выбирается другой.
     *         Если все генераторы выбрасывают исключение, то и тут выбрасывается исключение.
     */
    @Override
    public Task generate() {
        int index = ThreadLocalRandom.current().nextInt(taskGenerators.size());
        try {
            return taskGenerators.get(index).generate();
        } catch (Exception e) {
            return generateTaskFromOneOfAllGeneratorsExcept(taskGenerators.get(index));
        }
    }

    // for speed
    private Task generateTaskFromOneOfAllGeneratorsExcept(Task.Generator forbiddenGenerator) {
        List<Task.Generator> allowedGenerators =
                taskGenerators.stream()
                        .filter(g -> g != forbiddenGenerator)
                        .collect(Collectors.toList());
        Collections.shuffle(allowedGenerators);

        RuntimeException exceptionIfTaskWontBeGenerated = new RuntimeException();
        for (int i = 0; i < allowedGenerators.size(); i++) {
            try {
                return taskGenerators.get(i).generate();
            } catch (Exception e) {
                exceptionIfTaskWontBeGenerated.addSuppressed(e);
            }
        }

        throw exceptionIfTaskWontBeGenerated;
    }
}

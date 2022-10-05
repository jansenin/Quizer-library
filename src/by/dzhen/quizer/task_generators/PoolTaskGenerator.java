package by.dzhen.quizer.task_generators;

import by.dzhen.quizer.exceptions.NoMoreTasksException;
import by.dzhen.quizer.tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PoolTaskGenerator implements Task.Generator {
    private List<Task> tasks;
    private boolean allowDuplicates;
    private int previousTaskIndex;

    public PoolTaskGenerator(boolean allowDuplicates, Task... tasks) {
        this(allowDuplicates, List.of(tasks));
    }

    public PoolTaskGenerator(boolean allowDuplicates, Collection<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        this.allowDuplicates = allowDuplicates;
        if (!allowDuplicates) {
            Collections.shuffle(this.tasks);
            previousTaskIndex = -1;
        }
    }

    @Override
    public Task generate() {
        int index;

        if (allowDuplicates) {
            index = ThreadLocalRandom.current().nextInt(tasks.size());
        } else {
            if (previousTaskIndex + 1 == tasks.size()) {
                throw new NoMoreTasksException();
            }
            index = ++previousTaskIndex;
        }

        return tasks.get(index);
    }
}

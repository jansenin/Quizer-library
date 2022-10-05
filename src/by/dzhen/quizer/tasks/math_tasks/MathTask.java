package by.dzhen.quizer.tasks.math_tasks;

import by.dzhen.quizer.tasks.Task;

/**
 * Doesn't work with non-integers
 */
public interface MathTask extends Task {
    interface Generator extends Task.Generator {
        enum Operation {
            ADD("+"),
            SUBTRACT("-"),
            MULTIPLY("*"),
            DIVIDE("/");

            private String stringRepresentation;

            Operation(String stringRepresentation) {
                this.stringRepresentation = stringRepresentation;
            }

            @Override
            public String toString() {
                return stringRepresentation;
            }

            public int perform(int a, int b) {
                return switch (this) {
                    case ADD -> a + b;
                    case SUBTRACT -> a - b;
                    case MULTIPLY -> a * b;
                    case DIVIDE -> a / b;
                };
            }
        }

        int getMinNumber();
        int getMaxNumber();

        /**
         * @return разница между максимальным и минимальным возможным числом
         */
        default int getDiffNumber() {
            return getMaxNumber() - getMinNumber();
        }
    }
}

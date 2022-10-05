package by.dzhen.quizer.tasks.math_tasks;

import by.dzhen.quizer.Result;
import by.dzhen.quizer.Utils;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractMathTask implements MathTask {
    public abstract static class Generator implements MathTask.Generator {
        protected class SimpleExpression {
            private int firstOperand;
            private int secondOperand;
            private Operation operation;
            private int answer;

            public SimpleExpression(int firstOperand, int secondOperand, Operation operation) {
                exceptionIfDivisionByZero(operation, secondOperand);

                this.firstOperand = firstOperand;
                this.secondOperand = secondOperand;
                this.operation = operation;

                answer = operation.perform(firstOperand, secondOperand);
            }

            public int getFirstOperand() {
                return firstOperand;
            }

            public int getSecondOperand() {
                return secondOperand;
            }

            public Operation getOperation() {
                return operation;
            }

            public int getAnswer() {
                return answer;
            }
        }

        protected int minNumber;
        protected int maxNumber;
        protected EnumSet<Operation> allowedOperations;

        Generator(
                int minNumber,
                int maxNumber,
                EnumSet<Operation> allowedOperations
        ) {
            this.minNumber = minNumber;
            this.maxNumber = maxNumber;
            this.allowedOperations = allowedOperations.clone();

            validateFields();
        }

        private void validateFields() {
            if (allowedOperations.size() == 0) {
                throw new IllegalArgumentException("There must be at least one allowed operation.");
            }
            if (minNumber > maxNumber) {
                throw new IllegalArgumentException("minNumber must be <= maxNumber.");
            }
        }

        @Override
        public int getMinNumber() {
            return minNumber;
        }

        @Override
        public int getMaxNumber() {
            return maxNumber;
        }

        protected SimpleExpression generateAllowedSimpleExpression() {
            Operation operation = generateRandomAllowedOperation();

            if (operation == Operation.DIVIDE) {
                return generateSimpleExpressionWithDivision();
            }

            int firstOperand = generateRandomAllowedOperand();
            int secondOperand = generateRandomAllowedOperand();

            return new SimpleExpression(firstOperand, secondOperand, operation);
        }

        private int generateRandomAllowedOperand() {
            return ThreadLocalRandom.current().nextInt(minNumber, maxNumber + 1);
        }

        private Operation generateRandomAllowedOperation() {
            return Utils.getRandomElementFrom(allowedOperations);
        }

        /**
         * generates such expression a / b = c, that a = b*c
         */
        private SimpleExpression generateSimpleExpressionWithDivision() {
            int dividend = ThreadLocalRandom.current().nextInt(minNumber, maxNumber + 1);
            List<Integer> dividers = Utils.getUniquePositiveDivisors(dividend);
            int index = ThreadLocalRandom.current().nextInt(dividers.size());
            int divider = dividers.get(index);

            if (Utils.trueWithProbabilityOf(computeNegativeDivisorProbability()) && -divider >= minNumber) {
                divider *= -1;
            }

            return new SimpleExpression(dividend, divider, Operation.DIVIDE);
        }

        private double computeNegativeDivisorProbability() {
            if (minNumber >= 0) {
                return 0;
            }
            if (maxNumber < 0) {
                return 1;
            }
            int positiveNumbersWithZeroCount = maxNumber + 1;
            return ((double) Math.abs(minNumber)) / positiveNumbersWithZeroCount;
        }

        protected String toStringWithSurroundingBracesIfNegative(int a) {
            return a < 0 ? "(" + a + ")" : Integer.toString(a);
        }

        private void exceptionIfDivisionByZero(Operation operation, int operand) {
            if (isDivisionByZero(operation, operand)) {
                throw new RuntimeException("Can't divide by zero");
            }
        }

        private boolean isDivisionByZero(Operation operation, int operand) {
            return operation == Operation.DIVIDE && operand == 0;
        }
    }

    protected String text;
    protected int answer;

    public AbstractMathTask(String expression, int answer) {
        this.text = generateText(expression);
        this.answer = answer;
    }

    protected abstract String generateText(String expression);

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Result validate(String answer) {
        if (!IsValidInput(answer)) {
            return Result.INCORRECT_INPUT;
        }

        int intAnswer = Integer.parseInt(answer);
        return this.answer == intAnswer ? Result.OK : Result.WRONG;
    }

    protected boolean IsValidInput(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

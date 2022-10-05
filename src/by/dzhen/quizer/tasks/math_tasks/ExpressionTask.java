package by.dzhen.quizer.tasks.math_tasks;

import java.util.EnumSet;

public class ExpressionTask extends AbstractMathTask {
    public static class Generator extends AbstractMathTask.Generator {
        public Generator(int minNumber, int maxNumber, EnumSet<Operation> allowedOperations) {
            super(minNumber, maxNumber, allowedOperations);
            validateFields();
        }

        private void validateFields() {
            if (onlyDivisionByZeroCanBeGenerated()) {
                throw new IllegalArgumentException("Only 0/0 (which is illegal) can be generated.");
            }
        }

        private boolean onlyDivisionByZeroCanBeGenerated() {
            return minNumber == 0 &&
                    maxNumber == 0 &&
                    allowedOperations.size() == 1 &&
                    allowedOperations.contains(Operation.DIVIDE);
        }

        @Override
        public ExpressionTask generate() {
            SimpleExpression simpleExpression = generateAllowedSimpleExpression();
            String expression = generateStringRepresentation(simpleExpression);

            return new ExpressionTask(expression, simpleExpression.getAnswer());
        }

        private String generateStringRepresentation(SimpleExpression simpleExpression) {
            return simpleExpression.getFirstOperand() +
                   " " +
                   simpleExpression.getOperation() +
                   " " +
                   toStringWithSurroundingBracesIfNegative(simpleExpression.getSecondOperand());
        }
    }

    public ExpressionTask(String expression, int answer) {
        super(expression, answer);
    }

    @Override
    protected String generateText(String expression) {
        return "Compute: " + expression;
    }
}

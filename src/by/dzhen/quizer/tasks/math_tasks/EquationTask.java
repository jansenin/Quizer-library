package by.dzhen.quizer.tasks.math_tasks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static by.dzhen.quizer.Utils.trueWithProbabilityOf;

public class EquationTask extends AbstractMathTask {
    public static class Generator extends AbstractMathTask.Generator {
        public Generator(int minNumber, int maxNumber, EnumSet<Operation> allowedOperations) {
            super(minNumber, maxNumber, allowedOperations);
            validateFields();
        }

        private void validateFields() {
            if (minNumber != maxNumber) {
                return;
            }
            if (allowedOperations.contains(Operation.ADD) ||
                allowedOperations.contains(Operation.SUBTRACT)) {
                return;
            }

            String exceptionMessagePrefix = "This generator can only generate ";
            List<String> exceptionMessageParts = new ArrayList<>();

            if (allowedOperations.contains(Operation.DIVIDE)) {
                exceptionMessageParts.add("0/0=? (which is invalid) ");
            }
            if (allowedOperations.contains(Operation.MULTIPLY)) {
                exceptionMessageParts.add("x*0=a or 0*x=a (which don't have one solution)");
            }

            String message = exceptionMessagePrefix + String.join(", ", exceptionMessageParts);
            throw new IllegalArgumentException(message);
        }

        @Override
        public EquationTask generate() {
            SimpleExpression simpleExpression = generateAllowedSimpleExpression();

            String expression;
            int answerToQuestion;
            if (trueWithProbabilityOf(0.5)) {
                // operand <op> x = result
                expression = generateStringWithEquationWithSecondUnknown(simpleExpression);
                answerToQuestion = simpleExpression.getSecondOperand();
            } else {
                // x <op> operand = result
                expression = generateStringWithEquationWithFirstUnknown(simpleExpression);
                answerToQuestion = simpleExpression.getFirstOperand();
            }

            return new EquationTask(expression, answerToQuestion);
        }

        private String generateStringWithEquationWithSecondUnknown(SimpleExpression e) {
            return e.getFirstOperand() + " " + e.getOperation() + " x = " + e.getAnswer();
        }

        private String generateStringWithEquationWithFirstUnknown(SimpleExpression e) {
            return "x " +
                   e.getOperation() +
                   " " +
                   toStringWithSurroundingBracesIfNegative(e.getSecondOperand()) +
                   " = " +
                   e.getAnswer();
        }
    }

    public EquationTask(String expression, int answer) {
        super(expression, answer);
    }

    @Override
    protected String generateText(String expression) {
        return "Find solution: " + expression;
    }
}

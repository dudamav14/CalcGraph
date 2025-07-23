package calcgraph.model.evaluator;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.Token;
import calcgraph.model.token.TokenType;
import java.util.List;

import java.util.Stack;

/**
 * Avalia uma expressão matemática na notação pós-fixada (RPN).
 */
public class PostfixEvaluator {

    /**
     * Avalia uma lista de tokens na notação pós-fixada e retorna o resultado.
     *
     * @param postfixTokens A lista de tokens na notação pós-fixada.
     * @return O resultado do cálculo.
     * @throws ExpressionException Se a expressão pós-fixada for inválida ou o cálculo resultar em erro (ex: divisão por zero).
     */
    public double evaluate(List<Token> postfixTokens) {
        Stack<Double> operandStack = new Stack<>();

        for (Token token : postfixTokens) {
            if (token.getType() == TokenType.NUMBER) {
                try {
                    operandStack.push(Double.parseDouble(token.getValue()));
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Número inválido: " + token.getValue(), e);
                }
            } else if (token.getType() == TokenType.CONSTANT) { // *** CORREÇÃO AQUI: Tratamento de CONSTANT ***
                if (token.getValue().equalsIgnoreCase("pi")) {
                    operandStack.push(Math.PI);
                } else if (token.getValue().equalsIgnoreCase("e")) {
                    operandStack.push(Math.E);
                } else {
                    // Isso não deveria acontecer se o parser for robusto, mas é uma segurança.
                    throw new ExpressionException("Constante desconhecida: " + token.getValue());
                }
            } else if (token.getType() == TokenType.OPERATOR) {
                if (operandStack.size() < 2) {
                    throw new ExpressionException("Sintaxe inválida: operador '" + token.getValue() + "' requer dois operandos.");
                }
                double operand2 = operandStack.pop();
                double operand1 = operandStack.pop();
                double result = applyOperator(token.getValue(), operand1, operand2);
                operandStack.push(result);
            } else if (token.getType() == TokenType.FUNCTION) {
                if (operandStack.isEmpty()) {
                    throw new ExpressionException("Sintaxe inválida: função '" + token.getValue() + "' requer um operando.");
                }
                double operand = operandStack.pop();
                double result = applyFunction(token.getValue(), operand);
                operandStack.push(result);
            } else {
                throw new ExpressionException("Tipo de token inesperado na avaliação pós-fixada: " + token.getType());
            }
        }

        if (operandStack.size() != 1) {
            throw new ExpressionException("Sintaxe inválida: expressão pós-fixada resultou em múltiplos valores ou nenhum.");
        }

        return operandStack.pop();
    }

    /**
     * Aplica a operação matemática.
     */
    private double applyOperator(String operator, double operand1, double operand2) {
        switch (operator) {
            case "+": return operand1 + operand2;
            case "-": return operand1 - operand2;
            case "*": return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ExpressionException("Divisão por zero.");
                }
                return operand1 / operand2;
            case "^": return Math.pow(operand1, operand2);
            default: throw new ExpressionException("Operador desconhecido: " + operator);
        }
    }

    /**
     * Aplica a função matemática.
     */
    private double applyFunction(String functionName, double operand) {
        switch (functionName.toLowerCase()) {
            case "sin": return Math.sin(operand);
            case "cos": return Math.cos(operand);
            case "tan": return Math.tan(operand);
            case "log": return Math.log10(operand); // Logaritmo base 10
            case "ln": return Math.log(operand);   // Logaritmo natural
            case "sqrt": return Math.sqrt(operand);
            case "abs": return Math.abs(operand);
            case "ceil": return Math.ceil(operand);
            case "floor": return Math.floor(operand);
            default: throw new ExpressionException("Função desconhecida: " + functionName);
        }
    }
}
package calcgraph.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia a precedência e associatividade dos operadores matemáticos.
 */
public class OperatorPrecedence {

    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    private static final Map<String, Boolean> LEFT_ASSOCIATIVITY = new HashMap<>();

    static {
        // Definir precedência (maior número = maior precedência)
        PRECEDENCE.put("+", 1);
        PRECEDENCE.put("-", 1);
        PRECEDENCE.put("*", 2);
        PRECEDENCE.put("/", 2);
        PRECEDENCE.put("^", 3);
        PRECEDENCE.put("!", 4);

        // Definir associatividade (true = esquerda para direita, false = direita para esquerda)
        LEFT_ASSOCIATIVITY.put("+", true);
        LEFT_ASSOCIATIVITY.put("-", true);
        LEFT_ASSOCIATIVITY.put("*", true);
        LEFT_ASSOCIATIVITY.put("/", true);
        LEFT_ASSOCIATIVITY.put("^", false);
        LEFT_ASSOCIATIVITY.put("!", false);
    }

    public static int getPrecedence(String operator) {
        return PRECEDENCE.getOrDefault(operator, 0);
    }

    public static boolean isLeftAssociative(String operator) {
        return LEFT_ASSOCIATIVITY.getOrDefault(operator, true);
    }

    public static boolean isOperator(String token) {
        return PRECEDENCE.containsKey(token);
    }
}
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
        PRECEDENCE.put("^", 3); // Potência tem a maior precedência

        // Definir associatividade (true = esquerda para direita, false = direita para esquerda)
        LEFT_ASSOCIATIVITY.put("+", true);
        LEFT_ASSOCIATIVITY.put("-", true);
        LEFT_ASSOCIATIVITY.put("*", true);
        LEFT_ASSOCIATIVITY.put("/", true);
        LEFT_ASSOCIATIVITY.put("^", false); // Potência é associativa à direita
    }

    /**
     * Retorna a precedência de um operador.
     *
     * @param operator O operador.
     * @return O nível de precedência.
     */
    public static int getPrecedence(String operator) {
        return PRECEDENCE.getOrDefault(operator, 0); // 0 para operadores não mapeados (e.g., parênteses)
    }

    /**
     * Verifica se um operador tem associatividade à esquerda.
     *
     * @param operator O operador.
     * @return true se for associativo à esquerda, false caso contrário.
     */
    public static boolean isLeftAssociative(String operator) {
        return LEFT_ASSOCIATIVITY.getOrDefault(operator, true); // Padrão para esquerda se não definido
    }

    /**
     * Verifica se o token é um operador válido.
     * @param token O token a ser verificado.
     * @return true se for um operador, false caso contrário.
     */
    public static boolean isOperator(String token) {
        return PRECEDENCE.containsKey(token);
    }
}
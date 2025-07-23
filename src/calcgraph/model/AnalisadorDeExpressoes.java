package calcgraph.model;

import calcgraph.model.evaluator.PostfixEvaluator;
import calcgraph.model.exception.ExpressionException;
import calcgraph.model.parser.ExpressionParser;
import calcgraph.model.parser.ShuntingYardAlgorithm;
import calcgraph.model.token.Token;
import calcgraph.model.token.TokenType; // Importe TokenType

import java.util.List;
import java.util.Stack; // Ainda necessário para a validação de parênteses

/**
 * A classe AnalisadorDeExpressoes é o ponto de entrada principal para
 * validar, converter e avaliar expressões matemáticas.
 * Ela orquestra o uso do ExpressionParser, ShuntingYardAlgorithm e PostfixEvaluator.
 */
public class AnalisadorDeExpressoes {

    private static final String VALID_CHARS_BASIC_REGEX = "[0-9+\\-*/^().,a-zA-Z\\s]+";

    /**
     * Realiza uma validação preliminar da expressão, focando em:
     * - Caracteres permitidos.
     * - Balanceamento de parênteses.
     *
     * @param expressao A string da expressão a ser validada.
     * @return true se a validação preliminar passar, false caso contrário (para balanceamento).
     * @throws ExpressionException se contiver caracteres inválidos.
     */
    public static boolean validarExpressaoPreliminar(String expressao) {
        if (expressao == null || expressao.trim().isEmpty()) {
            return false;
        }

        String expr = expressao.replaceAll("\\s+", "");

        // Validação de caracteres inválidos
        if (!expr.matches(VALID_CHARS_BASIC_REGEX)) {
            throw new ExpressionException("Expressão contém caracteres inválidos.");
        }

        // Validação de balanceamento de parênteses
        Stack<Character> pilhaParenteses = new Stack<>();
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                pilhaParenteses.push(c);
            } else if (c == ')') {
                if (pilhaParenteses.isEmpty()) {
                    return false; // Parêntese de fechamento sem um de abertura correspondente
                }
                pilhaParenteses.pop();
            }
        }
        return pilhaParenteses.isEmpty(); // Retorna true se todos os parênteses foram balanceados
    }

    /**
     * Avalia uma expressão matemática infixa e retorna o resultado.
     * Este é o método principal a ser chamado externamente.
     *
     * @param expressao A string da expressão infixa a ser avaliada.
     * @return O resultado do cálculo da expressão.
     * @throws ExpressionException se a expressão for inválida ou o cálculo falhar.
     */
    public static double avaliarExpressao(String expressao) {
        // 1. Validação preliminar
        if (!validarExpressaoPreliminar(expressao)) {
            throw new ExpressionException("Expressão inválida: Parênteses desbalanceados ou vazia.");
        }

        ExpressionParser parser = new ExpressionParser();
        ShuntingYardAlgorithm shuntingYard = new ShuntingYardAlgorithm();
        PostfixEvaluator evaluator = new PostfixEvaluator();

        List<Token> infixTokensPreProcessed; // Lista de tokens APÓS o pré-processamento de unários
        List<Token> postfixTokens;
        double result;

        try {
            // 2. Tokenização da expressão (já inclui o pré-processamento de unários e retorna a lista processada)
            infixTokensPreProcessed = parser.tokenize(expressao); // <--- AQUI ESTÁ A MUDANÇA!

            // 3. Validação da sequência de tokens (sintaxe mais granular)
            // Agora validamos a lista JÁ PROCESSADA
            parser.validateTokenSequence(infixTokensPreProcessed);

            // 4. Conversão de infixa para pós-fixada
            // Agora convertemos a lista JÁ PROCESSADA
            postfixTokens = shuntingYard.convertToPostfix(infixTokensPreProcessed);

            // Impressão para depuração (opcional)
            // Agora sim, esta linha vai mostrar a lista com os '0's inseridos
            System.out.println("Tokens Infixos (Processados): " + infixTokensPreProcessed);
            System.out.println("Tokens Pós-Fixados: " + postfixTokens);

            // 5. Avaliação da expressão pós-fixada
            result = evaluator.evaluate(postfixTokens);

        } catch (ExpressionException e) {
            throw e;
        } catch (Exception e) {
            throw new ExpressionException("Erro inesperado ao avaliar a expressão: " + e.getMessage(), e);
        }

        return result;
    }

    // Não precisamos mais de um main de teste aqui, pois o teste será na CalcGraph.java
    // O main de teste anterior pode ser removido ou comentado.
    // public static void main(String[] args) { ... }
}
package calcgraph.model;

import calcgraph.model.evaluator.PostfixEvaluator;
import calcgraph.model.exception.ExpressionException;
import calcgraph.model.parser.ExpressionParser;
import calcgraph.model.parser.ShuntingYardAlgorithm;
import calcgraph.model.token.Token;

import java.util.List;
import java.util.Stack;

/**
 * A classe AnalisadorDeExpressoes é o ponto de entrada principal para
 * validar, converter e avaliar expressões matemáticas.
 * Ela orquestra o uso do ExpressionParser, ShuntingYardAlgorithm e PostfixEvaluator.
 */
public class AnalisadorDeExpressoes {

    private static final String VALID_CHARS_BASIC_REGEX = "[0-9+\\-*/^().,a-zA-Z!\\s]+";

    public static boolean validarExpressaoPreliminar(String expressao) {
        if (expressao == null || expressao.trim().isEmpty()) {
            return false;
        }
        String expr = expressao.replaceAll("\\s+", "");

        if (!expr.matches(VALID_CHARS_BASIC_REGEX)) {
            throw new ExpressionException("Expressão contém caracteres inválidos.");
        }

        Stack<Character> pilhaParenteses = new Stack<>();
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                pilhaParenteses.push(c);
            } else if (c == ')') {
                if (pilhaParenteses.isEmpty()) {
                    return false;
                }
                pilhaParenteses.pop();
            }
        }
        return pilhaParenteses.isEmpty();
    }

   
    public static double avaliarExpressao(String expressao) {
        if (!validarExpressaoPreliminar(expressao)) {
            throw new ExpressionException("Expressão inválida: Parênteses desbalanceados ou vazia.");
        }

        ExpressionParser parser = new ExpressionParser();
        ShuntingYardAlgorithm shuntingYard = new ShuntingYardAlgorithm();
        PostfixEvaluator evaluator = new PostfixEvaluator();

        List<Token> infixTokensPreProcessed;
        List<Token> postfixTokens;
        double result;

        try {
            infixTokensPreProcessed = parser.tokenize(expressao); 
            System.out.println("Tokens Infixos (Sem validação): " + infixTokensPreProcessed);
            parser.validateTokenSequence(infixTokensPreProcessed);
            postfixTokens = shuntingYard.convertToPostfix(infixTokensPreProcessed);

            System.out.println("Tokens Infixos (Processados): " + infixTokensPreProcessed);
            System.out.println("Tokens Pós-Fixados: " + postfixTokens);

            result = evaluator.evaluate(postfixTokens);

        } catch (ExpressionException e) {
            throw e;
        } catch (Exception e) {
            throw new ExpressionException("Erro inesperado ao avaliar a expressão: " + e.getMessage(), e);
        }

        return result;
    }
}
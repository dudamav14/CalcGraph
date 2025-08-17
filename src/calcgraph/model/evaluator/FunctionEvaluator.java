/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calcgraph.model.evaluator;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.parser.ExpressionParser;
import calcgraph.model.parser.ShuntingYardAlgorithm;
import calcgraph.model.token.Token;
import calcgraph.model.token.TokenType;
import java.util.List;

/**
 *
 * @author Caio
 */
public class FunctionEvaluator {
    private final List<Token> postfixTokens;

    public FunctionEvaluator(String expression) throws ExpressionException {
        // Pré-processa a expressão uma única vez no construtor.
        ExpressionParser parser = new ExpressionParser();
        ShuntingYardAlgorithm shuntingYard = new ShuntingYardAlgorithm();

        List<Token> infixTokens = parser.tokenize(expression);
        parser.validateTokenSequence(infixTokens);
        this.postfixTokens = shuntingYard.convertToPostfix(infixTokens);
    }

    // Método de avaliação RÁPIDA
    public double evaluate(double x) {
        // Usa o PostfixEvaluator para avaliar a expressão para um dado 'x'.
        PostfixEvaluator evaluator = new PostfixEvaluator();
        
        // Substitui a variável 'x' na lista de tokens por um token de número
        List<Token> tokensWithX = new java.util.ArrayList<>(this.postfixTokens);
        for (int i = 0; i < tokensWithX.size(); i++) {
            if (tokensWithX.get(i).getType() == TokenType.VARIABLE) {
                tokensWithX.set(i, new Token(TokenType.VARIABLE, Double.toString(x)) {});
            }
        }
        
        return evaluator.evaluate(tokensWithX);
    }
}

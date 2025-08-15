package calcgraph.model.parser;

import calcgraph.model.OperatorPrecedence;
import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Implementa o algoritmo Shunting-yard para converter uma lista de tokens infixos
 * (notação normal) para uma lista de tokens pós-fixados (Notação Polonesa Inversa - RPN).
 */
public class ShuntingYardAlgorithm {

    /**
     * Converte uma lista de tokens infixos para pós-fixados.
     *
     * @param infixTokens A lista de tokens na notação infixa.
     * @return Uma lista de tokens na notação pós-fixada.
     * @throws ExpressionException Se houver um erro durante a conversão (ex: parênteses desbalanceados).
     */
    public List<Token> convertToPostfix(List<Token> infixTokens) { // entender convertoposfix
        List<Token> outputQueue = new ArrayList<>();
        Stack<Token> operatorStack = new Stack<>();

        for (Token token : infixTokens) {
            TokenType type = token.getType();
            String value = token.getValue();

            switch (type) {
                case NUMBER:
                case CONSTANT: // *** CORREÇÃO AQUI: Tratar CONSTANT como um operando ***
                    outputQueue.add(token);
                    break;
                case FUNCTION:
                    operatorStack.push(token);
                    break;
                case SEPARATOR: 
                    while (!operatorStack.isEmpty() && !operatorStack.peek().getValue().equals("(")) {
                        outputQueue.add(operatorStack.pop());
                    }
                    if (operatorStack.isEmpty()) {
                        throw new ExpressionException("Vírgula ',' em posição inválida ou parênteses desbalanceados.");
                    }
                    break; // Pula o resto da iteração, não empilha a vírgula
                case OPERATOR:
                    if (value.equals("!")) {
                        // Fatorial é pós-fixo, então só empilha sem comparar precedência
                        operatorStack.push(token);
                        break;
                    }
                    while (!operatorStack.isEmpty() && operatorStack.peek().getType() == TokenType.OPERATOR &&
                           ((OperatorPrecedence.isLeftAssociative(value) && OperatorPrecedence.getPrecedence(value) <= OperatorPrecedence.getPrecedence(operatorStack.peek().getValue())) ||
                            (!OperatorPrecedence.isLeftAssociative(value) && OperatorPrecedence.getPrecedence(value) < OperatorPrecedence.getPrecedence(operatorStack.peek().getValue())))) {
                        outputQueue.add(operatorStack.pop());
                    }
                    operatorStack.push(token);
                    break;
                case PARENTHESIS:
                    if (value.equals("(")) {
                        operatorStack.push(token);
                    } else { // value.equals(")")
                        while (!operatorStack.isEmpty() && !operatorStack.peek().getValue().equals("(")) {
                            outputQueue.add(operatorStack.pop());
                        }
                        if (operatorStack.isEmpty()) {
                            throw new ExpressionException("Parênteses desbalanceados: falta abre parênteses.");
                        }
                        operatorStack.pop(); // Pop the left parenthesis
                        // Se o topo da pilha é uma função, joga para a fila de saída (funções são prefixas)
                        if (!operatorStack.isEmpty() && operatorStack.peek().getType() == TokenType.FUNCTION) {
                            outputQueue.add(operatorStack.pop());
                        }
                    }
                    break;
                default:
                    throw new ExpressionException("Tipo de token desconhecido: " + token.getType() + " com valor: " + token.getValue());
            }
        }

        while (!operatorStack.isEmpty()) {
            Token op = operatorStack.pop();
            if (op.getType() == TokenType.PARENTHESIS) {
                throw new ExpressionException("Parênteses desbalanceados: falta fecha parênteses.");
            }
            outputQueue.add(op);
        }

        return outputQueue;
    }
}
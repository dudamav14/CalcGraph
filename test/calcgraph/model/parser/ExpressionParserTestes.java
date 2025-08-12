package calcgraph.model.parser;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.*;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static java.util.Arrays.asList;

/**
 * Classe de testes para ExpressionParser.
 * Cobre a tokenização e a validação de sequências de tokens em expressões.
 */
public class ExpressionParserTestes {

    private final ExpressionParser parser = new ExpressionParser();

    // --- Testes para o método tokenize ---

    @Test
    public void testTokenize_SimpleExpression() {
        // Testa a tokenização de uma expressão simples com números, operadores, funções e constantes.
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        
        assertEquals(8, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("1", tokens.get(0).getValue());
        assertEquals(TokenType.OPERATOR, tokens.get(1).getType());
        assertEquals("+", tokens.get(1).getValue());
        assertEquals(TokenType.FUNCTION, tokens.get(4).getType());
        assertEquals("sin", tokens.get(4).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(5).getType());
        assertEquals("(", tokens.get(5).getValue());
        assertEquals(TokenType.CONSTANT, tokens.get(6).getType());
        assertEquals("pi", tokens.get(6).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(7).getType());
        assertEquals(")", tokens.get(7).getValue());
    }

    @Test
    public void testTokenize_NegativeNumber() {
        // Testa a tokenização de um número negativo, que o código converte para (0-3).
        String expression = "5 * (-3)";
        List<Token> tokens = parser.tokenize(expression);
        
        assertEquals(9, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("5", tokens.get(0).getValue());
        assertEquals(TokenType.OPERATOR, tokens.get(1).getType());
        assertEquals("*", tokens.get(1).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(2).getType());
        assertEquals("(", tokens.get(2).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(3).getType());
        assertEquals("(", tokens.get(3).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(4).getType());
        assertEquals("0", tokens.get(4).getValue());
        assertEquals(TokenType.OPERATOR, tokens.get(5).getType());
        assertEquals("-", tokens.get(5).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(6).getType());
        assertEquals("3", tokens.get(6).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(7).getType());
        assertEquals(")", tokens.get(7).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(8).getType());
        assertEquals(")", tokens.get(8).getValue());
    }

    // --- Testes para o método validateTokenSequence (casos de erro) ---
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_EmptyExpression() {
        // Testa se a validação falha para uma lista de tokens vazia.
        List<Token> tokens = asList();
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_OperatorAtEnd() {
        // Testa se a validação falha para uma expressão que termina com um operador.
        List<Token> tokens = asList(new NumberToken("5"), new OperatorToken("+"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_TwoConsecutiveOperators() {
        // Testa se a validação falha para dois operadores consecutivos.
        List<Token> tokens = asList(new NumberToken("5"), new OperatorToken("+"), new OperatorToken("*"), new NumberToken("3"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_EmptyParentheses() {
        // Testa se a validação falha para parênteses vazios.
        List<Token> tokens = asList(new ParenthesisToken("("), new ParenthesisToken(")"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_NumberBeforeFunction() {
        // Testa se a validação falha para um número seguido de uma função sem operador.
        List<Token> tokens = asList(new NumberToken("5"), new FunctionToken("sin"));
        parser.validateTokenSequence(tokens);
    }
}
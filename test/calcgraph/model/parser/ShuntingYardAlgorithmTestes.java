package calcgraph.model.parser;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.*;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Classe de testes para o ShuntingYardAlgorithm.
 * Cobre a conversão de expressões infixas para pós-fixadas em vários cenários.
 */
public class ShuntingYardAlgorithmTestes {

    private final ShuntingYardAlgorithm algorithm = new ShuntingYardAlgorithm();

    // --- Testes para expressões válidas ---

    @Test
    public void testConvertToPostfix_SimpleExpression() {
        // Testa uma expressão simples como "3 + 4".
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("3"), new OperatorToken("+"), new NumberToken("4")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        assertEquals(3, postfixTokens.size());
        assertEquals(TokenType.NUMBER, postfixTokens.get(0).getType());
        assertEquals("3", postfixTokens.get(0).getValue());
        assertEquals(TokenType.NUMBER, postfixTokens.get(1).getType());
        assertEquals("4", postfixTokens.get(1).getValue());
        assertEquals(TokenType.OPERATOR, postfixTokens.get(2).getType());
        assertEquals("+", postfixTokens.get(2).getValue());
    }

    @Test
    public void testConvertToPostfix_Precedence() {
        // Testa uma expressão com precedência de operadores como "2 * 3 + 4".
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("2"), new OperatorToken("*"), new NumberToken("3"), new OperatorToken("+"), new NumberToken("4")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        assertEquals(5, postfixTokens.size());
        assertEquals(TokenType.NUMBER, postfixTokens.get(0).getType());
        assertEquals("2", postfixTokens.get(0).getValue());
        assertEquals(TokenType.NUMBER, postfixTokens.get(1).getType());
        assertEquals("3", postfixTokens.get(1).getValue());
        assertEquals(TokenType.OPERATOR, postfixTokens.get(2).getType());
        assertEquals("*", postfixTokens.get(2).getValue());
        assertEquals(TokenType.NUMBER, postfixTokens.get(3).getType());
        assertEquals("4", postfixTokens.get(3).getValue());
        assertEquals(TokenType.OPERATOR, postfixTokens.get(4).getType());
        assertEquals("+", postfixTokens.get(4).getValue());
    }

    @Test
    public void testConvertToPostfix_Parentheses() {
        // Testa uma expressão com parênteses, como "(2 + 3) * 4".
        List<Token> infixTokens = Arrays.asList(
            new ParenthesisToken("("), new NumberToken("2"), new OperatorToken("+"), new NumberToken("3"), new ParenthesisToken(")"), new OperatorToken("*"), new NumberToken("4")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        assertEquals(5, postfixTokens.size());
        assertEquals(TokenType.NUMBER, postfixTokens.get(0).getType());
        assertEquals("2", postfixTokens.get(0).getValue());
        assertEquals(TokenType.NUMBER, postfixTokens.get(1).getType());
        assertEquals("3", postfixTokens.get(1).getValue());
        assertEquals(TokenType.OPERATOR, postfixTokens.get(2).getType());
        assertEquals("+", postfixTokens.get(2).getValue());
        assertEquals(TokenType.NUMBER, postfixTokens.get(3).getType());
        assertEquals("4", postfixTokens.get(3).getValue());
        assertEquals(TokenType.OPERATOR, postfixTokens.get(4).getType());
        assertEquals("*", postfixTokens.get(4).getValue());
    }

    @Test
    public void testConvertToPostfix_Function() {
        // Testa uma expressão com uma função e uma constante, como "sin(3.14)".
        List<Token> infixTokens = Arrays.asList(
            new FunctionToken("sin"), new ParenthesisToken("("), new NumberToken("3.14"), new ParenthesisToken(")")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        assertEquals(2, postfixTokens.size());
        assertEquals(TokenType.NUMBER, postfixTokens.get(0).getType());
        assertEquals("3.14", postfixTokens.get(0).getValue());
        assertEquals(TokenType.FUNCTION, postfixTokens.get(1).getType());
        assertEquals("sin", postfixTokens.get(1).getValue());
    }


    // --- Testes para casos de erro ---

    @Test(expected = ExpressionException.class)
    public void testConvertToPostfix_UnbalancedParentheses_MissingClose() {
        // Testa uma expressão com parênteses desbalanceados (falta ')').
        List<Token> infixTokens = Arrays.asList(
            new ParenthesisToken("("), new NumberToken("2"), new OperatorToken("+"), new NumberToken("3")
        );
        algorithm.convertToPostfix(infixTokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testConvertToPostfix_UnbalancedParentheses_MissingOpen() {
        // Testa uma expressão com parênteses desbalanceados (falta '(').
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("2"), new OperatorToken("+"), new NumberToken("3"), new ParenthesisToken(")")
        );
        algorithm.convertToPostfix(infixTokens);
    }
}
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
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("3"), new NumberToken("4"), new OperatorToken("+")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }

    @Test
    public void testConvertToPostfix_Precedence() {
        // Testa uma expressão com precedência de operadores como "2 * 3 + 4".
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("2"), new OperatorToken("*"), new NumberToken("3"), new OperatorToken("+"), new NumberToken("4")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("2"), new NumberToken("3"), new OperatorToken("*"), new NumberToken("4"), new OperatorToken("+")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }

    @Test
    public void testConvertToPostfix_Parentheses() {
        // Testa uma expressão com parênteses, como "(2 + 3) * 4".
        List<Token> infixTokens = Arrays.asList(
            new ParenthesisToken("("), new NumberToken("2"), new OperatorToken("+"), new NumberToken("3"), new ParenthesisToken(")"), new OperatorToken("*"), new NumberToken("4")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("2"), new NumberToken("3"), new OperatorToken("+"), new NumberToken("4"), new OperatorToken("*")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }

    @Test
    public void testConvertToPostfix_Function() {
        // Testa uma expressão com uma função e uma constante, como "sin(3.14)".
        List<Token> infixTokens = Arrays.asList(
            new FunctionToken("sin"), new ParenthesisToken("("), new NumberToken("3.14"), new ParenthesisToken(")")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("3.14"), new FunctionToken("sin")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }


    // --- Testes para casos de erro ---

    @Test
    public void testConvertToPostfix_UnbalancedParentheses_MissingClose() {
        // Testa uma expressão com parênteses desbalanceados (falta ')').
        List<Token> infixTokens = Arrays.asList(
            new ParenthesisToken("("), new NumberToken("2"), new OperatorToken("+"), new NumberToken("3")
        );
        
        assertThrows(ExpressionException.class, () -> {
            algorithm.convertToPostfix(infixTokens);
        });
    }
    
    @Test
    public void testConvertToPostfix_UnbalancedParentheses_MissingOpen() {
        // Testa uma expressão com parênteses desbalanceados (falta '(').
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("2"), new OperatorToken("+"), new NumberToken("3"), new ParenthesisToken(")")
        );
        
        assertThrows(ExpressionException.class, () -> {
            algorithm.convertToPostfix(infixTokens);
        });
    }
}
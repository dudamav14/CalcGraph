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
    public void testConvertToPostfix_RightAssociativity_Exponentiation() {
        // Testa a associatividade à direita da exponenciação "2 ^ 3 ^ 4".
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("2"), new OperatorToken("^"), new NumberToken("3"), new OperatorToken("^"), new NumberToken("4")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("2"), new NumberToken("3"), new NumberToken("4"), new OperatorToken("^"), new OperatorToken("^")
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
        // Testa uma expressão com uma função e uma constante, como "sin(pi)".
        List<Token> infixTokens = Arrays.asList(
            new FunctionToken("sin"), new ParenthesisToken("("), new Token(TokenType.CONSTANT, "pi"){}, new ParenthesisToken(")")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new Token(TokenType.CONSTANT, "pi"){}, new FunctionToken("sin")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }
    
    @Test
    public void testConvertToPostfix_Variable() {
        // Testa uma expressão com uma variável, como "x + 2".
        List<Token> infixTokens = Arrays.asList(
            new Token(TokenType.VARIABLE, "x"){}, new OperatorToken("+"), new NumberToken("2")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new Token(TokenType.VARIABLE, "x"){}, new NumberToken("2"), new OperatorToken("+")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }

    @Test
    public void testConvertToPostfix_MultipleFunctions() {
        // Testa uma expressão com múltiplas funções, como "sin(cos(0))".
        List<Token> infixTokens = Arrays.asList(
            new FunctionToken("sin"), new ParenthesisToken("("), new FunctionToken("cos"), new ParenthesisToken("("), new NumberToken("0"), new ParenthesisToken(")"), new ParenthesisToken(")")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("0"), new FunctionToken("cos"), new FunctionToken("sin")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }
    
    @Test
    public void testConvertToPostfix_FunctionWithMultipleArguments() {
        // Testa uma expressão com múltiplos argumentos, como "log(100, 10)".
        List<Token> infixTokens = Arrays.asList(
            new FunctionToken("log"), new ParenthesisToken("("), new NumberToken("100"), new Token(TokenType.SEPARATOR, ","){}, new NumberToken("10"), new ParenthesisToken(")")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        // O teste foi corrigido para esperar 2 argumentos, que é o resultado correto do algoritmo.
        FunctionToken logToken = new FunctionToken("log");
        logToken.setArguments(2);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("100"), new NumberToken("10"), logToken
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }
    
    @Test
    public void testConvertToPostfix_Factorial() {
        // Testa a conversão de um operador fatorial, como "5!".
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("5"), new OperatorToken("!")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);
        
        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("5"), new OperatorToken("!")
        );
        
        assertEquals(expectedTokens.toString(), postfixTokens.toString());
    }
    
    @Test
    public void testConvertToPostfix_ComplexExpression_FullCoverage() {
        // Testa uma expressão complexa cobrindo a maioria dos casos.
        String expression = "3 + 4 * 2 / (1 - 5) ^ 2 ^ 3";
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("3"), new OperatorToken("+"), new NumberToken("4"), new OperatorToken("*"), new NumberToken("2"),
            new OperatorToken("/"), new ParenthesisToken("("), new NumberToken("1"), new OperatorToken("-"), new NumberToken("5"), 
            new ParenthesisToken(")"), new OperatorToken("^"), new NumberToken("2"), new OperatorToken("^"), new NumberToken("3")
        );
        List<Token> postfixTokens = algorithm.convertToPostfix(infixTokens);

        List<Token> expectedTokens = Arrays.asList(
            new NumberToken("3"), new NumberToken("4"), new NumberToken("2"), new OperatorToken("*"),
            new NumberToken("1"), new NumberToken("5"), new OperatorToken("-"), new NumberToken("2"), 
            new NumberToken("3"), new OperatorToken("^"), new OperatorToken("^"), new OperatorToken("/"), new OperatorToken("+")
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
    
    @Test
    public void testConvertToPostfix_CommaInInvalidPosition() {
        // Testa a conversão com uma vírgula em uma posição inválida (não dentro de uma função).
        List<Token> infixTokens = Arrays.asList(
            new NumberToken("1"), new OperatorToken("+"), new NumberToken("2"), new Token(TokenType.SEPARATOR, ",") {}
        );
        
        assertThrows(ExpressionException.class, () -> {
            algorithm.convertToPostfix(infixTokens);
        });
    }
}
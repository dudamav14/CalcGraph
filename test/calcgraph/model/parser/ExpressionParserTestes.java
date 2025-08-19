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
    public void testTokenize_SimpleExpression_CorrectSize() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(8, tokens.size());
    }

    @Test
    public void testTokenize_SimpleExpression_Number1() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("1", tokens.get(0).getValue());
    }

    @Test
    public void testTokenize_SimpleExpression_OperatorPlus() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.OPERATOR, tokens.get(1).getType());
        assertEquals("+", tokens.get(1).getValue());
    }

    @Test
    public void testTokenize_SimpleExpression_FunctionSin() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.FUNCTION, tokens.get(4).getType());
        assertEquals("sin", tokens.get(4).getValue());
    }

    @Test
    public void testTokenize_SimpleExpression_ParenthesisOpen() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.PARENTHESIS, tokens.get(5).getType());
        assertEquals("(", tokens.get(5).getValue());
    }

    @Test
    public void testTokenize_SimpleExpression_ConstantPi() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.CONSTANT, tokens.get(6).getType());
        assertEquals("pi", tokens.get(6).getValue());
    }

    @Test
    public void testTokenize_SimpleExpression_ParenthesisClose() {
        String expression = "1 + 2 * sin(pi)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.PARENTHESIS, tokens.get(7).getType());
        assertEquals(")", tokens.get(7).getValue());
    }

    @Test
    public void testTokenize_NegativeNumber_CorrectSize() {
        String expression = "5 * (-3)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(9, tokens.size());
    }

    @Test
    public void testTokenize_NegativeNumber_Part1() {
        String expression = "5 * (-3)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("5", tokens.get(0).getValue());
        assertEquals(TokenType.OPERATOR, tokens.get(1).getType());
        assertEquals("*", tokens.get(1).getValue());
    }

    @Test
    public void testTokenize_NegativeNumber_Part2() {
        String expression = "5 * (-3)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.PARENTHESIS, tokens.get(2).getType());
        assertEquals("(", tokens.get(2).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(4).getType());
        assertEquals("0", tokens.get(4).getValue());
    }

    @Test
    public void testTokenize_NegativeNumber_Part3() {
        String expression = "5 * (-3)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.OPERATOR, tokens.get(5).getType());
        assertEquals("-", tokens.get(5).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(6).getType());
        assertEquals("3", tokens.get(6).getValue());
    }

    @Test
    public void testTokenize_NegativeNumber_Part4() {
        String expression = "5 * (-3)";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.PARENTHESIS, tokens.get(7).getType());
        assertEquals(")", tokens.get(7).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(8).getType());
        assertEquals(")", tokens.get(8).getValue());
    }
    
    // --- Novos testes para o método tokenize ---
    
    @Test
    public void testTokenize_ExpressionWithVariable() {
        String expression = "x^2 + 2x - 1";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(8, tokens.size());
        assertEquals(TokenType.VARIABLE, tokens.get(0).getType());
        assertEquals("x", tokens.get(0).getValue());
    }

    @Test
    public void testTokenize_ExpressionWithDecimalNumber() {
        String expression = "3.14 + 1.5";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(3, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("3.14", tokens.get(0).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(2).getType());
        assertEquals("1.5", tokens.get(2).getValue());
    }

    @Test
    public void testTokenize_ExpressionWithUnaryMinus_CorrectSize() {
        String expression = "-5 + 2";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(7, tokens.size());
    }

    @Test
    public void testTokenize_ExpressionWithUnaryMinus_Tokens() {
        String expression = "-5 + 2";
        List<Token> tokens = parser.tokenize(expression);
        assertEquals(TokenType.PARENTHESIS, tokens.get(0).getType());
        assertEquals("(", tokens.get(0).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(1).getType());
        assertEquals("0", tokens.get(1).getValue());
        assertEquals(TokenType.OPERATOR, tokens.get(2).getType());
        assertEquals("-", tokens.get(2).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(3).getType());
        assertEquals("5", tokens.get(3).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(4).getType());
        assertEquals(")", tokens.get(4).getValue());
    }

    @Test(expected = ExpressionException.class)
    public void testTokenize_InvalidCharacters() {
        parser.tokenize("2 * @x - 5");
    }
    
    // --- Testes para o método validateTokenSequence (casos de erro) ---
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_EmptyExpression() {
        List<Token> tokens = asList();
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_OperatorAtEnd() {
        List<Token> tokens = asList(new NumberToken("5"), new OperatorToken("+"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_TwoConsecutiveOperators() {
        List<Token> tokens = asList(new NumberToken("5"), new OperatorToken("+"), new OperatorToken("*"), new NumberToken("3"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_EmptyParentheses() {
        List<Token> tokens = asList(new ParenthesisToken("("), new ParenthesisToken(")"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_NumberBeforeFunction() {
        List<Token> tokens = asList(new NumberToken("5"), new FunctionToken("sin"));
        parser.validateTokenSequence(tokens);
    }
    
    // --- Novos testes para o método validateTokenSequence ---
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_OperatorAtStart() {
        List<Token> tokens = asList(new OperatorToken("/"), new NumberToken("5"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_FactorialAtStart() {
        List<Token> tokens = asList(new OperatorToken("!"), new NumberToken("5"));
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_NumberBeforeOpenParenthesis() {
        List<Token> tokens = asList(new NumberToken("5"), new ParenthesisToken("("), new NumberToken("3"), new ParenthesisToken(")"));
        parser.validateTokenSequence(tokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_CloseParenthesisBeforeNumber() {
        List<Token> tokens = asList(new ParenthesisToken("("), new NumberToken("3"), new ParenthesisToken(")"), new NumberToken("5"));
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_FunctionNotFollowedByParenthesis() {
        List<Token> tokens = asList(new FunctionToken("sin"), new NumberToken("3"));
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_ConstantBeforeOpenParenthesis() {
        List<Token> tokens = asList(new Token(TokenType.CONSTANT, "pi") {}, new ParenthesisToken("("));
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_OperatorBeforeCloseParenthesis() {
        List<Token> tokens = asList(new ParenthesisToken("("), new NumberToken("5"), new OperatorToken("+"), new ParenthesisToken(")"));
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_CommaAtEnd() {
        List<Token> tokens = asList(new NumberToken("5"), new Token(TokenType.SEPARATOR, ",") {});
        parser.validateTokenSequence(tokens);
    }

    @Test(expected = ExpressionException.class)
    public void testValidateTokenSequence_CommaAfterFunction() {
        List<Token> tokens = asList(new FunctionToken("sin"), new Token(TokenType.SEPARATOR, ",") {}, new NumberToken("5"));
        parser.validateTokenSequence(tokens);
    }
}
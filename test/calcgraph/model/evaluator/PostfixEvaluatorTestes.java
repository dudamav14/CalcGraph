package calcgraph.model.evaluator;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.FunctionToken;
import calcgraph.model.token.NumberToken;
import calcgraph.model.token.OperatorToken;
import calcgraph.model.token.Token;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PostfixEvaluatorTestes {

    private final PostfixEvaluator evaluator = new PostfixEvaluator();

    // Testes para operações matemáticas
    @Test
    public void testEvaluate_SimpleAddition() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("5"), new NumberToken("3"), new OperatorToken("+"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(8.0, result, 0.0001);
    }
    
    @Test
    public void testEvaluate_SimpleSubtraction() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("10"), new NumberToken("4"), new OperatorToken("-"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(6.0, result, 0.0001);
    }

    @Test
    public void testEvaluate_SimpleMultiplication() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("6"), new NumberToken("7"), new OperatorToken("*"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(42.0, result, 0.0001);
    }
    
    @Test
    public void testEvaluate_SimpleDivision() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("20"), new NumberToken("5"), new OperatorToken("/"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(4.0, result, 0.0001);
    }
    
    @Test
    public void testEvaluate_SimpleExponentiation() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("2"), new NumberToken("3"), new OperatorToken("^"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(8.0, result, 0.0001);
    }

    // Testes para funções trigonométricas e logarítmicas
    @Test
    public void testEvaluate_FunctionSin() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("0"), new FunctionToken("sin"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(0.0, result, 0.0001);
    }
    
    @Test
    public void testEvaluate_FunctionCos() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("0"), new FunctionToken("cos"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(1.0, result, 0.0001);
    }
    
    @Test
    public void testEvaluate_FunctionTan() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("0"), new FunctionToken("tan"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(0.0, result, 0.0001);
    }
    
    @Test
    public void testEvaluate_FunctionLog() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("100"), new FunctionToken("log"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    public void testEvaluate_FunctionLn() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("2.71828"), new FunctionToken("ln"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(1.0, result, 0.0001);
    }

    // Testes para outras funções matemáticas
    @Test
    public void testEvaluate_FunctionSqrt() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("9"), new FunctionToken("sqrt"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(3.0, result, 0.0001);
    }

    @Test
    public void testEvaluate_FunctionAbs() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("-5"), new FunctionToken("abs"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(5.0, result, 0.0001);
    }

    @Test
    public void testEvaluate_FunctionCeil() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("4.2"), new FunctionToken("ceil"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(5.0, result, 0.0001);
    }

    @Test
    public void testEvaluate_FunctionFloor() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("4.8"), new FunctionToken("floor"));
        double result = evaluator.evaluate(postfixTokens);
        assertEquals(4.0, result, 0.0001);
    }

    // Testes para casos de erro
    @Test(expected = ExpressionException.class)
    public void testEvaluate_DivisionByZero() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("10"), new NumberToken("0"), new OperatorToken("/"));
        evaluator.evaluate(postfixTokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testEvaluate_UnknownOperator() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("5"), new NumberToken("3"), new OperatorToken("?"));
        evaluator.evaluate(postfixTokens);
    }

    @Test(expected = ExpressionException.class)
    public void testEvaluate_UnknownFunction() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("5"), new FunctionToken("abc"));
        evaluator.evaluate(postfixTokens);
    }

    @Test(expected = ExpressionException.class)
    public void testEvaluate_EmptyExpression() {
        List<Token> postfixTokens = Collections.emptyList();
        evaluator.evaluate(postfixTokens);
    }
    
    @Test(expected = ExpressionException.class)
    public void testEvaluate_InsufficientOperands() {
        List<Token> postfixTokens = Arrays.asList(new NumberToken("5"), new OperatorToken("+"));
        evaluator.evaluate(postfixTokens);
    }
}
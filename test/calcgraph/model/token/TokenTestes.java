package calcgraph.model.token;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Classe de testes para as classes de tokens concretas.
 * Cobre a correta inicialização do tipo e valor de cada token.
 */
public class TokenTestes {

    // --- Testes para as classes de token ---

    @Test
    public void testNumberToken() {
        // Verifica se a classe NumberToken é inicializada com o tipo e valor corretos.
        NumberToken token = new NumberToken("123.45");
        assertEquals(TokenType.NUMBER, token.getType());
        assertEquals("123.45", token.getValue());
    }

    @Test
    public void testOperatorToken() {
        // Verifica se a classe OperatorToken é inicializada com o tipo e valor corretos.
        OperatorToken token = new OperatorToken("+");
        assertEquals(TokenType.OPERATOR, token.getType());
        assertEquals("+", token.getValue());
    }

    @Test
    public void testFunctionToken() {
        // Verifica se a classe FunctionToken é inicializada com o tipo e valor corretos.
        FunctionToken token = new FunctionToken("sin");
        assertEquals(TokenType.FUNCTION, token.getType());
        assertEquals("sin", token.getValue());
    }

    @Test
    public void testParenthesisToken() {
        // Verifica se a classe ParenthesisToken é inicializada com o tipo e valor corretos.
        ParenthesisToken token = new ParenthesisToken("(");
        assertEquals(TokenType.PARENTHESIS, token.getType());
        assertEquals("(", token.getValue());
    }

    // --- Teste para o método toString da classe Token (herança) ---
    
    @Test
    public void testTokenToString() {
        // Verifica se o método toString da classe base retorna a representação esperada.
        NumberToken numberToken = new NumberToken("10");
        assertEquals("Token{type=NUMBER, value='10', arguments='1'}", numberToken.toString());
        
        OperatorToken operatorToken = new OperatorToken("-");
        assertEquals("Token{type=OPERATOR, value='-', arguments='1'}", operatorToken.toString());
    }
}
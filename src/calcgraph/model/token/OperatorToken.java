package calcgraph.model.token;

/**
 * Representa um token de operador.
 */
public class OperatorToken extends Token {
    public OperatorToken(String value) {
        super(TokenType.OPERATOR, value);
    }
}
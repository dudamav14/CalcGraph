package calcgraph.model.token;

/**
 * Representa um token de número.
 */
public class NumberToken extends Token {
    public NumberToken(String value) {
        super(TokenType.NUMBER, value);
    }
}
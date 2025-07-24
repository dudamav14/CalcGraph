package calcgraph.model.token;

/**
 * Representa um token de parenteses.
 */
public class ParenthesisToken extends Token {
    public ParenthesisToken(String value) {
        super(TokenType.PARENTHESIS, value);
    }
}
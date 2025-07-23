package calcgraph.model.token;

/**
 * Representa um token de função (ex: sin, cos, log).
 */
public class FunctionToken extends Token {
    public FunctionToken(String value) {
        super(TokenType.FUNCTION, value);
    }
}
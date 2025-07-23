package calcgraph.model.token;

/**
 * Classe base abstrata para todos os tokens.
 * Define o tipo de token e seu valor.
 */
public abstract class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
               "type=" + type +
               ", value='" + value + '\'' +
               '}';
    }
}
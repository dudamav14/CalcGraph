package calcgraph.model.token;

/**
 * Classe base abstrata para todos os tokens.
 * Define o tipo de token e seu valor.
 */
public abstract class Token {
    private final TokenType type;
    private String value;
    private int arguments;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.arguments=1;
    }
    
    public Token(TokenType type, String value, int arguments){
        this.type = type;
        this.value = value;
        this.arguments = arguments;
    }

    public TokenType getType() {
        return type;
    }
    
     public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public int getArguments() {
        return arguments;
    }
    
    public void setArguments(int arguments){
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "Token{" +
               "type=" + type +
               ", value='" + value + '\'' +
                ", arguments='" + arguments + '\'' +
               '}';
    }
}
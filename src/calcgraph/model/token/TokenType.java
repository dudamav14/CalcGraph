package calcgraph.model.token;

public enum TokenType {
    NUMBER,         // Números (inteiros ou decimais)
    OPERATOR,       // Operadores (+, -, *, /, ^)
    FUNCTION,       // Funções (sin, cos, log, etc.)
    PARENTHESIS,    // Parênteses (abertura e fechamento)
    CONSTANT        // Constantes (pi, e)
}
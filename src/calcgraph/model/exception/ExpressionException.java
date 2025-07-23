package calcgraph.model.exception;

/**
 * Exceção personalizada para erros relacionados à análise ou avaliação de expressões.
 */
public class ExpressionException extends RuntimeException {
    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
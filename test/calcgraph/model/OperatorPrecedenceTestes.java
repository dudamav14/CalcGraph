package calcgraph.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Classe de testes para OperatorPrecedence.
 * Cobre a lógica de precedência, associatividade e identificação de operadores.
 */
public class OperatorPrecedenceTestes {

    // --- Testes para o método getPrecedence ---

    @Test
    public void testarPrecedencia_OperadoresValidos() {
        // Verifica se a precedência para operadores conhecidos está correta.
        assertEquals(1, OperatorPrecedence.getPrecedence("+"));
        assertEquals(1, OperatorPrecedence.getPrecedence("-"));
        assertEquals(2, OperatorPrecedence.getPrecedence("*"));
        assertEquals(2, OperatorPrecedence.getPrecedence("/"));
        assertEquals(3, OperatorPrecedence.getPrecedence("^"));
    }

    @Test
    public void testarPrecedencia_OperadorInvalido() {
        // Verifica se a precedência para um operador inválido retorna o valor padrão (0).
        assertEquals(0, OperatorPrecedence.getPrecedence("x")); 
    }

    @Test
    public void testarPrecedencia_OperadorNulo() {
        // Testa se o método lida corretamente com um operador nulo.
        assertEquals(0, OperatorPrecedence.getPrecedence(null)); 
    }

    // --- Testes para o método isLeftAssociative ---

    @Test
    public void testarAssociatividade_OperadoresEsquerda() {
        // Verifica se os operadores de associatividade à esquerda retornam true.
        assertTrue(OperatorPrecedence.isLeftAssociative("+"));
        assertTrue(OperatorPrecedence.isLeftAssociative("-"));
        assertTrue(OperatorPrecedence.isLeftAssociative("*"));
        assertTrue(OperatorPrecedence.isLeftAssociative("/"));
    }

    @Test
    public void testarAssociatividade_OperadoresDireita() {
        // Verifica se o operador de associatividade à direita (^) retorna false.
        assertFalse(OperatorPrecedence.isLeftAssociative("^"));
    }

    @Test
    public void testarAssociatividade_OperadorDesconhecido() {
        // Verifica se um operador desconhecido retorna o valor padrão (true).
        assertTrue(OperatorPrecedence.isLeftAssociative("x"));
    }
    
    @Test
    public void testarAssociatividade_OperadorNulo() {
        // Testa se o método lida corretamente com um operador nulo.
        assertTrue(OperatorPrecedence.isLeftAssociative(null));
    }

    // --- Testes para o método isOperator ---

    @Test
    public void testarSeEhOperador_OperadoresValidos() {
        // Verifica se o método retorna true para operadores válidos.
        assertTrue(OperatorPrecedence.isOperator("+"));
        assertTrue(OperatorPrecedence.isOperator("-"));
        assertTrue(OperatorPrecedence.isOperator("*"));
        assertTrue(OperatorPrecedence.isOperator("/"));
        assertTrue(OperatorPrecedence.isOperator("^"));
    }

    @Test
    public void testarSeEhOperador_NaoOperadores() {
        // Verifica se o método retorna false para tokens que não são operadores.
        assertFalse(OperatorPrecedence.isOperator("5"));
        assertFalse(OperatorPrecedence.isOperator("("));
        assertFalse(OperatorPrecedence.isOperator("sin"));
        assertFalse(OperatorPrecedence.isOperator(null));
    }
}

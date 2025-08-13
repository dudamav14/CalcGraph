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
    public void testarPrecedencia_Soma() {
        // Verifica a precedência do operador de soma.
        assertEquals(1, OperatorPrecedence.getPrecedence("+"));
    }

    @Test
    public void testarPrecedencia_Subtracao() {
        // Verifica a precedência do operador de subtração.
        assertEquals(1, OperatorPrecedence.getPrecedence("-"));
    }

    @Test
    public void testarPrecedencia_Multiplicacao() {
        // Verifica a precedência do operador de multiplicação.
        assertEquals(2, OperatorPrecedence.getPrecedence("*"));
    }

    @Test
    public void testarPrecedencia_Divisao() {
        // Verifica a precedência do operador de divisão.
        assertEquals(2, OperatorPrecedence.getPrecedence("/"));
    }

    @Test
    public void testarPrecedencia_Potenciacao() {
        // Verifica a precedência do operador de potenciação.
        assertEquals(3, OperatorPrecedence.getPrecedence("^"));
    }

    @Test
    public void testarPrecedencia_OperadorInvalido() {
        // Verifica se a precedência para um operador inválido retorna 0.
        assertEquals(0, OperatorPrecedence.getPrecedence("x"));
    }

    @Test
    public void testarPrecedencia_OperadorNulo() {
        // Testa se o método lida corretamente com um operador nulo.
        assertEquals(0, OperatorPrecedence.getPrecedence(null));
    }

    // --- Testes para o método isLeftAssociative ---

    @Test
    public void testarAssociatividade_Soma() {
        // Verifica a associatividade da soma (esquerda).
        assertTrue(OperatorPrecedence.isLeftAssociative("+"));
    }

    @Test
    public void testarAssociatividade_Subtracao() {
        // Verifica a associatividade da subtração (esquerda).
        assertTrue(OperatorPrecedence.isLeftAssociative("-"));
    }

    @Test
    public void testarAssociatividade_Multiplicacao() {
        // Verifica a associatividade da multiplicação (esquerda).
        assertTrue(OperatorPrecedence.isLeftAssociative("*"));
    }

    @Test
    public void testarAssociatividade_Divisao() {
        // Verifica a associatividade da divisão (esquerda).
        assertTrue(OperatorPrecedence.isLeftAssociative("/"));
    }

    @Test
    public void testarAssociatividade_Potenciacao() {
        // Verifica a associatividade da potenciação (direita).
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
    public void testarSeEhOperador_Soma() {
        assertTrue(OperatorPrecedence.isOperator("+"));
    }
    
    @Test
    public void testarSeEhOperador_Subtracao() {
        assertTrue(OperatorPrecedence.isOperator("-"));
    }
    
    @Test
    public void testarSeEhOperador_Multiplicacao() {
        assertTrue(OperatorPrecedence.isOperator("*"));
    }
    
    @Test
    public void testarSeEhOperador_Divisao() {
        assertTrue(OperatorPrecedence.isOperator("/"));
    }
    
    @Test
    public void testarSeEhOperador_Potenciacao() {
        assertTrue(OperatorPrecedence.isOperator("^"));
    }
    
    @Test
    public void testarSeEhOperador_NaoOperador() {
        assertFalse(OperatorPrecedence.isOperator("5"));
    }
    
    @Test
    public void testarSeEhOperador_Parenteses() {
        assertFalse(OperatorPrecedence.isOperator("("));
    }
    
    @Test
    public void testarSeEhOperador_Funcao() {
        assertFalse(OperatorPrecedence.isOperator("sin"));
    }
    
    @Test
    public void testarSeEhOperador_Nulo() {
        assertFalse(OperatorPrecedence.isOperator(null));
    }
}
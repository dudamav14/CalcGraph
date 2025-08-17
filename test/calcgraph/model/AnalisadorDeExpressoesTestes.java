package calcgraph.model;

import calcgraph.model.exception.ExpressionException;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Classe de testes para AnalisadorDeExpressoes.
 * Cobre a validação preliminar e o método de avaliação completo da expressão.
 */
public class AnalisadorDeExpressoesTestes {

    // --- Testes para o método validarExpressaoPreliminar ---

    @Test
    public void testarValidarExpressaoPreliminar_ExpressaoValida() {
        // Testa se uma expressão válida e balanceada retorna true.
        String expressao = "(1 + 2) * 3";
        boolean resultado = AnalisadorDeExpressoes.validarExpressaoPreliminar(expressao);
        assertTrue(resultado);
    }

    @Test
    public void testarValidarExpressaoPreliminar_ParentesesDesbalanceados_FaltaFecha() {
        // Testa se uma expressão com parênteses desbalanceados (falta ')') retorna false.
        String expressao = "(1 + 2 * 3";
        boolean resultado = AnalisadorDeExpressoes.validarExpressaoPreliminar(expressao);
        assertFalse(resultado);
    }
    
    @Test
    public void testarValidarExpressaoPreliminar_ParentesesDesbalanceados_FaltaAbre() {
        // Testa se uma expressão com parênteses desbalanceados (falta '(') retorna false.
        String expressao = "1 + 2) * 3";
        boolean resultado = AnalisadorDeExpressoes.validarExpressaoPreliminar(expressao);
        assertFalse(resultado);
    }

    @Test(expected = ExpressionException.class)
    public void testarValidarExpressaoPreliminar_CaracteresInvalidos() {
        // Testa se uma exceção é lançada para caracteres não permitidos.
        String expressao = "1 + 2 & 3";
        AnalisadorDeExpressoes.validarExpressaoPreliminar(expressao);
    }
    
    @Test
    public void testarValidarExpressaoPreliminar_ExpressaoVazia() {
        // Testa se uma string vazia retorna false.
        String expressao = "";
        boolean resultado = AnalisadorDeExpressoes.validarExpressaoPreliminar(expressao);
        assertFalse(resultado);
    }
    
    @Test
    public void testarValidarExpressaoPreliminar_ExpressaoNula() {
        // Testa se uma string nula retorna false.
        String expressao = null;
        boolean resultado = AnalisadorDeExpressoes.validarExpressaoPreliminar(expressao);
        assertFalse(resultado);
    }

    // --- Testes para o método avaliarExpressao (integração de Parser, Shunting-Yard e Avaliador) ---
    
    @Test
    public void testarAvaliarExpressao_SomaSimples() {
        // Testa uma expressão simples e o resultado da avaliação.
        String expressao = "5 + 3";
        ResultadoAvaliacao resultado = AnalisadorDeExpressoes.avaliarExpressao(expressao);
        assertEquals(8.0, (Double) resultado.getValor(), 0.0001);
    }
    
    @Test
    public void testarAvaliarExpressao_ComPrecedencia() {
        // Testa uma expressão que exige a correta precedência de operadores.
        String expressao = "2 + 3 * 4";
        ResultadoAvaliacao resultado = AnalisadorDeExpressoes.avaliarExpressao(expressao);
        assertEquals(14.0, (Double) resultado.getValor(), 0.0001);
    }

    @Test
    public void testarAvaliarExpressao_ComParenteses() {
        // Testa uma expressão com parênteses.
        String expressao = "(2 + 3) * 4";
        ResultadoAvaliacao resultado = AnalisadorDeExpressoes.avaliarExpressao(expressao);
        assertEquals(20.0, (Double) resultado.getValor(), 0.0001);
    }
    
    @Test(expected = ExpressionException.class)
    public void testarAvaliarExpressao_ExpressaoInvalida() {
        // Testa se uma exceção é lançada para uma expressão inválida.
        String expressao = "5 + * 3";
        AnalisadorDeExpressoes.avaliarExpressao(expressao);
    }
}

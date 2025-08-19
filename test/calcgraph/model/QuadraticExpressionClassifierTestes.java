package calcgraph.model;

import calcgraph.model.exception.ExpressionException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Classe de testes para QuadraticExpressionClassifier.
 * Cobre a classificação de expressões quadráticas para extrair os coeficientes a, b e c.
 */
public class QuadraticExpressionClassifierTestes {

    private static final double DELTA = 0.0001; // Para comparação de doubles

    @Test
    public void testClassify_CompleteExpression() {
        // Testa uma expressão completa com todos os coeficientes
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("2x^2 + 3x - 4");
        assertEquals(2.0, classifier.getA(), DELTA);
        assertEquals(3.0, classifier.getB(), DELTA);
        assertEquals(-4.0, classifier.getC(), DELTA);
    }
    
    @Test
    public void testClassify_ExpressionWithMissingBTerm() {
        // Testa uma expressão sem o termo 'x'
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("3x^2 + 5");
        assertEquals(3.0, classifier.getA(), DELTA);
        assertEquals(0.0, classifier.getB(), DELTA);
        assertEquals(5.0, classifier.getC(), DELTA);
    }
    
    @Test
    public void testClassify_ExpressionWithMissingCTerm() {
        // Testa uma expressão sem o termo constante
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("x^2 + 2x");
        assertEquals(1.0, classifier.getA(), DELTA);
        assertEquals(2.0, classifier.getB(), DELTA);
        assertEquals(0.0, classifier.getC(), DELTA);
    }

    @Test
    public void testClassify_ExpressionWithNegativeCoefficients() {
        // Testa uma expressão com coeficientes negativos
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("-2x^2 - 5x + 3");
        assertEquals(-2.0, classifier.getA(), DELTA);
        assertEquals(-5.0, classifier.getB(), DELTA);
        assertEquals(3.0, classifier.getC(), DELTA);
    }
    
    @Test
    public void testClassify_ExpressionWithCoefficientsAsOne() {
        // Testa uma expressão onde o coeficiente é 1 ou -1
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("x^2 - x + 1");
        assertEquals(1.0, classifier.getA(), DELTA);
        assertEquals(-1.0, classifier.getB(), DELTA);
        assertEquals(1.0, classifier.getC(), DELTA);
    }

    @Test
    public void testClassify_OnlyXSquaredTerm() {
        // Testa uma expressão apenas com o termo x^2
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("5x^2");
        assertEquals(5.0, classifier.getA(), DELTA);
        assertEquals(0.0, classifier.getB(), DELTA);
        assertEquals(0.0, classifier.getC(), DELTA);
    }

    @Test
    public void testClassify_OnlyXTerm() {
        // Testa uma expressão apenas com o termo x
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("-3x");
        assertEquals(0.0, classifier.getA(), DELTA);
        assertEquals(-3.0, classifier.getB(), DELTA);
        assertEquals(0.0, classifier.getC(), DELTA);
    }
    
    @Test
    public void testClassify_OnlyConstantTerm() {
        // Testa uma expressão apenas com o termo constante
        QuadraticExpressionClassifier classifier = new QuadraticExpressionClassifier("10");
        assertEquals(0.0, classifier.getA(), DELTA);
        assertEquals(0.0, classifier.getB(), DELTA);
        assertEquals(10.0, classifier.getC(), DELTA);
    }

    // --- Testes para cenários de erro ---

    @Test(expected = ExpressionException.class)
    public void testClassify_InvalidCharacters() {
        // Testa se uma exceção é lançada para caracteres inválidos
        new QuadraticExpressionClassifier("2x^2 + @x - 5");
    }

    @Test(expected = ExpressionException.class)
    public void testClassify_MalformedExpression() {
        // Testa se uma exceção é lançada para uma expressão com formato incorreto
        new QuadraticExpressionClassifier("2x^2 + 3x - 5y");
    }
}

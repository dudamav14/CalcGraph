/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calcgraph.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Caio
 */
public class QuadraticExpressionClassifier {

    private double a = 0.0;
    private double b = 0.0;
    private double c = 0.0;
    
    // Construtor para classificar a expressão ao criar o objeto
    public QuadraticExpressionClassifier(String expression) {
        parseEquation(expression);
    }
    
   private void parseEquation(String equation) {
        // Remove espaços
        equation = equation.replace(" ", "");

        // Inicializa coeficientes
        a = 0;
        b = 0;
        c = 0;

        // Regex para encontrar os termos
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)x\\^2|([+-]?\\d*\\.?\\d*)x|([+-]?\\d+)");
        Matcher matcher = pattern.matcher(equation);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Coeficiente a
                a = parseCoefficient(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Coeficiente b
                b = parseCoefficient(matcher.group(2));
            } else if (matcher.group(3) != null) {
                // Coeficiente c
                c = Double.parseDouble(matcher.group(3));
            }
        }
    }

    private double parseCoefficient(String coef) {
        if (coef.equals("") || coef.equals("+")) {
            return 1;
        } else if (coef.equals("-")) {
            return -1;
        } else {
            return Double.parseDouble(coef);
        }
    }
    
    // Métodos para acessar os coeficientes
    public double getA() {
        return a;
    }
    
    public double getB() {
        return b;
    }
    
    public double getC() {
        return c;
    }
    
}

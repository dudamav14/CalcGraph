package calcgraph.model.evaluator;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.Token;
import calcgraph.model.token.TokenType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Stack;

public class PostfixEvaluator {

    /**
     * Avalia uma lista de tokens na notação pós-fixada e retorna o resultado.
     *
     * @param postfixTokens A lista de tokens na notação pós-fixada.
     * @return O resultado do cálculo.
     * @throws ExpressionException Se a expressão pós-fixada for inválida ou o cálculo resultar em erro (ex: divisão por zero).
     */
    
    private boolean isMultiArgumentFunction(String functionName) {
        return functionName.equals("mean") ||
               functionName.equals("mode") ||
               functionName.equals("median") ||
               functionName.equals("variance") ||
               functionName.equals("standarddeviation");
    }
    
    public double evaluate(List<Token> postfixTokens) {
        Stack<Double> operandStack = new Stack<>();
        int count = 0;

        for (Token token : postfixTokens) {
            if (token.getType() == TokenType.NUMBER) {
                try {
                    operandStack.push(Double.parseDouble(token.getValue()));
                    count++;
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Número inválido: " + token.getValue(), e);
                }
            } else if (token.getType() == TokenType.CONSTANT) { 
                if (token.getValue().equalsIgnoreCase("pi")) {
                    operandStack.push(Math.PI);
                    count++;
                } else if (token.getValue().equalsIgnoreCase("e")) {
                    operandStack.push(Math.E);
                    count++;
                } else {
                    throw new ExpressionException("Constante desconhecida: " + token.getValue());
                }
            } else if (token.getType() == TokenType.OPERATOR) {
                if (token.getValue().equals("!")) {
                    if (operandStack.isEmpty()) {
                        throw new ExpressionException("Sintaxe inválida: operador '!' requer um operando.");
                    }
                    double operand = operandStack.pop();
                    operandStack.push(applyOperator("!", operand, 0.0));
                    continue;
                }
                if (operandStack.size() < 2) {
                    throw new ExpressionException("Sintaxe inválida: operador '" + token.getValue() + "' requer dois operandos.");
                }
                double operand2 = operandStack.pop();
                double operand1 = operandStack.pop();
                double result = applyOperator(token.getValue(), operand1, operand2);
                operandStack.push(result);
                count--;
            } else if (token.getType() == TokenType.FUNCTION) {
                String functionName = token.getValue().toLowerCase();
                
                System.out.println("\n\nTokens: "+postfixTokens);
                System.out.println("Achou uma função: "+functionName+"");
                System.out.println("OperandStack: "+operandStack);
                
                if (isMultiArgumentFunction(functionName)) {
                    List<Double> arguments = new ArrayList<>();
                    // Enquanto a pilha não estiver vazia, desempilha e adiciona à lista
                    if(count==0){
                        while(!operandStack.isEmpty()){
                            arguments.add(operandStack.pop());
                        }
                    }
                    for (int i=0; i<count; i++) { // Parar ao achar um parenteses de fechamento, para resolver a funcao primeiro
                        arguments.add(operandStack.pop());
                    }
                    count=0;
                    // A lista de argumentos está na ordem inversa, então vamos reverter
                    Collections.reverse(arguments);
                    double result = applyStatistic(functionName, arguments);
                    operandStack.push(result);
                }else{
                    if (operandStack.isEmpty()) {
                        throw new ExpressionException("Sintaxe inválida: função '" + token.getValue() + "' requer um operando.");
                    }
                    double operand = operandStack.pop();
                    double result = applyFunction(token.getValue(), operand);
                    operandStack.push(result);
                }
                //mean(mode(1,4,4,6,3),mode(4,5,6,6,7))
                System.out.println(" ----------- REALIZANDO OPERACOES ----------- ");
                System.out.println("OperandStack: "+operandStack);
                System.out.println("Tokens: "+postfixTokens);
            } else {
                throw new ExpressionException("Tipo de token inesperado na avaliação pós-fixada: " + token.getType());
            }
        }

        if (operandStack.size() != 1) {
            throw new ExpressionException("Sintaxe inválida: expressão pós-fixada resultou em múltiplos valores ou nenhum.");
        }

        return operandStack.pop();
    }

    /**
     * Aplica a operação matemática.
     */
    private double applyOperator(String operator, double operand1, double operand2) {
        switch (operator) {
            case "+": return operand1 + operand2;
            case "-": return operand1 - operand2;
            case "*": return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ExpressionException("Divisão por zero.");
                }
                return operand1 / operand2;
            case "^": return Math.pow(operand1, operand2);
            case "!":
                if (operand1 < 0) {
                    throw new ExpressionException("O fatorial não está definido para números negativos.");
                }
                if (operand1 != Math.floor(operand1)) {
                    throw new ExpressionException("O fatorial só é definido para inteiros.");
                }
                double result = 1;
                for (int i = 2; i <= (int) operand1; i++) {
                    result *= i;
                }
                return result;
            default: throw new ExpressionException("Operador desconhecido: " + operator);
        }
    }

    /**
     * Aplica a função matemática.
     */
    private double applyFunction(String functionName, double operand) {
        switch (functionName.toLowerCase()) {
            case "sin": return Math.sin(operand);
            case "cos": return Math.cos(operand);
            case "tan": return Math.tan(operand);
            case "log": return Math.log10(operand); // Logaritmo base 10
            case "ln": return Math.log(operand);   // Logaritmo natural
            case "sqrt": return Math.sqrt(operand);
            case "abs": return Math.abs(operand);
            case "ceil": return Math.ceil(operand);
            case "floor": return Math.floor(operand);
            case "fat": 
                if (operand < 0) {
                    throw new IllegalArgumentException("O fatorial não está definido para números negativos.");
                }
                long fatorial = 1;
                for (int i = 2; i <= operand; i++) {
                    fatorial *= i;
                }
                return fatorial;
            default: throw new ExpressionException("Função desconhecida: " + functionName);
        }
    }
    private double applyStatistic (String functionName, List<Double> numbers)throws ExpressionException{
       switch(functionName.toLowerCase()){
            case "mean":
            if(numbers.isEmpty()){
                throw new ExpressionException("Não é possível calcular a média de uma lista vazia");
            }
            double sum = 0.0;
            for(double number : numbers){
                sum += number;
            }
            
            return sum / numbers.size();
            case "mode":
                if(numbers.isEmpty()){
                    throw new ExpressionException("Não é possível calcular a moda de uma lista vazia.");
                }
                Map<Double, Integer> frequencyMap = new HashMap<>();
                for (double number : numbers) {
                    frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
                }
                
                double mode = 0.0;
                int maxFrequency = 0;

                for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
                
                    if (entry.getValue() > maxFrequency) {
                    maxFrequency = entry.getValue();
                    mode = entry.getKey();
                    }
                }
                if (maxFrequency <= 1 && numbers.size() > 1) {
                    throw new ExpressionException("Não há uma única moda definida para a lista.");
                }
            return mode;
            case "median":
                if (numbers.isEmpty()) {
                    throw new ExpressionException("Não é possível calcular a mediana de uma lista vazia.");
                }
                // 1. Crie uma cópia para não modificar a lista original
                List<Double> sortedNumbers = new ArrayList<>(numbers);
                // 2. Ordene a lista
                Collections.sort(sortedNumbers);
                int size = sortedNumbers.size();
                // 3. Verifique se o tamanho é ímpar
                if (size % 2 != 0) {
                // A mediana é o elemento do meio
                    return sortedNumbers.get(size / 2);
                } 
                else {
                // 4. Verifique se o tamanho é par
                // A mediana é a média dos dois elementos do meio
                    int middleIndex1 = (size / 2) - 1;
                    int middleIndex2 = size / 2;
                    return (sortedNumbers.get(middleIndex1) + sortedNumbers.get(middleIndex2)) / 2.0;
                }
                
            case "variance":
                if (numbers.isEmpty()) {
                    throw new ExpressionException("Não é possível calcular a variância de uma lista vazia.");
                }
            
                double mean = 0.0;
                for (double number : numbers) {
                    mean += number;
                }
                mean = mean / numbers.size();
            
                double squaredDifferencesSum = 0.0;
                for (double number : numbers) {
                    double difference = number - mean;
                    squaredDifferencesSum += difference * difference;
                }
            
                return squaredDifferencesSum / numbers.size();
                
            case "standarddeviation":
                if (numbers.isEmpty()) {
                    throw new ExpressionException("Não é possível calcular o desvio padrão de uma lista vazia.");
                }

                double variance = 0.0;
                double meanStdDev = 0.0;
                for (double number : numbers) {
                meanStdDev += number;
                }
                meanStdDev = meanStdDev / numbers.size();
            
                double squaredDifferencesSumStdDev = 0.0;
                for (double number : numbers) {
                    double difference = number - meanStdDev;
                    squaredDifferencesSumStdDev += difference * difference;
                }
                variance = squaredDifferencesSumStdDev / numbers.size();

                return Math.sqrt(variance);
            default:
                throw new ExpressionException("Função estatística desconhecida: " + functionName);
       }    
   }
}
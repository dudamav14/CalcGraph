package calcgraph.model.parser;

import calcgraph.model.exception.ExpressionException;
import calcgraph.model.token.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsável por tokenizar a string da expressão matemática em uma lista de Tokens.
 * Também realiza validações sintáticas adicionais durante a tokenização.
 */
public class ExpressionParser {

    private static final String NUMBER_REGEX = "\\d+(\\.\\d+)?";
    private static final String OPERATOR_REGEX = "[+\\-*/^!]";
    private static final String PARENTHESIS_REGEX = "[()]";
    private static final String FUNCTION_CONSTANT_REGEX = "[a-zA-Z]+";
    private static final String SEPARATOR_REGEX = ",";
    private static final String VARIABLE_REGEX = "[xX]";


    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            NUMBER_REGEX + "|" +
            OPERATOR_REGEX + "|" +
            PARENTHESIS_REGEX + "|" +
            FUNCTION_CONSTANT_REGEX + "|" +
            SEPARATOR_REGEX + "|" +
            VARIABLE_REGEX 
    );

    
    public List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        String expr = expression.replaceAll("\\s+", "");
        
        Matcher matcher = TOKEN_PATTERN.matcher(expr);
        int lastMatchEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastMatchEnd) {
                String invalidPart = expr.substring(lastMatchEnd, matcher.start());
                throw new ExpressionException("Caracteres não reconhecidos na expressão: '" + invalidPart + "'");
            }

            String tokenValue = matcher.group();
            TokenType tokenType = TokenType.NUMBER; 
            if (tokenValue.matches(NUMBER_REGEX)) {
                tokenType = TokenType.NUMBER;
            } else if (tokenValue.matches(OPERATOR_REGEX)) {
               
                boolean isUnary = (tokens.isEmpty() ||
                                   (tokens.get(tokens.size() - 1).getType() == TokenType.OPERATOR && !tokens.get(tokens.size() - 1).getValue().equals(")")) || // Ex: 2 * -5
                                   (tokens.get(tokens.size() - 1).getType() == TokenType.PARENTHESIS && tokens.get(tokens.size() - 1).getValue().equals("(")) ); // Ex: (-5)

                if ((tokenValue.equals("-") || tokenValue.equals("+")) && isUnary) {
                    tokenType = TokenType.OPERATOR; 
                } else {
                    tokenType = TokenType.OPERATOR;
                }
            } else if (tokenValue.matches(PARENTHESIS_REGEX)) {
                tokenType = TokenType.PARENTHESIS;
            }else if (tokenValue.matches(VARIABLE_REGEX)) {
                tokenType = TokenType.VARIABLE;
            }else if (tokenValue.equals(",")) { 
                tokenType = TokenType.SEPARATOR;
            } else if (tokenValue.matches(FUNCTION_CONSTANT_REGEX)) {
                if (isConstant(tokenValue)) {
                    tokenType = TokenType.CONSTANT;
                    tokenValue = tokenValue.toLowerCase();
                } else {
                    tokenType = TokenType.FUNCTION;
                    tokenValue = tokenValue.toLowerCase();
                }
            }

            tokens.add(new Token(tokenType, tokenValue) {}); 

            lastMatchEnd = matcher.end();
        }

        if (lastMatchEnd < expr.length()) {
            String invalidPart = expr.substring(lastMatchEnd);
            throw new ExpressionException("Caracteres não reconhecidos no final da expressão: '" + invalidPart + "'");
        }
        
        List<Token> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            if (currentToken.getType() == TokenType.OPERATOR &&
                (currentToken.getValue().equals("-") || currentToken.getValue().equals("+"))) {

                boolean isUnary = false;
                if (i == 0) {
                    isUnary = true;
                } else {
                    Token prevToken = tokens.get(i - 1);
                    if (prevToken.getType() == TokenType.OPERATOR ||
                        (prevToken.getType() == TokenType.PARENTHESIS && prevToken.getValue().equals("(")) ||
                        prevToken.getType() == TokenType.FUNCTION) { // Ex: sin(-2)
                        isUnary = true;
                    }
                }

                if (isUnary) {

                    tokens.add(i, new ParenthesisToken("("));
                    tokens.add(i+1, new NumberToken("0"));
                    tokens.add(i+4, new ParenthesisToken(")"));
                    i=-1;
                    processedTokens.clear();
                    continue;
                }
            }
            processedTokens.add(currentToken);
        }

        return processedTokens;
    }

    public void validateTokenSequence(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new ExpressionException("Expressão vazia.");
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            Token previous = (i > 0) ? tokens.get(i - 1) : null;
            Token next = (i + 1 < tokens.size()) ? tokens.get(i + 1) : null;

            if (current.getType() == TokenType.OPERATOR) {
                String op = current.getValue();
                
                if (op.equals("!")) {
                    if (previous == null ||
                        !(previous.getType() == TokenType.NUMBER ||
                          previous.getType() == TokenType.CONSTANT ||
                          (previous.getType() == TokenType.PARENTHESIS && previous.getValue().equals(")")))) {
                        throw new ExpressionException("O operador '!' deve vir após um número, constante ou parênteses fechando.");
                    }
                    // Permite '!' no final ou antes de outro operador
                    continue;
                }
                
                if (previous == null && !op.equals("+") && !op.equals("-")) {
                    throw new ExpressionException("Operador '" + op + "' no início da expressão.");
                }
                
                if (next == null && !op.equals("!")) {
                    throw new ExpressionException("Operador '" + op + "' no final da expressão.");
                }
                
                if (next != null && next.getType() == TokenType.OPERATOR) {
                    String nextOp = next.getValue();
                    if (!((op.equals("+") || op.equals("-")) && (nextOp.equals("+") || nextOp.equals("-"))) && !(op.equals("^") && nextOp.equals("^"))) {
                        throw new ExpressionException("Dois operadores consecutivos: '" + op + nextOp + "'.");
                    }
                }
                if (next != null && next.getType() == TokenType.PARENTHESIS && next.getValue().equals(")")) {
                    throw new ExpressionException("Operador '" + op + "' seguido por fecha parênteses.");
                }
                
                if (previous != null && previous.getType() == TokenType.PARENTHESIS && previous.getValue().equals("(") && !op.equals("+") && !op.equals("-")) {
                    throw new ExpressionException("Operador '" + op + "' não permitido após abre parênteses.");
                }

               
                if (op.equals("/") && next != null && next.getType() == TokenType.NUMBER && Double.parseDouble(next.getValue()) == 0) {
                    throw new ExpressionException("Divisão por zero não permitida na sintaxe explícita (e.g., 5/0).");
                }
            }

           
            if (current.getType() == TokenType.PARENTHESIS) {
                if (current.getValue().equals("(")) {
                    
                    if (next != null && next.getType() == TokenType.PARENTHESIS && next.getValue().equals(")")) {
                        throw new ExpressionException("Parênteses vazios.");
                    }
                    
                    if (previous != null && (previous.getType() == TokenType.NUMBER || (previous.getType() == TokenType.PARENTHESIS && previous.getValue().equals(")")) || previous.getType() == TokenType.CONSTANT)) {
                        
                        throw new ExpressionException("Número, Constante ou FechaParênteses antes de abre parênteses sem operador.");
                    }
                } else { 
                    if (previous == null) {
                        throw new ExpressionException("Fecha parênteses no início da expressão.");
                    }
                   
                    if (next != null && (next.getType() == TokenType.NUMBER || next.getType() == TokenType.FUNCTION || next.getType() == TokenType.CONSTANT || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals("(")))) {
                        throw new ExpressionException("Número/Função/Constante/AbreParênteses após fecha parênteses sem operador.");
                    }
                }
            }
            if (current.getType() == TokenType.NUMBER) {
                if (next != null && (next.getType() == TokenType.FUNCTION || next.getType() == TokenType.CONSTANT || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals("(")))) {
                    throw new ExpressionException("Número antes de função, constante ou abre parênteses sem operador.");
                }
            }
            
            if (current.getType() == TokenType.FUNCTION) {
               
                if (next == null || (next.getType() != TokenType.PARENTHESIS || !next.getValue().equals("("))) {
                    throw new ExpressionException("Função '" + current.getValue() + "' deve ser seguida por abre parênteses.");
                }
            }
           
            if (current.getType() == TokenType.CONSTANT) {
                
                if (next != null && (next.getType() == TokenType.NUMBER || next.getType() == TokenType.FUNCTION || next.getType() == TokenType.CONSTANT || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals("(")))) {
                    throw new ExpressionException("Constante '" + current.getValue() + "' seguida por número/função/constante/abre parênteses sem operador.");
                }
            }
            
            if (current.getType() == TokenType.SEPARATOR) {
                // A vírgula deve ser sempre precedida por um operando (número, constante, etc.)
                if (previous == null || previous.getType() == TokenType.OPERATOR || previous.getType() == TokenType.FUNCTION || (previous.getType() == TokenType.PARENTHESIS && previous.getValue().equals("("))) {
                    throw new ExpressionException("Vírgula ',' em posição inválida.");
                }
                // A vírgula deve ser sempre seguida por um operando ou um parêntese de fechamento
                if (next == null || next.getType() == TokenType.OPERATOR || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals(")"))) {
                    throw new ExpressionException("Vírgula ',' em posição inválida.");
                }
            }
        }
        if (!tokens.isEmpty()) {
            Token lastToken = tokens.get(tokens.size() - 1);
            if (lastToken.getType() == TokenType.OPERATOR && !lastToken.getValue().equals(")") && !lastToken.getValue().equals("!")) {
              
                throw new ExpressionException("Expressão termina com operador inválido.");
            }
        }
    }

    private boolean isConstant(String value) {
        return value.equalsIgnoreCase("pi") || value.equalsIgnoreCase("e");
    }
}
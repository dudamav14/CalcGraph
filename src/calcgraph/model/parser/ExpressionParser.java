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
    private static final String OPERATOR_REGEX = "[+\\-*/^]";
    private static final String PARENTHESIS_REGEX = "[()]";
    private static final String FUNCTION_CONSTANT_REGEX = "[a-zA-Z]+";

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            NUMBER_REGEX + "|" +
            OPERATOR_REGEX + "|" +
            PARENTHESIS_REGEX + "|" +
            FUNCTION_CONSTANT_REGEX
    );

    /**
     * Tokeniza a expressão infixa, dividindo-a em uma lista de objetos Token.
     * Trata operadores unários ('-' ou '+' no início da expressão ou após um '(' ou outro operador).
     *
     * @param expression A expressão infixa como string.
     * @return Uma lista de Tokens.
     * @throws ExpressionException Se a expressão contiver erros de sintaxe ou caracteres inválidos.
     */
    public List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        String expr = expression.replaceAll("\\s+", ""); // Remover espaços
        
        Matcher matcher = TOKEN_PATTERN.matcher(expr);
        int lastMatchEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastMatchEnd) {
                String invalidPart = expr.substring(lastMatchEnd, matcher.start());
                throw new ExpressionException("Caracteres não reconhecidos na expressão: '" + invalidPart + "'");
            }

            String tokenValue = matcher.group();
            TokenType tokenType = TokenType.NUMBER; // Tipo padrão, será sobrescrito

            if (tokenValue.matches(NUMBER_REGEX)) {
                tokenType = TokenType.NUMBER;
            } else if (tokenValue.matches(OPERATOR_REGEX)) {
                // *** LÓGICA DE IDENTIFICAÇÃO DE OPERADOR UNÁRIO AQUI ***
                // Um operador é unário se:
                // 1. É o primeiro token OU
                // 2. O token anterior é um operador OU
                // 3. O token anterior é um parêntese de abertura '('
                boolean isUnary = (tokens.isEmpty() ||
                                   (tokens.get(tokens.size() - 1).getType() == TokenType.OPERATOR && !tokens.get(tokens.size() - 1).getValue().equals(")")) || // Ex: 2 * -5
                                   (tokens.get(tokens.size() - 1).getType() == TokenType.PARENTHESIS && tokens.get(tokens.size() - 1).getValue().equals("(")) ); // Ex: (-5)

                if ((tokenValue.equals("-") || tokenValue.equals("+")) && isUnary) {
                    // Se for um sinal unário e o próximo token é um número ou função/parênteses,
                    // devemos tratar isso como parte do próximo número ou como um operador unário
                    // antes de uma subexpressão.
                    // Para simplificar, podemos criar um token OPERATOR com um valor que indica ser unário,
                    // ou tentar "consumir" o próximo número se houver.
                    // A abordagem mais robusta para Shunting-Yard é ter um token OPERATOR_UNARY.
                    // Por enquanto, vamos manter como OPERATOR e ajustar o avaliador.
                    // Para o Shunting-Yard padrão, um "-" unário é um operador que age sobre o próximo termo.
                    // Será tratado pelo avaliador.
                    tokenType = TokenType.OPERATOR; // Ainda operador, mas o contexto de unário será no avaliador/parser.
                } else {
                    tokenType = TokenType.OPERATOR;
                }
            } else if (tokenValue.matches(PARENTHESIS_REGEX)) {
                tokenType = TokenType.PARENTHESIS;
            } else if (tokenValue.matches(FUNCTION_CONSTANT_REGEX)) {
                if (isConstant(tokenValue)) {
                    tokenType = TokenType.CONSTANT;
                    tokenValue = tokenValue.toLowerCase(); // Normaliza para minúsculas
                } else {
                    tokenType = TokenType.FUNCTION;
                    tokenValue = tokenValue.toLowerCase(); // Normaliza para minúsculas
                }
            }

            // Adicionar o token à lista
            tokens.add(new Token(tokenType, tokenValue) {}); // Usa a classe Token genérica para flexibilidade

            lastMatchEnd = matcher.end();
        }

        if (lastMatchEnd < expr.length()) {
            String invalidPart = expr.substring(lastMatchEnd);
            throw new ExpressionException("Caracteres não reconhecidos no final da expressão: '" + invalidPart + "'");
        }

        // *** REFINAMENTO IMPORTANTE PÓS-TOKENIZAÇÃO: Unary Minus/Plus Association ***
        // Este é o melhor lugar para identificar o operador unário.
        // Se um '-' ou '+' é precedido por um operador, um '(' ou está no início da expressão,
        // ele é um unário. Podemos transformar isso em um operador unário específico
        // para o Shunting-Yard, ou modificar o número.
        // A abordagem mais simples para Shunting-Yard é:
        // - Inserir um '0' antes de um '-' unário, transformando-o em subtração de 0.
        // Ex: -5 -> (0-5)
        // Ex: 1 + -5 -> 1 + (0-5)
        // Isso simplifica o avaliador.

        List<Token> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            if (currentToken.getType() == TokenType.OPERATOR &&
                (currentToken.getValue().equals("-") || currentToken.getValue().equals("+"))) {

                boolean isUnary = false;
                if (i == 0) { // Início da expressão
                    isUnary = true;
                } else {
                    Token prevToken = tokens.get(i - 1);
                    // Se o anterior é operador, abre parêntese ou função (que atua como um 'início' de sub-expressão)
                    if (prevToken.getType() == TokenType.OPERATOR ||
                        (prevToken.getType() == TokenType.PARENTHESIS && prevToken.getValue().equals("(")) ||
                        prevToken.getType() == TokenType.FUNCTION) { // Ex: sin(-2)
                        isUnary = true;
                    }
                }

                if (isUnary) {
                    // Insere um '0' implícito para o operador unário
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

    /**
     * Realiza uma validação sintática mais granular na lista de tokens.
     * Esta validação complementa a validação inicial de caracteres e balanceamento de parênteses.
     *
     * @param tokens A lista de tokens a ser validada.
     * @throws ExpressionException Se a sintaxe dos tokens for inválida.
     */
    public void validateTokenSequence(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new ExpressionException("Expressão vazia.");
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            Token previous = (i > 0) ? tokens.get(i - 1) : null;
            Token next = (i + 1 < tokens.size()) ? tokens.get(i + 1) : null;

            // 1. Validar operadores
            if (current.getType() == TokenType.OPERATOR) {
                String op = current.getValue();

                // Operador binário no início (exceto + ou - unários)
                if (previous == null && !op.equals("+") && !op.equals("-")) {
                    throw new ExpressionException("Operador '" + op + "' no início da expressão.");
                }
                // Operador binário no final
                if (next == null) {
                    throw new ExpressionException("Operador '" + op + "' no final da expressão.");
                }
                // Dois operadores binários seguidos (ex: "++", "+*", "*/")
                if (next != null && next.getType() == TokenType.OPERATOR) {
                    String nextOp = next.getValue();
                    if (!((op.equals("+") || op.equals("-")) && (nextOp.equals("+") || nextOp.equals("-"))) && !(op.equals("^") && nextOp.equals("^"))) {
                        throw new ExpressionException("Dois operadores consecutivos: '" + op + nextOp + "'.");
                    }
                }
                // Operador seguido imediatamente por ')'
                if (next != null && next.getType() == TokenType.PARENTHESIS && next.getValue().equals(")")) {
                    throw new ExpressionException("Operador '" + op + "' seguido por fecha parênteses.");
                }
                // Abre parênteses seguido por operador binário (ex: "(*" )
                if (previous != null && previous.getType() == TokenType.PARENTHESIS && previous.getValue().equals("(") && !op.equals("+") && !op.equals("-")) {
                    throw new ExpressionException("Operador '" + op + "' não permitido após abre parênteses.");
                }

                // Divisão por zero (apenas o padrão 5/0, não a lógica de cálculo)
                if (op.equals("/") && next != null && next.getType() == TokenType.NUMBER && Double.parseDouble(next.getValue()) == 0) {
                    throw new ExpressionException("Divisão por zero não permitida na sintaxe explícita (e.g., 5/0).");
                }
            }

            // 2. Validar parênteses
            if (current.getType() == TokenType.PARENTHESIS) {
                if (current.getValue().equals("(")) {
                    // Parênteses vazios "()"
                    if (next != null && next.getType() == TokenType.PARENTHESIS && next.getValue().equals(")")) {
                        throw new ExpressionException("Parênteses vazios.");
                    }
                    // Número ou FechaParênteses antes de abre parênteses sem operador.
                    if (previous != null && (previous.getType() == TokenType.NUMBER || (previous.getType() == TokenType.PARENTHESIS && previous.getValue().equals(")")) || previous.getType() == TokenType.CONSTANT)) {
                        // Se o anterior for uma constante (pi, e), também precisa de um operador antes de '('
                        throw new ExpressionException("Número, Constante ou FechaParênteses antes de abre parênteses sem operador.");
                    }
                } else { // current.getValue().equals(")")
                    // Fecha parênteses no início
                    if (previous == null) {
                        throw new ExpressionException("Fecha parênteses no início da expressão.");
                    }
                    // Número, função, constante ou abre parênteses após ')' sem operador
                    if (next != null && (next.getType() == TokenType.NUMBER || next.getType() == TokenType.FUNCTION || next.getType() == TokenType.CONSTANT || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals("(")))) {
                        throw new ExpressionException("Número/Função/Constante/AbreParênteses após fecha parênteses sem operador.");
                    }
                }
            }
            // 3. Validar números e funções
            if (current.getType() == TokenType.NUMBER) {
                // Número antes de função, constante ou abre parênteses sem operador (ex: 2sin, 2pi, 2(3))
                if (next != null && (next.getType() == TokenType.FUNCTION || next.getType() == TokenType.CONSTANT || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals("(")))) {
                    throw new ExpressionException("Número antes de função, constante ou abre parênteses sem operador.");
                }
            }
            if (current.getType() == TokenType.FUNCTION) {
                // Função (sin, cos, etc.) deve ser seguida por abre parênteses
                if (next == null || (next.getType() != TokenType.PARENTHESIS || !next.getValue().equals("("))) {
                    throw new ExpressionException("Função '" + current.getValue() + "' deve ser seguida por abre parênteses.");
                }
            }
            // *** NOVO: Validação para CONSTANT ***
            if (current.getType() == TokenType.CONSTANT) {
                // Constante (pi, e) não pode ser seguida por número, função ou abre parênteses sem operador
                if (next != null && (next.getType() == TokenType.NUMBER || next.getType() == TokenType.FUNCTION || next.getType() == TokenType.CONSTANT || (next.getType() == TokenType.PARENTHESIS && next.getValue().equals("(")))) {
                    throw new ExpressionException("Constante '" + current.getValue() + "' seguida por número/função/constante/abre parênteses sem operador.");
                }
            }
        }
        if (!tokens.isEmpty()) {
            Token lastToken = tokens.get(tokens.size() - 1);
            if (lastToken.getType() == TokenType.OPERATOR && !lastToken.getValue().equals(")")) {
                // A exceção para o último token é se for um operador binário.
                // Não se aplica se for um número, constante, fecha parênteses ou função
                throw new ExpressionException("Expressão termina com operador inválido.");
            }
        }
    }

    // Helper para identificar se uma string é uma constante conhecida
    private boolean isConstant(String value) {
        return value.equalsIgnoreCase("pi") || value.equalsIgnoreCase("e");
    }
}
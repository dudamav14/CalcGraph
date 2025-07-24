package calcgraph.app;

import calcgraph.model.AnalisadorDeExpressoes;
import calcgraph.model.exception.ExpressionException;

import java.util.Scanner;

public class ConsoleTestRunner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- CalcGraph - Teste de Avaliação de Expressões (Console) ---");
        System.out.println("Digite uma expressão matemática (ou 'sair' para encerrar):");

        while (true) {
            System.out.print("> "); 
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando o programa.");
                break;
            }

            try {
                double resultado = AnalisadorDeExpressoes.avaliarExpressao(input);
                System.out.printf("Resultado: %.6f%n", resultado); 
            } catch (ExpressionException e) {
                System.err.println("Erro na expressão: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }
}
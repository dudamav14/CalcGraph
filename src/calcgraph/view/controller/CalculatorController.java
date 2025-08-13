package calcgraph.view.controller;

import calcgraph.model.AnalisadorDeExpressoes;
import calcgraph.model.exception.ExpressionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javafx.scene.control.MenuItem;

public class CalculatorController {

    @FXML
    private TextField display; // Campo de expressão/resultado para o modo Cálculo

    @FXML
    private Label errorMessageLabel; // Label para exibir mensagens de erro na UI

    @FXML
    private StackPane contentArea; // O StackPane que contém todos os painéis de modo

    @FXML
    private VBox calculationModePane; // Painel do modo Cálculo
    @FXML
    private VBox graphModePane;       // Painel do modo Gráfico
    @FXML
    private VBox numericModePane;     // Painel do modo Numérico

    @FXML
    private Button btnModeCalculation; // Botão de navegação para Cálculo
    @FXML
    private Button btnModeGraph;       // Botão de navegação para Gráfico
    @FXML
    private Button btnModeNumeric;     // Botão de navegação para Numérico
    
    // Novos elementos do MODO GRÁFICO (para o Controller)
    @FXML
    private VBox functionInputArea; // O VBox que contém os HBoxes de input
    @FXML
    private TextField functionInput1; // O primeiro campo de input de função
    @FXML
    private Button btnAddFunction;     // O botão "+" para adicionar mais campos
    @FXML
    private StackPane graphArea1;     // Área do gráfico principal
    @FXML
    private StackPane graphArea2;     // Área do gráfico de comparação
    @FXML
    private VBox selectGraphPrompt;    // O VBox com o botão "Selecionar gráfico"
    @FXML
    private Label legendLabel1;       // Legenda do gráfico 1
    @FXML
    private Label legendLabel2;       // Legenda do gráfico 2
    

    private final DecimalFormat decimalFormat;

    public CalculatorController() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat = new DecimalFormat("0.##########", symbols);
    }

    @FXML
    public void initialize() {
        display.requestFocus();
        showModePane(calculationModePane);
        updateModeButtonStyles(btnModeCalculation);
    }

   
    @FXML
    private void handleModeChange(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        errorMessageLabel.setText(""); 

        if (clickedButton == btnModeCalculation) {
            showModePane(calculationModePane);
            display.requestFocus();
        } else if (clickedButton == btnModeGraph) {
            showModePane(graphModePane);
        } else if (clickedButton == btnModeNumeric) {
            showModePane(numericModePane);
        }
        updateModeButtonStyles(clickedButton);
    }

    private void showModePane(Pane paneToShow) {
        contentArea.getChildren().forEach(pane -> pane.setVisible(false));
        paneToShow.setVisible(true);
    }

    
    private void updateModeButtonStyles(Button activeButton) {
        btnModeCalculation.setStyle("-fx-background-color: #e6f7ff;");
        btnModeGraph.setStyle("-fx-background-color: #e6f7ff;");
        btnModeNumeric.setStyle("-fx-background-color: #e6f7ff;");

        activeButton.setStyle("-fx-background-color: #cceeff; -fx-font-weight: bold;");
    }

    @FXML
    private void handleNumber(ActionEvent event) {
        String number = ((Button) event.getSource()).getText();
        display.appendText(number);
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleOperator(ActionEvent event) {
        String operator = ((Button) event.getSource()).getText();
        display.appendText(operator);
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleDecimal(ActionEvent event) {
        display.appendText(".");
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleFunction(ActionEvent event) {
        String functionText = "";

        // Verifica a origem do evento (Button ou MenuItem) e extrai o texto
        if (event.getSource() instanceof Button) {
            functionText = ((Button) event.getSource()).getText();
        } else if (event.getSource() instanceof MenuItem) {
            functionText = ((MenuItem) event.getSource()).getText();
        }

        if (!functionText.isEmpty()) {
            display.appendText(functionText + "(");
            errorMessageLabel.setText("");
        }
    }

    @FXML
    private void handleConstant(ActionEvent event) {
        String constant = ((Button) event.getSource()).getText();
        display.appendText(constant);
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleParenthesis(ActionEvent event) {
        String parenthesis = ((Button) event.getSource()).getText();
        display.appendText(parenthesis);
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleClear(ActionEvent event) {
        display.clear();
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        String currentText = display.getText();
        if (!currentText.isEmpty()) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        }
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleEquals(ActionEvent event) {
        String expression = display.getText();
        try {
            double result = AnalisadorDeExpressoes.avaliarExpressao(expression);
            display.setText(decimalFormat.format(result));
            errorMessageLabel.setText("");
        } catch (ExpressionException e) {
            errorMessageLabel.setText("Erro: " + e.getMessage());
            System.err.println("Erro na expressão (UI): " + e.getMessage());
        } catch (Exception e) {
            errorMessageLabel.setText("Erro Inesperado. Verifique o console.");
            System.err.println("Ocorreu um erro inesperado (UI): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleToggleSign(ActionEvent event) {
        System.out.println("Botão +/- clicado. Implementação pendente.");
        display.appendText("-");
        errorMessageLabel.setText("Função '+/-' pode ser complexa. Verifique a expressão.");
    }
    
    // GRAFICO
    
    @FXML
    private void handleAddFunction(ActionEvent event) {
        System.out.println("Botão '+' para adicionar função clicado. Funcionalidade pendente.");
        // Lógica para adicionar um novo HBox com TextField e Button "+" dinamicamente aqui.
    }

    /**
     * Lida com o clique no botão "Selecionar gráfico" para a área de comparação no modo gráfico.
     * @param event O evento de ação do botão.
     */
    @FXML
    private void handleSelectGraph(ActionEvent event) {
        System.out.println("Botão 'Selecionar gráfico' clicado. Funcionalidade pendente.");
        // A lógica de abrir uma janela de seleção de funções salvas será implementada em etapas futuras.
    }
    
    @FXML
    private void handleGraphNumber(ActionEvent event) {
        System.out.println("handleGraphNumber chamado.");
        // Lógica de adicionar o número ao campo de input ATIVO
    }
    @FXML
    private void handleGraphOperator(ActionEvent event) {
        System.out.println("handleGraphOperator chamado.");
        // Lógica de adicionar o operador ao campo de input ATIVO
    }
    @FXML
    private void handleGraphParenthesis(ActionEvent event) {
        System.out.println("handleGraphParenthesis chamado.");
        // Lógica de adicionar parênteses ao campo de input ATIVO
    }
    @FXML
    private void handleGraphDelete(ActionEvent event) {
        System.out.println("handleGraphDelete chamado.");
        // Lógica de deletar um caractere do campo de input ATIVO
    }
    @FXML
    private void handleGraphClear(ActionEvent event) {
        System.out.println("handleGraphClear chamado.");
        // Lógica de limpar o campo de input ATIVO
    }
    @FXML
    private void handleGraphEquals(ActionEvent event) {
        System.out.println("handleGraphEquals chamado.");
        // Lógica de calcular e plotar o gráfico a partir do campo de input ATIVO
    }
    @FXML
    private void handleGraphDecimal(ActionEvent event) {
        System.out.println("handleGraphDecimal chamado.");
        // Lógica para adicionar o ponto decimal ao campo de input ATIVO
    }
    @FXML
    private void handleGraphFunction(ActionEvent event) {
        System.out.println("handleGraphFunction chamado.");
        // Lógica para adicionar uma função ao campo de input ATIVO
    }
    @FXML
    private void handleGraphConstant(ActionEvent event) {
        System.out.println("handleGraphConstant chamado.");
        // Lógica para adicionar uma constante ao campo de input ATIVO
    }
   
}
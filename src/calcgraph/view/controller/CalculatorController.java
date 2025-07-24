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
        String function = ((Button) event.getSource()).getText();
        display.appendText(function + "(");
        errorMessageLabel.setText("");
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
}
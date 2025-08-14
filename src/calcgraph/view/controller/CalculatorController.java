package calcgraph.view.controller;

import calcgraph.service.CalcService;
import calcgraph.model.Expressao;

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
import java.util.List;
import java.util.Locale;
import javafx.scene.control.MenuItem;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.TableRow;


public class CalculatorController {
    
    private CalcService service;
    private Long lastExpressaoId;
    private Stage historyStage;
    private TableView<Expressao> historyTable;


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

        service = new CalcService();
        lastExpressaoId = null;
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
            String resultText = decimalFormat.format(result);

            display.setText(resultText);
            errorMessageLabel.setText("");

            // salvar no histórico
            try {
                calcgraph.model.Expressao e = service.registrarExpressao(expression, resultText, 0);
                lastExpressaoId = e.getId();
            } catch (Exception dbEx) {
                System.err.println("Falha ao salvar histórico: " + dbEx.getMessage());
            }

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
    
    @FXML
    private void handleOpenHistory(ActionEvent e) {
        try {
            if (historyStage == null) {
                initHistoryStage();   // cria a janela só uma vez
            }
            loadHistoryIntoTable();   // recarrega dados sempre que abrir
            historyStage.show();
            historyStage.toFront();
        } catch (Exception ex) {
            errorMessageLabel.setText("Falha ao abrir histórico. Veja o console.");
            ex.printStackTrace();
        }
    }
    
    private void initHistoryStage() {
    historyTable = new TableView<>();

    TableColumn<Expressao, String> colData = new TableColumn<>("Data");
    colData.setPrefWidth(160);
    colData.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDataCriacao() != null
                    ? cd.getValue().getDataCriacao().toString().replace('T',' ')
                    : "-"
    ));

    TableColumn<Expressao, String> colExpr = new TableColumn<>("Expressão");
    colExpr.setPrefWidth(300);
    colExpr.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEntrada()));

    TableColumn<Expressao, String> colRes = new TableColumn<>("Resultado");
    colRes.setPrefWidth(180);
    colRes.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getResultado()));

    historyTable.getColumns().addAll(colData, colExpr, colRes);

    // duplo clique: jogar expressão no display e fechar
    historyTable.setRowFactory(tv -> {
        TableRow<Expressao> row = new TableRow<>();
        row.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !row.isEmpty()) {
                Expressao sel = row.getItem();
                display.setText(sel.getEntrada());
                historyStage.close();
            }
        });
        return row;
    });

    // Botões de ação
    Button btnReload = new Button("Recarregar");
    btnReload.setOnAction(ae -> loadHistoryIntoTable());

    Button btnInsert = new Button("Inserir no display");
    btnInsert.setOnAction(ae -> {
        Expressao sel = historyTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            display.setText(sel.getEntrada());
            errorMessageLabel.setText("");
            historyStage.close();
        } else {
            errorMessageLabel.setText("Selecione um item do histórico.");
        }
    });

    Button btnDelete = new Button("Excluir selecionado");
    btnDelete.setOnAction(ae -> {
        Expressao sel = historyTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                service.removerExpressao(sel.getId());
                loadHistoryIntoTable();
                errorMessageLabel.setText("Item removido do histórico.");
            } catch (Exception ex) {
                errorMessageLabel.setText("Falha ao remover (veja o console).");
                ex.printStackTrace();
            }
        } else {
            errorMessageLabel.setText("Selecione um item do histórico.");
        }
    });

    Button btnClose = new Button("Fechar");
    btnClose.setOnAction(ae -> historyStage.close());

    HBox actions = new HBox(8, btnReload, btnInsert, btnDelete, btnClose);
    actions.setPadding(new Insets(8));

    BorderPane root = new BorderPane();
    root.setCenter(historyTable);
    root.setBottom(actions);

    historyStage = new Stage();
    historyStage.setTitle("Histórico de Expressões");
    historyStage.setScene(new Scene(root, 720, 420));
    }
    
    private void loadHistoryIntoTable() {
    try {
        List<Expressao> list = service.historico(500); // ajuste o limite se quiser
        historyTable.getItems().setAll(list);
    } catch (Exception ex) {
        System.err.println("Falha ao carregar histórico: " + ex.getMessage());
    }
}


    

}
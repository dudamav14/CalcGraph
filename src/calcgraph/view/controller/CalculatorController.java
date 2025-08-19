package calcgraph.view.controller;

import calcgraph.service.CalcService;
import calcgraph.model.Expressao;
import calcgraph.model.ResultadoAvaliacao;

import calcgraph.model.AnalisadorDeExpressoes;
import calcgraph.model.Favorito;
import calcgraph.model.GraphPlotter;
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
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;


public class CalculatorController {
    
    private CalcService service;
    private Long lastExpressaoId;
    private Stage historyStage;
    private TableView<Expressao> historyTable;
    private Stage favoritesStage;
    private TableView<Favorito> favoritesTable;


    @FXML
    private TextField display; // Campo de expressão/resultado para o modo Cálculo

    @FXML
    private Label errorMessageLabel; // Label para exibir mensagens de erro na UI
    
    @FXML
    private Label errorMessageLabelGrafico; // Label para exibir mensagens de erro na UI


    
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
        graphPlotter = new GraphPlotter(mainGraphPane, mainGraphLegend);
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
    private void handleSeparator(ActionEvent event) {
        display.appendText(",");
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
            
            ResultadoAvaliacao result = AnalisadorDeExpressoes.avaliarExpressao(expression);
            if (result.getTipo() == ResultadoAvaliacao.TipoResultado.NUMERICO) {
                System.out.println(result);
                String resultText = decimalFormat.format(result.getValor());

                display.setText(resultText);
                errorMessageLabel.setText("");
            }else if (result.getTipo() == ResultadoAvaliacao.TipoResultado.GRAFICO) {
                errorMessageLabel.setText("Variáveis só podem ser calculadas no modo gráfico.");
            }

            // salvar no histórico
            try {
                calcgraph.model.Expressao e = service.registrarExpressao(expression, decimalFormat.format(result.getValor()), 0);
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
    private void handleOpenFavorites(ActionEvent e) {
        try {
            if (favoritesStage == null) {
                initFavoritesStage(); // cria a janela só uma vez
            }
            loadFavoritesIntoTable(); // recarrega dados sempre que abrir
            favoritesStage.show();
            favoritesStage.toFront();
        } catch (Exception ex) {
            errorMessageLabel.setText("Falha ao abrir favoritos. Veja o console.");
            ex.printStackTrace();
        }
    }
    
    private void initFavoritesStage() {
        favoritesTable = new TableView<>();

        // Colunas
        TableColumn<Favorito, String> colData = new TableColumn<>("Data");
        colData.setPrefWidth(160);
        colData.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getExpressao().getDataCriacao() != null
                        ? cd.getValue().getExpressao().getDataCriacao().toString().replace('T', ' ')
                        : "-"
        ));

        TableColumn<Favorito, String> colExpr = new TableColumn<>("Expressão");
        colExpr.setPrefWidth(200);
        colExpr.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getExpressao().getEntrada()));

        TableColumn<Favorito, String> colRes = new TableColumn<>("Resultado");
        colRes.setPrefWidth(120);
        colRes.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getExpressao().getResultado()));
        
        TableColumn<Favorito, String> colApelido = new TableColumn<>("Apelido");
        colApelido.setPrefWidth(150);
        colApelido.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getApelido()));
        
        TableColumn<Favorito, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setPrefWidth(250);
        colDesc.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescricao()));

        favoritesTable.getColumns().addAll(colData, colExpr, colRes, colApelido, colDesc);
        
        // Duplo clique: jogar expressão no display e fechar
        favoritesTable.setRowFactory(tv -> {
            TableRow<Favorito> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    Favorito sel = row.getItem();
                    display.setText(sel.getExpressao().getEntrada());
                    favoritesStage.close();
                }
            });
            return row;
        });

        // Botões de ação
        Button btnReload = new Button("Recarregar");
        btnReload.setOnAction(ae -> loadFavoritesIntoTable());

        Button btnInsert = new Button("Inserir no display");
        btnInsert.setOnAction(ae -> {
            Favorito sel = favoritesTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
               adicionarCampo(sel.getExpressao().getEntrada());
                errorMessageLabel.setText("");
                favoritesStage.close();
            } else {
                errorMessageLabel.setText("Selecione um item do histórico.");
            }
        });

        Button btnDelete = new Button("Excluir selecionado");
        btnDelete.setOnAction(ae -> {
            Favorito sel = favoritesTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    service.removerFavorito(sel.getId());
                    loadFavoritesIntoTable();
                    errorMessageLabel.setText("Item " + sel.getApelido() + " removido.");
                } catch (Exception ex) {
                    errorMessageLabel.setText("Falha ao remover (veja o console).");
                    ex.printStackTrace();
                }
            } else {
                errorMessageLabel.setText("Selecione um item para remover.");
            }
        });

        Button btnClose = new Button("Fechar");
        btnClose.setOnAction(ae -> favoritesStage.close());

        HBox actions = new HBox(8, btnReload, btnInsert, btnDelete, btnClose);
        actions.setPadding(new Insets(8));

        BorderPane root = new BorderPane();
        root.setCenter(favoritesTable);
        root.setBottom(actions);

        favoritesStage = new Stage();
        favoritesStage.setTitle("Meus Favoritos");
        favoritesStage.setScene(new Scene(root, 900, 420));
    }
    
    private void loadFavoritesIntoTable() {
        try {
            List<Favorito> list = service.favoritos();
            favoritesTable.getItems().setAll(list);
        } catch (Exception ex) {
            System.err.println("Falha ao carregar favoritos: " + ex.getMessage());
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
                errorMessageLabel.setText("Item "+ sel.getEntrada()+" removido do histórico.");
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
    
    // GRÁFICO
    
    @FXML
    private VBox functionInputsBox;
    @FXML
    private Pane mainGraphPane, comparisonGraphPane;
    @FXML
    private ListView<String> mainGraphLegend, comparisonGraphLegend;
    @FXML
    private Button btnSelectComparisonGraph;
    @FXML
    private GridPane smallCalculatorPane;
    @FXML
    private TextField selectedFunctionField;
 
    @FXML
    private void handleAddFunctionField() {
        adicionarCampo("");
    }
    
    private void adicionarCampo(String expressao){
        HBox fieldBox = new HBox(5);
        
        Button favButton = new Button();
        favButton.setOnAction(e -> handleToggleFavorite(e));
        if(expressao.isEmpty())
            favButton.setText("☆");
        else{
            favButton.setText("★");
        }

        TextField tf = new TextField();
        tf.setPromptText("Digite a função");
        tf.setText(expressao);
        tf.setOnMouseClicked(this::handleFunctionFieldClick);

        Button removeBtn = new Button("X");
        removeBtn.setOnAction(e -> functionInputsBox.getChildren().remove(fieldBox));

        fieldBox.getChildren().addAll(favButton, tf, removeBtn);

        // Adiciona antes do botão "+"
        functionInputsBox.getChildren().add(functionInputsBox.getChildren().size() - 1, fieldBox);
    }

    @FXML
    private void handleSelectComparisonGraph() {
        // TODO: abrir diálogo para selecionar função salva e gerar gráfico no comparisonGraphPane
    }

    // Método para plotar gráficos (exemplo simples, pode ser com Canvas ou JFreeChart)
    private void plotFunctionOnPane(Pane pane, String function) {
        // TODO: implementar renderização
    }
    
    @FXML
    private void handleFunctionFieldClick(MouseEvent event) {
        selectedFunctionField = (TextField) event.getSource();
    }
    
    @FXML
    private void handleRemoveFunctionField(ActionEvent e) {
        Button btn = (Button) e.getSource();
        HBox parent = (HBox) btn.getParent();
        functionInputsBox.getChildren().remove(parent);
    }

     @FXML
    private void handleToggleFavorite(ActionEvent e) {
        Button btn = (Button) e.getSource();
        
        // A lógica do seu código original para encontrar a expressão
        HBox parentHBox = (HBox) btn.getParent();
        TextField functionField = null;
        for (Node node : parentHBox.getChildren()) {
            if (node instanceof TextField) {
                functionField = (TextField) node;
                break;
            }
        }
        
        if (functionField == null || functionField.getText().isEmpty()) {
            errorMessageLabel.setText("Nenhuma expressão para favoritar neste campo.");
            return;
        }

        String expressaoTexto = functionField.getText();

        // Se o botão está no estado "não favoritado"
        if ("☆".equals(btn.getText())) {
            try {
                // Procura a Expressao correspondente no histórico
                List<Expressao> historico = service.historico(9999);
                Expressao expressaoParaFavoritar = null;
                for (Expressao exp : historico) {
                    if (exp.getEntrada().equals(expressaoTexto)) {
                        expressaoParaFavoritar = exp;
                        break;
                    }
                }
                
                if (expressaoParaFavoritar != null) {
                    // Chama a janela de diálogo para pedir apelido e descrição
                    showFavoriteDialog(expressaoParaFavoritar, btn);
                } else {
                    errorMessageLabel.setText("Expressão não encontrada no histórico para favoritar.");
                }
            } catch (Exception ex) {
                errorMessageLabel.setText("Erro ao buscar a expressão para favoritar.");
                ex.printStackTrace();
            }
            
        } else {
            // Se o botão está no estado "favoritado"
            try {
                // Procura o Favorito correspondente para remover
                List<Favorito> favoritos = service.favoritos();
                for (Favorito f : favoritos) {
                    if (f.getExpressao().getEntrada().equals(expressaoTexto)) {
                        service.removerFavorito(f.getId());
                        btn.setText("☆");
                        errorMessageLabel.setText("Favorito removido!");
                        break;
                    }
                }
            } catch (Exception dbEx) {
                errorMessageLabel.setText("Falha ao desfavoritar. Veja o console.");
                dbEx.printStackTrace();
            }
        }
    }
    
    private void showFavoriteDialog(Expressao expressao, Button toggleBtn) {
        // Cria a nova janela de diálogo
        final Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(toggleBtn.getScene().getWindow());
        dialogStage.setTitle("Adicionar aos Favoritos");

        // Componentes da UI
        TextField apelidoField = new TextField();
        apelidoField.setPromptText("Apelido (ex: Função quadrática)");
        
        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Descrição (opcional)");

        Label apelidoLabel = new Label("Apelido:");
        Label descricaoLabel = new Label("Descrição:");

        Button saveButton = new Button("Salvar");
        Button cancelButton = new Button("Cancelar");

        // Ação do botão Salvar
        saveButton.setOnAction(e -> {
            String apelido = apelidoField.getText();
            String descricao = descricaoField.getText();

            // Validação simples
            if (apelido == null || apelido.trim().isEmpty()) {
                errorMessageLabel.setText("O apelido não pode ser vazio.");
                return;
            }

            try {
                // Chama o serviço para salvar o favorito com os dados do usuário
                
                service.marcarFavorito(expressao.getId(), apelido, descricao);
                toggleBtn.setText("★"); // Atualiza o botão para indicar que foi favoritado
                errorMessageLabel.setText("Expressão salva como favorito!");
                dialogStage.close(); // Fecha a janela de diálogo
            } catch (Exception ex) {
                errorMessageLabel.setText("Falha ao salvar favorito.");
                ex.printStackTrace();
            }
        });

        // Ação do botão Cancelar
        cancelButton.setOnAction(e -> {
            dialogStage.close();
            toggleBtn.setText("☆"); // Volta o ícone para o estado original
        });

        // Layout da janela
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        VBox root = new VBox(10, apelidoLabel, apelidoField, descricaoLabel, descricaoField, buttonBox);
        root.setPadding(new Insets(20));

        dialogStage.setScene(new Scene(root, 400, 200));
        dialogStage.showAndWait();
    }
    
    private void insertTextToSelectedField(String text) {
        if (selectedFunctionField != null) {
            selectedFunctionField.setText(selectedFunctionField.getText() + text);
        }
    }
    
    // Calculadora da parte gráfica
    
    @FXML
    private void handleGraphNumber(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String number = ((Button) event.getSource()).getText();
            insertTextToSelectedField(number);
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphOperator(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String operator = ((Button) event.getSource()).getText();
            insertTextToSelectedField(operator);
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphDecimal(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String decimal = ((Button) event.getSource()).getText();
            insertTextToSelectedField(decimal);
            errorMessageLabelGrafico.setText("");
        }
    }
    
    @FXML
    private void handleGraphSeparator(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String decimal = ((Button) event.getSource()).getText();
            insertTextToSelectedField(decimal);
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphFunction(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String function = ((Button) event.getSource()).getText();
            insertTextToSelectedField(function + "(");
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphConstant(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String constant = ((Button) event.getSource()).getText();
            insertTextToSelectedField(constant);
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphParenthesis(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String parenthesis = ((Button) event.getSource()).getText();
            insertTextToSelectedField(parenthesis);
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphClear(ActionEvent event) {
        if (selectedFunctionField != null) {
            selectedFunctionField.clear();
            errorMessageLabelGrafico.setText("");
        }
    }

    @FXML
    private void handleGraphDelete(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String currentText = selectedFunctionField.getText();
            if (!currentText.isEmpty()) {
                selectedFunctionField.setText(currentText.substring(0, currentText.length() - 1));
            }
            errorMessageLabelGrafico.setText("");
        }
    }
    
    private void restoreInputFormat(){
        String text = selectedFunctionField.getText();
        if(selectedFunctionField != null){
            if(selectedFunctionField.getText().contains("=")){
                int idx = text.indexOf('=');
                selectedFunctionField.setText(text.substring(0, idx-1));
            }
        }
    }
    
    private GraphPlotter graphPlotter;

    @FXML
    private void handleGraphEquals(ActionEvent event) {
        if (selectedFunctionField != null) {
            restoreInputFormat();
            String expression = selectedFunctionField.getText();
            try {
                ResultadoAvaliacao resultado = AnalisadorDeExpressoes.avaliarExpressao(expression);
                
                if (resultado.getTipo() == ResultadoAvaliacao.TipoResultado.NUMERICO) {
                    double resultValue = resultado.getValor();
                    String resultText = decimalFormat.format(resultValue);

                    selectedFunctionField.setText(expression + " = " + resultText);

                    // salvar no histórico
                    try {
                        calcgraph.model.Expressao e = service.registrarExpressao(expression, resultText, 0);
                        lastExpressaoId = e.getId();
                    } catch (Exception dbEx) {
                        System.err.println("Falha ao salvar histórico: " + dbEx.getMessage());
                    }
               }else if (resultado.getTipo() == ResultadoAvaliacao.TipoResultado.GRAFICO) {
                // Se for um resultado de gráfico, plota a função
                    String funcaoString = resultado.getFuncao(); 
                    graphPlotter.plotGraph(funcaoString); 
                    try {
                        calcgraph.model.Expressao e = service.registrarExpressao(expression, "Gráfico gerado", 0);
                        lastExpressaoId = e.getId();
                    } catch (Exception dbEx) {
                        System.err.println("Falha ao salvar histórico: " + dbEx.getMessage());
                    }
                }

            } catch (ExpressionException e) {
                errorMessageLabelGrafico.setText("Erro: " + e.getMessage());
                System.err.println("Erro na expressãoDD (UI): " + e.getMessage());
            } catch (Exception e) {
                errorMessageLabelGrafico.setText("Erro Inesperado. Verifique o console.");
                System.err.println("Ocorreu um erro inesperado (UI): " + e.getMessage());
                e.printStackTrace();
            }
        } 
    }
}
package calcgraph.model;

import calcgraph.model.evaluator.FunctionEvaluator;
import calcgraph.model.exception.ExpressionException;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GraphPlotter {

    private final Pane graphPane;
    private final ListView<String> legendListView;
    private final AnalisadorDeExpressoes analisador;

    // Definição do intervalo inicial do gráfico
    private double xMin = -10;
    private double xMax = 10;
    private double yMin = -10;
    private double yMax = 10;
    
    // Armazena a função atual para redesenhar
    private String currentFunction;
    
    private FunctionEvaluator currentEvaluator;
    
    // Variáveis para o zoom e arrasto
    private static final double ZOOM_FACTOR = 2; // Aumentar este valor para um zoom mais forte
    private double mousePressX;
    private double mousePressY;
    private double dragLastX;
    private double dragLastY;

    public GraphPlotter(Pane graphPane, ListView<String> legendListView) {
        this.graphPane = graphPane;
        this.legendListView = legendListView;
        this.analisador = new AnalisadorDeExpressoes();
        
        
        // Limitar o desenho ao tamanho do Pane
        Rectangle clip = new Rectangle(0, 0, graphPane.getPrefWidth(), graphPane.getPrefHeight());
        graphPane.setClip(clip);

        setupZoom(); // Ajusta a direção do zoom e a velocidade
        setupPanning(); // Novo método para arrastar o gráfico
        redraw(); // Desenhar os eixos e o gráfico inicial
    }

    private void setupZoom() {
        graphPane.setOnScroll((ScrollEvent event) -> {
            double zoomFactor = ZOOM_FACTOR;
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Converter coordenadas da tela para coordenadas do gráfico
            double xGraph = mapValue(mouseX, 0, graphPane.getPrefWidth(), xMin, xMax);
            double yGraph = mapValue(mouseY, 0, graphPane.getPrefHeight(), yMax, yMin);

            if (event.getDeltaY() > 0) {
                // Rolar para trás: zoom out
                zoomFactor = 1 / zoomFactor;
            }

            // Aplicar zoom centrado no ponteiro do mouse
            double newXMin = xGraph - (xGraph - xMin) * zoomFactor;
            double newXMax = xGraph + (xMax - xGraph) * zoomFactor;
            double newYMin = yGraph - (yGraph - yMin) * zoomFactor;
            double newYMax = yGraph + (yMax - yGraph) * zoomFactor;

            // Atualizar os limites
            xMin = newXMin;
            xMax = newXMax;
            yMin = newYMin;
            yMax = newYMax;

            redraw(); // Redesenha o gráfico com os novos limites
            event.consume();
        });
    }
    
    private void setupPanning() {
        // Evento de clique inicial
        graphPane.setOnMousePressed((MouseEvent event) -> {
            mousePressX = event.getX();
            mousePressY = event.getY();
            dragLastX = event.getX();
            dragLastY = event.getY();
            event.consume();
        });

        // Evento de arrastar
        graphPane.setOnMouseDragged((MouseEvent event) -> {
            double dragX = event.getX();
            double dragY = event.getY();

            // Calcula a diferença de coordenadas na tela
            double deltaX = dragX - dragLastX;
            double deltaY = dragY - dragLastY;

            // Mapeia a diferença de tela para as coordenadas do gráfico
            double panX = mapValue(deltaX, 0, graphPane.getPrefWidth(), 0, xMax - xMin);
            double panY = mapValue(deltaY, 0, graphPane.getPrefHeight(), 0, yMax - yMin);

            // Inverte o eixo Y para o arrasto ser intuitivo
            panY = -panY;

            // Atualiza os limites do gráfico
            xMin -= panX;
            xMax -= panX;
            yMin -= panY;
            yMax -= panY;
            
            dragLastX = dragX;
            dragLastY = dragY;

            redraw();
            event.consume();
        });

        // Evento de soltar o botão do mouse
        graphPane.setOnMouseReleased((MouseEvent event) -> {
            // Apenas para consumir o evento e limpar as variáveis, se necessário
            event.consume();
        });
    }

    public void redraw() {
        graphPane.getChildren().clear(); // Limpa tudo, incluindo os eixos
        drawAxes(); // Desenha os eixos novamente

        if (currentFunction != null && !currentFunction.isEmpty()) {
            try {
                // Cria uma única instância do avaliador
                this.currentEvaluator = new FunctionEvaluator(currentFunction);
                plotFunction(currentFunction);
            } catch (ExpressionException e) {
                System.err.println("Erro ao plotar a função: " + e.getMessage());
            }
        }
    }
    
    // Método para desenhar os eixos X e Y
    private void drawAxes() {
        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();

        // Eixo Y
        double xAxisCenter = mapValue(0, xMin, xMax, 0, width);
        Line yAxis = new Line(xAxisCenter, 0, xAxisCenter, height);
        yAxis.setStroke(Color.GRAY);

        // Eixo X
        double yAxisCenter = mapValue(0, yMin, yMax, height, 0);
        Line xAxis = new Line(0, yAxisCenter, width, yAxisCenter);
        xAxis.setStroke(Color.GRAY);

        graphPane.getChildren().addAll(xAxis, yAxis);
        
        double xTickSpacing = getTickSpacing(xMax - xMin);
        for (double val = 0; val < xMax; val += xTickSpacing) {
            drawXAxisTick(val);
        }
        for (double val = -xTickSpacing; val > xMin; val -= xTickSpacing) {
            drawXAxisTick(val);
        }

        // Marcadores do Eixo Y
        double yTickSpacing = getTickSpacing(yMax - yMin);
        for (double val = 0; val < yMax; val += yTickSpacing) {
            drawYAxisTick(val);
        }
        for (double val = -yTickSpacing; val > yMin; val -= yTickSpacing) {
            drawYAxisTick(val);
        }
    }
    
    private void drawXAxisTick(double value) {
        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();
        double xPane = mapValue(value, xMin, xMax, 0, width);
        double yAxisCenter = mapValue(0, yMin, yMax, height, 0);

        Line tick = new Line(xPane, yAxisCenter - 5, xPane, yAxisCenter + 5);
        tick.setStroke(Color.GRAY);
        graphPane.getChildren().add(tick);

        if (Math.abs(value) > 0.0001) { // Evita desenhar o rótulo "0" duas vezes
            Text label = new Text(String.format("%.2f", value));
            label.setFont(new Font(8));
            label.setLayoutX(xPane - label.getLayoutBounds().getWidth() / 2);
            label.setLayoutY(yAxisCenter + 20); // Posição abaixo do eixo
            graphPane.getChildren().add(label);
        }
    }

    // Novo método auxiliar para desenhar um marcador no eixo Y
    private void drawYAxisTick(double value) {
        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();
        double yPane = mapValue(value, yMin, yMax, height, 0);
        double xAxisCenter = mapValue(0, xMin, xMax, 0, width);

        Line tick = new Line(xAxisCenter - 5, yPane, xAxisCenter + 5, yPane);
        tick.setStroke(Color.GRAY);
        graphPane.getChildren().add(tick);

        if (Math.abs(value) > 0.0001) {
            Text label = new Text(String.format("%.2f", value));
            label.setFont(new Font(8));
            label.setLayoutX(xAxisCenter - 25); // Posição à esquerda do eixo
            label.setLayoutY(yPane + label.getLayoutBounds().getHeight() / 4);
            graphPane.getChildren().add(label);
        }
    }

    // Novo método para calcular o espaçamento ideal entre os marcadores
    private double getTickSpacing(double range) {
        double roughStep = range / 10;
        double magnitude = Math.pow(10, Math.floor(Math.log10(roughStep)));
        double normalizedStep = roughStep / magnitude;
        if (normalizedStep < 1.5) {
            return 1 * magnitude;
        } else if (normalizedStep < 3) {
            return 2 * magnitude;
        } else if (normalizedStep < 7.5) {
            return 5 * magnitude;
        } else {
            return 10 * magnitude;
        }
    }

    /**
     * Plota o gráfico de uma função matemática.
     * @param function A expressão da função, por exemplo, "x^2".
     */
    public void plotGraph(String function) {
        this.currentFunction = function; // Armazena a função atual
        redraw();
    }
    
    private void plotFunction(String function) {
        Polyline polyline = new Polyline();
        polyline.setStroke(Color.BLUE);
        polyline.setStrokeWidth(2);

        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();
        // Novo: passo de plotagem
        double plotStep = 1.0;

        for (double xPane = 0; xPane <= width; xPane += plotStep) {
            double xGraph = mapValue(xPane, 0, width, xMin, xMax);
            double yGraph = this.currentEvaluator.evaluate(xGraph);
            double yPane = mapValue(yGraph, yMin, yMax, height, 0);

            // Adiciona o ponto, verificando se é um número válido para evitar falhas
            if (!Double.isNaN(yPane) && !Double.isInfinite(yPane)) {
                polyline.getPoints().addAll(xPane, yPane);
            }
        }
        
        graphPane.getChildren().add(polyline);
        addToLegend(function);
    }

    // Mapeia um valor de um intervalo para outro
    private double mapValue(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    
    // Adiciona a função à legenda
    private void addToLegend(String function) {
        legendListView.getItems().clear(); // Limpa a legenda antes de adicionar
        legendListView.getItems().add("f(x) = " + function);
    }
}
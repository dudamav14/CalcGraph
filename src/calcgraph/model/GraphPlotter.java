package calcgraph.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class GraphPlotter {

    private final Pane graphPane;
    private final ListView<String> legendListView;
    private final AnalisadorDeExpressoes analisador;

    public GraphPlotter(Pane graphPane, ListView<String> legendListView) {
        this.graphPane = graphPane;
        this.legendListView = legendListView;
        this.analisador = new AnalisadorDeExpressoes();
        drawAxes(); // Desenha os eixos assim que o objeto é criado
    }

    // Método para desenhar os eixos X e Y
    private void drawAxes() {
        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();

        // Eixo Y
        Line yAxis = new Line(width / 2, 0, width / 2, height);
        yAxis.setStroke(Color.GRAY);
        yAxis.setStrokeWidth(1);

        // Eixo X
        Line xAxis = new Line(0, height / 2, width, height / 2);
        xAxis.setStroke(Color.GRAY);
        xAxis.setStrokeWidth(1);

        graphPane.getChildren().addAll(xAxis, yAxis);
    }

    /**
     * Plota o gráfico de uma função matemática.
     * @param function A expressão da função, por exemplo, "x^2".
     */
    public void plotGraph(String function) {
        // Limpa gráficos anteriores, mas mantém os eixos
        graphPane.getChildren().removeIf(node -> node instanceof Polyline);

        Polyline polyline = new Polyline();
        polyline.setStroke(Color.BLUE);
        polyline.setStrokeWidth(2);

        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();

        // Itera sobre a largura do painel para calcular os pontos
        for (double xPane = 0; xPane <= width; xPane++) {
            // Converte a coordenada da tela (xPane) para o sistema de coordenadas do gráfico
            // (por exemplo, de -10 a 10)
            double xGraph = mapValue(xPane, 0, width, -10, 10);

            // Avalia a função para o valor de x
            double yGraph = evaluateFunction(function, xGraph);

            // Converte a coordenada do gráfico de volta para a coordenada da tela
            double yPane = mapValue(yGraph, -10, 10, height, 0);

            // Adiciona o ponto à polilinha
            polyline.getPoints().addAll(xPane, yPane);
        }

        graphPane.getChildren().add(polyline);
        addToLegend(function);
    }

    // Converte um valor de um intervalo para outro
    private double mapValue(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    // Avalia a expressão para um dado valor de 'x'
    private double evaluateFunction(String function, double x) {
        // Substitui 'x' na string da função e avalia
        String expression = function.replace("x", "(" + x + ")");
        try {
            // Usa o analisador de expressões para o cálculo
            ResultadoAvaliacao resultado = analisador.avaliarExpressao(expression);
            // Retorna o valor numérico.
            if (resultado.getTipo() == ResultadoAvaliacao.TipoResultado.NUMERICO) {
                return resultado.getValor();
            }
        } catch (Exception e) {
            // Em caso de erro, retorna um valor que não será visível no gráfico
            return Double.NaN;
        }
        return Double.NaN;
    }

    private void addToLegend(String function) {
        ObservableList<String> items = legendListView.getItems();
        items.add(function);
    }
}
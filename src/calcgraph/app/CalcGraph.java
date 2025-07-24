package calcgraph.app;

import calcgraph.model.AnalisadorDeExpressoes;
import calcgraph.model.exception.ExpressionException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada da aplicação CalcGraph.
 * Inclui um loop de entrada de console para testar a funcionalidade da calculadora.
 *
 * @author Caio
 */
public class CalcGraph extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/calcgraph/view/layout/FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("CalcGraph - Calculadora Gráfica");
        stage.show();
    }

    /**
     * Ponto de entrada principal para a aplicação.
     * Configurado para rodar o AnalisadorDeExpressoes via console.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
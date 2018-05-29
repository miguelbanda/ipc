/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panelbarco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author miguelbanda
 */
public class PanelBarco extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PanelBarcoFXML.fxml"));
        
        Scene scene = new Scene(root);
        stage.setResizable(false);
        scene.getStylesheets().add(PanelBarco.class.getResource("dia.css").toExternalForm());
        stage.setScene(scene);
        stage.setOnCloseRequest((e) -> System.exit(0));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

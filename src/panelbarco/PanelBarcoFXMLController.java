/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panelbarco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sf.marineapi.nmea.event.AbstractSentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.MDASentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.util.Position;

/**
 *
 * @author miguelbanda
 */
public class PanelBarcoFXMLController implements Initializable {
    
    private Model model;
    
    @FXML
    private Label textoLatitud;
    @FXML
    private Label textoLongitud;
    @FXML
    private Label labelHDG;
    @FXML
    private Label labelTWD;
    @FXML
    private Label labelTWS;
    @FXML
    private Label labelSOG;
    @FXML
    private Label labelCOG;
    @FXML
    private Label labelTWD_;
    @FXML
    private Label labelTWS_;
    @FXML
    private CheckBox checkNocturno;
    @FXML
    private AnchorPane root;
    @FXML
    private Tab tab1;
    @FXML
    private GridPane grid1;
    @FXML
    private Tab tab2;
    @FXML
    private GridPane grid2;
    @FXML
    private Tab tab3;
    @FXML
    private GridPane grid3;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cambiarADia();
        
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        
        //Cargar Fichero automaticamente
        model = Model.getInstance();
        try {
            cargarFichero();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PanelBarcoFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        // anadimos un listener para que cuando cambie el valor en el modelo 
        //se actualice su valor en su correspondiente representacion grafica
        // en la inicialización del controlador
        
        model.HDGProperty().addListener((observable, oldValue, newValue) -> {
            String dat = String.valueOf(newValue) + "º";
                 Platform.runLater(() -> {
                labelHDG.setText(dat);
            });
        });
        
        model.TWDProperty().addListener((observable, oldValue, newValue) -> {
            String dat = String.valueOf(newValue) + "º";
            Platform.runLater(() -> {
                labelTWD.setText(dat);
            });
        });
        
        model.TWSProperty().addListener((observable, oldValue, newValue)-> {
            String dat = String.valueOf(newValue) + " Kn";
            Platform.runLater(() -> {
                labelTWS.setText(dat);
            });
        });
        
        
        //GPS (Latitud y longitud)
        model.GPSProperty().addListener((observable, oldValue, newValue)-> {
            Platform.runLater(() -> {
                textoLongitud.setText(String.valueOf(df.format(newValue.getLongitude())) + " " + newValue.getLongitudeHemisphere());
                textoLatitud.setText(String.valueOf(df.format(newValue.getLatitude())) + " " + newValue.getLatitudeHemisphere());
            });
        });
        
        //PITCH y ROLL
        
        //COG
        model.COGProperty().addListener((observable, oldValue, newValue)-> {
            String dat = String.valueOf(newValue) + "º";
            Platform.runLater(() -> {
                labelCOG.setText(dat);
            });
        });
        
        //SOG
        model.SOGProperty().addListener((observable, oldValue, newValue)-> {
            String dat = String.valueOf(newValue) + " Kn";
            Platform.runLater(() -> {
                labelSOG.setText(dat);
            });
        });
        
        
    }    
    
    void cambiarADia() {
        tab1.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: #293042;");
        tab2.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: #293042;");
        tab3.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: #293042;");
        grid1.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: #293042;");
        grid2.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: #293042;");
        grid3.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: #293042;");
        root.setStyle("-fx-background-color: #f7f7f7;");
    }
    
    void cambiarANoche() {
        tab1.setStyle("-fx-background-color: #293042; -fx-text-fill: f7f7f7;");
        tab2.setStyle("-fx-background-color: #293042; -fx-text-fill: #f7f7f7;");
        tab3.setStyle("-fx-background-color: #293042; -fx-text-fill: #f7f7f7;");
        grid1.setStyle("-fx-background-color: #293042; -fx-text-fill: #f7f7f7;");
        grid2.setStyle("-fx-background-color: #293042; -fx-text-fill: #f7f7f7;");
        grid3.setStyle("-fx-background-color: #293042; -fx-text-fill: #f7f7f7;");
        root.setStyle("-fx-background-color: #293042;");
    }
    
    void cargarFichero() throws FileNotFoundException {
        File fichero = new File("datos.NMEA");
        model.addSentenceReader(fichero);
    }

    @FXML
    private void modoNocturno(ActionEvent event) {
        if (!checkNocturno.isSelected()) {
            cambiarADia();
        } else {
            cambiarANoche();
        }
    }
    
}

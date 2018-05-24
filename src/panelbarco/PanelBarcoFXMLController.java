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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
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
    private Label labelAWA;
    @FXML
    private Label labelAWS;
    @FXML
    private Label labelTEMP;
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
    @FXML
    private LineChart<String, Number> lnTWS;
    @FXML
    private LineChart<String, Number> lnTWD;
    @FXML
    private Slider slideTWS;
    @FXML
    private Slider slideTWD;
    
    private ObservableList<Double> datosTWD = null;
    private ObservableList<Double> datosTWS = null;
    
    private Integer contadorTWD = new Integer(0);
    private Integer contadorTWS = new Integer(0);
    
    private Double limiteTWD = new Double(120);
    private Double limiteTWS = new Double(120);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //cambiarADia();
        
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        
        //Cargar Fichero automaticamente
        model = Model.getInstance();
        try {
            cargarFichero();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PanelBarcoFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        // Añadir los datos en la grafica
        

        XYChart.Series serieTWS = new XYChart.Series();
        lnTWS.getData().add(serieTWS);
        lnTWS.setCreateSymbols(false);
        lnTWS.getXAxis().setTickLabelsVisible(false);
        lnTWS.getXAxis().setOpacity(0);
        
        XYChart.Series serieTWD = new XYChart.Series();        
        lnTWD.getData().add(serieTWD);
        lnTWD.setCreateSymbols(false);
        lnTWD.getXAxis().setTickLabelsVisible(false);
        lnTWD.getXAxis().setOpacity(0);
        
        // sliders -- listener
        
        slideTWD.valueProperty().addListener((observable, oldVal, newVal) ->
                { setLimiteTWD((Double)newVal); 
        });
        
        slideTWS.valueProperty().addListener((observable, oldVal1, newVal1) ->
                { setLimiteTWS((Double)newVal1); 
        });
        
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
                if(serieTWD.getData().size() > limiteTWD) { serieTWD.getData().remove(0); }
                serieTWD.getData().add(new XYChart.Data(contadorTWD.toString(), newValue.doubleValue()));
                contadorTWD++;
            });
        });        
       
        model.TWSProperty().addListener((observable, oldValue, newValue)-> {
            String dat = String.valueOf(newValue) + " Kn";
            Platform.runLater(() -> {
                labelTWS.setText(dat);
                if(serieTWS.getData().size() > limiteTWS) { serieTWS.getData().remove(0); }
                serieTWS.getData().add(new XYChart.Data(contadorTWS.toString(), newValue.doubleValue()));
                contadorTWS++;
            });
        });
        
        model.AWAProperty().addListener((observable, oldValue, newValue) -> {
            String dat = String.valueOf(newValue) + "º";
            Platform.runLater(() -> {
                labelAWA.setText(dat);
            });
        });
       
        model.TEMPProperty().addListener((observable, oldValue, newValue)-> {
            String dat = String.valueOf(newValue) + "º";
            Platform.runLater(() -> {
                labelTEMP.setText(dat);
            });
        });        
        
        model.AWSProperty().addListener((observable, oldValue, newValue)-> {
            String dat = String.valueOf(newValue) + " Kn";
            Platform.runLater(() -> {
                labelAWS.setText(dat);
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
    
    void cargarFichero() throws FileNotFoundException {
        File fichero = new File("datos.NMEA");
        model.addSentenceReader(fichero);
    }

    @FXML
    private void modoNocturno(ActionEvent event) {
        if (!checkNocturno.isSelected()) {
            root.getStylesheets().clear();
            root.getStylesheets().add(PanelBarco.class.getResource("dia.css").toExternalForm());
        } else {
            root.getStylesheets().clear();
            root.getStylesheets().add(PanelBarco.class.getResource("noche.css").toExternalForm());
        }
    }
    
    private void setLimiteTWD(Double valor) {
        limiteTWD = valor * 60;
    }
    
    private void setLimiteTWS(Double valor) {
        limiteTWS = valor * 60;
    }
    
}

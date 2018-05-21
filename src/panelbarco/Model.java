/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panelbarco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import net.sf.marineapi.nmea.event.AbstractSentenceListener;
import net.sf.marineapi.nmea.io.ExceptionListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.MDASentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.XDRSentence;
import net.sf.marineapi.nmea.util.Position;

/**
 *
 * @author miguelbanda
 */
public class Model {

    //implementa el patron singleton
    // esto asegura que solamente se va a crear una instancia de la clase model
    // y se podra acceder a ella desde cualquier clase del proyecto
    private static Model model;

    private Model() {
        
    }
    
    public static Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }
    
    
    //===================================================================
    // CUIDADO, el objeto de la clase SentenceReader se ejecuta en un hilo
    // no se pueden modificar las propiedades de los objetos graficos desde
    // un metodo ejecutado en este hilo
    
    private SentenceReader reader;

    //True Wind Dir -- direccion del viento respecto al norte
    private final DoubleProperty TWD = new SimpleDoubleProperty();
    public DoubleProperty TWDProperty() {
        return TWD;
    }
    
    // True Wind Speed -- intensidad de viento
    private final DoubleProperty TWS = new SimpleDoubleProperty();
    public DoubleProperty TWSProperty() {
        return TWS;
    }
    
    //Heading - compas magnetic
    private final DoubleProperty HDG = new SimpleDoubleProperty();
    public DoubleProperty HDGProperty() {
        return HDG;
    }
    //==================================================================
    // anade todas las propiedades que necesites, en el hilo principal
    // podras anadir listeners sobre estas propiedades que modifquen la interfaz
    
    // Position -- posicion del GPS
    private final ObjectProperty<Position> GPS = new SimpleObjectProperty();
    public ObjectProperty<Position> GPSProperty() {
        return GPS;
    }
    
    // COG -- rumbo del GPS
    private final DoubleProperty COG = new SimpleDoubleProperty();
    public DoubleProperty COGProperty() {
        return COG;
    }
    // SOG -- velocidad del GPS
    private final DoubleProperty SOG = new SimpleDoubleProperty();
    public DoubleProperty SOGProperty() {
        return SOG;
    }
    
    //
    
    
    
    
    
    
    
    
    
    

    //====================================================================
    //anadir tantos sentenceListener como tipos de sentence queremos tratar
    class HDGSentenceListener
            extends AbstractSentenceListener<HDGSentence> {

        @Override
        public void sentenceRead(HDGSentence sentence) {
            // anadimos el codigo necesario para guardar la información de la sentence    
            HDG.set(sentence.getHeading());
        }
    };

    class MDASentenceListener
            extends AbstractSentenceListener<MDASentence> {

        @Override
        public void sentenceRead(MDASentence sentence) {
            // anadimos el codigo necesario para guardar la información de la sentence 
            TWD.set(sentence.getTrueWindDirection());
            TWS.set(sentence.getWindSpeedKnots());
   
        }
    }
    
    //========================================================================================
    // anade todas las clases de que extiendan AbstractSentenceListener que necesites
    class RMCSentenceListener
            extends AbstractSentenceListener<RMCSentence> {

        @Override
        public void sentenceRead(RMCSentence sentence) {
            GPS.set(sentence.getPosition());
            COG.set(sentence.getCourse());
            SOG.set(sentence.getSpeed());
        }
    }
    
    
    
    
    
    
    // falta por gestiona que solamente hay un senteceReader
    public void addSentenceReader(File file) throws FileNotFoundException {

        InputStream stream = new FileInputStream(file);
        
        if (reader != null) {  // esto ocurre si ya estamos leyendo un fichero
            reader.stop();
        }
        reader = new SentenceReader(stream);
 
        //==================================================================
        //============= Registra todos los sentenceListener que necesites
        HDGSentenceListener hdg = new HDGSentenceListener();
        reader.addSentenceListener(hdg);

        MDASentenceListener mda = new MDASentenceListener();
        reader.addSentenceListener(mda);

        RMCSentenceListener rmd = new RMCSentenceListener();
        reader.addSentenceListener(rmd);
        
                
         //===============================================================

         //===============================================================
         //== Anadimos un exceptionListener para que capture las tramas que 
         // == no tienen parser, ya que no las usamos
         reader.setExceptionListener(e->{System.out.println(e.getMessage());});
         
         //================================================================
         //======== arrancamos el SentenceReader para que empieze a escucha             
        reader.start();
    } 
}

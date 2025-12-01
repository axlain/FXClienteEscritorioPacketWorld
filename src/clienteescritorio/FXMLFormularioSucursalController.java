package clienteescritorio;

import clienteescritorio.dominio.CatalogoImp;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colonia;
import clienteescritorio.pojo.Estado;
import clienteescritorio.pojo.Municipio;
import clienteescritorio.pojo.Pais;
import clienteescritorio.pojo.Sucursal;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioSucursalController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombreCorto;
    @FXML
    private TextField tfCalle;
    @FXML
    private TextField tfNumero;
    @FXML
    private ComboBox<Pais> cbPais;
    @FXML
    private ComboBox<Estado> cbEstado;
    @FXML
    private ComboBox<Municipio> cbMunicipio;
    @FXML
    private ComboBox<Colonia> cbColonia;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private ObservableList<Pais> paises;
    private ObservableList<Estado> estados;
    private ObservableList<Municipio> municipios;
    private ObservableList<Colonia> colonias;
    
    private Sucursal sucursalEdicion;
    private INotificador observador;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // Cargar países al inicio
        cargarPais();

        // Cuando seleccionen un país
        cbPais.setOnAction(event -> {
            cargarEstado();
            cbEstado.setDisable(false);

            // Reiniciar combos dependientes
            cbMunicipio.setDisable(true);
            cbColonia.setDisable(true);
            cbMunicipio.getItems().clear();
            cbColonia.getItems().clear();
        });

        // Cuando seleccionen un estado
        cbEstado.setOnAction(event -> {
            cargarMunicipio();
            cbMunicipio.setDisable(false);
            cbColonia.setDisable(true);
            cbColonia.getItems().clear();
        });

        // Cuando seleccionen un municipio
        cbMunicipio.setOnAction(event -> {
            cargarColonia();
            cbColonia.setDisable(false);
        });
    }    
    
    public void iniciarlizarDatos(Sucursal sucursalEdicion, INotificador observador){
        this.sucursalEdicion = sucursalEdicion;
        this.observador = observador;
        
        if(sucursalEdicion != null){
            lblTitulo.setText("Editar Colaborador");
            tfNombreCorto.setText(sucursalEdicion.getNombreCorto());
            tfCalle.setText(sucursalEdicion.getCalle());
            tfNumero.setText(sucursalEdicion.getNumero());
            
            int posicionPais= obtenerPosicionPais(sucursalEdicion.getIdPais());
            cbPais.getSelectionModel().select(posicionPais);
            
            int posicionEstado= obtenerPosicionEstado(sucursalEdicion.getIdEstado());
            cbEstado.getSelectionModel().select(posicionEstado);
            
            int posicionMunicipio= obtenerPosicionMunicipio(sucursalEdicion.getIdMunicipio());
            cbMunicipio.getSelectionModel().select(posicionMunicipio);
            
            int posicionColonia= obtenerPosicionColonia(sucursalEdicion.getIdColonia());
            cbColonia.getSelectionModel().select(posicionColonia);
            

        } 
    }
    
    @FXML
    private void clickGuardar(ActionEvent event) {
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana(); 
    }
    
    private void cerrarVentana(){
        ((Stage) tfNombreCorto.getScene().getWindow()).close();
    }
    
    private void cargarPais(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerPais();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Pais> paisesAPI = (List<Pais>) respuesta.get(Constantes.KEY_LISTA);
            paises = FXCollections.observableArrayList();
            paises.addAll(paisesAPI);
            cbPais.setItems(paises);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    private void cargarEstado(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerEstado();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Estado> estadosAPI = (List<Estado>) respuesta.get(Constantes.KEY_LISTA);
            estados = FXCollections.observableArrayList();
            estados.addAll(estadosAPI);
            cbEstado.setItems(estados);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    private void cargarMunicipio(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerMunicipio();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Municipio> municipiosAPI = (List<Municipio>) respuesta.get(Constantes.KEY_LISTA);
            municipios = FXCollections.observableArrayList();
            municipios.addAll(municipiosAPI);
            cbMunicipio.setItems(municipios);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    private void cargarColonia(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerColonia();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Colonia> coloniasAPI = (List<Colonia>) respuesta.get(Constantes.KEY_LISTA);
            colonias = FXCollections.observableArrayList();
            colonias.addAll(coloniasAPI);
            cbColonia.setItems(colonias);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    
    
    
    
    
    
    
    
    
    
    private int obtenerPosicionPais(int idPais){
        for(int i = 0; i < paises.size(); i++){
            if(paises.get(i).getIdPais()== idPais){
                return i; 
            }      
        }
        return -1; 
    }
    private int obtenerPosicionEstado(int idEstado){
        for(int i = 0; i < estados.size(); i++){
            if(estados.get(i).getIdEstado()== idEstado){
                return i; 
            }      
        }
        return -1; 
    }
    private int obtenerPosicionMunicipio(int idMunicipio){
        for(int i = 0; i < municipios.size(); i++){
            if(municipios.get(i).getIdMunicipio()== idMunicipio){
                return i; 
            }      
        }
        return -1; 
    }
    private int obtenerPosicionColonia(int idColonias){
        for(int i = 0; i < colonias.size(); i++){
            if(colonias.get(i).getIdColonia()== idColonias){
                return i; 
            }      
        }
        return -1; 
    }
}

package clienteescritorio;

import clienteescritorio.dominio.SucursalImp;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Sucursal;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLAdministracionSucursalesController implements Initializable, INotificador {

    @FXML
    private TextField tfBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private TableView<Sucursal> tvSucursales;
    @FXML
    private TableColumn tcCodigo;
    @FXML
    private TableColumn tcNombreCorto;
    @FXML
    private TableColumn tcEstatus;
    @FXML
    private TableColumn tcCalle;
    @FXML
    private TableColumn tcNumero;
    @FXML
    private TableColumn tcColonia;
    @FXML
    private TableColumn tcMunicipio;
    @FXML
    private TableColumn tcEstado;
    @FXML
    private TableColumn tcCodigoPostal;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnDarBaja;
    
    private ObservableList <Sucursal> sucursales; 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla(); 
        cargarInformacionSucursales();
    }    
    
    private void configurarTabla(){
        tcCodigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        tcNombreCorto.setCellValueFactory(new PropertyValueFactory("nombreCorto"));
        tcEstatus.setCellValueFactory(new PropertyValueFactory("estatusSucursal"));     
        tcCalle.setCellValueFactory(new PropertyValueFactory("calle"));   
        tcNumero.setCellValueFactory(new PropertyValueFactory("numero"));    
        tcColonia.setCellValueFactory(new PropertyValueFactory("colonia"));
        tcMunicipio.setCellValueFactory(new PropertyValueFactory("municipio"));
        tcEstado.setCellValueFactory(new PropertyValueFactory("estado"));
        tcCodigoPostal.setCellValueFactory(new PropertyValueFactory("codigoPostal"));
    }
    
    private void cargarInformacionSucursales(){
        HashMap <String, Object> respuesta = SucursalImp.obtenerTodas(); 
        //se hace un casteo pq se declaro incialmente como objeto y no boolean
        boolean esError =(boolean)respuesta.get("error"); 
        if(!esError){
            //se hace un casteo pq se declaro incialmente como objeto y no lista
            List<Sucursal> sucursalesAPI = (List<Sucursal>)respuesta.get(Constantes.KEY_LISTA); 
            //instancia observablelist
            sucursales = FXCollections.observableArrayList();
            sucursales.addAll(sucursalesAPI); 
            tvSucursales.setItems(sucursales);
        }else{
            //se concatena siulando un toString
            Utilidades.mostrarAlertaSimple("Error al cargar", ""+respuesta.get("mensaje"),
                    Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void clickBuscar(ActionEvent event) {
    }

    @FXML
    private void clickLimpiarBusqueda(ActionEvent event) {
        tfBuscar.clear();
        tvSucursales.setItems(sucursales);
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
         irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Sucursal sucursal = tvSucursales.getSelectionModel().getSelectedItem();
        if (sucursal != null){
            irFormulario(sucursal);
        } else {
            Utilidades.mostrarAlertaSimple("Selecciona una sucursal", "Para editar la información de la sucursal, debes de seleccionarla primero en la tabla", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clickDarBaja(ActionEvent event) {
        Sucursal sucursal = tvSucursales.getSelectionModel().getSelectedItem();
        if (sucursal != null){
            darDeBajaSucursal(sucursal);
        } else {
            Utilidades.mostrarAlertaSimple("Selecciona una sucursal", 
                    "Para dar de baja la sucursal, debes de seleccionarla primero en la tabla", Alert.AlertType.WARNING);
        }
    }
    
    private void irFormulario(Sucursal sucursal){
        try{
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFormularioSucursal.fxml"));
            Parent vista = cargador.load();
            FXMLFormularioSucursalController controlador = cargador.getController();
            controlador.iniciarlizarDatos(sucursal, this);
            
            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Registro de unidad");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    private void darDeBajaSucursal(Sucursal sucursal){
        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
            "Confirmar baja",
            "¿Estás seguro de dar de baja la sucursal \"" 
            + sucursal.getNombreCorto() + "\"?"
        );

        if (confirmado){
            clienteescritorio.dto.Respuesta respuesta =
                    SucursalImp.eliminar(sucursal.getIdSucursal());

            if (!respuesta.isError()){
                Utilidades.mostrarAlertaSimple(
                        "Sucursal dada de baja",
                        respuesta.getMensaje(),
                        Alert.AlertType.INFORMATION
                );
                cargarInformacionSucursales();
            } else {
                Utilidades.mostrarAlertaSimple(
                        "Error al dar de baja",
                        respuesta.getMensaje(),
                        Alert.AlertType.ERROR
                );
            }
        }
    }
    
    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        System.out.println("Operación: " + operacion + ",unidad: " + nombre);
        cargarInformacionSucursales();
    }
}

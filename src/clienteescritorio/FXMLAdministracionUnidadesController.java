package clienteescritorio;

import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.dominio.UnidadImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Unidad;
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


public class FXMLAdministracionUnidadesController implements Initializable, INotificador {

    @FXML
    private TextField tfBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private TableView<Unidad> tvUnidades;
    @FXML
    private TableColumn tcVIN;
    @FXML
    private TableColumn tcNII;
    @FXML
    private TableColumn tcEstatusUnidad;
    @FXML
    private TableColumn tcMarca;
    @FXML
    private TableColumn tcModelo;
    @FXML
    private TableColumn tcAnio;
    @FXML
    private TableColumn tcTipoDeUnidad;
    @FXML
    private TableColumn tcConductor;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnAsignarUnidad;
    
    private ObservableList <Unidad> unidades; 
    @FXML
    private Button btnDesasignarConductor;
    @FXML
    private Button btnDarBaja;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla(); 
        cargarInformacionUnidades(); 
    }    
    private void configurarTabla(){
        tcVIN.setCellValueFactory(new PropertyValueFactory("vin"));
        tcNII.setCellValueFactory(new PropertyValueFactory("numeroInterno"));
        tcEstatusUnidad.setCellValueFactory(new PropertyValueFactory("estatusUnidad"));     
        tcMarca.setCellValueFactory(new PropertyValueFactory("marca"));   
        tcModelo.setCellValueFactory(new PropertyValueFactory("modelo"));    
        tcAnio.setCellValueFactory(new PropertyValueFactory("anio"));
        tcTipoDeUnidad.setCellValueFactory(new PropertyValueFactory("tipoUnidad"));
        tcConductor.setCellValueFactory(new PropertyValueFactory("numeroPersonalConductor"));
    }
    
    private void cargarInformacionUnidades(){
        HashMap <String, Object> respuesta = UnidadImp.obtenerTodos(); 
        //se hace un casteo pq se declaro incialmente como objeto y no boolean
        boolean esError =(boolean)respuesta.get("error"); 
        if(!esError){
            //se hace un casteo pq se declaro incialmente como objeto y no lista
            List<Unidad> unidadesAPI = (List<Unidad>)respuesta.get("unidades"); 
            //instancia observablelist
            unidades = FXCollections.observableArrayList();
            unidades.addAll(unidadesAPI); 
            tvUnidades.setItems(unidades);
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
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
         irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Unidad unidad = tvUnidades.getSelectionModel().getSelectedItem();
        if (unidad != null){
            irFormulario(unidad);
        } else {
            Utilidades.mostrarAlertaSimple("Selecciona una unidad", 
                    "Para editar la información de la unidad, debes de seleccionarlo primero de la tabla", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void clickDarBaja(ActionEvent event) {
        Unidad unidad = tvUnidades.getSelectionModel().getSelectedItem();
        if (unidad != null){
            darDeBajaUnidad(unidad);
        } else {
            Utilidades.mostrarAlertaSimple("Selecciona una unidad", 
                    "Para dar de baja la unidad, debes de seleccionarla primero en la tabla", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clickAsignarUnidad(ActionEvent event) {
        Unidad unidad = tvUnidades.getSelectionModel().getSelectedItem();
        if (unidad != null){
            irAsignarConductor(unidad);
        } else {
            Utilidades.mostrarAlertaSimple("Selecciona una unidad", 
                    "Para asignarle un conductor a una unidad, primero debes selecionar la unidad", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void clickDesasignarConductor(ActionEvent event) {
        desasignarConductor();
    }
    
    private void irFormulario(Unidad unidad){
        try{
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFormularioUnidad.fxml"));
            Parent vista = cargador.load();
            FXMLFormularioUnidadController controlador = cargador.getController();
            controlador.iniciarlizarDatos(unidad, this);
            
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
    
    private void irAsignarConductor(Unidad unidad){
        try{
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLAsignarConductor.fxml"));
            Parent vista = cargador.load();
            FXMLAsignarConductorController controlador = cargador.getController();
            controlador.iniciarlizarDatos(unidad, this);
            
            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(" Asignar conductor");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    
    private void darDeBajaUnidad(Unidad unidad){
        // Pedir motivo de baja
        String motivo = Utilidades.mostrarDialogoEntrada(
                "Motivo de baja",
                "Ingrese el motivo de la baja:"
        );

        if (motivo == null || motivo.trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                "Motivo requerido",
                "No se puede realizar la baja sin un motivo.",
                Alert.AlertType.WARNING
            );
            return;
        }

        //Confirmación
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                "Confirmar baja",
                "¿Deseas dar de baja esta unidad?\nMotivo: " + motivo
        );

        if (!confirmar) {
            return;
        }

        Respuesta respuesta = UnidadImp.eliminar(unidad.getIdUnidad(), motivo);

        // Manejar respuesta
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple(
                "Unidad dada de baja",
                respuesta.getMensaje(),
                Alert.AlertType.INFORMATION
            );

            // Refrescar tabla
            cargarInformacionUnidades();

            // Notificar a quien abrió esta pantalla (si aplica)
            notificarOperacionExitosa("baja", unidad.getVin());

        } else {
            Utilidades.mostrarAlertaSimple(
                "Error al dar de baja",
                respuesta.getMensaje(),
                Alert.AlertType.ERROR
            );
        }
    }
    
    private void desasignarConductor() {
        Unidad unidad = tvUnidades.getSelectionModel().getSelectedItem();

        if (unidad == null) {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona una unidad",
                    "Para desasignar el conductor, primero debes seleccionar una unidad.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // Validar si la unidad tiene conductor asignado
        if (unidad.getIdConductor() == null || unidad.getIdConductor() == 0) {
            Utilidades.mostrarAlertaSimple(
                    "Sin conductor",
                    "Esta unidad no tiene un conductor asignado.",
                    Alert.AlertType.INFORMATION
            );
            return;
        }

        // Confirmación
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                "Confirmar desasignación",
                "¿Deseas desasignar al conductor de esta unidad?"
        );

        if (!confirmar) {
            return;
        }

        Respuesta respuesta = ColaboradorImp.desasignarConductor(unidad.getIdConductor());
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple(
                    "Conductor desasignado",
                    respuesta.getMensaje(),
                    Alert.AlertType.INFORMATION
            );

            // Refrescar tabla
            cargarInformacionUnidades();

            // Notificar al observador
            notificarOperacionExitosa("desasignación", unidad.getVin());

        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error al desasignar conductor",
                    respuesta.getMensaje(),
                    Alert.AlertType.ERROR
            );
        }
    }


    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        System.out.println("Operación: " + operacion + ",unidad: " + nombre);
        cargarInformacionUnidades();
    }

   
   
    
}

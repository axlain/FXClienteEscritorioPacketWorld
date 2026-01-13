package clienteescritorio;

import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import java.io.File;
import java.nio.file.Files;
import javafx.stage.FileChooser;
public class FXMLAdministracionColaboradoresController implements Initializable, INotificador {

    @FXML
    private TextField tfBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private TableView<Colaborador> tvColaboradores;
    @FXML
    private TableColumn tcNumeroPersonal;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcCurp;
    @FXML
    private TableColumn tcCorreo;
    @FXML
    private TableColumn tcRol;
    @FXML
    private TableColumn tcSucursal;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnSubirFoto;
    private ObservableList<Colaborador> colaboradores;
    private Colaborador colaboradorSesion;
    private INotificador observadorPrincipal;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionColaboradores();
    }
    public void inicializar(Colaborador colaboradorSesion, INotificador observadorPrincipal) {
    this.colaboradorSesion = colaboradorSesion;
    this.observadorPrincipal = observadorPrincipal;
}




    
private void configurarTabla(){
    tcNumeroPersonal.setCellValueFactory(new PropertyValueFactory("numeroPersonal"));
    tcNombre.setCellValueFactory(new PropertyValueFactory("nombreCompleto"));
    tcCurp.setCellValueFactory(new PropertyValueFactory("curp"));
    tcCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
    tcRol.setCellValueFactory(new PropertyValueFactory("rol"));
    tcSucursal.setCellValueFactory(new PropertyValueFactory("sucursal"));
}
    
    private void cargarInformacionColaboradores(){
        HashMap<String, Object> respuesta = ColaboradorImp.obtenerTodos();
        boolean esError = (boolean)respuesta.get("error");
        if(!esError){
            List<Colaborador> colaboradoresAPI = (List<Colaborador>)respuesta.get("colaboradores");
            colaboradores = FXCollections.observableArrayList();
            colaboradores.addAll(colaboradoresAPI);
            tvColaboradores.setItems(colaboradores);
        }else{
            Utilidades.mostrarAlertaSimple("Error al cargar", ""+respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText().trim();

        if (filtro.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Campo vacío",
                    "Ingrese un nombre, número del personal o rol para buscar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        HashMap<String, Object> respuesta = ColaboradorImp.buscar(filtro);
        boolean esError = (boolean) respuesta.get("error");

        if (!esError) {

            List<Colaborador> lista =
                    (List<Colaborador>) respuesta.get("colaboradores");


            if (lista == null) {
                lista = FXCollections.observableArrayList();
            }

            ObservableList<Colaborador> colaboradoresBusqueda =
                    FXCollections.observableArrayList(lista);

            tvColaboradores.setItems(colaboradoresBusqueda);

        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error en la búsqueda",
                    respuesta.get("mensaje").toString(),
                    Alert.AlertType.ERROR
            );
        }
    }


    @FXML
    private void clickLimpiarBusqueda(ActionEvent event) {
        tfBuscar.clear();
        tvColaboradores.setItems(colaboradores);
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Colaborador colaborador = tvColaboradores.getSelectionModel().getSelectedItem();
        if (colaborador != null){
            irFormulario(colaborador);
        } else {
            Utilidades.mostrarAlertaSimple("Selecciona un colaborador", "Para editar la información del colaborador, debes de seleccionarlo primero de la tabla", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clickEliminar(ActionEvent event) {
        Colaborador seleccionado = tvColaboradores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un colaborador",
                    "Para eliminar la información del colaborador, primero debes seleccionarlo de la tabla",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // Bloquear eliminarse a sí mismo
        if (colaboradorSesion != null
                && seleccionado.getIdColaborador() == colaboradorSesion.getIdColaborador()) {
            Utilidades.mostrarAlertaSimple(
                    "Acción no permitida",
                    "No puedes eliminar tu propio usuario mientras tienes la sesión iniciada.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean confirmarOperacion = Utilidades.mostrarAlertaConfirmacion(
                "Eliminar colaborador",
                "¿Estás seguro de eliminar el registro del colaborador " + seleccionado.getNombre() + "?\n"
                + "Al eliminar un registro no podrás recuperar la información del colaborador."
        );

        if (confirmarOperacion) {
            eliminarColaborador(seleccionado.getIdColaborador());
        }
    }
 
    private void irFormulario(Colaborador colaborador){
        try{
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFormularioColaborador.fxml"));
            Parent vista = cargador.load();
            FXMLFormularioColaboradorController controlador = cargador.getController();
            controlador.iniciarlizarDatos(colaborador, this);
            
            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Registro de Colaborador");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    private void eliminarColaborador(int idColaborador){
        Respuesta respuesta = ColaboradorImp.eliminar(idColaborador);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Registro eliminado", "El registro del colaborador fue eliminado correctamente", Alert.AlertType.INFORMATION);
            cargarInformacionColaboradores();
        } else {
            Utilidades.mostrarAlertaSimple("Error al eliminar colaborador", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

@FXML
private void clickSubirFoto(ActionEvent event) {
    Colaborador seleccionado = tvColaboradores.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        Utilidades.mostrarAlertaSimple(
                "Seleccionar colaborador",
                "Debe seleccionar un colaborador de la tabla para subir su foto.",
                Alert.AlertType.WARNING
        );
        return;
    }

    irVentanaFoto(seleccionado.getIdColaborador());
}

private void irVentanaFoto(int idColaborador) {
    try {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFotoColaborador.fxml"));
        Parent vista = cargador.load();

        FXMLFotoColaboradorController controlador = cargador.getController();
        controlador.inicializar(idColaborador, this); // <- NOTIFICADOR

        Stage escenario = new Stage();
        escenario.setScene(new Scene(vista));
        escenario.setTitle("Foto del Colaborador");
        escenario.initModality(Modality.APPLICATION_MODAL);
        escenario.showAndWait();

    } catch (IOException ex) {
        ex.printStackTrace();
        Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir la ventana de foto.", Alert.AlertType.ERROR);
    }
}

@Override
public void notificarFotoActualizada(int idColaborador) {
    int index = tvColaboradores.getSelectionModel().getSelectedIndex();
    cargarInformacionColaboradores();

    if (index >= 0 && index < tvColaboradores.getItems().size()) {
        tvColaboradores.getSelectionModel().select(index);
    }

    if (observadorPrincipal != null) {
        observadorPrincipal.notificarFotoActualizada(idColaborador);
    }
}




    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        System.out.println("Operación: " + operacion + ", nombre colaborador: " + nombre);
        cargarInformacionColaboradores();
    }
    
}
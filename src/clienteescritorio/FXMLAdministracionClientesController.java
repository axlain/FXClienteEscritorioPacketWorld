package clienteescritorio;

import clienteescritorio.dominio.ClienteImp;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Cliente;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

public class FXMLAdministracionClientesController implements Initializable, INotificador {

    @FXML
    private TextField tfBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private TableView<Cliente> tvClientes;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcApellidoPaterno;
    @FXML
    private TableColumn tcApellidoMaterno;
    @FXML
    private TableColumn tcCorreo;
    @FXML
    private TableColumn tcTelefono;
    @FXML
    private TableColumn tcCalle;
    @FXML
    private TableColumn tcNumero;
    @FXML
    private TableColumn tcColonia;
    @FXML
    private TableColumn tcEstado;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    
    private ObservableList<Cliente> clientes;
    @FXML
    private TableColumn tcCodigoPostal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionClientes();
    }    
    private void configurarTabla() {
        tcNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        tcApellidoPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        tcApellidoMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        tcCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
        tcTelefono.setCellValueFactory(new PropertyValueFactory("telefono"));
        tcCalle.setCellValueFactory(new PropertyValueFactory("calle"));
        tcNumero.setCellValueFactory(new PropertyValueFactory("numero"));
        tcColonia.setCellValueFactory(new PropertyValueFactory("colonia"));
        tcEstado.setCellValueFactory(new PropertyValueFactory("estado"));
        tcCodigoPostal.setCellValueFactory(new PropertyValueFactory("codigoPostal"));

    }

    private void cargarInformacionClientes() {
        HashMap<String, Object> respuesta = ClienteImp.obtenerTodos();
        boolean esError = (boolean) respuesta.get("error");

        if (!esError) {
            List<Cliente> lista = (List<Cliente>) respuesta.get(Constantes.KEY_LISTA);
            clientes = FXCollections.observableArrayList(lista);
            tvClientes.setItems(clientes);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error al cargar",
                    respuesta.get("mensaje").toString(),
                    Alert.AlertType.ERROR
            );
        }
    }
    
    @FXML
    private void clickBuscar(ActionEvent event) {
         String filtro = tfBuscar.getText().trim();

        if (filtro.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Campo vacío",
                    "Ingresa un criterio de búsqueda",
                    Alert.AlertType.WARNING
            );
            return;
        }

        HashMap<String, Object> respuesta = ClienteImp.buscar(filtro);
        boolean esError = (boolean) respuesta.get(Constantes.KEY_ERROR);

        if (!esError) {

            List<Cliente> lista =
                    (List<Cliente>) respuesta.get(Constantes.KEY_LISTA);

            if (lista == null) {
                lista = new ArrayList<>();
            }

            ObservableList<Cliente> clientesBusqueda =
                    FXCollections.observableArrayList();
            clientesBusqueda.addAll(lista);

            tvClientes.setItems(clientesBusqueda);

        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error en la búsqueda",
                    respuesta.get(Constantes.KEY_MENSAJE).toString(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void clickLimpiarBusqueda(ActionEvent event) {
        tfBuscar.clear();
        tvClientes.setItems(clientes);
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
         irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Cliente cliente = tvClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            irFormulario(cliente);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un cliente",
                    "Debes seleccionar un cliente de la tabla.",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clickEliminar(ActionEvent event) {
        Cliente cliente = tvClientes.getSelectionModel().getSelectedItem();

        if (cliente != null) {
            boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                    "Eliminar cliente",
                    "¿Estás seguro de eliminar al cliente "
                    + cliente.getNombre() + "?"
            );

            if (confirmado) {
                eliminarCliente(cliente.getIdCliente());
            }
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un cliente",
                    "Debes seleccionar un cliente de la tabla.",
                    Alert.AlertType.WARNING
            );
        }
    }
     private void eliminarCliente(int idCliente) {
        clienteescritorio.dto.Respuesta respuesta = ClienteImp.eliminar(idCliente);

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple(
                    "Cliente eliminado",
                    respuesta.getMensaje(),
                    Alert.AlertType.INFORMATION
            );
            cargarInformacionClientes();
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    respuesta.getMensaje(),
                    Alert.AlertType.ERROR
            );
        }
    }
    
    private void irFormulario(Cliente cliente){
        try{
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFormularioCliente.fxml"));
            Parent vista = cargador.load();
            FXMLFormularioClienteController controlador = cargador.getController();
            controlador.inicializarDatos(cliente, this);
            
            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Registro de cliente");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionClientes();
    }
}

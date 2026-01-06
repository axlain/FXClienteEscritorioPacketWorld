package clienteescritorio;

import clienteescritorio.dominio.ClienteImp;
import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.dominio.DestinatarioImp;
import clienteescritorio.dominio.EnvioImp;
import clienteescritorio.dominio.SucursalImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Cliente;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.pojo.Destinatario;
import clienteescritorio.pojo.Envio;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLFormularioEnvioController implements Initializable, INotificador {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Sucursal> cbSucursal;
    @FXML private ComboBox<Destinatario> cbDestinatario;
    @FXML private ComboBox<Colaborador> cbConductor;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Envio envioEdicion;
    private INotificador observador;

    // sesión
    private Integer idCreadoPor;
    private Integer idSucursalColaborador;

    // listas
    private ObservableList<Cliente> clientes;
    private ObservableList<Sucursal> sucursales;
    private ObservableList<Destinatario> destinatarios;
    private ObservableList<Colaborador> conductores;
    @FXML
    private Button btnAgregarDestinatario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarClientes();
        cargarSucursales();     // aquí aplicamos selección automática si ya tenemos idSucursalColaborador
        cargarDestinatarios();
        cargarConductores();
    }

    public void iniciarlizarDatos(Envio envioEdicion, INotificador observador, Integer idCreadoPor, Integer idSucursalColaborador) {
        this.envioEdicion = envioEdicion;
        this.observador = observador;
        this.idCreadoPor = idCreadoPor;
        this.idSucursalColaborador = idSucursalColaborador;

        if (idCreadoPor == null) {
            Utilidades.mostrarAlertaSimple(
                    "Sesión no válida",
                    "No se detectó el usuario que está registrando el envío.",
                    Alert.AlertType.ERROR
            );
            btnGuardar.setDisable(true);
            return;
        }

        if (envioEdicion != null) {
            lblTitulo.setText("Editar Envío");

            cbCliente.getSelectionModel().select(obtenerPosicionCliente(envioEdicion.getIdCliente()));
            cbSucursal.getSelectionModel().select(obtenerPosicionSucursal(envioEdicion.getIdSucursal()));
            cbDestinatario.getSelectionModel().select(obtenerPosicionDestinatario(envioEdicion.getIdDestinatario()));
            cbConductor.getSelectionModel().select(obtenerPosicionConductor(envioEdicion.getIdConductor()));

        } else {
            lblTitulo.setText("Registrar Envío");
            // auto-seleccionar sucursal del colaborador
            seleccionarSucursalColaborador();
        }
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!sonCamposValidos()) return;

        Envio envio = new Envio();

        envio.setIdCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
        envio.setIdSucursal(cbSucursal.getSelectionModel().getSelectedItem().getIdSucursal());
        envio.setIdDestinatario(cbDestinatario.getSelectionModel().getSelectedItem().getIdDestinatario());
        envio.setIdConductor(cbConductor.getSelectionModel().getSelectedItem().getIdColaborador());

        envio.setIdCreadoPor(idCreadoPor);

        if (envioEdicion == null) {
            registrarEnvio(envio);
        } else {
            envio.setIdEnvio(envioEdicion.getIdEnvio());
            editarEnvio(envio);
        }
    }

    private boolean sonCamposValidos() {
        String mensaje = "";

        if (cbCliente.getSelectionModel().getSelectedItem() == null) mensaje += "- Debes seleccionar un Cliente.\n";
        if (cbSucursal.getSelectionModel().getSelectedItem() == null) mensaje += "- Debes seleccionar una Sucursal.\n";
        if (cbDestinatario.getSelectionModel().getSelectedItem() == null) mensaje += "- Debes seleccionar un Destinatario.\n";
        if (cbConductor.getSelectionModel().getSelectedItem() == null) mensaje += "- Debes seleccionar un Conductor.\n";
        if (idCreadoPor == null) mensaje += "- No hay sesión activa.\n";

        if (!mensaje.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", mensaje, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void registrarEnvio(Envio envio) {
        Respuesta respuesta = EnvioImp.registrar(envio);
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Envío registrado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            if (observador != null) observador.notificarOperacionExitosa("registro", "envío");
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al registrar envío", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    private void editarEnvio(Envio envio) {
        Respuesta respuesta = EnvioImp.editar(envio);
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Envío editado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            if (observador != null) observador.notificarOperacionExitosa("edición", "envío");
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al editar envío", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // -------------------- CARGAS --------------------

    private void cargarClientes() {
        HashMap<String, Object> respuesta = ClienteImp.obtenerTodos();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Cliente> lista = (List<Cliente>) respuesta.get(Constantes.KEY_LISTA);
            clientes = FXCollections.observableArrayList(lista);
            cbCliente.setItems(clientes);
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }

    private void cargarSucursales() {
        HashMap<String, Object> respuesta = SucursalImp.obtenerTodas();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Sucursal> lista = (List<Sucursal>) respuesta.get(Constantes.KEY_LISTA);
            sucursales = FXCollections.observableArrayList(lista);
            cbSucursal.setItems(sucursales);

            // ✅ clave: si es registro y ya tenemos la sucursal del colaborador, selecciónala aquí también
            if (envioEdicion == null) {
                seleccionarSucursalColaborador();
            }

        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }

    private void cargarDestinatarios() {
        HashMap<String, Object> respuesta = DestinatarioImp.obtenerTodos();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Destinatario> lista = (List<Destinatario>) respuesta.get(Constantes.KEY_LISTA);
            destinatarios = FXCollections.observableArrayList(lista);
            cbDestinatario.setItems(destinatarios);
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }

    private void cargarConductores() {
        HashMap<String, Object> respuesta = ColaboradorImp.obtenerConductores();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Colaborador> lista = (List<Colaborador>) respuesta.get(Constantes.KEY_LISTA);
            conductores = FXCollections.observableArrayList(lista);
            cbConductor.setItems(conductores);
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }

    // ✅ método que te faltaba
    private void seleccionarSucursalColaborador() {
        if (idSucursalColaborador == null) return;
        if (sucursales == null || sucursales.isEmpty()) return;

        int pos = obtenerPosicionSucursal(idSucursalColaborador);
        if (pos >= 0) {
            cbSucursal.getSelectionModel().select(pos);
        }
    }

    // -------------------- POSICIONES --------------------

    private int obtenerPosicionCliente(int idCliente) {
        if (clientes == null) return -1;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdCliente() == idCliente) return i;
        }
        return -1;
    }

    private int obtenerPosicionSucursal(int idSucursal) {
        if (sucursales == null) return -1;
        for (int i = 0; i < sucursales.size(); i++) {
            if (sucursales.get(i).getIdSucursal() == idSucursal) return i;
        }
        return -1;
    }

    private int obtenerPosicionDestinatario(int idDestinatario) {
        if (destinatarios == null) return -1;
        for (int i = 0; i < destinatarios.size(); i++) {
            if (destinatarios.get(i).getIdDestinatario() == idDestinatario) return i;
        }
        return -1;
    }

    private int obtenerPosicionConductor(int idConductor) {
        if (conductores == null) return -1;
        for (int i = 0; i < conductores.size(); i++) {
            if (conductores.get(i).getIdColaborador() == idConductor) return i;
        }
        return -1;
    }

    @FXML
    private void clickAgregarDestinatario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioDestinatario.fxml"));
            Parent vista = loader.load();

            // controlador del formulario destinatario
            FXMLFormularioDestinatarioController controller = loader.getController();

            // pasar este formulario como observador
            controller.inicializarDatos(this);

            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(vista));
            stage.setTitle("Registrar Destinatario");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    "No se pudo abrir el formulario de destinatario",
                    Alert.AlertType.ERROR
            );
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarDestinatarios();
    }
}

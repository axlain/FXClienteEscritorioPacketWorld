package clienteescritorio;

import clienteescritorio.dominio.CatalogoImp;
import clienteescritorio.dominio.ClienteImp;
import clienteescritorio.dto.RSDatosCodigoPostal;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Cliente;
import clienteescritorio.pojo.Colonia;
import clienteescritorio.pojo.Estado;
import clienteescritorio.pojo.Municipio;
import clienteescritorio.pojo.Pais;
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

public class FXMLFormularioClienteController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private TextField tfCorreo;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfCalle;
    @FXML
    private TextField tfNumero;
    @FXML
    private TextField tfCodigoPostal;
    @FXML
    private Button btnBuscarCP;
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

    private Cliente clienteEdicion;
    private INotificador observador;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPais();
        cbPais.setDisable(true);
        cbEstado.setDisable(true);
        cbMunicipio.setDisable(true);
        cbColonia.setDisable(true);
        limitarTelefono();
        limitarNumeroExterior();
    }

    public void inicializarDatos(Cliente cliente, INotificador observador) {
        this.clienteEdicion = cliente;
        this.observador = observador;

        if (cliente != null) {
            lblTitulo.setText("Editar Cliente");

            tfNombre.setText(cliente.getNombre());
            tfApellidoPaterno.setText(cliente.getApellidoPaterno());
            tfApellidoMaterno.setText(cliente.getApellidoMaterno());
            tfCorreo.setText(cliente.getCorreo());
            tfTelefono.setText(cliente.getTelefono());
            tfCalle.setText(cliente.getCalle());
            tfNumero.setText(cliente.getNumero());
            tfCodigoPostal.setText(cliente.getCodigoPostal());
        } else {
            lblTitulo.setText("Registrar Cliente");
        }
    }


    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!sonCamposValidos()) {
            return;
        }

        Cliente cliente = (clienteEdicion != null) ? clienteEdicion : new Cliente();

        cliente.setNombre(tfNombre.getText().trim());
        cliente.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        cliente.setApellidoMaterno(tfApellidoMaterno.getText().trim());
        cliente.setCorreo(tfCorreo.getText().trim());
        cliente.setTelefono(tfTelefono.getText().trim());
        cliente.setCalle(tfCalle.getText().trim());
        cliente.setNumero(tfNumero.getText().trim());
        cliente.setCodigoPostal(tfCodigoPostal.getText().trim());

        cliente.setIdPais(cbPais.getSelectionModel().getSelectedItem().getIdPais());
        cliente.setIdEstado(cbEstado.getSelectionModel().getSelectedItem().getIdEstado());
        cliente.setIdMunicipio(cbMunicipio.getSelectionModel().getSelectedItem().getIdMunicipio());
        cliente.setIdColonia(cbColonia.getSelectionModel().getSelectedItem().getIdColonia());

        if (clienteEdicion != null) {
            cliente.setIdCliente(clienteEdicion.getIdCliente());
            editarCliente(cliente);
        } else {
            registrarCliente(cliente);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) tfNombre.getScene().getWindow()).close();
    }

    private boolean sonCamposValidos() {
        String mensaje = "";

        if (tfNombre.getText().trim().isEmpty()) mensaje += "- Nombre obligatorio\n";
        if (tfApellidoPaterno.getText().trim().isEmpty()) mensaje += "- Apellido paterno obligatorio\n";
        if (tfCorreo.getText().trim().isEmpty()) mensaje += "- Correo obligatorio\n";
        if (!tfCodigoPostal.getText().matches("\\d{5}")) mensaje += "- CP inválido\n";

        if (cbPais.getValue() == null ||
            cbEstado.getValue() == null ||
            cbMunicipio.getValue() == null ||
            cbColonia.getValue() == null) {

            mensaje += "- Primero debes ingresar el CP y presionar Buscar.\n";
        }

        if (!mensaje.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", mensaje, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void registrarCliente(Cliente cliente) {
        Respuesta respuesta = ClienteImp.registrar(cliente);
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Cliente registrado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("registro", cliente.getNombre());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    private void editarCliente(Cliente cliente) {
        Respuesta respuesta = ClienteImp.editar(cliente);
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Cliente editado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("edición", cliente.getNombre());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscarCP(ActionEvent event) {
        String cp = tfCodigoPostal.getText().trim();

        if (!cp.matches("\\d{5}")) {
            Utilidades.mostrarAlertaSimple("Advertencia", "Ingresa un CP válido de 5 dígitos.", Alert.AlertType.WARNING);
            return;
        }

        if (tfCodigoPostal.getScene() != null) {
            tfCodigoPostal.getScene().setCursor(javafx.scene.Cursor.WAIT);
        }

        try {
            HashMap<String, Object> respuestaDatos = CatalogoImp.obtenerDatosCP(cp);

            if (!(boolean) respuestaDatos.get(Constantes.KEY_ERROR)) {
                HashMap<String, Object> respuestaColonias = CatalogoImp.obtenerColoniasPorCP(cp);
                respuestaDatos.put("colonias_extra", respuestaColonias);
            }

            procesarRespuestaCP(respuestaDatos, cp);

        } catch (Exception e) {
            Utilidades.mostrarAlertaSimple("Error", "Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        if (tfCodigoPostal.getScene() != null) {
            tfCodigoPostal.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        }
    }

    private void limitarTelefono() {
        tfTelefono.textProperty().addListener((obs, oldValue, newValue) -> {
            // Solo números
            if (!newValue.matches("\\d*")) {
                tfTelefono.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            // Máximo 10 dígitos
            if (newValue.length() > 10) {
                tfTelefono.setText(newValue.substring(0, 10));
            }
        });
    }
    private void limitarNumeroExterior() {
    tfNumero.textProperty().addListener((obs, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            tfNumero.setText(newValue.replaceAll("[^\\d]", ""));
            return;
        }
        if (newValue.length() > 10) {
            tfNumero.setText(newValue.substring(0, 10));
        }
    });
}



    
    private void procesarRespuestaCP(HashMap<String, Object> respuesta, String cp) {
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            RSDatosCodigoPostal datos = (RSDatosCodigoPostal) respuesta.get(Constantes.KEY_OBJETO);

            cbPais.setDisable(false);
            cbEstado.setDisable(false);
            cbMunicipio.setDisable(false);

            // País
            int posPais = obtenerPosicionPais(datos.getIdPais());
            cbPais.getSelectionModel().select(posPais);

            // Estado
            cargarEstado();
            int posEstado = obtenerPosicionEstado(datos.getIdEstado());
            cbEstado.getSelectionModel().select(posEstado);

            // Municipio
            cargarMunicipio();
            int posMunicipio = obtenerPosicionMunicipio(datos.getIdMunicipio());
            cbMunicipio.getSelectionModel().select(posMunicipio);

            // Colonias
            HashMap<String, Object> respColonias = (HashMap<String, Object>) respuesta.get("colonias_extra");
            if (respColonias != null && !(boolean) respColonias.get(Constantes.KEY_ERROR)) {
                List<Colonia> listaCols = (List<Colonia>) respColonias.get(Constantes.KEY_LISTA);
                colonias = FXCollections.observableArrayList(listaCols);
                cbColonia.setItems(colonias);
                cbColonia.setDisable(false);
                if (colonias.size() == 1) cbColonia.getSelectionModel().select(0);
            }

        } else {
            Utilidades.mostrarAlertaSimple("Sin resultados", "No se encontró información para el CP: " + cp, Alert.AlertType.INFORMATION);
            limpiarCombosDireccion();
        }
    }

    private void cargarPais() {
        HashMap<String, Object> respuesta = CatalogoImp.obtenerPais();
        paises = FXCollections.observableArrayList((List<Pais>) respuesta.get(Constantes.KEY_LISTA));
        cbPais.setItems(paises);
    }

    private void cargarEstado() {
        estados = FXCollections.observableArrayList(
                (List<Estado>) CatalogoImp.obtenerEstado().get(Constantes.KEY_LISTA)
        );
        cbEstado.setItems(estados);
    }

    private void cargarMunicipio() {
        municipios = FXCollections.observableArrayList(
                (List<Municipio>) CatalogoImp.obtenerMunicipio().get(Constantes.KEY_LISTA)
        );
        cbMunicipio.setItems(municipios);
    }

    private int obtenerPosicionPais(int id) {
        for (int i = 0; i < paises.size(); i++)
            if (paises.get(i).getIdPais() == id) return i;
        return -1;
    }

    private int obtenerPosicionEstado(int id) {
        for (int i = 0; i < estados.size(); i++)
            if (estados.get(i).getIdEstado() == id) return i;
        return -1;
    }

    private int obtenerPosicionMunicipio(int id) {
        for (int i = 0; i < municipios.size(); i++)
            if (municipios.get(i).getIdMunicipio() == id) return i;
        return -1;
    }
    
    private void limpiarCombosDireccion() {
        cbPais.getSelectionModel().clearSelection();
        cbEstado.getItems().clear();
        cbMunicipio.getItems().clear();
        cbColonia.getItems().clear();
        cbColonia.setDisable(true);
    }
}

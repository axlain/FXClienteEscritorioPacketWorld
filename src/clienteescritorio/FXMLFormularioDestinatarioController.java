package clienteescritorio;

import clienteescritorio.dominio.CatalogoImp;
import clienteescritorio.dominio.DestinatarioImp;
import clienteescritorio.dto.RSDatosCodigoPostal;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colonia;
import clienteescritorio.pojo.Destinatario;
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


public class FXMLFormularioDestinatarioController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
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
    
    private INotificador observador;

    private ObservableList<Pais> paises;
    private ObservableList<Estado> estados;
    private ObservableList<Municipio> municipios;
    private ObservableList<Colonia> colonias;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPais();
        cbPais.setDisable(true);
        cbEstado.setDisable(true);
        cbMunicipio.setDisable(true);
        cbColonia.setDisable(true);
    }    
    public void inicializarDatos(INotificador observador) {
        this.observador = observador;
        lblTitulo.setText("Registrar Destinatario");
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

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!sonCamposValidos()) return;

        Destinatario d = new Destinatario();
        d.setNombre(tfNombre.getText().trim());
        d.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        d.setApellidoMaterno(tfApellidoMaterno.getText().trim());
        d.setCalle(tfCalle.getText().trim());
        d.setNumero(tfNumero.getText().trim());

        d.setIdPais(cbPais.getSelectionModel().getSelectedItem().getIdPais());
        d.setIdEstado(cbEstado.getSelectionModel().getSelectedItem().getIdEstado());
        d.setIdMunicipio(cbMunicipio.getSelectionModel().getSelectedItem().getIdMunicipio());
        d.setIdColonia(cbColonia.getSelectionModel().getSelectedItem().getIdColonia());

        Respuesta resp = DestinatarioImp.registrar(d);

        if (!resp.isError()) {
            Utilidades.mostrarAlertaSimple("Destinatario registrado", resp.getMensaje(), Alert.AlertType.INFORMATION);

            if (observador != null) {
                observador.notificarOperacionExitosa("registro", "destinatario");
            }

            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
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

    private boolean sonCamposValidos() {
        String msg = "";

        if (tfNombre.getText().trim().isEmpty()) msg += "- Nombre es obligatorio.\n";
        if (tfApellidoPaterno.getText().trim().isEmpty()) msg += "- Apellido paterno es obligatorio.\n";
        if (tfApellidoMaterno.getText().trim().isEmpty()) msg += "- Apellido materno es obligatorio.\n";
        if (tfCalle.getText().trim().isEmpty()) msg += "- Calle es obligatoria.\n";
        if (tfNumero.getText().trim().isEmpty()) msg += "- Número es obligatorio.\n";

        String cp = tfCodigoPostal.getText().trim();
        if (!cp.matches("\\d{5}")) msg += "- Código Postal debe tener 5 dígitos.\n";

        if (cbPais.getSelectionModel().getSelectedItem() == null) msg += "- Debes seleccionar país.\n";
        if (cbEstado.getSelectionModel().getSelectedItem() == null) msg += "- Debes seleccionar estado.\n";
        if (cbMunicipio.getSelectionModel().getSelectedItem() == null) msg += "- Debes seleccionar municipio.\n";
        if (cbColonia.getSelectionModel().getSelectedItem() == null) msg += "- Debes seleccionar colonia.\n";

        if (!msg.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", msg, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
    private void cargarPais() {
        HashMap<String, Object> respuesta = CatalogoImp.obtenerPais();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Pais> lista = (List<Pais>) respuesta.get(Constantes.KEY_LISTA);
            paises = FXCollections.observableArrayList(lista);
            cbPais.setItems(paises);
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
        }
    }

    private void cargarEstado() {
        HashMap<String, Object> respuesta = CatalogoImp.obtenerEstado();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Estado> lista = (List<Estado>) respuesta.get(Constantes.KEY_LISTA);
            estados = FXCollections.observableArrayList(lista);
            cbEstado.setItems(estados);
        }
    }

    private void cargarMunicipio() {
        HashMap<String, Object> respuesta = CatalogoImp.obtenerMunicipio();
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Municipio> lista = (List<Municipio>) respuesta.get(Constantes.KEY_LISTA);
            municipios = FXCollections.observableArrayList(lista);
            cbMunicipio.setItems(municipios);
        }
    }

    private int obtenerPosicionPais(int idPais) {
        for (int i = 0; i < paises.size(); i++) if (paises.get(i).getIdPais() == idPais) return i;
        return -1;
    }
    private int obtenerPosicionEstado(int idEstado) {
        for (int i = 0; i < estados.size(); i++) if (estados.get(i).getIdEstado() == idEstado) return i;
        return -1;
    }
    private int obtenerPosicionMunicipio(int idMunicipio) {
        for (int i = 0; i < municipios.size(); i++) if (municipios.get(i).getIdMunicipio() == idMunicipio) return i;
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

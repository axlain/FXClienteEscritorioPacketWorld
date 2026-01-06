package clienteescritorio;

import clienteescritorio.dominio.EnvioImp;
import clienteescritorio.dominio.PaqueteImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Envio;
import clienteescritorio.pojo.Paquete;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioPaqueteController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private ComboBox<Envio> cbEnvio;
    @FXML
    private TextField tfDescripcion;
    @FXML
    private TextField tfPeso;
    @FXML
    private TextField tfAlto;
    @FXML
    private TextField tfAncho;
    @FXML
    private TextField tfProfundidad;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private ObservableList<Envio> envios;

    private Paquete paqueteEdicion;
    private INotificador observador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboEnvio();
        cargarEnvios();
        limitarCamposNumericos();
    }

    public void inicializarDatos(Paquete paquete, INotificador observador) {
        this.paqueteEdicion = paquete;
        this.observador = observador;

        if (paquete != null) {
            lblTitulo.setText("Editar Paquete");

            tfDescripcion.setText(paquete.getDescripcion());
            tfPeso.setText(String.valueOf(paquete.getPeso()));
            tfAlto.setText(String.valueOf(paquete.getAlto()));
            tfAncho.setText(String.valueOf(paquete.getAncho()));
            tfProfundidad.setText(String.valueOf(paquete.getProfundidad()));

            // Seleccionar el envío correspondiente (si ya viene ligado)
            if (envios != null && paquete.getIdEnvio() != null) {
                for (Envio e : envios) {
                    if (e.getIdEnvio() == paquete.getIdEnvio()) {
                        cbEnvio.getSelectionModel().select(e);
                        break;
                    }
                }
            }
        } else {
            lblTitulo.setText("Registrar Paquete");
        }
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!sonCamposValidos()) {
            return;
        }

        Paquete paquete = (paqueteEdicion != null) ? paqueteEdicion : new Paquete();

        Envio envioSeleccionado = cbEnvio.getSelectionModel().getSelectedItem();
        paquete.setIdEnvio(envioSeleccionado.getIdEnvio());

        paquete.setDescripcion(tfDescripcion.getText().trim());
        paquete.setPeso(Double.parseDouble(tfPeso.getText().trim()));
        paquete.setAlto(Double.parseDouble(tfAlto.getText().trim()));
        paquete.setAncho(Double.parseDouble(tfAncho.getText().trim()));
        paquete.setProfundidad(Double.parseDouble(tfProfundidad.getText().trim()));

        if (paqueteEdicion != null) {
            // si tu Paquete requiere idPaquete, ya debe venir en paqueteEdicion
            editarPaquete(paquete);
        } else {
            registrarPaquete(paquete);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) tfDescripcion.getScene().getWindow()).close();
    }

    private boolean sonCamposValidos() {
        String mensaje = "";

        if (cbEnvio.getValue() == null) mensaje += "- Debes seleccionar un envío.\n";
        if (tfDescripcion.getText().trim().isEmpty()) mensaje += "- Descripción obligatoria.\n";

        if (!esNumeroValido(tfPeso.getText())) mensaje += "- Peso inválido.\n";
        if (!esNumeroValido(tfAlto.getText())) mensaje += "- Alto inválido.\n";
        if (!esNumeroValido(tfAncho.getText())) mensaje += "- Ancho inválido.\n";
        if (!esNumeroValido(tfProfundidad.getText())) mensaje += "- Profundidad inválida.\n";

        if (!mensaje.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", mensaje, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean esNumeroValido(String texto) {
        if (texto == null) return false;
        String t = texto.trim();
        if (t.isEmpty()) return false;

        try {
            double valor = Double.parseDouble(t);
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void registrarPaquete(Paquete paquete) {
        Respuesta respuesta = PaqueteImp.registrar(paquete);

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Paquete registrado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            if (observador != null) {
                observador.notificarOperacionExitosa("registro", paquete.getDescripcion());
            }
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    private void editarPaquete(Paquete paquete) {
        Respuesta respuesta = PaqueteImp.actualizar(paquete);

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Paquete actualizado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            if (observador != null) {
                observador.notificarOperacionExitosa("edición", paquete.getDescripcion());
            }
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    private void configurarComboEnvio() {
        cbEnvio.setCellFactory(lv -> new ListCell<Envio>() {
            @Override
            protected void updateItem(Envio item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNumeroGuia());
            }
        });

        cbEnvio.setButtonCell(new ListCell<Envio>() {
            @Override
            protected void updateItem(Envio item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNumeroGuia());
            }
        });
    }

   private void cargarEnvios() {
        HashMap<String, Object> respuesta =
                EnvioImp.obtenerRecibidosSucursal();

        boolean esError = (boolean) respuesta.get(Constantes.KEY_ERROR);

        if (!esError) {
            List<Envio> lista =
                    (List<Envio>) respuesta.get(Constantes.KEY_LISTA);

            envios = FXCollections.observableArrayList(lista);
            cbEnvio.setItems(envios);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    respuesta.get(Constantes.KEY_MENSAJE).toString(),
                    Alert.AlertType.ERROR
            );
        }
    }


    private void limitarCamposNumericos() {
        // permite números y punto decimal
        limitarDecimal(tfPeso);
        limitarDecimal(tfAlto);
        limitarDecimal(tfAncho);
        limitarDecimal(tfProfundidad);
    }

    private void limitarDecimal(TextField tf) {
        tf.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;

            // solo dígitos y un punto
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tf.setText(oldValue);
                return;
            }
        });
    }
}

package clienteescritorio;
import clienteescritorio.dominio.EnvioImp;
import clienteescritorio.dominio.PaqueteImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.pojo.Envio;
import clienteescritorio.pojo.Paquete;
import clienteescritorio.utilidad.Utilidades;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLAsignarEnvioPaqueteController implements Initializable {

    @FXML
    private Label lblPaquete;
    @FXML
    private TextField tfNumeroGuia;
    @FXML
    private Label lblEnvioEncontrado;

    private Paquete paquete;
    private Integer idEnvioSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarValores(Paquete paquete) {
        this.paquete = paquete;
        lblPaquete.setText(paquete.getDescripcion());
        
        if (paquete.getIdEnvio() != null && paquete.getIdEnvio() > 0) {
            lblEnvioEncontrado.setText("Envío actual: " + (paquete.getNumeroGuia() != null ? paquete.getNumeroGuia() : "ID " + paquete.getIdEnvio()));
        }
    }

    @FXML
    private void clickBuscarEnvio(ActionEvent event) {
        String numeroGuia = tfNumeroGuia.getText();

        if (numeroGuia == null || numeroGuia.trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campo requerido",
                    "Debe ingresar un número de guía.",
                    Alert.AlertType.WARNING);
            return;
        }

        HashMap<String, Object> respuesta = EnvioImp.buscarPorGuia(numeroGuia.trim());
        boolean error = (boolean) respuesta.get("error");

        if (!error) {
            Envio envio = (Envio) respuesta.get("envio");

            if (envio != null) {
                idEnvioSeleccionado = envio.getIdEnvio();
                lblEnvioEncontrado.setText("✓ Envío encontrado: " + envio.getNumeroGuia());
            } else {
                Utilidades.mostrarAlertaSimple("No encontrado",
                        "No existe un envío con ese número de guía.",
                        Alert.AlertType.WARNING);
                idEnvioSeleccionado = null;
                lblEnvioEncontrado.setText("");
            }
        } else {
            String mensaje = (String) respuesta.get("mensaje");
            Utilidades.mostrarAlertaSimple("Error", mensaje, Alert.AlertType.ERROR);
            idEnvioSeleccionado = null;
            lblEnvioEncontrado.setText("");
        }
    }

    @FXML
    private void clickAsignar(ActionEvent event) {
        if (idEnvioSeleccionado == null) {
            Utilidades.mostrarAlertaSimple("Buscar envío",
                    "Primero debe buscar y seleccionar un envío.",
                    Alert.AlertType.WARNING);
            return;
        }

        paquete.setIdEnvio(idEnvioSeleccionado);

        Respuesta respuesta = PaqueteImp.actualizar(paquete);

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Envío asignado",
                    respuesta.getMensaje(),
                    Alert.AlertType.INFORMATION);
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error",
                    respuesta.getMensaje(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblPaquete.getScene().getWindow();
        stage.close();
    }
}

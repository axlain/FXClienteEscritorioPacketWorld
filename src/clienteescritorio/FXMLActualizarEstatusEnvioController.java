package clienteescritorio;

import clienteescritorio.dominio.CatalogoImp;
import clienteescritorio.dominio.EnvioImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.EstatusEnvio;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import clienteescritorio.pojo.Envio;


public class FXMLActualizarEstatusEnvioController implements Initializable {

    @FXML
    private Label lblGuia;
    @FXML
    private ComboBox<EstatusEnvio> cbEstatus;
    @FXML
    private TextArea taComentario;

    private Envio envioSeleccionado;
    private Integer idColaboradorSesion;
    private INotificador observador;

    private ObservableList<EstatusEnvio> estatus;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Se carga en inicializarDatos() para tener ya el envío
    }

    public void inicializarDatos(Envio envio, Integer idColaboradorSesion, INotificador observador) {
        this.envioSeleccionado = envio;
        this.idColaboradorSesion = idColaboradorSesion;
        this.observador = observador;

        lblGuia.setText(envio.getNumeroGuia());

        cargarEstatus();

        // Seleccionar el estatus actual como default si existe en el combo
        if (envio.getIdEstatusActual() != null && estatus != null) {
            for (EstatusEnvio e : estatus) {
                if (e.getIdEstatus() != null && e.getIdEstatus().equals(envio.getIdEstatusActual())) {
                    cbEstatus.getSelectionModel().select(e);
                    break;
                }
            }
        }
    }

    private void cargarEstatus() {
        HashMap<String, Object> respuesta = CatalogoImp.obtenerCatalogoEstatus();
        boolean esError = (boolean) respuesta.get(Constantes.KEY_ERROR);

        if (!esError) {
            List<EstatusEnvio> lista = (List<EstatusEnvio>) respuesta.get(Constantes.KEY_LISTA);
            if (lista == null) {
                lista = java.util.Collections.emptyList();
            }
            estatus = FXCollections.observableArrayList(lista);
            cbEstatus.setItems(estatus);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    respuesta.get(Constantes.KEY_MENSAJE).toString(),
                    Alert.AlertType.ERROR
            );
        }
    }


    @FXML
    private void clickGuardar(ActionEvent event) {
        if (envioSeleccionado == null) {
            Utilidades.mostrarAlertaSimple("Error", "No hay envío seleccionado.", Alert.AlertType.ERROR);
            return;
        }
        if (idColaboradorSesion == null) {
            Utilidades.mostrarAlertaSimple("Error", "No hay sesión activa.", Alert.AlertType.ERROR);
            return;
        }

        EstatusEnvio estatusSel = cbEstatus.getSelectionModel().getSelectedItem();
        if (estatusSel == null) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", "Selecciona un estatus.", Alert.AlertType.WARNING);
            return;
        }

        Integer idNuevoEstatusObj = estatusSel.getIdEstatus();
        if (idNuevoEstatusObj == null) {
            Utilidades.mostrarAlertaSimple(
                    "Campos inválidos",
                    "El estatus seleccionado no tiene ID.",
                    Alert.AlertType.WARNING
            );
            return;
        }
        int idNuevoEstatus = idNuevoEstatusObj;

        String comentario = taComentario.getText() != null ? taComentario.getText().trim() : "";

        // Regla backend: comentario obligatorio si estatus = 4 o 6
        if ((idNuevoEstatus == 4 || idNuevoEstatus == 6) && comentario.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Comentario requerido",
                    "El comentario es obligatorio cuando el estatus es 'Detenido' o 'Cancelado'.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Envio req = new Envio();
        req.setNumeroGuia(envioSeleccionado.getNumeroGuia());
        req.setIdEstatusActual(idNuevoEstatus);
        req.setIdCreadoPor(idColaboradorSesion);
        req.setComentario(comentario);

        Respuesta resp = EnvioImp.actualizarEstatus(req);

        if (!resp.isError()) {
            Utilidades.mostrarAlertaSimple("Éxito", resp.getMensaje(), Alert.AlertType.INFORMATION);
            if (observador != null) {
                observador.notificarOperacionExitosa("estatus", envioSeleccionado.getNumeroGuia());
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
        ((Stage) lblGuia.getScene().getWindow()).close();
    }
}

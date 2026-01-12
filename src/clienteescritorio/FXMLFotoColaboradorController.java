package clienteescritorio;

import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.utilidad.Utilidades;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLFotoColaboradorController {

    @FXML
    private ImageView ivFoto;
    @FXML
    private Button btnGuardar;

    private int idColaborador;
    private INotificador observador;

    private byte[] fotoBytesSeleccionada;

    public void inicializar(int idColaborador, INotificador observador) {
        this.idColaborador = idColaborador;
        this.observador = observador;

        // Cargar foto actual si existe
        cargarFotoActual();
        btnGuardar.setDisable(true);
    }

   private void cargarFotoActual() {
    Colaborador col = ColaboradorImp.obtenerFoto(idColaborador);

    if (col != null && col.getFotoBase64() != null && !col.getFotoBase64().trim().isEmpty()) {
        try {
            String base64 = col.getFotoBase64().trim();

            if (base64.contains(",")) { // por si viene data:image/...;base64,
                base64 = base64.split(",")[1];
            }

            byte[] bytes = Base64.getDecoder().decode(base64);
            ivFoto.setImage(new Image(new ByteArrayInputStream(bytes)));
        } catch (Exception ignored) {
        }
    }
}


    @FXML
    private void clickSeleccionar(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar fotografía");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) ivFoto.getScene().getWindow();
        File archivo = fc.showOpenDialog(stage);

        if (archivo != null) {
            try {
                fotoBytesSeleccionada = Files.readAllBytes(archivo.toPath());
                ivFoto.setImage(new Image(new ByteArrayInputStream(fotoBytesSeleccionada)));
                btnGuardar.setDisable(false);
            } catch (Exception e) {
                Utilidades.mostrarAlertaSimple("Error", "No se pudo leer la imagen.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (fotoBytesSeleccionada == null) {
            Utilidades.mostrarAlertaSimple("Falta imagen", "Primero selecciona una imagen.", Alert.AlertType.WARNING);
            return;
        }

        Respuesta r = ColaboradorImp.subirFoto(idColaborador, fotoBytesSeleccionada);

        if (!r.isError()) {
            Utilidades.mostrarAlertaSimple("Foto subida", r.getMensaje(), Alert.AlertType.INFORMATION);

            // Notificar a la pantalla padre (estilo Ramón)
            if (observador != null) {
                observador.notificarFotoActualizada(idColaborador);
            }

            cerrar();
        } else {
            Utilidades.mostrarAlertaSimple("Error", r.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        ((Stage) ivFoto.getScene().getWindow()).close();
    }
}

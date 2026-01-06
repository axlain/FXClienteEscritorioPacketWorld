package clienteescritorio;

import clienteescritorio.dominio.PaqueteImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Paquete;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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

public class FXMLAdministracionPaqueteController implements Initializable, INotificador  {

    @FXML
    private TextField tfBuscarGuia;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private TableView<Paquete> tvPaquetes;
    @FXML
    private TableColumn tcNumeroGuia;
    @FXML
    private TableColumn tcDescripcion;
    @FXML
    private TableColumn tcPeso;
    @FXML
    private TableColumn tcAlto;
    @FXML
    private TableColumn tcAncho;
    @FXML
    private TableColumn tcProfundidad;
    @FXML
    private Button btnRegistrarPaquete;
    @FXML
    private Button btnEditarPaquete;
    @FXML
    private Button btnEliminarPaquete;

    private ObservableList<Paquete> paquetes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionPaquetes();
    }

    private void configurarTabla() {
        tcNumeroGuia.setCellValueFactory(new PropertyValueFactory("numeroGuia"));
        tcDescripcion.setCellValueFactory(new PropertyValueFactory("descripcion"));
        tcPeso.setCellValueFactory(new PropertyValueFactory("peso"));
        tcAlto.setCellValueFactory(new PropertyValueFactory("alto"));
        tcAncho.setCellValueFactory(new PropertyValueFactory("ancho"));
        tcProfundidad.setCellValueFactory(new PropertyValueFactory("profundidad"));
    }

    private void cargarInformacionPaquetes() {
        HashMap<String, Object> respuesta = PaqueteImp.obtenerTodos();
        boolean esError = (boolean) respuesta.get(Constantes.KEY_ERROR);

        if (!esError) {
            List<Paquete> listaApi = (List<Paquete>) respuesta.get(Constantes.KEY_LISTA);
            paquetes = FXCollections.observableArrayList();
            paquetes.addAll(listaApi);
            tvPaquetes.setItems(paquetes);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error al cargar paquetes",
                    respuesta.get(Constantes.KEY_MENSAJE).toString(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String guia = tfBuscarGuia.getText().trim();

        if (guia.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Campo vacío",
                    "Ingresa el número de guía para buscar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (paquetes == null || paquetes.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Sin datos",
                    "No hay paquetes cargados para realizar la búsqueda.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // No hay endpoint buscarPorGuia en PaqueteImp, así que filtramos localmente
        List<Paquete> filtrados = paquetes.stream()
                .filter(p -> p.getNumeroGuia() != null
                        && p.getNumeroGuia().toLowerCase().contains(guia.toLowerCase()))
                .collect(Collectors.toList());

        if (filtrados.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Sin resultados",
                    "No se encontraron paquetes con esa guía.",
                    Alert.AlertType.INFORMATION
            );
            tvPaquetes.setItems(FXCollections.observableArrayList());
        } else {
            tvPaquetes.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscarGuia.clear();
        if (paquetes != null) {
            tvPaquetes.setItems(paquetes);
        }
    }

    @FXML
    private void clickRegistrarPaquete(ActionEvent event) {
        irFormularioPaquete(null);
    }

    @FXML
    private void clickEditarPaquete(ActionEvent event) {
        Paquete paquete = tvPaquetes.getSelectionModel().getSelectedItem();

        if (paquete != null) {
            irFormularioPaquete(paquete);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un paquete",
                    "Para editar un paquete, primero debes seleccionarlo en la tabla.",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clickEliminarPaquete(ActionEvent event) {
        Paquete paquete = tvPaquetes.getSelectionModel().getSelectedItem();

        if (paquete == null) {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un paquete",
                    "Para eliminar un paquete, primero debes seleccionarlo en la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        int idPaquete = paquete.getIdPaquete();

        Respuesta resp = PaqueteImp.eliminar(idPaquete);

        if (!resp.isError()) {
            Utilidades.mostrarAlertaSimple(
                    "Paquete eliminado",
                    resp.getMensaje() != null ? resp.getMensaje() : "El paquete se eliminó correctamente.",
                    Alert.AlertType.INFORMATION
            );
            cargarInformacionPaquetes();
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error al eliminar",
                    resp.getMensaje(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void irFormularioPaquete(Paquete paquete) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFormularioPaquete.fxml"));
            Parent vista = cargador.load();

            FXMLFormularioPaqueteController controlador = cargador.getController();
            controlador.inicializarDatos(paquete, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Registro / Edición de Paquete");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

            cargarInformacionPaquetes();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionPaquetes();
    }
}

package clienteescritorio;

import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.pojo.Unidad;
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
import javafx.stage.Stage;

public class FXMLAsignarConductorController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblUnidad;
    @FXML
    private ComboBox<Colaborador> cbConductores;
    @FXML
    private Button btnAsignar;
    @FXML
    private Button btnCancelar;

    private ObservableList<Colaborador> colaboradoresConductores;
    private INotificador observador;

    private Unidad unidad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // IMPORTANT: No cargar aquí porque unidad todavía no existe
        // cargarColaboradoresConductores();
    }

    public void iniciarlizarDatos(Unidad unidad, INotificador observador){
        this.observador = observador;
        this.unidad = unidad;

        if (unidad != null) {
            lblUnidad.setText(
                unidad.getVin() + " - " + unidad.getModelo() + " - " + unidad.getAnio()
            );

            // ✅ Cargar solo conductores de la sucursal de la unidad
            cargarColaboradoresConductoresPorSucursal();

            // Seleccionar conductor si ya tiene uno asignado
            Integer idConductor = unidad.getIdConductor();
            if (idConductor != null && idConductor > 0) {
                int posicionConductor = obtenerPosicionColaboradorConductor(idConductor);
                if (posicionConductor != -1) {
                    cbConductores.getSelectionModel().select(posicionConductor);
                }
            }
        } else {
            Utilidades.mostrarAlertaSimple(
                "Error",
                "No se recibió la unidad a asignar.",
                Alert.AlertType.ERROR
            );
            cerrarVentana();
        }
    }

    @FXML
    private void clickAsignar(ActionEvent event) {
        asignarConductor();
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana(){
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }

    private void asignarConductor() {
        Colaborador seleccionado = cbConductores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple(
                "Sin selección",
                "Debes seleccionar un conductor.",
                Alert.AlertType.WARNING
            );
            return;
        }

        Respuesta respuesta = ColaboradorImp.asignarConductor(
                unidad.getIdUnidad(),
                seleccionado.getIdColaborador()
        );

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple(
                "Conductor asignado",
                respuesta.getMensaje(),
                Alert.AlertType.INFORMATION
            );

            if (observador != null) {
                observador.notificarOperacionExitosa("asignación", unidad.getVin());
            }
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple(
                "Error",
                respuesta.getMensaje(),
                Alert.AlertType.ERROR
            );
        }
    }

    // ✅ Nuevo: carga filtrada por sucursal
    private void cargarColaboradoresConductoresPorSucursal(){
        Integer idSucursal = unidad.getIdSucursal(); // <- asegúrate que tu POJO Unidad tenga este getter

        if (idSucursal == null || idSucursal <= 0) {
            Utilidades.mostrarAlertaSimple(
                "Sucursal no válida",
                "No se pudo identificar la sucursal de la unidad.",
                Alert.AlertType.ERROR
            );
            cerrarVentana();
            return;
        }

        HashMap<String,Object> respuesta = ColaboradorImp.obtenerConductoresPorSucursal(idSucursal);

        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            List<Colaborador> lista = (List<Colaborador>) respuesta.get(Constantes.KEY_LISTA);

            colaboradoresConductores = FXCollections.observableArrayList();
            colaboradoresConductores.addAll(lista);
            cbConductores.setItems(colaboradoresConductores);

        } else {
            Utilidades.mostrarAlertaSimple(
                "Error",
                respuesta.get(Constantes.KEY_MENSAJE).toString(),
                Alert.AlertType.ERROR
            );
            cerrarVentana();
        }
    }

    private int obtenerPosicionColaboradorConductor(Integer idColaborador){
        if (idColaborador == null || colaboradoresConductores == null) return -1;

        for(int i = 0; i < colaboradoresConductores.size(); i++){
            if (colaboradoresConductores.get(i).getIdColaborador() == idColaborador){
                return i;
            }
        }
        return -1;
    }
}

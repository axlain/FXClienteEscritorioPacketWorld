
package clienteescritorio;

import clienteescritorio.dominio.EnvioImp;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.pojo.Envio;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class FXMLAdministracionEnvioController implements Initializable, INotificador {

    @FXML
    private TextField tfBuscarGuia;
    @FXML
    private Button btnBuscarGuia;
    @FXML
    private Button btnLimpiarBusqueda;
    @FXML
    private TableView<Envio> tvEnvios;
    @FXML
    private TableColumn tcNumeroGuia;
    @FXML
    private TableColumn tcCliente;
    @FXML
    private TableColumn tcDestinatario;
    @FXML
    private TableColumn tcSucursal;
    @FXML
    private TableColumn tcEstatusActual;
    @FXML
    private TableColumn tcConductor;
    @FXML
    private TableColumn tcCostoTotal;
    @FXML
    private Button btnRegistrarEnvio;
    @FXML
    private Button btnEditarEnvio;
    @FXML
    private Button btnActualizarEstatus;

    private ObservableList<Envio> envios;
    private Colaborador colaboradorSesion;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionEnvios();
    }    
    public void inicializarSesion(Colaborador colaborador) {
        this.colaboradorSesion = colaborador;
    }
    private void configurarTabla() {
        tcNumeroGuia.setCellValueFactory(new PropertyValueFactory("numeroGuia"));

        tcCliente.setCellValueFactory(new PropertyValueFactory("clienteCompleto"));
        tcDestinatario.setCellValueFactory(new PropertyValueFactory("destinatarioCompleto"));

        tcSucursal.setCellValueFactory(new PropertyValueFactory("codigoSucursal"));
        tcEstatusActual.setCellValueFactory(new PropertyValueFactory("estatusActual"));

        tcConductor.setCellValueFactory(new PropertyValueFactory("conductorTexto"));

        tcCostoTotal.setCellValueFactory(new PropertyValueFactory("costoTotal"));
    }

    private void cargarInformacionEnvios() {
       HashMap<String, Object> respuesta = EnvioImp.obtenerTodos();
       boolean esError = (boolean) respuesta.get("error");
       if (!esError) {
           List<Envio> listaApi = (List<Envio>) respuesta.get(Constantes.KEY_LISTA);
           envios = FXCollections.observableArrayList();
           envios.addAll(listaApi);
           tvEnvios.setItems(envios);
       } else {
           Utilidades.mostrarAlertaSimple(
                   "Error al cargar envíos",
                   respuesta.get("mensaje").toString(),
                   Alert.AlertType.ERROR
           );
       }
   }
    @FXML
    private void clickBuscarGuia(ActionEvent event) {
        String guia = tfBuscarGuia.getText().trim();

        if (guia.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Campo vacío",
                    "Ingresa el número de guía para buscar.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        HashMap<String, Object> respuesta = EnvioImp.buscarPorGuia(guia);
        boolean esError = (boolean) respuesta.get(Constantes.KEY_ERROR);

        if (!esError) {
            Envio envio = (Envio) respuesta.get(Constantes.KEY_OBJETO);

            if (envio != null) {
                tvEnvios.setItems(FXCollections.observableArrayList(envio));
            } else {
                Utilidades.mostrarAlertaSimple(
                        "Sin resultados",
                        "No se encontró un envío con esa guía.",
                        Alert.AlertType.INFORMATION
                );
                tvEnvios.setItems(FXCollections.observableArrayList());
            }

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
        tfBuscarGuia.clear();
        if (envios != null) {
            tvEnvios.setItems(envios);
        }
    }

    @FXML
    private void clickRegistrarEnvio(ActionEvent event) {
        irFormularioEnvio(null);
    }

    @FXML
    private void clickEditarEnvio(ActionEvent event) {
        Envio envio = tvEnvios.getSelectionModel().getSelectedItem();

        if (envio != null) {
            irFormularioEnvio(envio);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un envío",
                    "Para editar un envío, primero debes seleccionarlo en la tabla.",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    private void clickActualizarEstatus(ActionEvent event) {
        Envio envio = tvEnvios.getSelectionModel().getSelectedItem();

        if (envio == null) {
            Utilidades.mostrarAlertaSimple(
                    "Selecciona un envío",
                    "Para actualizar el estatus, primero debes seleccionar un envío en la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (colaboradorSesion == null || colaboradorSesion.getIdColaborador() <= 0) {
            Utilidades.mostrarAlertaSimple(
                    "Sesión no válida",
                    "No se encontró el colaborador en sesión (idCreadoPor).",
                    Alert.AlertType.ERROR
            );
            return;
        }
        irFormularioActualizarEstatus(envio);
    }

    
    private void irFormularioEnvio(Envio envio) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLFormularioEnvio.fxml"));
            Parent vista = cargador.load();

            FXMLFormularioEnvioController controlador = cargador.getController();

            Integer idCreadoPor = (colaboradorSesion != null) ? colaboradorSesion.getIdColaborador() : null;
            Integer idSucursalColab = (colaboradorSesion != null) ? colaboradorSesion.getIdSucursal() : null; 

            controlador.iniciarlizarDatos(envio, this, idCreadoPor, idSucursalColab);


            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Registro / Edición de Envío");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void irFormularioActualizarEstatus(Envio envio) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLActualizarEstatusEnvio.fxml"));
            Parent vista = cargador.load();

            FXMLActualizarEstatusEnvioController controlador = cargador.getController();
            Integer idColaboradorSesion = colaboradorSesion.getIdColaborador();

            controlador.inicializarDatos(envio, idColaboradorSesion, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Actualizar Estatus de Envío");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    "No se pudo abrir la ventana para actualizar estatus.",
                    Alert.AlertType.ERROR
            );
        }
    }


    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
       cargarInformacionEnvios();
    }
}

package clienteescritorio;

import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.utilidad.Utilidades;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import clienteescritorio.interfaz.INotificador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable, INotificador {


    @FXML private Label rol;
    @FXML private Label name;
    @FXML private Label numeroPersonal;

    private Colaborador colaboradorSesion;

    @FXML private VBox sideMenu;
    @FXML private Button btnColaboradores;
    @FXML private Button btnUnidades;
    @FXML private Button btnSucursales;
    @FXML private Button btnClientes;
    @FXML private Button btnEnvios;
    @FXML private Button btnPaquetes;
    @FXML private Button cerrarSesionBtn;
    @FXML private Label bienvenidaTxt;
    @FXML private BorderPane borderPaneContenedor;

    @FXML private Label titleApp;

    @FXML private ImageView ivFotoHeader;
    @FXML private StackPane spAvatar;
    @FXML private Circle cAvatarBg;
    @FXML private Circle cAvatarBorde;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cAvatarBg.setStyle("-fx-fill: rgba(255,255,255,0.18);");
        cAvatarBorde.setStyle("-fx-fill: transparent; -fx-stroke: rgba(255,255,255,0.55); -fx-stroke-width: 2;");

        Circle clip = new Circle(22, 22, 22);
        ivFotoHeader.setClip(clip);
    }

    public void cargarInformacion(Colaborador colaborador) {
        colaboradorSesion = colaborador;

        String nombreCompleto = colaborador.getNombre() + " " +
                                colaborador.getApellidoPaterno() + " " +
                                colaborador.getApellidoMaterno();

        name.setText(nombreCompleto);
        rol.setText("Rol: " + colaborador.getRol());
        numeroPersonal.setText(colaborador.getNumeroPersonal());

        titleApp.setText(nombreCompleto);

        aplicarPermisos(colaborador.getRol());

        cargarFotoHeader(colaborador.getIdColaborador());
    }

    private void cargarFotoHeader(int idColaborador) {
        try {
            Colaborador colFoto = ColaboradorImp.obtenerFoto(idColaborador);

            if (colFoto != null && colFoto.getFotoBase64() != null && !colFoto.getFotoBase64().trim().isEmpty()) {
                String base64 = colFoto.getFotoBase64().trim();

                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }

                byte[] bytes = Base64.getDecoder().decode(base64);
                Image img = new Image(new ByteArrayInputStream(bytes));

                if (img.isError()) {
                    ponerAvatarDefault();
                } else {
                    ivFotoHeader.setImage(img);
                }
            } else {
                ponerAvatarDefault();
            }
        } catch (IllegalArgumentException ex) {
            ponerAvatarDefault();
        } catch (Exception e) {
            ponerAvatarDefault();
        }
    }

    private void ponerAvatarDefault() {
        try {
            Image def = new Image(getClass().getResourceAsStream("/clienteescritorio/imagen/avatar_default.png"));
            ivFotoHeader.setImage(def);
        } catch (Exception e) {
            ivFotoHeader.setImage(null);
        }
    }

    @FXML
    private void clickCerrarSesion(ActionEvent event) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("FXMLinicioSesion.fxml"));
            Scene login = new Scene(vista);

            Stage stPrincipal = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stPrincipal.setScene(login);
            stPrincipal.setTitle("Login");
            stPrincipal.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void aplicarPermisos(String rol) {
        switch (rol) {
            case "Administrador":
                btnColaboradores.setDisable(false);
                btnUnidades.setDisable(false);
                btnSucursales.setDisable(false);
                btnClientes.setDisable(false);
                btnEnvios.setDisable(false);
                btnPaquetes.setDisable(false);
                break;

            case "Ejecutivo de tienda":
                btnColaboradores.setDisable(true);
                btnUnidades.setDisable(true);
                btnSucursales.setDisable(true);
                btnClientes.setDisable(false);
                btnEnvios.setDisable(false);
                btnPaquetes.setDisable(false);
                break;

            case "Conductor":
            default:
                btnColaboradores.setDisable(true);
                btnUnidades.setDisable(true);
                btnSucursales.setDisable(true);
                btnClientes.setDisable(true);
                btnEnvios.setDisable(true);
                btnPaquetes.setDisable(true);
                break;
        }
    }

    @FXML
private void clickColaboradores(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionColaboradores.fxml"));
        Parent vista = loader.load();

        FXMLAdministracionColaboradoresController controller = loader.getController();
        controller.inicializar(colaboradorSesion, this); // ✅ ESTILO RAMÓN

        borderPaneContenedor.setCenter(vista);
    } catch (Exception e) {
        e.printStackTrace();
        Utilidades.mostrarAlertaSimple("Error",
                "No se pudo cargar el módulo de colaboradores",
                Alert.AlertType.ERROR);
    }
}


    @FXML
    private void clickUnidades(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionUnidades.fxml"));
            Parent vista = loader.load();
            borderPaneContenedor.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo cargar el módulo de unidades",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickSucursales(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionSucursales.fxml"));
            Parent vista = loader.load();
            borderPaneContenedor.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo cargar el módulo de sucursales",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickClientes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionClientes.fxml"));
            Parent vista = loader.load();
            borderPaneContenedor.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo cargar el módulo de clientes",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickEnvios(ActionEvent event) {
        cargarModuloEnvios();
    }
    
    public void cargarModuloEnvios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionEnvio.fxml"));
            Parent vista = loader.load();

            FXMLAdministracionEnvioController controller = loader.getController();
            controller.inicializarSesion(colaboradorSesion);

            borderPaneContenedor.setCenter(vista);

        } catch (Exception e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo cargar el módulo de envíos",
                    Alert.AlertType.ERROR);
        }
    }
@FXML
private void clickPaquetes(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionPaquetes.fxml"));
        Parent vista = loader.load();
        borderPaneContenedor.setCenter(vista);
    } catch (Exception e) {
        e.printStackTrace();
        Utilidades.mostrarAlertaSimple("Error",
                "No se pudo cargar el módulo de paquetes",
                Alert.AlertType.ERROR);
    }
}
@Override
public void notificarFotoActualizada(int idColaborador) {
    if (colaboradorSesion != null && idColaborador == colaboradorSesion.getIdColaborador()) {
        cargarFotoHeader(idColaborador); // ✅ refresca el avatar del header
    }
}

@Override
public void notificarOperacionExitosa(String operacion, String nombre) {

}



}
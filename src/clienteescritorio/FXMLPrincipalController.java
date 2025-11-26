package clienteescritorio;

import clienteescritorio.pojo.Colaborador;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable {

    @FXML
    private Label rol;
    
    @FXML
    private Label name;
    
    @FXML
    private Label numeroPersonal;

    private Colaborador colaboradorSesion; 
    @FXML
    private Pane header;
    @FXML
    private Label titleApp;
    @FXML
    private VBox sideMenu;
    @FXML
    private Button btnColaboradores;
    @FXML
    private Button btnUnidades;
    @FXML
    private Button btnSucursales;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnEnvios;
    @FXML
    private Button btnPaquetes;
    @FXML
    private Button btnAsignarVehiculo;
    @FXML
    private Button btnAsignarEnvio;
    @FXML
    private Button cerrarSesionBtn;
    @FXML
    private Label bienvenidaTxt;
    @FXML
    private BorderPane borderPaneContenedor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
   
    }    

    public void cargarInformacion(Colaborador colaborador){
        colaboradorSesion = colaborador;
        name.setText(colaborador.getNombre() + " " + colaborador.getApellidoPaterno() + " " + colaborador.getApellidoMaterno() + " ");
        rol.setText("Rol: " + colaborador.getRol());
        numeroPersonal.setText(colaborador.getNumeroPersonal());
        aplicarPermisos(colaborador.getRol());
    }
    
    @FXML
    private void clickCerrarSesion(ActionEvent event) {
        try{
            Parent vista = FXMLLoader.load(getClass().getResource("FXMLInicioSesion.fxml"));
            Scene login = new Scene(vista);
            
            Stage stPrincipal = (Stage) ((Button) event.getSource()).getScene().getWindow();
            
            //Cambio Scene
            stPrincipal.setScene(login);
            stPrincipal.setTitle("Login");
            stPrincipal.show();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void aplicarPermisos(String rol) {

    switch (rol) {

        case "Administrador":
            // El Administrador puede TODO
            btnColaboradores.setDisable(false);
            btnUnidades.setDisable(false);
            btnSucursales.setDisable(false);
            btnClientes.setDisable(false);
            btnEnvios.setDisable(false);
            btnPaquetes.setDisable(false);
            btnAsignarVehiculo.setDisable(false);
            btnAsignarEnvio.setDisable(false);
            break;


        case "Ejecutivo de tienda":
            // Ejecutivos SOLO pueden clientes, envíos, paquetes y asignar envío
            btnColaboradores.setDisable(true);   // CU-02,03,04 solo Admin
            btnUnidades.setDisable(true);        // CU-06,07,08 solo Admin
            btnSucursales.setDisable(true);      // CU-10,11,12 solo Admin

            btnClientes.setDisable(false);       // CU-13 al CU-16 sí puede
            btnEnvios.setDisable(false);         // CU-18 al CU-21 sí puede
            btnPaquetes.setDisable(false);       // CU-22 al CU-24 sí puede

            btnAsignarVehiculo.setDisable(true); // CU-17 solo Admin
            btnAsignarEnvio.setDisable(false);   // CU-26 sí puede
            break;


        case "Conductor":
            // El Conductor NO usa escritorio → deshabilitar todo
            btnColaboradores.setDisable(true);
            btnUnidades.setDisable(true);
            btnSucursales.setDisable(true);
            btnClientes.setDisable(true);
            btnEnvios.setDisable(true);
            btnPaquetes.setDisable(true);
            btnAsignarVehiculo.setDisable(true);
            btnAsignarEnvio.setDisable(true);
            break;


        default:
            // Seguridad extra
            btnColaboradores.setDisable(true);
            btnUnidades.setDisable(true);
            btnSucursales.setDisable(true);
            btnClientes.setDisable(true);
            btnEnvios.setDisable(true);
            btnPaquetes.setDisable(true);
            btnAsignarVehiculo.setDisable(true);
            btnAsignarEnvio.setDisable(true);
            break;
    }
}
    @FXML
    private void clickColaboradores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAdministracionColaboradores.fxml"));
            Parent vista = loader.load();
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


    
}

package clienteescritorio;

import clienteescritorio.dominio.AutentificarImp;
import clienteescritorio.dto.RSAutentificarAdmin;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLinicioSesionController implements Initializable {
    
    @FXML
    private TextField numero_personal;
    @FXML
    private PasswordField contrasena;
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    private void verificarCredenciales(String numero_personal, String contrasena){
        RSAutentificarAdmin respuesta = AutentificarImp.verificarCredenciales(numero_personal, contrasena);
        if(!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Credenciales verificadas", "Bienvenido(a) " 
                    + respuesta.getColaborador().getNombre() + " al sistema de paqueteria.", Alert.AlertType.INFORMATION);
            irPantallaInicio(respuesta.getColaborador());
        } else {
            Utilidades.mostrarAlertaSimple("Credenciales incorrectas", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    
    private void irPantallaInicio(Colaborador colaborador){
        try{
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLPrincipal.fxml"));
            Parent vista = cargador.load();
            
            FXMLPrincipalController controlador = cargador.getController();
            controlador.cargarInformacion(colaborador);
            controlador.cargarModuloEnvios();
            
            Scene escenePrincipal = new Scene(vista);
            Stage stPrincipal = (Stage) numero_personal.getScene().getWindow();
            stPrincipal.setScene(escenePrincipal);
            stPrincipal.setTitle("Home");
            stPrincipal.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }   
    }
    
    @FXML
    private void clickIngresarSesion(ActionEvent event) {
        String noPersonal = numero_personal.getText();
        String password = contrasena.getText();
        
        if(!noPersonal.isEmpty() && !password.isEmpty()){
            verificarCredenciales(noPersonal, password);
        }else{
            Utilidades.mostrarAlertaSimple("Campos requeridos", "El numero de personal y/o la contraseña son obligatorios", Alert.AlertType.WARNING);
        }
    }
}
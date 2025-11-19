package clienteescritorio;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class FXMLinicioSesionController implements Initializable {

    @FXML
    private TextField tfCorreo;
    @FXML
    private PasswordField pfContrasenia;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
    }    

    @FXML
    private void clickIngresarSesion(ActionEvent event) {   
       
       String correo = tfCorreo.getText();
       String password = pfContrasenia.getText();
       
       //si no es vacio 
       if(!correo.isEmpty() && !password.isEmpty()){
           irPantallaInicio(); 
       }else{
           Alert alerta= new Alert(AlertType.WARNING); 
           alerta.setTitle("Campos vacios");
           alerta.setHeaderText("Falta informacion");
           /*Java entiende q no deberia ir un titulo 
           alerta.setHeaderText("Falta null");*/
           alerta.setContentText("Debes ingresar tu correo electronico" 
                   +" y contraseña para inicair sesión");
           //showAndWait hace q el usuario no pueda navegar a otras ventanas 
           alerta.showAndWait(); 
       }
    }
    //mismo stage difrentes escenas 
    private void irPantallaInicio(){
        try {
            //crear escena
            Parent vista =
                    FXMLLoader.load(getClass().getResource("FXMLPrincipal.fxml"));
            Scene escenaPrincipal = new Scene(vista); 
            
            //obtener escenario actual
            //se utiliza caulquier componente visual q este en mi stage
            //con el componente tenemos acceso a la escena y cn el escena tenemos accso al stage o window
            //se hace un casteo, la ventana se convertira en un Stage
            Stage stPrincipal = (Stage)tfCorreo.getScene().getWindow(); 
            
            //cambio de escena 
            stPrincipal.setScene(escenaPrincipal);
            stPrincipal.setTitle("Home");
            stPrincipal.show(); 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}

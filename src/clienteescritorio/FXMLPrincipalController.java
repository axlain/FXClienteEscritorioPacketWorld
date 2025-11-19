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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable {

    @FXML
    private Label lHome;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void clicIrAdminUsuario(ActionEvent event) {
        try {
            Parent vista =
                    FXMLLoader.load(getClass().getResource("FXMLAdministracionUsuarios.fxml"));
            Scene scAdminUsuarios = new Scene(vista); 
            
            //Genrar un nuevo stage
            Stage stAdmin = new Stage();
            stAdmin.setScene(scAdminUsuarios); 
            stAdmin.setTitle("Administración de usaurios"); 
            stAdmin.initModality(Modality.APPLICATION_MODAL);
            stAdmin.showAndWait();
        } catch (IOException ex) {
           ex.printStackTrace();
        }
             
    }

    @FXML
    private void clilckCerrarSesion(ActionEvent event) {
        try { 
            //se crea una escena 
            Parent vista  = 
                    FXMLLoader.load(getClass().getResource("FXMLinicioSesion.fxml"));
            Scene escenaCerrarSesion = new Scene(vista); 
            
            //identificamos en q stage nos encontramos
            //se hace un casteo, la ventana se convertira en un Stage
            //el lHome, es necesario un objeto para saber en que contexto nos encontramos 
            
            //Stage stCerrarSesion = (Stage)lHome.getScene().getWindow(); 
            
            //identifica el objeto que lo lanzo
            //se catea para q sepa q sea un button y de ahi se castea para q sea un stage
            Stage stCerrarSesion =(Stage)((Button)event.getSource()).getScene().getWindow(); 
            
            //el stage es renombrado pero sigue sinedo el mismo
            //se le añade una nueva escena al stage
            //cambio de escena 
            stCerrarSesion.setScene(escenaCerrarSesion);
            stCerrarSesion.setTitle("Login");
            stCerrarSesion.show(); 
        } catch (IOException ex) {
             ex.printStackTrace();
        }
    }
    
}

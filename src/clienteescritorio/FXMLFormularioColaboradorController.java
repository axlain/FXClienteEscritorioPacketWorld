package clienteescritorio;

import clienteescritorio.dominio.ColaboradorImp;
import clienteescritorio.dominio.SucursalImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colaborador;
import clienteescritorio.pojo.Sucursal;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


public class FXMLFormularioColaboradorController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private TextField tfCurp;
    @FXML
    private TextField tfCorreo;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private ComboBox<String> cbRol;
    @FXML
    private VBox vboxLicencia;
    @FXML
    private TextField tfLicencia;
    @FXML
    private ComboBox<Sucursal> cbSucursal;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private ObservableList<String> roles;
    private ObservableList<Sucursal> sucursales;
    
    private Colaborador colaboradorEdicion;
    private INotificador observador;
    private static final int MAX_LICENCIA = 15; // AJUSTA al tamaño real en MySQL (ej. VARCHAR(15))
    private static final String REGEX_LICENCIA_FINAL = "^[A-Z0-9-]+$"; // formato final permitido


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarRoles();
        cargarSucursales();
        configurarListenerRol();
        configurarCamposSoloLetras(tfNombre);
        configurarCamposSoloLetras(tfApellidoPaterno);
        configurarCamposSoloLetras(tfApellidoMaterno);
    }
    
    public void iniciarlizarDatos(Colaborador colaboradorEdicion, INotificador observador){
        this.colaboradorEdicion = colaboradorEdicion;
        this.observador = observador;
        
        if(colaboradorEdicion != null){
            lblTitulo.setText("Editar Colaborador");
            tfNombre.setText(colaboradorEdicion.getNombre());
            tfApellidoPaterno.setText(colaboradorEdicion.getApellidoPaterno());
            tfApellidoMaterno.setText(colaboradorEdicion.getApellidoMaterno());
            tfCurp.setText(colaboradorEdicion.getCurp());
            tfCorreo.setText(colaboradorEdicion.getCorreo());
            
            int posicionRol = obtenerPosicionRol(colaboradorEdicion.getIdRol());
            cbRol.getSelectionModel().select(posicionRol);

            if(colaboradorEdicion.getNumeroLicencia() != null && 
               !colaboradorEdicion.getNumeroLicencia().isEmpty()){
                vboxLicencia.setVisible(true);
                vboxLicencia.setManaged(true);
                tfLicencia.setText(colaboradorEdicion.getNumeroLicencia());
            }
            
            int posicionSucursal = obtenerPosicionSucursal(colaboradorEdicion.getIdSucursal());
            cbSucursal.getSelectionModel().select(posicionSucursal);
            
            tfCurp.setEditable(false);
            pfContrasena.setVisible(false);
        

        } else {
            lblTitulo.setText("Registrar Colaborador");
        }
    }
    
    private void configurarCamposSoloLetras(TextField campo) {
        campo.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            if (change.getText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!sonCamposValidos()) {
            return;
        }

        Colaborador colaborador = new Colaborador();
        colaborador.setNombre(tfNombre.getText());
        colaborador.setApellidoPaterno(tfApellidoPaterno.getText());
        colaborador.setApellidoMaterno(tfApellidoMaterno.getText());
        colaborador.setCurp(tfCurp.getText().toUpperCase());
        colaborador.setCorreo(tfCorreo.getText());
        colaborador.setContrasena(pfContrasena.getText());
        
        String rolSeleccionado = cbRol.getSelectionModel().getSelectedItem();
        colaborador.setIdRol(obtenerIdRol(rolSeleccionado));
        
        if("Conductor".equals(rolSeleccionado)){
            colaborador.setNumeroLicencia(tfLicencia.getText());
        }
        
        Sucursal sucursalSeleccionada = cbSucursal.getSelectionModel().getSelectedItem();
        colaborador.setIdSucursal(sucursalSeleccionada.getIdSucursal());

        if (colaboradorEdicion == null){
            registrarColaborador(colaborador);
        } else {
            editarColaborador(colaborador);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cargarRoles(){
        roles = FXCollections.observableArrayList();
        roles.addAll("Administrador", "Ejecutivo de tienda", "Conductor");
        cbRol.setItems(roles);
    }
    
    private void cargarSucursales(){
        HashMap<String,Object> respuesta = SucursalImp.obtenerTodas();
        if(!(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Sucursal> sucursalesAPI = (List<Sucursal>) respuesta.get(Constantes.KEY_LISTA);
            sucursales = FXCollections.observableArrayList();
            sucursales.addAll(sucursalesAPI);
            cbSucursal.setItems(sucursales);
        }else{
            Utilidades.mostrarAlertaSimple("Error", respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    
   private void configurarListenerRol(){
        cbRol.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if ("Conductor".equals(newValue)) {
                        vboxLicencia.setVisible(true);
                        vboxLicencia.setManaged(true);
                    } else {
                        vboxLicencia.setVisible(false);
                        vboxLicencia.setManaged(false);
                        tfLicencia.clear();
                    }
                }
            }
        );
    }
    private String validarLicenciaFinal(String licenciaCruda) {
        String lic = (licenciaCruda == null) ? "" : licenciaCruda.trim().toUpperCase();

        if (lic.isEmpty()) {
            return "El número de licencia es obligatorio para conductores.";
        }

        if (lic.length() > MAX_LICENCIA) {
            return "El número de licencia no puede exceder " + MAX_LICENCIA + " caracteres.";
        }

        // Validación de formato final (no vacío y cumpliendo patrón)
        if (!lic.matches(REGEX_LICENCIA_FINAL)) {
            return "La licencia solo puede contener letras, números y guiones.";
        }

        return null; // OK
    }
    
    private boolean sonCamposValidos() {
        String mensaje = "";

        TextField[] camposTexto;
        String[] nombresCampos;

        if (colaboradorEdicion == null) {
            camposTexto = new TextField[]{ tfNombre, tfApellidoPaterno, tfApellidoMaterno, tfCurp, tfCorreo, pfContrasena };
            nombresCampos = new String[]{ "Nombre", "Apellido paterno", "Apellido materno", "CURP", "Correo", "Contraseña" };
        } else {
            camposTexto = new TextField[]{ tfNombre, tfApellidoPaterno, tfApellidoMaterno, tfCurp, tfCorreo };
            nombresCampos = new String[]{ "Nombre", "Apellido paterno", "Apellido materno", "CURP", "Correo" };
        }

        for (int i = 0; i < camposTexto.length; i++) {
            if (camposTexto[i].getText() == null || camposTexto[i].getText().trim().isEmpty()) {
                mensaje += "- " + nombresCampos[i] + " es obligatorio.\n";
            }
        }

        if (cbRol.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar un rol.\n";
        }

        if (cbSucursal.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar una sucursal.\n";
        }

        String rolSeleccionado = cbRol.getSelectionModel().getSelectedItem();
        if ("Conductor".equals(rolSeleccionado)) {
            String errorLic = validarLicenciaFinal(tfLicencia.getText());
            if (errorLic != null) {
                mensaje += "- " + errorLic + "\n";
            }
        }

        if (!mensaje.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", mensaje, Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }
    
    private void registrarColaborador(Colaborador colaborador){
        Respuesta respuesta = ColaboradorImp.registrar(colaborador);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Colaborador registrado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("registro", colaborador.getNombre());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al registrar colaborador", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    private void editarColaborador(Colaborador colaborador){
        colaborador.setIdColaborador(colaboradorEdicion.getIdColaborador());
        colaborador.setNumeroPersonal(colaboradorEdicion.getNumeroPersonal());

        Respuesta respuesta = ColaboradorImp.editar(colaborador);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Colaborador editado", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("edición", colaborador.getNombre());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al editar colaborador", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    
    private int obtenerIdRol(String rol){
        if("Administrador".equals(rol)){
            return 1;
        } else if("Ejecutivo de tienda".equals(rol)){
            return 2;
        } else if("Conductor".equals(rol)){
            return 3;
        }
        return 0;
    }
    
    private int obtenerPosicionRol(int idRol){
        for(int i = 0; i < roles.size(); i++){
            if(obtenerIdRol(roles.get(i)) == idRol){
                return i;
            }
        }
        return -1;
    }
    
    private int obtenerPosicionSucursal(int idSucursal){
        for(int i = 0; i < sucursales.size(); i++){
            if(sucursales.get(i).getIdSucursal() == idSucursal){
                return i;
            }
        }
        return -1;
    }
    
    private void cerrarVentana(){
        ((Stage) tfNombre.getScene().getWindow()).close();
    }
}
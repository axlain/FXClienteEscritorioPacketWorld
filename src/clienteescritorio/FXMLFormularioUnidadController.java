package clienteescritorio;


import clienteescritorio.dominio.CatalogoImp;
import clienteescritorio.pojo.Sucursal;
import clienteescritorio.pojo.TipoUnidad;
import clienteescritorio.dominio.SucursalImp;
import clienteescritorio.dominio.UnidadImp;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Unidad;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioUnidadController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfMarca;
    @FXML
    private TextField tfModelo;
    @FXML
    private TextField tfAnio;
    @FXML
    private TextField tfVin;
    @FXML
    private ComboBox<TipoUnidad> cbTipoUnidad;
    @FXML
    private ComboBox<Sucursal> cbSucursal;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private ObservableList<Sucursal> sucursales; 
    private ObservableList<TipoUnidad> tipoUnidades; 
    private INotificador observador;
    private Unidad unidadEdicion; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cargarTipoUnidad();
        cargarSucursales();
    }    
    
    public void iniciarlizarDatos(Unidad unidadEdicion, INotificador observador){
        this.unidadEdicion = unidadEdicion; 
        this.observador = observador; 
        if(unidadEdicion != null){
            tfMarca.setText(unidadEdicion.getMarca());
            tfModelo.setText(unidadEdicion.getModelo());
            tfAnio.setText(String.valueOf(unidadEdicion.getAnio()));
            tfVin.setText(unidadEdicion.getVin());
            
            int posicionTipoUnidad = obtenerPosicionTipoUnidad(unidadEdicion.getIdTipoUnidad());
            cbTipoUnidad.getSelectionModel().select(posicionTipoUnidad);
            
            int posicionSucursal = obtenerPosicionSucursal(unidadEdicion.getIdSucursal());
            cbSucursal.getSelectionModel().select(posicionSucursal);
            tfVin.setEditable(false);
        }
    }
    
    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!sonCamposValidos()) {
            return;
        }

        Unidad unidad = new Unidad();
        unidad.setMarca(tfMarca.getText().trim());
        unidad.setModelo(tfModelo.getText().trim());
        unidad.setAnio(Integer.parseInt(tfAnio.getText().trim()));

        // VIN solo en registro
        if (unidadEdicion == null) {
            unidad.setVin(tfVin.getText().trim());
        } else {
            unidad.setVin(unidadEdicion.getVin());
        }

        unidad.setIdTipoUnidad(cbTipoUnidad.getSelectionModel().getSelectedItem().getIdTipoUnidad());
        unidad.setIdSucursal(cbSucursal.getSelectionModel().getSelectedItem().getIdSucursal());

        if (unidadEdicion == null) {
            registrarUnidades(unidad);
        } else {
            unidad.setIdUnidad(unidadEdicion.getIdUnidad());
            editarUnidad(unidad);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    
    private void cargarTipoUnidad(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerTipoUnidades();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<TipoUnidad> tipoDeUnidadesAPI = (List<TipoUnidad>) respuesta.get(Constantes.KEY_LISTA);
            tipoUnidades = FXCollections.observableArrayList();
            tipoUnidades.addAll(tipoDeUnidadesAPI);
            cbTipoUnidad.setItems(tipoUnidades);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    
    private void cargarSucursales(){
        HashMap<String,Object> respuesta = SucursalImp.obtenerTodas();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Sucursal> sucursalesAPI = (List<Sucursal>) respuesta.get(Constantes.KEY_LISTA);
            sucursales = FXCollections.observableArrayList();
            sucursales.addAll(sucursalesAPI);
            cbSucursal.setItems(sucursales);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    
    private boolean sonCamposValidos() {
        String mensaje = "";
        // Campos obligatorios comunes 
        TextField[] camposTexto = {
            tfMarca,
            tfModelo,
            tfAnio
        };
        String[] nombresCampos = {
            "Marca",
            "Modelo",
            "Año"
        };

        // Validar campos vacíos comunes
        for (int i = 0; i < camposTexto.length; i++) {
            if (camposTexto[i].getText() == null || camposTexto[i].getText().trim().isEmpty()) {
                mensaje += "- " + nombresCampos[i] + " es obligatorio.\n";
            }
        }

        // VIN solo es obligatorio en REGISTRO
        if (unidadEdicion == null) {
            if (tfVin.getText().trim().isEmpty()) {
                mensaje += "- El VIN es obligatorio.\n";
            } else if (tfVin.getText().trim().length() < 4) {
                mensaje += "- El VIN debe tener al menos 4 caracteres.\n";
            }
        }

        // Validar Año
        String anioTexto = tfAnio.getText().trim();
        if (!anioTexto.isEmpty()) {
            try {
                int anio = Integer.parseInt(anioTexto);
                int anioActual = LocalDate.now().getYear();

                if (anio < 1900 || anio > anioActual) {
                    mensaje += "- El año debe estar entre 1900 y " + anioActual + ".\n";
                }
            } catch (NumberFormatException e) {
                mensaje += "- El año debe ser un número entero válido.\n";
            }
        }

        if (cbTipoUnidad.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar un Tipo de Unidad.\n";
        }
        if (cbSucursal.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar una Sucursal.\n";
        }
        //errores
        if (!mensaje.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", mensaje, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    
    private void cerrarVentana(){
        ((Stage) tfVin.getScene().getWindow()).close();
    }
    
    private void registrarUnidades(Unidad unidad){
        Respuesta respuesta = UnidadImp.registrar(unidad);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Unidad registrada", 
                    respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("registro", unidad.getCodigoSucursal());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al registrar unidad", 
                    respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    private void editarUnidad(Unidad unidad){
        unidad.setIdUnidad(unidadEdicion.getIdUnidad());
        Respuesta respuesta = UnidadImp.editar(unidad);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Unidad editada", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("edición", unidad.getMarca());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al editar unidad", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    
    private int obtenerPosicionTipoUnidad(int idTipoUnidad){
        for(int i = 0; i < tipoUnidades.size(); i++){
            if(tipoUnidades.get(i).getIdTipoUnidad()== idTipoUnidad){
                return i; 
            }         
        }
        return -1; 
    }
    
     private int obtenerPosicionSucursal(int idSucursal){
        for(int i = 0; i < sucursales.size(); i++){
            if(sucursales.get(i).getIdSucursal()== idSucursal){
                return i; 
            }      
        }
        return -1; 
    }
}

package clienteescritorio;

import clienteescritorio.dominio.CatalogoImp;
import clienteescritorio.dominio.SucursalImp;
import clienteescritorio.dto.RSDatosCodigoPostal;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.interfaz.INotificador;
import clienteescritorio.pojo.Colonia;
import clienteescritorio.pojo.Estado;
import clienteescritorio.pojo.Municipio;
import clienteescritorio.pojo.Pais;
import clienteescritorio.pojo.Sucursal;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioSucursalController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombreCorto;
    @FXML
    private TextField tfCalle;
    @FXML
    private TextField tfNumero;
    @FXML
    private ComboBox<Pais> cbPais;
    @FXML
    private ComboBox<Estado> cbEstado;
    @FXML
    private ComboBox<Municipio> cbMunicipio;
    @FXML
    private ComboBox<Colonia> cbColonia;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private ObservableList<Pais> paises;
    private ObservableList<Estado> estados;
    private ObservableList<Municipio> municipios;
    private ObservableList<Colonia> colonias;
    
    private Sucursal sucursalEdicion;
    private INotificador observador;
    @FXML
    private TextField tfCodigoPostal;
    @FXML
    private Button btnBuscarCP;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPais();
        cbPais.setDisable(true);
        cbEstado.setDisable(true);
        cbMunicipio.setDisable(true);
        cbColonia.setDisable(true);
    }    
    
    public void iniciarlizarDatos(Sucursal sucursalEdicion, INotificador observador){
        this.sucursalEdicion = sucursalEdicion;
        this.observador = observador;

        if(sucursalEdicion != null){
            lblTitulo.setText("Editar Sucursal");
            tfNombreCorto.setText(sucursalEdicion.getNombreCorto());
            tfCalle.setText(sucursalEdicion.getCalle());
            tfNumero.setText(sucursalEdicion.getNumero());
            tfCodigoPostal.setText(sucursalEdicion.getCodigoPostal());

            // Asegúrate de que los ComboBox están cargados antes de seleccionar
            if (paises != null && !paises.isEmpty()) {
                int posicionPais = obtenerPosicionPais(sucursalEdicion.getIdPais());
                cbPais.getSelectionModel().select(posicionPais);
            }

            cargarEstado();
            if (estados != null && !estados.isEmpty()) {
                int posicionEstado = obtenerPosicionEstado(sucursalEdicion.getIdEstado());
                cbEstado.getSelectionModel().select(posicionEstado);
            }

            cargarMunicipio();
            if (municipios != null && !municipios.isEmpty()) {
                int posicionMunicipio = obtenerPosicionMunicipio(sucursalEdicion.getIdMunicipio());
                cbMunicipio.getSelectionModel().select(posicionMunicipio);
            }

            // No deshabilitar el ComboBox de colonia aquí
            if (colonias != null && !colonias.isEmpty()) {
                int posicionColonia = obtenerPosicionColonia(sucursalEdicion.getIdColonia());
                cbColonia.getSelectionModel().select(posicionColonia);
            }
        }
    }

    
    @FXML
    private void clickGuardar(ActionEvent event) {
        // 1. Validar los campos del formulario
        if (!sonCamposValidos()) {
            return; 
        }

        // 2. Crear el objeto sucursal con los datos del formulario
        Sucursal sucursal = new Sucursal();
        sucursal.setNombreCorto(tfNombreCorto.getText().trim());
        sucursal.setCalle(tfCalle.getText().trim());
        sucursal.setNumero(tfNumero.getText().trim());
        sucursal.setCodigoPostal(tfCodigoPostal.getText().trim()); 

        // 3. Asignar los IDs desde los ComboBoxes (ya validados en sonCamposValidos)
        sucursal.setIdPais(cbPais.getSelectionModel().getSelectedItem().getIdPais());
        sucursal.setIdEstado(cbEstado.getSelectionModel().getSelectedItem().getIdEstado());
        sucursal.setIdMunicipio(cbMunicipio.getSelectionModel().getSelectedItem().getIdMunicipio());
        sucursal.setIdColonia(cbColonia.getSelectionModel().getSelectedItem().getIdColonia());

        // 4. DECISIÓN IMPORTANTE: ¿Es nuevo o es edición?
        if (sucursalEdicion != null) {
            // Si sucursalEdicion NO es null, significa que estamos EDITANDO
            sucursal.setIdSucursal(sucursalEdicion.getIdSucursal());
            editarSucursal(sucursal);
        } else {
            // Si es null, significa que es un REGISTRO NUEVO (la BD genera el ID)
            registrarSucursal(sucursal);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrarVentana(); 
    }
    
    private void cerrarVentana(){
        ((Stage) tfNombreCorto.getScene().getWindow()).close();
    }
    
    private boolean sonCamposValidos() {
        String mensaje = "";
        TextField[] camposTexto = {
            tfNombreCorto,
            tfCalle,
            tfNumero,
            tfCodigoPostal
        };
        String[] nombresCampos = {
            "Nombre Corto",
            "Calle",
            "Número",
            "Código Postal"
        };

        // Validar campos vacíos comunes
        for (int i = 0; i < camposTexto.length; i++) {
            if (camposTexto[i].getText() == null || camposTexto[i].getText().trim().isEmpty()) {
                mensaje += "- " + nombresCampos[i] + " es obligatorio.\n";
            }
        }
        // Validar Código Postal (debe ser 5 dígitos)
        String cp = tfCodigoPostal.getText().trim();
        if (!cp.matches("\\d{5}")) {
            mensaje += "- El Código Postal debe ser un número de 5 dígitos.\n";
        }

        // Validar selección en los ComboBoxes
        if (cbPais.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar un país.\n";
        }
        if (cbEstado.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar un estado.\n";
        }
        if (cbMunicipio.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar un municipio.\n";
        }
        if (cbColonia.getSelectionModel().getSelectedItem() == null) {
            mensaje += "- Debes seleccionar una colonia.\n";
        }
        if (!mensaje.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", mensaje, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
     private void registrarSucursal(Sucursal sucursal){
        Respuesta respuesta = SucursalImp.registrar(sucursal);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Sucursal registrada", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("registro", sucursal.getNombreCorto());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al registrar sucursal", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }
     private void editarSucursal(Sucursal sucursal){
        sucursal.setIdSucursal(sucursalEdicion.getIdSucursal());
        Respuesta respuesta = SucursalImp.editar(sucursal);
        if (!respuesta.isError()){
            Utilidades.mostrarAlertaSimple("Sucursal editada", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            observador.notificarOperacionExitosa("edición", sucursal.getNombreCorto());
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error al editar sucursal", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    
    
    
    
    private void cargarPais(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerPais();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Pais> paisesAPI = (List<Pais>) respuesta.get(Constantes.KEY_LISTA);
            paises = FXCollections.observableArrayList();
            paises.addAll(paisesAPI);
            cbPais.setItems(paises);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    private void cargarEstado(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerEstado();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Estado> estadosAPI = (List<Estado>) respuesta.get(Constantes.KEY_LISTA);
            estados = FXCollections.observableArrayList();
            estados.addAll(estadosAPI);
            cbEstado.setItems(estados);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    private void cargarMunicipio(){
        HashMap<String,Object> respuesta = CatalogoImp.obtenerMunicipio();
        if( !(boolean) respuesta.get(Constantes.KEY_ERROR)){
            List<Municipio> municipiosAPI = (List<Municipio>) respuesta.get(Constantes.KEY_LISTA);
            municipios = FXCollections.observableArrayList();
            municipios.addAll(municipiosAPI);
            cbMunicipio.setItems(municipios);
        }else{
            Utilidades.mostrarAlertaSimple("Error", 
                    respuesta.get(Constantes.KEY_MENSAJE).toString(), Alert.AlertType.ERROR);
            cerrarVentana();
        }
    }
    
    

    private int obtenerPosicionPais(int idPais){
        for(int i = 0; i < paises.size(); i++){
            if(paises.get(i).getIdPais()== idPais){
                return i; 
            }      
        }
        return -1; 
    }
    private int obtenerPosicionEstado(int idEstado){
        for(int i = 0; i < estados.size(); i++){
            if(estados.get(i).getIdEstado()== idEstado){
                return i; 
            }      
        }
        return -1; 
    }
    private int obtenerPosicionMunicipio(int idMunicipio){
        for(int i = 0; i < municipios.size(); i++){
            if(municipios.get(i).getIdMunicipio()== idMunicipio){
                return i; 
            }      
        }
        return -1; 
    }
    private int obtenerPosicionColonia(int idColonias){
        for(int i = 0; i < colonias.size(); i++){
            if(colonias.get(i).getIdColonia()== idColonias){
                return i; 
            }      
        }
        return -1; 
    }

    @FXML
    private void clicBuscarCP(ActionEvent event) {
        String cp = tfCodigoPostal.getText();

        if (cp.length() != 5 || !cp.matches("\\d+")) {
            Utilidades.mostrarAlertaSimple("Advertencia", "Ingresa un CP válido de 5 dígitos.", Alert.AlertType.WARNING);
            return;
        }

        // Cambiar cursor para indicar que "algo está pasando"
        // Aunque se trabe, al menos el usuario ve el reloj de arena
        if (tfCodigoPostal.getScene() != null) {
            tfCodigoPostal.getScene().setCursor(javafx.scene.Cursor.WAIT);
        }

        try { 
            // Pedimos los datos generales 
            HashMap<String, Object> respuestaDatos = CatalogoImp.obtenerDatosCP(cp);
       
            // Si encontró el CP, pedimos las colonias inmediatamente
            if (!(boolean) respuestaDatos.get(Constantes.KEY_ERROR)) {
                HashMap<String, Object> respuestaColonias = CatalogoImp.obtenerColoniasPorCP(cp);
                // Las metemos en el mismo mapa para procesar todo junto
                respuestaDatos.put("colonias_extra", respuestaColonias); 
            }
            // Llenar combos
            procesarRespuestaCP(respuestaDatos, cp);

        } catch (Exception e) {
            Utilidades.mostrarAlertaSimple("Error", "Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        // Restaurar cursor y UI pase lo que pase
        if (tfCodigoPostal.getScene() != null) {
            tfCodigoPostal.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        }
        
    }
    private void procesarRespuestaCP(HashMap<String, Object> respuesta, String cp) {
        if (!(boolean) respuesta.get(Constantes.KEY_ERROR)) {
            RSDatosCodigoPostal datos = (RSDatosCodigoPostal) respuesta.get(Constantes.KEY_OBJETO);

            // 1. País
            int posPais = obtenerPosicionPais(datos.getIdPais());
            cbPais.getSelectionModel().select(posPais);
            
            // 2. Estado 
            cargarEstado(); 
            int posEstado = obtenerPosicionEstado(datos.getIdEstado());
            cbEstado.getSelectionModel().select(posEstado);
            
            // 3. Municipio 
            cargarMunicipio();
            int posMunicipio = obtenerPosicionMunicipio(datos.getIdMunicipio());
            cbMunicipio.getSelectionModel().select(posMunicipio);
            
            // 4. Colonias 
            HashMap<String, Object> respColonias = (HashMap<String, Object>) respuesta.get("colonias_extra");
            if(respColonias != null && !(boolean) respColonias.get(Constantes.KEY_ERROR)){
                List<Colonia> listaCols = (List<Colonia>) respColonias.get(Constantes.KEY_LISTA);
                colonias = FXCollections.observableArrayList(listaCols);
                cbColonia.setItems(colonias);
                cbColonia.setDisable(false);
                
                // Si solo hay una, la seleccionamos
                if (colonias.size() == 1) cbColonia.getSelectionModel().select(0);
                cbColonia.show(); 
            }

        } else {
            Utilidades.mostrarAlertaSimple("Sin resultados", "No se encontró información para el CP: " + cp, Alert.AlertType.INFORMATION);
            limpiarCombosDireccion();
        }
    }

    
    private void restaurarUI() {
        btnBuscarCP.setDisable(false);
        tfCodigoPostal.setDisable(false);
        tfCodigoPostal.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
    }
    
    private void limpiarCombosDireccion() {
        cbPais.getSelectionModel().clearSelection();
        cbEstado.getItems().clear();
        cbMunicipio.getItems().clear();
        cbColonia.getItems().clear();
        cbColonia.setDisable(true);
    }
}

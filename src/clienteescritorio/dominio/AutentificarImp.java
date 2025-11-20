/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clienteescritorio.dominio;
import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.RSAutentificarAdmin;
import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.utilidad.Constantes;
import com.google.gson.Gson;
import java.net.HttpURLConnection;
/**
 *
 * @author ferro
 */
public class AutentificarImp {
    
    public static RSAutentificarAdmin verificarCredenciales(String numero_personal, String contrasena){
        RSAutentificarAdmin respuesta = new RSAutentificarAdmin(); 
        String parametros = "numero_personal=" + numero_personal + "&contrasena=" + contrasena;
        String URL = Constantes.URL_WS + "autentificar/administracion";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(URL, "POST", parametros, "application/x-www-form-urlencoded");
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            //flujo controlado 
           try{
               Gson gson = new Gson();
               respuesta = gson.fromJson(respuestaAPI.getContenido(), RSAutentificarAdmin.class);
           } catch (Exception e){
               respuesta.setError(true);
               respuesta.setMensaje("Lo sentimos hubo un error al obtener la información, intentelo mas tarde.");
           }
        } else {
            respuesta.setError(true);
            switch (respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_URL);
                    break; 
                case Constantes.ERROR_PETICION:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_PETICION);
                    break; 
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    respuesta.setMensaje("Datos requeridos para poder realizar la operación");
                    break; 
                default:
                    respuesta.setMensaje("Lo sentimos hay problemas para verificar sus credenciales en este momento, por favor ingrese sus credenciales");
            }
        }
        return respuesta;
    }
}


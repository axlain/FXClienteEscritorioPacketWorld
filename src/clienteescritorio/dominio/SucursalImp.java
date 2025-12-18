package clienteescritorio.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.pojo.Sucursal;
import clienteescritorio.utilidad.Constantes;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SucursalImp {
    
    public static HashMap<String, Object> obtenerTodas(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "sucursal/obtener-todos";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Sucursal>>(){}.getType();
            List<Sucursal> sucursales = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, sucursales);
        }else{
            respuesta.put(Constantes.KEY_ERROR, true);
            switch(respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.put(Constantes.KEY_MENSAJE, Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.put(Constantes.KEY_MENSAJE, Constantes.MSJ_ERROR_PETICION);
                    break;
                default:
                    respuesta.put(Constantes.KEY_MENSAJE, "Lo sentimos, estamos teniendo problemas para obtener las sucursales en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static Respuesta registrar(Sucursal sucursal){
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "sucursal/registrar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(sucursal);
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(URL, Constantes.PETICION_POST, parametrosJSON, Constantes.APPLICATION_JSON);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
           respuesta = gson.fromJson(respuestaAPI.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            switch(respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_PETICION);
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    respuesta.setMensaje("Campos en formato incorrecto, por favor verifica toda la información enviada");
                    break;
                default:
                    respuesta.setMensaje("Lo sentimos, estamos teniendo problemas para obtener la información en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static Respuesta editar(Sucursal sucursal){
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "sucursal/editar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(sucursal);
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(URL, Constantes.PETICION_PUT, parametrosJSON, Constantes.APPLICATION_JSON);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            respuesta = gson.fromJson(respuestaAPI.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            switch(respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_PETICION);
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    respuesta.setMensaje("Campos en formato incorrecto, por favor verifica toda la información enviada");
                    break; 
                default:
                    respuesta.setMensaje("Lo sentimos, estamos teniendo problemas para obtener la información en este momento, por favor inténtelo en otro momento.");   
            }
        }
        return respuesta; 
    }
    
   public static Respuesta eliminar(int idSucursal){
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "sucursal/dar-de-baja/" + idSucursal;
        Gson gson = new Gson();

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionSinBody(
                URL,
                Constantes.PETICION_PUT
        );

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            respuesta = gson.fromJson(respuestaAPI.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            switch(respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_PETICION);
                    break;
                default:
                    respuesta.setMensaje("No fue posible dar de baja la sucursal en este momento.");
            }
        }
        return respuesta;
    }

}
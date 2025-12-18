package clienteescritorio.dominio;

import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.pojo.Unidad;
import clienteescritorio.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class UnidadImp {
    public static HashMap<String, Object> obtenerTodos(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "unidad/obtener-todos";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Unidad>>(){}.getType();
            List<Unidad> unidades = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put("unidades", unidades);
        }else{
            respuesta.put(Constantes.KEY_ERROR, true);
            switch(respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.put(Constantes.KEY_MENSAJE,Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.put(Constantes.KEY_MENSAJE,Constantes.MSJ_ERROR_PETICION);
                    break;
                default:
                    respuesta.put(Constantes.KEY_MENSAJE, "Lo sentimos, Estamos teniendo problemas para verificar sus obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static Respuesta registrar(Unidad unidad){
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "unidad/registrar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(unidad);
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
                    respuesta.setMensaje("Campos en formato incorrecto, por favor verifica toda la version enviada");
                    break;
                default:
                    respuesta.setMensaje("Lo sentimos, Estamos teniendo problemas para obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static Respuesta editar(Unidad unidad){
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "unidad/editar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(unidad);
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
                    respuesta.setMensaje("Campos en formato incorrecto, por favor verifica toda la version enviada");
                    break; 
                default:
                    respuesta.setMensaje("Lo sentimos, Estamos teniendo problemas para obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        return respuesta; 
    }
    
    public static Respuesta eliminar(int idUnidad, String motivoBaja){
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "unidad/dar-de-baja";

        Unidad unidad = new Unidad();
        unidad.setIdUnidad(idUnidad);
        unidad.setMotivoBaja(motivoBaja);

        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(unidad);

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(
                URL, Constantes.PETICION_PUT, parametrosJSON, Constantes.APPLICATION_JSON
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
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    respuesta.setMensaje("Campos en formato incorrecto.");
                    break;
                default:
                    respuesta.setMensaje(Constantes.MSJ_ERROR_PETICION);
            }
        }
        return respuesta;
    }
    
    public static HashMap<String, Object> buscar(String filtro){
        HashMap<String, Object> respuesta = new HashMap<>();
        String URL = Constantes.URL_WS + "unidad/buscar?filtro=" + filtro;

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Unidad>>(){}.getType();
            List<Unidad> unidades = gson.fromJson(
                    respuestaAPI.getContenido(),
                    tipoLista
            );

            respuesta.put("error", false);
            respuesta.put("unidades", unidades);
        } else {
            respuesta.put("error", true);
            switch (respuestaAPI.getCodigo()){
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.put("mensaje", Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.put("mensaje", Constantes.MSJ_ERROR_PETICION);
                    break;
                default:
                    respuesta.put(
                            "mensaje",
                            "No fue posible realizar la búsqueda de unidades."
                    );
            }
        }
        return respuesta;
    }

}

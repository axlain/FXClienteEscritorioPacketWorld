package clienteescritorio.dominio;

import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.RSDatosCodigoPostal;
import clienteescritorio.pojo.Colonia;
import clienteescritorio.pojo.Estado;
import clienteescritorio.pojo.EstatusEnvio;
import clienteescritorio.pojo.Municipio;
import clienteescritorio.pojo.Pais;
import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.pojo.Rol;
import clienteescritorio.pojo.TipoUnidad;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CatalogoImp {
    public static HashMap<String, Object> obtenerRoles(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "catalogo/obtener-roles";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Rol>>(){}.getType();
            List<Rol> roles = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, roles);
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
    
    public static HashMap<String, Object> obtenerTipoUnidades(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "catalogo/obtener-tipo-de-unidades";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<TipoUnidad>>(){}.getType();
            List<TipoUnidad> tipoDeUnidades = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, tipoDeUnidades);
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
                    respuesta.put(Constantes.KEY_MENSAJE, 
                            "Lo sentimos, Estamos teniendo problemas para verificar sus obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static HashMap<String, Object> obtenerPais(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "catalogo/obtener-paises";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Pais>>(){}.getType();
            List<Pais> paises = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, paises);
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
                    respuesta.put(Constantes.KEY_MENSAJE, 
                            "Lo sentimos, Estamos teniendo problemas para verificar sus obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static HashMap<String, Object> obtenerEstado(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "catalogo/obtener-estados";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Estado>>(){}.getType();
            List<Estado> estados = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, estados);
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
                    respuesta.put(Constantes.KEY_MENSAJE, 
                            "Lo sentimos, Estamos teniendo problemas para verificar sus obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static HashMap<String, Object> obtenerMunicipio(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "catalogo/obtener-municipios";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Municipio>>(){}.getType();
            List<Municipio> municipios = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, municipios);
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
                    respuesta.put(Constantes.KEY_MENSAJE, 
                            "Lo sentimos, Estamos teniendo problemas para verificar sus obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static HashMap<String, Object> obtenerColonia(){
        HashMap<String, Object> respuesta = new LinkedHashMap();
        String URL = Constantes.URL_WS + "catalogo/obtener-colonias";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);
        
        if(respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK){
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Colonia>>(){}.getType();
            List<Colonia> colonias = gson.fromJson(respuestaAPI.getContenido(), tipoLista);
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, colonias);
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
                    respuesta.put(Constantes.KEY_MENSAJE, 
                            "Lo sentimos, Estamos teniendo problemas para verificar sus obtener la informacion en este momento, por favor inténtelo en otro momento.");   
            }
        }
        
        return respuesta;
    }
    
    public static HashMap<String, Object> obtenerDatosCP(String codigoPostal) {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String URL = Constantes.URL_WS + "direccion/obtener-datos-por-cp/" + codigoPostal;

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        // Si no fue 200, tratamos como error normal
        if (respuestaAPI.getCodigo() != HttpURLConnection.HTTP_OK) {
            respuesta.put(Constantes.KEY_ERROR, true);

            String msg = "No fue posible consultar el CP en este momento. Intenta más tarde.";
            if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_NOT_FOUND
                    || respuestaAPI.getCodigo() == HttpURLConnection.HTTP_NO_CONTENT) {
                msg = "No se encontró información para el CP: " + codigoPostal;
            } else if (respuestaAPI.getCodigo() == Constantes.ERROR_MALFORMED_URL) {
                msg = Constantes.MSJ_ERROR_URL;
            } else if (respuestaAPI.getCodigo() == Constantes.ERROR_PETICION) {
                msg = Constantes.MSJ_ERROR_PETICION;
            }

            respuesta.put(Constantes.KEY_MENSAJE, msg);
            return respuesta;
        }

        // ---- Aquí sí fue 200 ----
        String contenido = respuestaAPI.getContenido();

        if (contenido == null || contenido.trim().isEmpty()) {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(Constantes.KEY_MENSAJE, "Respuesta vacía del servidor.");
            return respuesta;
        }

        try {
            JsonObject json = new JsonParser().parse(contenido).getAsJsonObject();

            // ✅ Si el JSON trae "error": true, regresar como error (aunque sea 200)
            if (json.has("error") && json.get("error").getAsBoolean()) {
                respuesta.put(Constantes.KEY_ERROR, true);

                String msg = (json.has("mensaje") && !json.get("mensaje").isJsonNull())
                        ? json.get("mensaje").getAsString()
                        : "No se encontró información para el CP: " + codigoPostal;

                respuesta.put(Constantes.KEY_MENSAJE, msg);
                return respuesta;
            }

            // ✅ Caso éxito real: parsear datos
            Gson gson = new Gson();
            RSDatosCodigoPostal datos = gson.fromJson(contenido, RSDatosCodigoPostal.class);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_OBJETO, datos);
            return respuesta;

        } catch (Exception ex) {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(Constantes.KEY_MENSAJE, "No se pudo interpretar la respuesta del servidor.");
            return respuesta;
        }
    }


    public static HashMap<String, Object> obtenerColoniasPorCP(String codigoPostal) {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String URL = Constantes.URL_WS + "direccion/obtener-colonias-por-cp/" + codigoPostal;

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Colonia>>() {}.getType();
            List<Colonia> colonias = gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, colonias);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            switch (respuestaAPI.getCodigo()) {
                case HttpURLConnection.HTTP_NOT_FOUND:
                case HttpURLConnection.HTTP_NO_CONTENT:
                    respuesta.put(Constantes.KEY_MENSAJE, "No se encontraron colonias para el CP: " + codigoPostal);
                    break;
                case Constantes.ERROR_MALFORMED_URL:
                    respuesta.put(Constantes.KEY_MENSAJE, Constantes.MSJ_ERROR_URL);
                    break;
                case Constantes.ERROR_PETICION:
                    respuesta.put(Constantes.KEY_MENSAJE, Constantes.MSJ_ERROR_PETICION);
                    break;
                default:
                    respuesta.put(Constantes.KEY_MENSAJE,
                            "No fue posible obtener colonias en este momento. Intenta más tarde.");
            }
        }

        return respuesta;
    }

    
    public static HashMap<String, Object> obtenerCatalogoEstatus() {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String URL = Constantes.URL_WS + "catalogo/obtener-estatus-envio"; 

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<EstatusEnvio>>() {}.getType();
            List<EstatusEnvio> lista = gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, lista);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(
                    Constantes.KEY_MENSAJE,
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "No fue posible obtener el catálogo de estatus."
                    )
            );
        }

        return respuesta;
    }

}

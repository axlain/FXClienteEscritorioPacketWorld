package clienteescritorio.dominio;

import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.pojo.Envio;
import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class EnvioImp {
    public static HashMap<String, Object> obtenerTodos() {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String URL = Constantes.URL_WS + "envio/obtener-todos";

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Envio>>() {}.getType();
            List<Envio> envios =
                    gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, envios);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(
                    Constantes.KEY_MENSAJE,
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "Lo sentimos, no fue posible obtener los envíos en este momento."
                    )
            );
        }

        return respuesta;
    }
    
    public static HashMap<String, Object> buscarPorGuia(String numeroGuia) {
        HashMap<String, Object> respuesta = new HashMap<>();
        String URL = Constantes.URL_WS + "envio/consultar/" + numeroGuia;

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();

            Type tipoLista = new TypeToken<List<Envio>>() {}.getType();
            List<Envio> envios =
                    gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, envios);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(
                    Constantes.KEY_MENSAJE,
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "No fue posible realizar la búsqueda del envío."
                    )
            );
        }

        return respuesta;
    }
    public static Respuesta registrar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "envio/registrar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(envio);

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(
                URL,
                Constantes.PETICION_POST,
                parametrosJSON,
                Constantes.APPLICATION_JSON
        );

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            respuesta = gson.fromJson(respuestaAPI.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            respuesta.setMensaje(
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "No fue posible registrar el envío en este momento."
                    )
            );
        }

        return respuesta;
    }
    public static Respuesta editar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "envio/actualizar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(envio);

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(
                URL,
                Constantes.PETICION_PUT,
                parametrosJSON,
                Constantes.APPLICATION_JSON
        );

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            respuesta = gson.fromJson(respuestaAPI.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            respuesta.setMensaje(
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "No fue posible editar la información del envío."
                    )
            );
        }

        return respuesta;
    }
}

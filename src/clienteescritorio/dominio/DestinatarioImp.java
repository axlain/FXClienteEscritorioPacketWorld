package clienteescritorio.dominio;

import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.pojo.Destinatario;
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

public class DestinatarioImp {
    public static HashMap<String, Object> obtenerTodos() {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String URL = Constantes.URL_WS + "destinatario/obtener-todos";

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Destinatario>>() {}.getType();
            List<Destinatario> destinatarios =
                    gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, destinatarios);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(
                    Constantes.KEY_MENSAJE,
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "Lo sentimos, no fue posible obtener los destinatarios en este momento."
                    )
            );
        }

        return respuesta;
    }
    
    public static Respuesta registrar(Destinatario destinatario) {
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "destinatario/registrar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(destinatario);

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
                            "No fue posible registrar el cliente en este momento."
                    )
            );
        }

        return respuesta;
    }
}

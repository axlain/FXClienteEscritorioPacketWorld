package clienteescritorio.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritorio.conexion.ConexionAPI;
import clienteescritorio.dto.Respuesta;
import clienteescritorio.pojo.Cliente;
import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ClienteImp {

    public static HashMap<String, Object> obtenerTodos() {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String URL = Constantes.URL_WS + "cliente/obtener-todos";
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Cliente>>(){}.getType();
            List<Cliente> clientes =
                    gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, clientes);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(
                    Constantes.KEY_MENSAJE,
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "Lo sentimos, no fue posible obtener los clientes en este momento."
                    )
            );
        }

        return respuesta;
    }

    public static HashMap<String, Object> buscar(String filtro) {
        HashMap<String, Object> respuesta = new HashMap<>();
        String URL = Constantes.URL_WS + "cliente/buscar?filtro=" + filtro;
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionGET(URL);

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Cliente>>(){}.getType();
            List<Cliente> clientes =
                    gson.fromJson(respuestaAPI.getContenido(), tipoLista);

            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, clientes);
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(
                    Constantes.KEY_MENSAJE,
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "No fue posible realizar la búsqueda del cliente."
                    )
            );
        }

        return respuesta;
    }

    public static Respuesta registrar(Cliente cliente) {
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "cliente/registrar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(cliente);

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

    public static Respuesta editar(Cliente cliente) {
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "cliente/editar";
        Gson gson = new Gson();
        String parametrosJSON = gson.toJson(cliente);

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
                            "No fue posible editar la información del cliente."
                    )
            );
        }

        return respuesta;
    }

    public static Respuesta eliminar(int idCliente) {
        Respuesta respuesta = new Respuesta();
        String URL = Constantes.URL_WS + "cliente/eliminar/" + idCliente;
        Gson gson = new Gson();

        RespuestaHTTP respuestaAPI = ConexionAPI.peticionSinBody(
                URL,
                Constantes.PETICION_DELETE
        );

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            respuesta = gson.fromJson(respuestaAPI.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            respuesta.setMensaje(
                    Utilidades.obtenerMensajeErrorHTTP(
                            respuestaAPI.getCodigo(),
                            "No fue posible eliminar el cliente en este momento."
                    )
            );
        }

        return respuesta;
    }
}

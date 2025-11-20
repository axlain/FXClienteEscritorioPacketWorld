/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clienteescritorio.conexion;

import clienteescritorio.pojo.RespuestaHTTP;
import clienteescritorio.utilidad.Constantes;
import clienteescritorio.utilidad.Utilidades;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author ferro
 */
public class ConexionAPI {
     public static RespuestaHTTP peticionBody(String URL, String metodoHTTP, String parametros, String contentType){
        RespuestaHTTP respuesta = new RespuestaHTTP();
        try{
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection)urlWS.openConnection();
            conexionHTTP.setRequestMethod(metodoHTTP);
            conexionHTTP.setRequestProperty("Content-Type", contentType);
            conexionHTTP.setDoOutput(true);
            OutputStream os = conexionHTTP.getOutputStream();
            os.write(parametros.getBytes());
            os.flush();
            os.close();
            int codigo = conexionHTTP.getResponseCode();
            
            if (codigo == HttpURLConnection.HTTP_OK){
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            }
            respuesta.setCodigo(codigo);
        } catch (MalformedURLException e){
            respuesta.setCodigo(Constantes.ERROR_MALFORMED_URL);
            respuesta.setContenido(e.getMessage());
        } catch (IOException e) {
            respuesta.setCodigo(Constantes.ERROR_PETICION);
            respuesta.setContenido(e.getMessage());
        }
        return respuesta; 
    }
    
    public static RespuestaHTTP peticionSinBody(String URL, String metodoHTTP){
        RespuestaHTTP res = new RespuestaHTTP(); 
        try{
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection)urlWS.openConnection(); 
            conexionHTTP.setRequestMethod(metodoHTTP);
            int codigo = conexionHTTP.getResponseCode(); 
            //codigo == 200 
            if(codigo ==HttpURLConnection.HTTP_OK){
                res.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            }
            res.setCodigo(codigo);
        }catch(MalformedURLException e){
            res.setCodigo(Constantes.ERROR_PETICION);
            res.setContenido(e.getMessage());
        }catch(IOException e){
            res.setCodigo(Constantes.ERROR_MALFORMED_URL);
            res.setContenido(e.getMessage());
        }
        return res; 
    }
    
    public static RespuestaHTTP peticionGET(String URL ){
        RespuestaHTTP res = new RespuestaHTTP(); 
        try{
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection)urlWS.openConnection(); 
            int codigo = conexionHTTP.getResponseCode(); 
            //codigo == 200 
            if(codigo ==HttpURLConnection.HTTP_OK){
                res.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            }
            res.setCodigo(codigo);
        }catch(MalformedURLException e){
            res.setCodigo(Constantes.ERROR_PETICION);
            res.setContenido(e.getMessage());
        }catch(IOException e){
            res.setCodigo(Constantes.ERROR_MALFORMED_URL);
            res.setContenido(e.getMessage());
        }
        return res; 
    }
}

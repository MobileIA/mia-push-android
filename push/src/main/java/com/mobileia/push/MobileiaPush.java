package com.mobileia.push;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobileia.authentication.MobileiaAuth;
import com.mobileia.authentication.entity.User;
import com.mobileia.core.Mobileia;
import com.mobileia.core.rest.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by matiascamiletti on 18/10/17.
 */

public class MobileiaPush {
    /**
     * Alamcena la URL del servicio
     */
    public static final String BASE_URL = "http://push.mobileia.com:8080/";
    /**
     * Variable del nombre del evento para enviar
     */
    public static final String EVENT_MIAPUSH = "miapush_event";
    /**
     * Almacena la instancia del socket
     */
    protected Socket mSocket;
    /**
     * Almacena el contexto
     */
    protected Context mContext;
    /**
     * Servicio para convertir los datos recibidos a Objetos
     */
    protected JsonParser mParser = new JsonParser();

    /**
     * Iniciamos conexion del socket
     * @param context
     */
    public MobileiaPush init(Context context){
        // Guardamos el contexto
        mContext = context;
        // Iniciamos el socket
        try {
            mSocket = IO.socket(BASE_URL, getOptions());
            mSocket.connect();

            /*mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    System.out.println("resultado se conecto!!!!!");
                    System.out.println("Respuesta connect: " + args);
                    if(args.length > 0){
                        System.out.println("Respuesta connect: " + args[0]);
                    }
                    if(args != null){
                        System.out.println("Respuesta connect: " + args.toString());
                    }
                    try {
                        JSONObject obj = (JSONObject)args[0];
                        System.out.println("Respuesta connect: " + obj);
                    }catch (Exception e){
                        System.out.println("Respuesta connect: error al parsear");
                    }
                }

            });*/
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Devolver instancia
        return this;
    }

    /**
     * Registrarse a un evento
     * @param event
     * @param callback
     */
    public void on(String event, final Callback callback){
        // Asignamos el evento
        mSocket.on(event, new Emitter.Listener() {
            @Override
            public void call(Object... args) {


                callback.call(args[0]);
            }
        });
    }

    /**
     * Enviar un evento al socket sin necesidad de parametros
     * @param event
     * @param callback
     */
    public void emit(String event, Callback callback){
        emit(event, new JsonObject(), callback);
    }
    /**
     * Enviar un evento al socket sin necesidad de respuesta
     * @param event
     * @param message
     */
    public void emit(String event, JSONObject message){
        emit(event, message, null);
    }

    /**
     * Enviar un evento con el formato mas comun de JSON
     * @param event
     * @param message
     * @param callback
     */
    public void emit(String event, JsonObject message, Callback callback){
        // Convertimos el json al formato valido
        JSONObject params = null;
        try {
            params = new JSONObject(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        emit(event, params, callback);
    }
    /**
     * Enviar un evento al socket
     * @param event
     * @param message
     * @param callback
     */
    public void emit(String event, JSONObject message, final Callback callback){
        try {
            // Creamos objeto base para enviar
            final JSONObject params = new JSONObject();
            params.put("event", event);
            if(message != null){
                params.put("message", message);
            }
            // Enviamos llamada al socket
            mSocket.emit(EVENT_MIAPUSH, params, new Ack() {
                @Override
                public void call(Object... args) {
                    // Verificamos si tiene elementos recibidos
                    if(args.length == 0){
                        return;
                    }
                    // verificamos si se envio un callback
                    if(callback == null){
                        return;
                    }
                    // Verificamos si el argumento es un JSON
                    if(args[0] instanceof JSONObject || args[0] instanceof JSONArray){
                        // Llamar al call
                        callback.call(mParser.parse(args[0].toString()));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Desconectar del socket
     */
    public void disconnect(){
        mSocket.disconnect();
    }

    /**
     * Obtiene la configuración del socket
     * @return
     */
    protected IO.Options getOptions(){
        // Creamos objeto de opciones
        IO.Options opts = new IO.Options();
        // Si se fuerza a conectar
        //opts.forceNew = true;
        // Si permite reconexion
        opts.reconnection = true;
        // Agregar los parametros obligatorios
        opts.query = "appId=" + Mobileia.getInstance().getAppId();
        // Obtener Usuario logueado
        User user = MobileiaAuth.getInstance(mContext).getCurrentUser();
        // Verificar si hay un usuario logueado
        if(user != null){
            // Agregamos a los parametros el AccessToken
            //opts.query = opts.query + "&accessToken=" + user.getAccessToken();
            //opts.query = opts.query + "&accessToken=7c62bb699d03e1484db378995ae550e69163c158";
        }
        opts.query = opts.query + "&accessToken=7c62bb699d03e1484db378995ae550e69163c158";

        return opts;
    }

    /**
     * Interface para comunicarse con el socket
     */
    public interface Callback<T>{
        void call(T obj);
    }
}

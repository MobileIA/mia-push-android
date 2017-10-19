package com.mobileia.push.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mobileia.push.MobileiaPush;

public class MainActivity extends AppCompatActivity {

    protected MobileiaPush socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Iniciar socket
        socket = new MobileiaPush().init(this);
    }

    public void onClick(View v){

        // Enviar peticion de prueba
        JsonObject params = new JsonObject();
        params.addProperty("tournament_id", 1);
        socket.emit("ranking_top", params, new MobileiaPush.Callback<JsonArray>() {
            @Override
            public void call(JsonArray obj) {
                System.out.println("Respuersta3: " + obj);
                /*for(JsonElement ranking : obj){
                    System.out.println("Respuersta3: " + ranking);
                }*/
                /*for(int i = 0; i < obj.length(); i++){
                    try {
                        JSONObject ranking = (JsonObject) obj.get(i);
                        System.out.println("Respuersta3: " + ranking);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }*/

            }
        });
    }
}

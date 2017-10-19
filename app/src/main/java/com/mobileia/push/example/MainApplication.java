package com.mobileia.push.example;

import android.support.multidex.MultiDexApplication;

import com.mobileia.core.Mobileia;

/**
 * Created by matiascamiletti on 18/10/17.
 */

public class MainApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // Configurar Mobileia Lab
        Mobileia.getInstance().setAppId(11);
        // Configurar token del dispositivo
        //Mobileia.getInstance().setDeviceToken(FirebaseInstanceId.getInstance().getToken());
    }
}

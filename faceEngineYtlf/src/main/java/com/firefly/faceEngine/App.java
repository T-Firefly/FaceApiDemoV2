package com.firefly.faceEngine;

import android.app.Application;
import android.content.Context;

import com.firefly.faceEngine.utils.Tools;
import com.firefly.faceEngine.dblib.DBManager;

public class App extends Application{
    private DBManager dbManager;
    private static Context context;
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        instance = this;
        Tools.init(this);
        initDBManager();
    }

    private void initDBManager(){
        if(dbManager == null){
            dbManager = new DBManager(context);
        }
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public static Context getContext(){
        return context;
    }

    public static App getInstance() {
        return instance;
    }
}

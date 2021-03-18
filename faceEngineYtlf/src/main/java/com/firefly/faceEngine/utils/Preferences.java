package com.firefly.faceEngine.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by firefly on 2016/12/6.
 */

public class Preferences {
    private Context mContext;
    private String mName;
    public static final String SETTING = "Setting";
    public Preferences(Context context, String name) {
        this.mContext = context;
        this.mName = name;
    }
    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
    }
    public void setCameraId(int cameraId){
        getSharedPreferences().edit().putInt("cameraId", cameraId).apply();
    }
    public int getCameraId(){
        return getSharedPreferences().getInt("cameraId", 0);
    }
}

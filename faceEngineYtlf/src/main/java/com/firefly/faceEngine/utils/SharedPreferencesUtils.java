package com.firefly.faceEngine.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by x on 17-10-13.
 */

public class SharedPreferencesUtils {
    private static final String FILE_NAME = "share_date";

    public static void setParam(Context context , String key, Object object){

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(object instanceof String){
            editor.putString(key, (String)object);
        }
        else if(object instanceof Integer){
            editor.putInt(key, (Integer)object);
        }
        else if(object instanceof Boolean){
            editor.putBoolean(key, (Boolean)object);
        }
        else if(object instanceof Float){
            editor.putFloat(key, (Float)object);
        }
        else if(object instanceof Long){
            editor.putLong(key, (Long)object);
        }

        editor.commit();
    }

    public static Object getParam(Context context , String key, Object defaultObject){

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if(defaultObject instanceof String){
            return sp.getString(key, (String)defaultObject);
        }
        else if(defaultObject instanceof Integer){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if(defaultObject instanceof Boolean){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if(defaultObject instanceof Float){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if(defaultObject instanceof Long){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }

    public static <T> T getValue(String key, T object) {
        return (T) getParam(Tools.getApp(), key, object);
    }

    public static void setValue(String key, Object object) {
        setParam(Tools.getApp(), key, object);
    }

}

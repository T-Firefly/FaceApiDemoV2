package com.firefly.faceEngine.utils;


import com.firefly.faceEngine.App;

public class SPUtil {
    private static final String recognition_overturn_rgbcamera = "recognition_overturn_rgbcamera";
    private static final String recognition_overturn_ircamera = "recognition_overturn_ircamera";
    private static final String face_frame_mirror = "face_frame_mirror";
    private static final String face_frame_reverse = "face_frame_reverse";
    private static final String screen_rotate_rgbcamera = "screen_rotate_rgbcamera";
    private static final String screen_rotate_ircamera = "screen_rotate_ircamera";

    public static void writeCamera1(boolean isOverturn){
        SharedPreferencesUtils.setParam(App.getContext(), recognition_overturn_rgbcamera,isOverturn);
    }

    public static void writeCamera2(boolean isOverturn){
        SharedPreferencesUtils.setParam(App.getContext(), recognition_overturn_ircamera,isOverturn);
    }

    public static void writeFaceFrameMirror(boolean isMirror){
        SharedPreferencesUtils.setParam(App.getContext(), face_frame_mirror,isMirror);
    }

    public static void writeFaceFrameReverse(boolean isReverse){
        SharedPreferencesUtils.setParam(App.getContext(), face_frame_reverse,isReverse);
    }

    public static boolean readCameraRgb(){
        return (boolean) SharedPreferencesUtils.getParam(App.getContext(), recognition_overturn_rgbcamera,true);
    }

    public static boolean readCameraIr(){
        return (boolean) SharedPreferencesUtils.getParam(App.getContext(), recognition_overturn_ircamera,false);
    }

    public static boolean readFaceFrameMirror(){
        return (boolean) SharedPreferencesUtils.getParam(App.getContext(), face_frame_mirror,false);
    }

    public static boolean readFaceFrameReverse(){
        return (boolean) SharedPreferencesUtils.getParam(App.getContext(), face_frame_reverse,false);
    }

    public static void writeScreenRotateRgbCamera(int rotate){
        SharedPreferencesUtils.setParam(App.getContext(), screen_rotate_rgbcamera,rotate);
    }

    public static int readScreenRotateRgbCamera(){
        return (int) SharedPreferencesUtils.getParam(App.getContext(), screen_rotate_rgbcamera, 0);
    }

    public static void writeScreenRotateIrCamera(int rotate){
        SharedPreferencesUtils.setParam(App.getContext(), screen_rotate_ircamera,rotate);
    }

    public static int readScreenRotateIrCamera(){
        return (int) SharedPreferencesUtils.getParam(App.getContext(), screen_rotate_ircamera, 0);
    }
}

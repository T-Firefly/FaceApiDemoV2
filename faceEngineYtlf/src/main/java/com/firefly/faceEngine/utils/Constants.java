package com.firefly.faceEngine.utils;

public class Constants {
    public static int select_screen_rotate_rgbcamera = 0;
    public static int select_screen_rotate_ircamera = 0;
    /**
     * -1代表Auto
     */
    public static final int[] SCREEN_ROTATE = {-1, 0, 90, 180, 270};

    //public static int select_recognition_orientation = 0;
    //public static final int[] RECOGNITION_ORIENTATION = {-1, 0, 90, 180, 270};

    /**
     * Camera1识别方向翻转180
     */
    public static boolean recognition_overturn_rgbcamera = false;
    /**
     * Camera2识别方向翻转180
     */
    public static boolean recognition_overturn_ircamera = false;
    /**
     * 人脸框左右镜像
     */
    public static boolean face_frame_mirror = false;
    /**
     * RectF.dRectLeft 与 RectF.dRectRight 颠倒
     */
    public static boolean face_frame_reverse = false;

}

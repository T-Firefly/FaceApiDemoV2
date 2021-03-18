package com.firefly.faceEngine.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;

import com.intellif.arctern.base.ArcternRect;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 人脸相关处理
 * Created by firefly on 2017/10/17.
 */
public class Tools extends com.firefly.fireflyapidemo.Tools{
    private static AtomicInteger atomicInteger = new AtomicInteger(1);
    //    /** 在bitmap上画图（人脸框） */
    public static Bitmap drawBitmap(ArcternRect faceRect, Bitmap previewBitmap) {
        if (previewBitmap == null)
            return null;
        int w = previewBitmap.getWidth();
        int h = previewBitmap.getHeight();
        Bitmap newb = previewBitmap.copy(Bitmap.Config.ARGB_8888, true);//创建一个新的位图
        // 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        paint.setStrokeWidth(4);
//        // 画人脸框
//        if (faceRectEntity.getFaceRect() != null && faceRectEntity.getFaceNum() > 0) {
        paint.setColor(Color.GREEN);
        paint.setTextSize(32);
        paint.setStyle(Paint.Style.STROKE);
//            for (int i = 0; i < faceRectEntity.getFaceNum(); i++) {
        cv.drawRect(getRectF_left(faceRect), getRectF_top(faceRect), getRectF_right(faceRect), getRectF_bottom(faceRect), paint);
//            }
//        }
        cv.save();
        cv.restore();
        return newb;
    }
    public static Bitmap drawBitmap(ArcternRect[] faceRect, Bitmap previewBitmap) {
        if (previewBitmap == null)
            return null;
        int w = previewBitmap.getWidth();
        int h = previewBitmap.getHeight();
        Bitmap newb = previewBitmap.copy(Bitmap.Config.ARGB_8888, true);//创建一个新的位图
        // 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        paint.setStrokeWidth(4);
//        // 画人脸框
//        if (faceRectEntity.getFaceRect() != null && faceRectEntity.getFaceNum() > 0) {
        paint.setColor(Color.GREEN);
        paint.setTextSize(32);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < faceRect.length; i++) {
            cv.drawRect(getRectF_left(faceRect[i]), getRectF_top(faceRect[i]), getRectF_right(faceRect[i]), getRectF_bottom(faceRect[i]), paint);
        }
//        }
        cv.save();
        cv.restore();
        return newb;
    }

    /**
     * uri 转 bitmap
     * @param uri
     * @return
     */
    public static Bitmap getBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /**
     * uri 转 byte[]
     * @param uri
     * @return
     */
    public static byte[] getJpegData(Context context, Uri uri) {
        Bitmap bitmap = null;
        bitmap = getBitmap(context,uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    /**
     * 获得人脸比对的rgb图片数组
     * @param bitmap
     * @return
     */
    public static byte[] getRGBByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = convertColorToByte(pixels);
        return data;
    }
    /*
     * 像素数组转化为RGB数组
     */
    public static byte[] convertColorToByte(int color[]) {
        if (color == null) {
            return null;
        }
        byte[] data = new byte[color.length * 3];
        for (int i = 0; i < color.length; i++) {
            data[i * 3] = (byte) (color[i] & 0xff);
            data[i * 3 + 1] = (byte) (color[i] >> 8 & 0xff);
            data[i * 3 + 2] = (byte) (color[i]>> 16 & 0xff);
        }
        return data;
    }

    /**
     * @方法描述 Bitmap转RGB
     */
    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {

            pixels[i * 3] = rgba[i * 4];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B

        }

        return pixels;
    }

    public static ArcternRect getMaxFace(ArcternRect[] rects){
        int maxIndex = 0;
        int maxHeight = rects[0].height;
        for (int i = 1; i < rects.length; i++) {
            int height = rects[i].height;
            if (height > maxHeight) {
                maxHeight = height;
                maxIndex = i;
            }
        }
        return rects[maxIndex];
    }

    public static int getRectF_left(ArcternRect rect){
        return rect.x;
    }

    public static int getRectF_right(ArcternRect rect){
        return rect.x + rect.width;
    }

    public static int getRectF_top(ArcternRect rect){
        return rect.y;
    }

    public static int getRectF_bottom(ArcternRect rect){
        return rect.y + rect.height;
    }

    public static int getNextId(){
        return atomicInteger.get();
    }


}
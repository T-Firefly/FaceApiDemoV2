package com.firefly.faceEngine.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;

import com.intellif.YTLFFaceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.firefly.faceEngine.activity.ArcternMainActivity.FACE_PATH;

/**
 * Created by firefly on 2017/3/13.
 */

public class MatrixYuvUtils {
    // Orientation hysteresis amount used in rounding, in degrees
    private static final int ORIENTATION_HYSTERESIS = 5;

    private static final String TAG = "Util";

    /**
     * Gets the current display rotation in angles.
     *
     * @param activity
     * @return
     */
    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for
        // documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation,
                                     int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    public static void prepareMatrix2(Matrix matrix, boolean mirror, int displayOrientation,
                                      int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
//        matrix.postScale(viewWidth/1080f, viewHeight/1920f);
        matrix.postScale(viewWidth / viewHeight, viewHeight / viewWidth);
//        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist, 360 - dist);
            changeOrientation = (dist >= 45 + ORIENTATION_HYSTERESIS);
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }

    public static Camera.Size getOptimalPreviewSize(Activity currentActivity,
                                                    List<Camera.Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of preview surface. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size.
        Point point = getDefaultDisplaySize(currentActivity, new Point());
        int targetHeight = Math.min(point.x, point.y);
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            Log.w(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @SuppressWarnings("deprecation")
    private static Point getDefaultDisplaySize(Activity activity, Point size) {
        Display d = activity.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            d.getSize(size);
        } else {
            size.set(d.getWidth(), d.getHeight());
        }
        return size;
    }

    //90
    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    //180
    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;

        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    //270
    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = imageWidth - 1; x >= 0; x--) {
            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }// Rotate the U and V color components
        i = imageWidth * imageHeight;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;
            }
        }
        return yuv;
    }


    public static Bitmap dataToBitmap(byte[] data, int width, int height) {
        Bitmap jniBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21,
                jniBitmap.getWidth(), jniBitmap.getHeight(), null);
        Rect rectImage = new Rect(0, 0, jniBitmap.getWidth(), jniBitmap.getHeight());
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        if (!yuv.compressToJpeg(rectImage, 100, baout)) {
            Log.e("CreateBitmap", "compressToJpeg failed");
        }
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inPreferredConfig = Bitmap.Config.RGB_565;
        jniBitmap = BitmapFactory.decodeStream(
                new ByteArrayInputStream(baout.toByteArray()), null, bfo);
        return jniBitmap;
    }

    /**
     * @方法描述 将RGB字节数组转换成Bitmap，
     */
    public static Bitmap rgb2Bitmap(byte[] data, int width, int height) {
        int[] colors = convertByteToColor(data);    //取RGB值转换为int数组
        Log.e("color", "---------------" + colors.length);
        if (colors == null) {
            return null;
        }

        Bitmap bmp = Bitmap.createBitmap(colors, 0, width, width, height,
                Bitmap.Config.RGB_565);
        return bmp;
    }

    // 将纯RGB数据数组转化成int像素数组
    public static int[] convertByteToColor(byte[] data) {
        int size = data.length;
        if (size == 0) {
            return null;
        }

        int arg = 0;
        if (size % 3 != 0) {
            arg = 1;
        }

        // 一般RGB字节数组的长度应该是3的倍数，
        // 不排除有特殊情况，多余的RGB数据用黑色0XFF000000填充
        int[] color = new int[size / 3 + arg];
        int red, green, blue;
        int colorLen = color.length;
        if (arg == 0) {
            for (int i = 0; i < colorLen; ++i) {
                color[i] = (data[i * 3 + 2] << 16 & 0x00FF0000) |
                        (data[i * 3 + 1] << 8 & 0x0000FF00) |
                        (data[i * 3] & 0x000000FF) |
                        0xFF000000;
            }
        } else {
            for (int i = 0; i < colorLen - 1; ++i) {
                color[i] = (data[i * 3 + 2] << 16 & 0x00FF0000) |
                        (data[i * 3 + 1] << 8 & 0x0000FF00) |
                        (data[i * 3] & 0x000000FF) |
                        0xFF000000;
            }
            color[colorLen - 1] = 0xFF000000;
        }

        return color;
    }

    /**
     * ANDROID中数据转化
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static Bitmap yuvToBitmap(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }

    public static void saveArcternDataToImage(byte[] data, int width, int height) {
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            YuvImage yuv = new YuvImage(data, ImageFormat.NV21, bitmap.getWidth(), bitmap.getHeight(), null);
            Rect rectImage = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            byteArrayOutputStream = new ByteArrayOutputStream();
            if (!yuv.compressToJpeg(rectImage, 100, byteArrayOutputStream)) {
                Tools.toast("saveArcternDataToImage compressToJpeg failed");
                return;
            }

            //"/sdcard/firefly/ytlf_v2/"
            String path = YTLFFaceManager.getInstance().getYTIFFacthPath() + "img/";
            File file = new File(path + System.currentTimeMillis() + ".jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();

        } catch (Exception e) {
            Tools.printStackTrace(e);

        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                Tools.printStackTrace(e);
            }
        }
    }

    //nv21的镜像
    public static void mirrorForNv21(byte[] src, int w, int h) {
        int i;
        int index;
        byte temp;
        int a, b;
        //mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = src[a];
                src[a] = src[b];
                src[b] = temp;
                a++;
                b--;
            }
        }

        // mirror u and v
        index = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w;
            b = (i + 1) * w - 2;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;

                temp = src[a + index + 1];
                src[a + index + 1] = src[b + index + 1];
                src[b + index + 1] = temp;
                a+=2;
                b-=2;
            }
        }
    }

    //YUV420的镜像
    public static void mirrorForYUV420(byte[] src, int w, int h) {
        int i;
        int index;
        byte temp;
        int a, b;
        //mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = src[a];
                src[a] = src[b];
                src[b] = temp;
                a++;
                b--;
            }
        }
        //mirror u
        index = w * h;//U起始位置
        for (i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;
                a++;
                b--;
            }
        }
        //mirror v
        index = w * h / 4 * 5;//V起始位置
        for (i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;
                a++;
                b--;
            }
        }
    }
}

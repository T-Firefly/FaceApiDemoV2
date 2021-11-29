package com.firefly.fireflyapidemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tools {
    protected static Application app;
    protected static LoadingProgress loadingProgress;

    public static void init(Application application) {
        app = application;
    }

    public static void init(Activity activity) {
        if (app == null) {
            app = activity.getApplication();
        }
    }

    public static Application getApp() {
        return app;
    }


    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = getApp().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Throwable e) {
            Tools.printStackTrace(e);
            return false;
        }
    }

    public static boolean isNetWorkConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") boolean result = cm.getActiveNetworkInfo() != null;
            return result;
        } catch (Exception e) {
            printStackTrace(e);
            return false;
        }
    }

    public static void runOnUiThread(Runnable run) {
        new Handler(Looper.getMainLooper()).post(run);
    }

    public static void runOnUiThread(Runnable run, long delayMillis) {
        if (delayMillis < 100)
            runOnUiThread(run);
        else
            new Handler(Looper.getMainLooper()).postDelayed(run, delayMillis);
    }

    public static void toast(final Exception e) {
        if (e != null) {
            toast(e.getMessage());
        }
    }

    public static void toast(final CharSequence msg) {
        debugLog(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    Toast.makeText(getApp(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void toast(String format, Object... args) {
        toast(String.format(format, args));
    }

    public static void toast(int resId) {
        toast(getString(resId));
    }

    public static String getString(int id) {
        try {
            return getApp().getResources().getString(id);
        } catch (Throwable e) {
            Tools.printStackTrace("firefly_debug", e);
            return "";
        }
    }

    public static void printStackTrace(String tag, Throwable e) {
        Log.e(tag, e.getMessage(), e);
    }

    public static void printStackTrace(Object tag, Throwable e) {
        Log.e(tag.getClass().getSimpleName(), e.getMessage(), e);
    }

    public static void printStackTrace(Throwable e) {
        Log.e("firefly_debug", e.getMessage(), e);
    }

    public static void printStackTrace(String msg) {
        Log.e("firefly_debug", msg);
    }

    public static void debugLog(CharSequence msg) {
        String thread = "";
        if (isUIThread()) {
            thread = "isUIThread: ";
        }
        log_d("firefly_debug", thread + msg);
    }

    public static void debugLog(String format, Object... args) {
        debugLog(String.format(format, args));
    }

    public static void debugLog(int tag, String format, Object... args) {
        String thread = "";
        if (isUIThread()) {
            thread = "isUIThread: ";
        }
        log_d("firefly_debug"+tag, thread + String.format(format, args));
    }

    public static void debugLog(Bundle bundle) {
        try {
            StringBuilder sb = new StringBuilder();
            for (String key : bundle.keySet()) {
                Object obj = bundle.get(key);
                sb.append("\nkey:").append(key).append(", value:").append(obj);
            }
            debugLog(sb.toString());
        } catch (Throwable e) {
            printStackTrace(e);
        }
    }

    public static void debugLog(List<?> mList) {
        if (mList != null)
            debugLog("size=%s: %s", mList.size(), mList.toString());
        else
            debugLog("List == null");
    }

    public static void debugLog() {
        debugLog("============================================");
    }

    public static void log_d(String tag, String msg) {
        if (!isApkDebugable())
            return;
        int maxLength = 2001 - tag.length();
        while (msg.length() > maxLength) {
            Log.d(tag, msg.substring(0, maxLength));
            msg = msg.substring(maxLength);
        }
        Log.d(tag, msg);
    }

    public static void dismissLoadingProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingProgress != null)
                        loadingProgress.dismiss();
                } catch (Throwable e) {
                    printStackTrace(e);
                }
            }
        });
    }

    public static void dismissLoadingProgress(final int cancelableTime) {
        dismissLoadingProgress(cancelableTime, null);
    }

    public static void dismissLoadingProgress(final int cancelableTime, final Runnable run) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingProgress != null) {
                        loadingProgress.dismiss();
                    }
                    if (run != null) {
                        run.run();
                    }
                } catch (Throwable e) {
                    printStackTrace(e);
                }
            }
        }, cancelableTime);
    }

    public static void showLoadingProgress(Activity activity) {
        showLoadingProgress(activity, true);
    }

    public static void showLoadingProgress(final Activity activity, final boolean isCancelable) {
        showLoadingProgress(activity, isCancelable, 0);
    }

    public static void showLoadingProgress(final Activity activity, final int cancelableTime) {
        showLoadingProgress(activity, cancelableTime <= 0, cancelableTime);
    }

    public static void showLoadingProgressAutoDismiss(final Activity activity, final int cancelableTime) {
        showLoadingProgressAutoDismiss(activity, cancelableTime, null);
    }

    public static void showLoadingProgressAutoDismiss(final Activity activity, final int cancelableTime, Runnable run) {
        showLoadingProgress(activity, false);
        dismissLoadingProgress(cancelableTime, run);
    }

    public static void showLoadingProgressAutoDismiss(final Activity activity) {
        showLoadingProgressAutoDismiss(activity, 2000, null);
    }

    public static void showLoadingProgressAutoDismiss(final Activity activity, Runnable run) {
        showLoadingProgressAutoDismiss(activity, 2000, run);
    }

    public static void showLoadingProgress(final Activity activity, final boolean isCancelable, final int cancelableTime) {
        if (activity == null || isActivityDestroyed(activity)) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingProgress != null && loadingProgress.mDialog.isShowing() == true) {
                        return;
                    }

                    loadingProgress = new LoadingProgress(activity);
                    loadingProgress.mDialog.setCancelable(isCancelable && cancelableTime <= 0);
                    loadingProgress.mDialog.setCanceledOnTouchOutside(false);
                    loadingProgress.show();

                    if (cancelableTime > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingProgress.mDialog.setCancelable(true);
                            }
                        });
                    }

                } catch (Throwable e) {
                    printStackTrace(e);
                }
            }
        });
    }

    public static void showLoadingProgressText(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingProgress != null) {
                    loadingProgress.showText(txt);
                }
            }
        });
    }

    public static boolean isActivityDestroyed(Fragment fragment) {
        try {
            return fragment != null && fragment.isAdded() && isActivityDestroyed(fragment.getActivity());
        } catch (Throwable e) {
            Tools.printStackTrace(e);
            return true;
        }
    }

    public static boolean isActivityDestroyed(Activity activity) {
        try {
            if (activity == null || activity.isFinishing())
                return true;

            if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed())
                return true;

            return false;
        } catch (Throwable e) {
            Tools.printStackTrace(e);
            return true;
        }
    }

    public static boolean isUIThread() {
        try {
            return Looper.myLooper() == Looper.getMainLooper();
        } catch (Throwable e) {
            printStackTrace(e);
            return false;
        }
    }

    public static boolean isCameraCanUse(int cameraId) {
        boolean canUse = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(0);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            printStackTrace(e);
            canUse = false;
        }

        try {
            if (mCamera != null) {
                mCamera.release();
                canUse = true;
            }
        } catch (Exception e) {
            printStackTrace(e);
        }

        return canUse;
    }

    public static boolean isCameraCanUse(){
        return isCameraCanUse(Camera.CameraInfo.CAMERA_FACING_BACK) &&
                isCameraCanUse(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public static boolean isZh() {
        try {
            return getApp().getResources().getConfiguration()
                    .locale
                    .getLanguage()
                    .toLowerCase()
                    .endsWith("zh");
        } catch (Exception e) {
            printStackTrace(e);
            return true;
        }
    }

    /**
     * 水平翻转Bitmap，
     */
    public static Bitmap mirroringBitmap(Bitmap bmp) {
        try {
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            Canvas cv = new Canvas(newb);
            Matrix m = new Matrix();
            //m.postScale(1, -1);   //镜像垂直翻转
            m.postScale(-1, 1);   //镜像水平翻转
            //m.postRotate(-90);  //旋转-90度
            Bitmap new2 = Bitmap.createBitmap(bmp, 0, 0, w, h, m, true);
            cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w, h), null);
            return newb;
        } catch (Exception e) {
            printStackTrace(e);
            return bmp;
        }
    }

    // 将纯BGR数据数组转化成int像素数组
    public static int[] convertBGRByteToColor(byte[] data) {
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
                color[i] = (data[i * 3+ 2] << 16 & 0x00FF0000) |
                        (data[i * 3 + 1] << 8 & 0x0000FF00 ) |
                        (data[i * 3 ] & 0x000000FF ) |
                        0xFF000000;
            }
        } else {
            for (int i = 0; i < colorLen - 1; ++i) {
                color[i] = (data[i * 3+ 2] << 16 & 0x00FF0000) |
                        (data[i * 3 + 1] << 8 & 0x0000FF00 ) |
                        (data[i * 3 ] & 0x000000FF ) |
                        0xFF000000;
            }
            color[colorLen - 1] = 0xFF000000;
        }

        return color;
    }

    public static Bitmap drawPointOnBitmap(Bitmap bitmap, int[] landmark){
        Bitmap newbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        try {
            Canvas cv = new Canvas(newbitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);//设置非填充
            paint.setStrokeWidth(8);//笔宽5像素
            paint.setColor(Color.RED);//设置为红笔
            paint.setAntiAlias(true);//锯齿不显示
            paint.setDither(true);//设置图像抖动处理
            paint.setStrokeJoin(Paint.Join.ROUND);//设置图像的结合方式
            paint.setStrokeCap(Paint.Cap.ROUND);//设置画笔为圆形样式

            int splitSize = 2;//分割的块大小
            Object[] subAry = splitAry(landmark, splitSize);//分割后的子块数组
            for(Object obj: subAry){//打印输出结果
                int[] aryItem = (int[]) obj;
                Log.e("array",(aryItem[0] + ", "+aryItem[1]));
                cv.drawPoint(aryItem[0],aryItem[1],paint);
            }

            cv.save();
            cv.restore();
        } catch (Throwable e) {
            printStackTrace(e);
        }

        return newbitmap;
    }

    /**
     * splitAry方法<br>
     * @param ary 要分割的数组
     * @param subSize 分割的块大小
     * @return
     *
     */
    private static Object[] splitAry(int[] ary, int subSize) {
        int count = ary.length % subSize == 0 ? ary.length / subSize: ary.length / subSize + 1;

        List<List<Integer>> subAryList = new ArrayList<List<Integer>>();

        for (int i = 0; i < count; i++) {
            int index = i * subSize;
            List<Integer> list = new ArrayList<Integer>();
            int j = 0;
            while (j < subSize && index < ary.length) {
                list.add(ary[index++]);
                j++;
            }
            subAryList.add(list);
        }

        Object[] subAry = new Object[subAryList.size()];

        for(int i = 0; i < subAryList.size(); i++){
            List<Integer> subList = subAryList.get(i);
            int[] subAryItem = new int[subList.size()];
            for(int j = 0; j < subList.size(); j++){
                subAryItem[j] = subList.get(j).intValue();
            }
            subAry[i] = subAryItem;
        }

        return subAry;
    }

    /**
     * 将BRG字节数组转换成Bitmap，
     */
    public static Bitmap bgr2Bitmap(byte[] data, int width, int height) {
        int[] colors = convertBGRByteToColor(data);    //取RGB值转换为int数组
        if (colors == null) {
            return null;
        }

        Bitmap bmp = Bitmap.createBitmap(colors, 0, width, width, height,Bitmap.Config.RGB_565);
        return bmp;
    }

    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (Exception e) {
            printStackTrace(e);
        }
        return bitmap;
    }

    //保存bitmap本地图片
    public static boolean saveBitmap2Jpeg(Bitmap bitmap, String path) {
        if(bitmap == null || TextUtils.isEmpty(path)){
            return false;
        }

        OutputStream outputStream = null;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            file.createNewFile();

            outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            return true;

        } catch (Exception e) {
            printStackTrace(e);
            return false;

        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                printStackTrace(e);
            }
        }
    }

    public static String getTimeShort() {
        return new SimpleDateFormat("HH:mm:ss:SSS ").format(new Date());
    }

    public static void recycle(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    //是否横屏
    public static boolean isLandscape() {
        try {
            Configuration mConfiguration = getApp().getResources().getConfiguration(); //获取设置的配置信息
            return mConfiguration.orientation == mConfiguration.ORIENTATION_LANDSCAPE;
        } catch (Exception e) {
            printStackTrace(e);
            return true;
        }
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) getApp().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) getApp().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        return metrics.heightPixels;
    }

    public static float getScreenWHScale() {
        return 1.0f * getScreenWidth() / getScreenHeight();
    }
}

package com.firefly.faceEngine.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.firefly.faceEngine.view.LoadingProgress;

import java.io.File;
import java.util.List;

public class AppTools {
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
        log_d("firefly_debug", "" + msg);
    }

    public static void debugLog(String format, Object... args) {
        debugLog(String.format(format, args));
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
}

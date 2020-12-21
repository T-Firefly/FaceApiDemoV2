package com.firefly.faceEngine.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.firefly.fireflyapidemo.Tools;
import com.intellif.YTLFFaceManager;
import com.intellif.arctern.base.ArcternFeatureResult;
import com.intellif.arctern.base.ArcternImage;
import com.intellif.arctern.base.ArcternRect;
import com.intellif.arctern.base.ExtractCallBack;

public class Debug {

    // 提取特证值，用例，异步方式
    // path："/sdcard/test.png"
    public static void getFeatureAsync(String path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            YTLFFaceManager.getInstance().doFeature(bitmap, new ExtractCallBack() {
                @Override
                public void onExtractFeatureListener(ArcternImage arcternImage, byte[][] features, ArcternRect[] arcternRects) {
                    if (features.length > 0) {
                        Tools.debugLog("feature.length： " + features[0].length);
                    } else {
                        Tools.debugLog("feature is empty！！！");
                    }
                }
            });
        } catch (Exception e) {
            Tools.printStackTrace(e);
        }
    }

    // 提取特证值，用例，同步方式
    // path："/sdcard/test.png"
    public static void getFeature(String path) {
        Tools.debugLog("wait for ...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    ArcternFeatureResult arcternFeatureResult =  YTLFFaceManager.getInstance().doFeature(bitmap);
                    if (arcternFeatureResult.feature.length > 0) {
                        Tools.debugLog("feature.length： " + arcternFeatureResult.feature.length);
                    } else {
                        Tools.debugLog("feature is empty！！！");
                    }
                } catch (Exception e) {
                    Tools.printStackTrace(e);
                }
            }
        }).start();
    }
}

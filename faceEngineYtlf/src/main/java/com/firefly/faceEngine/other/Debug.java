package com.firefly.faceEngine.other;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.firefly.faceEngine.App;
import com.firefly.faceEngine.dblib.bean.Person;
import com.firefly.fireflyapidemo.Tools;
import com.intellif.YTLFFaceManager;
import com.intellif.arctern.base.ArcternFeatureResult;
import com.intellif.arctern.base.ArcternImage;
import com.intellif.arctern.base.ArcternRect;
import com.intellif.arctern.base.ExtractCallBack;

import java.io.File;
import java.util.List;

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

    /*
        Debug.doSearch(context, "/sdcard/firefly/图1.jpg");
        Tools.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Debug.doSearch(context, "/sdcard/firefly/图2.jpg");
            }
        }, 3000);
     */
    // 人脸识别
    public static void doSearch(Activity activity, String path) {
        if (!new File(path).exists()) {
            Tools.debugLog("doSearch %s file is no exist", path);
            return;
        }

        Tools.showLoadingProgress(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    ArcternFeatureResult arcternFeatureResult = YTLFFaceManager.getInstance().doFeature(bitmap);
                    if (arcternFeatureResult.feature.length > 0) {
                        long faceId = YTLFFaceManager.getInstance().doSearch(arcternFeatureResult.feature);
                        if (faceId > 0) {
                            List<Person> mPeople = App.getInstance().getDbManager().getPersonList();
                            for (Person person : mPeople) {
                                if (person.getId() == faceId) {
                                    Tools.debugLog("doSearch %s %s", path, person.toString2());
                                    return;
                                }
                            }
                        }

                        Tools.debugLog("doSearch %s 没有匹配的人", path);

                    } else {
                        Tools.debugLog("%s feature is empty！！！", path);
                    }
                } catch (Exception e) {
                    Tools.printStackTrace(e);
                } finally {
                    Tools.dismissLoadingProgress();
                }
            }
        }).start();
    }
}

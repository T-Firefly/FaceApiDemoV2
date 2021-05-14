package com.firefly.faceEngine.other;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.firefly.faceEngine.App;
import com.firefly.faceEngine.dblib.bean.Person;
import com.firefly.fireflyapidemo.Tools;
import com.intellif.YTLFFaceManager;
import com.intellif.arctern.base.ArcternAttrResult;
import com.intellif.arctern.base.ArcternAttribute;
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
    public static void getFeature(Activity activity, String path) {
        Tools.showLoadingProgress(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    ArcternFeatureResult arcternFeatureResult = YTLFFaceManager.getInstance().doFeature(bitmap);
                    Bitmap landmarksBitmap = com.firefly.faceEngine.utils.Tools.drawPointOnBitmap(bitmap, arcternFeatureResult.landmarks);
                    if (arcternFeatureResult.feature.length > 0) {
                        Tools.debugLog("feature.length： " + arcternFeatureResult.feature.length);
                    } else {
                        Tools.debugLog("feature is empty！！！");
                    }

                } catch (Exception e) {
                    Tools.printStackTrace(e);
                } finally {
                    Tools.dismissLoadingProgress();
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

    //人脸属性 同步方式
    public static void getFaceAttrs(Activity activity, String path) {
        if (!new File(path).exists()) {
            Tools.debugLog("doSearch %s file is no exist", path);
            return;
        }

        Tools.showLoadingProgress(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Tools.debugLog("%s -------------------", path);

                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    ArcternAttrResult arcternAttrResult = YTLFFaceManager.getInstance().getFaceAttrs(bitmap, ArcternAttrResult.ARCTERN_FACE_ATTR_MASK_ALL);
                    ArcternAttribute[] attributes = arcternAttrResult.arcternAttributes[0];

                    for (int i = 0; i < attributes.length; i++) {
                        ArcternAttribute item = attributes[i];
                        switch (i) {
                            case ArcternAttribute.ArcternFaceAttrTypeEnum.QUALITY: //人脸质量
                                Tools.debugLog("%s >>> 人脸质量 %s", i, item.toString());
                                break;

                            case ArcternAttribute.ArcternFaceAttrTypeEnum.FACE_MASK: //口罩
                                Tools.debugLog("%s >>> 口罩 %s", i, item.toString());
                                break;

                            default:
                                Tools.debugLog("%s >>> %s", i, item.toString());
                                break;
                        }
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

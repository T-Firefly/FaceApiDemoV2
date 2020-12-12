package com.firefly.faceEngine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firefly.arcterndemo.R;
import com.firefly.faceEngine.App;
import com.firefly.faceEngine.dblib.DBManager;
import com.firefly.faceEngine.dblib.bean.Person;
import com.firefly.faceEngine.other.FaceInfo;
import com.firefly.faceEngine.utils.Constants;
import com.firefly.faceEngine.utils.GlideImageLoader;
import com.firefly.faceEngine.utils.SPUtil;
import com.firefly.faceEngine.utils.Tools;
import com.intellif.YTLFFaceManager;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;

import java.util.List;


public class ArcternMainActivity extends BaseActivity {

    // 在线获取授权 API_KEY
    public final String API_KEY = "xrZEJz51qfiBI3FB";

    // 指定本地SD卡目录，用于存放models和license公钥等文件
    public static String FACE_PATH = "/sdcard/firefly/";

    // SDK
    private YTLFFaceManager YTLFFace = YTLFFaceManager.getInstance().initPath(FACE_PATH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_main);
        initSetting();
        //requestPermission();
        //findViewById(R.id.btn_detect).performClick();
        if (!Tools.isCameraCanUse()) {
            Tools.toast("Camera occupied or unavailable");
            finish();
        }
    }

    // 是否SDK是否已可以
    private boolean isFaceSdkReady() {
        return YTLFFaceManager.isSDKRuning && YTLFFaceManager.isLoadDB;
    }

    // 初始化SDK
    private void initSdk(Runnable runnable) {
        Tools.showLoadingProgress(this, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (YTLFFace.initIntellif(context) && initLicenseBySecret() && startFaceSDK() && loadDB()) {
                        Tools.debugLog("initSDK finish");
                    }

                    if (isFaceSdkReady() && runnable != null) {
                        runnable.run();
                    }

                } catch (Exception e) {
                    Tools.printStackTrace(e);
                    finish();
                } finally {
                    Tools.dismissLoadingProgress();
                }
            }
        }).start();
    }

    // 检测环境，并运行
    private void runOnFaceSdkReady(Runnable runnable) {
        if (isFaceSdkReady()) {
            if (runnable != null) {
                runnable.run();
            }
        } else {
            initSdk(runnable);
        }
    }

    // 加载授权license
    public boolean initLicenseBySecret() {
        return YTLFFace.initLicense(API_KEY);
        //return YTLFFace.initLicenseBySecret();
    }

    // 启动FaceSDK
    public boolean startFaceSDK() {
        if (!YTLFFaceManager.isSDKRuning) {
            int flag = YTLFFace.startFaceSDK();
            if (flag != 0) {
                YTLFFaceManager.isSDKRuning = false;
                showShortToast(R.string.app_name);
            } else {
                YTLFFaceManager.isSDKRuning = true;
            }
        }
        return YTLFFaceManager.isSDKRuning;
    }

    // 加载人脸库
    public boolean loadDB() {
        DBManager dbManager = App.getInstance().getDbManager();
        List<Person> personList = dbManager.getPersonList();
        if (personList.size() <= 0) {
            Tools.debugLog("initSDK DB is empty");
            YTLFFaceManager.isLoadDB = true;
        } else {
            long[] ids = new long[personList.size()];
            byte[][] features = new byte[personList.size()][];
            for (int i = 0; i < personList.size(); i++) {
                Person person = personList.get(i);
                ids[i] = person.getId();
                features[i] = person.getFeature();
            }

            int result = YTLFFace.dataBaseAdd(ids, features);
            Tools.debugLog("initSDK dataBaseAdd =%s", result == 0 ? R.string.ytlf_dictionaries3 : R.string.ytlf_dictionaries4);
            YTLFFaceManager.isLoadDB = true;
        }

        return YTLFFaceManager.isLoadDB;
    }

    // 人脸检测
    public void onEnterDetect(View view) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, FaceDetectActivity.class);
                startActivity(intent);
            }
        };

        runOnFaceSdkReady(runnable);
    }

    // 人脸注册
    public void onEnterRegister(View view) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, DBActivity.class);
                startActivity(intent);
            }
        };

        runOnFaceSdkReady(runnable);
    }

    // SDK设置
    public void onSetting(View view) {
        Intent intent = new Intent(this, FaceInfo.SettingActivity.class);
        startActivity(intent);
    }

    private void requestPermission() {
       /* final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (!granted) {
                            Toast.makeText(ArcternMainActivity.this, "Please agree to the required permissions", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });*/
    }

    //初始化ImagePicker，拍照或选图
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setMultiMode(false);
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private void initSetting() {
        //初始化ImagePicker，拍照或选图
        initImagePicker();
        Constants.recognition_overturn_rgbcamera = SPUtil.readCameraRgb();
        Constants.recognition_overturn_ircamera = SPUtil.readCameraIr();
        Constants.face_frame_mirror = SPUtil.readFaceFrameMirror();
        Constants.face_frame_reverse = SPUtil.readFaceFrameReverse();
        Constants.select_screen_rotate_rgbcamera = SPUtil.readScreenRotateRgbCamera();
        Constants.select_screen_rotate_ircamera = SPUtil.readScreenRotateIrCamera();

        Tools.debugLog("Signature=%s", YTLFFace.getSignature());
    }

    private void run(Runnable... runnables) {
        int index = 1;
        for (Runnable item : runnables) {
            Tools.runOnUiThread(item, index++ * 500);
        }
    }
}

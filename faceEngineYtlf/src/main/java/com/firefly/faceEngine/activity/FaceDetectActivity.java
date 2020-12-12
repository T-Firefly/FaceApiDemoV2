package com.firefly.faceEngine.activity;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.firefly.arcterndemo.R;
import com.firefly.faceEngine.App;
import com.firefly.faceEngine.other.FaceInfo;
import com.intellif.YTLFFaceManager;
import com.firefly.faceEngine.dblib.bean.Person;
import com.firefly.faceEngine.utils.Tools;
import com.firefly.faceEngine.view.FaceView;
import com.firefly.faceEngine.view.GrayInterface;
import com.firefly.faceEngine.view.GraySurfaceView;
import com.firefly.faceEngine.view.LivingInterface;
import com.firefly.faceEngine.view.LivingListener;
import com.intellif.arctern.base.ArcternAttrResult;
import com.intellif.arctern.base.ArcternAttribute;
import com.intellif.arctern.base.ArcternImage;
import com.intellif.arctern.base.ArcternRect;
import com.intellif.arctern.base.AttributeCallBack;
import com.intellif.arctern.base.ExtractCallBack;
import com.intellif.arctern.base.SearchCallBack;
import com.intellif.arctern.base.TrackCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FaceDetectActivity extends BaseActivity implements TrackCallBack, AttributeCallBack, SearchCallBack, ExtractCallBack {
    private ArcternImage irImage = null;
    private ArcternImage rbgImage = null;
    private TextView txt1, txt2, txt3;
    private FaceView faceView;
    private ImageView imgLandmark;
    private GraySurfaceView grayInterface;
    private Map<Long, Person> mMapPeople = new HashMap<>();
    private CountDownTimer mCountDownTimer;
    private YTLFFaceManager YTLFFace = YTLFFaceManager.getInstance();
    private ExecutorService executorService;
    private Future future;
    private FaceInfo faceInfo;

    private int view_width, view_height;
    private int frame_width, frame_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);

        initView();
        getViewWH();
        startCountDownTimer();
        setInfraredFillLight(true); //补光灯
    }

    private void initView() {
        setActionBarTitle(R.string.app_name);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        faceView = findViewById(R.id.faceview);
        imgLandmark = findViewById(R.id.img_landmark);

        grayInterface = findViewById(R.id.grayInterface);
        grayInterface.setZOrderOnTop(true);
        grayInterface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        LivingInterface.getInstance().init(this);
        LivingInterface.getInstance().setLivingCallBack(rgbLivingListener);

        GrayInterface.getInstance().init(this);
        GrayInterface.getInstance().setLivingCallBack(irLivingListener);

        YTLFFace.setOnTrackCallBack(this);
        YTLFFace.setOnSearchCallBack(this);
        YTLFFace.setOnAttributeCallBack(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Person> mPeople = App.getInstance().getDbManager().getPersonList();
        for (Person person : mPeople) {
            mMapPeople.put(person.getId(), person);
        }
    }

    @Override
    public void onExtractFeatureListener(ArcternImage arcternImage, byte[][] features, ArcternRect[] arcternRects) {
    }

    @Override
    public void onTrackListener(ArcternImage arcternImage, long[] trackIds, ArcternRect[] arcternRects) {
        if (arcternRects != null) {
            faceView.setFaces(arcternRects, frame_width, frame_height, view_width, view_height);
        }
    }

    //人脸属监听回调
    @Override
    public void onAttributeListener(ArcternImage arcternImage, long[] trackIds, ArcternRect[] arcternRects, ArcternAttribute[][] arcternAttributes, int[] landmarks) {
        ArcternAttribute[] attributes = arcternAttributes[0];
        if (attributes.length == 0) {
            return;
        }

        faceInfo = new FaceInfo(arcternImage, arcternAttributes);
        handleAttribute();
    }

    @Override
    public void onSearchListener(ArcternImage arcternImage, long[] trackIds, ArcternRect[] arcternRects, long[] searchIds, int[] landmarks, float[] socre) {
        if (searchIds.length <= 0 || arcternImage == null ||
                faceInfo == null || faceInfo.getFrameId() != arcternImage.frame_id) {
            return;
        }

        faceInfo.setSearchId(searchIds[0]);
        handlePerson();
    }

    LivingListener rgbLivingListener = new LivingListener() {
        @Override
        public void livingData(ArcternImage arcternImage) {
            rbgImage = arcternImage;
            frame_width = rbgImage.width;
            frame_height = rbgImage.height;
            if (irImage != null) {
                doDelivery(rbgImage, irImage);
            }
        }
    };

    LivingListener irLivingListener = new LivingListener() {
        @Override
        public void livingData(ArcternImage image) {
            irImage = image;
        }
    };

    private void doDelivery(final ArcternImage rbgImage, final ArcternImage irImage) {
        if (future != null && !future.isDone()) {
            return;
        }

        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                LivingInterface.rotateYUV420Degree90(rbgImage);
                LivingInterface.rotateYUV420Degree90(irImage);
                Tools.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YTLFFace.doDelivery(rbgImage, irImage);
                    }
                });
            }
        });
    }

    // 处理人员信息
    private void handlePerson() {
        Person person = mMapPeople.get(faceInfo.getSearchId());
        StringBuffer s = new StringBuffer();
        if (person != null) {
            faceView.isRed = false;
            s.append(getTimeShort()).append(person.toString()).append("\n");
        } else {
            faceView.isRed = true;
            s.append(getTimeShort()).append(" ").append(getString(R.string.ytlf_dictionaries12));
        }
        showText(txt2, s);
    }

    // 处理人脸属性信息
    private void handleAttribute() {
        ArcternAttribute[] attributes = faceInfo.getAttributes()[0];

        StringBuilder attribute = new StringBuilder();
        for (int i = 0; i < attributes.length; i++) {
            ArcternAttribute item = attributes[i];
            switch (i) {
                case ArcternAttribute.ArcternFaceAttrTypeEnum.QUALITY://人脸质量
                    attribute.append("\n").append(getString(R.string.ytlf_dictionaries21)).append(item.confidence);

                    if (item.confidence < 0.4) {//质量 < 0.4 ，那么不处理
                        faceView.isRed = false;
                        showText(txt1, "--");
                        return;
                    }

                    break;

                case ArcternAttribute.ArcternFaceAttrTypeEnum.LIVENESS_IR: //活体
                    if (item.label == ArcternAttribute.LabelFaceLive.LIVE) {
                        faceView.isRed = false;
                        attribute.append("\n").append(getString(R.string.ytlf_dictionaries19)).append(" ：").append(item.confidence);
                    } else {
                        attribute.append("\n").append(getString(R.string.ytlf_dictionaries20)).append(" ");
                        faceView.isRed = true;
                        showText(txt2, "--");
                    }
                    break;

                case ArcternAttribute.ArcternFaceAttrTypeEnum.FACE_MASK: //口罩
                    boolean isMask = item.label == ArcternAttribute.LabelFaceMask.MASK;
                    String str = getString(isMask ?R.string.ytlf_dictionaries8 : R.string.ytlf_dictionaries9);
                    attribute.append("\n").append(str).append(" ");
                    break;
            }
        }

        showText(txt1, attribute);
    }

    private String toString2(float[] confidences) {
        if (confidences == null) {
            return "confidences == null";
        }
        StringBuffer str = new StringBuffer();
        for (float item : confidences) {
            str.append(item).append(" ");
        }
        return str.toString();
    }

    protected void showText(TextView txt, CharSequence msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt.setText(msg);
            }
        });
    }

    public static String getTimeShort() {
        return new SimpleDateFormat("HH:mm:ss:SSS ").format(new Date());
    }

    //获得容器的高度
    private void getViewWH() {
        ViewTreeObserver vto = faceView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                faceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                view_width = faceView.getWidth();
                view_height = faceView.getHeight();
            }
        });
    }

    //刷新
    private void startCountDownTimer() {
        if (mCountDownTimer != null) {
            return;
        }

        mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 100) {
            private long timer = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                timer++;
                if (timer % 30 == 0) {
                }

                if (timer % 150 == 0) {
                    showText(txt2, "--");
                }

                if (timer % 30 == 0) {
                    //showLandmarksImage(); // test landmark
                }
            }

            @Override
            public void onFinish() {
                cancel();
            }
        };

        mCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            if (mCountDownTimer != null) {
                mCountDownTimer.onFinish();
            }
            setInfraredFillLight(false);
        } catch (Exception e) {
            Tools.printStackTrace(e);
        }
    }
}

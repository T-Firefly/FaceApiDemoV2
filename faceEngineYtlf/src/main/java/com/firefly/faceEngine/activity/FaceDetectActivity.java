package com.firefly.faceEngine.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.firefly.arcterndemo.R;
import com.firefly.faceEngine.App;
import com.intellif.YTLFFaceManager;
import com.firefly.faceEngine.dblib.bean.Person;
import com.firefly.faceEngine.utils.Tools;
import com.firefly.faceEngine.view.FaceView;
import com.firefly.faceEngine.view.GrayInterface;
import com.firefly.faceEngine.view.GraySurfaceView;
import com.firefly.faceEngine.view.LivingInterface;
import com.firefly.faceEngine.view.LivingListener;
import com.intellif.arctern.base.ArcternAttribute;
import com.intellif.arctern.base.ArcternImage;
import com.intellif.arctern.base.ArcternRect;
import com.intellif.arctern.base.AttributeCallBack;
import com.intellif.arctern.base.DetectCallBack;
import com.intellif.arctern.base.ExtractCallBack;
import com.intellif.arctern.base.SearchCallBack;
import com.intellif.arctern.base.TrackCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceDetectActivity extends BaseActivity implements DetectCallBack, TrackCallBack, AttributeCallBack, SearchCallBack, ExtractCallBack {
    private ArcternImage iRimage = null;
    private TextView txt1, txt2, txt3;
    private FaceView faceView;
    private GraySurfaceView grayInterface;
    private Map<Long, Person> mMapPeople = new HashMap<>();
    private CountDownTimer mCountDownTimer;
    private YTLFFaceManager YTLFFace = YTLFFaceManager.getInstance();

    private int view_width, view_height;
    private int frame_width, frame_height;
    private boolean isLive;
    private long isLiveTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);

        initView();
        getViewWH();
        startCountDownTimer();
    }

    private void initView() {
        setActionBarTitle(R.string.app_name);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        faceView = findViewById(R.id.faceview);

        grayInterface = findViewById(R.id.grayInterface);
        grayInterface.setZOrderOnTop(true);
        grayInterface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        LivingInterface.getInstance().init(this);
        LivingInterface.getInstance().setLivingCallBack(rgbLivingListener);

        GrayInterface.getInstance().init(this);
        GrayInterface.getInstance().setLivingCallBack(irLivingListener);

        YTLFFace.setOnDetectCallBack(this);
        YTLFFace.setOnTrackCallBack(this);
        YTLFFace.setOnSearchCallBack(this);
        YTLFFace.setOnAttributeCallBack(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Person>  mPeople = App.getInstance().getDbManager().getPersonList();
        for (Person person : mPeople) {
            mMapPeople.put(person.getId(), person);
            Tools.debugLog("person:" + person);
        }
    }

    @Override
    public void onExtractFeatureListener(ArcternImage arcternImage, byte[][] features, ArcternRect[] arcternRects) {
    }

    @Override
    public void onTrackListener(ArcternImage arcternImage, long[] trackId_list, ArcternRect[] arcternRects) {
        if (arcternRects != null) {
            faceView.setFaces(arcternRects, frame_width, frame_height, view_width, view_height);
        }
    }

    // RGB 回调
    @Override
    public void onDetectListener(ArcternImage arcternImage, ArcternRect[] arcternRects, float[] confidences) {
    }

    // IR 回调
    @Override
    public void onLivingDetectListener(ArcternImage arcternImage, ArcternRect[] arcternRects, float[] confidences) {
    }

    public void onAttributeListener(ArcternImage arcternImage, long[] trackId_list, ArcternRect[] arcternRects, ArcternAttribute[][] arcternAttributes) {
        StringBuilder attribute = new StringBuilder();
        for (int i = 0; i < arcternRects.length; i++) {
            for (int j = 0; j < arcternAttributes[i].length; j++) {
                Tools.debugLog("arcternRects.length=%s arcternAttributes[%s].length=%s", arcternRects.length, i, arcternAttributes[i].length);
                ArcternAttribute attr = arcternAttributes[i][j];

                switch (j) {
                    case ArcternAttribute.ArcternFaceAttrTypeEnum.QUALITY:
                        attribute.append("\n").append(getString(R.string.ytlf_dictionaries21)).append(attr.confidence);
                        break;

                    case ArcternAttribute.ArcternFaceAttrTypeEnum.LIVENESS_IR: //活体检测
                        if (attr.label != -1 && attr.confidence >= 0.5) {
                            isLiveTime = System.currentTimeMillis();
                            isLive = true;
                            attribute.append("\n").append(getString(R.string.ytlf_dictionaries19)).append(" ：").append(attr.confidence);
                        } else if (attr.label == -1 && System.currentTimeMillis() - isLiveTime > 3000) {
                            isLive = false;
                            attribute.append("\n").append(getString(R.string.ytlf_dictionaries20)).append(" ");
                            faceView.isRed = true;
                            //attribute.append("\n  ");
                            showText(txt2, "--");
                        } else{
                            return ;
                        }
                        break;

                    case ArcternAttribute.ArcternFaceAttrTypeEnum.FACE_MASK: //口罩检测
                        attribute.append("\n").append(getString(attr.label == 1 ? R.string.ytlf_dictionaries8 : R.string.ytlf_dictionaries9)).append(" ");
                        break;

                    case ArcternAttribute.ArcternFaceAttrTypeEnum.IMAGE_COLOR:
                        if (attr.label == ArcternAttribute.LabelFaceImgColor.COLOR) {
                            attribute.append(getString(R.string.ytlf_dictionaries30)).append(" ：").append(attr.confidence);
                        } else {
                            attribute.append(getString(R.string.ytlf_dictionaries31)).append(" ：").append(attr.confidence);
                        }
                        break;
                }
            }
        }
        showText(txt1, attribute);
    }

    @Override
    public void onSearchListener(ArcternImage arcternImage, long[] trackId_list, ArcternRect[] arcternRects, long[] searchId_list) {
        Tools.debugLog("onSearchListener");
        Person person = null;
        if (searchId_list.length > 0 && searchId_list[0] != -1) {
             person = mMapPeople.get(searchId_list[0]);
        }

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

    LivingListener rgbLivingListener = new LivingListener() {
        @Override
        public void livingData(ArcternImage image) {
            frame_width = image.width;
            frame_height = image.height;
            if (iRimage != null) {
                YTLFFace.doDelivery(image, iRimage);
            }
        }
    };

    LivingListener irLivingListener = new LivingListener() {
        @Override
        public void livingData(ArcternImage image) {
            //Tools.debugLog("ir livingData: ArcternImage: " + image);
            iRimage = image;
        }
    };

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
        } catch (Exception e) {
            Tools.printStackTrace(e);
        }
    }
}

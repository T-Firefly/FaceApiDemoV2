package com.firefly.fireflyapidemo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.firefly.api.face.temperature.TempatureUtil;


public class TempatureActivity extends BaseActivity implements TempatureUtil.TempatureCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        if (TempatureUtil.getInstance().isSupport()) {
            //初始化
            TempatureUtil.getInstance().openDevice();
            TempatureUtil.getInstance().setTempatureCallback(this);
            setVisibility(true, R.id.txt);
            setVisibility(false, R.id.txt_not_support);
        } else {
            setVisibility(false, R.id.txt);
            setVisibility(true, R.id.txt_not_support);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TempatureUtil.getInstance().closeDevice();
    }

    @Override
    public void connect() {
        Tools.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setText(R.id.txt, R.string.firefly_api_dictionaries1);
            }
        });
    }

    @Override
    public void update(final float ambientTempature, final float objectTempature) {
        Tools.runOnUiThread(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                setText(R.id.txt, getTimeShort() + getString(R.string.firefly_api_dictionaries11,
                        ambientTempature, objectTempature, TempatureUtil.correctTemp(objectTempature)));
            }
        });

    }
}

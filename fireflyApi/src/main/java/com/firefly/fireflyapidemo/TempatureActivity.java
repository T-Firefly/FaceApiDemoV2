package com.firefly.fireflyapidemo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.firefly.api.face.temperature.TempatureUtil;


public class TempatureActivity extends BaseActivity implements TempatureUtil.TempatureCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        TempatureUtil.getInstance().setTempatureCallback(this);
        TempatureUtil.getInstance().openDevice();

        if (!TempatureUtil.getInstance().isSupport()) {
            Tools.showLoadingProgress(content, false);
            Tools.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TempatureUtil.getInstance().isSupport()) {
                        setVisibility(true, R.id.txt);
                        setVisibility(false, R.id.txt_not_support);
                        Tools.dismissLoadingProgress();
                    }
                }
            }, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TempatureUtil.getInstance().closeDevice();
    }

    @Override
    public void connect() {
        Tools.dismissLoadingProgress();
        Tools.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setText(R.id.txt, "Device connected");
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

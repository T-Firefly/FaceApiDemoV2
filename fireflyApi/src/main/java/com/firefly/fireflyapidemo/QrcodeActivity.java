package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.firefly.api.face.qrcode.QrCodeUtil;


public class QrcodeActivity extends BaseActivity {

    private QrCodeUtil mQrCodeUtil;
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        setVisibility(false, R.id.llayout_main);
        Tools.showLoadingProgress(content, false);
        mSwitch = findViewById(R.id.switch_active);

        mQrCodeUtil = QrCodeUtil.getInstance();
        mQrCodeUtil.setQRCodeCallback(mQRCodeCallback);
        mQrCodeUtil.init();

        Tools.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!QrCodeUtil.getInstance().isQrCodeSupport()) {
                    setVisibility(false, R.id.llayout_main);
                    setVisibility(true, R.id.txt_not_support);
                    Tools.dismissLoadingProgress();
                }
            }
        }, 3000);

        // 二维码工作状态切换，会有10秒钟左右的延迟
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_AUTO);
                    mQrCodeUtil.setFocusLedState(QrCodeUtil.LED_STATE_AUTO);
                } else {
                    mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_OFF);
                    mQrCodeUtil.setFocusLedState(QrCodeUtil.LED_STATE_OFF);
                }

                mQrCodeUtil.setActive(isChecked);
                setText(R.id.txt_msg, "");
                Tools.showLoadingProgressAutoDismiss(content, 10 * 1000);
            }
        });
    }

    private QrCodeUtil.QRCodeCallback mQRCodeCallback = new QrCodeUtil.QRCodeCallback() {

        // 二维码工作状态初始化后，在onConnect()中进行最终的UI设置
        @Override
        public void onConnect() {
            Tools.debugLog("onConnect: QrCodeUtil.getInstance().isQrCodeSupport() = " + QrCodeUtil.getInstance().isQrCodeSupport());
            Tools.dismissLoadingProgress();
            Tools.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setText(R.id.txt_msg, "");
                    if (QrCodeUtil.getInstance().isQrCodeSupport()) {
                        mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_AUTO);
                        mQrCodeUtil.setFocusLedState(QrCodeUtil.LED_STATE_AUTO);
                        setVisibility(true, R.id.llayout_main);
                        setVisibility(false, R.id.txt_not_support);
                    } else {
                        setVisibility(false, R.id.llayout_main);
                        setVisibility(true, R.id.txt_not_support);
                    }
                }
            });
        }

        @Override
        public void onData(final String s) {
            Tools.debugLog("onData:" + s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setText(R.id.txt_msg, getString(R.string.testcase_qrcode_title) + ":" + s);
                }
            });
        }
    };

    @Override
    public void finish(){
        // 有10秒钟左右的延迟
        if (mSwitch.isChecked() && mQrCodeUtil != null) {
            mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_OFF);
            mQrCodeUtil.setFocusLedState(QrCodeUtil.LED_STATE_OFF);
            mQrCodeUtil.setActive(false);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mQrCodeUtil != null) {
            mQrCodeUtil.release();
        }
        mQrCodeUtil = null;
        super.onDestroy();
    }
}

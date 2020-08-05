package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.firefly.api.face.qrcode.QrCodeUtil;


public class QrcodeActivity extends BaseActivity implements View.OnClickListener {

    private QrCodeUtil mQrCodeUtil;
    private Button mLedAuto, mLedOn, mLedOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        setVisibility(true, R.id.llayout_main);
        mLedAuto = (Button) findViewById(R.id.qrcode_led_auto);
        mLedOn = (Button) findViewById(R.id.qrcode_led_on);
        mLedOff = (Button) findViewById(R.id.qrcode_led_off);

        mLedAuto.setOnClickListener(this);
        mLedOn.setOnClickListener(this);
        mLedOff.setOnClickListener(this);

        //二维码
        mQrCodeUtil = QrCodeUtil.getInstance();
        mQrCodeUtil.setQRCodeCallback(mQRCodeCallback);

        mQrCodeUtil.init();
        //设置对焦灯状态
        mQrCodeUtil.setFocusLedState(QrCodeUtil.LED_STATE_ON);

        Tools.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!QrCodeUtil.getInstance().isQrCodeSupport()) {
                    setVisibility(false, R.id.llayout_main);
                    setVisibility(true, R.id.txt_not_support);
                }
            }
        }, 3000);

        ((Switch) findViewById(R.id.switch_active)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QrCodeUtil.getInstance().setActive(isChecked);
                setText(R.id.txt_msg, "");
                Tools.showLoadingProgressAutoDismiss(content);
            }
        });
    }


    private QrCodeUtil.QRCodeCallback mQRCodeCallback = new QrCodeUtil.QRCodeCallback() {

        @Override
        public void onConnect() {
            Tools.debugLog("QrCodeUtil.QRCodeCallback onConnect");
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
    protected void onDestroy() {
        super.onDestroy();
        if (mQrCodeUtil != null) {
            mQrCodeUtil.release();
        }
        mQrCodeUtil = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.qrcode_led_auto) {
            mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_AUTO);
        } else if (id == R.id.qrcode_led_on) {
            mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_ON);
        } else if (id == R.id.qrcode_led_off) {
            mQrCodeUtil.setLedState(QrCodeUtil.LED_STATE_OFF);
        }

        Tools.showLoadingProgressAutoDismiss(content);
    }
}

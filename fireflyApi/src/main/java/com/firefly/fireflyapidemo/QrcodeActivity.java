package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firefly.api.face.qrcode.QrCodeUtil;


public class QrcodeActivity extends BaseActivity implements View.OnClickListener {

    private QrCodeUtil mQrCodeUtil;
    private Handler mHandler = new Handler();
    private Button mLedAuto,mLedOn,mLedOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        if (!QrCodeUtil.getInstance().isQrCodeSupport()) {
            setVisibility(false, R.id.llayout_main);
            setVisibility(true, R.id.txt_not_support);
            return;
        }

        setVisibility(true, R.id.llayout_main);
        mLedAuto = (Button)findViewById(R.id.qrcode_led_auto);
        mLedOn = (Button)findViewById(R.id.qrcode_led_on);
        mLedOff = (Button)findViewById(R.id.qrcode_led_off);

        mLedAuto.setOnClickListener(this);
        mLedOn.setOnClickListener(this);
        mLedOff.setOnClickListener(this);

        //二维码
        mQrCodeUtil = QrCodeUtil.getInstance();
        mQrCodeUtil.setQRCodeCallback(mQRCodeCallback);

        mQrCodeUtil.openQrcode();
        //是否开启二维码扫描补光灯
        mQrCodeUtil.setLedState(true);


    }


    private QrCodeUtil.QRCodeCallback mQRCodeCallback = new QrCodeUtil.QRCodeCallback() {

        @Override
        public void onConnect() {

        }

        @Override
        public void onData(final String s) {
            Log.v("sjfq","onData:"+s);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QrcodeActivity.this, s, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQrCodeUtil != null) {
            mQrCodeUtil.destory();
        }
        mQrCodeUtil = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.qrcode_led_auto) {
            mQrCodeUtil.setLedStatus(QrCodeUtil.LED_STATE_AUTO);
        } else if (id == R.id.qrcode_led_on) {
            mQrCodeUtil.setLedStatus(QrCodeUtil.LED_STATE_ON);
        } else if (id == R.id.qrcode_led_off) {
            mQrCodeUtil.setLedStatus(QrCodeUtil.LED_STATE_OFF);
        }
    }
}

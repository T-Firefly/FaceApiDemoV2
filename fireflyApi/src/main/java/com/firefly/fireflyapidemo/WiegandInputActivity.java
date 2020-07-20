package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.os.Handler;

import com.firefly.api.HardwareCtrl;
import com.firefly.api.callback.RecvWiegandCallBack;

public class WiegandInputActivity extends BaseActivity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        //韦根输入
        HardwareCtrl.openRecvMiegandSignal("/dev/wiegand");
        HardwareCtrl.recvWiegandSignal(new RecvWiegandCallBack() {
            @Override
            public void recvWiegandMsg(final int i) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setText(R.id.txt, getTimeShort() + "RecvWiegandCallBack: " + i);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardwareCtrl.closeRecvMiegandSignal();
    }
}

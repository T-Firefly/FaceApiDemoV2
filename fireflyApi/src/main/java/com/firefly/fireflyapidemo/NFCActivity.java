package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.view.KeyEvent;

import com.firefly.api.face.nfc.NfcUtil;

public class NFCActivity extends BaseActivity implements NfcUtil.OnNfcListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        if (NfcUtil.getInstance().isSupport()) {
            //初始化nfc读卡
            NfcUtil.getInstance().setOnNfcListener(this);
            setVisibility(true, R.id.txt);
            setVisibility(false, R.id.txt_not_support);
        } else {
            setVisibility(false, R.id.txt);
            setVisibility(true, R.id.txt_not_support);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Tools.debugLog("dispatchKeyEvent keycode=" + event.getKeyCode());
        if (NfcUtil.getInstance().isSupport()) {
            return NfcUtil.getInstance().handleEvent(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onNfcSwipe(String code) {
        setText(R.id.txt, getString(R.string.firefly_api_dictionaries10) + "code:" + code);
    }

}

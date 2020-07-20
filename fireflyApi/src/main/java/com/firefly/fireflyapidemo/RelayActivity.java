package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.firefly.api.HardwareCtrl;
import com.firefly.api.face.control.RelayUtil;

public class RelayActivity extends BaseActivity {

    private Switch relaySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relay);
        relaySwitch = findViewById(R.id.relaySwitch);
        relaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RelayUtil.sendRelaySignal(isChecked);
                try {
                    HardwareCtrl.sendRelaySignal(isChecked);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

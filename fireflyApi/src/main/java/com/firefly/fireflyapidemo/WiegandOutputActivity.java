package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firefly.api.HardwareCtrl;

public class WiegandOutputActivity extends BaseActivity implements View.OnClickListener {
    private Button wiegand26Btn;
    private Button wiegand34Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiegand_26_34);
        wiegand26Btn = findViewById(R.id.wiegand26Btn);
        wiegand34Btn = findViewById(R.id.wiegand34Btn);

        wiegand26Btn.setOnClickListener(this);
        wiegand34Btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.wiegand26Btn) {//同一时间内，只能使用其中一个
            //韦根26，通过cat /sys/devices/platform/wiegand-gpio/wiegand26 查看设置的值变化。
            HardwareCtrl.sendWiegandSignal("123456789");
        } else if (id == R.id.wiegand34Btn) {//韦根34，通过cat /sys/devices/platform/wiegand-gpio/wiegand34 查看设置的值变化。
            HardwareCtrl.sendWiegand34Signal("123456789");
        }
    }
}

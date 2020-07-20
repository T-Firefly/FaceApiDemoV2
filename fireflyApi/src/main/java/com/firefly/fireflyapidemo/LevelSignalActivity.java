package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.firefly.api.face.control.LevelSignalUtil;

public class LevelSignalActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private Switch mD0Btn;
    private Switch mD1Btn;
    private Switch mD0_D1Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_signal);
        initView();
    }

    private void initView() {
        mD0Btn = findViewById(R.id.btn_d0);
        mD1Btn = findViewById(R.id.btn_d1);
        mD0_D1Btn = findViewById(R.id.btn_d0_d1);

        mD0Btn.setOnCheckedChangeListener(this);
        mD1Btn.setOnCheckedChangeListener(this);
        mD0_D1Btn.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.btn_d0) {
            LevelSignalUtil.sendSignalD0(isChecked);
        } else if (id == R.id.btn_d1) {
            LevelSignalUtil.sendSignalD1(isChecked);
        } else if (id == R.id.btn_d0_d1) {
            LevelSignalUtil.sendSignalD0(isChecked);
            LevelSignalUtil.sendSignalD1(isChecked);
        }
    }
}

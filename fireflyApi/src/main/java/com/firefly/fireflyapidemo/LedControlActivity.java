package com.firefly.fireflyapidemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.firefly.api.HardwareCtrl;

public class LedControlActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private Switch switchWhite;
    private Switch switchRed;
    private Switch switchGreen;
    private Switch switchInfrared;
    private SeekBar seekBarWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_light_v2);
        initView();

        Tools.debugLog("HardwareCtrl.isSupportInfraredFillLight()=%s", HardwareCtrl.isSupportInfraredFillLight());
    }

    @SuppressLint("NewApi")
    private void initView() {
        switchWhite = findViewById(R.id.switch_white);
        switchRed = findViewById(R.id.switch_red);
        switchGreen = findViewById(R.id.switch_green);
        switchInfrared = findViewById(R.id.switch_infrared);
        seekBarWhite = findViewById(R.id.seekBar_white);

        switchWhite.setOnCheckedChangeListener(this);
        switchRed.setOnCheckedChangeListener(this);
        switchGreen.setOnCheckedChangeListener(this);
        switchInfrared.setOnCheckedChangeListener(this);

        initSeekBar();

        switchInfrared.setEnabled(HardwareCtrl.isSupportInfraredFillLight());
        setTextSupport(R.id.txt_infrared, getString(R.string.firefly_api_dictionaries25), HardwareCtrl.isSupportInfraredFillLight());
    }

    @SuppressLint("NewApi")
    private void initSeekBar() {
        /**2.白色补光灯
         HardwareCtrl.isFillLightBrightnessSupport()//是否支持补光灯亮度调节
         HardwareCtrl.getFillLightBrightnessMin() //支持亮度调节的最小值
         HardwareCtrl.getFillLightBrightnessMax() //支持亮度调节的最大值
         */
        if (HardwareCtrl.isFillLightBrightnessSupport()) {
            setTextSupport(R.id.txt_white);
            setVisibility(switchWhite.isChecked(), R.id.llayout_seekBar);
            seekBarWhite.setMax(HardwareCtrl.getFillLightBrightnessMax());
            //seekBarWhite.setMin(HardwareCtrl.getFillLightBrightnessMin());
            seekBarWhite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    HardwareCtrl.writeFillLightBrightnessOptions(progress);
                    setText(R.id.txt_seekBar_white, getString(R.string.firefly_api_dictionaries24, String.valueOf(progress)));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            if (switchWhite.isChecked()) {
                seekBarWhite.setProgress(HardwareCtrl.readFillLightBrightnessValue());
            }
        } else {
            setTextSupport(R.id.txt_white, getString(R.string.firefly_api_dictionaries24, ""), false);
            seekBarWhite.setEnabled(false);
            setVisibility(false, R.id.llayout_seekBar);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.switch_white) {
            if (isChecked) {
                if (seekBarWhite.getProgress() > 0) {
                    HardwareCtrl.ctrlLedWhite(true, seekBarWhite.getProgress());
                } else {
                    HardwareCtrl.ctrlLedWhite(true);
                    if (seekBarWhite.isEnabled()) {
                        seekBarWhite.setProgress(HardwareCtrl.readFillLightBrightnessValue());
                    }
                }
            } else {
                HardwareCtrl.ctrlLedWhite(false);
            }
            seekBarWhite.setEnabled(isChecked && HardwareCtrl.isFillLightBrightnessSupport());
            setVisibility(seekBarWhite.isEnabled(), R.id.llayout_seekBar);
        } else if (id == R.id.switch_red) {
            HardwareCtrl.ctrlLedRed(isChecked);
        } else if (id == R.id.switch_green) {
            HardwareCtrl.ctrlLedGreen(isChecked);
        } else if (id == R.id.switch_infrared) {
            HardwareCtrl.setInfraredFillLight(isChecked);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardwareCtrl.ctrlLedWhite(false);
        HardwareCtrl.ctrlLedRed(false);
        HardwareCtrl.ctrlLedGreen(false);
        HardwareCtrl.setInfraredFillLight(false);
    }
}

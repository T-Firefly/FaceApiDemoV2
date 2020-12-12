package com.firefly.faceEngine.other;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.firefly.arcterndemo.R;
import com.firefly.faceEngine.activity.BaseActivity;
import com.firefly.faceEngine.utils.Constants;
import com.firefly.faceEngine.utils.SPUtil;
import com.intellif.arctern.base.ArcternAttribute;
import com.intellif.arctern.base.ArcternImage;

public class FaceInfo {
    private ArcternImage arcternImage;
    private ArcternAttribute[][] attributes;
    private long searchId;

    public FaceInfo() {

    }

    public FaceInfo(ArcternImage arcternImage, ArcternAttribute[][] attributes) {
        this.arcternImage = arcternImage;
        this.attributes = attributes;
    }

    public long getFrameId() {
        if (arcternImage != null) {
            return arcternImage.frame_id;
        } else {
            return -1;
        }
    }

    public ArcternImage getArcternImage() {
        return arcternImage;
    }

    public void setArcternImage(ArcternImage arcternImage) {
        this.arcternImage = arcternImage;
    }

    public ArcternAttribute[][] getAttributes() {
        return attributes;
    }

    public void setAttributes(ArcternAttribute[][] attributes) {
        this.attributes = attributes;
    }

    public long getSearchId() {
        return searchId;
    }

    public void setSearchId(long searchId) {
        this.searchId = searchId;
    }

    public static class SettingActivity extends BaseActivity {

        private Switch switch_recognition_rgbcamera, switch_recognition_ircamera, switch_mirroring, switch_reverse;
        private Spinner spinner_rotate_rgb, spinner_rotate_ir;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setting);

            initViews();
        }

        @Override
        protected void onResume() {
            super.onResume();
            initDatas();
        }

        private void initViews(){
            setActionBarTitle(R.string.ytlf_dictionaries11);
            switch_recognition_rgbcamera = findViewById(R.id.switch_recognition_rgbcamera);
            switch_recognition_ircamera = findViewById(R.id.switch_recognition_ircamera);
            switch_mirroring = findViewById(R.id.switch_mirroring);
            switch_reverse = findViewById(R.id.switch_reverse);
            spinner_rotate_rgb = findViewById(R.id.spinner_rotate_rgb);
            spinner_rotate_ir = findViewById(R.id.spinner_rotate_ir);
        }

        private void initDatas(){
            switch_recognition_rgbcamera.setChecked(Constants.recognition_overturn_rgbcamera);
            switch_recognition_ircamera.setChecked(Constants.recognition_overturn_ircamera);
            switch_mirroring.setChecked(Constants.face_frame_mirror);
            switch_reverse.setChecked(Constants.face_frame_reverse);
            spinner_rotate_rgb.setSelection(Constants.select_screen_rotate_rgbcamera);
            spinner_rotate_ir.setSelection(Constants.select_screen_rotate_ircamera);

            switch_recognition_rgbcamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Constants.recognition_overturn_rgbcamera = isChecked;
                    SPUtil.writeCamera1(isChecked);
                }
            });
            switch_recognition_ircamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Constants.recognition_overturn_ircamera = isChecked;
                    SPUtil.writeCamera2(isChecked);
                }
            });
            switch_mirroring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Constants.face_frame_mirror = isChecked;
                    SPUtil.writeFaceFrameMirror(isChecked);
                }
            });
            switch_reverse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Constants.face_frame_reverse = isChecked;
                    SPUtil.writeFaceFrameReverse(isChecked);
                }
            });

            spinner_rotate_rgb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.select_screen_rotate_rgbcamera = position;
                    SPUtil.writeScreenRotateRgbCamera(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spinner_rotate_ir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.select_screen_rotate_ircamera = position;
                    SPUtil.writeScreenRotateIrCamera(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
}

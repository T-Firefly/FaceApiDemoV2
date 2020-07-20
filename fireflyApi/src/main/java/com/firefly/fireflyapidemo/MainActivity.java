package com.firefly.fireflyapidemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button ledLightBtn;
    private Button rs485_rs232Btn;
    private Button wiegand26_wiegand34Btn;
    private Button wiegandInputBtn;
    private Button levelSignalBtn;
    private Button relaySignaBtn;
    private Button nfcBtn;
    private Button radarBtn;
    private Button qrcodeBtn;
    private Button tempatureBtn;
    private Button mIdcardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        checkPermission();
    }

    private void initView() {
        mIdcardBtn = (Button)findViewById(R.id.test_idcard);
        ledLightBtn = findViewById(R.id.led_light_ctrl);
        rs485_rs232Btn = findViewById(R.id.rs485_rs232);
        wiegand26_wiegand34Btn = findViewById(R.id.wiegand26_wiegand34);
        wiegandInputBtn = findViewById(R.id.wiegand_input);
        levelSignalBtn = findViewById(R.id.level_signal);
        relaySignaBtn = findViewById(R.id.relay_signal);
        nfcBtn = findViewById(R.id.nfc);
        radarBtn = findViewById(R.id.radar);
        qrcodeBtn = findViewById(R.id.qrcode);
        tempatureBtn = findViewById(R.id.tempature);

        mIdcardBtn.setOnClickListener(this);
        ledLightBtn.setOnClickListener(this);
        rs485_rs232Btn.setOnClickListener(this);
        wiegand26_wiegand34Btn.setOnClickListener(this);
        wiegandInputBtn.setOnClickListener(this);
        relaySignaBtn.setOnClickListener(this);
        levelSignalBtn.setOnClickListener(this);
        nfcBtn.setOnClickListener(this);
        radarBtn.setOnClickListener(this);
        qrcodeBtn.setOnClickListener(this);
        tempatureBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.test_idcard) {
            startActivity(new Intent(this, IDCardActivity.class));
        } else if (id == R.id.led_light_ctrl) {
            Intent led_light_ctrlintent = new Intent(this, LedControlActivity.class);
            startActivity(led_light_ctrlintent);
        } else if (id == R.id.rs485_rs232) {
            Intent rs485_rs232intent = new Intent(this, Rs485AndRs232Activity.class);
            startActivity(rs485_rs232intent);
        } else if (id == R.id.wiegand26_wiegand34) {
            Intent wiegand26_wiegand34intent = new Intent(this, WiegandOutputActivity.class);
            startActivity(wiegand26_wiegand34intent);
        } else if (id == R.id.wiegand_input) {
            Intent wiegand_inputintent = new Intent(this, WiegandInputActivity.class);
            startActivity(wiegand_inputintent);
        } else if (id == R.id.level_signal) {
            Intent level_signalintent = new Intent(this, LevelSignalActivity.class);
            startActivity(level_signalintent);
        } else if (id == R.id.relay_signal) {
            Intent relayintent = new Intent(this, RelayActivity.class);
            startActivity(relayintent);
        } else if (id == R.id.nfc) {
            Intent nfcintent = new Intent(this, NFCActivity.class);
            startActivity(nfcintent);
        } else if (id == R.id.radar) {
            Intent radarintent = new Intent(this, RadarActivity.class);
            startActivity(radarintent);
        } else if (id == R.id.qrcode) {
            Intent qrcodeintent = new Intent(this, QrcodeActivity.class);
            startActivity(qrcodeintent);
        } else if (id == R.id.tempature) {
            Intent tempIntent = new Intent(this, TempatureActivity.class);
            startActivity(tempIntent);
        }

    }

    private static String[] sPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private void checkPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        for(String permission:sPermissions){
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission);
            }
        }
        if (permissionList.size() > 0) {
            String[] permissions = new String[permissionList.size()];
            permissionList.toArray(permissions);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1000);
        } else {
            permissionGrant();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++)
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

            }

        //checkPermission();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //权限通过
    protected void permissionGrant() {
    }
}

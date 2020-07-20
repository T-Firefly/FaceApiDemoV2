package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firefly.api.HardwareCtrl;
import com.firefly.api.serialport.SerialPort;
import com.firefly.api.utils.StringUtils;

import java.io.File;

public class Rs485AndRs232Activity extends BaseActivity implements SerialPort.Callback {

    private SerialPort serialPort;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rs485_rs232);

        //485串口：/dev/ttyS4  ;  232串口：/dev/ttyS3
        serialPort = HardwareCtrl.openSerialPortSignal(new File("/dev/ttyS3"), 19200, this);

        sendBtn = findViewById(R.id.send_rs485_rs232);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HardwareCtrl.sendSerialPortMsg(serialPort, "48562311");
                //然后通过cat /dev/ttyS4 或者 cat /dev/ttyS3 查看设置的值变化。
                if (serialPort != null)
                    HardwareCtrl.sendSerialPortHexMsg(serialPort, "48562311");
            }
        });
    }

    //rs485/232发送信号后接收返回值
    @Override
    public void onDataReceived(byte[] bytes, int size) {
        String result = StringUtils.bytesToHexString(bytes, size);
        Tools.debugLog("result = "+result);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialPort != null)
            HardwareCtrl.closeSerialPortSignal(serialPort);
    }
}

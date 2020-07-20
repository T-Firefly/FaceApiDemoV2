package com.firefly.faceApi.V2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firefly.faceEngine.utils.Tools;
import com.firefly.fireflyapidemo.IDCardActivity;
import com.firefly.fireflyapidemo.LedControlActivity;
import com.firefly.fireflyapidemo.LevelSignalActivity;
import com.firefly.fireflyapidemo.NFCActivity;
import com.firefly.fireflyapidemo.QrcodeActivity;
import com.firefly.fireflyapidemo.RadarActivity;
import com.firefly.fireflyapidemo.RelayActivity;
import com.firefly.fireflyapidemo.Rs485AndRs232Activity;
import com.firefly.fireflyapidemo.TempatureActivity;
import com.firefly.fireflyapidemo.WiegandInputActivity;
import com.firefly.fireflyapidemo.WiegandOutputActivity;
import com.firefly.faceEngine.activity.ArcternMainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppMainActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new SimpleAdapter(this, getData(), R.layout.app_menu_list_item, new String[] { "title" }, new int[] { android.R.id.text1 }));

        checkPermission();
        if (Tools.isApkDebugable()) {
            //startActivity((Intent) getData().get(2).get("intent"));
        }
    }

    protected List<Map<String, Object>> getData() {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
        addItem(myData, R.string.app_face_recognition, new Intent(this, ArcternMainActivity.class));
        addItem(myData, R.string.testcase_idcard_rb_title_idcard, new Intent(this, IDCardActivity.class));
        addItem(myData, R.string.testcase_nfc_title, new Intent(this, NFCActivity.class));
        addItem(myData, R.string.testcase_ledcontrol_title, new Intent(this, LedControlActivity.class));
        addItem(myData, R.string.testcase_qrcode_title, new Intent(this, QrcodeActivity.class));
        addItem(myData, R.string.testcase_tempature_title, new Intent(this, TempatureActivity.class));
        addItem(myData, R.string.testcase_radar_title, new Intent(this, RadarActivity.class));
        addItem(myData, R.string.testcase_wiegand_input_title, new Intent(this, WiegandInputActivity.class));
        addItem(myData, R.string.testcase_wiegand_title, new Intent(this, WiegandOutputActivity.class));
        addItem(myData, R.string.testcase_rs485_rs232_title, new Intent(this, Rs485AndRs232Activity.class));
        addItem(myData, R.string.testcase_level_signal_title, new Intent(this, LevelSignalActivity.class));
        addItem(myData, R.string.testcase_relay_signal_title, new Intent(this, RelayActivity.class));


        return myData;
    }

    protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    protected void addItem(List<Map<String, Object>> data, int nameResId, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", getString(nameResId));
        temp.put("intent", intent);
        data.add(temp);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(!checkPermission()){
            return;
        }

        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
        Intent intent = (Intent) map.get("intent");
        intent.putExtra("title", (String) map.get("title"));
        startActivity(intent);
    }

    private static String[] sPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    private boolean checkPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        for(String permission:sPermissions){
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission);
            }
        }

        if (permissionList.size() > 0) {
            String[] permissions = new String[permissionList.size()];
            permissionList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 1000);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // 重新申请权限
                    //checkPermission();

                    //Tools.toast("拒绝权限，将无法使用程序。");
                    showWaringDialog();
                    return;
                }
            }
        }
    }

    private void showWaringDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Denying permission will make it impossible to use the program.\n\n" +
                        "Or please go to: Settings -> Application -> Permissions -> Permissions to open the relevant permissions, otherwise the function will not work properly!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermission();
                    }

                }).show();
    }

    //权限通过
    protected void permissionGrant() {
    }
}



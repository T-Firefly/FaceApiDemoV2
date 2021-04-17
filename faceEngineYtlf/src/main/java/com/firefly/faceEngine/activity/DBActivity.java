package com.firefly.faceEngine.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.firefly.faceEngine.App;
import com.firefly.fireflyapidemo.Tools;
import com.intellif.YTLFFaceManager;
import com.firefly.faceEngine.dblib.DBManager;
import com.firefly.faceEngine.dblib.bean.Person;
import com.intellif.arctern.base.ArcternFeatureResult;
import com.intellif.arctern.base.ArcternImage;
import com.intellif.arctern.base.ArcternRect;
import com.intellif.arctern.base.ExtractCallBack;
import com.firefly.arcterndemo.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;
import java.util.List;

public class DBActivity extends BaseActivity implements ExtractCallBack {
    private ImageView image;
    private EditText et_name;
    private Button btn_register;
    private DBManager dbManager;
    private Bitmap faceBitmap;
    private String mBitmapPath = "";
    private byte[] bitmapFeature = null;

    private YTLFFaceManager YTLFFace = YTLFFaceManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        initView();
        initDb();
    }

    private void initView() {
        setActionBarTitle(R.string.ytlf_dictionaries13);
        image = findViewById(R.id.image);
        et_name = findViewById(R.id.et_name);
        btn_register = findViewById(R.id.btn_register);

    }

    private void initDb() {
        dbManager = App.getInstance().getDbManager();
    }

    public void onRegister(View view) {
        String name = et_name.getText().toString();
        if (faceBitmap == null) {
            showShortToast(R.string.ytlf_dictionaries14);
            return;
        }

        if ("".equals(name)) {
            showShortToast(R.string.ytlf_dictionaries15);
            return;
        }

        if (bitmapFeature != null) {
            long searchId = YTLFFace.doSearch(bitmapFeature);
            if (searchId > 0) {
                Tools.debugLog("searchId=%s", searchId);
                showShortToast(R.string.ytlf_dictionaries44);
                return;
            }
        }

        if (bitmapFeature != null) {
            long id = dbManager.insertPerson(name, bitmapFeature);
            //载入内存
            int result = YTLFFace.dataBaseAdd(id, bitmapFeature);
            String s = getString(result == 0 ? R.string.ytlf_dictionaries16 : R.string.ytlf_dictionaries17);
            showShortToast(s);
            Tools.debugLog(s);
            if (result == 0) {
                et_name.setText("");
                image.setImageResource(R.mipmap.ic_launcher);
                bitmapFeature = null;
                mBitmapPath = "";
            }
        }
    }

    private int getFeature(String bitmapPath) {
        return YTLFFace.doFeature(bitmapPath, this);
    }

    private int getFeature(Bitmap bitmap) {
        return YTLFFace.doFeature(bitmap, this);
    }

    @Override
    public void onExtractFeatureListener(ArcternImage arcternImage, byte[][] features, ArcternRect[] arcternRects) {
        if (features.length > 0) {
            bitmapFeature = features[0];
            Tools.debugLog("bitmapFeature.length： " + bitmapFeature.length);
        } else {
            Tools.debugLog("feature is empty！！！");
        }
    }

    public void selectImage(View view) {
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER_ONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER_ONE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    mBitmapPath = images.get(0).path;
                    Bitmap bitmap = BitmapFactory.decodeFile(mBitmapPath);
                    image.setImageBitmap(bitmap);
                    faceBitmap = bitmap;
                    Tools.debugLog("bitmap path:%s", mBitmapPath);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int result = getFeature(mBitmapPath);
                            Tools.debugLog("result: %s", result);
                        }
                    }).start();
                }
            }
        }
    }

    public void onLoadPersons(View view) {
        List<Person> personList = dbManager.getPersonList();
        Tools.debugLog(personList);
        if (personList.size() <= 0) {
            showShortToast(R.string.ytlf_dictionaries18);
            return;
        }
        long[] ids = new long[personList.size()];
        byte[][] features = new byte[personList.size()][];

        for (int i = 0; i < personList.size(); i++) {
            Person person = personList.get(i);
            ids[i] = person.getId();
            features[i] = person.getFeature();
        }
        int result = YTLFFace.dataBaseAdd(ids, features);
        showShortToast(result == 0 ? R.string.ytlf_dictionaries3 : R.string.ytlf_dictionaries4);
    }

    public void onDeletePersons(View view) {
        List<Person> personList = dbManager.getPersonList();
        if (personList.size() == 0) {
            Tools.toast("DB is empty");
            return;
        }

        int number = 0;
        for (int i = 0; i < personList.size(); i++) {
            Person person = personList.get(i);
            int flag = YTLFFace.dataBaseDelete(person.getId());
            if (flag == 0) {
                number++;
                dbManager.deletePerson(person);
            }

            Tools.toast(" Deletion complete, %s people were deleted", number);
        }
    }

    public void onClear(View view) {
        int result = YTLFFace.dataBaseClear();
        showShortToast(result == 0 ? R.string.ytlf_dictionaries3 : R.string.ytlf_dictionaries4);
    }
}

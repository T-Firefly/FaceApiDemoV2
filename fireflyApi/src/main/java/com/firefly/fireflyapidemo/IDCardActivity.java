package com.firefly.fireflyapidemo;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firefly.api.face.idcard.IDCardUtil;
import com.firefly.id_card.ICCardBean;
import com.firefly.id_card.IDCardBean;
import com.firefly.id_card.IDCardConfig;

/***
 * 1.只支持ICCard
 * 2.支持ICCard和IDCard 即身份证
 */
public class IDCardActivity extends BaseActivity implements IDCardUtil.IDCardCallBack {
    //是否连接上后台服务
    private boolean isMachineConnect = false;
    private int mReadMode;
    private int mICcardType = IDCardConfig.ICCARD_TYPE_TYPEA;
    private int mIDCardReadCount = 0;//身份证读卡次数

    private TextView mIDcardDeviceConnect;
    private ImageView mIDcardHeadIcon;
    private TextView mIDcardMessage;
    private TextView mIDcardReadCount;
    private TextView mIDcardUUID;
    private TextView mICCardID;
    private RadioGroup mICcardTypeRadioGroup;
    private IDCardUtil idCardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_v2);
        idCardUtil =  new IDCardUtil();
        initView();

        Tools.showLoadingProgressAutoDismiss(this, new Runnable() {
            @Override
            public void run() {
                initViewData();
                initIDCardReadMode();
            }
        });
    }

    private void initView() {
        mIDcardDeviceConnect = findViewById(R.id.idcard_connect);
        mIDcardHeadIcon = findViewById(R.id.idcard_head_icon);
        mIDcardMessage = findViewById(R.id.idcard_message);
        mIDcardReadCount = findViewById(R.id.idcard_read_count);
        mIDcardUUID = findViewById(R.id.idcard_uuid);
        mICCardID = findViewById(R.id.iccard_id);

        mICcardTypeRadioGroup = findViewById(R.id.iccard_read_type);
        mICcardTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.iccard_read_typea) {//iccard的大端模式
                    idCardUtil.setICCardEndianMode(true);
                } else if (id == R.id.iccard_read_typeb) {//iccard的小端模式
                    idCardUtil.setICCardEndianMode(false);
                }
                mICCardID.setText(null);
            }
        });

        RadioGroup mModeRadioGroup = findViewById(R.id.idcard_mode_rg);
        mModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.idcard_mode_rb_idcard) {//身份证
                    setVisibility(true, R.id.lyt_idcard);
                    setVisibility(false, R.id.lyt_iccard);
                    mReadMode = IDCardConfig.READCARD_MODE_IDENTITY_CARD;

                } else if (id == R.id.idcard_mode_rb_iccard) { //IC卡
                    setVisibility(false, R.id.lyt_idcard);
                    setVisibility(true, R.id.lyt_iccard);
                    mReadMode = IDCardConfig.READCARD_MODE_IC_CARD;
                }
                switchReadMode();
            }
        });
    }

    private void initViewData(){
        if (idCardUtil.isSupportIDCard()) {
            setTextSupport(R.id.txt_id_card);
        } else {
            setTextSupport(R.id.txt_id_card, "IDCard", false);
        }

        if (idCardUtil.isSupportICCard()) {
            setTextSupport(R.id.txt_ic_card);
        } else {
            for (int i = 0; i < mICcardTypeRadioGroup.getChildCount(); i++) {
                mICcardTypeRadioGroup.getChildAt(i).setEnabled(false);
            }
            setTextSupport(R.id.txt_ic_card, "ICCard", false);
        }
    }

    private void initIDCardReadMode(){
        if (idCardUtil.isSupportIDCard()) {
            mReadMode = IDCardConfig.READCARD_MODE_IDENTITY_CARD;
        } else if (idCardUtil.isSupportICCard()) {
            mReadMode = IDCardConfig.READCARD_MODE_IC_CARD;
        }
    }

    private void switchReadMode() {
        if (isMachineConnect) {
            idCardUtil.setModel(mReadMode);
        }

        switch (mReadMode) {
            case IDCardConfig.READCARD_MODE_IDENTITY_CARD://身份证
                mIDCardReadCount = 0;
                mIDcardReadCount.setText(mIDCardReadCount + "");
                mIDcardMessage.setText(null);
                mIDcardHeadIcon.setImageResource(R.mipmap.idcard_default_head_icon);

                break;

            case IDCardConfig.READCARD_MODE_IC_CARD://IC卡
                mICCardID.setText(null);

                if (mICcardTypeRadioGroup.getCheckedRadioButtonId() == R.id.iccard_big) {
                    mICcardType = IDCardConfig.ICCARD_TYPE_TYPEA;
                } else if (mICcardTypeRadioGroup.getCheckedRadioButtonId() == R.id.iccard_little) {
                    mICcardType = IDCardConfig.ICCARD_TYPE_TYPEB;
                }

                if (isMachineConnect) {
                    idCardUtil.setICCardType(mICcardType);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        idCardUtil.startIDCardListener(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        idCardUtil.stopIDCardListener(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (idCardUtil.handleEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onMachineConnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Tools.debugLog("onMachineConnect");
                isMachineConnect = true;
                switchReadMode();
                mIDcardDeviceConnect.setText(R.string.testcase_idcard_connect);
                mIDcardDeviceConnect.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onModeChanged(int mode) {
        Tools.debugLog("onModeChanged:" + mode);
    }

    //获取身份证信息
    @Override
    public void onSwipeIDCard(final IDCardBean info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIDCardReadCount++;
                mIDcardReadCount.setText(mIDCardReadCount + "");
                if (info != null) {
                    mIDcardHeadIcon.setImageBitmap(BitmapFactory.decodeFile(info.getPhoto()));
                    mIDcardMessage.setText(getString(R.string.firefly_api_dictionaries12,
                            info.getName(),
                            info.getSex(),
                            info.getNation(),
                            info.getBirthDateStr(),
                            info.getAddress(),
                            info.getNum(),
                            info.getIssue(),
                            info.getCreateTimeStr(),
                            info.getValidTimeStr()));

                    Tools.debugLog("picture:" + info.getPhoto());

                        /*mIDcardMessage.setText("姓名：" + info.getName() + "\n" +
                                "性别：" + info.getSex() + "\n" +
                                "民族：" + info.getNation() + "\n" +
                                "出生年月：" + info.getBirthDateStr() + "\n" +
                                "居住地：" + info.getAddress() + "\n" +
                                "身份证号：" + info.getNum() + "\n" +
                                "签发机关：" + info.getIssue() + "\n" +
                                "有效期：" + info.getCreateTimeStr() + "-" + info.getValidTimeStr() + "\n");*/
                }
            }
        });
    }

    @Override
    public void onSwipeICCard(final ICCardBean info) {
        if (info != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mICCardID.setText(info.getIcID());
                }
            });
        }
    }

    @Override
    public void onSwipeIDCardUUID(final String uuid) {
        Tools.debugLog("onSwipeIDCardUUID:" + uuid);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIDcardUUID.setText(uuid);
            }
        });
    }


}

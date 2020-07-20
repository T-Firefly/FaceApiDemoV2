package com.firefly.faceEngine.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.firefly.arcterndemo.R;


public class LoadingProgress {
    public Dialog mDialog;
    private Context mContext;

    public LoadingProgress(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_custom, null);
        mContext = context;
        mDialog = new Dialog(context, R.style.CustomDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        mDialog.getWindow().setAttributes(lp);
    }

    public void show() {
        if (mDialog != null) {
            try {
                mDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
            }
        }
    }

}

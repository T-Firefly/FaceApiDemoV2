package com.firefly.fireflyapidemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


public class BaseActivity extends AppCompatActivity {
    protected AppCompatActivity content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.init(this);
        content = this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (enableBack()) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            showActionBarTitle();
        }
    }

    protected boolean enableBack() {
        return true;
    }

    protected void setActionBarTitle(@StringRes int var1){
        setActionBarTitle(getString(var1));
    }

    protected void setActionBarTitle(CharSequence title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void showActionBarTitle(){
        try {
            if (getIntent().getExtras() != null) {
                String title = getIntent().getExtras().getString("title", "");
                if (!TextUtils.isEmpty(title)) {
                    setActionBarTitle(title);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showShortToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    public void showShortToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void setText(int id, CharSequence text) {
        ((TextView) findViewById(id)).setText(text);
    }

    public void setTextSupport(int id, CharSequence text, boolean isSupport) {
        if (isSupport) {
            setVisibility(false, id);
        } else {
            setVisibility(true, id);
            setText(id, getText(R.string.firefly_api_dictionaries20) + " " + text);
        }
    }

    public void setTextSupport(int id) {
        setTextSupport(id, null, true);
    }

    public void setText(int id, int text) {
        ((TextView) findViewById(id)).setText(text);
    }

    public void setVisibility(boolean isVisibility, int... ids) {
        for (int id : ids) {
            if(findViewById(id)!= null){
                findViewById(id).setVisibility(isVisibility ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static String getTimeShort() {
        return new SimpleDateFormat("HH:mm:ss ").format(new Date());
    }
}

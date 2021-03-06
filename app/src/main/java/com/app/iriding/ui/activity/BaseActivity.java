package com.app.iriding.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by 王海 on 2015/5/24.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            init();
        }catch (Exception e){
            Log.e("CCCCCCCCCC",e.toString());
        }

    }

    public void init(){
        setContentView();
        findViews();
        getData();
        showContent();
    }

    public abstract void setContentView();
    public abstract void findViews();
    public abstract void getData();
    public abstract void showContent();
}

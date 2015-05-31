package com.app.iriding.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.service.MyLocationManager;
import com.app.iriding.service.TestService;
import com.app.iriding.util.SwitchJsonString;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.RevealColorView;

/**
 * Created by 王海 on 2015/5/27.
 */
public class TimingActivity extends BaseActivity implements View.OnClickListener{
    private MapView mMapView = null; // 地图控件
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;// 当前定位的模式
    private int mCurrentStyle = 2;// 地图定位的模式 2表示陀螺仪模式 0表示正常模式

    private ImageButton ib_timing_switching;
    private ImageButton ib_timing_myloc;
    List<LatLng> pts = new ArrayList<LatLng>();// 绘制折线的坐标数组
    private boolean isRiding = false;
    private Chronometer chronometer;
    private Chronometer chronometerRest;
    private TextView tv_timing_distance;// 显示总里程
    private TextView tv_timing_current;// 显示当前速度
    private TextView tv_timing_average;// 显示平均速度
    private TextView tv_timing_maximum;// 显示最高速度
    private ImageButton ib_timing_continue;
    private ImageButton ib_timing_pause;
    private ImageButton ib_timing_start;
    private ImageButton ib_timing_finish;
    private ImageButton ib_timing_color;
    private ImageButton ib_timing_location;
    private ImageButton ib_timing_location2;

    private RevealColorView revealColorView;
    private int backgroundColor1;
    private int backgroundColor2;
    private int backgroundColor3;
    private int backgroundColor4;
    private int backgroundColor0;
    private int changecolor = 1;

    private FrameLayout fl_timing_contor;

    private long mBaseTime = SystemClock.elapsedRealtime();
    private long recordingTime = 0;// 记录下来的总时间
    private long ridingTime = 0;// 记录下来骑行时间
    private boolean statusRun = false;

    private View infoContainer = null;

    private float maxSpeed = 0;// 保存最高速度
    private float averageSpeed = 0;// 保存平均速度
    private double distance = 0.0;// 保存骑行里程总数

    private MyLocationManager myLocationManager = null;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;


    @Override
    public void setContentView() {
        setContentView(R.layout.timing_activity);
    }

    @Override
    public void findViews() {
        chronometer = (Chronometer) findViewById(R.id.ch_timing_time);
        chronometerRest = (Chronometer) findViewById(R.id.ch_timing_rest);
        tv_timing_distance = (TextView) findViewById(R.id.tv_timing_distance);
        tv_timing_current = (TextView) findViewById(R.id.tv_timing_current);
        tv_timing_average = (TextView) findViewById(R.id.tv_timing_average);
        tv_timing_maximum = (TextView) findViewById(R.id.tv_timing_maximum);

        ib_timing_continue = (ImageButton) findViewById(R.id.ib_timing_continue);
        ib_timing_pause = (ImageButton) findViewById(R.id.ib_timing_pause);
        ib_timing_start = (ImageButton) findViewById(R.id.ib_timing_start);
        ib_timing_finish = (ImageButton) findViewById(R.id.ib_timing_finish);
        ib_timing_color = (ImageButton) findViewById(R.id.ib_timing_color);
        ib_timing_location = (ImageButton) findViewById(R.id.ib_timing_location);
        ib_timing_location2 = (ImageButton) findViewById(R.id.ib_timing_location2);

        fl_timing_contor = (FrameLayout) findViewById(R.id.fl_timing_contor);

        revealColorView = (RevealColorView) findViewById(R.id.rlv_timing_reveal);

        ib_timing_switching = (ImageButton) findViewById(R.id.ib_timing_switching);
        ib_timing_myloc = (ImageButton) findViewById(R.id.ib_timing_myloc);
        mMapView = (MapView) findViewById(R.id.mv_timing_bmapView);// 获取地图控件引用
        mMapView.showZoomControls(false);// 不显示缩放控件
        infoContainer = findViewById(R.id.fl_timing_baidumap);
    }

    @Override
    public void getData() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this); // 获取实例
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcasttest.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter); // 注册本地广播监听器
        backgroundColor1 = Color.parseColor("#8bc34a");
        backgroundColor2 = Color.parseColor("#3f51b5");
        backgroundColor3 = Color.parseColor("#00bcd4");
        backgroundColor4 = Color.parseColor("#e91e63");
        backgroundColor0 = Color.parseColor("#0091EA");
        myLocationManager = new MyLocationManager(mMapView);// 初始化地图和定位的相关信息
    }

    @Override
    public void showContent() {
        // 设置监听
        ib_timing_switching.setOnClickListener(this);
        ib_timing_myloc.setOnClickListener(this);
        ib_timing_continue.setOnClickListener(this);
        ib_timing_pause.setOnClickListener(this);
        ib_timing_start.setOnClickListener(this);
        ib_timing_finish.setOnClickListener(this);
        ib_timing_color.setOnClickListener(this);
        ib_timing_location.setOnClickListener(this);
        ib_timing_location2.setOnClickListener(this);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            ridingTime = (chronometer.getBase() - chronometerRest.getBase());
            tv_timing_average.setText(ridingTime+"");
            tv_timing_current.setText(intent.getStringExtra("Speed"));
            tv_timing_maximum.setText(intent.getStringExtra("MaxSpeed"));
            tv_timing_distance.setText(intent.getStringExtra("Distance"));
        }
    }

    @Override
    public void onClick(View view) {
        chronometer.setFormat("%s");
        chronometerRest.setFormat("%s");
        final Point p = getLocationInView(revealColorView, view);
        final Point pi = getLocationInView(revealColorView, view);
        switch (view.getId()){
            case R.id.ib_timing_start : // 开始骑行
                myLocationManager.changeIsRiding(true);// 打开定位
                myLocationManager.locationClientStart();// 打开传感器
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                statusRun = true; // 骑行状态
                ib_timing_start.setVisibility(View.GONE);// 开始按钮消失
                ib_timing_pause.setVisibility(View.VISIBLE);// 暂停按钮显示出来
                ib_timing_finish.setVisibility(View.VISIBLE);// 完成按钮显示出来
                isRiding = true;
                Intent intent1 = new Intent(this, TestService.class);
                startService(intent1);
                break;
            case R.id.ib_timing_pause : // 暂停骑行
                if (statusRun){
                    chronometerRest.setBase(SystemClock.elapsedRealtime() - recordingTime);
                    chronometerRest.start();
                    statusRun = false;
                    ib_timing_pause.setVisibility(View.INVISIBLE);
                    ib_timing_continue.setVisibility(View.VISIBLE);// 继续按钮显示出来
                }
                break;
            case R.id.ib_timing_continue : // 继续骑行
                if (!statusRun){
                    chronometerRest.stop();
                    recordingTime = SystemClock.elapsedRealtime()- chronometerRest.getBase();// 保存这次记录了的时间
                    statusRun = true;
                    ib_timing_continue.setVisibility(View.INVISIBLE);
                    ib_timing_pause.setVisibility(View.VISIBLE);// 暂停按钮显示出来
                }
                break;
            case R.id.ib_timing_finish : // 完成骑行
                myLocationManager.locationClientStop();// 关闭定位
                mBaseTime = SystemClock.elapsedRealtime();
                chronometer.setBase(mBaseTime);
                chronometer.stop();
                chronometerRest.setBase(mBaseTime);
                chronometerRest.stop();
                recordingTime = 0;
                statusRun = false;
                ib_timing_continue.setVisibility(View.INVISIBLE);
                ib_timing_pause.setVisibility(View.INVISIBLE);// 暂停按钮显示出来
                ib_timing_start.setVisibility(View.VISIBLE);// 开始按钮消失
                ib_timing_finish.setVisibility(View.INVISIBLE);// 开始按钮消失
                isRiding = false;
                Toast.makeText(getApplicationContext(), "" + pts.size(), Toast.LENGTH_SHORT).show();
                myLocationManager.overlayOptions(getResources().getColor(R.color.ColorPrimary));// 绘制折线
                //*********************************************************
                SwitchJsonString switchJsonString = new SwitchJsonString();
                String point = switchJsonString.toCyclingPointString(myLocationManager.getCyclingPoints());
                CyclingRecord cyclingRecord = new CyclingRecord();
                cyclingRecord.setTotalPoint(point);
                cyclingRecord.setAverageSpeed("0");
                cyclingRecord.setDistance("0");
                cyclingRecord.setMaxSpeed("0");
                cyclingRecord.setRestTime("00:00:00");
                cyclingRecord.setTotalTime("00:00:00");
                cyclingRecord.save();
                Intent intent11 = new Intent(this, TestService.class);
                stopService(intent11);
                break;
            case R.id.ib_timing_color : // 切换面板背景颜色
                switch (changecolor){
                    case 1 :
                        revealColorView.reveal(p.x, p.y, backgroundColor1, view.getHeight() / 2, 500, null);
                        changecolor++;
                        break;
                    case 2 :
                        revealColorView.reveal(p.x, p.y, backgroundColor2, view.getHeight() / 2, 500, null);
                        changecolor++;
                        break;
                    case 3 :
                        revealColorView.reveal(p.x, p.y, backgroundColor3, view.getHeight() / 2, 500, null);
                        changecolor++;
                        break;
                    case 4 :
                        revealColorView.reveal(p.x, p.y, backgroundColor4, view.getHeight() / 2, 500, null);
                        changecolor = 0;
                        break;
                    case 0 :
                        revealColorView.hide(p.x, p.y, backgroundColor0, 0, 850, null);
                        changecolor++;
                        break;
                }
                break;
            case R.id.ib_timing_location :
                toggleInformationView(pi);
                break;
            case R.id.ib_timing_location2 :
                toggleInformationView(pi);
                break;
            case R.id.ib_timing_switching : // 切换地图显示模式
                switch (mCurrentStyle){
                    case 0:
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mCurrentStyle = 2;
                        break;
                    case 2:
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mCurrentStyle = 0;
                        break;
                }
                myLocationManager.changeConfiguration(mCurrentMode);
                break;
            case R.id.ib_timing_myloc :
                myLocationManager.center2myLoc();// 移动到当前位置
                break;
        }
    }

    // 切换百度地图 显示或隐藏
    public void toggleInformationView(Point p) {
        //final View infoContainer = findViewById(R.id.fl_timing_baidumap);
        float radius = Math.max(infoContainer.getWidth(), infoContainer.getHeight()) * 2.0f;

        if (infoContainer.getVisibility() == View.INVISIBLE) {
            myLocationManager.locationClientStart();// 开启定位
            myLocationManager.orientationListenerStart();// 开启方向传感器
            Animator reveal = ViewAnimationUtils.createCircularReveal(infoContainer, p.x, p.y, 0, radius);
            reveal.setDuration(500);
            infoContainer.setVisibility(View.VISIBLE);
            reveal.start();
        } else {
            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, p.x, p.y, radius, 0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    infoContainer.setVisibility(View.INVISIBLE);
                    if (!isRiding) {// 如果不是骑行中，关闭定位
                        myLocationManager.locationClientStop();
                    }
                    myLocationManager.orientationListenerStop();// 关闭方向传感器
                }
            });
            reveal.setDuration(500);
            reveal.start();
        }
    }

    /**
    * 获取点击按钮所在的位置
    * */
    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;
        return new Point(l1[0], l1[1]);
    }


    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        Log.e("Tag", "stop");
        myLocationManager.orientationListenerStop();// 关闭方向传感器
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.e("Tag", "destory");
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myLocationManager.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    protected void onResume(){
        Log.e("Tag", "resume");
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        myLocationManager.onResume();
        myLocationManager.locationClientStart();// 开启定位
    }

    @Override
    protected void onPause(){
        Log.e("Tag", "pause");
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        myLocationManager.onPause();
        mMapView.onPause();
    }
}

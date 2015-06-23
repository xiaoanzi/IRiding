package com.app.iriding.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.iriding.R;
import com.app.iriding.model.CyclingPoint;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.service.MyLocationManager;
import com.app.iriding.service.TestService;
import com.app.iriding.ui.listener.MyOrientationListener;
import com.app.iriding.util.MyApplication;
import com.app.iriding.util.SqliteUtil;
import com.app.iriding.util.SwitchJsonString;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import at.markushi.ui.RevealColorView;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 王海 on 2015/5/27.
 */
public class TimingActivity extends BaseActivity implements View.OnClickListener{
    private final String TAG = "TimingActivity";
    private final int AnimatorTime = 500;
    private final double MIN_DISTANCE = 0.1;

    List<LatLng> pts = new ArrayList<LatLng>();// 绘制折线的坐标数组

    private MapView mMapView = null; // 地图控件
    private BaiduMap mBaiduMap;// 地图实例
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;// 当前定位的模式
    private int mXDirection;// 方向传感器X方向的值
    private int mCurrentStyle = 2;// 地图定位的模式 2表示陀螺仪模式 0表示正常模式
    private float mCurrentAccracy;// 当前的精度
    private volatile boolean isFristLocation = true;
    // 最新一次的经纬度
    private double mCurrentLantitude = 0;
    private double mCurrentLongitude = 0;

    private Chronometer chronometer;// 总时间计时控件
    private Chronometer chronometerRest;// 休息时间控件
    private TextView tv_timing_distance;// 显示总里程
    private TextView tv_timing_current;// 显示当前速度
    private TextView tv_timing_average;// 显示平均速度
    private TextView tv_timing_maximum;// 显示最高速度
    private ImageButton ib_timing_continue;// 继续按钮
    private ImageButton ib_timing_pause;// 暂停按钮
    private ImageButton ib_timing_start;// 开始按钮
    private ImageButton ib_timing_finish;// 完成按钮
    private ImageButton ib_timing_color;// 更换背景颜色按钮
    private ImageButton ib_timing_location;// 切换地图按钮
    private ImageButton ib_timing_location2;// 切换仪表盘按钮
    private ImageButton ib_timing_switching;// 切换地图的显示模式
    private ImageButton ib_timing_myloc;// 在地图上回到我的位置按钮

    private RevealColorView revealColorView;
    private FrameLayout fl_timing_contor;

    private int backgroundColor1;
    private int backgroundColor2;
    private int backgroundColor3;
    private int backgroundColor4;
    private int backgroundColor0;
    private int changecolor = 1;

    private long mBaseTime = SystemClock.elapsedRealtime();// 基准时间
    private long recordingTime = 0;// 记录下来的总时间
    private long ridingTime = 0;// 记录下来骑行时间
    private boolean isRiding = false;// 是否开始骑行(未开始 false  已开始 true)
    private boolean statusRun = false;// 骑行状态(骑行中 true  暂停状态 false);
    private View infoContainer = null;

    // 本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private NumberFormat ddf1;// 保存两位小数点

    private Toolbar toolbar;
    private CyclingRecord cyclingRecord;

    private long ltotalTime = 0;
    private long lrestTime = 0;

    private MyOrientationListener myOrientationListener;// 方向传感器的监听器

    private TestService.TestBind testBind;
    private ServiceConnection connection = new ServiceConnection() {

        @Override public void onServiceDisconnected(ComponentName name) {
            SharedPreferences.Editor editor = getSharedPreferences("lastData",MODE_PRIVATE).edit();
            editor.putString("mCurrentLantitude", 123 + "");
            editor.putString("mCurrentLongitude", 234 + "");
            editor.putString("maxLantitude", 456+"");
            editor.putString("maxtLongitude", 678+"");
            editor.putString("minLantitude", 901+"");
            editor.putString("mintLongitude", 234+"");
            editor.putString("distance", 567+"");
            editor.putFloat("maxSpeed", 34);
            editor.putInt("totalTime", 34);
            editor.putInt("restTime", 34);
            editor.putBoolean("statusRun", true);
            editor.putString("mCyclingPoints", "qqqqqqqqqqqqqqqqqq");
            editor.commit();
            Log.e(TAG, "取消绑定");
        }

        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            testBind = (TestService.TestBind) service;
            testBind.startBind();
            testBind.endBind();
            testBind.locationClientStart();// 开启定位
            if (isRiding){
                testBind.isStop(false);
                ib_timing_start.setVisibility(View.GONE);
                ib_timing_finish.setVisibility(View.VISIBLE);
                chronometer.setFormat("%s");
                chronometerRest.setFormat("%s");
                chronometer.setBase(SystemClock.elapsedRealtime() - (testBind.getToalTime() * 1000));// 读取service里面的总时间
                chronometer.start();// 开始计时
                recordingTime = testBind.getRestTime() * 1000;// 读取service里面的休息时间
                statusRun = testBind.getStatusRun();
                if (statusRun){
                    ib_timing_continue.setVisibility(View.INVISIBLE);
                    ib_timing_pause.setVisibility(View.VISIBLE);// 暂停按钮显示出来
                    chronometerRest.setBase(SystemClock.elapsedRealtime() - recordingTime);
                }else{
                    ib_timing_pause.setVisibility(View.INVISIBLE);
                    ib_timing_continue.setVisibility(View.VISIBLE);// 继续按钮显示出来
                    chronometerRest.setBase(SystemClock.elapsedRealtime() - recordingTime);
                    chronometerRest.start();
                }
            }
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.timing_activity);
    }

    @Override
    public void findViews() {
        Log.e(TAG, "onCreat");
        isRiding = ServiceIsStart();
        Log.e(TAG, isRiding+" service");
        Intent bindIntent = new Intent(TimingActivity.this, TestService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务

        toolbar = (Toolbar) findViewById(R.id.timing_toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        toolbar.setTitle("仪表盘");//设置Toolbar标题
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);// 设置toolbar图标可见
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置home按钮可以点击
        actionBar.setHomeButtonEnabled(true);// 设置返回键可用

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
        ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);
        localBroadcastManager = LocalBroadcastManager.getInstance(TimingActivity.this); // 获取实例
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcasttest.LOCAL_BROADCASTT");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter); // 注册本地广播监听器
        mBaiduMap = mMapView.getMap();// 获得地图的实例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);// 设置地图缩放级别
        mBaiduMap.setMapStatus(msu);
        initOritationListener();
    }

    @Override
    public void showContent() {
        backgroundColor1 = Color.parseColor("#8bc34a");
        backgroundColor2 = Color.parseColor("#3f51b5");
        backgroundColor3 = Color.parseColor("#00bcd4");
        backgroundColor4 = Color.parseColor("#e91e63");
        backgroundColor0 = Color.parseColor("#0091EA");

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

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        Log.e(TAG, "stop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.e(TAG, "destory");
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    //************
    @Override
    protected void onResume(){
        Log.e(TAG, "resume");
        super.onResume();
        mMapView.onResume();
        try{
            if (testBind != null){
                testBind.isStop(false);
            }
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected void onPause(){
        Log.e(TAG, "pause");
        super.onPause();
        testBind.isStop(true);
        mMapView.onPause();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        unbindService(connection);
        super.onBackPressed();
    }

    // 设置toolbar返回按钮的监听听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            unbindService(connection);// 销毁activity会自动解除与service的绑定，但是会出现报一个错误，所以在这里手动解除绑定
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        chronometer.setFormat("%s");
        chronometerRest.setFormat("%s");
        final Point p = getLocationInView(revealColorView, view);
        switch (view.getId()){
            case R.id.ib_timing_start : // 开始骑行
                startRiding();
                break;
            case R.id.ib_timing_pause : // 暂停骑行
                pauseRiding();
                break;
            case R.id.ib_timing_continue : // 继续骑行
                continueRiding();
                break;
            case R.id.ib_timing_finish : // 完成骑行
                finishRiding();
                break;
            case R.id.ib_timing_color : // 切换面板背景颜色
                changeBgColor(p,view);
                break;
            case R.id.ib_timing_location :
                toggleInformationView(p);
                break;
            case R.id.ib_timing_location2 :
                toggleInformationView(p);
                break;
            case R.id.ib_timing_switching : // 切换地图显示模式
                switchingCurrentMode();
                break;
            case R.id.ib_timing_myloc :
                LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);// 移动到当前位置
                break;
        }
    }

    public void startRiding(){
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("提示");
        mMaterialDialog.setMessage("准备好开始骑行了吗？别忘了要带东西哦~");
        mMaterialDialog.setPositiveButton("开始吧", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                // 当定位不成功的时候应该return
                try {
                    testBind.locationClientStart();// 开启定位
                    int code = testBind.getMLocType();
                    if (code == 61 || code == 161 || code == 65 || code == 66 || code == 68) {
                        isRiding = true;
                        statusRun = true;
                        testBind.changeStatusRun(statusRun);
                        testBind.startRiding();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();// 开始计时
                        ib_timing_start.setVisibility(View.GONE);
                        ib_timing_pause.setVisibility(View.VISIBLE);
                        ib_timing_finish.setVisibility(View.VISIBLE);
                        Intent bindIntent = new Intent(TimingActivity.this, TestService.class);
                        startService(bindIntent);
                    } else {
                        Toast.makeText(MyApplication.getContext(), "定位失败，请检查定位的相关设置", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        mMaterialDialog.setNegativeButton("还没有", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                return;
            }
        });

        mMaterialDialog.show();
    }

    public void pauseRiding(){
        if (statusRun){
            statusRun = false;
            testBind.changeStatusRun(statusRun);
            testBind.pauseRiding();
            chronometerRest.setBase(SystemClock.elapsedRealtime() - recordingTime);
            chronometerRest.start();
            ib_timing_pause.setVisibility(View.INVISIBLE);
            ib_timing_continue.setVisibility(View.VISIBLE);// 继续按钮显示出来
        }
    }

    public void continueRiding(){
        if (!statusRun){
            statusRun = true;
            testBind.changeStatusRun(statusRun);
            testBind.continueRiding();
            chronometerRest.stop();
            recordingTime = SystemClock.elapsedRealtime()- chronometerRest.getBase();// 保存这次记录了的时间
            ib_timing_continue.setVisibility(View.INVISIBLE);
            ib_timing_pause.setVisibility(View.VISIBLE);// 暂停按钮显示出来
        }
    }
    public void finishRiding(){
        pauseRiding();
        cyclingRecord = testBind.getCyclingRecord();
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("提示");
        mMaterialDialog.setMessage("是否要结束本次骑行？");
        if (cyclingRecord.getDistance() < MIN_DISTANCE){
            mMaterialDialog.setMessage("本次骑行距离太短，无法保存，是否要结束本次骑行？");
        }
        mMaterialDialog.setPositiveButton("是", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                try {
                    if (cyclingRecord.getDistance() > MIN_DISTANCE) {
                        isRiding = false;
                        ltotalTime = SystemClock.elapsedRealtime() - chronometer.getBase();// 获取到总的时间
                        if (!statusRun) {
                            recordingTime = SystemClock.elapsedRealtime() - chronometerRest.getBase();
                        }
                        lrestTime = recordingTime;// 获取到休息的时间
                        statusRun = false;
                        testBind.changeStatusRun(statusRun);
                        testBind.finishRiding();
                        // 重置计时器
                        mBaseTime = SystemClock.elapsedRealtime();
                        chronometer.setBase(mBaseTime);
                        chronometerRest.setBase(mBaseTime);
                        chronometer.stop();
                        chronometerRest.stop();
                        recordingTime = 0;

                        // 按钮回归初始状态
                        ib_timing_continue.setVisibility(View.INVISIBLE);
                        ib_timing_pause.setVisibility(View.INVISIBLE);
                        ib_timing_start.setVisibility(View.VISIBLE);
                        ib_timing_finish.setVisibility(View.INVISIBLE);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 保存骑行数据
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                String sDateTimeTotal = sdf.format(ltotalTime - TimeZone.getDefault().getRawOffset());// 减去时间差
                                String sDateTimeRest = sdf.format(lrestTime - TimeZone.getDefault().getRawOffset());
                                cyclingRecord.setTotalTime(ltotalTime);
                                cyclingRecord.setRestTime(lrestTime);
                                cyclingRecord.setTotalTimeStr(sDateTimeTotal);
                                cyclingRecord.setRestTimeStr(sDateTimeRest);
                                cyclingRecord.setAverageSpeed(Double.parseDouble(getAvSpeed(ltotalTime - lrestTime, cyclingRecord.getDistance())));
                                cyclingRecord.save();
                            }
                        }).start();
                        ltotalTime = 0;
                        lrestTime = 0;
                        // 停止前台service
                        Intent intent11 = new Intent(TimingActivity.this, TestService.class);
                        stopService(intent11);
                    } else {
                        Toast.makeText(MyApplication.getContext(), "本次骑行距离太短，无法保存", Toast.LENGTH_SHORT).show();
                        Intent intent11 = new Intent(TimingActivity.this, TestService.class);
                        stopService(intent11);
                        unbindService(connection);
                        finish();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        mMaterialDialog.setNegativeButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                continueRiding();
                return;
            }
        });
        mMaterialDialog.show();
    }

    public void changeBgColor(Point p,View view){
        switch (changecolor){
            case 1 :
                revealColorView.reveal(p.x, p.y, backgroundColor1, view.getHeight() / 2, AnimatorTime, null);
                changecolor++;
                break;
            case 2 :
                revealColorView.reveal(p.x, p.y, backgroundColor2, view.getHeight() / 2, AnimatorTime, null);
                changecolor++;
                break;
            case 3 :
                revealColorView.reveal(p.x, p.y, backgroundColor3, view.getHeight() / 2, AnimatorTime, null);
                changecolor++;
                break;
            case 4 :
                revealColorView.reveal(p.x, p.y, backgroundColor4, view.getHeight() / 2, AnimatorTime, null);
                changecolor = 0;
                break;
            case 0 :
                revealColorView.hide(p.x, p.y, backgroundColor0, 0, AnimatorTime, null);
                changecolor++;
                break;
        }
    }

    public void switchingCurrentMode(){
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
        MyLocationConfiguration config = new MyLocationConfiguration(
                mCurrentMode, true, null);
        mBaiduMap.setMyLocationConfigeration(config);
    }

    // 得到保留两位数的平均速度KM/H（毫秒，公里）
    public String getAvSpeed(double t, double s){
        String v = ddf1.format(s / (t / 3600000));
        return v;
    }

    // 切换百度地图 显示或隐藏
    public void toggleInformationView(Point p) {
        float radius = Math.max(infoContainer.getWidth(), infoContainer.getHeight()) * 2.0f;
        if (infoContainer.getVisibility() == View.INVISIBLE) {
            testBind.locationClientStart();// 开启定位
            mBaiduMap.setMyLocationEnabled(true);// 开启图层定位
            myOrientationListener.start();// 开启方向传感器
            Animator reveal = ViewAnimationUtils.createCircularReveal(infoContainer, p.x, p.y, 0, radius);
            reveal.setDuration(AnimatorTime);
            infoContainer.setVisibility(View.VISIBLE);
            reveal.start();
        } else {
            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, p.x, p.y, radius, 0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    infoContainer.setVisibility(View.INVISIBLE);
                    mBaiduMap.setMyLocationEnabled(false);// 关闭图层定位
                    myOrientationListener.stop();// 关闭方向传感器
                }
            });
            reveal.setDuration(AnimatorTime);
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


    // 广播接收器 用于更新UI显示的数据
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("isRing",-1) == 0){
                ridingTime = ((SystemClock.elapsedRealtime() - chronometer.getBase()) - recordingTime);
                tv_timing_average.setText(getAvSpeed(ridingTime, Double.parseDouble(intent.getStringExtra("Distance"))));
                tv_timing_current.setText(intent.getStringExtra("Speed"));
                tv_timing_maximum.setText(intent.getStringExtra("MaxSpeed"));
                tv_timing_distance.setText(intent.getStringExtra("Distance"));
            }
            BDLocation location = (BDLocation) getIntent().getParcelableExtra("BDLocation");
            if(location == null){
                Log.e(TAG,"is null");
            }
            mCurrentAccracy = intent.getFloatExtra("mRadius",0);
            mCurrentLantitude = intent.getDoubleExtra("mCurrentLantitude", 0);
            mCurrentLongitude = intent.getDoubleExtra("mCurrentLongitude", 0);
            MyLocationData locData = new MyLocationData.Builder()// 构造定位数据
                    .accuracy(mCurrentAccracy)
                    .direction(mXDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLantitude)
                    .longitude(mCurrentLongitude).build();
            mBaiduMap.setMyLocationData(locData);// 设置定位数据

            MyLocationConfiguration config = new MyLocationConfiguration(
                    mCurrentMode, true, null);
            mBaiduMap.setMyLocationConfigeration(config);
            if (isFristLocation){// 第一次定位时，将地图位置移动到当前位置
                isFristLocation = false;
                LatLng ll = new LatLng(mCurrentLantitude,
                        mCurrentLongitude);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener(){
        myOrientationListener = new MyOrientationListener(MyApplication.getContext());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentAccracy)
                        .direction(mXDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                        .latitude(mCurrentLantitude)
                        .longitude(mCurrentLongitude).build();
                mBaiduMap.setMyLocationData(locData);// 设置定位数据
                MyLocationConfiguration config = new MyLocationConfiguration(
                        mCurrentMode, true, null);// 最后一个参数可以传入自定义的图标
                mBaiduMap.setMyLocationConfigeration(config);
            }
        });
    }

    private boolean ServiceIsStart(){
        ActivityManager mActivityManager =
                (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);
        final String className = "com.app.iriding.service.TestService";
        for(int i = 0; i < mServiceList.size(); i ++){
            if(className.equals(mServiceList.get(i).service.getClassName())){
                return true;
            }
        }
        return false;
    }
}

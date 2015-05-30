package com.app.iriding.ui.activity.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.iriding.R;
import com.app.iriding.ui.listener.MyOrientationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.RevealColorView;

/**
 * Created by 王海 on 2015/5/24.
 */
public class TestActivity extends Activity implements View.OnClickListener{
    /**
     * 地图控件
     */
    private MapView mMapView = null;
    /**
     * 地图实例
     */
    private BaiduMap mBaiduMap;
    /**
     * 定位的客户端
     */
    private LocationClient mLocationClient;
    /**
     * 定位的监听器
     */
    public MyLocationListener mMyLocationListener;
    /**
     * 当前定位的模式
     */
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    /***
     * 是否是第一次定位
     */
    private volatile boolean isFristLocation = true;

    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    /**
     * 当前的精度
     */
    private float mCurrentAccracy;
    /**
     * 方向传感器的监听器
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;

    /**
     * 地图定位的模式
     */
    private int mCurrentStyle = 2;

    private ImageButton ib_timing_switching;
    private ImageButton ib_timing_myloc;
    List<LatLng> pts = new ArrayList<LatLng>();
    private boolean xian = false;
    private Chronometer chronometer;
    private Chronometer chronometerRest;
    private TextView tv_timing_distance;
    private TextView tv_timing_current;
    private TextView tv_timing_average;
    private TextView tv_timing_maximum;
    private Button button1;
    private Button button2;
    private Button button3;
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
    private boolean statusRun = false;
//    private SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timing_activity);
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
        backgroundColor1 = Color.parseColor("#8bc34a");
        backgroundColor2 = Color.parseColor("#3f51b5");
        backgroundColor3 = Color.parseColor("#00bcd4");
        backgroundColor4 = Color.parseColor("#e91e63");
        backgroundColor0 = Color.parseColor("#0091EA");

        // 第一次定位
        isFristLocation = true;
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mv_timing_bmapView);
        ib_timing_switching = (ImageButton) findViewById(R.id.ib_timing_switching);
        ib_timing_myloc = (ImageButton) findViewById(R.id.ib_timing_myloc);
        // 设置监听
        ib_timing_switching.setOnClickListener(this);
        ib_timing_myloc.setOnClickListener(this);
        // 不显示缩放控件
        mMapView.showZoomControls(false);
        // 获得地图的实例
        mBaiduMap = mMapView.getMap();
        // 设置地图缩放级别
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);

        mBaiduMap.setMapStatus(msu);
        // 初始化定位
        initMyLocation();
        // 初始化传感器
        initOritationListener();

        ib_timing_continue.setOnClickListener(this);
        ib_timing_pause.setOnClickListener(this);
        ib_timing_start.setOnClickListener(this);
        ib_timing_finish.setOnClickListener(this);
        ib_timing_color.setOnClickListener(this);
        ib_timing_location.setOnClickListener(this);
        ib_timing_location2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        chronometer.setFormat("%s");
        chronometerRest.setFormat("%s");
        final Point p = getLocationInView(revealColorView, view);
        switch (view.getId()){
            case R.id.ib_timing_start : // 开始骑行
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                statusRun = true; // 骑行状态
                ib_timing_start.setVisibility(View.GONE);// 开始按钮消失
                ib_timing_pause.setVisibility(View.VISIBLE);// 暂停按钮显示出来
                ib_timing_finish.setVisibility(View.VISIBLE);// 完成按钮显示出来
                xian = true;
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
                xian = false;
                Toast.makeText(getApplicationContext(), "" + pts.size(), Toast.LENGTH_SHORT).show();
                OverlayOptions ooCircle = new PolylineOptions()
                        .color(getResources().getColor(R.color.ColorPrimary))//折线的颜色
                        .dottedLine(false)//折线是否为虚线
                        .points(pts);//折线的点坐标
                mBaiduMap.addOverlay(ooCircle);
                break;
            case R.id.ib_timing_color :
                switch (changecolor){
                    case 1 :
                        revealColorView.reveal(p.x, p.y, backgroundColor1, view.getHeight() / 2, 850, null);
                        changecolor++;
                        break;
                    case 2 :
                        revealColorView.reveal(p.x, p.y, backgroundColor2, view.getHeight() / 2, 850, null);
                        changecolor++;
                        break;
                    case 3 :
                        revealColorView.reveal(p.x, p.y, backgroundColor3, view.getHeight() / 2, 850, null);
                        changecolor++;
                        break;
                    case 4 :
                        revealColorView.reveal(p.x, p.y, backgroundColor4, view.getHeight() / 2, 850, null);
                        changecolor = 0;
                        break;
                    case 0 :
                        revealColorView.hide(p.x, p.y, backgroundColor0, 0, 1000, null);
                        changecolor++;
                        break;
                }
                break;
            case R.id.ib_timing_location :
                    toggleInformationView(view);
                break;
            case R.id.ib_timing_location2 :
                    toggleInformationView(view);
                break;
            case R.id.ib_timing_switching :
                switch (mCurrentStyle)
                {
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
                break;
            case R.id.ib_timing_myloc :
                center2myLoc();
                break;
        }
    }

    // 切换百度地图
    public void toggleInformationView(View view) {
        final View infoContainer = findViewById(R.id.fl_timing_baidumap);
        final Point p = getLocationInView(infoContainer, view);
        float radius = Math.max(infoContainer.getWidth(), infoContainer.getHeight()) * 2.0f;

        if (infoContainer.getVisibility() == View.INVISIBLE) {
            Animator reveal = ViewAnimationUtils.createCircularReveal(infoContainer, p.x, p.y, 0, radius);
            reveal.setDuration(850);
            infoContainer.setVisibility(View.VISIBLE);
            reveal.start();
        } else {
            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, p.x, p.y, radius, 0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    infoContainer.setVisibility(View.INVISIBLE);
                }
            });
            reveal.setDuration(850);
            reveal.start();
        }
    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(
                getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
                {
                    @Override
                    public void onOrientationChanged(float x)
                    {
                        mXDirection = (int) x;
                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                        // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLantitude)
                                .longitude(mCurrentLongitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        // 设置自定义图标
//                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                                .fromResource(R.drawable.navi_map_gps_locked);
                        MyLocationConfiguration config = new MyLocationConfiguration(
                                mCurrentMode, true, null);// 最后一个参数可以传入自定义的图标
                        mBaiduMap.setMyLocationConfigeration(config);

                    }
                });
    }

    /**
     * 初始化定位相关代码
     */
    private void initMyLocation()
    {
        // 定位初始化
        mLocationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000); // 设置发起定位请求的间隔时间
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mXDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mCurrentAccracy = location.getRadius();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            mCurrentLantitude = location.getLatitude();// 绘制线路需要的数据
            mCurrentLongitude = location.getLongitude();// 绘制线路需要的数据
            if (xian){
                LatLng pt1 = new LatLng(mCurrentLantitude, mCurrentLongitude);
                pts.add(pt1);
            }
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mCurrentMode, true, null);
            mBaiduMap.setMyLocationConfigeration(config);
            // 第一次定位时，将地图位置移动到当前位置
            if (isFristLocation)
            {
                isFristLocation = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

    }

    private Point getLocationInView(View src, View target) {

        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }


    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位；
     * 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
     */
    private void center2myLoc()
    {
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    protected void onStart()
    {
        // 开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
        {
            mLocationClient.start();
        }
        // 开启方向传感器
        myOrientationListener.start();
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        // 关闭图层定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        // 关闭方向传感器
        myOrientationListener.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}

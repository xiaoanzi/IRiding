package com.app.iriding.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.iriding.model.CyclingPoint;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.ui.listener.MyOrientationListener;
import com.app.iriding.util.MyApplication;
import com.app.iriding.util.SwitchJsonString;
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
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 王海 on 2015/5/29.
 */
public class MyLocationManager {
    private final String TAG = "MyLocationManager";
    private MapView mMapView = null; // 地图控件
    private BaiduMap mBaiduMap;// 地图实例
    private LocationClient mLocationClient;// 定位的客户端
    public MyLocationListener mMyLocationListener;// 定位的监听器
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;// 当前定位的模式
    private volatile boolean isFristLocation = true;// 是否是第一次定位
    private volatile boolean isFristLocationAdd = true;// 是否是开始骑行第一次加入坐标

    // 最新一次的经纬度
    private double mCurrentLantitude = 0;
    private double mCurrentLongitude = 0;

    // 上一次的经纬度
    private double lastLantitude = 0;
    private double lastLongitude = 0;

    // 最大最小纬度经度
    private double maxLantitude = 0;
    private double maxtLongitude = 0;
    private double minLantitude = 0;
    private double mintLongitude = 0;

    private int mLocType = 0;

    private float mCurrentAccracy;// 当前的精度
    private MyOrientationListener myOrientationListener;// 方向传感器的监听器
    private int mXDirection;// 方向传感器X方向的值
    private int mCurrentStyle = 2;// 地图定位的模式 2表示陀螺仪模式 0表示正常模式
    private boolean isRiding = false;
    List<CyclingPoint> mCyclingPoints = new ArrayList<CyclingPoint>();// 绘制折线的坐标数组
    List<LatLng> pts = new ArrayList<LatLng>();// 绘制折线的坐标数组

    private LocalBroadcastManager localBroadcastManager;

    private double distance = 0;// 总距离
    private float maxSpeed = 0;// 最高速度
    private float currentSpeed = 0;// 最高速度
    private NumberFormat ddf1;
    private boolean isView = false;// 程序是否为可见状态

    public MyLocationManager(MapView mapView){
        this.mMapView = mapView;
        mBaiduMap = mMapView.getMap();// 获得地图的实例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);// 设置地图缩放级别
        mBaiduMap.setMapStatus(msu);
        localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext()); // 获取广播实例
        ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);
        initMyLocation();
        initOritationListener();
    }

    /**
     * 初始化定位相关代码
     */
    private void initMyLocation(){
        // 定位初始化
        mLocationClient = new LocationClient(MyApplication.getContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();// 设置定位的相关配置
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2000); // 设置发起定位请求的间隔时间
        mLocationClient.setLocOption(option);
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

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()// 构造定位数据
                    .accuracy(location.getRadius())
                    .direction(mXDirection)
                    .latitude(location.getLatitude())// 此处设置开发者获取到的方向信息，顺时针0-360
                    .longitude(location.getLongitude()).build();
            mCurrentAccracy = location.getRadius();
            mBaiduMap.setMyLocationData(locData);// 设置定位数据
            LatLng pt1 = new LatLng(mCurrentLantitude, mCurrentLongitude);
/*            Log.e(TAG,mCurrentLantitude+"");
            Log.e(TAG,mCurrentLongitude+"");*/

            mLocType = location.getLocType();
/*            Log.e(TAG,mLocType+"code");*/

            mCurrentLantitude = location.getLatitude();// 绘制线路需要的数据
            mCurrentLongitude = location.getLongitude();// 绘制线路需要的数据
            if (isRiding){
                if (lastLantitude != mCurrentLantitude || lastLongitude != mCurrentLongitude){
                    lastLantitude = mCurrentLantitude;
                    lastLongitude = mCurrentLongitude;
                    if (isFristLocationAdd){
                        isFristLocationAdd = false;
                        minLantitude = mCurrentLantitude;
                        mintLongitude = mCurrentLongitude;
                    }
                    if (minLantitude > mCurrentLantitude){
                        minLantitude = mCurrentLantitude;
                    }
                    if (mintLongitude > mCurrentLongitude){
                        mintLongitude = mCurrentLongitude;
                    }
                    if (maxLantitude < mCurrentLantitude){
                        maxLantitude = mCurrentLantitude;
                    }
                    if (maxtLongitude < mCurrentLongitude){
                        maxtLongitude = mCurrentLongitude;
                    }
                    LatLng pt2 = new LatLng(mCurrentLantitude, mCurrentLongitude);
                    pts.add(pt2);
                    CyclingPoint cyclingPoint = new CyclingPoint(mCurrentLantitude, mCurrentLongitude);
                    mCyclingPoints.add(cyclingPoint);
                    distance += DistanceUtil. getDistance(pt1, pt2);// 单位米
                    currentSpeed = location.getSpeed();
                    if (maxSpeed < currentSpeed){
                        maxSpeed = currentSpeed;
                    }
                    // 不可见状态就不发送广播通知更新页面
                    if (isView){
                        try{
                            Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
                            intent.putExtra("Speed",ddf1.format(currentSpeed));
                            intent.putExtra("Distance",ddf1.format(distance/1000));
                            intent.putExtra("MaxSpeed",ddf1.format(maxSpeed));
                            localBroadcastManager.sendBroadcast(intent); // 发送本地广播
                        }catch (Exception e){
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mCurrentMode, true, null);
            mBaiduMap.setMyLocationConfigeration(config);
            if (isFristLocation){// 第一次定位时，将地图位置移动到当前位置
                isFristLocation = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

    }

    // 绘制折线getResources().getColor(R.color.ColorPrimary)
    public void overlayOptions(int rColor){
        OverlayOptions ooCircle = new PolylineOptions()
                .color(rColor)//折线的颜色
                .dottedLine(false)//折线是否为虚线
                .points(pts);//折线的点坐标
        mBaiduMap.addOverlay(ooCircle);
    }

    public List<CyclingPoint> getCyclingPoints(){
        return mCyclingPoints;
    }

    // 返回CyclingRecord用于保存
    public CyclingRecord getCyclingRecord(){
        Date date = new Date();
        long lSysTime1 = date.getTime() / 1000;   //得到秒数，Date类型的getTime()返回毫秒数
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
 //       java.util.Date dt = new Date(lSysTime1 * 1000);
        String sDateTime = sdf.format(date);
        CyclingRecord cyclingRecord = new CyclingRecord();
        cyclingRecord.setTotalPoint(SwitchJsonString.toCyclingPointString(mCyclingPoints));
        cyclingRecord.setMaxLantitude(maxLantitude);
        cyclingRecord.setMaxtLongitude(maxtLongitude);
        cyclingRecord.setMinLantitude(minLantitude);
        cyclingRecord.setMintLongitude(mintLongitude);
        cyclingRecord.setDistance(Double.parseDouble(ddf1.format(distance / 1000)));
        cyclingRecord.setMaxSpeed(Double.parseDouble(ddf1.format(maxSpeed)));
        cyclingRecord.setMdateTime(lSysTime1);
        cyclingRecord.setMdateTimeStr(sDateTime);
        return cyclingRecord;
    }

    public int getMLocType(){
        return mLocType;
    }

    public void changeConfiguration(MyLocationConfiguration.LocationMode locationMode){
        MyLocationConfiguration config = new MyLocationConfiguration(
                locationMode, true, null);
        mBaiduMap.setMyLocationConfigeration(config);
    }

    /**
     * 申请定位
     */
    public void locationClientStart(){
        mBaiduMap.setMyLocationEnabled(true);// 开启图层定位
        if (!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    /**
     * 关闭定位
     */
    public void locationClientStop(){
        // 关闭图层定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    /**
     * 开启传感器
     */
    public void orientationListenerStart(){
        myOrientationListener.start();
    }

    /**
     * 关闭方向传感器
     */
    public void orientationListenerStop(){
        myOrientationListener.stop();
    }

    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位；
     * 直接拿最近一次经纬度，快捷，但有点不稳定
     */
    public void center2myLoc(){
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

    // 改变是否为骑行状态
    public void changeIsRiding(boolean status){
        isRiding = status;
    }

    // mMapView指向的是同一个地址
    public void onDestroy(){
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
    }

    public void onResume(){
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        locationClientStart();
        isView = true;
    }

    public void onPause(){
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (!isRiding){
            locationClientStop();
        }
        isView = false;
        mMapView.onPause();
    }
}

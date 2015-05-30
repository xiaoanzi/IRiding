package com.app.iriding.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.iriding.model.CyclingPoint;
import com.app.iriding.ui.listener.MyOrientationListener;
import com.app.iriding.util.MyApplication;
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

/**
 * Created by 王海 on 2015/5/29.
 */
public class MyLocationManager {
    private MapView mMapView = null; // 地图控件
    private BaiduMap mBaiduMap;// 地图实例
    private LocationClient mLocationClient;// 定位的客户端
    public MyLocationListener mMyLocationListener;// 定位的监听器
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;// 当前定位的模式
    private volatile boolean isFristLocation = true;// 是否是第一次定位
    //最新一次的经纬度
    private double mCurrentLantitude;
    private double mCurrentLongitude;

    private float mCurrentAccracy;// 当前的精度
    private MyOrientationListener myOrientationListener;// 方向传感器的监听器
    private int mXDirection;// 方向传感器X方向的值
    private int mCurrentStyle = 2;// 地图定位的模式 2表示陀螺仪模式 0表示正常模式
    private boolean isRiding = false;
    List<CyclingPoint> mCyclingPoints = new ArrayList<CyclingPoint>();// 绘制折线的坐标数组
    List<LatLng> pts = new ArrayList<LatLng>();// 绘制折线的坐标数组

    private LocalBroadcastManager localBroadcastManager;

    public MyLocationManager(MapView mapView){
        this.mMapView = mapView;
        mBaiduMap = mMapView.getMap();// 获得地图的实例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);// 设置地图缩放级别
        mBaiduMap.setMapStatus(msu);
        localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext()); // 获取广播实例
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
        option.setScanSpan(1000); // 设置发起定位请求的间隔时间
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
        public void onReceiveLocation(BDLocation location)
        {
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
            mCurrentLantitude = location.getLatitude();// 绘制线路需要的数据
            mCurrentLongitude = location.getLongitude();// 绘制线路需要的数据
            if (isRiding){
                LatLng pt = new LatLng(mCurrentLantitude, mCurrentLongitude);
                pts.add(pt);
                CyclingPoint cyclingPoint = new CyclingPoint(mCurrentLantitude, mCurrentLongitude);
                mCyclingPoints.add(cyclingPoint);
                // 更新当前速度
                try{
                    Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
                    intent.putExtra("Speed",location.getSpeed()+"");
                    localBroadcastManager.sendBroadcast(intent); // 发送本地广播
                 }catch (Exception e){
                    Log.e("tagggg",e.toString());
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

    public void onDestroy(){
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
    }

    public void onResume(){
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        locationClientStart();
    }

    public void onPause(){
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}

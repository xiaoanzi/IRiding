package com.app.iriding.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import com.app.iriding.R;
import com.app.iriding.model.CyclingPoint;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.ui.activity.TimingActivity;
import com.app.iriding.ui.listener.MyOrientationListener;
import com.app.iriding.util.MyApplication;
import com.app.iriding.util.SwitchJsonString;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
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
public class TestService extends Service {
    private final String TAG = "TestService";
    private LocationClient mLocationClient;// 定位的客户端
    public MyLocationListener mMyLocationListener;// 定位的监听器

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

    private double distance = 0;// 总距离
    private float maxSpeed = 0;// 最高速度
    private float currentSpeed = 0;// 最高速度

    private NumberFormat ddf1;
    private int mLocType = 0;
    List<LatLng> pts = new ArrayList<LatLng>();// 绘制折线的坐标数组
    List<CyclingPoint> mCyclingPoints = new ArrayList<CyclingPoint>();// 绘制折线的坐标数组
    private boolean statusRun = false;
    private boolean isStop = false;
    private volatile boolean isFristLocationAdd = true;// 是否是开始骑行第一次加入坐标
    private LocalBroadcastManager localBroadcastManager;

    private TestBind mTestBind = new TestBind();
    public TestService() {
    }

    public class TestBind extends Binder{
        public void startBind(){
            Log.e(TAG, "startDownload executed");
        }
        public void endBind(){
            Log.e(TAG, "endDownload executed");
        }
        //申请定位
        public void locationClientStart(){
            if (!mLocationClient.isStarted()){
                mLocationClient.start();
            }
        }

        //关闭定位
        public void locationClientStop(){
            mLocationClient.stop();
        }

        // 改变是否为骑行状态
        public void changeStatusRun(boolean status){
            statusRun = status;
        }

        // 是否在后台
        public void isStop(boolean status){
            isStop = status;
        }

        public int getMLocType(){
            return mLocType;
        }
        // 返回CyclingRecord用于保存
        public CyclingRecord getCyclingRecord(){
            Date date = new Date();
            long lSysTime1 = date.getTime() / 1000;   //得到秒数，Date类型的getTime()返回毫秒数
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTestBind;
    }

    @Override public void onCreate() {
        Log.e(TAG, "onCreate");
        localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext()); // 获取广播实例
        ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);
        initMyLocation();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        if (statusRun){
            Notification notification = new Notification(R.drawable.ic_my_location_grey600_24dp, "骑行开始", System.currentTimeMillis());
            Intent notificationIntent = new Intent(this, TimingActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            notification.setLatestEventInfo(this, "IRing", "骑行进行中", pendingIntent);
            startForeground(1, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
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
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;
            LatLng pt1 = new LatLng(mCurrentLantitude, mCurrentLongitude);
            mLocType = location.getLocType();

            mCurrentLantitude = location.getLatitude();// 绘制线路需要的数据
            mCurrentLongitude = location.getLongitude();// 绘制线路需要的数据
            if (statusRun){
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
                    if (!isStop){ // 不可见状态就不发送广播通知更新页面
                        Log.e(TAG, "是否可见");
                        try{
                            Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCASTT");
                            intent.putExtra("isRing",0);
                            intent.putExtra("Speed",ddf1.format(currentSpeed));
                            intent.putExtra("Distance",ddf1.format(distance/1000));
                            intent.putExtra("MaxSpeed",ddf1.format(maxSpeed));
                            intent.putExtra("mRadius",location.getRadius());
                            intent.putExtra("mCurrentLantitude",mCurrentLantitude);
                            intent.putExtra("mCurrentLongitude",mCurrentLongitude);
                            intent.putExtra("BDLocation",location);
                            localBroadcastManager.sendBroadcast(intent); // 发送本地广播
                        }catch (Exception e){
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }else {
                if (!isStop) {
                    Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCASTT");
                    intent.putExtra("isRing", -1);
                    intent.putExtra("mRadius", location.getRadius());
                    intent.putExtra("mCurrentLantitude", mCurrentLantitude);
                    intent.putExtra("mCurrentLongitude", mCurrentLongitude);
                    intent.putExtra("BDLocation", location);
                    localBroadcastManager.sendBroadcast(intent); // 发送本地广播
                }
            }

        }

    }
}

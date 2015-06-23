package com.app.iriding.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.util.SqliteUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 王海 on 2015/6/5.
 */
public class RecordShareActivity extends BaseActivity{
    private MapView mv_share_bmapView = null;
    private BaiduMap mBaiduMap;// 地图实例
    private FloatingActionButton fb_share_button;
    private TextView tv_share_distance;
    private TextView tv_share_averageSpeed;
    private TextView tv_share_maxSpeed;
    private TextView tv_share_totalTime;
    private TextView tv_share_restTime;
    private Toolbar toolbar;
    private SqliteUtil sqliteUtil;
    private CyclingRecord cyclingRecord = new CyclingRecord();
    @Override
    public void setContentView() {
        setContentView(R.layout.record_share_activity);
    }

    @Override
    public void findViews() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mv_share_bmapView = (MapView) findViewById(R.id.mv_share_bmapView);
        tv_share_distance = (TextView) findViewById(R.id.tv_share_distance);
        tv_share_averageSpeed = (TextView) findViewById(R.id.tv_share_averageSpeed);
        tv_share_maxSpeed = (TextView) findViewById(R.id.tv_share_maxSpeed);
        tv_share_totalTime = (TextView) findViewById(R.id.tv_share_totalTime);
        tv_share_restTime = (TextView) findViewById(R.id.tv_share_restTime);
        mBaiduMap = mv_share_bmapView.getMap();
        mv_share_bmapView.showZoomControls(false);// 不显示缩放控件

    }

    @Override
    public void getData() {
        sqliteUtil = new SqliteUtil();
        Intent intent = getIntent();
        cyclingRecord = sqliteUtil.selectSingleCyclingRecord(intent.getLongExtra("CyclingRecordId",-1));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        toolbar.setTitle(cyclingRecord.getMdateTimeStr());//设置Toolbar标题
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);// 设置toolbar图标可见
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置home按钮可以点击
        actionBar.setHomeButtonEnabled(true);// 设置返回键可用
    }

    @Override
    public void showContent() {
        tv_share_distance.setText(cyclingRecord.getDistance() + "");
        tv_share_averageSpeed.setText(cyclingRecord.getAverageSpeed() + "");
        tv_share_maxSpeed.setText(cyclingRecord.getMaxSpeed() + "");
        tv_share_totalTime.setText(cyclingRecord.getTotalTimeStr());
        tv_share_restTime.setText(cyclingRecord.getRestTimeStr());
        OverlayOptions ooCircle = new PolylineOptions()
                .color(getResources().getColor(R.color.ColorPrimary))//折线的颜色
                .dottedLine(false)//折线是否为虚线
                .points(cyclingRecord.getLatLngs());//折线的点坐标
                mBaiduMap.addOverlay(ooCircle);
        try {
            mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    Log.e("RecordShareActivity", "重听啦12345");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<OverlayOptions> overlayOptions = new ArrayList<OverlayOptions>();
                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_my_location_grey600_24dp);
                            LatLng latLng1 = new LatLng(cyclingRecord.getMaxLantitude(), cyclingRecord.getMaxtLongitude());
                            LatLng latLng2 = new LatLng(cyclingRecord.getMinLantitude(), cyclingRecord.getMintLongitude());
                            LatLng latLng3 = new LatLng(cyclingRecord.getMaxLantitude(), cyclingRecord.getMintLongitude());
                            LatLng latLng4 = new LatLng(cyclingRecord.getMinLantitude(), cyclingRecord.getMaxtLongitude());
                            OverlayOptions options1= new MarkerOptions().position(latLng1).icon(bitmap);
                            OverlayOptions options2 = new MarkerOptions().position(latLng2).icon(bitmap);
                            OverlayOptions options3 = new MarkerOptions().position(latLng3).icon(bitmap);
                            OverlayOptions options4 = new MarkerOptions().position(latLng4).icon(bitmap);
                            overlayOptions.add(options1);
                            overlayOptions.add(options2);
                            overlayOptions.add(options3);
                            overlayOptions.add(options4);

                            OverlayManager overlayManager = new OverlayManager(mBaiduMap) {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    return false;
                                }

                                @Override
                                public List<OverlayOptions> getOverlayOptions() {
                                    return overlayOptions;
                                }
                            };
                            overlayManager.addToMap();
                            overlayManager.zoomToSpan();
                            overlayManager.removeFromMap();
                        }
                    }).start();

/*                    LatLngBounds bounds = new LatLngBounds.Builder().include(latLngs.get(0))
                            .include(latLngs.get(lSize - 2)).build();
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(bounds.getCenter());
                    mBaiduMap.setMapStatus(u);*/
                }
            });
        }catch (Exception e){
            Log.e("RecordShareActivity", e.toString());
        }
        Log.e("RecordShareActivity", "出府哦123456");
    }


    // 设置toolbar返回按钮的监听听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

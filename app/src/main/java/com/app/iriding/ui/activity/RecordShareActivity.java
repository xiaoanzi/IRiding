package com.app.iriding.ui.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.service.TestService;
import com.app.iriding.util.MyApplication;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 王海 on 2015/6/5.
 */
public class RecordShareActivity extends BaseActivity{
    private MapView mv_share_bmapView = null;
    private BaiduMap mBaiduMap;// 地图实例
    private TextView tv_share_distance;
    private TextView tv_share_averageSpeed;
    private TextView tv_share_maxSpeed;
    private TextView tv_share_totalTime;
    private TextView tv_share_restTime;
    private TextView tv_share_gpstext;
    private Toolbar toolbar;
    private SqliteUtil sqliteUtil;
    private CyclingRecord cyclingRecord = new CyclingRecord();
    private String mPathTemp = "";
    View acView;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_share:
                    break;
                case R.id.action_settings:
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(RecordShareActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
    @Override
    public void setContentView() {
        setContentView(R.layout.record_share_activity);
    }

    @Override
    public void findViews() {
        Log.e("TAG", "ok");
        acView = findViewById(R.id.vi_share_view);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mv_share_bmapView = (MapView) findViewById(R.id.mv_share_bmapView);
        tv_share_distance = (TextView) findViewById(R.id.tv_share_distance);
        tv_share_averageSpeed = (TextView) findViewById(R.id.tv_share_averageSpeed);
        tv_share_maxSpeed = (TextView) findViewById(R.id.tv_share_maxSpeed);
        tv_share_totalTime = (TextView) findViewById(R.id.tv_share_totalTime);
        tv_share_restTime = (TextView) findViewById(R.id.tv_share_restTime);
        tv_share_gpstext = (TextView) findViewById(R.id.tv_share_gpstext);
        mBaiduMap = mv_share_bmapView.getMap();
        mv_share_bmapView.showZoomControls(false);// 不显示缩放控件
        acView.setDrawingCacheEnabled(true);

    }

    @Override
    public void getData() {
        sqliteUtil = new SqliteUtil();
        Intent intent = getIntent();
        cyclingRecord = sqliteUtil.selectSingleCyclingRecord(intent.getLongExtra("CyclingRecordId",-1));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        toolbar.setTitle("骑行轨迹");//设置Toolbar标题
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);// 设置toolbar图标可见
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置home按钮可以点击
        actionBar.setHomeButtonEnabled(true);// 设置返回键可用
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        File f = new File(getImagePath());
                        Uri u = Uri.fromFile(f);
                        intent.putExtra(Intent.EXTRA_STREAM, u);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                        intent.putExtra(Intent.EXTRA_TEXT, "测试分享");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, getTitle()));
                        break;
                    case R.id.action_delete:
                        final MaterialDialog mMaterialDialog = new MaterialDialog(RecordShareActivity.this);
                        mMaterialDialog.setTitle("提示");
                        mMaterialDialog.setMessage("是否永久删除该记录？");
                        mMaterialDialog.setPositiveButton("是", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                                if (sqliteUtil.deleteSingleRecord(cyclingRecord.getId())) {
                                    Toast.makeText(RecordShareActivity.this, "删除成功", Toast.LENGTH_SHORT);
                                    finish();
                                } else {
                                    Toast.makeText(RecordShareActivity.this, "删除失败", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                        mMaterialDialog.setNegativeButton("否", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                                return;
                            }
                        });
                        mMaterialDialog.show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void showContent() {
        tv_share_distance.setText(cyclingRecord.getDistance() + "");
        tv_share_averageSpeed.setText(cyclingRecord.getAverageSpeed() + "");
        tv_share_maxSpeed.setText(cyclingRecord.getMaxSpeed() + "");
        tv_share_totalTime.setText(cyclingRecord.getTotalTimeStr());
        tv_share_restTime.setText(cyclingRecord.getRestTimeStr());
        tv_share_gpstext.setText(cyclingRecord.getMdateTimeStr());
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
                }
            });
        }catch (Exception e){
            Log.e("RecordShareActivity", e.toString());
        }
    }

    /**
     * 截取对象是普通view
     */
    private String getImagePath() {

        final String imagePath = getPathTemp() + File.separator + System.currentTimeMillis() + ".png";
        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                Bitmap acBitmap = acView.getDrawingCache();
                Bitmap newbmp = Bitmap.createBitmap(acBitmap.getWidth(), acBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas cv = new Canvas(newbmp);
                cv.drawBitmap(acBitmap, 0, 0, null);
                cv.drawBitmap(bitmap, 0, 0, null);
                cv.save(Canvas.ALL_SAVE_FLAG);//保存
                cv.restore();//存储
                if (newbmp != null) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(imagePath);
                        newbmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return imagePath;
    }

    /**
     * 临时文件地址 *
     */
    public String getPathTemp() {
        if (TextUtils.isEmpty(mPathTemp)) {
            mPathTemp = RecordShareActivity.this.getExternalCacheDir() + File.separator + "temp";
            File dir = new File(mPathTemp);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return mPathTemp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
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

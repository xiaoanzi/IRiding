package com.app.iriding.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import com.app.iriding.R;
import com.app.iriding.ui.activity.TimingActivity;
import com.app.iriding.util.MyApplication;

/**
 * Created by 王海 on 2015/5/29.
 */
public class TestService extends Service {
    private final String TAG = "TestService";
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTestBind;
    }

    @Override public void onCreate() {
        Log.e(TAG, "onCreate");
        Notification notification = new Notification(R.drawable.ic_my_location_grey600_24dp, "骑行开始", System. currentTimeMillis());
        Intent notificationIntent = new Intent(this, TimingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "IRing", "骑行进行中", pendingIntent);
        startForeground(1, notification);
        Log.e("MyService", "onCreate executed");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }
}

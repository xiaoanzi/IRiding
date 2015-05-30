package com.app.iriding.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.app.iriding.R;
import com.app.iriding.ui.activity.TimingActivity;

/**
 * Created by 王海 on 2015/5/29.
 */
public class TestService extends Service {

    private TestBind mTestBind = new TestBind();

    class TestBind extends Binder{
        public void startBind(){
            Log.d("MyService", "startDownload executed");

        }
        public void endBind(){
            Log.d("MyService", "endDownload executed");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTestBind;
    }

    @Override public void onCreate() {
        super.onCreate();
        Notification notification = new Notification(R.drawable.ic_my_location_grey600_24dp, "骑行开始", System. currentTimeMillis());
        Intent notificationIntent = new Intent(this, TimingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "IRing", "骑行进行中", pendingIntent);
        startForeground(1, notification);
        Log.d("MyService", "onCreate executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}

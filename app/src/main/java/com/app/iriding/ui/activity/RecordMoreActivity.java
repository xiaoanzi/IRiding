package com.app.iriding.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.model.StatisticalRecords;
import com.app.iriding.ui.adapter.TraveListViewAdapter;
import com.app.iriding.util.MyApplication;
import com.app.iriding.util.SqliteUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by 王海 on 2015/6/11.
 */
public class RecordMoreActivity extends BaseActivity implements OnDateChangedListener, OnMonthChangedListener {
    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat FORMATTER_MONTH = new SimpleDateFormat("yyyy-MM");
    private static final DateFormat FORMATTER_TOTAL = new SimpleDateFormat("HH");

    private MaterialCalendarView widget;
    private TextView tv_record_mDistance;
    private TextView tv_record_mTotalTime;
    private TextView tv_record_mFrequency;
    private List<CyclingRecord> cyclingRecords = new ArrayList<CyclingRecord>();
    private ListView lv_travel_fiverecord;
    private TraveListViewAdapter traveListViewAdapter;
    private SqliteUtil sqliteUtil = new SqliteUtil();
    private Toolbar toolbar;
    @Override
    public void setContentView() {
        setContentView(R.layout.record_more_activity);
    }

    @Override
    public void findViews() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        lv_travel_fiverecord = (ListView) findViewById(R.id.lv_record_dRecord);
        tv_record_mDistance = (TextView) findViewById(R.id.tv_record_mDistance);
        tv_record_mTotalTime = (TextView) findViewById(R.id.tv_record_mTotalTime);
        tv_record_mFrequency = (TextView) findViewById(R.id.tv_record_mFrequency);
        widget = (MaterialCalendarView) findViewById(R.id.cv_record_calendar);
    }

    @Override
    public void getData() {
        toolbar.setTitle("骑行数据");//设置Toolbar标题
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);// 设置toolbar图标可见
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置home按钮可以点击
        actionBar.setHomeButtonEnabled(true);// 设置返回键可用
    }

    @Override
    public void showContent() {
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        widget.setSelectedDate(date);
        traveListViewAdapter = new TraveListViewAdapter(MyApplication.getContext(), R.layout.travel_item_listview, cyclingRecords);
        lv_travel_fiverecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyApplication.getContext(), RecordShareActivity.class);
                intent.putExtra("CyclingRecordId", cyclingRecords.get(i).getId());
                startActivity(intent);
            }
        });
        lv_travel_fiverecord.setAdapter(traveListViewAdapter);
        getTotalMonth(FORMATTER_MONTH.format(date));
        getSelectDate(FORMATTER.format(date));
    }

    @Override
    public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        getSelectDate(FORMATTER.format(calendarDay.getDate()));
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getTotalMonth(FORMATTER_MONTH.format(date.getDate()));
    }

    public void getSelectDate(String calendarDay){
        List<CyclingRecord> temp = sqliteUtil.selectDateCyclingRecord(calendarDay);
        if (temp.size() != 0) {
            cyclingRecords.clear();
            cyclingRecords.addAll(temp);
            traveListViewAdapter.notifyDataSetChanged();
            // 自动高度
            int totalHeight = 0;
            for (int i = 0; i < traveListViewAdapter.getCount(); i++) {
                View listItem = traveListViewAdapter.getView(i, null, lv_travel_fiverecord);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = lv_travel_fiverecord.getLayoutParams();
            params.height = totalHeight + (lv_travel_fiverecord.getDividerHeight() * (traveListViewAdapter.getCount() - 1));
            lv_travel_fiverecord.setLayoutParams(params);
        }else {
            Toast.makeText(MyApplication.getContext(), "该日期没有骑行数据", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTotalMonth(String date){
        StatisticalRecords sr = sqliteUtil.selectMonthCyclingRecord(date);
        tv_record_mDistance.setText(sr.getsDistance() + "");
        tv_record_mTotalTime.setText(FORMATTER_TOTAL.format(sr.getsTotalTime() - TimeZone.getDefault().getRawOffset()));
        tv_record_mFrequency.setText(sr.getsFrequency() + "");
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

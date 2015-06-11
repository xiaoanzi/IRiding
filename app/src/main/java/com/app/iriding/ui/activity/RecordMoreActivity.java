package com.app.iriding.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
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
import java.util.List;

/**
 * Created by 王海 on 2015/6/11.
 */
public class RecordMoreActivity extends BaseActivity implements OnDateChangedListener, OnMonthChangedListener {
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private List<CyclingRecord> cyclingRecords = new ArrayList<CyclingRecord>();
    private ListView lv_travel_fiverecord;
    TraveListViewAdapter traveListViewAdapter;
    private SqliteUtil sqliteUtil = new SqliteUtil();
    private Toolbar toolbar;
    @Override
    public void setContentView() {
        setContentView(R.layout.record_more_activity);
    }

    @Override
    public void findViews() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        MaterialCalendarView widget = (MaterialCalendarView) findViewById(R.id.cv_record_calendar);
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        lv_travel_fiverecord = (ListView) findViewById(R.id.lv_record_dRecord);
        cyclingRecords.clear();
        cyclingRecords = sqliteUtil.selectLastCyclingRecord(0,10);

        lv_travel_fiverecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyApplication.getContext(), RecordShareActivity.class);
                intent.putExtra("CyclingRecordId", cyclingRecords.get(i).getId());
                startActivity(intent);
            }
        });
        toolbar.setTitle("骑行数据");//设置Toolbar标题
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);// 设置toolbar图标可见
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置home按钮可以点击
        actionBar.setHomeButtonEnabled(true);// 设置返回键可用
    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Log.e("TESTACTIVITY", "" + FORMATTER.format(calendarDay.getDate()));
        Toast.makeText(MyApplication.getContext(), calendarDay.getDate() + "", Toast.LENGTH_SHORT).show();
        List<CyclingRecord> cyclingRecords23 = new Select("Id,totalTimeStr,distance,mdateTimeStr")
                .from(CyclingRecord.class)
                .where("mdateTimeStr = ?", "2015-06-05")
                .orderBy("Id DESC")
                .execute();
        traveListViewAdapter = new TraveListViewAdapter(MyApplication.getContext(),R.layout.travel_item_listview,cyclingRecords23);

        int totalHeight = 0;
        for (int i = 0; i < traveListViewAdapter.getCount(); i++) {
            View listItem = traveListViewAdapter.getView(i, null, lv_travel_fiverecord);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv_travel_fiverecord.getLayoutParams();
        params.height = totalHeight
                + (lv_travel_fiverecord.getDividerHeight() * (traveListViewAdapter.getCount() - 1));
        lv_travel_fiverecord.setLayoutParams(params);

        lv_travel_fiverecord.setAdapter(traveListViewAdapter);
    }
    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        Toast.makeText(this, FORMATTER.format(date.getDate()), Toast.LENGTH_SHORT).show();
    }
}

package com.app.iriding.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.ui.adapter.TravelInfoRecyclerViewAdapter;
import com.app.iriding.util.MyApplication;
import com.app.iriding.util.SqliteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王海 on 2015/6/7.
 */
public class TravelInfoListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<CyclingRecord> cyclingRecords = new ArrayList<CyclingRecord>();
    private SqliteUtil sqliteUtil = new SqliteUtil();
    TravelInfoRecyclerViewAdapter travelInfoRecyclerViewAdapter;
    @Override
    public void setContentView() {
        setContentView(R.layout.travelinfo_activity);
    }

    @Override
    public void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_travelinfo_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        cyclingRecords.clear();
        cyclingRecords = sqliteUtil.selectLastCyclingRecord(0,10);
        travelInfoRecyclerViewAdapter = new TravelInfoRecyclerViewAdapter(cyclingRecords);
        mRecyclerView.setAdapter(travelInfoRecyclerViewAdapter);
    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

    }
}

package com.app.iriding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.ui.adapter.TraveListViewAdapter;
import com.app.iriding.util.MyApplication;
import com.app.iriding.util.SqliteUtil;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class TravelScrollFragment extends Fragment {

    private ObservableScrollView mScrollView;
    private ListView lv_travel_fiverecord;
    private List<CyclingRecord> cyclingRecords = new ArrayList<CyclingRecord>();
    private TextView tv_travel_recently;
    private FloatingActionButton fb_travel_scadd;
    private SqliteUtil sqliteUtil = new SqliteUtil();
    public static TravelScrollFragment newInstance() {
        return new TravelScrollFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_scroll, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.sc_travel_scrollView);
        tv_travel_recently = (TextView) view.findViewById(R.id.tv_travel_recently);
        lv_travel_fiverecord = (ListView) view.findViewById(R.id.lv_travel_fiverecord);
        fb_travel_scadd = (FloatingActionButton) view.findViewById(R.id.fb_travel_scadd);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    @Override
    public void onStart() {
        Log.e("TEST","START4565");
        super.onStart();
        fb_travel_scadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.getContext(), TimingActivity.class);
                startActivity(intent);
            }
        });
        cyclingRecords.clear();
        cyclingRecords = sqliteUtil.selectLastCyclingRecord(0,5);
        if (cyclingRecords.size() == 0){
            tv_travel_recently.setText("暂无骑行记录，点击右边'+'按钮开启一次骑行吧！");
            return;
        }

        TraveListViewAdapter traveListViewAdapter = new TraveListViewAdapter(MyApplication.getContext(),R.layout.travel_item_listview,cyclingRecords);

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


        lv_travel_fiverecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyApplication.getContext(), RecordShareActivity.class);
                intent.putExtra("CyclingRecordId", cyclingRecords.get(i).getId());
                startActivity(intent);
            }
        });
    }
}

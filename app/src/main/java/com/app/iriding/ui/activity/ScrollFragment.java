package com.app.iriding.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.ui.adapter.TraveListViewAdapter;
import com.app.iriding.util.MyApplication;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class ScrollFragment extends Fragment {

    private ObservableScrollView mScrollView;
    private ListView testListView;
    private List<CyclingRecord> cyclingRecords = new ArrayList<CyclingRecord>();
    public static ScrollFragment newInstance() {
        return new ScrollFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scroll, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        testListView = (ListView) view.findViewById(R.id.testListView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        cyclingRecords.clear();
        for (int i = 0;i<6;i++){
            CyclingRecord cyclingRecord = new CyclingRecord();
            cyclingRecord.setDistance("" + 10 + i);
            cyclingRecord.setTotalTime("10:45:1" + i);
            cyclingRecord.setMdateTime("2015-08-1" + i);
            cyclingRecords.add(cyclingRecord);
        }
        TraveListViewAdapter traveListViewAdapter = new TraveListViewAdapter(MyApplication.getContext(),R.layout.travel_item_listview,cyclingRecords);
        testListView.setAdapter(traveListViewAdapter);
    }
}

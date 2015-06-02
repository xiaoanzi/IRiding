package com.app.iriding.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.iriding.R;
import com.app.iriding.model.TravelPackage;
import com.app.iriding.ui.adapter.TravelRecyclerViewAdapter;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王海 on 2015/6/2.
 */
public class TravelRecyclerViewFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<TravelPackage> travelPackages = new ArrayList<>();

    public static TravelRecyclerViewFragment newInstance() {
        return new TravelRecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_travel_card);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        for (int i = 0; i < 10; ++i){
            TravelPackage travelPackage = new TravelPackage();
            travelPackage.setCover(i + "");
            travelPackage.setTitle("小逗比之旅" + 1);
            travelPackage.setDeparture("成都");
            travelPackage.setDestination("拉萨");
            travelPackage.setAlreadyDay(5 + i);
            travelPackage.setForecastDay(28 + i);
            if (i%2 == 1){
                travelPackage.setIsDone(1);
            }else{
                travelPackage.setIsDone(0);
            }
            travelPackages.add(travelPackage);
        }
        mAdapter = new RecyclerViewMaterialAdapter(new TravelRecyclerViewAdapter(travelPackages));
        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }
}

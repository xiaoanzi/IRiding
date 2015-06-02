package com.app.iriding.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;

import java.util.List;

/**
 * Created by 王海 on 2015/6/3.
 */
public class TraveListViewAdapter extends ArrayAdapter<CyclingRecord> {
    private int resourceId;
    public TraveListViewAdapter(Context context, int textViewResourceId, List<CyclingRecord> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CyclingRecord cyclingRecord = getItem(position); // 获取当前项的Fruit实例
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView textView1 =(TextView) view.findViewById(R.id.tv_travel_item_distance);
        TextView textView2 =(TextView) view.findViewById(R.id.tv_travel_item_totalTime);
        TextView textView3 =(TextView) view.findViewById(R.id.tv_travel_item_date);
        textView1.setText(cyclingRecord.getDistance());
        textView2.setText(cyclingRecord.getTotalTime());
        textView3.setText(cyclingRecord.getMdateTime());
        return view;
    }
}

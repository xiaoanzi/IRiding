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
        CyclingRecord cyclingRecord = getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView item_distance =(TextView) view.findViewById(R.id.tv_travel_item_distance);
        TextView item_totalTime =(TextView) view.findViewById(R.id.tv_travel_item_totalTime);
        TextView item_date =(TextView) view.findViewById(R.id.tv_travel_item_date);
        item_distance.setText(cyclingRecord.getDistance()+"");
        item_totalTime.setText(cyclingRecord.getTotalTimeStr());
        item_date.setText(cyclingRecord.getMdateTimeStr());
        return view;
    }
}

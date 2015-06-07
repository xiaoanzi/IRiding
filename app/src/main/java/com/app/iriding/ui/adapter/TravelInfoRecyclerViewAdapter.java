package com.app.iriding.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.iriding.R;
import com.app.iriding.model.CyclingRecord;

import java.util.List;

/**
 * Created by 王海 on 2015/6/7.
 */
public class TravelInfoRecyclerViewAdapter extends RecyclerView.Adapter<TravelInfoRecyclerViewAdapter.ViewHolder>{
    private List<CyclingRecord> mList;
    private View mView;
    private final int TYPE_HEADER = 0;
    private final int TYPE_CHILD = 1;
    /**
     * Item的回调接口
     *
     */
    public interface OnItemClickListener {
        void onItemClickListener(CyclingRecord parent,View view, int position);
    }

    private OnItemClickListener listener; // 点击Item的回调对象

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public TravelInfoRecyclerViewAdapter(List<CyclingRecord> list) {
        mList = list;
    }

    @Override
    public TravelInfoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travelinfo_item_listview, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.tv_travelinfo_day = (TextView) v.findViewById(R.id.tv_travelinfo_day);
            holder.tv_travelinfo_distance = (TextView) v.findViewById(R.id.tv_travelinfo_distance);
            holder.tv_travelinfo_totalTime = (TextView) v.findViewById(R.id.tv_travelinfo_totalTime);
            holder.tv_travelinfo_date = (TextView) v.findViewById(R.id.tv_travelinfo_date);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final TravelInfoRecyclerViewAdapter.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) return;
        if (mView != null) {
            position = position - 1;
        }
        final int i = position;
        CyclingRecord cyclingRecord = mList.get(position);
        holder.tv_travelinfo_day.setText(position+"");
        holder.tv_travelinfo_distance.setText(cyclingRecord.getDistance()+"");
        holder.tv_travelinfo_totalTime.setText(cyclingRecord.getTotalTimeStr());
        holder.tv_travelinfo_date.setText(cyclingRecord.getMdateTimeStr());
        //如果设置了回调，则设置点击事件
        if (listener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onItemClickListener(mList.get(i),holder.itemView, i);
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_CHILD;
    }

    @Override
    public int getItemCount() {
        return mView != null ? mList.size() + 1 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        TextView tv_travelinfo_day;
        TextView tv_travelinfo_distance;
        TextView tv_travelinfo_totalTime;
        TextView tv_travelinfo_date;
    }

    public void addHeaderView(View view) {
        this.mView = view;
    }
}

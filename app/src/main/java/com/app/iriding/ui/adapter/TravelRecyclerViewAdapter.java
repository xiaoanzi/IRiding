package com.app.iriding.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.iriding.R;
import com.app.iriding.model.TravelPackage;

import java.util.List;

/**
 * Created by 王海 on 2015/6/2.
 */
public class TravelRecyclerViewAdapter extends RecyclerView.Adapter<TravelRecyclerViewAdapter.ViewHolder> {
    private List<TravelPackage> mList;
    private View mView;
    private final int TYPE_HEADER = 0;
    private final int TYPE_CHILD = 1;
//    MyImageLoader myImageLoader = new MyImageLoader();
    /**
     * Item的回调接口
     *
     */
    public interface OnItemClickListener {
        void onItemClickListener(TravelPackage parent,View view, int position);
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
    public TravelRecyclerViewAdapter(List<TravelPackage> list) {
        mList = list;
//        myImageLoader.getImageLoaderConfiguration();
    }

    @Override
    public TravelRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_item_cardview, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.tv_travel_item_title = (TextView) v.findViewById(R.id.tv_travel_item_title);
            holder.tv_travel_item_departure = (TextView) v.findViewById(R.id.tv_travel_item_departure);
            holder.tv_travel_item_alreadyDay = (TextView) v.findViewById(R.id.tv_travel_item_alreadyDay);
            holder.tv_travel_item_isdone = (TextView) v.findViewById(R.id.tv_travel_item_isdone);
            holder.iv_travel_item_cover = (ImageView) v.findViewById(R.id.iv_travel_item_cover);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final TravelRecyclerViewAdapter.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) return;
        if (mView != null) {
            position = position - 1;
        }
        final int i = position;
        TravelPackage travelPackage = mList.get(position);
        final String tv_travel_item_title = travelPackage.getTitle();
        final String tv_travel_item_departure = travelPackage.getDeparture()+"--"+travelPackage.getDestination();
        final String tv_travel_item_alreadyDay = "已进行"+travelPackage.getAlreadyDay()+"天  预计"+travelPackage.getForecastDay()+"天";
        String tv_travel_item_isdone = "进行中";
        if (travelPackage.getIsDone() == 1){
            tv_travel_item_isdone = "已完成";
        }
        final String iv_travel_item_cover = travelPackage.getCover();
        holder.tv_travel_item_title.setText(tv_travel_item_title);
        holder.tv_travel_item_departure.setText(tv_travel_item_departure);
        holder.tv_travel_item_alreadyDay.setText(tv_travel_item_alreadyDay);
        holder.tv_travel_item_isdone.setText(tv_travel_item_isdone);
        holder.iv_travel_item_cover.setImageResource(R.drawable.myuser);
//        ImageLoader.getInstance().displayImage(bookListNote.getAuthor_user().getAvatar(), holder.note_user_image, myImageLoader.getDisplayImageOptions());
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

        ImageView iv_travel_item_cover;
        TextView tv_travel_item_title;
        TextView tv_travel_item_departure;
        TextView tv_travel_item_alreadyDay;
        TextView tv_travel_item_isdone;
    }

    public void addHeaderView(View view) {
        this.mView = view;
    }
}

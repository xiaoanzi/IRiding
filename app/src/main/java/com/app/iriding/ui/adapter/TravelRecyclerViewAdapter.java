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
    static final int TYPE_PLACEHOLDER = -2147483648;
    private int mPlaceholderSize = 1;
    private List<TravelPackage> mList;
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
        switch(viewType) {
            case TYPE_PLACEHOLDER:
                View view = null;
                view = LayoutInflater.from(parent.getContext()).inflate(com.github.florent37.materialviewpager.R.layout.material_view_pager_placeholder, parent, false);
                return new ViewHolder(view);
            default:
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
        switch(this.getItemViewType(position)) {
            default:
                position = position - 1;
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
            case TYPE_PLACEHOLDER:
        }

    }

    @Override
    public int getItemViewType(int position) {
        int p = position < this.mPlaceholderSize?-2147483648:(position - this.mPlaceholderSize);
        return p;
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
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
}

package com.android.common.refreshview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.common.R;

import java.util.ArrayList;
import java.util.List;

public abstract class MyCommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MyCommonAdapter.class.getSimpleName();
    private static final int FOOTER = -1;
    private List<T> dataList = new ArrayList<>();

    private int resourceId;
    private OnItemClickListener mOnItemClickListener;
    private RecyclerView.Adapter adapter;
    private boolean isLoadMore = false, loadMoreError = false;

    public MyCommonAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public MyCommonAdapter(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (FOOTER == viewType) { // 添加底部布局
            View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_viwe_loadmore, parent, false);
            return MyViewHolder.getHolder(footerView);
        }
        if (adapter != null) {
            return adapter.onCreateViewHolder(parent, viewType);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
            return MyViewHolder.getHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (adapter != null)
            bindData(adapter, holder, position, adapter.getItemCount());
        else
            bindData(null, holder, position, dataList.size());
    }


    private void bindData(RecyclerView.Adapter adapter, final RecyclerView.ViewHolder holder, final int position, int dataSize) {
        if (position == dataSize) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            if (dataSize != 0) {
                myViewHolder.setText(R.id.tv_loadmore, "加载中...");
                myViewHolder.setVisible(R.id.progressbar_loadmore, true);
                myViewHolder.setVisible(R.id.view_loadmore, true);
                if (!loadMoreError) {
                    if (!isLoadMore) {
                        myViewHolder.setText(R.id.tv_loadmore, "暂无更多数据");
                        myViewHolder.setVisible(R.id.progressbar_loadmore, false);
                    } else {
//                        myViewHolder.setText(R.id.tv_loadmore, "加载中...");
//                        myViewHolder.setVisible(R.id.progressbar_loadmore, true);
                    }
                } else {  // 网络异常，数据加载失败情况下
                    myViewHolder.setText(R.id.tv_loadmore, "数据加载失败，请稍后重试");
                    myViewHolder.setVisible(R.id.progressbar_loadmore, false);
                }
            } else {
                myViewHolder.setVisible(R.id.view_loadmore, false);
            }
        } else {
            if (adapter != null) {
                adapter.onBindViewHolder(holder, position);
            } else {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                T t = dataList.get(position);
                this.convert(myViewHolder, t, position);
                if (position != dataList.size() && mOnItemClickListener != null) {
                    myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.onItemClick(holder.itemView, position);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (adapter != null)
            return adapter.getItemCount() + 1;
        else
            return dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (adapter != null ? adapter.getItemCount() : dataList.size()))
            return FOOTER;
        else
            return position;
    }

    public List<T> cleanData() {
        dataList.clear();
        return dataList;
    }

    public List<T> appendData(List<T> list) {
        if (list != null && list.size() != 0) {
            List<T> newArray = new ArrayList<>();
            newArray.addAll(dataList);
            newArray.addAll(list);
            this.dataList.clear();
            this.dataList.addAll(newArray);
        }
        return dataList;
    }

    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public void loadMoreError(boolean loadMoreError) {
        this.loadMoreError = loadMoreError;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public abstract void convert(MyViewHolder holder, T t, int position);

}
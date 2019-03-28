package com.android.common.widget.photo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.common.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * 图片adapter
 * Created by Administrator on 2016/12/9.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private static final String TAG = "PhotoAdapter";
    private Context context;
    private ArrayList<String> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public PhotoAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.common_item_photo, null);
        return ViewHolder.getHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0) {
            holder.image.setBackgroundResource(R.drawable.common_camera);
        } else {
            if (mDatas.size() > 0) {
                ImageView imageView = holder.image;
                String path = mDatas.get(position - 1);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.error(R.drawable.common_image_failed);
                Glide.with(context)
                        .load(path)
                        .apply(requestOptions)
                        .thumbnail(0.1f)
                        .into(imageView);
            }
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (pos == 0) {
                        mOnItemClickListener.onItemClick("", pos);
                    } else {
                        mOnItemClickListener.onItemClick(mDatas.get(pos - 1), pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String path, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_image);
            this.itemView = itemView;
        }

        public static ViewHolder getHolder(View view) {
            ViewHolder myHolder = (ViewHolder) view.getTag();
            if (myHolder == null) {
                myHolder = new ViewHolder(view);
                view.setTag(myHolder);
            }
            return myHolder;
        }
    }
}

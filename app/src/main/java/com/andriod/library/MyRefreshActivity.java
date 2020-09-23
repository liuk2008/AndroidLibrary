package com.andriod.library;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.andriod.library.network.FinanceListInfo;
import com.andriod.library.network.RetrofitDemo;
import com.android.common.refreshview.MyCommonAdapter;
import com.android.common.refreshview.MyRefreshView;
import com.android.common.refreshview.MyViewHolder;
import com.android.common.utils.common.LogUtils;
import com.android.common.utils.common.ToastUtils;
import com.android.network.callback.Callback;
import com.android.network.retrofit.RetrofitEngine;


/**
 * 1、设置标题栏根据页面滑动渐变
 * 2、测试下拉刷新、上拉加载、点击屏幕刷新等功能
 * 3、测试Loading页面，加载失败页面，无数据页面
 * 4、可自定义对应状态的view
 */
public class MyRefreshActivity extends AppCompatActivity {

    private static final String TAG = MyRefreshActivity.class.getSimpleName();
    private MyRefreshView refreshView;
    private MyCommonAdapter<FinanceListInfo.ListBean> adapter;
    private int index = 1, pageSize;
    private RetrofitDemo retrofitDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrefresh);
        RetrofitEngine.getInstance().init(getApplicationContext());
        refreshView = findViewById(R.id.refreshView);
        // 设置refreshView
        adapter = new MyCommonAdapter<FinanceListInfo.ListBean>(R.layout.item_view) {
            @Override
            public void convert(MyViewHolder holder, FinanceListInfo.ListBean data, int position) {
                holder.setText(R.id.item_title, data.loanTitle);
            }
        };
        refreshView.setAdapter(adapter);
        setRefreshView();
        request();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        retrofitDemo.cancelAll();
    }

    private void setRefreshView() {
        refreshView.setMyRefreshController(new MyRefreshView.MyRefreshController() {
            @Override
            public void onRefresh() {
                // 下拉刷新
                index = 1;
                request();
            }

            @Override
            public void onLoadMore() {
                // 上拉加载
                index++;
                if (index <= pageSize)
                    request();
            }

            @Override
            public void onReload() {
                // 重新加载数据
                request();
            }
        });
        // item点击事件
        adapter.setOnItemClickListener(new MyCommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtils.showToast(getApplicationContext(), "position:" + position);
            }
        });
    }

    private void request() {
        retrofitDemo = new RetrofitDemo();
        retrofitDemo.financeList(index, new Callback<FinanceListInfo>() {
            @Override
            public void onSuccess(FinanceListInfo financeListInfo) {
                refreshView.refreshComplete();
                if (financeListInfo == null || financeListInfo.list == null || financeListInfo.list.size() == 0) {
                    refreshView.showEmptyView();
                    return;
                }
                int size = financeListInfo.pageInfo.total / 40;
                pageSize = financeListInfo.pageInfo.total % 40 == 0 ? size : size + 1;
                if (index == 1)
                    adapter.cleanData();
                adapter.appendData(financeListInfo.list);
                refreshView.setLoadMore(index < size);
            }

            @Override
            public void onFail(int resultCode, String msg, String data) {
                LogUtils.logd(TAG, "resultCode:" + resultCode + ", msg:" + msg + ", data:" + data);
                refreshView.refreshComplete();
                if (index > 1)
                    index--;
                else
                    adapter.cleanData();
                refreshView.showErrorView();
            }
        });
    }


}

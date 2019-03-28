package com.android.common.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.R;

/**
 * 自定义列表View
 * 1、实现下拉刷新、上拉加载、点击屏幕刷新等功能，默认开启
 * 2、包装Adapter，提供item点击、添加数据功能
 * 3、自定义Loading页面、加载失败页面、无数据页面
 * 4、setXXX()：是否开启对应功能
 * 5、setXXXView()、showXXXView()：设置/显示对应状态的View
 * 注意：
 * 1、ProgressBar在运行时，禁止点击
 * 2、下拉刷新与上拉加载不能同时进行，否则会出现数据混乱情况
 */
public class MyRefreshView extends LinearLayout {

    private static final String TAG = MyRefreshView.class.getSimpleName();
    private static final int VIEW_LOADING = 0;
    private static final int VIEW_NO_DATA = 1;
    private static final int VIEW_ERROR = 2;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isRefresh = true, isLoadMore = true, isLoading = false, isReload = true;
    private MyCommonAdapter wrapAdapter;
    private ViewGroup loadingView, emptyView, errorView;
    private TextView tvReload;

    public MyRefreshView(Context context) {
        super(context);
        initView(context);
    }

    public MyRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View rooView = View.inflate(context, R.layout.common_refreshview, null);
        recyclerView = rooView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = rooView.findViewById(R.id.swipeRefreshLayout);
        loadingView = rooView.findViewById(R.id.view_loading);
        emptyView = rooView.findViewById(R.id.view_empty);
        errorView = rooView.findViewById(R.id.view_error);
        tvReload = rooView.findViewById(R.id.tv_reload);
        this.addView(rooView);
        setSwipeRefreshLayout();
        setRecyclerView();
        loadingView.setVisibility(VISIBLE);
        errorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReload && onReLoadListener != null) {
                    loadingView.setVisibility(VISIBLE);
                    emptyView.setVisibility(GONE);
                    errorView.setVisibility(GONE);
                    setLoadMore(true);
                    onReLoadListener.onReload();
                }
            }
        });
    }

    private void setSwipeRefreshLayout() {
        // 设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
        swipeRefreshLayout.setProgressViewOffset(true, 50, 200);
        // 设置下拉圆圈的大小，两个值 LARGE， DEFAULT
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        // 设置下拉圆圈上的颜色
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setEnabled(false);
        // onRefresh 的回调只有在手势下拉的情况下才会触发，通过 setRefreshing 只能调用刷新的动画是否显示
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.isRefreshing() && onRefreshListener != null)
                    onRefreshListener.onRefresh(); // 下拉刷新功能
            }
        });
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastVisibleItemPosition() == (wrapAdapter.getItemCount() - 1)) {//最后一条可见
                        if (!isLoading && isLoadMore && onLoadMoreListener != null) {
                            isLoading = true; // 防止重复刷新
                            wrapAdapter.loadMoreError(false);
                            wrapAdapter.notifyDataSetChanged();
                            onLoadMoreListener.onLoadMore(); // 上拉加载功能
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (onScrollChangeListener != null) {
                    int scrollY = recyclerView.computeVerticalScrollOffset();
                    int scrollX = recyclerView.computeHorizontalScrollOffset();
                    onScrollChangeListener.onScrollChange(recyclerView, scrollX, scrollY);
                }
            }
        });
    }

    /**
     * 设置RecyclerView中的Adapter：
     * <p>
     * 1、可传入自定义RecyclerView.Adapter
     * <p>
     * 2、可使用 MyCommonAdapter
     *
     * @param adapter RecyclerView.Adapter
     */
    public void setAdapter(final RecyclerView.Adapter adapter) {
        if (adapter instanceof MyCommonAdapter) {
            wrapAdapter = (MyCommonAdapter) adapter;
        } else {
            // 包装自定义Adapter，共用CommonAdapter实现数据包装
            wrapAdapter = new MyCommonAdapter(adapter) {
                @Override
                public void convert(MyViewHolder holder, Object o, int position) {

                }
            };
        }
        recyclerView.setAdapter(wrapAdapter);
    }

    /**
     * 结束刷新
     */
    public void refreshComplete() {
        if (loadingView.getVisibility() != GONE) {
            loadingView.setVisibility(GONE);
            swipeRefreshLayout.setEnabled(isRefresh);
        }
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(VISIBLE);
        wrapAdapter.notifyDataSetChanged();
    }

    /**
     * 是否开启下拉刷新功能：
     *
     * @param isRefresh false：关闭下拉刷新功能，true：打开下拉刷新功能
     */
    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    /**
     * 是否开启上拉加载功能：
     * <p>
     * 用于列表分页加载数据，当最后一页数据加载完毕时关闭此功能
     *
     * @param isLoadMore false：关闭上拉加载功能，true：打开上拉加载功能
     */
    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
        isLoading = false;
        wrapAdapter.setLoadMore(isLoadMore);
    }

    /**
     * 是否开启重新加载数据功能
     * <p>
     * 用于数据加载失败情况，点击屏幕重新获取数据
     *
     * @param isReload false：关闭重新加载数据功能，true：打开重新加载数据功能
     */
    public void setReload(boolean isReload) {
        this.isReload = isReload;
        if (tvReload != null)
            tvReload.setVisibility(isReload ? VISIBLE : GONE);
    }

    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;
    private OnReLoadListener onReLoadListener;
    private OnScrollChangeListener onScrollChangeListener;

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnReLoadListener {
        void onReload();
    }

    public interface OnScrollChangeListener {
        void onScrollChange(RecyclerView recyclerView, int scrollX, int scrollY);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnReLoadListener(OnReLoadListener onReLoadListener) {
        this.onReLoadListener = onReLoadListener;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public void setLoadingView(View view) {
        setView(loadingView, view);
    }

    public void setEmptyView(View view) {
        setView(emptyView, view);
    }

    public void setErrorView(View view) {
        setView(errorView, view);
    }

    private void setView(ViewGroup rootView, View view) {
        if (null == view)
            return;
        rootView.removeAllViews();
        rootView.addView(view);
    }

    public void showEmptyView() {
        recyclerView.setVisibility(GONE);
        showView(VIEW_NO_DATA);
        // 禁止下拉刷新、上拉加载
        swipeRefreshLayout.setEnabled(false);
        setLoadMore(false);
    }

    public void showErrorView() {
        int dataCount = wrapAdapter.getItemCount();
        if (dataCount - 1 == 0) { // 数据加载失败情况下
            recyclerView.setVisibility(GONE);
            showView(VIEW_ERROR);
            // 禁止下拉刷新、上拉加载
            swipeRefreshLayout.setEnabled(false);
            setLoadMore(false);
        } else { // 刷新失败情况下
            loadingView.setVisibility(GONE);
            emptyView.setVisibility(GONE);
            errorView.setVisibility(GONE);
            setLoadMore(true);
            wrapAdapter.loadMoreError(true);
        }
    }

    private void showView(int type) {
        loadingView.setVisibility(type == VIEW_LOADING ? VISIBLE : GONE);
        emptyView.setVisibility(type == VIEW_NO_DATA ? VISIBLE : GONE);
        errorView.setVisibility(type == VIEW_ERROR ? VISIBLE : GONE);
    }

}

package com.example.android.mvp;


import java.util.ArrayList;
import java.util.List;

/**
 * 统一管理http请求
 * 1、页面销毁时，统一取消网络回调
 * 2、中断Presenter强引用
 */
public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();
    private List<BasePresenter> presenterList;

    public RequestManager() {
        presenterList = new ArrayList<>();
    }

    public void addPresenter(BasePresenter presenter) {
        presenterList.add(presenter);
    }


    public void cancelAll() {
        // 集合类添加元素后，将会持有元素对象的引用，导致该元素对象不能被垃圾回收，从而发生内存泄漏。
        for (int i = 0; i < presenterList.size(); i++) {
            BasePresenter presenter = presenterList.get(i);
            if (presenter != null) {
                presenter.onDestroy();
                // 虽然被置空了，但是集合里还是持有引用
                presenter = null;
            }
        }
        //  清空，防止内存泄漏
        presenterList.clear();
        presenterList = null;
    }
}

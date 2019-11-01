package com.android.network.rxjava;


import android.util.Log;


import com.android.network.callback.Callback;
import com.android.network.callback.Callback1;
import com.android.network.NetworkData;
import com.android.network.error.ErrorHandler;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * RxJava与Retrofit网络框架
 */
public class RxNetUtils {

    private static final String TAG = "RxNetUtils";

    public static <T> Disposable subscribe(Observable<T> observable, final Callback<T> callback) {
        Disposable disposable = null;
        try {
            disposable = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<T>() {
                        @Override
                        public void accept(T t) throws Exception {
                            try {
                                callback.onSuccess(t);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            handleException(throwable);
                            NetworkData error = ErrorHandler.handlerError(throwable);
                            callback.onFail(error.getCode(), error.getMsg(), error.getData());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposable;
    }

    public static <X, Y> Disposable subscribe(Observable<X> xObservable, Observable<Y> yObservable, final Callback1<X, Y> callback) {
        Disposable disposable = null;
        try {
            disposable = Observable.zip(xObservable, yObservable, new BiFunction<X, Y, Object>() {
                @Override
                public Object apply(X x, Y y) throws Exception {
                    return new Object[]{x, y};
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object object) throws Exception {
                            try {
                                Object[] objects = (Object[]) object;
                                callback.onSuccess((X) objects[0], (Y) objects[1]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            handleException(throwable);
                            NetworkData error = ErrorHandler.handlerError(throwable);
                            callback.onFail(error.getCode(), error.getMsg(), error.getData());
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposable;
    }

    private static void handleException(@NonNull Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求异常：").append(throwable.getClass().getName()).append('\n');
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            sb.append("Request:").append(httpException.response().raw().request().toString()).append('\n');
            sb.append("Headers:").append(httpException.response().raw().headers().toString()).append('\n');
            sb.append("Response:").append(httpException.response().toString()).append('\n');
        } else {
            sb.append(throwable.getMessage());
        }
        Log.d(TAG, "handleException: " + sb.toString());
    }

}

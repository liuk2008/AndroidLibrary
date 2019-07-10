package com.example.rxjava;

import android.os.Process;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 背压含义：
 * 在RxJava中会经常遇到一种情况就是被观察者发送消息十分迅速以至于观察者不能及时的响应这些消息，
 * 则会产生Backpressure(背压)，即生产者的速度大于消费者的速度带来的问题。
 * <p>
 * 产生条件：
 * 1.如果生产者和消费者在一个线程的情况下，无论生产者的生产速度有多快，每生产一个事件都会通知消费者，等待消费者消费完毕，
 * 再生产下一个事件。所以在这同步情况下，Backpressure问题不存在。
 * 2.如果生产者和消费者不在同一线程的情况下，如果生产者的速度大于消费者的速度，就会产生Backpressure问题。即异步情况下，Backpressure问题才会存在。
 * <p>
 * 处理策略：
 * 1、从数量上进行治理, 减少发送进水缸里的事件，需注意事件丢失
 * 2、从速度上进行治理, 减缓事件发送进水缸的速度，需注意性能
 * <p>
 * 注意：
 * 1、此代码存事件积压情况，会造成OOM。
 * 2、大数据流用Flowable，小数据流用Observable
 */
public class RxJavaBackPressure {

    private static final String TAG = RxJavaBackPressure.class.getSimpleName();

    // 减缓事件发送
    public static void testObserver() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {  // 循环产生事件
                    emitter.onNext(i);
                    Thread.sleep(2000);  // 发送事件之后延时2秒
                }
            }
        })
                .subscribeOn(Schedulers.io())
//                .sample(2, TimeUnit.SECONDS) // 每隔指定的时间就从上游中取出一个事件发送给下游，存在事件丢失情况
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        // 消费事件能力 < 产生事件能力，产生背压
                        Log.d(TAG, getThreadName() + "accept" + integer);
                    }
                });

    }

    /**
     * 处理Backpressure的策略仅仅是处理Subscriber接收事件的方式，并不影响Flowable发送事件的方法
     * 即使采用了处理Backpressure的策略，Flowable原来以什么样的速度产生事件，现在还是什么样的速度不会变化，主要处理的是Subscriber接收事件的方式。
     * <p>
     * 同步调用：
     * 1、未设置 subscription.request()方法时，抛出MissingBackpressureException异常
     * 2、设置request()，生产一个事件，消费一个事件，当request设置的数量小于生产事件数量时，抛出MissingBackpressureException异常
     * 异步调用：
     * 1、未设置 subscription.request()方法时，运行正常
     * 2、设置request()，先全部生产事件，再消费事件，
     * 3、在异步调用时，RxJava中有个缓存池，用来缓存消费者处理不了暂时缓存下来的数据，缓存池的默认大小为128，即只能缓存128个事件。
     * 无论request()中传入的数字比128大或小，缓存池中在刚开始都会存入128个事件。当然如果本身并没有这么多事件需要发送，则不会存128个事件。
     * <p>
     * BackpressureStrategy.ERROR  如果缓存池溢出，抛出MissingBackpressureException异常，不会处理发送的事件
     * BackpressureStrategy.DROP  当消费者处理不了事件，就丢弃
     * BackpressureStrategy.BUFFER
     * BackpressureStrategy.LATEST
     */
    public static Subscription mSubscription;

    private static void test1(FlowableEmitter<Integer> emitter) {
        boolean flag;
        for (int i = 0; ; i++) {
            flag = false;
            while (emitter.requested() == 0) {
                if (!flag) {
                    Log.d(TAG, getThreadName() + "emit is over");
                    flag = true;
                }
            }
            Log.d(TAG, getThreadName() + "emit " + i + " , requested = " + emitter.requested());
            emitter.onNext(i);
        }
    }

    private static void test2(FlowableEmitter<Integer> emitter) {
        for (int i = 0; i < 150; i++) {
            Log.d(TAG, getThreadName() + "emit " + i + " , requested = " + emitter.requested());
            emitter.onNext(i);
        }
        emitter.onComplete();
    }


    public static void backPressureDemo() {

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception { // 注意内存问题
                Log.d(TAG, getThreadName() + "request : " + emitter.requested());
                test2(emitter);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        Log.d(TAG, getThreadName());
                        /*
                         * subscription.request()是用来向生产者申请可以消费的事件数量
                         * 注意：
                         * 1、如果不显示调用request就表示消费能力为0，不处理事件
                         * 2、当下游每消费96个事件便会自动触发内部的request()去设置上游的requested的值
                         */
                        subscription.request(200);
                        mSubscription = subscription;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, getThreadName() + "onNext : " + integer);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, getThreadName() + throwable);

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, getThreadName());

                    }
                });
    }

    private static String getThreadName() {
        StringBuffer sb = new StringBuffer();
        sb.append("Pid:" + Process.myPid() + ",");
        sb.append(Thread.currentThread().getName());
        sb.append("-> ");
        return sb.toString();
    }

}

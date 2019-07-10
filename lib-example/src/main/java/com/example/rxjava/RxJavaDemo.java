package com.example.rxjava;

import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava2使用方法
 * 1、基本使用方法
 * 2、常见操作符使用方法
 * 3、使用场景：1、结合Retrofit使用，进行网络请求，2、进行数据库读写操作
 */
public class RxJavaDemo {

    private static final String TAG = RxJavaDemo.class.getSimpleName();

    private static Student[] students = {new Student("test1"), new Student("test2"),
            new Student("test3"), new Student("test4")};

    private static List<String> list = new ArrayList<>();

    static {
        list.add("test1");
        list.add("test2");
        list.add("test3");
        list.add("test4");
        list.add("test5");
    }

    private static Observer<String> observer = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable disposable) {
            Log.d(TAG, "onSubscribe: " + getThreadName());
        }

        @Override
        public void onNext(String string) {
            Log.d(TAG, "onNext: " + getThreadName() + "string:" + string);
        }

        @Override
        public void onError(Throwable throwable) {
            Log.d(TAG, "onError: " + getThreadName());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            Log.d(TAG, "onComplete: " + getThreadName());
        }
    };

    // 基本使用方法
    public static void create() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "subscribe:emit 1");
                emitter.onNext(1);
                Log.d(TAG, "subscribe:emit 2");
                emitter.onNext(2);
                Log.d(TAG, "subscribe:emit 3");
                emitter.onNext(3);
                Log.d(TAG, "subscribe:emit complete");
                emitter.onComplete();
                Log.d(TAG, "subscribe:emit 4");
                emitter.onNext(4);
                Log.d(TAG, "Observable subscribe thread is : " + getThreadName()); // subscribeOn 指定的线程
            }
        });
        Observer<Integer> observer = new Observer<Integer>() {
            private Disposable mDisposable; // 解除订阅关系

            @Override
            public void onSubscribe(Disposable disposable) { // main
                mDisposable = disposable;
                Log.d(TAG, "Observable onSubscribe thread is : " + getThreadName());
            }

            @Override
            public void onNext(Integer integer) { //  observeOn 最后一次指定的线程
                Log.d(TAG, "Observer onNext thread is : " + getThreadName() + "onNext : " + integer);
                if (integer == 2) {
                    mDisposable.dispose(); // 解除订阅关系，Observable还在继续发送事件，但是Observer不再接收事件
                }
            }

            @Override
            public void onError(Throwable throwable) { //  observeOn 最后一次指定的线程
                Log.d(TAG, "Observer onError thread is : " + getThreadName());
            }

            @Override
            public void onComplete() {  //  observeOn 最后一次指定的线程
                Log.d(TAG, "Observer onComplete thread is : " + getThreadName());
            }
        };
        observable
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(new Consumer<Disposable>() { // main ，必须写在subscribeOn 之后才能运行
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.d(TAG, "doOnSubscribe thread is : " + getThreadName());
                    }
                })
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(newThread)," + getThreadName() + "Integer : " + integer);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn((Schedulers.io)," + getThreadName() + "doOnNext : " + integer);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception { // observeOn 最后一次指定的线程
                        Log.d(TAG, "doOnComplete thread is : " + getThreadName());
                    }
                })
                .subscribe(observer);
    }


    // just 方式
    public static void just() {
        Observable<String> just = Observable.just("test1", "test2");
        just.subscribe(observer); // 默认在 main 线程
    }

    // fromArray 方式
    public static void fromArray() {
        String[] strings = {"test1", "test2"};
        Observable.fromArray(strings)
                .subscribeOn(Schedulers.io())
                .subscribe(observer); // io 线程
    }

    // fromIterable 方式 : 遍历集合，发送每个item。相当于多次回调onNext()方法，每次传入一个item。
    public static void fromIterable() {
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        Observable.fromIterable(list)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(observer); // newthread 线程
    }

    //  defer 方式 : 当观察者订阅时，才创建Observable，并且针对每个观察者创建都是一个新的Observable。
    public static void defer() {
        Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                Log.d(TAG, "defer: " + getThreadName()); //  默认在 main 线程
                return Observable.just("test1", "test2");
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer); // main 线程
    }

    // interval 方式: 按固定时间间隔发射整数序列的Observable，可用作定时器
    public static void interval() {
        Observable.interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()) // 设置不起作用o
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long time) throws Exception {  //  // 默认在 RxComputationThreadPool-1
                        Log.d(TAG, getThreadName() + "interval:" + time);
                    }
                });
    }

    // 一对一操作符
    public static void MapDemo() {
        Observable.fromArray(students)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Function<Student, String>() {
                    @Override
                    public String apply(Student student) throws Exception {
                        Log.d(TAG, "MapDemo : " + getThreadName());
                        return student.name;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * 一对多操作符
     * 多种网络嵌套使用时可以考虑使用flatMap实现
     * 注意：
     * 1、flatMap并不保证事件的顺序，concatMap实现顺序执行
     * 2、flatMap 操作符在使用时指定线程，则所有事件转换运行在指定的线程中，且转换为并行执行，默认在主线程执行
     * 3、concatMap 操作符在使用过程中，第一次事件转换操作运行在指定线程，剩下的转换操作运行在RxComputationThreadPool线程中，
     * 且线程为串行执行，影响性能，默认在RxComputationThreadPool线程执行
     */
    public static void flatMapDemo() {
        Observable.fromArray(students)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<Student, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Student student) throws Exception {
                        Log.d(TAG, getThreadName());
                        ArrayList<String> arrayList = student.arrayList;
                        return Observable.fromIterable(arrayList).delay(5, TimeUnit.SECONDS);
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    // 过滤器操作符
    public static void filterDemo() {
        Observable.fromIterable(list)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        if ("test2".equals(s)) {
                            return true;
                        }
                        return false;
                    }
                })
                .subscribe(observer);
    }

    // 输出最多指定数量的结果
    public static void takeDemo() {
        Observable.fromIterable(list)
                .take(3)
                .subscribe(observer);
    }


    /**
     * 打包请求
     * 比如一个界面需要展示用户的一些信息, 而这些信息分别要从两个服务器接口中获取,
     * 而只有当两个都获取到了之后才能进行展示, 这个时候就可以用Zip了
     * 使用zip操作符时，ObservableA、ObservableB 默认运行在同一个线程中，且ObservableA运行完后，ObservableB执行
     * <p>
     * Zip来打包请求
     */
    public static void zipDemo() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, getThreadName() + "emit 1");
                emitter.onNext(1);
                Log.d(TAG, getThreadName() + "emit 2");
                emitter.onNext(2);
                Log.d(TAG, getThreadName() + "emit 3");
                emitter.onNext(3);
                Log.d(TAG, getThreadName() + "emit 4");
                emitter.onNext(4);
                Log.d(TAG, getThreadName() + "emit complete1");
            }
        })
                .subscribeOn(Schedulers.io()); // 不指定线程默认运行在同一个线程中

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d(TAG, getThreadName() + "emit A");
                emitter.onNext("A");
                Log.d(TAG, getThreadName() + "emit B");
                emitter.onNext("B");
                Log.d(TAG, getThreadName() + "emit C");
                emitter.onNext("C");
                Log.d(TAG, getThreadName() + "emit complete2");
                emitter.onComplete();

            }
        })
                .subscribeOn(Schedulers.newThread()); // 不指定线程默认运行在同一个线程中


        // zip接收的事件数量跟上游中发送事件最少事件数量是有关的，且运行线程也与此有关
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                Log.d(TAG, "zip : " + getThreadName());
                return integer + s;
            }
        })
                .subscribeOn(Schedulers.io()) // 设置不起作用
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    private static String getThreadName() {
        StringBuffer sb = new StringBuffer();
        sb.append("Pid:" + Process.myPid() + ",");
        sb.append(Thread.currentThread().getName());
        sb.append("-> ");
        return sb.toString();
    }

    static class Student {

        public String name;
        public ArrayList<String> arrayList = new ArrayList<String>();

        public Student(String name) {
            this.name = name;
            this.arrayList.add(name + " Java");
            this.arrayList.add(name + " android");
            this.arrayList.add(name + " c++");
        }

    }
}

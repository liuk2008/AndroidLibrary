package com.example.android;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

/**
 * 使用AsyncTask发送网络请求：
 * 1、AsyncTask对象不可重复使用，也就是说一个AsyncTask对象只能execute()一次，否则会有异常抛出"java.lang.IllegalStateException
 * 2、当调用cancel()后，只是AsyncTask对象设置了一个标识位。此时doInBackground()和onProgressUpdate()还会继续执行，而doInBackground()执行完后
 * 将会调用onCancelled()，不再调用onPostExecute()。此外，我们可以在doInBackground()不断的检查 isCancelled()的返回值，当其返回true时就停止执行，
 * 特别是有循环的时候，从而停止任务的运行。
 * <p>
 * AsyncTask通过其execute方法启动执行，底层通过线程池来管理和调度进程中的所有Task的，通过重用原来创建的线程执行新的任务。
 * execute底层实际调用executeOnExecutor，使用AsyncTask默认创建SerialExecutor线程池执行任务，按先后顺序每次只运行一个，每个任务都是串行执行的
 * Executor SERIAL_EXECUTOR = new SerialExecutor();           单任务运行   此线程池是AsyncTask内部默认使用
 * Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor();  多任务运行
 * <p>
 * Created by Administrator on 2018/4/12.
 */
public class AsyncTaskDemo extends AsyncTask<String, Integer, String> {
    private final static String TAG = "AsyncTaskDemo";
    private String name;

    public AsyncTaskDemo(String name) {
        this.name = name;
    }

    /**
     * 1、预加载,加载前的初始化,在UI线程运行
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * 2、在onPreExecute方法执行后马上执行，在子线程运行，其实就是创建个线程任务
     */
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, name + " is 开始 thread id " + Thread.currentThread().getId());
        publishProgress(Integer.valueOf(name));
        SystemClock.sleep(2000);
        Log.d(TAG, name + " is 结束 thread id " + Thread.currentThread().getId());
        return "";
    }

    /**
     * 3、 在doInBackground调用publishProgress时触发,在UI线程运行
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, " onProgressUpdate： " + values[0]);
    }

    /**
     * 4、在doInBackground 执行完成后，onPostExecute方法将被UI线程调用
     * result就是上面doInBackground执行后的返回值
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    // 在用户取消线程操作的时候调用。在主线程中调用cancel()的时候调用。
    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
    }

}

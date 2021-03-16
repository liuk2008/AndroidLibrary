package com.android.common.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.common.R;
import com.android.common.utils.common.ToastUtils;
import com.android.common.utils.view.StatusBarUtils;
import com.android.common.webview.client.MyWebChromeClient;
import com.android.common.webview.client.MyWebViewClient;
import com.android.common.webview.client.WebViewUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class WebViewHelper {

    private static final String TAG = "WebViewHelper";
    private MyWebChromeClient webChromeClient;
    private MyWebViewClient webViewClient;

    private FragmentActivity activity;
    private MyWebView mWebView;
    private ImageView iv_arrow, iv_close;
    private View rootView;

    // webview 拍照
    private PopupWindow picPopupWindow;
    private static final int PHOTO_GRAPH = 6;// 拍照
    private static final int PHOTO_PICTURE = 7;// 图库
    private static final String IMAGE_UNSPECIFIED = "image/*";

    // webview 读取系统图片
    private ValueCallback<Uri> mUploadMsg;
    private ValueCallback<Uri[]> mUploadMsg5Plus;
    private File file, captureFile;

    public static WebViewHelper create(FragmentActivity activity) {
        WebViewHelper webViewHelper = new WebViewHelper(activity);
        webViewHelper.initView();
        webViewHelper.initListener();
        webViewHelper.registerLifecycle();
        WebViewUtils.clearHeader();
        WebViewUtils.clearCookie();
        return webViewHelper;
    }

    private WebViewHelper(FragmentActivity activity) {
        this.activity = activity;
    }

    private void initView() {
        rootView = LayoutInflater.from(activity).inflate(R.layout.common_activity_webview, null);
        // 设置系统状态栏透明
        StatusBarUtils.configStatusBar(activity);
        // 获取系统状态栏高度
        int height = StatusBarUtils.getStatusBarHeight(activity);
        View viewTitle = rootView.findViewById(R.id.view_title);
        viewTitle.setPadding(0, height, 0, 0);
        mWebView = rootView.findViewById(R.id.webview);
        iv_arrow = rootView.findViewById(R.id.iv_arrow);
        iv_close = rootView.findViewById(R.id.iv_close);
    }


    private void initListener() {
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    activity.finish();
                }
            }
        });
        // 处理webview返回键
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {  //表示按返回键
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void initClient() {
        webViewClient = new MyWebViewClient(rootView);
        mWebView.setWebViewClient(webViewClient);
        webChromeClient = new MyWebChromeClient(rootView);
        mWebView.setWebChromeClient(webChromeClient);
    }

    private void registerLifecycle() {
        activity.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            private void onDestroy() {
                WebViewHelper.this.onDestroy();
            }
        });
    }

    public void setWebViewClientInterface(MyWebViewClient.WebViewClientInterface clientInterface) {
        if (webViewClient != null)
            webViewClient.setWebViewClientInterface(clientInterface);
    }

    public void setWebChromeClientInterface(MyWebChromeClient.OpenFileChooserCallBack clientInterface) {
        if (webChromeClient != null)
            webChromeClient.setWebViewClientInterface(clientInterface);
    }

    public View getRootView() {
        return rootView;
    }

    public MyWebView getWebView() {
        return mWebView;
    }

    /**
     * 释放资源
     */
    private void onDestroy() {
        iv_arrow.setOnClickListener(null);
        iv_close.setOnClickListener(null);
        mWebView.setOnKeyListener(null);
        WebViewUtils.clearHeader();
        WebViewUtils.clearCookie();
        if (mWebView != null) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
            activity = null;
        }
    }

    /**
     * 从assets中加载文件
     */
    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    activity.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 注入本地js脚本
     */
    public void injectJs(WebView webView, String src) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(src, null);
        } else {
            webView.loadUrl(String.format("javascript:%s", src));
        }
    }

    /**
     * 本地调用js方法，无回调方法
     */
    public void excJsMethod(WebView webView, String method) {
        if (method.endsWith("()")) {
            webView.loadUrl("javascript:" + method);
        } else {
            webView.loadUrl("javascript:" + method + "()");
        }
    }

    /**
     * 本地调用js函数
     * 1、JavaScript和Java的交互是在子线程上面进行的
     * 2、js 函数返回值在onReceiveValue回调函数中取得，
     */
    public void evaluateJs(WebView webView, String method, ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (callback == null) return;
            webView.evaluateJavascript(method, callback); // 本地执行js方法，在主线程执行回调方法
        } else {
            // 4.4 以下需要手动执行js方法，js函数的返回值要再通过js调用本地java方法传递
            webView.loadUrl(method);
        }
    }

    /**
     * Js调用本地方法，jsObject 参数会被指向object参数
     * 因此js中可以通过jsObject.methodName()调用object对象有的本地方法
     * 1、object 参数的实现是一个普通Java类,实现对应的方法
     * 2、method 参数，其实就是注入的JS对象名称
     * CallAppFunction.onReceiveTitleInfo()
     */
    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String jsObject) {
        mWebView.addJavascriptInterface(object, jsObject); // js调用本地方法，执行在子线程
    }

    public void load(String url) {
        mWebView.loadUrl(url);
    }

    /**
     * 弹出图片方式选择框
     */
    public void showPicWindow() {
        mUploadMsg = webChromeClient.getUploadMsg();
        mUploadMsg5Plus = webChromeClient.getUploadMsgs();

        // android 7.0系统解决拍照Uri的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        file = new File(Environment.getExternalStorageDirectory() + "/common/webview");
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }

        if (picPopupWindow == null) {
            View contentView = View.inflate(activity, R.layout.common_popupwindow_photo, null);
            Button btnCancel = contentView.findViewById(R.id.btn_cancel);
            Button btnCapture = contentView.findViewById(R.id.btn_capture);
            Button btnPicture = contentView.findViewById(R.id.btn_picture);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetWebViewPic();
                    picPopupWindow.dismiss();
                }
            });
            btnCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA)) {
                        ToastUtils.showToast(activity.getApplicationContext(), "请开启相机权限");
                        return;
                    }
                    Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureFile = new File(file, System.currentTimeMillis() + "_picture.jpg");
                    capture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureFile));
                    activity.startActivityForResult(capture, PHOTO_GRAPH);
                    picPopupWindow.dismiss();
                }
            });
            btnPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent picture = new Intent(Intent.ACTION_PICK, null);
                    picture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                    activity.startActivityForResult(picture, PHOTO_PICTURE);
                    picPopupWindow.dismiss();
                }
            });
            // 使用布局参数设置宽高
            picPopupWindow = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            // 设置空白背景,点击空白/返回键可以消失
//            picPopupWindow.setBackgroundDrawable(new ColorDrawable());
            // 设置不消失，但是下层控件还会响应
//            picPopupWindow.setFocusable(false);
//            picPopupWindow.setOutsideTouchable(false);
            picPopupWindow.setAnimationStyle(R.style.CommonAnimation);
        }
        View parent = View.inflate(activity, R.layout.common_activity_webview, null);
        // 防止手机底部的菜单栏挡住
        picPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 从底层弹出
        picPopupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 获取本地图片
     */
    public void setPicData(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (mUploadMsg == null && mUploadMsg5Plus == null) {
                return;
            }
            if (requestCode == PHOTO_GRAPH) { // 拍照 返回的data为null
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg/jpg");
                values.put(MediaStore.Images.Media.DATA, captureFile.toString());
                Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) { // 更新系统图库
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(captureFile)));
                    activity.sendBroadcast(new Intent("Intent.ACTION_GET_IMAGE"));
                    // 将图片传递至webview
                    setWebViewPic(uri);
                }
            }
            if (requestCode == PHOTO_PICTURE) { // 图库 返回的data不为null
                setWebViewPic(data.getData());
            }
        } else {
            resetWebViewPic();
        }
    }

    /**
     * 向WebView传递图片
     */
    private void setWebViewPic(Uri uri) {
        if (mUploadMsg != null) {
            mUploadMsg.onReceiveValue(uri);
            mUploadMsg = null;
        } else {
            mUploadMsg5Plus.onReceiveValue(new Uri[]{uri});
            mUploadMsg5Plus = null;
        }
    }

    /**
     * 取消之后要告诉WebView 不要再等待返回结果，设置为空就等于重置了状态
     */
    private void resetWebViewPic() {
        if (mUploadMsg != null) {
            mUploadMsg.onReceiveValue(null);
            mUploadMsg = null;
        }
        if (mUploadMsg5Plus != null) {
            mUploadMsg5Plus.onReceiveValue(null);
            mUploadMsg5Plus = null;
        }
    }

}

package com.android.common.widget.photo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.utils.common.ToastUtils;
import com.android.common.utils.view.StatusBarUtils;
import com.android.common.utils.view.ToolbarUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

/**
 * 图片页面
 * Created by Administrator on 2016/10/31.
 */
public class PhotoPickerActivity extends AppCompatActivity {

    private static final int PHOTO_GRAPH = 6;// 拍照
    private File file, captureFile;
    private ArrayList<String> datas = new ArrayList<>();
    private PhotoAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_photo);
        // 设置系统状态栏透明
        StatusBarUtils.configStatusBar(this);
        // 获取系统状态栏高度
        int height = StatusBarUtils.getStatusBarHeight(this);
        View viewTitle = findViewById(R.id.title);
        viewTitle.setPadding(0, height, 0, 0);
        ToolbarUtil.configTitle(this, "选择图片", View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        adapter = new PhotoAdapter(this, datas);
        mRecyclerView.setAdapter(adapter);
        // 定义分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(String path, int position) {
                if (position == 0) {
                    if (!checkPermissions(Manifest.permission.CAMERA))
                        return;
                    capture();
                } else {
                    showDialog(path);
                }
            }
        });

        // android 7.0系统解决拍照后Uri的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        file = new File(Environment.getExternalStorageDirectory() + "/common/image");
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }

        if (checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE))
            queryImage(adapter);
    }

    private void capture() {

        Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureFile = new File(file, System.currentTimeMillis() + "_picture.jpg");
        capture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureFile));
        startActivityForResult(capture, PHOTO_GRAPH);
    }

    private void queryImage(PhotoAdapter adapter) {
        // 使用异步查询类
        PhotoQueryHandler queryHandler = new PhotoQueryHandler(getContentResolver(), adapter);
        // 开启子线程查询数据，通过token区分当前查询的标识，cookie为查询数据
        // 获取真实图片 id/path
        queryHandler.startQuery(0, datas, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA
                }, null, null, "_id desc");
    }


    // 获取本地图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PHOTO_GRAPH) { // 拍照 返回的data为null
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg/jpg");
                values.put(MediaStore.Images.Media.DATA, captureFile.toString());
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) { // 更新系统图库
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(captureFile)));
                    sendBroadcast(new Intent("Intent.ACTION_GET_IMAGE"));
                    // 传递图片
                    mCallback.photoResult(captureFile.toString());
                    mCallback = null;
                    finish();
                }
            }
        }
    }

    public static void requestPhoto(Activity activity, PhotoPickerActivity.PhotoResultCallback callback) {
        startPhoto(activity);
        mCallback = callback;
    }

    private static void startPhoto(Activity activity) {
        Intent intent = new Intent(activity, PhotoPickerActivity.class);
        activity.startActivity(intent);
    }

    private static PhotoPickerActivity.PhotoResultCallback mCallback;

    public interface PhotoResultCallback {
        void photoResult(String photoPath);
    }

    private void showDialog(final String path) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_NoActionBar);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.common_dialog_photo, null);
        dialog.setContentView(dialogView);
        dialog.show();
        PhotoView photoView = dialogView.findViewById(R.id.photoview);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.common_image_failed);
        File file = new File(path);
        Glide.with(this).load(file).apply(requestOptions).thumbnail(0.1f).into(photoView);
        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mCallback.photoResult(path);
                mCallback = null;
                finish();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // 检查权限
    private boolean checkPermissions(String permission) {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getApplicationContext(), permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i])) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    queryImage(adapter);
                } else {
                    ToastUtils.showToast(getApplicationContext(), "请开启SD卡读写权限");
                }
            }
            if (Manifest.permission.CAMERA.equals(permissions[i])) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    capture();
                } else {
                    ToastUtils.showToast(getApplicationContext(), "请开启相机权限");
                }
            }
        }
    }

}

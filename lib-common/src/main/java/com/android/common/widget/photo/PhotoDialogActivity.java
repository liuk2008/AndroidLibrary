package com.android.common.widget.photo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.common.R;
import com.android.common.utils.common.ToastUtils;

import java.io.File;


/**
 * Android 7.0开始，一个应用提供自身文件给其它应用使用时，如果给出一个file://格式的URI的话，应用会抛出FileUriExposedException
 * 想要在自己应用和其他应用之间共享File数据，只能使用content://的方式
 */
public class PhotoDialogActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PHOTO_GRAPH = 6;// 拍照
    private static final int PHOTO_PICTURE = 7;// 图库
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private View photoView;
    private File file, captureFile;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_popupwindow_photo);
        photoView = findViewById(R.id.view_photo);
        Button btnCapture = findViewById(R.id.btn_capture);
        Button btnPicture = findViewById(R.id.btn_picture);
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCapture.setOnClickListener(this);
        btnPicture.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // android 7.0系统解决拍照Uri的问题
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
    }

    // 获取本地图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            if (requestCode == PHOTO_PICTURE) { // 图库 返回的data不为null
                final Uri uri = data.getData();
                String scheme = uri.getScheme();
                if (scheme == null) {
                    mCallback.photoResult(uri.getPath());
                    mCallback = null;
                    finish();
                } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                    mCallback.photoResult(uri.getPath());
                    mCallback = null;
                    finish();
                } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                            if (cursor.moveToFirst()) {
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                path = cursor.getString(column_index);
                            }
                            cursor.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCallback.photoResult(path);
                                    mCallback = null;
                                    finish();
                                }
                            });
                        }
                    }).start();
                }
            }
        } else {
            mCallback = null;
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_capture) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            else
                capture();
        } else if (id == R.id.btn_picture) {
            photoView.setVisibility(View.GONE);
            Intent picture = new Intent(Intent.ACTION_PICK, null);
            picture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            startActivityForResult(picture, PHOTO_PICTURE);
        } else if (id == R.id.btn_cancel) {
            photoView.setVisibility(View.GONE);
            mCallback = null;
            finish();
        }
    }

    public static void requestPhoto(Activity activity, PhotoResultCallback callback) {
        startPhoto(activity);
        mCallback = callback;
    }

    private static void startPhoto(Activity activity) {
        Intent intent = new Intent(activity, PhotoDialogActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.common_anim_enter, R.anim.common_anim_out);
    }

    private static PhotoResultCallback mCallback;

    public interface PhotoResultCallback {
        void photoResult(String photoPath);
    }

    private void capture() {
        photoView.setVisibility(View.GONE);
        Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureFile = new File(file, System.currentTimeMillis() + "_picture.jpg");
        capture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureFile));
        startActivityForResult(capture, PHOTO_GRAPH);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Manifest.permission.CAMERA.equals(permissions[0]) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            capture();
        else
            ToastUtils.showToast(getApplicationContext(), "请开启相机权限");
    }

}

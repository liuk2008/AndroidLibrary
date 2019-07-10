package com.android.common.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * android 6.0 动态申请权限
 * <p>
 * shouldShowRequestPermissionRationale()说明
 * 1、shouldShowRequestPermissionRationale() 默认返回 false。
 * 2、第一次请求权限时，如果用户拒绝了，再次请求时 shouldShowRequestPermissionRationale() 返回 true。
 * 3、当多次（两次或两次以上）请求操作时，会有不再提示的选择框，如果用户选择了不再提示并拒绝，shouldShowRequestPermissionRationale() 返回 false。
 * 4、设备的策略禁止当前应用获取这个权限的授权，shouldShowRequestPermissionRationale() 返回 false。
 */
public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = "PermissionActivity";
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private String tip, permission;
    private String[] permissions;
    private int request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        request = intent.getIntExtra("request", 0);
        tip = intent.getStringExtra("tip");
        permission = intent.getStringExtra("permission");
        permissions = intent.getStringArrayExtra("permissions");
    }

    @Override
    protected void onStart() {
        super.onStart();
        switch (request) {
            case REQUEST_PERMISSION:
                if (!checkPermission(this, permission))
                    ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
                else
                    finish();
                break;
            case REQUEST_PERMISSIONS:
                List<String> deniedList = new ArrayList<>();
                // 检测权限是否授权
                for (String permission : permissions) {
                    if (!checkPermission(this, permission)) {
                        deniedList.add(permission);
                    }
                }
                if (deniedList.size() > 0)
                    ActivityCompat.requestPermissions(this, deniedList.toArray(new String[deniedList.size()]), PERMISSION_REQUEST_CODE);
                else
                    finish();
                break;
            default:
                finish();
                break;
        }
    }

    public static void requestPermission(Activity activity, @NonNull String permission, String tip) {
        startPermission(activity, permission, tip);
    }

    public static void requestPermissions(Activity activity, @NonNull String[] permissions, String tips) {
        startPermissions(activity, permissions, tips);
    }

    private static void startPermission(Activity activity, @NonNull String permission, @NonNull String tip) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra("permission", permission);
        intent.putExtra("tip", tip);
        intent.putExtra("request", REQUEST_PERMISSION);
        activity.startActivity(intent);
    }

    private static void startPermissions(Activity activity, @NonNull String[] permissions, @NonNull String tips) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra("permissions", permissions);
        intent.putExtra("tip", tips);
        intent.putExtra("request", REQUEST_PERMISSIONS);
        activity.startActivity(intent);
    }

    /**
     * 此方法是权限申请的回调方法，在此方法中处理权限申请成功或失败后的操作。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean result = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        result = false;
                        break;
                    }
                }
                if (!result)
                    showDialog(tip);
                else
                    finish();
                break;
            default:
                break;
        }
    }

    public interface OnPermissionCallback {
        void onGranted(String[] permissions);

        void onDenied(String[] permissions);
    }

    private void showDialog(String tip) {
        new AlertDialog.Builder(this)
                .setTitle("申请权限")
                .setMessage(!TextUtils.isEmpty(tip) ? tip : "请在系统设置中开启APP相关权限")
                .setCancelable(false)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                        }
                    }
                }).show();
    }

    private boolean checkPermission(Context context, String permission) {
        boolean result = true;
        // android 6.0 以下会在安装时自动获取权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return result;
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (targetSdkVersion >= Build.VERSION_CODES.M) {
            result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            result = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
        return result;
    }

}

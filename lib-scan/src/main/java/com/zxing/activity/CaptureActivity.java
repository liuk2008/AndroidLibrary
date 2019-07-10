package com.zxing.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxing.R;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * 扫码页面
 */
public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CaptureActivity";
    private boolean playBeep;
    private boolean vibrate;
    private boolean hasSurface;
    private String characterSet;

    private MediaPlayer mediaPlayer;
    private ViewfinderView viewfinderView;
    private CaptureActivityHandler handler;
    private Vector<BarcodeFormat> decodeFormats;
    private InactivityTimer inactivityTimer;
    private static final float BEEP_VOLUME = 0.10f;

    private static final int REQUEST_PERMISSION_CAMERA = 1000;
    private static final int REQUEST_PERMISSION_PHOTO = 1001;
    public static final int PHOTO_REQUEST_GALLERY = 1002;
    private int ifOpenLight = 0; //判断是否开启闪光灯
    private ImageView ivLight;
    private int myLayout = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        View view_title = findViewById(R.id.view_title);
        view_title.setPadding(0,getStatusBarHeight(),0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        ImageView iv_arrow = findViewById(R.id.iv_arrow);
        ImageView iv_gallery = findViewById(R.id.iv_gallery);
        ivLight = findViewById(R.id.iv_light);
        ivLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IfOpenLight();
            }
        });
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_PHOTO);
                } else {
                    startActivityForGallery(PHOTO_REQUEST_GALLERY);
                }
            }
        });

        viewfinderView = findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        CameraManager.init(getApplication());
        // 动态申请相机权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode 获取结果
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        // FIXME
        String codeFormat = result.getBarcodeFormat().toString();
        String scanData = result.getText();
        Log.d(TAG, "编码： ------>" + codeFormat);//QR_CODE-->二维码
        Log.d(TAG, "扫码：scanData-->" + scanData); // 当前二维码页面扫描到的vin
        dealData(scanData, codeFormat);// 处理扫码数据
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {

            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }

    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    // 是否开启闪光灯
    private void IfOpenLight() {
        ifOpenLight++;
        switch (ifOpenLight % 2) {
            case 0:
                // 关闪光灯
                if (CameraManager.get().closeLight()) {
                    ivLight.setImageResource(R.drawable.icon_light_close);
                }
                break;
            case 1:
                // 开闪光灯
                if (CameraManager.get().openLight()) {
                    ivLight.setImageResource(R.drawable.icon_light_open);
                }
                break;
            default:
                break;
        }
    }

    // 加载各自的布局文件
    public void initView(int layout) {
        this.myLayout = layout;
    }

    // 处理扫码数据
    public void dealData(String data, String format) {

    }

    // 重置扫描区
    public void initCapture() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(-1, 500);
        }
    }

    /**
     * 进入系统图库
     */
    private void startActivityForGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    // 处理权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 未获得Camera权限
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App开启摄像头权限后重试")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_PHOTO) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App中开启文件权限后重试")
                        .setPositiveButton("确定", null)
                        .show();
            } else {
                startActivityForGallery(PHOTO_REQUEST_GALLERY);
            }
        }
    }

    // 获取系统图库
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null && requestCode == PHOTO_REQUEST_GALLERY) {
                Uri inputUri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(inputUri, proj, null, null, null);
                if (cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    Result result = QrUtils.decodeImage(path);
                    if (result != null) {
                        /*  if (BuildConfig.DEBUG) Log.d(TAG, result.getText());*/
                        handleDecode(result, null);
                    } else {
                        new AlertDialog.Builder(CaptureActivity.this)
                                .setTitle("提示")
                                .setMessage("未识别的二维码/条码，请重新选择")
                                .setPositiveButton("确定", null)
                                .show();
                    }
                }
            }
        }
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

}
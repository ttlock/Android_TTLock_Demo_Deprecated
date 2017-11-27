package com.example.ttlock.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.ttlock.bl.sdk.util.LogUtil;

public class BaseActivity extends AppCompatActivity {

    protected static boolean DBG = true;

    private static final int REQUEST_PERMISSION_REQ_CODE = 1;

    public ProgressDialog progressDialog;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
//        setContentView(R.layout.activity_base_list);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void onResume() {
        super.onResume();
    }

    public void showProgressDialog() {
//        if(progressDialog == null) {
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("请稍后……");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        }
//        progressDialog.show();
        showProgressDialog(getString(R.string.words_wait));
    }

    public void showProgressDialog(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog == null) {
                    progressDialog = new ProgressDialog(BaseActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                }
                progressDialog.setMessage(msg);
                progressDialog.show();
            }
        });
    }

    public void cancelProgressDialog() {
        if(progressDialog != null)
            progressDialog.cancel();
    }

    protected final <V extends View> V getView(int id) {
        return (V) findViewById(id);
    }

    protected final <V extends View> V getView(View parent, int id) {
        return (V) parent.findViewById(id);
    }

    public final void toast(final String msg) {
        if(Looper.getMainLooper() == Looper.myLooper())
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * 权限请求
     * @param permission
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean requestPermission(String permission) {
//        LogUtil.d("permission:" + permission, true);
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                LogUtil.w("not grant", true);
                return false;
            }
            //请求权限
//            LogUtil.d("请求权限", DBG);
            requestPermissions(new String[]{permission}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0]))//位置权限获取成功启动蓝牙
                    LogUtil.d("位置权限授权成功", DBG);
                    MyApplication.mTTLockAPI.startBTDeviceScan();
            } else {
                LogUtil.w("Permission denied.", DBG);
            }
        }
    }

    public void start_activity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("", DBG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelProgressDialog();
        LogUtil.d("", DBG);
    }
}

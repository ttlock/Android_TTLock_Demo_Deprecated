package com.example.ttlock.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.ttlock.bl.sdk.util.LogUtil;

public class BaseListActivity extends ListActivity {

    private static boolean DBG = true;

    private static final int REQUEST_PERMISSION_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
    }

    protected final <V extends View> V getView(int id) {
        return (V) findViewById(id);
    }

    /**
     * permission request
     * @param permission
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                LogUtil.w("not grant", true);
                return false;
            }
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
                if(Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0]))
                    MyApplication.mTTLockAPI.startBTDeviceScan();
            } else {
                LogUtil.w("Permission denied.", DBG);
            }
        }
    }
}

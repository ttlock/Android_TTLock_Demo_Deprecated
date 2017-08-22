package com.example.ttlock.activity;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.example.ttlock.constant.Config;
import com.example.ttlock.databinding.ActivityDeviceFirmwareUpdateBinding;
import com.example.ttlock.dialog.MultiButtonDialog;
import com.example.ttlock.model.FirmwareInfo;
import com.example.ttlock.model.Key;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.sp.MyPreference;
import com.ttlock.bl.sdk.api.DeviceFirmwareUpdateApi;
import com.ttlock.bl.sdk.callback.DeviceFirmwareUpdateCallback;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import static com.example.ttlock.activity.MainActivity.curKey;

public class DeviceFirmwareUpdateActivity extends BaseActivity {

    ActivityDeviceFirmwareUpdateBinding binding;
    private int checkNum = 0;
    private Key mKey;
    private FirmwareInfo firmwareInfo;

    /**
     * 再次检测
     */
    private boolean checkAgain;

    private DeviceFirmwareUpdateApi deviceFirmwareUpdateApi;

    private DeviceFirmwareUpdateCallback deviceFirmwareUpdateCallback = new DeviceFirmwareUpdateCallback() {
        @Override
        public void onGetLockFirmware(int specialValue, String module, String hardware, String firmware) {
            LogUtil.d("firmwareInfo:" + firmwareInfo, DBG);
            if(firmwareInfo != null) {
                firmwareInfo.specialValue = specialValue;
                firmwareInfo.modelNum = module;
                firmwareInfo.hardwareRevision = hardware;
                firmwareInfo.firmwareRevision = firmware;
                checkAgain();
            }
        }

        @Override
        public void onStatusChanged(final int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (status) {
                        case DeviceFirmwareUpdateApi.UpgradeOprationPreparing:
                            binding.status.setText("准备中");
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationUpgrading:
                            binding.status.setText("升级中");
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationRecovering:
                            binding.status.setText("恢复中");
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationSuccess:
                            deviceFirmwareUpdateApi.upgradeComplete();
                            cancelProgressDialog();
                            binding.status.setText("升级成功");
                            toast("升级成功");
                            break;
                    }
                }
            });
        }

        @Override
       public void onDfuAborted(String deviceAddress) {
            LogUtil.d(deviceAddress, DBG);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            LogUtil.e("percent:" + percent, DBG);
        }

        @Override
        public void onError(int errorCode, Error error, String errorContent) {
            LogUtil.w("errorCode:" + errorCode, DBG);
            LogUtil.w("error:" + error, DBG);
            LogUtil.w("errorContent:" + errorContent, DBG);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelProgressDialog();
                    binding.status.setText("升级失败");
                    showRetryDialog();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_firmware_update);
        mKey = curKey;
        deviceFirmwareUpdateApi = new DeviceFirmwareUpdateApi(this, MyApplication.mTTLockAPI, deviceFirmwareUpdateCallback);
    }

    public void onClick(View view) {
        if(checkAgain)
            getLockFirmware();
        else
            checkUpdate();
    }

    private void getLockFirmware() {
        showProgressDialog();
        deviceFirmwareUpdateApi.getLockFirmware(mKey.getLockMac(), mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr());
//        MyApplication.bleSession.setOperation(Operation.GET_LOCK_VERSION_INFO);
//        MyApplication.bleSession.setLockmac(mKey.getLockMac());
//        mTTLockAPI.connect(mKey.getLockMac());
    }

    private void checkAgain() {
        new AsyncTask<Void, String, FirmwareInfo>() {
            @Override
            protected FirmwareInfo doInBackground(Void... params) {
                String json = ResponseService.isNeedUpdateAgain(mKey.getLockId(), firmwareInfo);
                firmwareInfo = GsonUtil.toObject(json, FirmwareInfo.class);
                return firmwareInfo;
            }
            @Override
            protected void onPostExecute(FirmwareInfo firmwareInfo) {
                super.onPostExecute(firmwareInfo);
                cancelProgressDialog();
                if(firmwareInfo.errcode == 0) {
                    switch (firmwareInfo.needUpgrade) {
                        case 0://不需要升级
                            binding.status.setText("已是最新版本");
                            binding.version.setText(firmwareInfo.version);
                            break;
                        case 1://需要升级
                            binding.status.setText("有新版本");
                            binding.version.setText(firmwareInfo.version);
                            showUpgradeDialog();
                            //TODO:弹出升级对话框
                            break;
                        case 2://版本信息未知
                            binding.status.setText("版本信息未知");
                            binding.version.setText("版本信息未知");
                            showGetLockFirmwareDialog();
                            break;
                    }
                } else {
                    toast(firmwareInfo.errmsg);
                }
            }
        }.execute();
    }

    private void checkUpdate() {
        new AsyncTask<Void, String, FirmwareInfo>() {
            @Override
            protected FirmwareInfo doInBackground(Void... params) {
                    String json = ResponseService.isNeedUpdate(mKey.getLockId());
                    firmwareInfo = GsonUtil.toObject(json, FirmwareInfo.class);
                return firmwareInfo;
            }
            @Override
            protected void onPostExecute(FirmwareInfo firmwareInfo) {
                super.onPostExecute(firmwareInfo);
                cancelProgressDialog();
                if(firmwareInfo.errcode == 0) {
                    switch (firmwareInfo.needUpgrade) {
                        case 0://不需要升级
                            binding.status.setText("已是最新版本");
                            binding.version.setText(firmwareInfo.version);
                            break;
                        case 1://需要升级
                            binding.status.setText("有新版本");
                            binding.version.setText(firmwareInfo.version);
                            showUpgradeDialog();
                            //TODO:弹出升级对话框
                            break;
                        case 2://版本信息未知
                            binding.status.setText("版本信息未知");
                            binding.version.setText("版本信息未知");
                            showGetLockFirmwareDialog();
//                            checkAgain = true;
                            break;
                    }
                } else {
                    toast(firmwareInfo.errmsg);
                }
            }
        }.execute();
    }

    public void showGetLockFirmwareDialog() {
        final MultiButtonDialog dialog = new MultiButtonDialog(this);
        dialog.show();
        dialog.setContentText("是否获取锁固件信息");
        dialog.setPositiveClickListener(new MultiButtonDialog.PositiveClickListener() {
            @Override
            public void onPositiveClick(String inputContent) {
                dialog.cancel();
                getLockFirmware();
            }
        });
    }

    public void showRetryDialog() {
        final MultiButtonDialog dialog = new MultiButtonDialog(this);
        dialog.show();
        dialog.setContentText("是否重试");
        dialog.setPositiveClickListener(new MultiButtonDialog.PositiveClickListener() {
            @Override
            public void onPositiveClick(String inputContent) {
                dialog.cancel();
                showProgressDialog();
                deviceFirmwareUpdateApi.retry();
            }
        });
    }

    public void showUpgradeDialog() {
        final MultiButtonDialog dialog = new MultiButtonDialog(this);
        dialog.show();
        dialog.setContentText("是否进行升级");
        dialog.setPositiveClickListener(new MultiButtonDialog.PositiveClickListener() {
            @Override
            public void onPositiveClick(String inputContent) {
                dialog.cancel();
                showProgressDialog();
                deviceFirmwareUpdateApi.upgradeFirmware(Config.CLIENT_ID, MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN),
                        mKey.getLockId(), firmwareInfo.modelNum, firmwareInfo.hardwareRevision, firmwareInfo.firmwareRevision, mKey.getLockMac(), mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceFirmwareUpdateApi.abortUpgradeProcess();
    }
}

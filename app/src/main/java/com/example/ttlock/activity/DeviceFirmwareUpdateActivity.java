package com.example.ttlock.activity;

import android.app.ProgressDialog;
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

    private ProgressDialog progressDialog;

    /**
     * check again
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
                            binding.status.setText(getString(R.string.words_preparing));
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationUpgrading:
                            binding.status.setText(getString(R.string.words_upgrading));
                            progressDialog = new ProgressDialog(DeviceFirmwareUpdateActivity.this);
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationRecovering:
                            binding.status.setText(getString(R.string.words_recovering));
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationSuccess:
                            deviceFirmwareUpdateApi.upgradeComplete();
                            cancelProgressDialog();
                            binding.status.setText(getString(R.string.words_upgrade_successed));
                            toast(getString(R.string.words_upgrade_successed));
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
            cancelProgressDialog();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(percent);
            progressDialog.show();
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DBG);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DBG);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DBG);
            progressDialog.cancel();
            showProgressDialog(getString(R.string.words_recovering));
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
                    binding.status.setText(getString(R.string.words_upgrade_failed));
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
        /**
         * stop bluetooth scan
         */
        MyApplication.mTTLockAPI.stopBTDeviceScan();
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
                        case 0://no need to upgrade
                            binding.status.setText(getString(R.string.is_the_lastest_version));
                            binding.version.setText(firmwareInfo.version);
                            break;
                        case 1://need upgrade
                            binding.status.setText(getString(R.string.new_version_found));
                            binding.version.setText(firmwareInfo.version);
                            showUpgradeDialog();
                            break;
                        case 2://unknown version
                            binding.status.setText(getString(R.string.unknown_lock_version));
                            binding.version.setText(getString(R.string.unknown_lock_version));
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
                        case 0://no need to upgrade
                            binding.status.setText(getString(R.string.is_the_lastest_version));
                            binding.version.setText(firmwareInfo.version);
                            break;
                        case 1://need upgrade
                            binding.status.setText(getString(R.string.new_version_found));
                            binding.version.setText(firmwareInfo.version);
                            showUpgradeDialog();
                            break;
                        case 2://unknown version
                            binding.status.setText(getString(R.string.unknown_lock_version));
                            binding.version.setText(getString(R.string.unknown_lock_version));
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
        dialog.setContentText(getString(R.string.words_is_read_device_info));
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
        dialog.setContentText(getString(R.string.words_is_retry));
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
        dialog.setContentText(getString(R.string.words_is_upgrade));
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
        //start bluetooth scan again
        MyApplication.mTTLockAPI.startBTDeviceScan();
    }
}

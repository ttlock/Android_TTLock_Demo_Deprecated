package com.example.ttlock;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.ttlock.activity.BaseActivity;
import com.example.ttlock.activity.MainActivity;
import com.example.ttlock.constant.BleConstant;
import com.example.ttlock.dao.DbService;
import com.example.ttlock.dao.gen.DaoMaster;
import com.example.ttlock.dao.gen.DaoSession;
import com.example.ttlock.dao.gen.KeyDao;
import com.example.ttlock.enumtype.Operation;
import com.example.ttlock.model.BleSession;
import com.example.ttlock.model.Key;
import com.example.ttlock.myInterface.OperateCallback;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.sp.MyPreference;
import com.example.ttlock.utils.DateUitl;
import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.entity.DeviceInfo;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.entity.LockData;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.util.LogUtil;

import org.greenrobot.greendao.database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class MyApplication extends Application {

    private static boolean DBG = true;

    public static final boolean ENCRYPTED = true;

    public static Context mContext;

    private static DaoSession daoSession;

    public static KeyDao keyDao;

    private Activity curActivity;

    public static OperateCallback operateCallback;

    /**
     * current used key
     */
    private Key curKey;

    /**
     *  bluetooth operation
     */
    public static BleSession bleSession = BleSession.getInstance(Operation.UNLOCK, null);

    /**
     * TTLockAPI
     */
    public static TTLockAPI mTTLockAPI;

    private Handler handler = new Handler();

    /**
     * TTLock Callback
     */
    private TTLockCallback mTTLockCallback = new TTLockCallback() {
        @Override
        public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
            //found device and broadcast
            broadcastUpdate(BleConstant.ACTION_BLE_DEVICE, BleConstant.DEVICE, extendedBluetoothDevice);
            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
            Key localKey = DbService.getKeyByLockmac(extendedBluetoothDevice.getAddress());
            if(localKey != null) {
//                operateSuccess = false;
                switch (bleSession.getOperation()) {
//                    case UNLOCK:
//                        if(extendedBluetoothDevice.isTouch())
//                             mTTLockAPI.connect(extendedBluetoothDevice);
//                        break;
                    case SET_ADMIN_KEYBOARD_PASSWORD:
                    case SET_DELETE_PASSWORD:
                    case SET_LOCK_TIME:
                    case RESET_KEYBOARD_PASSWORD:
                    case RESET_EKEY:
                    case RESET_LOCK:
                    case GET_LOCK_TIME:
                    case GET_OPERATE_LOG:
                    case ADD_PASSCODE:
                        if(extendedBluetoothDevice.getAddress().equals(bleSession.getLockmac()))
                            mTTLockAPI.connect(extendedBluetoothDevice);
                        break;
                }
            }
        }

        @Override
        public void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
            LogUtil.d("bleSession.getOperation():" + bleSession.getOperation(), DBG);

            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
            Key localKey = DbService.getKeyByLockmac(extendedBluetoothDevice.getAddress());
            curKey = MainActivity.curKey;
            //uid equal to openid
            int uid = MyPreference.getOpenid(mContext, MyPreference.OPEN_ID);
//            operateSuccess = false;
            switch (bleSession.getOperation()) {
                case ADD_ADMIN:
                    if (localKey == null)
                        mTTLockAPI.lockInitialize(extendedBluetoothDevice);
                    else {
                        toast(getString(R.string.words_has_exist_lock));
                        ((BaseActivity) curActivity).cancelProgressDialog();
                    }
                    break;
                case UNLOCK:
                case CLICK_UNLOCK:
                    if(localKey != null) {
                        if(localKey.isAdmin())
                            mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                        else
                            mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    }
//                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), 0, localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case SET_ADMIN_KEYBOARD_PASSWORD:
                    mTTLockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr(), bleSession.getPassword());
                    break;
                case SET_DELETE_PASSWORD:
                    mTTLockAPI.setDeletePassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr(), bleSession.getPassword());
                    break;
                case SET_LOCK_TIME:
                    mTTLockAPI.setLockTime(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), System.currentTimeMillis(), curKey.getLockFlagPos(), curKey.getAesKeyStr(), curKey.getTimezoneRawOffset());
                    break;
                case RESET_KEYBOARD_PASSWORD:
                    mTTLockAPI.resetKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr());
                    break;
                case RESET_EKEY://reset ekey, lockFlagPos +1
                    mTTLockAPI.resetEKey(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockFlagPos() + 1, curKey.getAesKeyStr());
                    break;
                case RESET_LOCK:
                    mTTLockAPI.resetLock(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr());
                    break;
                case GET_OPERATE_LOG:
                    mTTLockAPI.getOperateLog(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
                case GET_LOCK_TIME:
                    mTTLockAPI.getLockTime(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
                case LOCKCAR_UP:
                    if(localKey.isAdmin())
                        mTTLockAPI.lockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr());
                    else
                        mTTLockAPI.lockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
//                    mTTLockAPI.lockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case LOCKCAR_DOWN:
                    if(localKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
//                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case DELETE_ONE_KEYBOARDPASSWORD://set the keyboard password type to 0
                    mTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), 0, bleSession.getPassword(), localKey.getAesKeyStr());
                    break;
                case GET_LOCK_VERSION_INFO:
                    mTTLockAPI.readDeviceInfo(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeyStr());
                    break;
                case ADD_PASSCODE:
                    mTTLockAPI.addPeriodKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), bleSession.getPassword(), bleSession.getStartDate(), bleSession.getEndDate(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
            }
        }

        @Override
        public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
            //default is unlock flag
            bleSession.setOperation(Operation.UNLOCK);
//            broadcastUpdate(BleConstant.ACTION_BLE_DISCONNECTED, BleConstant.DEVICE, extendedBluetoothDevice);
//            if(!operateSuccess) {
//                toast("disconnected");
//            }
            LogUtil.d("disconnected", DBG);
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int protocolType, int protocolVersion, int scene, int groupId, int orgId, Error error) {

        }

        @Override
        public void onLockInitialize(ExtendedBluetoothDevice extendedBluetoothDevice, final LockData lockData, Error error) {
            if(error == Error.SUCCESS) {
                final String lockDataJson = lockData.toJson();

                toast(getString(R.string.words_lock_add_successed_and_init));
                mTTLockAPI.unlockByAdministrator(null, 0, lockData.lockVersion, lockData.adminPwd, lockData.lockKey, lockData.lockFlagPos, System.currentTimeMillis(), lockData.aesKeyStr, lockData.timezoneRawOffset);
                new AsyncTask<Void, String, Boolean>() {

                    @Override
                    protected Boolean  doInBackground(Void... params) {
                        Boolean flag = false;
                        String json = ResponseService.lockInit(lockDataJson, lockData.getLockName());
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if(jsonObject.has("errcode")) {
                                String errmsg = jsonObject.getString("description");
                                toast(errmsg);
                            } else {
                                Intent intent = new Intent(mContext,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                flag = true;
                                toast(getString(R.string.words_lock_init_successed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast(getString(R.string.words_lock_init_failed) + e.getMessage());
                        }
                        return flag;
                    }

                    @Override
                    protected void onPostExecute(Boolean flag) {

                    }
                }.execute();
            } else {//failure
                toast(error.getErrorMsg());
            }
        }

        @Override
        public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, DeviceInfo deviceInfo, Error error) {

        }

        @Override
        public void onOperateRemoteControl(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operateType, int keyValue, Error error) {

        }

        @Override
        public void onOperateDoorSensorLocking(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operationType, int operationValue, Error error) {

        }

        @Override
        public void onGetDoorSensorState(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int state, Error error) {

        }

        @Override
        public void onSetNBServer(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

//        @Override
//        public void onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error) {
//            LogUtil.d("add admin:" + error + "," + "lockVersion--->" + lockVersionString+"battery------>"+extendedBluetoothDevice.getBatteryCapacity(), DBG);
//
//        }

        @Override
        public void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, final int lockFlagPos, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                curKey.setLockFlagPos(lockFlagPos);
                DbService.updateKey(curKey);
                toast(getString(R.string.words_lock_flag_pos) + lockFlagPos);
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String json = ResponseService.resetKey(curKey.getLockId(), lockFlagPos);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int errorCode = jsonObject.getInt("errcode");
                            if(errorCode != 0) {
                                String errmsg = jsonObject.getString("description");
                                toast(errmsg);
                            } else {
                                toast(getString(R.string.words_lock_flag_upload_successed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast(getString(R.string.words_lock_flag_upload_failed) + e.getMessage());
                        }
                        return json;
                    }
                }.execute();
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSetLockName(ExtendedBluetoothDevice extendedBluetoothDevice, String lockname, Error error) {

        }

        @Override
        public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminCode, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                curKey.setNoKeyPwd(adminCode);
                DbService.updateKey(curKey);
                toast(getString(R.string.words_set_admin_code_successed) + adminCode);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSetDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, String deleteCode, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                curKey.setDeletePwd(deleteCode);
                DbService.updateKey(curKey);
                toast(getString(R.string.words_set_delete_code_successed) + deleteCode);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error) {
            if(error == Error.SUCCESS) {
                toast(getString(R.string.words_unlock_successed));
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }


        @Override
        public void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
            if(error == Error.SUCCESS) {
                toast(getString(R.string.words_set_lock_time_successed));
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long lockTime, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                //convert the time by the lock time zone
                String time = DateUitl.getTime(lockTime - TimeZone.getDefault().getOffset(System.currentTimeMillis()) + curKey.getTimezoneRawOffset(), "yyyy:MM:dd HH:mm");
                toast(String.format(getString(R.string.words_lock_time), time));
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, final String pwdInfo, final long timestamp, final Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                new AsyncTask<Void, String, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        curKey.setPwdInfo(pwdInfo);
                        curKey.setTimestamp(timestamp);
                        DbService.updateKey(curKey);
                        String msg = error.getErrorMsg();
                        if(error == Error.SUCCESS) {
                            String json = ResponseService.resetKeyboardPwd(curKey);
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                msg = msg + " : " + jsonObject.getString("errmsg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return msg;
                    }
                    @Override
                    protected void onPostExecute(String msg) {
                        super.onPostExecute(msg);
                        ((BaseActivity)curActivity).cancelProgressDialog();
                        toast(msg);
                    }
                }.execute();
            } else {
                toast(error.getErrorMsg());
            }
        }

        @Override
        public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int validPwdNum, Error error) {

        }

        @Override
        public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice extendedBluetoothDevice, int progress, Error error) {

        }

        @Override
        public void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
            Log.d("reset", "error:" + error);
            if(error == Error.SUCCESS) {
                toast(getString(R.string.words_lock_reset_key_invalid));
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String password, long startDate, long endDate, Error error) {
            if(error == Error.SUCCESS) {
                String msg = getString(R.string.words_password) + ":" + password + "\n"
                        + getString(R.string.words_period) + ":" + DateUitl.getTime(startDate)
                        + "-" + DateUitl.getTime(endDate);
                toast(msg);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String originPwd, String newPwd, Error error) {

        }

        @Override
        public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String deletedPwd, Error error) {
            if(error == Error.SUCCESS) {
                toast(getString(R.string.words_delete_password_successed));
                if(operateCallback != null)
                    operateCallback.onSuccess();
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

        }

        @Override
        public void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, final String records, final Error error) {
            if(error == Error.SUCCESS) {
                new AsyncTask<Void, String, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = null;
                        if(error == Error.SUCCESS) {
                            String json = ResponseService.uploadOperateLog(curKey.getLockId(), records);
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                int errcode = jsonObject.getInt("errcode");
                                if(errcode == 0) {
                                    msg = getString(R.string.words_upload_log_successed);
                                } else msg = jsonObject.getString("errmsg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else msg = error.getErrorMsg();
                        return msg;
                    }
                    @Override
                    protected void onPostExecute(String msg) {
                        super.onPostExecute(msg);
                        ((BaseActivity)curActivity).cancelProgressDialog();
                        toast(msg);
                    }
                }.execute();
                toast(getString(R.string.words_log) + records);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int specialValue, Error error) {

        }

        @Override
        public void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long cardNo, Error error) {

        }

        @Override
        public void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, long startDate, long endDate, Error error) {

        }

        @Override
        public void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, Error error) {

        }

        @Override
        public void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

        @Override
        public void onSetWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

        @Override
        public void onSetWristbandKeyToDev(Error error) {

        }

        @Override
        public void onSetWristbandKeyRssi(Error error) {

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, Error error) {

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, int totalCount, Error error) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int currentCount, int totalCount, Error error) {

        }

        @Override
        public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long fingerPrintNo, long startDate, long endDate, Error error) {

        }

        @Override
        public void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long fingerPrintNo, Error error) {

        }

        @Override
        public void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

        @Override
        public void onSearchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int currentTime, int minTime, int maxTime, Error error) {

        }

        @Override
        public void onModifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int autoLockTime, Error error) {

        }

//        @Override
//        public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String modelNumber, String hardwareRevision, String firmwareRevision, String manufactureDate, String lockClock) {
//
//        }

        @Override
        public void onEnterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

        }

        @Override
        public void onGetLockSwitchState(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int status, Error error) {

        }

        @Override
        public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int uid, int uniqueid, long lockTime, Error error) {
            if(error == Error.SUCCESS) {
                toast(getString(R.string.words_lock_successed));
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onScreenPasscodeOperate(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int status, Error error) {

        }

        @Override
        public void onRecoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int op, Error error) {

        }

        @Override
        public void onSearchICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String json, Error error) {

        }

        @Override
        public void onSearchFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String json, Error error) {

        }

        @Override
        public void onSearchPasscode(ExtendedBluetoothDevice extendedBluetoothDevice, String json, Error error) {

        }

        @Override
        public void onSearchPasscodeParam(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String pwdInfo, long timestamp, Error error) {

        }

        @Override
        public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operateType, int state, int specialValue, Error error) {

        }

        @Override
        public void onOperateAudioSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operateType, int state, Error error) {

        }

        @Override
        public void onGetElectricQuantity(ExtendedBluetoothDevice extendedBluetoothDevice, int electricQuantity, Error error) {

        }

        @Override
        public void onGetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String adminCode, Error error) {

        }

        @Override
        public void onQueryPassageMode(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String passageModeData, Error error) {

        }

        @Override
        public void onAddOrModifyPassageMode(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

        @Override
        public void onDeletePassageMode(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

        @Override
        public void onClearPassageMode(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

        }

    };

    ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            curActivity = activity;
            LogUtil.d("activity:" + activity.getClass(), DBG);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //TODO:
            LogUtil.d("activity:" + activity.getClass(), DBG);
//            curActivity = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("init application", DBG);
        init();
    }

    private void init() {
        mContext = this;
        registerActivityLifecycleCallbacks(callbacks);
        initGreenDao();
        initTTLock();
    }

    /**
     * database initial
     */
    private void initGreenDao() {
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "ttlock-db-encrypted.db" : "ttlock-db.db");
//        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "ttlock.db", null);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        keyDao = daoSession.getKeyDao();
    }

    /**
     * TTLock initial
     */
    private void initTTLock() {
        LogUtil.d("create TTLockAPI", DBG);
        mTTLockAPI = new TTLockAPI(mContext, mTTLockCallback);
    }

    public static Context getInstance() {
        return mContext;
    }

    //TODO:
    private <K,V extends Parcelable> void broadcastUpdate(String action, K key, V value) {
        final Intent intent = new Intent(action);
        if(key != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable((String) key, value);
            intent.putExtras(bundle);
        }
        sendBroadcast(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.d("", DBG);
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    private void toast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}

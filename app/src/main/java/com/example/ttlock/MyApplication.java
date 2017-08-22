package com.example.ttlock;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Toast;

import com.example.ttlock.activity.BaseActivity;
import com.example.ttlock.activity.MainActivity;
import com.example.ttlock.constant.BleConstant;
import com.example.ttlock.dao.DbService;
import com.example.ttlock.enumtype.Operation;
import com.example.ttlock.model.BleSession;
import com.example.ttlock.model.DaoMaster;
import com.example.ttlock.model.DaoSession;
import com.example.ttlock.model.Key;
import com.example.ttlock.model.KeyDao;
import com.example.ttlock.myInterface.OperateCallback;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.sp.MyPreference;
import com.example.ttlock.utils.DateUitl;
import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.entity.Error;
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
     * 当前操作的key
     */
    private Key curKey;

    /**
     *  蓝牙操作
     */
    public static BleSession bleSession = BleSession.getInstance(Operation.UNLOCK, null);

    /**
     * 通通锁
     */
    public static TTLockAPI mTTLockAPI;

    private Handler handler = new Handler();

    /**
     * 通通锁回调
     */
    private TTLockCallback mTTLockCallback = new TTLockCallback() {
        @Override
        public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
            //发现设备并广播
            broadcastUpdate(BleConstant.ACTION_BLE_DEVICE, BleConstant.DEVICE, extendedBluetoothDevice);
            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
            Key localKey = DbService.getKeyByAccessTokenAndLockmac(accessToken, extendedBluetoothDevice.getAddress());
            if(localKey != null) {
//                operateSuccess = false;
                switch (bleSession.getOperation()) {
                    case UNLOCK:
                        if(extendedBluetoothDevice.isTouch())
                             mTTLockAPI.connect(extendedBluetoothDevice);
                        break;
                    case SET_ADMIN_KEYBOARD_PASSWORD:
                    case SET_DELETE_PASSWORD:
                    case SET_LOCK_TIME:
                    case RESET_KEYBOARD_PASSWORD:
                    case RESET_EKEY:
                    case RESET_LOCK:
                    case GET_LOCK_TIME:
                    case GET_OPERATE_LOG:
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
            //TODO:
            Key localKey = DbService.getKeyByAccessTokenAndLockmac(accessToken, extendedBluetoothDevice.getAddress());
            curKey = MainActivity.curKey;
            //TODO:目前uid传0即可
            int uid = 0;
//            operateSuccess = false;
            switch (bleSession.getOperation()) {
                case ADD_ADMIN:
                    //TODO:判断本地是否存在
                    mTTLockAPI.addAdministrator(extendedBluetoothDevice);
                    break;
                case UNLOCK:
                case CLICK_UNLOCK:
                    if(localKey != null) {//本地存在锁
                        if(localKey.isAdmin())
                            mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                        else
                            mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    }
//                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), 0, localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case SET_ADMIN_KEYBOARD_PASSWORD://管理码
                    mTTLockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
                    break;
                case SET_DELETE_PASSWORD://删除码
                    mTTLockAPI.setDeletePassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
                    break;
                case SET_LOCK_TIME://设置锁时间
                    mTTLockAPI.setLockTime(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getUnlockKey(), System.currentTimeMillis(), curKey.getLockFlagPos(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case RESET_KEYBOARD_PASSWORD://重置键盘密码
                    mTTLockAPI.resetKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
                    break;
                case RESET_EKEY://重置电子钥匙 锁标志位+1
                    mTTLockAPI.resetEKey(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
                    break;
                case RESET_LOCK://重置锁
                    mTTLockAPI.resetLock(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
                    break;
                case GET_OPERATE_LOG://获取操作日志
                    mTTLockAPI.getOperateLog(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case GET_LOCK_TIME://获取锁时间
                    mTTLockAPI.getLockTime(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case LOCKCAR_UP://车位锁升
                    if(localKey.isAdmin())
                        mTTLockAPI.lockByAdministrator(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr());
                    else
                        mTTLockAPI.lockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    mTTLockAPI.lockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case LOCKCAR_DOWN://车位锁降
                    if(localKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case DELETE_ONE_KEYBOARDPASSWORD://这里的密码类型传0
                    mTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), 0, bleSession.getPassword(), localKey.getAesKeystr());
                    break;
                case GET_LOCK_VERSION_INFO:
                    mTTLockAPI.readDeviceInfo(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeystr());
                    break;
            }
        }

        @Override
        public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
            //默认是开门标志
            bleSession.setOperation(Operation.UNLOCK);
//            //断开连接
//            broadcastUpdate(BleConstant.ACTION_BLE_DISCONNECTED, BleConstant.DEVICE, extendedBluetoothDevice);
//            if(!operateSuccess) {
//                toast("蓝牙已断开");
//            }
            LogUtil.d("蓝牙断开", DBG);
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, int i4, Error error) {

        }

        @Override
        public void onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error) {
            LogUtil.d("添加管理员:" + error + "," + "lockVersion--->" + lockVersionString+"电量------>"+extendedBluetoothDevice.getBatteryCapacity(), DBG);
            if(error == Error.SUCCESS) {
                //TODO:save数据
//                operateSuccess = true;
                final Key key = new Key();
                key.setAccessToken(MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN));
                key.setAdmin(true);
                key.setLockVersion(lockVersionString);
                key.setLockName(extendedBluetoothDevice.getName());
                key.setLockMac(extendedBluetoothDevice.getAddress());
                key.setAdminPs(adminPs);
                key.setUnlockKey(unlockKey);
                key.setAdminKeyboardPwd(adminKeyboardPwd);
                key.setDeletePwd(deletePwd);
                key.setPwdInfo(pwdInfo);
                key.setTimestamp(timestamp);
                key.setAesKeystr(aesKeystr);
                key.setSpecialValue(feature);

                //获取当前时区偏移量 可以传入-1 不考虑时区问题
                key.setTimezoneRawOffset(TimeZone.getDefault().getOffset(System.currentTimeMillis()));
                key.setModelNumber(modelNumber);
                key.setHardwareRevision(hardwareRevision);
                key.setFirmwareRevision(firmwareRevision);

                toast("锁添加成功，正在上传服务端进行初始化操作");

                new AsyncTask<Void, String, Boolean>() {

                    @Override
                    protected Boolean  doInBackground(Void... params) {
                        Boolean flag = false;
                        String json = ResponseService.lockInit(key);
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
                                toast("锁服务端初始化成功");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast("锁服务端初始化失败:" + e.getMessage());
                        }
                        return flag;
                    }

                    @Override
                    protected void onPostExecute(Boolean flag) {

                    }
                }.execute();
            } else {//失败
                toast(error.getErrorMsg());
            }
        }

        @Override
        public void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, final int lockFlagPos, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                curKey.setLockFlagPos(lockFlagPos);
                DbService.updateKey(curKey);
                toast("重置电子钥匙成功，锁标志位:" + lockFlagPos);
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
                                toast("锁标志位上传服务器成功");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast("锁标志位上传服务器失败:" + e.getMessage());
                        }
                        return json;
                    }
                }.execute();
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSetLockName(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

        }

        @Override
        public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminCode, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                curKey.setAdminKeyboardPwd(adminCode);
                DbService.updateKey(curKey);
                toast("设置管理码成功:" + adminCode);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSetDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, String deleteCode, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                curKey.setDeletePwd(deleteCode);
                DbService.updateKey(curKey);
                toast("设置清空码成功:" + deleteCode);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                toast("开门成功");
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int uniqueid, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                toast("关锁成功");
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
            if(error == Error.SUCCESS) {
                toast("设置锁时间成功");
//                operateSuccess = true;
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long lockTime, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                //转换成锁时区时间
                String time = DateUitl.getTime(lockTime - TimeZone.getDefault().getOffset(System.currentTimeMillis()) + curKey.getTimezoneRawOffset(), "yyyy:MM:dd HH:mm");
                toast("当前锁时区时间:" + time);
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
        public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                toast("锁重置成功，当前钥匙已失效");
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, long l, long l1, Error error) {

        }

        @Override
        public void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, String s1, Error error) {

        }

        @Override
        public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdTypei, String deletedPwd, Error error) {
            if(error == Error.SUCCESS) {
                toast("通过蓝牙删除密码成功");
                if(operateCallback != null)
                    operateCallback.onSuccess();
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

        }

        @Override
        public void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String records, Error error) {
            if(error == Error.SUCCESS) {
//                operateSuccess = true;
                toast("操作日志:" + records);
            } else toast(error.getErrorMsg());
            ((BaseActivity)curActivity).cancelProgressDialog();
        }

        @Override
        public void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int i, long l, Error error) {

        }

        @Override
        public void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, long l1, long l2, Error error) {

        }

        @Override
        public void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, Error error) {

        }

        @Override
        public void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSetWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSetWristbandKeyToDev(Error error) {

        }

        @Override
        public void onSetWristbandKeyRssi(Error error) {

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int i, long l, Error error) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, long l1, long l2, Error error) {

        }

        @Override
        public void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, Error error) {

        }

        @Override
        public void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSearchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, Error error) {

        }

        @Override
        public void onModifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String modelNumber, String hardwareRevision, String firmwareRevision, String manufactureDate, String lockClock) {

        }

        @Override
        public void onEnterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

        }

        @Override
        public void onSearchBicycleStatus(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, long l, Error error) {

        }

        @Override
        public void onScreenPasscodeOperate(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onRecoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

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
            curActivity = null;
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
     * 数据库初始化
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
     * 通通锁初始化操作
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

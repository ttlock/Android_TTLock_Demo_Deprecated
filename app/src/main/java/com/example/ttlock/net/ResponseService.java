package com.example.ttlock.net;

import com.example.ttlock.MyApplication;
import com.example.ttlock.constant.Config;
import com.example.ttlock.model.FirmwareInfo;
import com.example.ttlock.model.Key;
import com.example.ttlock.sp.MyPreference;
import com.ttlock.bl.sdk.util.DigitUtil;

import java.util.HashMap;

/**
 * Created by TTLock on 2016/9/8 0008.
 */
public class ResponseService {
    private static final String TAG = "ResponseService";
    private static String actionUrl = "https://api.ttlock.com.cn";
    private static String actionUrlV3 = actionUrl + "/v3";

    /**
     * authorize
     * @param username  user name
     * @param password  password
     * @return
     */
    public static String auth(String username, String password) {
        String url = actionUrl + "/oauth2/token";
        HashMap params = new HashMap();
        params.put("client_id", Config.CLIENT_ID);
        params.put("client_secret", Config.CLIENT_SECRET);
        params.put("grant_type", "password");
        params.put("username", username);
        params.put("password", DigitUtil.getMD5(password));
        params.put("redirect_uri", Config.REDIRECT_URI);
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api after calling SDK method to add a lock. This api will create an admin ekey for current user.
     * After initializing a lock, the admin can send ekey or create passcode for others.
     * @return
     */
    public static String lockInit(String lockData, String lockAlias) {
        String url = actionUrlV3 + "/lock/initialize";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockAlias", lockAlias);
        params.put("lockData", lockData);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api to delete common ekeys and admin ekey
     * All ekeys and passcodes will be deleted from server when you delete the admin ekey
     * @param keyId key id
     * @return
     */
    public static String deleteKey(int keyId) {
        String url = actionUrlV3 + "/key/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("keyId", String.valueOf(keyId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api after you calling the SDK method to reset the passcode. All passcode will be invalidated after reseting, except the super passcode.
     * @param key
     * @return
     */
    public static String resetKeyboardPwd(Key key) {
        String url = actionUrlV3 + "/lock/resetKeyboardPwd";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(key.getLockId()));
        params.put("pwdInfo", key.getPwdInfo());
        params.put("timestamp", String.valueOf(key.getTimestamp()));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * The admin of the locks can't send ekeys to himself
     * For one lock, any user can only have one ekey.If you send an ekey to someone who already have one, the previous one will be deleted.
     * Note:It will be a permenant ekey when you set the startDate and endDate to 0
     * @param key
     * @return
     */
    public static String sendEKey(Key key, String receiverUsername) {
        String url = actionUrlV3 + "/key/send";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(key.getLockId()));
        params.put("receiverUsername", receiverUsername);

        //It will be a permenant ekey when you set the startDate and endDate to 0
        params.put("startDate", String.valueOf(0));
        params.put("endDate", String.valueOf(0));

        params.put("remarks", "");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * For locks of non-v4 passcode, if you get the prompt of "passcode is used out" or "no passcode data", please reset the passcode.
     * The valid time of the passcode should be defined in HOUR,set the minute and second to 0. If the valid period is longer than one year, the end time should be XX months later than the start time, without any difference in DAY,HOUR.
     * Earlier passcode version，reference：https://open.sciener.cn/doc/api/keyboardPwdType
     * @param lockId
     * @param keyboardPwdVersion
     * @param keyboardPwdType
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getKeyboardPwd(int lockId, int keyboardPwdVersion, int keyboardPwdType, long startDate, long endDate) {
        String url = actionUrlV3 + "/keyboardPwd/get";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyboardPwdVersion", String.valueOf(keyboardPwdVersion));
        params.put("keyboardPwdType", String.valueOf(keyboardPwdType));
        params.put("startDate", String.valueOf(startDate));
        params.put("endDate", String.valueOf(endDate));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * If you don't assign any value to the parameter lastUpdateDate, you will get a list of all ekeys
     * If you assign a time to the parameter lastUpdateDate, you will get a list of ekeys which have been updated since lastUpdateDate.
     * @param lastUpdateDate    The time of current request to sync ekey data. You can assign it to parameter lastUpdateDate next time when you want to sync ekey
     * @return
     */
    public static String syncData(long lastUpdateDate) {
        String url = actionUrlV3 + "/key/syncData";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lastUpdateDate", String.valueOf(lastUpdateDate));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api after you calling the SDK method to reset ekeys
     * All common ekeys will be invalidated after reseting, except the admin ekey
     * @param lockId             key id
     * @param lockFlagPos       The flag which will be used to check the validity of the ekey
     * @return
     */
    public static String resetKey(int lockId, int lockFlagPos) {
        String url = actionUrlV3 + "/lock/resetKey";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("lockFlagPos", String.valueOf(lockFlagPos));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /** -------------------------------gateway related interface-------------------------------------- */

    /**
     * get user id
     * @return
     */
    public static String getUserId() {
        String url = actionUrlV3 + "/user/getUid";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api after you calling the SDK method to add a gateway, to check whether it has been added successfully.
     * @param gatewayNetMac    The Mac which you will get when calling the SDK method to add a gateway
     * @return
     */
    public static String isInitSuccess(String gatewayNetMac) {
        String url = actionUrlV3 + "/gateway/isInitSuccess";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("gatewayNetMac", gatewayNetMac);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Get the time of lock via WiFi
     * @param lockId    lock id
     * @return
     */
    public static String queryLockDate(int lockId) {
        String url = actionUrlV3 + "/lock/queryDate";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * update the lock time by wifi
     * @param lockId     lock id
     * @return
     */
    public static String updateLockDate(int lockId) {
        String url = actionUrlV3 + "/lock/updateDate";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     *  gateway list of user
     * @param pageNo        Page, start from 1
     * @param pageSize      Items per page, default 20, max 100
     * @return
     */
    public static String gatewayList(int pageNo, int pageSize) {
        String url = actionUrlV3 + "/gateway/list";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * delete the gateway
     * @param gatewayId    Gateway ID
     * @return
     */
    public static String deleteGateway(int gatewayId) {
        String url = actionUrlV3 + "/gateway/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("gatewayId", String.valueOf(gatewayId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Get the lock list of a gateway
     * @param gatewayId     gateway id
     * @return
     */
    public static String underGatewayLockList(int gatewayId) {
        String url = actionUrlV3 + "/gateway/listLock";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("gatewayId", String.valueOf(gatewayId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }




    /**
     * Get all created passcodes of a lock
     * @param lockId        Lock ID
     * @param pageNo        Page, start from 1
     * @param pageSize      Items per page, default 20, max 100
     * @return
     */
    public static String keyboardPwdList(int lockId, int pageNo, int pageSize) {
        String url = actionUrlV3 + "/lock/listKeyboardPwd";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Only the locks with V4 passcode can delete a passcode via bluetooth or gateway.
     * @param lockId            lock id
     * @param keyboardPwdId     Passcode ID
     * @param deleteType        Delete type:1-delete with App via Bluetooth;2-delete via WiFi gateway. The default value is 1. If it is 1, you should delete via bluetooth first. If it is 2, you can call this api to delete it directly.
     * @return
     */
    public static String deleteKeyboardPwd(int lockId, int keyboardPwdId, int deleteType) {
        String url = actionUrlV3 + "/keyboardPwd/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyboardPwdId", String.valueOf(keyboardPwdId));
        params.put("deleteType", String.valueOf(deleteType));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Check to see whether there is any upgrade for a lock, depending on modelNum、hardwareRevision、firmwareRevision、specialValue.
     * If there is no version info on the server, returns 'unknown'.In this case, you need to call SDK method to get aforementioned 4 parameters first, and then call [Upgrade recheck] to check for upgrading.
     * @param lockId    lock id
     * @return
     */
    public static String isNeedUpdate(int lockId) {
        String url = actionUrlV3 + "/lock/upgradeCheck";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Recheck to see whether there is any upgrade for a lock
     * Warning: The aforementioned 4 parameters will be set into database on server. Please make sure that they are from the SDK method.
     * @param lockId    lock id
     * @return
     */
    public static String isNeedUpdateAgain(int lockId, FirmwareInfo deviceInfo) {
        String url = actionUrlV3 + "/lock/upgradeRecheck";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("specialValue", String.valueOf(deviceInfo.specialValue));

        params.put("modelNum", deviceInfo.modelNum);
        params.put("hardwareRevision", deviceInfo.hardwareRevision);
        params.put("firmwareRevision", deviceInfo.firmwareRevision);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * upload the operation log from lock
     * @param lockId
     * @param records
     * @return
     */
    public static String uploadOperateLog(int lockId, String records) {
        String url = actionUrlV3 + "/lockRecord/upload";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("records", records);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
}

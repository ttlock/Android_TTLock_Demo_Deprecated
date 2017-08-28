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
//    private static String actionUrl = "http://120.26.119.23:8085";
    private static String actionUrlV2 = actionUrl + "/v2";
    private static String actionUrlV3 = actionUrl + "/v3";

    /**
     * 授权
     * @param username  用户名
     * @param password  密码
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
     * 绑定管理员
     * @param key
     * @return
     */
    @Deprecated
    public static String bindAdmin(Key key) {
        String url = actionUrlV2 + "/lock/bindingAdmin";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockName", key.getLockName());
        params.put("lockMac", key.getLockMac());
        params.put("lockKey", key.getUnlockKey());
        params.put("lockFlagPos", String.valueOf(key.getLockFlagPos()));
        params.put("aesKeyStr", key.getAesKeystr());
        params.put("lockVersion", key.getLockVersion());
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 锁初始化接口将锁相关的数据在服务端做初始化，同时会为调用该接口的用户生成一把管理员钥匙。
     * 锁初始化成功后，管理员钥匙拥有者就可以给其他用户发送普通钥匙或是发送密码了。
     * @param key
     * @return
     */
    public static String lockInit(Key key) {
        String url = actionUrlV3 + "/lock/init";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockName", key.getLockName());
        params.put("lockAlias", key.getLockAlias());
        params.put("lockMac", key.getLockMac());
        params.put("lockKey", key.getUnlockKey());
        params.put("lockFlagPos", String.valueOf(key.getLockFlagPos()));
        params.put("aesKeyStr", key.getAesKeystr());
        params.put("lockVersion", key.getLockVersion());

        params.put("adminPwd", key.getAdminPs());
        params.put("noKeyPwd", key.getAdminKeyboardPwd());
        params.put("deletePwd", key.getDeletePwd());
        params.put("pwdInfo", key.getPwdInfo());
        params.put("timestamp", String.valueOf(key.getTimestamp()));
        params.put("specialValue", String.valueOf(key.getSpecialValue()));
        params.put("timezoneRawOffset", String.valueOf(key.getTimezoneRawOffset()));

        params.put("modelNum", key.getModelNumber());
        params.put("hardwareRevision", key.getHardwareRevision());
        params.put("firmwareRevision", key.getFirmwareRevision());

        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 解绑管理员
     * @param lockId
     * @return
     */
    @Deprecated
    public static String unbindAdmin(int lockId) {
        String url = actionUrlV2 + "/lock/unbind";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 普通用户删除自己的钥匙
     * @param lockId
     * @param keyId
     * @return
     */
    @Deprecated
    public static String deleteEKeyBySelf(int lockId, int keyId) {
        String url = actionUrlV2 + "/key/deleteBySelf";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyId", String.valueOf(keyId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 删除钥匙
     * @param keyId 钥匙Id
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
     * 重置键盘密码
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
     * 发送电子钥匙
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

        //起始、结束时间传入时间戳，0表示永久钥匙
        params.put("startDate", String.valueOf(0));
        params.put("endDate", String.valueOf(0));

        //备注,留言
        params.put("remarks", "");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 获取钥匙列表
     * @return
     */
    @Deprecated
    public static String keyList() {
        String url = actionUrlV2 + "/lock/listShareKey";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 下载电子钥匙
     * @param lockId
     * @param keyId
     * @return
     */
    @Deprecated
    public static String downloadKey(int lockId, int keyId) {
        String url = actionUrlV2 + "/key/download";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", MyPreference.getStr(MyApplication.mContext, MyPreference.ACCESS_TOKEN));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyId", String.valueOf(keyId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 获取键盘密码
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
     * APP第一次同步数据，不需要传lastUpdateDate，服务端会返回全量的钥匙数据。
     * APP将钥匙数据缓存在本地，后续再调用该接口，携带上次返回的lastUpdateDate,服务端会返回这个时间点后新增加的钥匙和数据有变化的钥匙,APP根据返回的钥匙数据更新本地的钥匙。
     * @param lastUpdateDate    最近同步时间(最后一次调用该接口，服务端返回的)，不传则返回全量的钥匙数据。
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
     * 重置电子钥匙
     * @param lockId             钥匙Id
     * @param lockFlagPos       锁标志位
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

    /** -------------------------------网关相关接口-------------------------------------- */

    /**
     * 获取用户Id
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
     * 查询某网关是否初始化成功
     * @param gatewayNetMac    网关MAC地址
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
     * 读取锁时间
     * @param lockId    锁ID
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
     * 校准锁时间
     * @param lockId     锁ID
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
     *  获取名下网关列表
     * @param pageNo        页数
     * @param pageSize      页大小
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
     * 删除网关
     * @param gatewayId     网关id
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
     * 获取网关管理的锁列表
     * @param gatewayId     网关id
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
     * 获取锁的键盘密码列表
     * @param lockId        锁id
     * @param pageNo        页码，从1开始
     * @param pageSize      每页数量，默认20
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
     * 删除单个键盘密码
     * @param lockId            锁id
     * @param keyboardPwdId     键盘密码id
     * @param deleteType        删除方式:1-通过APP走蓝牙删除，2-通过网关走WIFI删除；不传则默认1,必需先通过APP蓝牙删除密码后调用该接口，如果锁有连接网关，则可以直接调用该接口删除密码。
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
     * 判断是否需要升级
     * @param lockId    锁id
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
     * 再次判断是否需要升级
     * @param lockId    锁id
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
     * 上传操作日志
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

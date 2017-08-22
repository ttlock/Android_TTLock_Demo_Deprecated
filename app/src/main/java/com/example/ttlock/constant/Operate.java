package com.example.ttlock.constant;

/**
 * Created by TTLock on 2016/9/19 0019.
 */
public class Operate {
    /**
     * 点击开门
     */
    public static final int CLICK_TO_UNLOCK = 0;
    /**
     * 设置管理码
     */
    public static final int SET_ADMIN_CODE = 1;
    /**
     * 设置清空码
     */
    public static final int SET_DELETE_CODE = 2;

    /**
     * 设置锁时间
     */
    public static final int SET_LOCK_TIME = 3;

    /**
     * 重置键盘密码
     */
    public static final int RESET_KEYBOARD_PASSWORD = 4;

    /**
     * 重置电子钥匙
     */
    public static final int RESET_EKEY = 5;

    /**
     * 重置锁(恢复出厂设置)
     */
    public static final int RESET_LOCK = 6;

    /**
     * 获取操作日志
     */
    public static final int GET_OPERATE_LOG = 7;

    /**
     * 获取锁时间
     */
    public static final int GET_LOCK_TIME = 8;

    /**
     * 发送电子钥匙
     */
    public static final int SEND_EKey = 9;

    /**
     * 获取密码
     */
    public static final int GET_PASSWORD = 10;

    /**
     * 车位锁升
     */
    public static final int LOCKCAR_UP = 11;

    /**
     * 车位锁降
     */
    public static final int LOCKCAR_DOWN = 12;

    /**
     * 设备固件升级
     */
    public static final int DEVICE_FIRMWARE_UPDATE = 13;
}

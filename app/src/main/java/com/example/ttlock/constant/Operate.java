package com.example.ttlock.constant;

/**
 * Created by TTLock on 2016/9/19 0019.
 */
public class Operate {
    /**
     * click to unlock
     */
    public static final int CLICK_TO_UNLOCK = 0;
    /**
     * set admin passcode
     */
    public static final int SET_ADMIN_CODE = 1;
    /**
     * set delete passcode
     */
    public static final int SET_DELETE_CODE = 2;

    /**
     * set lock time
     */
    public static final int SET_LOCK_TIME = 3;

    /**
     * reset keyboard passcode
     */
    public static final int RESET_KEYBOARD_PASSWORD = 4;

    /**
     * reset ekey
     */
    public static final int RESET_EKEY = 5;

    /**
     * reset lock(Restore factory settings)
     */
    public static final int RESET_LOCK = 6;

    /**
     * get operation log
     */
    public static final int GET_OPERATE_LOG = 7;

    /**
     * get lock time
     */
    public static final int GET_LOCK_TIME = 8;

    /**
     * send ekey
     */
    public static final int SEND_EKey = 9;

    /**
     * get passcode
     */
    public static final int GET_PASSWORD = 10;

    /**
     * parking lock up
     */
    public static final int LOCKCAR_UP = 11;

    /**
     * parking lock down
     */
    public static final int LOCKCAR_DOWN = 12;

    /**
     * device firmware upgrade
     */
    public static final int DEVICE_FIRMWARE_UPDATE = 13;
}

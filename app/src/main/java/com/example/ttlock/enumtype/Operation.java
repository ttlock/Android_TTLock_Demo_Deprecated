package com.example.ttlock.enumtype;

public enum Operation {
    /**
     * add admin
     */
    ADD_ADMIN,

    /**
     * nulock
     */
    UNLOCK,

    /**
     * reset keyboard passcode
     */
    RESET_KEYBOARD_PASSWORD,

    /**
     * reset ekey
     */
    RESET_EKEY,

    /**
     * set lock time
     */
    SET_LOCK_TIME,
    /**
     * get lock time
     */
    GET_LOCK_TIME,

    /**
     * get operation log
     */
    GET_OPERATE_LOG,

    /**
     * parking lock up
     */
    LOCKCAR_UP,

    /**
     * parking lock down
     */
    LOCKCAR_DOWN,

    /**
     * click to unlock
     */
    CLICK_UNLOCK,

    /**
     * set admin passcode
     */
    SET_ADMIN_KEYBOARD_PASSWORD,

    /**
     * set delete passcode
     */
    SET_DELETE_PASSWORD,

    /**
     * Restore factory settings
     */
    RESET_LOCK,

    /**
     * set lock time
     */
    CHECK_LOCKTIME,

    /**
     * modify key name
     */
    MODIFY_KEYNAME,

    /**
     * delete keyboard passcode
     *
     */
    DELETE_ONE_KEYBOARDPASSWORD,
    /**
     * get lock version information
     */
    GET_LOCK_VERSION_INFO,

}
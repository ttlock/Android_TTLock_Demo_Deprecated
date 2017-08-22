package com.example.ttlock.enumtype;

public enum Operation {
    /**
     * 添加管理员
     */
    ADD_ADMIN,

    /**
     * 开门
     */
    UNLOCK,

    /**
     * 重置键盘密码
     */
    RESET_KEYBOARD_PASSWORD,

    /**
     * 重置电子钥匙
     */
    RESET_EKEY,

    /**
     * 设置锁时间
     */
    SET_LOCK_TIME,
    /**
     * 获取锁的时间
     */
    GET_LOCK_TIME,

    /**
     * 获取操作日志
     */
    GET_OPERATE_LOG,

    /**
     * 车位锁升起
     */
    LOCKCAR_UP,

    /**
     * 车位锁降下
     */
    LOCKCAR_DOWN,

    /**
     * 点击开锁
     */
    CLICK_UNLOCK,

    /**
     * 设置管理员键盘密码
     */
    SET_ADMIN_KEYBOARD_PASSWORD,

    /**
     * 设置删除密码
     */
    SET_DELETE_PASSWORD,

    /**
     * 恢复出厂设置
     */
    RESET_LOCK,

    /**
     * 校准时间
     */
    CHECK_LOCKTIME,

    /**
     * 修改锁的名称
     */
    MODIFY_KEYNAME,

    /**
     * 设置删除单个键盘密码
     *
     */
    DELETE_ONE_KEYBOARDPASSWORD,
    /**
     * 获取锁版本信息
     */
    GET_LOCK_VERSION_INFO,

}
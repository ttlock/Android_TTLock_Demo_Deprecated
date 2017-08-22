package com.example.ttlock.model;

/**
 * Created by TTLock on 2017/8/16.
 */

public class FirmwareInfo {

    public int errcode;//错误码
    public String errmsg;//错误信息

    public int needUpgrade;//是否需要升级：0-否，1-是，2-未知
    public int specialValue;//特征值
    public String modelNum;//产品型号
    public String hardwareRevision;//硬件版本号
    public String firmwareRevision;//固件版本号
    public String version;//最新的固件版本号
}

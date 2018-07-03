package com.example.ttlock.model;

/**
 * Created by TTLock on 2017/8/16.
 */

public class FirmwareInfo {

    public int errcode;//error code
    public String errmsg;//error message

    public int needUpgrade;//is need upgrade：0-no，1-yes，2-unknown
    public int specialValue;//special value
    public String modelNum;//Product model
    public String hardwareRevision;
    public String firmwareRevision;
    public String version;//Latest version number
}

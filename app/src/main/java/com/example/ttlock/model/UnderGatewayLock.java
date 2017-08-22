package com.example.ttlock.model;

/**
 * Created by TTLock on 2017/3/24.
 */
public class UnderGatewayLock {
    /**
     * lockId : 24163
     * rssi : -52
     * updateDate : 1490337107000
     * lockMac : C5:BC:F2:41:46:08
     * lockName : S202T_084641
     */

    private int lockId;
    private int rssi;
    private long updateDate;
    private String lockMac;
    private String lockName;

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
}

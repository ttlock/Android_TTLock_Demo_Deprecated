package com.example.ttlock.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
@Entity
public class Key implements Parcelable {

    @Id
    private Long id;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 钥匙状态
     * "110401"	正常使用
     * "110402"	待接收
     * "110405"	已冻结
     * "110408"	已删除
     * "110410"	已重置
     */
    private String keyStatus;

    /**
     * 锁id
     */
    private int lockId;

    /**
     * 钥匙id
     */
    private int keyId;

    /**
     * 是否是管理员
     */
    private boolean isAdmin;

    /**
     * TODO:
     * 锁版本信息 json格式
     */
    private String lockVersion;

    /**
     * 锁名称
     */
    private String lockName;

    /**
     * 锁别名
     */
    private String lockAlias;

    /**
     * 锁mac地址
     */
    private String lockMac;

    /**
     * 电量
     */
    private int battery;

    /**
     * 锁标志位
     */
    private int lockFlagPos;

    /**
     * 锁数据
     * 直接传就行
     */
    private String adminPs;

    /**
     * 锁数据
     * 直接传就行
     */
    private String unlockKey;

    /**
     * 管理码
     */
    private String adminKeyboardPwd;

    /**
     * 删除码
     */
    private String deletePwd;

    /**
     * 密码数据信息
     */
    private String pwdInfo;

    /**
     * 时间搓
     */
    private long timestamp;

    /**
     * aesKey
     */
    private String aesKeystr;

    /**
     * 开始时间
     */
    private long startDate;

    /**
     * 结束时间
     */
    private long endDate;

    /**
     * 锁特征值，用于表示锁支持的功能
     */
    private int specialValue;

    /**
     * 不考虑时区问题传入-1即可
     * 锁所在时区和UTC时区时间的差数，单位milliseconds，默认28800000（中国时区）
     */
    private int timezoneRawOffset;

    /**
     * 锁型号
     */
    private String modelNumber;

    /**
     * 锁硬件版本号
     */
    private String hardwareRevision;
    
    /**
     * 锁固件版本号
     */
    String firmwareRevision;

    @Generated(hash = 124450289)
    public Key(Long id, String accessToken, String keyStatus, int lockId, int keyId, boolean isAdmin, String lockVersion, String lockName,
            String lockAlias, String lockMac, int battery, int lockFlagPos, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd,
            String pwdInfo, long timestamp, String aesKeystr, long startDate, long endDate, int specialValue, int timezoneRawOffset, String modelNumber,
            String hardwareRevision, String firmwareRevision) {
        this.id = id;
        this.accessToken = accessToken;
        this.keyStatus = keyStatus;
        this.lockId = lockId;
        this.keyId = keyId;
        this.isAdmin = isAdmin;
        this.lockVersion = lockVersion;
        this.lockName = lockName;
        this.lockAlias = lockAlias;
        this.lockMac = lockMac;
        this.battery = battery;
        this.lockFlagPos = lockFlagPos;
        this.adminPs = adminPs;
        this.unlockKey = unlockKey;
        this.adminKeyboardPwd = adminKeyboardPwd;
        this.deletePwd = deletePwd;
        this.pwdInfo = pwdInfo;
        this.timestamp = timestamp;
        this.aesKeystr = aesKeystr;
        this.startDate = startDate;
        this.endDate = endDate;
        this.specialValue = specialValue;
        this.timezoneRawOffset = timezoneRawOffset;
        this.modelNumber = modelNumber;
        this.hardwareRevision = hardwareRevision;
        this.firmwareRevision = firmwareRevision;
    }

    @Generated(hash = 2076226027)
    public Key() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLockId() {
        return this.lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getLockVersion() {
        return this.lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

    public String getAdminPs() {
        return this.adminPs;
    }

    public void setAdminPs(String adminPs) {
        this.adminPs = adminPs;
    }

    public String getUnlockKey() {
        return this.unlockKey;
    }

    public void setUnlockKey(String unlockKey) {
        this.unlockKey = unlockKey;
    }

    public String getAdminKeyboardPwd() {
        return this.adminKeyboardPwd;
    }

    public void setAdminKeyboardPwd(String adminKeyboardPwd) {
        this.adminKeyboardPwd = adminKeyboardPwd;
    }

    public String getDeletePwd() {
        return this.deletePwd;
    }

    public void setDeletePwd(String deletePwd) {
        this.deletePwd = deletePwd;
    }

    public String getPwdInfo() {
        return this.pwdInfo;
    }

    public void setPwdInfo(String pwdInfo) {
        this.pwdInfo = pwdInfo;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAesKeystr() {
        return this.aesKeystr;
    }

    public void setAesKeystr(String aesKeystr) {
        this.aesKeystr = aesKeystr;
    }

    public String getLockName() {
        return this.lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getLockMac() {
        return this.lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public int getLockFlagPos() {
        return this.lockFlagPos;
    }

    public void setLockFlagPos(int lockFlagPos) {
        this.lockFlagPos = lockFlagPos;
    }

    public int getKeyId() {
        return this.keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "Key{" +
                "id=" + id +
                ", lockId=" + lockId +
                ", keyId=" + keyId +
                ", isAdmin=" + isAdmin +
                ", lockVersion='" + lockVersion + '\'' +
                ", lockName='" + lockName + '\'' +
                ", lockMac='" + lockMac + '\'' +
                ", lockFlagPos=" + lockFlagPos +
                ", adminPs='" + adminPs + '\'' +
                ", unlockKey='" + unlockKey + '\'' +
                ", adminKeyboardPwd='" + adminKeyboardPwd + '\'' +
                ", deletePwd='" + deletePwd + '\'' +
                ", pwdInfo='" + pwdInfo + '\'' +
                ", timestamp=" + timestamp +
                ", aesKeystr='" + aesKeystr + '\'' +
                '}';
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public int getBattery() {
        return this.battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public long getStartDate() {
        return this.startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return this.endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getSpecialValue() {
        return this.specialValue;
    }

    public void setSpecialValue(int specialValue) {
        this.specialValue = specialValue;
    }

    public int getTimezoneRawOffset() {
        return this.timezoneRawOffset;
    }

    public void setTimezoneRawOffset(int timezoneRawOffset) {
        this.timezoneRawOffset = timezoneRawOffset;
    }

    public String getModelNumber() {
        return this.modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getHardwareRevision() {
        return this.hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public String getFirmwareRevision() {
        return this.firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public String getLockAlias() {
        return this.lockAlias;
    }

    public void setLockAlias(String lockAlias) {
        this.lockAlias = lockAlias;
    }

    public String getKeyStatus() {
        return this.keyStatus;
    }

    public void setKeyStatus(String keyStatus) {
        this.keyStatus = keyStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.accessToken);
        dest.writeString(this.keyStatus);
        dest.writeInt(this.lockId);
        dest.writeInt(this.keyId);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
        dest.writeString(this.lockVersion);
        dest.writeString(this.lockName);
        dest.writeString(this.lockAlias);
        dest.writeString(this.lockMac);
        dest.writeInt(this.battery);
        dest.writeInt(this.lockFlagPos);
        dest.writeString(this.adminPs);
        dest.writeString(this.unlockKey);
        dest.writeString(this.adminKeyboardPwd);
        dest.writeString(this.deletePwd);
        dest.writeString(this.pwdInfo);
        dest.writeLong(this.timestamp);
        dest.writeString(this.aesKeystr);
        dest.writeLong(this.startDate);
        dest.writeLong(this.endDate);
        dest.writeInt(this.specialValue);
        dest.writeInt(this.timezoneRawOffset);
        dest.writeString(this.modelNumber);
        dest.writeString(this.hardwareRevision);
        dest.writeString(this.firmwareRevision);
    }

    protected Key(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.accessToken = in.readString();
        this.keyStatus = in.readString();
        this.lockId = in.readInt();
        this.keyId = in.readInt();
        this.isAdmin = in.readByte() != 0;
        this.lockVersion = in.readString();
        this.lockName = in.readString();
        this.lockAlias = in.readString();
        this.lockMac = in.readString();
        this.battery = in.readInt();
        this.lockFlagPos = in.readInt();
        this.adminPs = in.readString();
        this.unlockKey = in.readString();
        this.adminKeyboardPwd = in.readString();
        this.deletePwd = in.readString();
        this.pwdInfo = in.readString();
        this.timestamp = in.readLong();
        this.aesKeystr = in.readString();
        this.startDate = in.readLong();
        this.endDate = in.readLong();
        this.specialValue = in.readInt();
        this.timezoneRawOffset = in.readInt();
        this.modelNumber = in.readString();
        this.hardwareRevision = in.readString();
        this.firmwareRevision = in.readString();
    }

    public static final Parcelable.Creator<Key> CREATOR = new Parcelable.Creator<Key>() {
        @Override
        public Key createFromParcel(Parcel source) {
            return new Key(source);
        }

        @Override
        public Key[] newArray(int size) {
            return new Key[size];
        }
    };
}

package com.example.ttlock.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TTLock on 2017/3/24.
 */
public class KeyboardPwd implements Parcelable {
    /**
     * lockId : 24163
     * keyboardPwdVersion : 4
     * endDate : 1490349825869
     * sendDate : 1490349836000
     * keyboardPwd : 58994923
     * keyboardPwdType : 1
     * startDate : 1490349825869
     */

    private int lockId;
    private int keyboardPwdVersion;
    private long endDate;
    private long sendDate;
    private String keyboardPwd;
    private int keyboardPwdType;
    private long startDate;
    private int keyboardPwdId;

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getKeyboardPwdVersion() {
        return keyboardPwdVersion;
    }

    public void setKeyboardPwdVersion(int keyboardPwdVersion) {
        this.keyboardPwdVersion = keyboardPwdVersion;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getSendDate() {
        return sendDate;
    }

    public void setSendDate(long sendDate) {
        this.sendDate = sendDate;
    }

    public String getKeyboardPwd() {
        return keyboardPwd;
    }

    public void setKeyboardPwd(String keyboardPwd) {
        this.keyboardPwd = keyboardPwd;
    }

    public int getKeyboardPwdType() {
        return keyboardPwdType;
    }

    public void setKeyboardPwdType(int keyboardPwdType) {
        this.keyboardPwdType = keyboardPwdType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public int getKeyboardPwdId() {
        return keyboardPwdId;
    }

    public void setKeyboardPwdId(int keyboardPwdId) {
        this.keyboardPwdId = keyboardPwdId;
    }

    public KeyboardPwd() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.lockId);
        dest.writeInt(this.keyboardPwdVersion);
        dest.writeLong(this.endDate);
        dest.writeLong(this.sendDate);
        dest.writeString(this.keyboardPwd);
        dest.writeInt(this.keyboardPwdType);
        dest.writeLong(this.startDate);
        dest.writeInt(this.keyboardPwdId);
    }

    protected KeyboardPwd(Parcel in) {
        this.lockId = in.readInt();
        this.keyboardPwdVersion = in.readInt();
        this.endDate = in.readLong();
        this.sendDate = in.readLong();
        this.keyboardPwd = in.readString();
        this.keyboardPwdType = in.readInt();
        this.startDate = in.readLong();
        this.keyboardPwdId = in.readInt();
    }

    public static final Creator<KeyboardPwd> CREATOR = new Creator<KeyboardPwd>() {
        @Override
        public KeyboardPwd createFromParcel(Parcel source) {
            return new KeyboardPwd(source);
        }

        @Override
        public KeyboardPwd[] newArray(int size) {
            return new KeyboardPwd[size];
        }
    };
}

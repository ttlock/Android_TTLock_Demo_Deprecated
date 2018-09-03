package com.example.ttlock.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TTLock on 2017/3/24.
 */
public class Gateway implements Parcelable {
    /**
     * gatewayMac : 93:C3:D6:79:EC:7C
     * lockNum : 0
     * isOnline : 1
     * gatewayId : 19
     */

    private String gatewayMac;
    private int lockNum;
    private int isOnline;
    private int gatewayId;

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    public int getLockNum() {
        return lockNum;
    }

    public void setLockNum(int lockNum) {
        this.lockNum = lockNum;
    }

    public boolean isOnline() {
        return isOnline == 1;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(int gatewayId) {
        this.gatewayId = gatewayId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gatewayMac);
        dest.writeInt(this.lockNum);
        dest.writeInt(this.isOnline);
        dest.writeInt(this.gatewayId);
    }

    public Gateway() {
    }

    protected Gateway(Parcel in) {
        this.gatewayMac = in.readString();
        this.lockNum = in.readInt();
        this.isOnline = in.readInt();
        this.gatewayId = in.readInt();
    }

    public static final Parcelable.Creator<Gateway> CREATOR = new Parcelable.Creator<Gateway>() {
        @Override
        public Gateway createFromParcel(Parcel source) {
            return new Gateway(source);
        }

        @Override
        public Gateway[] newArray(int size) {
            return new Gateway[size];
        }
    };
}

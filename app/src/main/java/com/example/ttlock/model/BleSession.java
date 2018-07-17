package com.example.ttlock.model;


import com.example.ttlock.enumtype.Operation;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class BleSession {

    /**
     * operation
     */
    private Operation operation;

    /**
     * lock mac
     */
    private String lockmac;

    /**
     * passcode
     */
    private String password;

    private long startDate;

    private long endDate;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getLockmac() {
        return lockmac;
    }

    public void setLockmac(String lockmac) {
        this.lockmac = lockmac;
    }

    public String getPassword() {
        return password;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static BleSession getInstance(Operation operation, String lockmac) {
        BleSession bleSession = new BleSession();
        bleSession.setOperation(operation);
        bleSession.setLockmac(lockmac);
        return bleSession;
    }
}

package com.dreamgyf.entity;

import java.io.Serializable;

public class UserPoint implements Serializable {

    private long userId;
    private int balance;
    private long updateTime;
    private int version;
    private int status;
    private int blockBalance;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBlockBalance() {
        return blockBalance;
    }

    public void setBlockBalance(int blockBalance) {
        this.blockBalance = blockBalance;
    }
}

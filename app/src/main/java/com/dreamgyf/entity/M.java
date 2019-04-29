package com.dreamgyf.entity;

import java.io.Serializable;

public class M implements Serializable {

    private long br;
    private int fid;
    private long size;
    private double vd;

    public long getBr() {
        return br;
    }

    public void setBr(long br) {
        this.br = br;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getVd() {
        return vd;
    }

    public void setVd(double vd) {
        this.vd = vd;
    }
}

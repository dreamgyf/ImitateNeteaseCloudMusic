package com.dreamgyf.entity;

import java.io.Serializable;

public class H implements Serializable {

    private long br;
    private int fid;
    private long size;
    private int vd;

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

    public int getVd() {
        return vd;
    }

    public void setVd(int vd) {
        this.vd = vd;
    }
}

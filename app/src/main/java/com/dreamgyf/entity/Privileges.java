package com.dreamgyf.entity;

import java.io.Serializable;

public class Privileges implements Serializable {

    private long id;
    private int fee;
    private int payed;
    private int st;
    private long pl;
    private long dl;
    private int sp;
    private int cp;
    private int subp;
    private boolean cs;
    private long maxbr;
    private long fl;
    private boolean toast;
    private int flag;
    private boolean preSell;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public long getPl() {
        return pl;
    }

    public void setPl(long pl) {
        this.pl = pl;
    }

    public long getDl() {
        return dl;
    }

    public void setDl(long dl) {
        this.dl = dl;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public int getSubp() {
        return subp;
    }

    public void setSubp(int subp) {
        this.subp = subp;
    }

    public boolean isCs() {
        return cs;
    }

    public void setCs(boolean cs) {
        this.cs = cs;
    }

    public long getMaxbr() {
        return maxbr;
    }

    public void setMaxbr(long maxbr) {
        this.maxbr = maxbr;
    }

    public long getFl() {
        return fl;
    }

    public void setFl(long fl) {
        this.fl = fl;
    }

    public boolean isToast() {
        return toast;
    }

    public void setToast(boolean toast) {
        this.toast = toast;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isPreSell() {
        return preSell;
    }

    public void setPreSell(boolean preSell) {
        this.preSell = preSell;
    }
}

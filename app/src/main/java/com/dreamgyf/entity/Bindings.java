package com.dreamgyf.entity;

public class Bindings {

    private long refreshTime;
    private long expiresIn;
    private boolean expired;
    private long userId;
    private String tokenJsonStr;
    private String url;
    private long bindingTime;
    private long id;
    private int type;

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTokenJsonStr() {
        return tokenJsonStr;
    }

    public void setTokenJsonStr(String tokenJsonStr) {
        this.tokenJsonStr = tokenJsonStr;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getBindingTime() {
        return bindingTime;
    }

    public void setBindingTime(long bindingTime) {
        this.bindingTime = bindingTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

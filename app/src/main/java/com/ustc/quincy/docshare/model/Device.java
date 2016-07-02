package com.ustc.quincy.docshare.model;

import java.io.Serializable;

/**
 * Author: Created by QuincyHu on 2016/7/1 0001 13:03.
 * Email:  zhihuqunxing@163.com
 */
public class Device implements Serializable {
    private String userName;
    private String deviceName;
    private String ipAddress;

    public String getUserName() {
        return userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}

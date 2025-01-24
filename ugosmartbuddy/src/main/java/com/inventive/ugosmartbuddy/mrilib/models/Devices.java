package com.inventive.ugosmartbuddy.mrilib.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Devices implements Serializable {
    private int part;
    private String serialNumber;
    private String highPwd;
    private String lowPwd;
    private String firwarePwd;
    private String authMode;
    private String authKey;
    private String cipheringMode;
    private String cipheringKey;
    private String systemTitle;
    private String meterType;
    private String manufacturer;
    private boolean miosFormat;
    private String ipAddress;
    private int port;
    private String ipVersion;
    private String startDate;
    private String endDate;
    private String deviceType;
    private Integer count;
    private Date billingCommDatetime;
    private String dailyLpStartDate;
    private String dailyLpEndDate;
    private String deltaLpStartDate;
    private String deltaLpEndDate;
    private List<String> obisCodeList;
    private List<String> prepayObisCodeList;
    private String obisCode;
    private String imageIdentifier;


    public String getImageIdentifier() {
        return imageIdentifier;
    }

    public void setImageIdentifier(String imageIdentifier) {
        this.imageIdentifier = imageIdentifier;
    }

    public String getFirwarePwd() {
        return firwarePwd;
    }

    public void setFirwarePwd(String firwarePwd) {
        this.firwarePwd = firwarePwd;
    }

    public String getObisCode() {
        return obisCode;
    }

    public void setObisCode(String obisCode) {
        this.obisCode = obisCode;
    }

    public Date getBillingCommDatetime() {
        return billingCommDatetime;
    }

    public void setBillingCommDatetime(Date billingCommDatetime) {
        this.billingCommDatetime = billingCommDatetime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getHighPwd() {
        return highPwd;
    }

    public void setHighPwd(String highPwd) {
        this.highPwd = highPwd;
    }

    public String getLowPwd() {
        return lowPwd;
    }

    public void setLowPwd(String lowPwd) {
        this.lowPwd = lowPwd;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getCipheringMode() {
        return cipheringMode;
    }

    public void setCipheringMode(String cipheringMode) {
        this.cipheringMode = cipheringMode;
    }

    public String getCipheringKey() {
        return cipheringKey;
    }

    public void setCipheringKey(String cipheringKey) {
        this.cipheringKey = cipheringKey;
    }

    public String getSystemTitle() {
        return systemTitle;
    }

    public void setSystemTitle(String systemTitle) {
        this.systemTitle = systemTitle;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public boolean isMiosFormat() {
        return miosFormat;
    }

    public void setMiosFormat(boolean miosFormat) {
        this.miosFormat = miosFormat;
    }

    public String getDailyLpStartDate() {
        return dailyLpStartDate;
    }

    public void setDailyLpStartDate(String dailyLpStartDate) {
        this.dailyLpStartDate = dailyLpStartDate;
    }

    public String getDailyLpEndDate() {
        return dailyLpEndDate;
    }

    public void setDailyLpEndDate(String dailyLpEndDate) {
        this.dailyLpEndDate = dailyLpEndDate;
    }

    public String getDeltaLpStartDate() {
        return deltaLpStartDate;
    }

    public void setDeltaLpStartDate(String deltaLpStartDate) {
        this.deltaLpStartDate = deltaLpStartDate;
    }

    public String getDeltaLpEndDate() {
        return deltaLpEndDate;
    }

    public void setDeltaLpEndDate(String deltaLpEndDate) {
        this.deltaLpEndDate = deltaLpEndDate;
    }

    public List<String> getObisCodeList() {
        return obisCodeList;
    }

    public void setObisCodeList(List<String> obisCodeList) {
        this.obisCodeList = obisCodeList;
    }

    public List<String> getPrepayObisCodeList() {
        return prepayObisCodeList;
    }

    public void setPrepayObisCodeList(List<String> prepayObisCodeList) {
        this.prepayObisCodeList = prepayObisCodeList;
    }
}


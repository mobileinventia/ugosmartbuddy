package com.inventive.ugosmartbuddy.mrilib.models;

public class Readings {
    private String METER_NO;
    private String GENERAL_READING;
    private String INSTANTENOUS_READING;
    private String BILLING_READING;
    private String LOADSURVEY_READING;
    private String LOADSURVEY_SCALER;
    private String TAMPER_READING;
    private String TIMESTAMP;
    private String KWH;
    private String KVAH;
    private String KW;
    private String RTC;
    private boolean ISDLMS;
    private String EXCEPTION_MESSAGE;

    public boolean getISDLMS() {
        return ISDLMS;
    }

    public void setISDLMS(boolean ISDLMS) {
        this.ISDLMS = ISDLMS;
    }

    public String getKWH() {
        return KWH;
    }

    public void setKWH(String KWH) {
        this.KWH = KWH;
    }

    public String getRTC() {
        return RTC;
    }

    public void setRTC(String RTC) {
        this.RTC = RTC;
    }

    public String getKVAH() {
        return KVAH;
    }

    public void setKVAH(String KVAH) {
        this.KVAH = KVAH;
    }

    public String getKW() {
        return KW;
    }

    public void setKW(String KW) {
        this.KW = KW;
    }

    public String getKVA() {
        return KVA;
    }

    public void setKVA(String KVA) {
        this.KVA = KVA;
    }

    private String KVA;

    public String getMETER_NO() {
        return METER_NO;
    }

    public void setMETER_NO(String METER_NO) {
        this.METER_NO = METER_NO;
    }

    public String getGENERAL_READING() {
        return GENERAL_READING;
    }

    public void setGENERAL_READING(String GENERAL_READING) {
        this.GENERAL_READING = GENERAL_READING;
    }

    public String getBILLING_READING() {
        return BILLING_READING;
    }

    public String getINSTANTENOUS_READING() {
        return INSTANTENOUS_READING;
    }

    public void setINSTANTENOUS_READING(String INSTANTENOUS_READING) {
        this.INSTANTENOUS_READING = INSTANTENOUS_READING;
    }

    public void setBILLING_READING(String BILLING_READING) {
        this.BILLING_READING = BILLING_READING;
    }

    public String getLOADSURVEY_READING() {
        return LOADSURVEY_READING;
    }

    public void setLOADSURVEY_READING(String LOADSURVEY_READING) {
        this.LOADSURVEY_READING = LOADSURVEY_READING;
    }

    public String getLOADSURVEY_SCALER() {
        return LOADSURVEY_SCALER;
    }

    public void setLOADSURVEY_SCALER(String LOADSURVEY_SCALER) {
        this.LOADSURVEY_SCALER = LOADSURVEY_SCALER;
    }

    public String getTAMPER_READING() {
        return TAMPER_READING;
    }

    public void setTAMPER_READING(String TAMPER_READING) {
        this.TAMPER_READING = TAMPER_READING;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getEXCEPTION_MESSAGE() {
        return EXCEPTION_MESSAGE;
    }

    public void setEXCEPTION_MESSAGE(String EXCEPTION_MESSAGE) {
        this.EXCEPTION_MESSAGE = EXCEPTION_MESSAGE;
    }
}

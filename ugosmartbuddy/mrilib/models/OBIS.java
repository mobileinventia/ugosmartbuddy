package com.inventive.ugosmartbuddy.mrilib.models;

public class OBIS {

    private String PROFILER_GROUP, PROFILER_CODE, OBIS_SCALER, OBIS_VALUE, OBJECT_TYPE;
    private boolean IS_ACTIVE;


    public String getPROFILER_GROUP() {
        return PROFILER_GROUP;
    }

    public void setPROFILER_GROUP(String PROFILER_GROUP) {
        this.PROFILER_GROUP = PROFILER_GROUP;
    }

    public String getPROFILER_CODE() {
        return PROFILER_CODE;
    }

    public void setPROFILER_CODE(String PROFILER_CODE) {
        this.PROFILER_CODE = PROFILER_CODE;
    }

    public String getOBIS_SCALER() {
        return OBIS_SCALER;
    }

    public void setOBIS_SCALER(String OBIS_SCALER) {
        this.OBIS_SCALER = OBIS_SCALER;
    }

    public String getOBIS_VALUE() {
        return OBIS_VALUE;
    }

    public void setOBIS_VALUE(String OBIS_VALUE) {
        this.OBIS_VALUE = OBIS_VALUE;
    }

    public String getOBJECT_TYPE() {
        return OBJECT_TYPE;
    }

    public void setOBJECT_TYPE(String OBJECT_TYPE) {
        this.OBJECT_TYPE = OBJECT_TYPE;
    }

    public boolean isIS_ACTIVE() {
        return IS_ACTIVE;
    }

    public boolean getIS_ACTIVE() {
        return IS_ACTIVE;
    }

    public void setIS_ACTIVE(boolean IS_ACTIVE) {
        this.IS_ACTIVE = IS_ACTIVE;
    }
}

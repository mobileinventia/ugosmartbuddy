package com.inventive.ugosmartbuddy.mrilib.models;

public class ErrorLog {
    private boolean IS_ERROR;

    private int ID;
    private String ERROR_MESSAGE;
    private String LOGIN_ID, ACCOUNTNO, ERROR, ERROR_TIME_MILLI, METER_MAKE, METER_TYPE, METER_NO;


    public boolean isError() {
        return IS_ERROR;
    }

    public void setError(boolean error) {
        IS_ERROR = error;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getErrorMessage() {
        return ERROR_MESSAGE;
    }

    public void setErrorMessage(String errorMessage) {
        ERROR_MESSAGE = errorMessage;
    }

    public String getLOGIN_ID() {
        return LOGIN_ID;
    }

    public void setLOGIN_ID(String LOGIN_ID) {
        this.LOGIN_ID = LOGIN_ID;
    }

    public String getAccountNo() {
        return ACCOUNTNO;
    }

    public void setAccountNo(String accountNo) {
        ACCOUNTNO = accountNo;
    }

    public String getError() {
        return ERROR;
    }

    public void setError(String error) {
        ERROR = error;
    }

    public String getErrorTimeMilli() {
        return ERROR_TIME_MILLI;
    }

    public void setErrorTimeMilli(String errorTimeMilli) {
        ERROR_TIME_MILLI = errorTimeMilli;
    }

    public String getMeterMake() {
        return METER_MAKE;
    }

    public void setMeterMake(String meterMake) {
        METER_MAKE = meterMake;
    }

    public String getMeterType() {
        return METER_TYPE ;
    }

    public void setMeterType(String meterType) {
        METER_TYPE  = meterType;
    }

    public String getMeterNo() {
        return METER_NO;
    }

    public void setMeterNo(String meterNo) {
        METER_NO = meterNo;
    }

    public String getACCOUNTNO() {
        return ACCOUNTNO;
    }

    public void setACCOUNTNO(String ACCOUNTNO) {
        this.ACCOUNTNO = ACCOUNTNO;
    }

    public String getERROR() {
        return ERROR;
    }

    public void setERROR(String ERROR) {
        this.ERROR = ERROR;
    }

    public String getERROR_TIME_MILLI() {
        return ERROR_TIME_MILLI;
    }

    public void setERROR_TIME_MILLI(String ERROR_TIME_MILLI) {
        this.ERROR_TIME_MILLI = ERROR_TIME_MILLI;
    }

    public String getMETER_MAKE() {
        return METER_MAKE;
    }

    public void setMETER_MAKE(String METER_MAKE) {
        this.METER_MAKE = METER_MAKE;
    }

    public String getMETER_TYPE() {
        return METER_TYPE;
    }

    public void setMETER_TYPE(String METER_TYPE) {
        this.METER_TYPE = METER_TYPE;
    }

    public String getMETER_NO() {
        return METER_NO;
    }

    public void setMETER_NO(String METER_NO) {
        this.METER_NO = METER_NO;
    }

    @Override
    public String toString() {
        return

                "USER_CODE='" + LOGIN_ID + '\'' +
                ", ACCOUNTNO='" + ACCOUNTNO + '\'' +
                        ", METER_NO='" + METER_NO + '\'' +
                ", ERROR_TIME_MILLI='" + ERROR_TIME_MILLI + '\'' +
                        ", ERROR='" + ERROR + '\'' +
                ", METER_MAKE='" + METER_MAKE + '\'' +
                ", METER_TYPE='" + METER_TYPE + '\''

                ;
    }
}

package com.inventive.ugosmartbuddy.mrilib.readData;

public class ObisCodeConstants {
    public static class EntityCode
    {
        public static String DEVICE_NO = "0.0.96.1.0.255";
        public static String SYSTEM_TITLE = "0.0.42.0.0.255";
    }

    public static class MasterObisCodes
    {
        public static String DELTA_LOAD_PROFILE = "1.0.99.1.0.255";
        public static String DAILY_LOAD_PROFILE = "1.0.99.2.0.255";
        public static String INSTAN_DATA_ATTRIBUTES = "1.0.94.91.3.255";
        public static String INSTAN_DATA = "1.0.94.91.0.255";
        public static String BILLING_DATA = "1.0.98.1.0.255";
        public static String NAME_PLATES_DATA = "0.0.94.91.10.255";
        public static String EVENTS_PROFILE_DATA = "0.0.99.98.e.255";
        public static String POWER_OUTAGES_EVENTS = "0.0.99.98.2.255";
        public static String CLOCK_DATA = "0.0.1.0.0.255";
        public static String PING_METER = "PING_METER";
        public static String EVENT_DATA = "0.0.99.98.1.255";
        public static String CONNECT_DISCONNECT_CONTROL = "0.0.96.3.10.255";
        public static String VOLTAGE_RELATED_EVENTS = "0.0.99.98.0.255";
        public static String CURRENT_RELATED_EVENTS = "0.0.99.98.1.255";
        public static String POWER_RELATED_EVENTS = "0.0.99.98.2.255";
        public static String TRANSACTION_RELATED_EVENTS = "0.0.99.98.3.255";
        public static String OTHER_RELATED_EVENTS = "0.0.99.98.4.255";
        public static String NON_ROLLOVER_RELATED_EVENTS = "0.0.99.98.5.255";
        public static String CONTROL_RELATED_EVENTS = "0.0.99.98.6.255";
        public static String Limiter = "0.0.17.0.0.255";
        public static String IMAGE_TRANSFER = "0.0.44.0.0.255";
        public static String IMAGE_TRANSFER_ACTIVATE = "0.0.15.0.2.255";
    }

    public static class RegisterObisCodes
    {
        public static String CUMULATIVEPOWERONDURATIONINMINUTES = "0.0.94.91.14.255";
        public static String ACTIVEPOWERKW = "1.0.1.7.0.255";
        public static String CUMULATIVEENERGYKWH = "1.0.1.8.0.255";
        public static String SIGNEDREACTIVEPOWERKVARLAGLEAD = "1.0.3.7.0.255";
        public static String APPARENTPOWERKVA = "1.0.9.7.0.255";
        public static String CUMULATIVEENERGYKVAH = "1.0.9.8.0.255";
        public static String PHASECURRENT = "1.0.11.7.0.255";
        public static String VOLTAGE = "1.0.12.7.0.255";
        public static String POWERFACTOR = "1.0.13.7.0.255";
        public static String FREQUENCYHZ = "1.0.14.7.0.255";
        public static String NEUTRALCURRENT = "1.0.91.7.0.255";
        public static String INTERVAL_DURATION = "1.0.0.8.4.255";
        public static String DEMAND_INTEGRATION_PERIOD = "1.0.0.8.0.255";
        public static String REGISTER_VOLTAGE = "1.0.32.7.0.255";

    }

    public static class DLMSDataObisCodes
    {
        public static String PUSHEVENTS = "0.0.94.91.18.255";
        public static String CLOCK = "0.0.1.0.0.255";
        public static String FWVersion = "1.0.0.2.0.255";
    }

    public static class Mode
    {
        public static String READ = "1";
        public static String WRITE = "2";
    }

    public static class WrittingCode
    {
        public static String DEMAND_INTEGRATION_PERIOD = "1.0.0.8.0.255";
        public static String PROFILE_CAPTURE_PERIOD = "1.0.0.8.4.255";
        public static String SINGLE_ACTION_SCHEDULING_BILLING_DATES = "0.0.15.0.0.255";
        public static String ACTIVITY_CALENDER_ZONE = "0.0.13.0.0.255";
    }

    public static class PrepayObisCode
    {
        public static String METERING_MODE = "0.0.94.96.19.255";
        public static String PAYMENT_MODE = "0.0.94.96.20.255";
        public static String LAST_TOKEN_RECHARGE_AMOUNT = "0.0.94.96.21.255";
        public static String LAST_TOKEN_RECHARGE_TIME = "0.0.94.96.22.255";
        public static String TOTAL_AMOUNT_AT_LAST_RECHARGE = "0.0.94.96.23.255";
        public static String CURRENT_BALANCE_AMOUNT = "0.0.94.96.24.255";
        public static String CURRENT_BALANCE_TIME = "0.0.94.96.25.255";
    }

    public static class ScriptTable
    {
        public static String COVER_OPEN = "0.0.10.3.111.255";
        public static String MD_RESET = "0.0.10.0.1.255";
    }

    public static class ActionSchedule
    {
        public static String BILLING_DATES = "0.0.15.0.0.255";
        public static String PUSH = "0.0.15.0.4.255";
    }

    public static class PushSetup
    {
        public static String INSTANT_PUSH = "0.0.25.9.0.255";
        public static String ALERT_PUSH = "0.4.25.9.0.255";
    }

    public static class SecuritySetup
    {
        public static String LLS = "0.0.43.0.2.255";
        public static String HLS = "0.0.43.0.3.255";
        public static String PUSH = "0.0.43.0.4.255";
        public static String FW_UPGRADE = "0.0.43.0.5.255";
    }

    public static class AssociationLogicalName
    {
        public static String PC = "0.0.40.0.1.255";
        public static String MR_LLS = "0.0.40.0.2.255";
        public static String US_HLS = "0.0.40.0.3.255";
        public static String PUSH_HLS = "0.0.40.0.4.255";
        public static String FW_UPGRADE_HLS = "0.0.40.0.5.255";
    }
}

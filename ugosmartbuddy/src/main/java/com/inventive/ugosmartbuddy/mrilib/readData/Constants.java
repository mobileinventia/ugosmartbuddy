package com.inventive.ugosmartbuddy.mrilib.readData;

public class Constants {
    public static class ManufacturerCodes
    {
        public static final String IHM_MANUFACTURER= "IHM";
        public static final String EVIT_MANUFACTURER= "EVIT";
        public static final String HEXING_MANUFACTURER= "HEXING";
        public static final String MSE_MANUFACTURER= "MSE";
        public static final String ZEN= "ZEN";
    }

    public static class Status{
        public static final String SUCCESS = "Success";
        public static final String FAILURE = "Failure";
        public static final String IN_PROGRESS = "IN_PROGRESS";
    }

    public static class IP
    {
        public static final String DEFAULT_IPV4_ADDRESS= "10.9.95.99";
        public static final String DEFAULT_IPV6_ADDRESS= "fc00:10:9:95::83";
        public static final String DEFAULT_IPV4_CONFIG= "IPV4";
        public static final String DEFAULT_IPV6_CONFIG= "IPV6";
        public static final String DEFAULT_PORT= "5000";


    }

    public static class MiosType
    {
        public static final String MIOS_EVI= "MIOS_OBIS_EVI.properties";
        public static final String MIOS_IHM= "MIOS_OBIS_IHM.properties";
        public static final String MIOS_INISH= "MIOS_OBIS_INISH.properties";
    }

    public static class MasterObisCodes
    {
        public static String LOAD_PROFILE_DATA1 = "1.0.99.1.0.255";
        public static String LOAD_PROFILE_DATA2 = "1.0.99.2.0.255";
        public static String INSTAN_DATA_ATTRIBUTES = "1.0.94.91.3.255";
        public static String INSTAN_DATA = "1.0.94.91.0.255";
        public static String BILLING_DATA = "1.0.98.1.0.255";
        public static String NAME_PLATES_DATA = "0.0.94.91.10.255";
        public static String EVENTS_PROFILE_DATA = "0.0.99.98.e.255";
        public static String CLOCK_DATA = "0.0.1.0.0.255";
        public static String EVENT_DATA = "0.0.99.98.1.255";
    }

    public static class PrepayObisCode
    {
        public static String PAYMENT_MODE = "0.0.94.96.20.255";
        public static String LAST_RECHARGE_AMOUNT = "0.0.94.96.21.255";
        public static String LAST_RECHARGE_TIME = "0.0.94.96.22.255";
        public static String TOTAL_AMOUNT_AT_LAST_RECHARGE = "0.0.94.96.23.255";
        public static String CURRENT_BALANCE_AMOUNT = "0.0.94.96.24.255";
        public static String CURRENT_BALANCE_TIME = "0.0.94.96.25.255";
    }

    /**
     * used to identify the different mode for initializing the connection.
     * @author NITIN SETHI
     *
     */
    public static class InitializationMode
    {
        public static String NONE = "None";
        public static String LOW = "Low";
        public static String HIGH = "High";
    }

    /**
     * used to identify the Devices Status.
     * @author NITIN SETHI
     *
     */
    public static class DeviceStatus
    {
        public static String CONNECTED = "Connected";
        public static String DISCONNECTED = "Connected";
        public static String BUSY = "Busy";
        public static String NOTCOMMUNICATTING = "Connected";
        //public static String CONNECTION_NOT_AVAILABLE1 = "Connection Not Availabe";
        public static String CONNECTION_ABRUPTLY_CLOSED =  "CONNECTION ABRUPTLY CLOSED";
        public static String PINGING = "PINGING";
    }

    public static class CommandsStatus
    {
        public static String SUCCESS = "Success";
        public static String PARTIAL_SUCCESS = "Partial Succeeded";
    }

    /**
     * used to identify the DLMS Mode.
     * @author NITIN SETHI
     *
     */
    public static class DLMSMode
    {
        public static String PART1 = "1";
        public static String PART2_WITH_CIPHERING = "2";
        public static String PART2_NO_CIPERING = "3";

    }



    public final class AuthenticationConstant {

        public static final String SUCCESS = "Success";
        public static final String AUTHENTICATED = "Authenticated";
        public static final String SESSION_IS_EXPIRED = "Session Is Expired";
        public static final String KEY_IS_NOTVALID = "Key Is Not Valid";
        public static final String API_KEY = "apiKey";
        public static final String DATA = "data";


    }

    public static class MeterMake{
        public static final String HPL = "HPL";
    }
}

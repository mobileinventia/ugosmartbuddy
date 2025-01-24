package com.inventive.ugosmartbuddy.mrilib.mri;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.models.Devices;
import com.inventive.ugosmartbuddy.mrilib.readData.Authentication;
import com.inventive.ugosmartbuddy.mrilib.readData.Constants;
import com.inventive.ugosmartbuddy.mrilib.readData.GXCommunicate;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSData;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSReader;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSSecureClient;
import com.inventive.ugosmartbuddy.mrilib.readData.Security;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DisconnectionControl {

    private Devices devices;

    public boolean readData(String meterNo, GXSerial serial, boolean reconnect, int baudRate, int databit,
                            String stopBits, String parity, String authentication,
                            String password, int clientaddress,
                            int serveraddress, String interfaceType,
                            boolean logicalNameReferencing,
                            String authKey,String cipherKey,
                            String systemTittle,String authMode,
                            String manufacturer, String phase,
                            FragmentActivity mActivity) throws Exception {
        GXDLMSReader reader = null;
        GXCommunicate reader1 = null;

        boolean result = false;
        try {
            if (serial != null || WifiConnectivity.getIsWifi()) {
                Utils.openSerialPort(serial, baudRate, databit, stopBits, parity);
                if (WifiConnectivity.getIsWifi() || serial.isOpen()) {
//                        password = "0TMP4GSM01035820";
                        devices = new Devices();
                        devices.setAuthKey(authKey);
                        devices.setCipheringKey(cipherKey);
                        devices.setCipheringMode("YES");
                        devices.setSystemTitle(systemTittle);
                        devices.setAuthMode(authMode);
                        devices.setHighPwd(password);
                        devices.setManufacturer(manufacturer);
                        devices.setDeviceType(phase);

                        GXDLMSSecureClient client = new GXDLMSSecureClient();
                        client.setAuthentication(Authentication.valueOf(authentication));
                        client.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                        client.getCiphering().setSystemTitle(systemTittle.getBytes(StandardCharsets.US_ASCII));
                        client.getCiphering().setAuthenticationKey(authKey.getBytes(StandardCharsets.US_ASCII));
                        client.getCiphering().setBlockCipherKey(cipherKey.getBytes(StandardCharsets.US_ASCII));
                        Utils.setClient(client, serial, password, clientaddress, serveraddress, interfaceType, logicalNameReferencing);

                        reader1 = new GXCommunicate().getManufactureSettings(serial, "High", mActivity.getAssets(), mActivity);
                        result = readMeter(reconnect, reader1, serial,Constants.InitializationMode.HIGH, devices);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (WifiConnectivity.getIsWifi() || (serial != null && serial.isOpen())) {
                if (reader != null) {
                    try {
                        reader.close();
                        serial.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return result;
    }

    public ArrayList<String> verifyMeterNo(GXSerial serial, int baudRate, int databit,
                                           String stopBits, String parity, String authentication, String password, int clientaddress,
                                           int serveraddress, String interfaceType,
                                           boolean logicalNameReferencing,
                                           String authKey,String cipherKey,
                                           String systemTittle,String authMode,
                                           String manufacturer, String phase,
                                           FragmentActivity mActivity) throws Exception {
        GXCommunicate reader1 = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            if (serial != null || WifiConnectivity.getIsWifi()) {
                Utils.openSerialPort(serial, baudRate, databit, stopBits, parity);
                if (WifiConnectivity.getIsWifi() || serial.isOpen()) {


                        //password = "0TMP4GSM01035820";
                        devices = new Devices();
                        devices.setAuthKey(authKey);
                        devices.setCipheringKey(cipherKey);
                        devices.setCipheringMode("YES");
                        devices.setSystemTitle(systemTittle);
                        devices.setAuthMode(authMode);
                        devices.setHighPwd(password);
                        //devices.setLowPwd("lnt1");
                        devices.setManufacturer(manufacturer);
                        devices.setDeviceType(phase);

                        GXDLMSSecureClient client = new GXDLMSSecureClient();
                        client.setAuthentication(Authentication.valueOf(authentication));
                        client.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                        client.getCiphering().setSystemTitle(systemTittle.getBytes(StandardCharsets.US_ASCII));

                        client.getCiphering().setAuthenticationKey(authKey.getBytes(StandardCharsets.US_ASCII));
                        client.getCiphering().setBlockCipherKey(cipherKey.getBytes(StandardCharsets.US_ASCII));
                        Utils.setClient(client, serial, password, clientaddress, serveraddress, interfaceType, logicalNameReferencing);

                        reader1 = new GXCommunicate().getManufactureSettings(serial, "High", mActivity.getAssets(), mActivity);
                        reader1.initializeConnection(Constants.InitializationMode.HIGH, devices);
                        result = readMeter(reader1, serial);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (WifiConnectivity.getIsWifi() || (serial != null && serial.isOpen())) {
                if (reader1 != null) {
                    try {
                        reader1.close();
                        serial.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return result;
    }

    private boolean readMeter(boolean reconnect, GXCommunicate reader, GXSerial serial,String initialisationMode , Devices devices) throws Exception {
        boolean isConnected = false;

        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                // String rtc = Utils.dateStringWithTime(((GXDateTime) reader.read(new GXDLMSClock("0.0.1.0.0.255"), 2)).getLocalCalendar().getTime());
                // String serialNo = Read_Data_Type(reader, "0.0.96.1.0.255").trim();
                isConnected = reader.initializeConnection(initialisationMode,devices,reconnect);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (serial != null) {
                try {
                    reader.close();
                    serial.close();
                } catch (Exception e) {
                    StringWriter stackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTrace));
                }
            }
        }
        return isConnected;
    }

    private ArrayList<String> readMeter(GXCommunicate reader, GXSerial serial) throws Exception {

        ArrayList<String> result = new ArrayList<>();
        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                String serialNo = Read_Data_Type(reader, "0.0.96.1.0.255").trim();
                String deviceID = Read_Data_Type(reader, "0.0.96.1.0.255").trim();
                String manufactureName = Read_Data_Type(reader, "0.0.96.1.1.255").trim();
                String phaseType = Read_Data_Type(reader, "0.0.94.91.9.255").trim();
                result.add(0, serialNo);
                result.add(1, deviceID);
                result.add(2, manufactureName);
                result.add(3, phaseType);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (serial != null) {
                try {
                    reader.close();
                    serial.close();
                } catch (Exception e) {
                    StringWriter stackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTrace));
                }
            }
        }
        return result;
    }

    public String Read_Data_Type(GXCommunicate reader, String obis_value) {
        try {
            Object obj = reader.readObject(new GXDLMSData(obis_value), 2);
            Log.d(TAG, "Read_Data_Type: " + obj);
            return Utils.getValue(obj);
        } catch (Exception e) {
           e.getMessage();
            return null;
        }
    }

}

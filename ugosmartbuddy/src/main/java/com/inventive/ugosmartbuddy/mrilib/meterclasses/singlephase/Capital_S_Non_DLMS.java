package com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase;

import com.inventive.ugosmartbuddy.mrilib.common.Non_DLMS_Parameter;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

public class Capital_S_Non_DLMS {

    int Utility_Code;

    public Capital_S_Non_DLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;

    }

    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try {
           read(serial);
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (WifiConnectivity.getIsWifi())
                    if (WifiConnectivity.getConnectivity() != null)
                        WifiConnectivity.getConnectivity().DisconnectWifi();

            } catch (Exception e) {
                e.getMessage();
            }
        }
        return non_dlms_parameter;
    }

    String  Alldata;

    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }

    public void read(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_9600);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(true);
                p.setWaitTime(10000);
                p.setReply(null);
                byte[] first_packet = null;
                synchronized (serial.getSynchronous()) {
                    synchronized (this){
                    first_packet = new byte[]{0x76, 0x6D, 0x61, 0x6B, 0x65};
                    p.setCount(20);
                    serial.send(first_packet, null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                }
                    synchronized (this) {
                        p.setReply(null);
                        p.setCount(10);
                        first_packet = new byte[]{0x48, 0x32};
                        serial.send(first_packet, null);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                    }

                    synchronized (this) {
                        p.setReply(null);
                        p.setCount(2334);
                        first_packet = new byte[]{0x44, 0x41, 0x54, 0x41, 0x46,};
                        serial.send(first_packet, null);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                       GXCommon.bytesToHex(p.getReply());
                    }
                }

            }

        } catch (Exception e) {

            e.getMessage();

        }

    }
}

package com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase;

import com.inventive.ugosmartbuddy.mrilib.common.Non_DLMS_Parameter;
import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

public class Avon_SPhase_NonDLMS {

    private int Utility_Code;
    Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();

    public Avon_SPhase_NonDLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;
    }

    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        try {
            read(serial);
        } catch (Exception e) {
            e.getMessage();
        }
        return non_dlms_parameter;
    }


    private void read(GXSerial serial) {
        StringBuilder builder = new StringBuilder();
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_2400);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setCount(253);
                p.setWaitTime(5000);
                synchronized (serial.getSynchronous()) {
                    byte[] first_packet = {(byte) 0xAB, 0x00, 0x01, (byte) 0xDD};
                    synchronized (this) {
                        serial.send(first_packet, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    parseData(GXCommon.bytesToHex(p.getReply()));
                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
    }


    private Non_DLMS_Parameter parseData(String data) {
        try {
            if (data != null) {
                String[] rawData = data.split(" ");
                String meterNo = "" + Utils.hex2decimal(rawData[7] + rawData[8] + rawData[9]);
                String rtc = Utils.hex2decimal(rawData[12]) + "/" + Utils.hex2decimal(rawData[13]) + "/" + Utils.hex2decimal(rawData[14]) + " "
                        + Utils.hex2decimal(rawData[15]) + ":" + Utils.hex2decimal(rawData[16]) + ":" + Utils.hex2decimal(rawData[17]);
                String kWH = "" + Utils.hex2decimal(rawData[102] + rawData[103] + rawData[104] + rawData[105]);
                kWH = "" + Double.parseDouble(kWH) / 100;


                non_dlms_parameter.setMETER_NO(meterNo);
                non_dlms_parameter.setBILLING_DATE(rtc);
                non_dlms_parameter.setACTIVE_ENERGY(kWH);
            }

        } catch (Exception e) {
            e.getMessage();
        }
        return non_dlms_parameter;
    }
}

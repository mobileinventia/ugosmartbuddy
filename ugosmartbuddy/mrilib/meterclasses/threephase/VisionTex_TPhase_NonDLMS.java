package com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase;

import com.inventive.ugosmartbuddy.mrilib.common.Non_DLMS_Parameter;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

public class VisionTex_TPhase_NonDLMS {
    private int Utility_Code;
    public VisionTex_TPhase_NonDLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;
    }
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try {
            String response_data = read(serial);
        } catch (Exception e) {
            e.getMessage();
        }
        return non_dlms_parameter;
    }

    private String read(GXSerial serial) {
        StringBuilder builder = new StringBuilder();
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
                p.setCount(15);
                p.setWaitTime(3000);
                synchronized (serial.getSynchronous()) {
                   byte[] first_packet = {(byte)0xe4, (byte)0x59, (byte)0xf8 };
                    synchronized (this) {
                        serial.send(first_packet, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    builder.append(GXCommon.bytesToHex(p.getReply()));
                    p.setCount(8);
                    p.setReply(null);
                    byte[] second_packet = { (byte)0xe4, (byte)0x42, (byte)0xf8};
                    synchronized (this) {
                        serial.send(second_packet, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    builder.append("new" + GXCommon.bytesToHex(p.getReply()));
                    p.setCount(585);
                    p.setReply(null);
                    byte[] third_packet ={(byte)0xe4, (byte)0x44, (byte)0xf8};
                    synchronized (this) {
                        serial.send(third_packet, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    builder.append("new" + GXCommon.bytesToHex(p.getReply()));

                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
        return builder.toString();
    }

}

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

public class VisionTex_SPhase_NonDLMS {
    private int Utility_Code;
    public VisionTex_SPhase_NonDLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;
    }
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try {
            String response_data = read(serial);
            return parseRawData(response_data.replaceAll("new"," "),non_dlms_parameter);

        } catch (Exception e) {
            e.getMessage();
        }
        return non_dlms_parameter;
    }
    private String read(GXSerial serial) {
        StringBuilder builder = new StringBuilder();
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_4800);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(4800, 8, 0));
                    this.wait(1000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                }
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setCount(11);
                p.setWaitTime(3000);
                synchronized (serial.getSynchronous()) {
                    byte[] first_packet = {(byte) 0x3A, (byte) 0x08, (byte) 0x00, (byte) 0x7A, (byte) 0x00, (byte) 0x56, (byte) 0x49, (byte) 0x53, (byte) 0x49, (byte) 0x4F, (byte) 0x4E, (byte) 0x33, (byte) 0x36, (byte) 0x3D, (byte) 0xC2, (byte) 0x0D, (byte) 0x0A};
                    synchronized (this) {
                        serial.send(first_packet, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    builder.append(GXCommon.bytesToHex(p.getReply()));
                    p.setCount(53);
                    p.setReply(null);
                    byte[] second_packet = {(byte) 0x3A, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xFF, (byte) 0x00, (byte) 0x0D, (byte) 0x0A};
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
                    byte[] third_packet = {(byte) 0x3A, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0xFE, (byte) 0x01, (byte) 0x0D, (byte) 0x0A};
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
        finally {
            if(serial!=null){
                try {
                    serial.close();
                }catch (Exception e){
                    e.getMessage();
                }
            }
        }

        return builder.toString();
    }
    public Non_DLMS_Parameter parseRawData(String response, Non_DLMS_Parameter non_dlms_parameter) {
        StringBuilder stringBuilder= new StringBuilder();
        response = response.replaceAll(" ", "");
        String serailNumber = response.substring(40, 56);
        serailNumber=Utils.hexToAscii(serailNumber);
        String maxDemandResponse=response;
         response=response.split("0D0A")[1];
        int energyPacketIndex = 74;
        stringBuilder.append(response.substring(energyPacketIndex,energyPacketIndex+8));
        String KWH= ""+Utils.hex2decimal(response.substring(energyPacketIndex + 6, energyPacketIndex + 8) + response.substring(energyPacketIndex + 4, energyPacketIndex + 6) + response.substring(energyPacketIndex + 2, energyPacketIndex + 4) + response.substring(energyPacketIndex, energyPacketIndex + 2));
        KWH=""+Double.parseDouble(KWH)/100;
        /*String KVAH=""+Utils.hex2decimal(response.substring(energyPacketIndex + 14, energyPacketIndex + 16) + response.substring(energyPacketIndex + 12, energyPacketIndex + 14) + response.substring(energyPacketIndex + 10, energyPacketIndex + 12) + response.substring(energyPacketIndex + 8, energyPacketIndex + 10));
        KVAH=""+Double.parseDouble(KVAH)/100;*/

        energyPacketIndex= maxDemandResponse.indexOf("0D0A", maxDemandResponse.indexOf("0D0A") + 1)+22;
        String maximumDemandKW=""+Utils.hex2decimal(maxDemandResponse.substring(energyPacketIndex + 22, energyPacketIndex + 24) + maxDemandResponse.substring(energyPacketIndex + 20, energyPacketIndex + 22));
        maximumDemandKW=""+Double.parseDouble(maximumDemandKW)/100;

        non_dlms_parameter.setMETER_NO(serailNumber);
        non_dlms_parameter.setACTIVE_ENERGY(KWH);
       // non_dlms_parameter.setAPPARENT_ENERGY(KVAH);
        non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(maximumDemandKW);
        return non_dlms_parameter;
    }
    
}

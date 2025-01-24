package com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase;

import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

import java.util.Date;

public class Genus_IR_SPhase_Non_DLMS {
//<0x43> <Utility ID (4B)> <0x29> <Encoded Number (2B)> <0xff> <Checksum (1B)>

    private byte headerByte = 0x43;
    private byte[] utilityID = {0x4a, 0x56, 0x56, 0x4e};
    private byte fixedByte = 0x29;
    private byte[] Encoded_bytes = {0x5e, 0x5d};
    private byte LastByte = (byte) 0xff;


    public void read(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_2400);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(2400, 8, 0));
                    this.wait(1000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                }
            }

            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                synchronized (serial.getSynchronous()) {
                    p.setCount(194);
                    p.setWaitTime(1000);
                    p.setReply(null);
                    byte[] encodedNumber = EncodedNumber();
                    byte[] data = {headerByte, utilityID[0], utilityID[1], utilityID[2], utilityID[4], encodedNumber[0], encodedNumber[1], LastByte};
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                }


            }

        } catch (Exception e) {
            e.getMessage();
        }
    }


    public byte[] EncodedNumber() {

        String day = String.valueOf(Utils.getDay(new Date()));
        String randomNumber = "0101011001111101"; // 0x567d
        randomNumber = new StringBuffer(randomNumber).reverse().toString();
        String binaryValue = Integer.toBinaryString(Integer.parseInt(day, 16));
        if (binaryValue.length() < 6) {
            int zeros=6-binaryValue.length();
            for(int i=0;i<zeros;i++){
                binaryValue = "0" + binaryValue;
            }
        }

        binaryValue = new StringBuffer(binaryValue).reverse().toString();
        char[] val = binaryValue.toCharArray();
        StringBuilder string = new StringBuilder(randomNumber);
        string.setCharAt(1, val[0]);
        string.setCharAt(3, val[1]);
        string.setCharAt(5, val[2]);
        string.setCharAt(7, val[3]);
        string.setCharAt(11, val[4]);
        string.setCharAt(13, val[5]);

        String hexString = appendZero(Integer.toHexString(Integer.parseInt(new StringBuffer(string).reverse().toString(), 2)));

        return new byte[]{(byte) Integer.parseInt(hexString.substring(0, 2)), (byte) Integer.parseInt(hexString.substring(2, 4))};


    }

    private String appendZero(String val) {
        if (val.length() < 4)
            val = "0" + val;

        return val;
    }


    private static String CalculateTwosCompliment(String bin) {

        String twos = null, ones = "";
        if(bin!=null) {
            for (int i = 0; i < bin.length(); i++) {
                ones += change(bin.charAt(i));
            }
            StringBuilder builder = new StringBuilder(ones);
            boolean b = false;
            for (int i = ones.length() - 1; i >= 0; i--) {
                if (ones.charAt(i) == '1') {
                    builder.setCharAt(i, '0');
                } else {
                    builder.setCharAt(i, '1');
                    b = true;
                    break;
                }
            }
            if (!b)
                builder.append("1", 0, 7);
            twos = builder.toString();
        }
        return twos;
    }

    private static char change(char c) {
        return (c == '0') ? '1' : '0';
    }

}


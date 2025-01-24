package com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase;


import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

public class LNT_EM101_96_SPhase_Non_DLMS {

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    String serialno;
    String Alldata;


    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }



    public void read(GXSerial serial, ReceiveParameters<byte[]> p) {
        try {
            serial.close();
            serial.setBaudRate(BaudRate.BAUD_RATE_9600);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi())
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(9600, 8, 0));
                this.wait(1000);
                WifiConnectivity.mReceiver.resetBytesReceived();
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                p.setAllData(false);
                p.setEop(null);
                p.setCount(1);
                p.setWaitTime(10000);
                byte data = 0x3A;
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                        serial.send(data, null);
                        this.wait(100);
                        serial.send(data, null);
                        this.wait(100);
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    p.setReply(null);
                    synchronized (this) {
                        byte[] first_packet={0x2F,0x3F,0x21,0x0D,0x0A};
                        p.setEop((byte)0x0A);
                        p.setCount(0);
                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setSerialno(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
                    }
                    p.setReply(null);
                    synchronized (this) {
                        byte[] second_packet={0x06, 0x30, 0x35, 0x33, 0x0D, 0x0A};
                        p.setEop(null);
                        serial.send(second_packet, null);
                        p.setReply(null);
                    }

                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
    }



    public String read_instantaneous(GXSerial serial,ReceiveParameters<byte[]> p){
        String instantaneous=null;
        try {
            synchronized (this) {
                p.setReply(null);
                byte eop = 0x21;
                p.setEop(eop);
                byte instant = 0x49;
                serial.send(instant, null);
                if (!serial.receive(p)) {
                    throw new Exception("Data Not Received");
                }
                instantaneous = (GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
            }

        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
        }
        return instantaneous;
    }

    public String read_billing(GXSerial serial,ReceiveParameters<byte[]> p){
        String billing=null;
        try {
            synchronized (this){
            p.setReply(null);
            byte eop = 0x21;
            p.setEop(eop);
            byte current_billing = 0x50;
            serial.send(current_billing, null);
            this.wait(100);
            if (!serial.receive(p)) {
                throw new Exception("Data Not Received");
            }
            billing = (GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
        }

        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
        }
        return billing;
    }

    public String serialno(){
        String s_no= getSerialno();
        s_no=s_no.replaceAll(" ","");
        s_no= hexToAscii(s_no);
        s_no=s_no.substring(5);
        return s_no;
    }

    public  String[] required_data(){
        String[] perfect_data = null;
        String str = getAlldata();
        if(str!=null) {
            String all_data = hexToAscii(getAlldata().replaceAll(" ", "").substring(0, 12000));
            all_data = all_data.substring(0, all_data.lastIndexOf("U")).replaceAll("\\(", "");
            String[] data_array = all_data.split("\\)");
            String manufacture_name = data_array[0].replace("H", "").trim();
            String reading_date_time = data_array[2].replace("H", "").trim();
            String Instant_voltage = data_array[206].replace("U", "").trim();
            Double instant_voltage = Double.parseDouble(Instant_voltage) / 100;
            Instant_voltage = "" + instant_voltage;
            String Instant_current = data_array[207].replace("U", "").trim();
            Instant_current = "" + Double.parseDouble(Instant_current) / 100;
            String active_value_KWH = data_array[30].replace("II", "").trim();
            active_value_KWH = "" + Double.parseDouble(active_value_KWH) / 100;
            String Tod1 = data_array[94].replace("M", "").trim(); // this contain date and time, value
            String Tod2 = data_array[95].replace("M", "").trim();
            String Tod3 = data_array[96].replace("M", "").trim();
            String Tod4 = data_array[97].replace("M", "").trim();

            perfect_data = new String[]{manufacture_name, reading_date_time, Instant_voltage, active_value_KWH,
                    Tod1, Tod2, Tod3, Tod4, Instant_current};
            /*tod1_value,tod1_date,tod1_time,tod2_value,tod2_date,tod2_time,tod3_value,tod3_date,tod3_time,tod4_value,tod4_date,tod4_time */

            /*
             *  this method will return current month energy and previout month 4 tod values and among those 4 tod values, maximum value will be maximum demand for previous month.
             *  need to divide maximum demand value by 100 to get exact value.
             *
             * */
        }
        return perfect_data;
    }

    private static String hexToAscii(String hxStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hxStr.length(); i += 2) {
            String str = hxStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

}


package com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase;


import com.inventive.ugosmartbuddy.mrilib.common.Non_DLMS_Parameter;
import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

/**
 * Created by jeet on 12-Mar-18.
 */

public class Genus_TPhase_NonDLMS {

    int Utility_Code;

    public Genus_TPhase_NonDLMS(int Utility_Code){
        this.Utility_Code=Utility_Code;

    }
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial){
        Non_DLMS_Parameter non_dlms_parameter= new Non_DLMS_Parameter();
         read(serial);
        String[] instant_data = fixed_parameter();
        String[] data = billing_data_current_month();
        String[] historyData = billing_history_data().split("new");
        String[] value1 = historyData[0].split(",");
        String[] value2 = historyData[1].split(",");
        String date = Utils.correctDateFormatWithTime(instant_data[8]);
        non_dlms_parameter.setMETER_NO(instant_data[0]);
        non_dlms_parameter.setMANUFACTURE_NAME("GENUS POWER INFRASTRUCTURES LTD");
        // non_dlms_parameter.setYEAR_OF_MANUFACTURE();
        non_dlms_parameter.setCURRENT_R_PHASE(instant_data[2]);
        non_dlms_parameter.setCURRENT_Y_PHASE(instant_data[3]);
        non_dlms_parameter.setCURRENT_B_PHASE(instant_data[4]);

        non_dlms_parameter.setVOLTAGE_R_PHASE(instant_data[5]);
        non_dlms_parameter.setVOLTAGE_Y_PHASE(instant_data[6]);
        non_dlms_parameter.setVOLTAGE_B_PHASE(instant_data[7]);
        non_dlms_parameter.setBILLING_DATE(date);
        non_dlms_parameter.setACTIVE_ENERGY(data[0]);
        non_dlms_parameter.setAPPARENT_ENERGY(data[3]);
         non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY(value2[1]);
        non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(value1[2]);
        return  non_dlms_parameter;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    String serialno="", Alldata="";
    String meter_rtc="";
    String manufacture_name="";
    String instantaneous_parameter="";
    String current_date_energy_values="", current_date_apparent="";
    String billing_history="";

    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }

    public String getManufacture_name() {
        return manufacture_name;
    }

    public void setManufacture_name(String manufacture_name) {
        this.manufacture_name = manufacture_name;
    }

    public String getMeter_rtc() {
        return meter_rtc;
    }

    public void setMeter_rtc(String meter_rtc) {
        this.meter_rtc = meter_rtc;
    }

    public String getInstantaneous_parameter() {
        return instantaneous_parameter;
    }

    public void setInstantaneous_parameter(String instantaneous_parameter) {
        this.instantaneous_parameter = instantaneous_parameter;
    }

    public String getCurrent_date_energy_values() {
        return current_date_energy_values;
    }

    public void setCurrent_date_energy_values(String current_date_energy_values) {
        this.current_date_energy_values = current_date_energy_values;
    }

    public String getCurrent_date_apparent() {
        return current_date_apparent;
    }

    public void setCurrent_date_apparent(String current_date_apparent) {
        this.current_date_apparent = current_date_apparent;
    }

    public String getBilling_history() {
        return billing_history;
    }

    public void setBilling_history(String billing_history) {
        this.billing_history = billing_history;
    }

    String Tod1_packet_response, Tod2_packet_response, Tod3_packet_response, Tod4_packet_response;

    public String getTod1_packet_response() {
        return Tod1_packet_response;
    }

    public void setTod1_packet_response(String tod1_packet_response) {
        Tod1_packet_response = tod1_packet_response;
    }

    public String getTod2_packet_response() {
        return Tod2_packet_response;
    }

    public void setTod2_packet_response(String tod2_packet_response) {
        Tod2_packet_response = tod2_packet_response;
    }

    public String getTod3_packet_response() {
        return Tod3_packet_response;
    }

    public void setTod3_packet_response(String tod3_packet_response) {
        Tod3_packet_response = tod3_packet_response;
    }

    public String getTod4_packet_response() {
        return Tod4_packet_response;
    }

    public void setTod4_packet_response(String tod4_packet_response) {
        Tod4_packet_response = tod4_packet_response;
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
                p.setAllData(false);
                p.setEop(null);
                p.setCount(20);
                p.setWaitTime(10000);
                byte[] data = {0x01, 0x3f, 0x50, 0x56, 0x4e, 0x4c, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x84};
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setManufacture_name(GXCommon.bytesToHex(p.getReply()));


                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] connection_packet = {0x01, 0x42, 0x45, (byte) 0xEF, (byte) 0xEF, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x9f};
                        p.setCount(2);
                        serial.send(connection_packet, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    //  setAlldata(GXCommon.bytesToHex(p.getReply()));


                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] second_packet = {0x01, 0x72, 0x01, 0x01, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x91};
                        p.setCount(20);
                        serial.send(second_packet, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                    }
                    setSerialno(GXCommon.bytesToHex(p.getReply()));


                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] second_packet = {0x01, 0x72, 0x01, 0x03, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x8f};
                        p.setCount(20);
                        serial.send(second_packet, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                    }
                    setMeter_rtc(GXCommon.bytesToHex(p.getReply()));


                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] second_packet = {0x01, 0x72, 0x01, 0x04, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x8E};

                        p.setCount(20);
                        serial.send(second_packet, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                    }
                    setInstantaneous_parameter(GXCommon.bytesToHex(p.getReply()));

                }
                read_billing_data(serial);
                read_tods(serial);

            }


        } catch (Exception e) {
        }

    }

    public void read_billing_data(GXSerial serial) {
        try {
            serial.close();
            serial.open();
            serial.resetByteCounters();
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setReply(null);
                p.setWaitTime(10000);

                synchronized (serial.getSynchronous()) {

                    synchronized (this) {

                        byte[] current_date_energy = {0x01, 0x72, 0x01, 0x05, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x8D};
                        p.setCount(20);
                        serial.send(current_date_energy, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    setCurrent_date_energy_values(GXCommon.bytesToHex(p.getReply()));


                    // apparent energy will get in next packet and remaining all will get in first packet

                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] current_date_apparent_energy = {0x01, 0x72, 0x01, 0x06, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x8C};
                        p.setCount(20);
                        serial.send(current_date_apparent_energy, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setCurrent_date_apparent(GXCommon.bytesToHex(p.getReply()));


                    String apparent_and_demand = getCurrent_date_apparent();

                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] current_day_maximum_demand = {0x01, 0x72, 0x01, 0x12, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x80};
                        p.setCount(20);
                        serial.send(current_day_maximum_demand, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    apparent_and_demand = apparent_and_demand + GXCommon.bytesToHex(p.getReply());
                    setCurrent_date_apparent(apparent_and_demand);


                    // packet to get 12 month billing history

                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] billing_history_packet = {0x01, 0x72, 0x03, 0x00, 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0x0D};
                        p.setCount(2433);
                        serial.send(billing_history_packet, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setBilling_history(GXCommon.bytesToHex(p.getReply()));


                }
            }

        } catch (Exception e) {
        }
    }

    public void read_tods(GXSerial serial) {

        byte[] Tod1_packet = tod_packet_Formation((byte) 0x58, (byte) 0xB5);
        byte[] Tod2_packet = tod_packet_Formation((byte) 0x60, (byte) 0xAD);
        byte[] Tod3_packet = tod_packet_Formation((byte) 0x68, (byte) 0xA5);
        byte[] Tod4_packet = tod_packet_Formation((byte) 0x70, (byte) 0x9D);
        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setWaitTime(10000);
                p.setReply(null);
                p.setCount(2433);
                synchronized (serial.getSynchronous()) {
                    // this packet response contains only 3 month tod's including current_date.

                    serial.send(Tod1_packet, null);
                    this.wait(100);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    setTod1_packet_response(GXCommon.bytesToHex(p.getReply()));
                    p.setReply(null);
                    p.setCount(2433);
                    this.wait(100);
                    serial.send(Tod2_packet, null);
                    this.wait(100);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    setTod2_packet_response(GXCommon.bytesToHex(p.getReply()));
                    p.setReply(null);
                    p.setCount(2433);
                    this.wait(100);
                    serial.send(Tod3_packet, null);
                    this.wait(100);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    setTod3_packet_response(GXCommon.bytesToHex(p.getReply()));
                    p.setReply(null);
                    p.setCount(2433);
                    this.wait(100);
                    serial.send(Tod4_packet, null);
                    this.wait(100);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    setTod4_packet_response(GXCommon.bytesToHex(p.getReply()));
                }
            }

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public byte[] tod_packet_Formation(byte fourth, byte crc) {

        byte[] Tod_packet = {0x01, 0x72, 0x03, fourth, 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) crc};

        return Tod_packet;
    }

    private StringBuilder builder = new StringBuilder();

    String values = "";

    public String read_load_survey(GXSerial serial) {
        try {
            byte[] bytes = {0x00, 0x08, 0x10, 0x18, 0x20, 0x28, 0x30, 0x38, 0x40, 0x48, 0x50, 0x58, 0x60, 0x68, 0x70, 0x78, (byte) 0x80, (byte) 0x88, (byte) 0x90, (byte) 0x98,
                    (byte) 0xa0, (byte) 0xa8, (byte) 0xb0, (byte) 0xb8, (byte) 0xc0, (byte) 0xc8, (byte) 0xd0, (byte) 0xd8, (byte) 0xe0, (byte) 0xe8, (byte) 0xf0, (byte) 0xf8};
            byte[] crc1 = {0x0b, 0x03, (byte) 0xfb, (byte) 0xf3, (byte) 0xeb, (byte) 0xe3, (byte) 0xdb, (byte) 0xd3, (byte) 0xcb, (byte) 0xc3, (byte) 0xbb, (byte) 0xb3, (byte) 0xab, (byte) 0xa3, (byte) 0x9b, (byte) 0x93, (byte) 0x8b, (byte) 0x83, 0x7b, 0x73,
                    0x6b, 0x63, 0x5b, 0x53, 0x4b, 0x43, 0x3b, 0x33, 0x2b, 0x23, 0x1b, 0x13};
            byte[] crc2 = {0x0a, 0x02, (byte) 0xfa, (byte) 0xf2, (byte) 0xea, (byte) 0xe2, (byte) 0xda, (byte) 0xd2, (byte) 0xca, (byte) 0xc2, (byte) 0xba, (byte) 0xb2, (byte) 0xaa, (byte) 0xa2, (byte) 0x9a, (byte) 0x92, (byte) 0x8a, (byte) 0x82, 0x7a, 0x72,
                    0x6a, 0x62, 0x5a, 0x52, 0x4a, 0x42, 0x3a, 0x32, 0x2a, 0x22, 0x1a, 0x12};
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setWaitTime(1000);
                synchronized (serial.getSynchronous()) {
                    p.setReply(null);
                    synchronized (this) {
                        byte[] load_packet = null;

                        for (int i = 0; i < (bytes.length * 2); i++) {
                            p.setReply(null);
                            p.setCount(2433);
                            if (i < bytes.length)
                                load_packet = new byte[]{0x01, 0x72, (byte) 0x05, bytes[i], 0x00, 0x00, (byte) 0x80, (byte) 0xff, (byte) 0xff, crc1[i]};
                            if (i >= bytes.length)
                                load_packet = new byte[]{0x01, 0x72, (byte) 0x06, bytes[i - bytes.length], 0x00, 0x00, (byte) 0x80, (byte) 0xff, (byte) 0xff, crc2[i - bytes.length]};
                            serial.send(load_packet, null);
                            this.wait(100);
                            if (!serial.receive(p)) {
                                continue;
                                //throw new Exception("Invalid meter type.");

                            }
                            builder.append((GXCommon.bytesToHex(p.getReply())) + "new");
                            this.wait(100);
                        }
                    }

                }
            }
        } catch (Exception e) {

            e.getMessage();

        }

        String[] all_packet_data = builder.toString().split("new");
        builder = new StringBuilder();
        for (int i = 0; i < all_packet_data.length; i++) {

            builder.append(load_survey_parsed_values(all_packet_data[i]));
        }

        return builder.toString();
    }

    public String[] load_survey_parsing(String data) {
        data = data.replaceAll(" ", "");
        data = data.substring(2);
        String[] month_wise_data = new String[data.length() / 38];
        int k = 0, l = 38;
        for (int i = 0; i < data.length() / 38; i++) {

            String value = data.substring(k, l);
            k = k + 38;
            l = l + 38;
            month_wise_data[i] = value;

        }
        return month_wise_data;
    }

    public String load_survey_parsed_values(String hexvalues) {
        String[] data = load_survey_parsing(hexvalues);
        String date_and_time = "";
        String kwh = "", apparent = "", reactive_lag = "", reactive_lead = "", voltage_B = "", voltage_Y = "", voltage_R = "", current_B = "",
                current_Y = "", current_R = "";
        String values = "";
        for (int i = 0; i < data.length; i++) {
            date_and_time = data[i].substring(14, 16) + "/" + data[i].substring(12, 14) + "/" + data[i].substring(10, 12) + " " + data[i].substring(8, 10) + ":" + data[i].substring(6, 8);

            kwh = "" + Integer.parseInt(data[i].substring(16, 18), 16);
            kwh = "" + Double.parseDouble(kwh) / 10;
            apparent = "" + Integer.parseInt(data[i].substring(18, 20), 16);
            apparent = "" + Double.parseDouble(apparent) / 10;

            reactive_lag = "" + Integer.parseInt(data[i].substring(20, 22), 16);
            reactive_lag = "" + Double.parseDouble(reactive_lag) / 10;

            reactive_lead = "" + Integer.parseInt(data[i].substring(22, 24), 16);
            reactive_lead = "" + Double.parseDouble(reactive_lead) / 10;

            voltage_B = "" + Integer.parseInt(data[i].substring(24, 26), 16);
            voltage_B = "" + Double.parseDouble(voltage_B) * 2;

            voltage_Y = "" + Integer.parseInt(data[i].substring(26, 28), 16);
            voltage_Y = "" + Double.parseDouble(voltage_Y) * 2;

            voltage_R = "" + Integer.parseInt(data[i].substring(28, 30), 16);
            voltage_R = "" + Double.parseDouble(voltage_R) * 2;

            current_B = "" + Integer.parseInt(data[i].substring(30, 32), 16);
            current_B = "" + Double.parseDouble(current_B) / 2;

            current_Y = "" + Integer.parseInt(data[i].substring(32, 34), 16);
            current_Y = "" + Double.parseDouble(current_Y) / 2;

            current_R = "" + Integer.parseInt(data[i].substring(34, 36), 16);
            current_R = "" + Double.parseDouble(current_R) / 2;


//            values=values+date_and_time+","+kwh+","+apparent+","+reactive_lag+","+
//                    reactive_lead+","+voltage_R+","+voltage_Y+","+voltage_B+","+current_R+","+
//                    current_Y+","+current_B+",";
            values = values + date_and_time + "," + voltage_R + "," + voltage_Y + "," + voltage_B + "," + current_R + "," +
                    current_Y + "," + current_B + "," + kwh + "," + apparent + ",";

        }

        return values;
    }

    String tamper_data_packet_response;

    public String getTamper_data_packet_response() {
        return tamper_data_packet_response;
    }

    public void setTamper_data_packet_response(String tamper_data_packet_response) {
        this.tamper_data_packet_response = tamper_data_packet_response;
    }

    public String read_tamper_data(GXSerial serial) {

        StringBuilder tamperbuilder = new StringBuilder();
        byte[] bytes = {0x00, 0x18, 0x20, 0x28, 0x30};
        byte[] crc = {0x0c, (byte) 0xf4, (byte) 0xec, (byte) 0xe4, (byte) 0xdc};
        byte[] Tod4_packet = tod_packet_Formation((byte) 0x70, (byte) 0x9D);
        try {
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setWaitTime(1000);
                synchronized (serial.getSynchronous()) {
                    p.setReply(null);
                    synchronized (this) {
                        // some tamper data available in tod4 packet and rest of data will get from this packet.
                        byte[] tamper_packet = {0x01, 0x72, 0x03, 0x78, 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0x95};
                        p.setCount(2433);
                        serial.send(tamper_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTamper_data_packet_response(GXCommon.bytesToHex(p.getReply()));

                        p.setReply(null);
                        p.setCount(2433);
                        this.wait(100);
                        serial.send(Tod4_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setTod4_packet_response(GXCommon.bytesToHex(p.getReply()));


                        for (int i = 0; i < bytes.length; i++) {
                            p.setReply(null);
                            p.setCount(2433);

                            byte[] packet = {0x01, 0x72, 0x04, bytes[i], 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, crc[i]};
                            serial.send(packet, null);
                            this.wait(100);
                            if (!serial.receive(p)) {
                                //throw new Exception("Invalid meter type.");
                                continue;
                            }

                            tamperbuilder.append(GXCommon.bytesToHex(p.getReply()) + "new");
                        }


                        String j = tamperbuilder.toString();
                    }


                }
            }
        } catch (Exception e) {
            e.getMessage();
        }


        String[] all_packets = tamperbuilder.toString().split("new");

        tamperbuilder = new StringBuilder();
        for (int i = 0; i < all_packets.length; i++) {

            tamperbuilder.append(All_Tamper_parsed_values(all_packets[i]));

        }

        return tamperbuilder.toString();

        //  String[] power_related_events=tamper_data_parsed_values();

    }

    public String[] All_tamper_parsing(String data) {
        data = data.replaceAll(" ", "");
        data = data.substring(2);
        String[] month_wise_data = new String[data.length() / 76];
        int k = 0, l = 76;
        for (int i = 0; i < data.length() / 76; i++) {

            String value = data.substring(k, l);
            k = k + 76;
            l = l + 76;
            month_wise_data[i] = value;

        }
        return month_wise_data;
    }

    public String All_Tamper_parsed_values(String hexvalues) {
        String[] data = All_tamper_parsing(hexvalues);
        String date_and_time = "";
        String kwh = "", apparent = "", average_powerfactor = "", voltage_B = "", voltage_Y = "", voltage_R = "", current_B = "",
                current_Y = "", current_R = "";
        String values = "";
        for (int i = 0; i < data.length; i++) {
            String event_code = data[i].substring(6, 8);
            date_and_time = data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + "/" + data[i].substring(12, 14) + " " + data[i].substring(10, 12) + ":" + data[i].substring(8, 10);

            kwh = "" + Integer.parseInt(data[i].substring(18, 24), 16);
            kwh = "" + Double.parseDouble(kwh) / 10;
            apparent = "" + Integer.parseInt(data[i].substring(24, 30), 16);
            apparent = "" + Double.parseDouble(apparent) / 10;

            average_powerfactor = "" + Integer.parseInt(data[i].substring(30, 34), 16);
            average_powerfactor = "" + Double.parseDouble(average_powerfactor) / 1000;

            voltage_B = "" + Integer.parseInt(data[i].substring(64, 68), 16);
            voltage_B = "" + Double.parseDouble(voltage_B) / 10;

            voltage_Y = "" + Integer.parseInt(data[i].substring(60, 64), 16);
            voltage_Y = "" + Double.parseDouble(voltage_Y) / 10;

            voltage_R = "" + Integer.parseInt(data[i].substring(56, 60), 16);
            voltage_R = "" + Double.parseDouble(voltage_R) / 10;

            current_B = "" + Integer.parseInt(data[i].substring(52, 56), 16);
            current_B = "" + Double.parseDouble(current_B) / 100;

            current_Y = "" + Integer.parseInt(data[i].substring(48, 52), 16);
            current_Y = "" + Double.parseDouble(current_Y) / 100;

            current_R = "" + Integer.parseInt(data[i].substring(44, 48), 16);
            current_R = "" + Double.parseDouble(current_R) / 100;

            values = values + date_and_time + "," + event_code + "," + kwh + "," + apparent + "," +
                    voltage_R + "," + voltage_Y + "," + voltage_B + "," + current_R + "," +
                    current_Y + "," + current_B + "," + average_powerfactor + ",";
        }
        return values;
    }

    public String[] tamper_data_parsed_values() {

        // this meter contains only power related events.
        String tamper_data = "", tamper_data_from_tod_packet = "", tamper_data_from_tamper_packet = "";
        try {
            tamper_data = getTod4_packet_response().replaceAll(" ", "") + getTamper_data_packet_response().replaceAll(" ", "");
        } catch (Exception e) {
            e.getMessage();
        }
        try {
            tamper_data_from_tod_packet = tamper_data.substring(tamper_data.indexOf("027600"), tamper_data.indexOf("0277F0"));
        } catch (Exception e) {
            e.getMessage();
        }
        try {

            tamper_data_from_tamper_packet = tamper_data.substring(tamper_data.indexOf("027800"), tamper_data.indexOf("027FF0"));
        } catch (Exception e) {
            e.getMessage();
        }
        String[] data = {tamper_data_from_tod_packet, tamper_data_from_tamper_packet};

        String[] values = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            values[i] = tamper_data_parsing(month_wise_data_string(data[i]));
        }
        return values; // this method will return power on and power off date and time. and sequence will be power off, then power on both are seperated by comma
    }

    public String tamper_data_parsing(String[] data) {
        String power_events = "";
        for (int i = 0; i < data.length; i++) {

            String power_date_and_time = data[i].substring(14, 16) + "/" + data[i].substring(12, 14) + "/" + data[i].substring(10, 12) + " " + data[i].substring(8, 10) + ":" + data[i].substring(6, 8);

            power_events = power_events + power_date_and_time + ",";


        }
        return power_events;
    }

    public String[] fixed_parameter() {

        String manufacture_name = hexToAscii(getManufacture_name().replaceAll(" ", "").substring(8, 20));

        String serial_no = "" + hex2decimal(getSerialno().replaceAll(" ", "").substring(10, 16));

        String meter_rtc = getMeter_rtc().replaceAll(" ", "").substring(8, 20);

        meter_rtc = meter_rtc.substring(10, 12) + "/" + meter_rtc.substring(8, 10) + "/" + meter_rtc.substring(6, 8) + " " + meter_rtc.substring(4, 6) + ":" + meter_rtc.substring(2, 4) + ":" + meter_rtc.substring(0, 2);


        String instantaneous_parameters = getInstantaneous_parameter().replaceAll(" ", "").substring(8, 32);

        String voltage_B_phase = "" + hex2decimal(instantaneous_parameters.substring(0, 4));

        voltage_B_phase = "" + Double.parseDouble(voltage_B_phase) / 10;
        String voltage_Y_phase = "" + hex2decimal(instantaneous_parameters.substring(4, 8));
        voltage_Y_phase = "" + Double.parseDouble(voltage_Y_phase) / 10;
        String voltage_R_phase = "" + hex2decimal(instantaneous_parameters.substring(8, 12));
        voltage_R_phase = "" + Double.parseDouble(voltage_R_phase) / 10;

        //current need to divide...

        String current_B_phase = "" + hex2decimal(instantaneous_parameters.substring(12, 16));

        current_B_phase = "" + Double.parseDouble(current_B_phase) / 100;
        String current_Y_phase = "" + hex2decimal(instantaneous_parameters.substring(16, 20));
        current_Y_phase = "" + Double.parseDouble(current_Y_phase) / 100;

        String current_R_phase = "" + hex2decimal(instantaneous_parameters.substring(20, 24));

        current_R_phase = "" + Double.parseDouble(current_R_phase) / 100;

        String[] data = {serial_no, manufacture_name, current_R_phase, current_Y_phase, current_B_phase, voltage_R_phase, voltage_Y_phase, voltage_B_phase, meter_rtc};

        return data;
    }

    public String[] tod_month_wise() {
        String tod_first_packet = "", tod_6 = "", tod_7 = "", current_month_tod = "", tod_2 = "", tod_3 = "";
        String maximum_demand_tod_current = "", maximum_demand_tod2 = "", maximum_demand_tod3 = "", tod_second_packet = "";
        String tod_4 = "", tod_5 = "", maximum_demand_tod4 = "", maximum_demand_tod5 = "";
        String maximum_demand_tod6 = "", maximum_demand_tod7 = "", tod_third_packet = "", tod_8 = "", tod_9 = "", tod_10 = "", tod_11 = "";
        String maximum_demand_tod8 = "", maximum_demand_tod9 = "", maximum_demand_tod10 = "", maximum_demand_tod11 = "";
        String tod_fourth_packet = "", tod_12 = "", maximum_demand_tod12 = "", tod_13="", maximum_demand_tod13="";
        synchronized (this) {
            // one string contains 8 tod of values of that particular month. tod string contains 4 energy tod i.e. 32 values and maximum_demand_tod contains tod values of both demands i.e. 16 values.
            try {
                tod_first_packet = getTod1_packet_response().replaceAll(" ", "");
            } catch (Exception e) {
            }
            try {
                current_month_tod = tod_first_packet.substring(tod_first_packet.indexOf("025A10"), tod_first_packet.indexOf("025A90"));
            } catch (Exception e) {
            }
            try {
                tod_2 = tod_first_packet.substring(tod_first_packet.indexOf("025C10"), tod_first_packet.indexOf("025C90"));
            } catch (Exception e) {
            }
            try {
                tod_3 = tod_first_packet.substring(tod_first_packet.indexOf("025E10"), tod_first_packet.indexOf("025E90"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod_current = tod_first_packet.substring(tod_first_packet.indexOf("025B10"), tod_first_packet.indexOf("025B90"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod2 = tod_first_packet.substring(tod_first_packet.indexOf("025D10"), tod_first_packet.indexOf("025D90"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod3 = tod_first_packet.substring(tod_first_packet.indexOf("025F10"), tod_first_packet.indexOf("025F90"));
            } catch (Exception e) {
            }
            try {
                tod_second_packet = getTod2_packet_response().replaceAll(" ", "");
            } catch (Exception e) {
            }
            try {
                tod_4 = tod_second_packet.substring(tod_second_packet.indexOf("026010"), tod_second_packet.indexOf("026090"));
            } catch (Exception e) {
            }
            try {
                tod_5 = tod_second_packet.substring(tod_second_packet.indexOf("026210"), tod_second_packet.indexOf("026290"));
            } catch (Exception e) {
            }
            try {
                tod_6 = tod_second_packet.substring(tod_second_packet.indexOf("026410"), tod_second_packet.indexOf("026490"));
            } catch (Exception e) {
            }
            try {
                tod_7 = tod_second_packet.substring(tod_second_packet.indexOf("026610"), tod_second_packet.indexOf("026690"));
            } catch (Exception e) {
            }


            try {
                maximum_demand_tod4 = tod_second_packet.substring(tod_second_packet.indexOf("026110"), tod_second_packet.indexOf("026190"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod5 = tod_second_packet.substring(tod_second_packet.indexOf("026310"), tod_second_packet.indexOf("026390"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod6 = tod_second_packet.substring(tod_second_packet.indexOf("026510"), tod_second_packet.indexOf("026590"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod7 = tod_second_packet.substring(tod_second_packet.indexOf("026710"), tod_second_packet.indexOf("026790"));
            } catch (Exception e) {
            }


            try {
                tod_third_packet = getTod3_packet_response().replaceAll(" ", "");
            } catch (Exception e) {
            }
            try {
                tod_8 = tod_third_packet.substring(tod_third_packet.indexOf("026810"), tod_third_packet.indexOf("026890"));
            } catch (Exception e) {
            }
            try {
                tod_9 = tod_third_packet.substring(tod_third_packet.indexOf("026A10"), tod_third_packet.indexOf("026A90"));
            } catch (Exception e) {
            }
            try {
                tod_10 = tod_third_packet.substring(tod_third_packet.indexOf("026C10"), tod_third_packet.indexOf("026C90"));
            } catch (Exception e) {
            }
            try {
                tod_11 = tod_third_packet.substring(tod_third_packet.indexOf("026E10"), tod_third_packet.indexOf("026E90"));
            } catch (Exception e) {
            }


            try {
                maximum_demand_tod8 = tod_third_packet.substring(tod_third_packet.indexOf("026910"), tod_third_packet.indexOf("026990"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod9 = tod_third_packet.substring(tod_third_packet.indexOf("026B10"), tod_third_packet.indexOf("026B90"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod10 = tod_third_packet.substring(tod_third_packet.indexOf("026D10"), tod_third_packet.indexOf("026D90"));
            } catch (Exception e) {
            }
            try {
                maximum_demand_tod11 = tod_third_packet.substring(tod_third_packet.indexOf("026F10"), tod_third_packet.indexOf("026F90"));
            } catch (Exception e) {
            }


            try {
                tod_fourth_packet = getTod4_packet_response().replaceAll(" ", "");
            } catch (Exception e) {
            }
            try {
                tod_12 = tod_fourth_packet.substring(tod_fourth_packet.indexOf("027010"), tod_fourth_packet.indexOf("027090"));
            } catch (Exception e) {
            }

          try {
              tod_13 = tod_fourth_packet.substring(tod_fourth_packet.indexOf("027210"), tod_fourth_packet.indexOf("027290"));

          }catch (Exception e){

          }

          try {

                maximum_demand_tod12 = tod_fourth_packet.substring(tod_fourth_packet.indexOf("027110"), tod_fourth_packet.indexOf("027190"));
            } catch (Exception e) {
            }
        }
        try {
            maximum_demand_tod13 = tod_fourth_packet.substring(tod_fourth_packet.indexOf("027310"), tod_fourth_packet.indexOf("027390"));
        }catch (Exception e){}

        // need to check sequence according to billing month sequence. still pending because all billing tod are same.

        /* tod_13,maximum_demand_tod13*/
        return new String[]{current_month_tod, maximum_demand_tod_current, tod_2, maximum_demand_tod2, tod_3, maximum_demand_tod3,
                tod_4, maximum_demand_tod4, tod_5, maximum_demand_tod5, tod_6, maximum_demand_tod6, tod_7, maximum_demand_tod7,
                tod_8, maximum_demand_tod8, tod_9, maximum_demand_tod9, tod_10, maximum_demand_tod10, tod_11, maximum_demand_tod11,
                tod_12, maximum_demand_tod12,tod_13,maximum_demand_tod13};
        // TODO: 17-Mar-18  nedd to create another method containing loop to give one by one tod till 8. give one month string as parameter
    }

    public String[] tod_values() {

        // this method will return 8 tod seperated by , and sequence is kwh,kvh,lag and lead power. these four powers are seperated by :

        // at index[0] contains tod of power and index [1] contains tod of both maximum demands.and so on....

        String[] data = tod_month_wise();

        String[] values = new String[data.length];

        for (int i = 0; i < data.length; i++) {
            values[i] = tod_parsed_values(month_wise_data_string(data[i]), i);
        }
        return values;

    }

    public String tod_parsed_values(String[] data, int flag) {


        String tod = "";

        for (int i = 0; i < data.length; i++) {
            if (flag % 2 == 0) {
                String kwh_tod = "" + hex2decimal(data[i].substring(6, 12));
                kwh_tod = "" + Double.parseDouble(kwh_tod) / 10;
                String kvh_tod = "" + hex2decimal(data[i].substring(12, 18));
                kvh_tod = "" + Double.parseDouble(kvh_tod) / 10;
                String lag_tod = "" + hex2decimal(data[i].substring(18, 24));
                lag_tod = "" + Double.parseDouble(lag_tod) / 10;
                String lead_tod = "" + hex2decimal(data[i].substring(24, 30));
                lead_tod = "" + Double.parseDouble(lead_tod) / 10;
                tod = tod + kwh_tod + "," + kvh_tod + "," + lag_tod + "," + lead_tod + ",";
            } else

            {

                String max_demand_kwh_tod = "" + hex2decimal(data[i].substring(6, 10));
                max_demand_kwh_tod = "" + Double.parseDouble(max_demand_kwh_tod) / 1000;


                String max_demand_kwh_tod_date_time = "" + data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + " " + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);

                String max_demand_kvh_tod = "" + hex2decimal(data[i].substring(20, 24));
                max_demand_kvh_tod = "" + Double.parseDouble(max_demand_kvh_tod) / 1000;

                String max_demand_kvh_tod_date_time = "" + data[i].substring(32, 34) + "/" + data[i].substring(30, 32) + "/" + data[i].substring(28, 30) + " " + data[i].substring(26, 28) + ":" + data[i].substring(24, 26);


                tod = tod + max_demand_kwh_tod + "," + max_demand_kwh_tod_date_time + "," + max_demand_kvh_tod + "," + max_demand_kvh_tod_date_time + ",";

            }
        }

        return tod;

    }

    public String[] billing_data_current_month() {

        String current_month_energy = getCurrent_date_energy_values().replaceAll(" ", "").substring(8, 26);

        String active_kwh = "" + hex2decimal(current_month_energy.substring(0, 6));

        active_kwh = "" + Double.parseDouble(active_kwh) / 10;

        String reactive_lag = "" + hex2decimal(current_month_energy.substring(6, 12));

        reactive_lag = "" + Double.parseDouble(reactive_lag) / 10;


        String reactive_lead = "" + hex2decimal(current_month_energy.substring(12, 18));

        reactive_lead = "" + Double.parseDouble(reactive_lead) / 10;

        String current_apparent_and_demand = getCurrent_date_apparent().replace(" ", "");

        String apparent_kvh = "" + hex2decimal(current_apparent_and_demand.substring(8, 14));

        apparent_kvh = "" + Double.parseDouble(apparent_kvh) / 10;

        String maximum_demand_kwh = "" + hex2decimal(current_apparent_and_demand.substring(48, 52));

        maximum_demand_kwh = "" + Double.parseDouble(maximum_demand_kwh) / 1000;

        String maximum_demand_kwh_date = "" + current_apparent_and_demand.substring(60, 62) + "/" + current_apparent_and_demand.substring(58, 60) + "/" + current_apparent_and_demand.substring(56, 58) + " " + current_apparent_and_demand.substring(54, 56) + ":" + current_apparent_and_demand.substring(52, 54);

        String maximum_demand_kvh = "" + hex2decimal(current_apparent_and_demand.substring(62, 66));

        maximum_demand_kvh = "" + Double.parseDouble(maximum_demand_kvh) / 1000;

        String maximum_demand_kvh_date = "" + current_apparent_and_demand.substring(74, 76) + "/" + current_apparent_and_demand.substring(72, 74) + "/" + current_apparent_and_demand.substring(70, 72) + " " + current_apparent_and_demand.substring(68, 70) + ":" + current_apparent_and_demand.substring(66, 68);


        String[] data = {active_kwh, reactive_lag, reactive_lead, apparent_kvh, maximum_demand_kwh, maximum_demand_kwh_date, maximum_demand_kvh, maximum_demand_kvh_date};
        return data;
    }

    private String[] billing_history_all(String data) {
        String[] month_wise_data = null;
        if(data!=null) {
            data = data.replaceAll(" ", "");
            data = data.substring(2);
            month_wise_data = new String[data.length() / 38];
            int k = 0, l = 38;
            for (int i = 0; i < data.length() / 38; i++) {

                String value = data.substring(k, l);
                k = k + 38;
                l = l + 38;
                month_wise_data[i] = value;

            }
        }
        return month_wise_data;
    }


    public String billing_history_data() {

        String[] data = billing_history_all(getBilling_history());

        if(data!=null) {
            String billing_date;
            String maximum_demand_kw = "", maximum_demand_kw_Date_and_time = "", maximum_demand_kv, maximum_demand_kv_date;
            String active_energy_kw = "", apparent_energy, power_factor;


            String kw_values = "";
            String kv_values = "";
            for (int i = 0; i < 12; i++) {
                maximum_demand_kw = "" + hex2decimal(data[i].substring(6, 10));
                maximum_demand_kw = "" + Double.parseDouble(maximum_demand_kw) / 1000;
                maximum_demand_kw_Date_and_time = data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + " " + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);
                active_energy_kw = "" + hex2decimal(data[i].substring(20, 26));
                active_energy_kw = "" + Double.parseDouble(active_energy_kw) / 10;
                billing_date = data[i].substring(32, 34) + "/" + data[i].substring(30, 32) + "/" + data[i].substring(28, 30);
                kw_values = kw_values + billing_date + "," + active_energy_kw + "," + maximum_demand_kw + "," + maximum_demand_kw_Date_and_time + ",";

            }

            for (int i = 16; i < 28; i++) {
                maximum_demand_kv = "" + hex2decimal(data[i].substring(6, 10));
                maximum_demand_kv = "" + Double.parseDouble(maximum_demand_kv) / 1000;
                maximum_demand_kv_date = data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + " " + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);
                apparent_energy = "" + hex2decimal(data[i].substring(20, 26));
                apparent_energy = "" + Double.parseDouble(apparent_energy) / 10;
                power_factor = "" + Double.parseDouble("" + hex2decimal(data[i].substring(26, 30))) / 1000;
                kv_values = kv_values + apparent_energy + "," + maximum_demand_kv + "," + maximum_demand_kv_date + "," + power_factor + ",";

            }

            /* there is issue of sequence. kw_values and kv_values contain both contains 4 values for corresponding billing date.
             *
             * and lag and lead will get from lag lead function.
             * */
            return kw_values + "new" + kv_values;
        } else
            return null;

    }


    public String[] billing_history() {


        String billing_history = getBilling_history().replaceAll(" ", "");

        int index_kwh_start = billing_history.indexOf("020000");
        int index_kwh_end = billing_history.indexOf("0200D0");
        int index_kvh_start = billing_history.indexOf("020100");
        int index_kvh_end = billing_history.indexOf("0201D0");
        int index_lag_and_lead_start = billing_history.indexOf("020300");
        int index_lag_and_lead_end = billing_history.indexOf("0203D0");

        String active_kwh = billing_history.substring(index_kwh_start, index_kwh_end);

        String apparent_kvh = billing_history.substring(index_kvh_start, index_kvh_end);

        String lag_and_lead = billing_history.substring(index_lag_and_lead_start, index_lag_and_lead_end);

        String[] data = {active_kwh, apparent_kvh, lag_and_lead};
        return data;
    }

    public String[] billing_history_values() {

        String[] month_wise_data = month_wise_data_string(billing_history()[0]);

        String fifth_last_month_kwh = "" + hex2decimal(month_wise_data[0].substring(20, 26));
        fifth_last_month_kwh = "" + Double.parseDouble(fifth_last_month_kwh) / 10;
        String fifth_billing_date = month_wise_data[0].substring(32, 34) + "/" + month_wise_data[0].substring(30, 32) + "/" + month_wise_data[0].substring(28, 30);

        String fourth_last_month_kwh = "" + hex2decimal(month_wise_data[1].substring(20, 26));
        fourth_last_month_kwh = "" + Double.parseDouble(fourth_last_month_kwh) / 10;
        String fourth_billing_date = month_wise_data[1].substring(32, 34) + "/" + month_wise_data[1].substring(30, 32) + "/" + month_wise_data[1].substring(28, 30);

        String third_last_month_kwh = "" + hex2decimal(month_wise_data[2].substring(20, 26));
        third_last_month_kwh = "" + Double.parseDouble(third_last_month_kwh) / 10;
        String third_billing_date = month_wise_data[2].substring(32, 34) + "/" + month_wise_data[2].substring(30, 32) + "/" + month_wise_data[2].substring(28, 30);


        String second_last_month_kwh = "" + hex2decimal(month_wise_data[3].substring(20, 26));
        second_last_month_kwh = "" + Double.parseDouble(second_last_month_kwh) / 10;
        String second_billing_date = month_wise_data[3].substring(32, 34) + "/" + month_wise_data[3].substring(30, 32) + "/" + month_wise_data[3].substring(28, 30);


        String last_month_kwh = "" + hex2decimal(month_wise_data[4].substring(20, 26));
        last_month_kwh = "" + Double.parseDouble(last_month_kwh) / 10;
        String last_billing_date = month_wise_data[4].substring(32, 34) + "/" + month_wise_data[4].substring(30, 32) + "/" + month_wise_data[4].substring(28, 30);


        String eleventh_last_month_kwh = "" + hex2decimal(month_wise_data[7].substring(20, 26));
        eleventh_last_month_kwh = "" + Double.parseDouble(eleventh_last_month_kwh) / 10;
        String eleventh_billing_date = month_wise_data[7].substring(32, 34) + "/" + month_wise_data[7].substring(30, 32) + "/" + month_wise_data[7].substring(28, 30);


        String tenth_last_month_kwh = "" + hex2decimal(month_wise_data[8].substring(20, 26));
        tenth_last_month_kwh = "" + Double.parseDouble(tenth_last_month_kwh) / 10;
        String tenth_billing_date = month_wise_data[8].substring(32, 34) + "/" + month_wise_data[8].substring(30, 32) + "/" + month_wise_data[8].substring(28, 30);


        String ninth_last_month_kwh = "" + hex2decimal(month_wise_data[9].substring(20, 26));
        ninth_last_month_kwh = "" + Double.parseDouble(ninth_last_month_kwh) / 10;
        String ninth_billing_date = month_wise_data[9].substring(32, 34) + "/" + month_wise_data[9].substring(30, 32) + "/" + month_wise_data[9].substring(28, 30);


        String eighth_last_month_kwh = "" + hex2decimal(month_wise_data[10].substring(20, 26));
        eighth_last_month_kwh = "" + Double.parseDouble(eighth_last_month_kwh) / 10;
        String eighth_billing_date = month_wise_data[10].substring(32, 34) + "/" + month_wise_data[10].substring(30, 32) + "/" + month_wise_data[10].substring(28, 30);


        String seventh_last_month_kwh = "" + hex2decimal(month_wise_data[11].substring(20, 26));
        seventh_last_month_kwh = "" + Double.parseDouble(seventh_last_month_kwh) / 10;
        String seventh_billing_date = month_wise_data[11].substring(32, 34) + "/" + month_wise_data[11].substring(30, 32) + "/" + month_wise_data[11].substring(28, 30);


        String sixth_last_month_kwh = "" + hex2decimal(month_wise_data[12].substring(20, 26));
        sixth_last_month_kwh = "" + Double.parseDouble(sixth_last_month_kwh) / 10;
        String sixth_billing_date = month_wise_data[12].substring(32, 34) + "/" + month_wise_data[12].substring(30, 32) + "/" + month_wise_data[12].substring(28, 30);


        String[] data = {last_month_kwh, last_billing_date, second_last_month_kwh, second_billing_date, third_last_month_kwh, third_billing_date, fourth_last_month_kwh, fourth_billing_date, fifth_last_month_kwh, fifth_billing_date, sixth_last_month_kwh, sixth_billing_date, seventh_last_month_kwh, seventh_billing_date, eighth_last_month_kwh, eighth_billing_date, ninth_last_month_kwh, ninth_billing_date, tenth_last_month_kwh, tenth_billing_date, eleventh_last_month_kwh, eleventh_billing_date};

        return data;


    }

    public String[] apparent_month_wise() {

        String[] month_wise_data = month_wise_data_string(billing_history()[1]);


        String fifth_last_month_kvh = "" + hex2decimal(month_wise_data[0].substring(20, 26));
        fifth_last_month_kvh = "" + Double.parseDouble(fifth_last_month_kvh) / 10;

        String fourth_last_month_kvh = "" + hex2decimal(month_wise_data[1].substring(20, 26));
        fourth_last_month_kvh = "" + Double.parseDouble(fourth_last_month_kvh) / 10;

        String third_last_month_kvh = "" + hex2decimal(month_wise_data[2].substring(20, 26));
        third_last_month_kvh = "" + Double.parseDouble(third_last_month_kvh) / 10;


        String second_last_month_kvh = "" + hex2decimal(month_wise_data[3].substring(20, 26));
        second_last_month_kvh = "" + Double.parseDouble(second_last_month_kvh) / 10;


        String last_month_kvh = "" + hex2decimal(month_wise_data[4].substring(20, 26));
        last_month_kvh = "" + Double.parseDouble(last_month_kvh) / 10;


        String eleventh_last_month_kvh = "" + hex2decimal(month_wise_data[7].substring(20, 26));
        eleventh_last_month_kvh = "" + Double.parseDouble(eleventh_last_month_kvh) / 10;


        String tenth_last_month_kvh = "" + hex2decimal(month_wise_data[8].substring(20, 26));
        tenth_last_month_kvh = "" + Double.parseDouble(tenth_last_month_kvh) / 10;


        String ninth_last_month_kvh = "" + hex2decimal(month_wise_data[9].substring(20, 26));
        ninth_last_month_kvh = "" + Double.parseDouble(ninth_last_month_kvh) / 10;


        String eighth_last_month_kvh = "" + hex2decimal(month_wise_data[10].substring(20, 26));
        eighth_last_month_kvh = "" + Double.parseDouble(eighth_last_month_kvh) / 10;


        String seventh_last_month_kvh = "" + hex2decimal(month_wise_data[11].substring(20, 26));
        seventh_last_month_kvh = "" + Double.parseDouble(seventh_last_month_kvh) / 10;


        String sixth_last_month_kvh = "" + hex2decimal(month_wise_data[12].substring(20, 26));
        sixth_last_month_kvh = "" + Double.parseDouble(sixth_last_month_kvh) / 10;

        String[] data = {last_month_kvh, second_last_month_kvh, third_last_month_kvh, fourth_last_month_kvh, fifth_last_month_kvh, sixth_last_month_kvh, seventh_last_month_kvh, eighth_last_month_kvh, ninth_last_month_kvh, tenth_last_month_kvh, eleventh_last_month_kvh};

        return data;
    }


    public String[][] month_wise_lag_and_lead() {
        String[][] data = null;
        try {
            String[] month_wise_data = month_wise_data_string(billing_history()[2]);

            String fifth_last_month_lead = "" + hex2decimal(month_wise_data[0].substring(20, 26));
            fifth_last_month_lead = "" + Double.parseDouble(fifth_last_month_lead) / 10;

            String fourth_last_month_lead = "" + hex2decimal(month_wise_data[1].substring(20, 26));
            fourth_last_month_lead = "" + Double.parseDouble(fourth_last_month_lead) / 10;

            String third_last_month_lead = "" + hex2decimal(month_wise_data[2].substring(20, 26));
            third_last_month_lead = "" + Double.parseDouble(third_last_month_lead) / 10;

            String second_last_month_lead = "" + hex2decimal(month_wise_data[3].substring(20, 26));
            second_last_month_lead = "" + Double.parseDouble(second_last_month_lead) / 10;

            String last_month_lead = "" + hex2decimal(month_wise_data[4].substring(20, 26));
            last_month_lead = "" + Double.parseDouble(last_month_lead) / 10;

            String eleventh_last_month_lead = "" + hex2decimal(month_wise_data[5].substring(20, 26));
            eleventh_last_month_lead = "" + Double.parseDouble(eleventh_last_month_lead) / 10;

            String tenth_last_month_lead = "" + hex2decimal(month_wise_data[6].substring(20, 26));
            tenth_last_month_lead = "" + Double.parseDouble(tenth_last_month_lead) / 10;

            String ninth_last_month_lead = "" + hex2decimal(month_wise_data[9].substring(20, 26));
            ninth_last_month_lead = "" + Double.parseDouble(ninth_last_month_lead) / 10;

            String eighth_last_month_lead = "" + hex2decimal(month_wise_data[10].substring(20, 26));
            eighth_last_month_lead = "" + Double.parseDouble(eighth_last_month_lead) / 10;

            String seventh_last_month_lead = "" + hex2decimal(month_wise_data[11].substring(20, 26));
            seventh_last_month_lead = "" + Double.parseDouble(seventh_last_month_lead) / 10;

            String sixth_last_month_lead = "" + hex2decimal(month_wise_data[12].substring(20, 26));
            sixth_last_month_lead = "" + Double.parseDouble(sixth_last_month_lead) / 10;

            String fifth_last_month_lag = "" + hex2decimal(month_wise_data[0].substring(14, 20));
            fifth_last_month_lag = "" + Double.parseDouble(fifth_last_month_lag) / 10;

            String fourth_last_month_lag = "" + hex2decimal(month_wise_data[1].substring(14, 20));
            fourth_last_month_lag = "" + Double.parseDouble(fourth_last_month_lag) / 10;

            String third_last_month_lag = "" + hex2decimal(month_wise_data[2].substring(14, 20));
            third_last_month_lag = "" + Double.parseDouble(third_last_month_lag) / 10;

            String second_last_month_lag = "" + hex2decimal(month_wise_data[3].substring(14, 20));
            second_last_month_lag = "" + Double.parseDouble(second_last_month_lag) / 10;

            String last_month_lag = "" + hex2decimal(month_wise_data[4].substring(14, 20));
            last_month_lag = "" + Double.parseDouble(last_month_lag) / 10;

            String eleventh_last_month_lag = "" + hex2decimal(month_wise_data[7].substring(14, 20));
            eleventh_last_month_lag = "" + Double.parseDouble(eleventh_last_month_lag) / 10;

            String tenth_last_month_lag = "" + hex2decimal(month_wise_data[8].substring(14, 20));
            tenth_last_month_lag = "" + Double.parseDouble(tenth_last_month_lag) / 10;

            String ninth_last_month_lag = "" + hex2decimal(month_wise_data[9].substring(14, 20));
            ninth_last_month_lag = "" + Double.parseDouble(ninth_last_month_lag) / 10;

            String eighth_last_month_lag = "" + hex2decimal(month_wise_data[5].substring(14, 20));
            eighth_last_month_lag = "" + Double.parseDouble(eighth_last_month_lag) / 10;

            String seventh_last_month_lag = "" + hex2decimal(month_wise_data[6].substring(14, 20));
            seventh_last_month_lag = "" + Double.parseDouble(seventh_last_month_lag) / 10;

            String sixth_last_month_lag = "" + hex2decimal(month_wise_data[12].substring(14, 20));
            sixth_last_month_lag = "" + Double.parseDouble(sixth_last_month_lag) / 10;

            data = new String[][]{{last_month_lag, last_month_lead}, {second_last_month_lag, second_last_month_lead}, {third_last_month_lag, third_last_month_lead}, {fourth_last_month_lag, fourth_last_month_lead}, {fifth_last_month_lag, fifth_last_month_lead}, {sixth_last_month_lag, sixth_last_month_lead}, {seventh_last_month_lag, seventh_last_month_lead}, {eighth_last_month_lag, eighth_last_month_lead}, {ninth_last_month_lag, ninth_last_month_lead}, {tenth_last_month_lag, tenth_last_month_lead}, {eleventh_last_month_lag, eleventh_last_month_lead}};
        } catch (Exception e) {
            e.getMessage();
            //Utility.saveError(MyApplication.getInstance(), null, null,  Login_Helper.getLogin(MyApplication.getInstance().getBaseContext()), "" + e.getMessage());
        }

        return data;

    }

    public String[] month_wise_data_string(String data) {

        String[] month_wise_data = new String[data.length() / 38];
        int k = 0, l = 38;
        for (int i = 0; i < data.length() / 38; i++) {

            String value = data.substring(k, l);
            k = k + 38;
            l = l + 38;
            month_wise_data[i] = value;

        }

        return month_wise_data;
    }


    public String[][] month_wise_maximum_demand(String[] month_wise_data) {

        String fifth_last_month = "" + hex2decimal(month_wise_data[0].substring(6, 10));
        fifth_last_month = "" + Double.parseDouble(fifth_last_month) / 1000;
        String fifth_demand_date = month_wise_data[0].substring(18, 20) + "/" + month_wise_data[0].substring(16, 18) + "/" + month_wise_data[0].substring(14, 16) + " " + month_wise_data[0].substring(12, 14) + ":" + month_wise_data[0].substring(10, 12);

        String fourth_last_month = "" + hex2decimal(month_wise_data[1].substring(6, 10));
        fourth_last_month = "" + Double.parseDouble(fourth_last_month) / 1000;
        String fourth_demand_date = month_wise_data[1].substring(18, 20) + "/" + month_wise_data[1].substring(16, 18) + "/" + month_wise_data[1].substring(14, 16) + " " + month_wise_data[1].substring(12, 14) + ":" + month_wise_data[1].substring(10, 12);

        String third_last_month = "" + hex2decimal(month_wise_data[2].substring(6, 10));
        third_last_month = "" + Double.parseDouble(third_last_month) / 1000;
        String third_demand_date = month_wise_data[2].substring(18, 20) + "/" + month_wise_data[2].substring(16, 18) + "/" + month_wise_data[2].substring(14, 16) + " " + month_wise_data[2].substring(12, 14) + ":" + month_wise_data[2].substring(10, 12);


        String second_last_month = "" + hex2decimal(month_wise_data[3].substring(6, 10));
        second_last_month = "" + Double.parseDouble(second_last_month) / 1000;
        String second_demand_date = month_wise_data[3].substring(18, 20) + "/" + month_wise_data[3].substring(16, 18) + "/" + month_wise_data[3].substring(14, 16) + " " + month_wise_data[3].substring(12, 14) + ":" + month_wise_data[3].substring(10, 12);


        String last_month = "" + hex2decimal(month_wise_data[4].substring(6, 10));
        last_month = "" + Double.parseDouble(last_month) / 1000;
        String last_demand_date = month_wise_data[4].substring(18, 20) + "/" + month_wise_data[4].substring(16, 18) + "/" + month_wise_data[4].substring(14, 16) + " " + month_wise_data[4].substring(12, 14) + ":" + month_wise_data[4].substring(10, 12);


        String eleventh_last_month = "" + hex2decimal(month_wise_data[7].substring(6, 10));
        eleventh_last_month = "" + Double.parseDouble(eleventh_last_month) / 1000;
        String eleventh_demand_date = month_wise_data[7].substring(18, 20) + "/" + month_wise_data[7].substring(16, 18) + "/" + month_wise_data[7].substring(14, 16) + " " + month_wise_data[7].substring(12, 14) + ":" + month_wise_data[7].substring(10, 12);


        String tenth_last_month = "" + hex2decimal(month_wise_data[8].substring(6, 10));
        tenth_last_month = "" + Double.parseDouble(tenth_last_month) / 1000;
        String tenth_demand_date = month_wise_data[8].substring(18, 20) + "/" + month_wise_data[8].substring(16, 18) + "/" + month_wise_data[8].substring(14, 16) + " " + month_wise_data[8].substring(12, 14) + ":" + month_wise_data[8].substring(10, 12);


        String ninth_last_month = "" + hex2decimal(month_wise_data[9].substring(6, 10));
        ninth_last_month = "" + Double.parseDouble(ninth_last_month) / 1000;
        String ninth_demand_date = month_wise_data[9].substring(18, 20) + "/" + month_wise_data[9].substring(16, 18) + "/" + month_wise_data[9].substring(14, 16) + " " + month_wise_data[9].substring(12, 14) + ":" + month_wise_data[9].substring(10, 12);


        String eighth_last_month = "" + hex2decimal(month_wise_data[10].substring(6, 10));
        eighth_last_month = "" + Double.parseDouble(eighth_last_month) / 1000;
        String eighth_demand_date = month_wise_data[10].substring(18, 20) + "/" + month_wise_data[10].substring(16, 18) + "/" + month_wise_data[10].substring(14, 16) + " " + month_wise_data[10].substring(12, 14) + ":" + month_wise_data[10].substring(10, 12);


        String seventh_last_month = "" + hex2decimal(month_wise_data[11].substring(6, 10));
        seventh_last_month = "" + Double.parseDouble(seventh_last_month) / 1000;
        String seventh_demand_date = month_wise_data[11].substring(18, 20) + "/" + month_wise_data[11].substring(16, 18) + "/" + month_wise_data[11].substring(14, 16) + " " + month_wise_data[11].substring(12, 14) + ":" + month_wise_data[11].substring(10, 12);


        String sixth_last_month = "" + hex2decimal(month_wise_data[12].substring(6, 10));
        sixth_last_month = "" + Double.parseDouble(sixth_last_month) / 1000;
        String sixth_demand_date = month_wise_data[12].substring(18, 20) + "/" + month_wise_data[12].substring(16, 18) + "/" + month_wise_data[12].substring(14, 16) + " " + month_wise_data[12].substring(12, 14) + ":" + month_wise_data[12].substring(10, 12);

        String[][] data = {{last_month, last_demand_date}, {second_last_month, second_demand_date}, {third_last_month, third_demand_date}, {fourth_last_month, fourth_demand_date}, {fifth_last_month, fifth_demand_date}, {sixth_last_month, sixth_demand_date}, {seventh_last_month, seventh_demand_date}, {eighth_last_month, eighth_demand_date}, {ninth_last_month, ninth_demand_date}, {tenth_last_month, tenth_demand_date}, {eleventh_last_month, eighth_demand_date}};

        return data;
    }


    public int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
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


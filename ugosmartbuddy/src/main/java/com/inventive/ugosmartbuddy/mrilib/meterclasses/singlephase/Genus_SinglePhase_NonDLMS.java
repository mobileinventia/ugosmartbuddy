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

import static com.inventive.ugosmartbuddy.mrilib.common.Utils.hexToAscii;

public class Genus_SinglePhase_NonDLMS {
    int Utility_Code;

    public Genus_SinglePhase_NonDLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;

    }

    public String getSerialno() {
        return serialno;
    }


    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try {
            String[] data = read(serial);
            String history_data = read_billing_history(serial);
            String[] history = history_data.split("date")[0].split(",");
            String rtc = data[2].substring(10, 12) + "/" + data[2].substring(8, 10) + "/" + data[2].substring(6, 8) + " "
                    + data[2].substring(4, 6) + ":" + data[2].substring(2, 4) + ":" + data[2].substring(0, 2);
            String date = Utils.correctDateFormatWithTime(rtc);
            non_dlms_parameter.setMETER_NO(data[1]);
            non_dlms_parameter.setMANUFACTURE_NAME("GENUS");
            // non_dlms_parameter.setYEAR_OF_MANUFACTURE();
            non_dlms_parameter.setCURRENT_R_PHASE(data[4]);
            non_dlms_parameter.setVOLTAGE_R_PHASE(data[3]);
            non_dlms_parameter.setBILLING_DATE(date);
            non_dlms_parameter.setACTIVE_ENERGY(data[5]);
            // non_dlms_parameter.setAPPARENT_ENERGY();
            // non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY();
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(history[37]);
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


    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    String serialno, Alldata;

    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }

    String[] instantaneous_data = new String[8];

    public String[] read(GXSerial serial) {
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
                p.setWaitTime(1000);
                p.setReply(null);
                byte dvnl = 0x44;
                byte pvnl = 0x50;
                byte[] first_packet = null;
                synchronized (serial.getSynchronous()) {
                    first_packet = new byte[]{0x01, 0x3f, dvnl, 0x56, 0x4e, 0x4c, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x90};
                    p.setCount(20);
                    serial.send(first_packet, null);

                    if (!serial.receive(p)) {
                        p.setReply(null);
                        p.setCount(20);
                        first_packet = new byte[]{0x01, 0x3f, pvnl, 0x56, 0x4e, 0x4c, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x84};
                        serial.send(first_packet, null);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                    }
                    instantaneous_data[0] = hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(4, 9);
                    String second = packet_formation(GXCommon.bytesToHex(p.getReply()));
                    p.setReply(null);
                    byte[] second_packet = {0x01, 0x42, 0x45, (byte) (Integer.parseInt(second.split(" ")[0], 16) & 0xFF), (byte) (Integer.parseInt(second.split(" ")[2], 16) & 0xFF), (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) (Integer.parseInt(second.split(" ")[4], 16) & 0xFF)};
                    p.setCount(1);
                    serial.send(second_packet, null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    p.setReply(null);
                    p.setCount(20);
                    serial.send(instantaneous_packet((byte) 0x01, (byte) 0x91), null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    instantaneous_data[1] = hex2decimal(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(10, 16));

                    p.setReply(null);
                    p.setCount(20);
                    serial.send(instantaneous_packet((byte) 0xc8, (byte) 0xca), null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    instantaneous_data[2] = GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(8, 20);


                    p.setReply(null);
                    p.setCount(20);
                    serial.send(instantaneous_packet((byte) 0xc9, (byte) 0xc9), null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    String current_voltage = GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "");
                    instantaneous_data[3] = String.valueOf(Integer.parseInt(current_voltage.substring(34, 38), 16));
                    instantaneous_data[3] = String.valueOf(Float.parseFloat(instantaneous_data[3]) / 100);
                    instantaneous_data[4] = String.valueOf(Integer.parseInt(current_voltage.substring(24, 28), 16));
                    instantaneous_data[4] = String.valueOf(Float.parseFloat(instantaneous_data[4]) / 1000);


                    p.setReply(null);
                    p.setCount(20);
                    serial.send(instantaneous_packet((byte) 0xcb, (byte) 0xc7), null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    instantaneous_data[5] = String.valueOf(Integer.parseInt(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(20, 26), 16));
                    instantaneous_data[5] = String.valueOf(Float.parseFloat(instantaneous_data[5]) / 10);

                    p.setReply(null);
                    p.setCount(20);
                    serial.send(instantaneous_packet((byte) 0xcd, (byte) 0xc5), null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    String max_Demand_date_time = GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "");
                    instantaneous_data[6] = String.valueOf(Integer.parseInt(max_Demand_date_time.substring(8, 12), 16));
                    instantaneous_data[6] = String.valueOf(Float.parseFloat(instantaneous_data[6]) / 1000);
                    instantaneous_data[7] = max_Demand_date_time.substring(12, 22);

                }

            }


        } catch (Exception e) {

            e.getMessage();

        }
        //read_billing_history(serial);

        //  load_survey(serial);

        //tamper_data(serial);
        return instantaneous_data;
    }

    public String read_billing_history(GXSerial serial) {
        StringBuilder billing_builder = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        byte[] bytes = {0x00, 0x08};
        byte[] crc = {0x0d, 0x05};
        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setWaitTime(1000);

                synchronized (this) {
                    for (int i = 0; i < bytes.length; i++) {
                        p.setReply(null);
                        byte[] billing_history_packet = {0x01, 0x72, 0x03, bytes[i], 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) crc[i]};
                        p.setCount(2433);
                        serial.send(billing_history_packet, null);
                        if (!serial.receive(p)) {
                            continue;
                            //  throw new Exception("Invalid meter type.");
                        }
                        billing_builder.append(GXCommon.bytesToHex(p.getReply()) + "new");
                        this.wait(100);
                    }
                }
                p.setReply(null);
                byte[] billing_history_packet = {0x01, 0x72, 0x03, 0x10, 0x00, 0x00, (byte) 0x60, (byte) 0xFF, (byte) 0xFF, (byte) 0x1d};
                p.setCount(1825);
                serial.send(billing_history_packet, null);
                if (!serial.receive(p)) {
                    throw new Exception("Invalid meter type.");
                }
                billing_builder.append(GXCommon.bytesToHex(p.getReply()));

                //tamper_data(serial);


                String[] all_packets = billing_builder.toString().split("new");


                //  for(int i=0;i<all_packets.length;i++){

                //  builder.append(billing_history_data(billing_history_all(all_packets[i]))+"new");
                builder.append(billing_history_data(billing_history_all(all_packets[0])));
                //  }


            }
        } catch (Exception e) {

            e.getMessage();
        }
        return builder.toString();
    }


    public String billing_history_data(String[] data) {


        String billing_date = "";

        String maximum_demand_kw = "", maximum_demand_kw_Date_and_time = "";
        String active_energy_kw = "", power_factor, reset_no;


        String kw_values = "";
        String kv_values = "";
        for (int i = 64; i <= 76; i++) {
            maximum_demand_kw = "" + hex2decimal(data[i].substring(6, 10));
            maximum_demand_kw = "" + Double.parseDouble(maximum_demand_kw) / 1000;
            maximum_demand_kw_Date_and_time = data[i].substring(20, 22) + "/" + data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + " " + data[i].substring(14, 16) + ":" + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);
            active_energy_kw = "" + hex2decimal(data[i].substring(22, 28));
            active_energy_kw = "" + Double.parseDouble(active_energy_kw) / 10;

            reset_no = "" + hex2decimal(data[i].substring(28, 30));

            power_factor = "" + Double.parseDouble("" + hex2decimal(data[i].substring(30, 34))) / 1000;

            //   kw_values=kw_values+reset_no+","+active_energy_kw+","+maximum_demand_kw+","+maximum_demand_kw_Date_and_time+","+ power_factor+",";
            kw_values = kw_values + active_energy_kw + "," + maximum_demand_kw + "," + maximum_demand_kw_Date_and_time + "," + power_factor + ",";


        }

        for (int i = 96; i <= 108; i++) {

            billing_date = billing_date + data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + ",";

        }


        return kw_values + "date" + billing_date;
    }


    public String[] billing_history_all(String data) {
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

    public String load_survey(GXSerial serial) {

        StringBuilder load_survey = new StringBuilder();

        byte[] bytes = {0x25, 0x2d, 0x35, 0x3d, 0x45, 0x4d, 0x55, 0x5d, 0x65, 0x6d, 0x75};

        byte[] crc = {(byte) 0xe8, (byte) 0xe0, (byte) 0xd8, (byte) 0xd0, (byte) 0xc8, (byte) 0xc0, (byte) 0xb8, (byte) 0xb0,
                (byte) 0xa8, (byte) 0xa0, (byte) 0x98};
        try {
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setWaitTime(1000);

                synchronized (serial.getSynchronous()) {


                    synchronized (this) {
                        for (int i = 0; i < bytes.length; i++) {
                            p.setReply(null);
                            byte[] billing_history_packet = {0x01, 0x72, 0x03, bytes[i], 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) crc[i]};
                            p.setCount(2433);
                            serial.send(billing_history_packet, null);
                            if (!serial.receive(p)) {
                                continue;
                                //  throw new Exception("Invalid meter type.");
                            }
                            load_survey.append(GXCommon.bytesToHex(p.getReply()) + "new");
                            this.wait(100);
                        }
                    }
                    synchronized (this) {
                        p.setReply(null);
                        byte[] billing_history_packet = {0x01, 0x72, 0x03, 0x7d, 0x00, 0x00, (byte) 0x30, (byte) 0xFF, (byte) 0xFF, (byte) 0xe0};
                        p.setCount(913);
                        serial.send(billing_history_packet, null);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        load_survey.append(GXCommon.bytesToHex(p.getReply()));
                    }


                    String[] load = load_survey.toString().split("new");


                    load_survey = new StringBuilder();

                    for (int i = 0; i < load.length; i++) {

                        load_survey.append(load_survey_parsed_values(load[i]));
                    }


                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return load_survey.toString();

    }

    public String load_survey_parsed_values(String hexvalues) {
        String[] data = billing_history_all(hexvalues);
        String date_and_time = "";
        String kwh = "";

        String date_and_time2 = "";
        String kwh2 = "";
        String values = "";
        for (int i = 0; i < data.length; i++) {
            date_and_time = data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + " " + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);
            kwh = "" + Integer.parseInt(data[i].substring(6, 10), 16);
            kwh = "" + Double.parseDouble(kwh) / 2;

            date_and_time2 = data[i].substring(34, 36) + "/" + data[i].substring(32, 34) + "/" + data[i].substring(30, 32) + " " + data[i].substring(28, 30) + ":" + data[i].substring(26, 28);
            kwh2 = "" + Integer.parseInt(data[i].substring(22, 26), 16);
            kwh2 = "" + Double.parseDouble(kwh2) / 2;

            values = values + date_and_time + "," + kwh + "," + date_and_time2 + "," + kwh2 + ",";
        }
        return values;
    }

    public String tamper_data(GXSerial serial) {
        StringBuilder builder = new StringBuilder();
        try {

            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setWaitTime(1000);
                synchronized (this) {
                    p.setReply(null);
                    byte[] billing_history_packet = {0x01, 0x72, 0x03, 0x1e, 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0xef};
                    p.setCount(2433);
                    serial.send(billing_history_packet, null);
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    builder.append(All_Tamper_parsed_values(GXCommon.bytesToHex(p.getReply())));

                }


            }
        } catch (Exception e) {
            e.getMessage();
        }

        return builder.toString();

    }

    public String All_Tamper_parsed_values(String hexvalues) {
        String[] data = billing_history_all(hexvalues);
        String date_and_time = "";
        String kwh = "", average_powerfactor = "", voltage = "",
                current = "";
        String values = "";
        for (int i = 16; i <= 47; i++) {
            // String event_code=data[i].substring(6,8);
            date_and_time = data[i].substring(14, 16) + "/" + data[i].substring(12, 14) + "/" + data[i].substring(10, 12) + " " + data[i].substring(8, 10) + ":" + data[i].substring(6, 8);

            kwh = "" + Integer.parseInt(data[i].substring(18, 22), 16);
            kwh = "" + Double.parseDouble(kwh) / 10;

            average_powerfactor = "" + Integer.parseInt(data[i].substring(22, 26), 16);
            average_powerfactor = "" + Double.parseDouble(average_powerfactor) / 1000;

            voltage = "" + Integer.parseInt(data[i].substring(26, 30), 16);
            voltage = "" + Double.parseDouble(voltage) / 100;


            current = "" + Integer.parseInt(data[i].substring(32, 36), 16);
            current = "" + Double.parseDouble(current) / 1000;

            // earth load is available then these value will be used.

            values = values + date_and_time + "," + kwh + "," + voltage + "," + current + "," + average_powerfactor + ",";
        }

        values = values + "neutral_disturbance";

        for (int i = 80; i <= 111; i++) {
            // String event_code=data[i].substring(6,8);
            date_and_time = data[i].substring(14, 16) + "/" + data[i].substring(12, 14) + "/" + data[i].substring(10, 12) + " " + data[i].substring(8, 10) + ":" + data[i].substring(6, 8);

            kwh = "" + Integer.parseInt(data[i].substring(18, 22), 16);
            kwh = "" + Double.parseDouble(kwh) / 10;


            average_powerfactor = "" + Integer.parseInt(data[i].substring(22, 26), 16);
            average_powerfactor = "" + Double.parseDouble(average_powerfactor) / 1000;

            voltage = "" + Integer.parseInt(data[i].substring(26, 30), 16);
            voltage = "" + Double.parseDouble(voltage) / 100;


            current = "" + Integer.parseInt(data[i].substring(32, 36), 16);
            current = "" + Double.parseDouble(current) / 1000;

            // neutral disturbance tamper occured then these values will be used.


            values = values + date_and_time + "," + kwh + "," + voltage + "," + current + "," + average_powerfactor + ",";
        }


        return values;
    }

    public byte[] instantaneous_packet(byte fourth, byte crc) {

        byte[] instantaneous = {0x01, 0x72, 0x01, (byte) fourth, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) crc};

        return instantaneous;
    }

    public String packet_formation(String data) {
        StringBuilder stringBuilder;
        String replaceAll = data.replaceAll(" ", "");
        replaceAll = replaceAll.split("45")[replaceAll.split("45").length - 1].substring(0, 8).replaceAll(" ", "");
        String substring = replaceAll.substring(0, 2);
        String substring2 = replaceAll.substring(2, 4);
        String substring3 = replaceAll.substring(4, 6);
        replaceAll = replaceAll.substring(6, 8);
        String addHex = addHex(replaceAll, substring3, substring2);
        if (addHex.length() > 2) {
            addHex = addHex.substring(1, 3);
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(addHex);
        stringBuilder2.append(",");
        stringBuilder2.append(substring3);

        addHex = addHexTwo(xorHex(addHex, substring3), substring);
        if (addHex.length() > 2) {
            addHex = addHex.substring(1, 3);
        }
        addHex = nibbleSwap(GXCommon.hexToBytes(addHex)).toUpperCase();
        substring = addHex(replaceAll, substring3, substring);
        if (substring.length() > 2) {
            substring = substring.substring(1, 3);
        }
        replaceAll = addHexTwo(xorHex(substring, replaceAll), substring2);
        if (replaceAll.length() > 2) {
            replaceAll = replaceAll.substring(1, 3);
        }
        replaceAll = nibbleSwap(GXCommon.hexToBytes(replaceAll));
        stringBuilder = new StringBuilder();
        stringBuilder.append("4245");
        stringBuilder.append(addHex);
        stringBuilder.append(replaceAll);
        stringBuilder.append("ffffffff");
        String calculate_checksum = calculate_checksum(stringBuilder.toString());
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("01 42 45 ");
        stringBuilder4.append(addHex);
        stringBuilder4.append(replaceAll);
        stringBuilder4.append("ff ff ff ff ");
        stringBuilder4.append(calculate_checksum);
        return addHex + " " + replaceAll + " " + calculate_checksum;

    }

    public String calculate_checksum(String str) {
        str = str.toUpperCase();
        String str2 = "0123456789ABCDEF";
        int i = 0;
        int i2 = 16;
        for (int i3 = 0; i3 < str.length(); i3++) {
            if (str.charAt(i3) != ' ') {
                int indexOf = str2.indexOf(str.charAt(i3));
                if (indexOf < 0) {
                    i = -1;
                    break;
                }
                i += indexOf * i2;
                i2 = i2 == 16 ? 1 : 16;
            }
        }
        if (i < 0) {
            return "Not a hex value";
        }
        if (i2 == 1) {
            return "Invalid hex number";
        }

        str = String.valueOf((((i & 255) ^ -1) + 1) & 255);
        char charAt = str2.charAt(Double.valueOf(Math.floor((double) ((Integer.parseInt(str)) / 16))).intValue());
        str = String.valueOf(str2.charAt(Integer.parseInt(str) % 16));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(charAt);
        stringBuilder.append("");
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    public String addHex(String str, String str2, String str3) {
        str = str.toUpperCase();
        str2 = str2.toUpperCase();
        str3 = str3.toUpperCase();
        if (!isHex(str)) {
            return "Enter a valid Hex value";
        }
        if (!isHex(str2)) {
            return "Enter a valid Hex value";
        }
        if (!isHex(str3)) {
            return "Enter a valid Hex value";
        }
        return findHexa((Integer.parseInt(str, 16) + Integer.parseInt(str2, 16)) + Integer.parseInt(str3, 16));
    }

    public String addHexTwo(String str, String str2) {
        str = str.toUpperCase();
        str2 = str2.toUpperCase();
        if (!isHex(str)) {
            return "Enter a valid Hex value";
        }
        if (isHex(str2)) {
            return findHexa(Integer.parseInt(str, 16) + Integer.parseInt(str2, 16));
        }
        return "Enter a valid Hex value";
    }

    private boolean isHex(String str) {
        String str2 = "0123456789ABCDEF";
        str = str.toUpperCase();
        for (int i = 0; i < str.length(); i++) {
            if (str2.indexOf(str.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    private String findHexa(int i) {
        char[] cArr = new char[]{'F', 'E', 'D', 'C', 'B', 'A'};
        String str = "";
        while (i > 0) {
            int i2 = i % 16;
            StringBuilder stringBuilder;
            if (i2 > 9) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(cArr[15 - i2]);
                stringBuilder.append(str);
                str = stringBuilder.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(i2);
                stringBuilder.append(str);
                str = stringBuilder.toString();
            }
            i /= 16;
        }
        return str;
    }

    public String xorHex(String str, String str2) {
        char[] cArr = new char[str.length()];
        for (int i = 0; i < cArr.length; i++) {
            cArr[i] = toHex(fromHex(str.charAt(i)) ^ fromHex(str2.charAt(i)));
        }
        return new String(cArr);
    }

    private int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        throw new IllegalArgumentException();
    }

    private char toHex(int i) {
        if (i >= 0) {
            if (i <= 15) {
                return "0123456789ABCDEF".charAt(i);
            }
        }
        throw new IllegalArgumentException();
    }

    public String nibbleSwap(byte[] bArr) {
        String str = "";
        for (byte b : bArr) {
            byte b2 = (byte) (((b >>> 4) & 15) | ((b << 4) & 240));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(String.format("%x ", new Object[]{Byte.valueOf(b2)}));
            str = stringBuilder.toString();
        }
        return str;
    }

    public String hex2decimal(String s) {

        int val = Integer.parseInt(s, 16);
         /*
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }*/
        return String.valueOf(val);
    }

}

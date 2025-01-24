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

public class Landis_gyr_Nondlms {

    int Utility_Code;

    public Landis_gyr_Nondlms(int Utility_Code){
        this.Utility_Code=Utility_Code;

    }
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial){
        Non_DLMS_Parameter non_dlms_parameter= new Non_DLMS_Parameter();
        try {
            read(serial);
            String[] instantParam = instant_values();
            String alldata[] =  energy_parsing();
            String Current_billing[] = alldata[0].split(";")[0].split(",");
            String History_billing[] = alldata[1].split(";")[1].split(",");
            if (instantParam != null) {
                if (instantParam.length > 3) {
                    non_dlms_parameter.setMETER_NO(instantParam[0]);
                    non_dlms_parameter.setMANUFACTURE_NAME(instantParam[1]);
                    non_dlms_parameter.setVOLTAGE_R_PHASE(instantParam[2]);
                    non_dlms_parameter.setCURRENT_R_PHASE(instantParam[3]);
                    non_dlms_parameter.setYEAR_OF_MANUFACTURE(instantParam[4]);

                }
                else{
                    non_dlms_parameter.setMETER_NO(instantParam[0]);
                    non_dlms_parameter.setMANUFACTURE_NAME(instantParam[1]);
                    non_dlms_parameter.setVOLTAGE_R_PHASE(instantParam[2]);
                }

            }

            String date = Utils.correctDateFormatWithTime(Current_billing[0]);
            non_dlms_parameter.setBILLING_DATE(date);
            non_dlms_parameter.setACTIVE_ENERGY(Current_billing[1]);
            // non_dlms_parameter.setAPPARENT_ENERGY();
            // non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY();
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(History_billing[1]);
        }catch (Exception e){
            e.getMessage();
        }
        return  non_dlms_parameter;
    }

    String serialno, Alldata;

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }

    public String read(GXSerial serial) {
        String s = null;
        // The return would be tamper data
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_300);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);

                synchronized (this) {
                    serial.open();
                }

                synchronized (this) {
                    if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(300,7,2));
                    this.wait(1000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                    }
                }

            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setWaitTime(10000);
                p.setReply(null);
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);
                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setSerialno(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")));
                        this.wait(100);
                        p.setReply(null);
                        synchronized (this) {
                            byte[] second_packet = {0x06, 0x30, 0x35, 0x30, 0x0D, 0x0A};
                            // p.setCount(9300);
                            byte eop = 0x21;
                            p.setEop(eop);
                            serial.send(second_packet, null);
                            this.wait(100);
                            serial.close();
                            this.wait(100);
                            if(WifiConnectivity.getIsWifi()){
                                WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(9600,7,2));
                                this.wait(1000);
                                WifiConnectivity.mReceiver.resetBytesReceived();
                            }else {
                                serial.setBaudRate(BaudRate.BAUD_RATE_9600);
                                this.wait(100);
                                serial.open();
                                this.wait(100);
                            }
                            if (!serial.receive(p)) {
                                throw new Exception("Invalid meter type.");
                            }
                            setAlldata(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")));


                           // s = tamper_data();

                            // energy_parsing();
                            //  max_demand_parsing();

                            //  tod_values();
                        }
                    }

                }


            }

        } catch (Exception e) {

                 e.getMessage();
        }
        return s;
    }


    public void read_load_survey(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_300);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);
            synchronized (this) {
                serial.open();
            }
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setWaitTime(10000);
                p.setReply(null);
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);

                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        //   hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
                    }

                   /* synchronized (this) {
                        byte[] first_packet = {0x01, 0x42, 0x30, 0x03, 0x71};
                      byte[] second_packet={0x01, 0x42, 0x30, 0x03, 0x71};
                              byte[] third_packet={0x01, 0x42, 0x30, 0x03, 0x71};

                        serial.send(first_packet, null);
                        serial.send(second_packet, null);
                        serial.send( third_packet, null);
                        this.wait(1000);

                       *//* if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }*//*
                        //   hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
                    }






                    synchronized (this){



                        serial.close();
                        this.wait(500);
                       // serial.setBaudRate(BaudRate.BAUD_RATE_9600);
                        this.wait(500);
                        serial.open();
                       // this.wait(10000);



                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);

                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                      //  hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));



                    }*/


                    p.setReply(null);
                    synchronized (this) {
                             /*this.wait(100);
                             serial.close();
                             this.wait(100);
                             serial.setBaudRate(BaudRate.BAUD_RATE_9600);
                             this.wait(100);
                             serial.open();
                             this.wait(100);*/
                        this.wait(500);
                        p.setCount(16);
                        p.setEop(null);
                        byte[] second_packet = {0x06, 0x63, 0x30, 0x36, 0x0D, 0x0A};
                        serial.send(second_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));


                    }
                    p.setReply(null);
                    synchronized (this) {
                        p.setCount(1);
                        byte[] raw_packet = {0x01, 0x50, 0x32, 0x02, 0x28, 0x30, 0x34, 0x41, 0x46, 0x34, 0x44, 0x37, 0x38, 0x29, 0x03, 0x1e};
                        serial.send(raw_packet, null);
                        //  this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
                    }

                    p.setReply(null);
                    synchronized (this) {
                        // this.wait(100);
                        byte[] raw1_packet = {0x01, 0x4c, 0x36, 0x30, 0x03, 0x49};
                        p.setCount(77);
                        serial.send(raw1_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));
                    }
                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        byte[] raw2_packet = {0x01, 0x4d, 0x32, 0x35, 0x03, 0x49};
                        p.setCount(7);
                        serial.send(raw2_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", ""));


                    }


                }


            }


        } catch (Exception e) {

            e.getMessage();
        }

    }


    public String power_factors_and_apparent_energy() {

        String apparent_energy = get_without_brackets(getAlldata().substring(getAlldata().indexOf("1.8.7"), getAlldata().indexOf("1.4.0"))).replace("kVAh", "");

        String current_powerfactor = get_without_brackets(getAlldata().substring(getAlldata().indexOf("13.9"), getAlldata().indexOf("1.7.0")));

        return apparent_energy + current_powerfactor + get_without_brackets(getAlldata().substring(getAlldata().indexOf("13.9.0.01"), getAlldata().indexOf("1.6.0.01")));


    }

    public String tod_values() {

        String current_month_tod = get_without_brackets(getAlldata().substring(getAlldata().indexOf("1.8.1"), getAlldata().indexOf("1.8.7"))).replaceAll("kWh", "");

        return get_without_brackets(getAlldata().substring(getAlldata().indexOf("1.8.1.01"), getAlldata().indexOf("13.9.0.01"))).replaceAll("kWh", "");
    }

    public String tamper_data() {

        String all_tamper_events = "";

        if (getAlldata() != null && !getAlldata().equals("")) {

            String total_reverse_current = getAlldata().substring(getAlldata().indexOf("C.50.1"), getAlldata().indexOf("C.50.1.01"));

            total_reverse_current = get_without_brackets(total_reverse_current).replace(",", "");


            // contain occurance and restoration date and time i.e. 2 date and 2 time
            String reverse_current_events = getAlldata().substring(getAlldata().indexOf("C.50.1.01"), getAlldata().indexOf("C.50.3"));

            reverse_current_events = get_without_brackets(reverse_current_events);


            String total_magnet_fraud_events = getAlldata().substring(getAlldata().indexOf("C.50.3"), getAlldata().indexOf("C.50.3.01"));

            total_magnet_fraud_events = get_without_brackets(total_magnet_fraud_events).replace(",", "");
            ;


            // contain occurance and restoration date and time i.e. 2 date and 2 time

            String magnet_fraud_events = getAlldata().substring(getAlldata().indexOf("C.50.3.01"), getAlldata().indexOf("C.50.4"));

            magnet_fraud_events = get_without_brackets(magnet_fraud_events);


            String total_number_cover_open_events = getAlldata().substring(getAlldata().indexOf("C.50.4"), getAlldata().indexOf("C.50.4.01"));

            total_number_cover_open_events = get_without_brackets(total_number_cover_open_events).replace(",", "");
            ;


//         contain date and time
            String cover_open_events = getAlldata().substring(getAlldata().indexOf("C.50.4.01"), getAlldata().indexOf("C.50.5"));


            cover_open_events = get_without_brackets(cover_open_events);


            String total_abnormal_voltage_events = getAlldata().substring(getAlldata().indexOf("C.50.5"), getAlldata().indexOf("C.50.5.01"));

            total_abnormal_voltage_events = get_without_brackets(total_abnormal_voltage_events).replace(",", "");
            ;


            // contain occurance and restoration date and time i.e. 2 date and 2 time

            String abnormal_voltage_events = getAlldata().substring(getAlldata().indexOf("C.50.5.01"), getAlldata().indexOf("C.50.7"));

            abnormal_voltage_events = get_without_brackets(abnormal_voltage_events);


            String total_single_wire_events = getAlldata().substring(getAlldata().indexOf("C.50.7"), getAlldata().indexOf("C.50.7.01"));

            total_single_wire_events = get_without_brackets(total_single_wire_events).replace(",", "");


            // contain occurance and restoration date and time i.e. 2 date and 2 time

            String single_wire_events = getAlldata().substring(getAlldata().indexOf("C.50.7.01"), getAlldata().indexOf("C.50.8"));

            single_wire_events = get_without_brackets(single_wire_events);


// only contain time and date.
            String transcation_log_events = getAlldata().substring(getAlldata().indexOf("C.50.09.01"), getAlldata().indexOf("C.51"));

            transcation_log_events = get_without_brackets(transcation_log_events);

            all_tamper_events = total_reverse_current + "czp" + reverse_current_events + "new" + total_magnet_fraud_events + "czp" +
                    magnet_fraud_events + "new" + total_number_cover_open_events + "czp" + cover_open_events + "new" +
                    total_abnormal_voltage_events + "czp" + abnormal_voltage_events + "new" + total_single_wire_events + "czp" +
                    single_wire_events + "new" + transcation_log_events;

        }


        return all_tamper_events;
    }


    public String get_without_brackets(String value) {

        if (value != null && value != "") {

            String[] data = value.split("\\)");
            value = "";
            for (int i = 0; i < data.length - 1; i++) {
                value = value + data[i].split("\\(")[1] + ",";
            }

        }

        return value;
    }

    public String[] instant_values() {
        String[] values = null;
        if (getSerialno() != null) {
            if (!getSerialno().contains("ZCE")) {

                String serial_no = getAlldata().substring(getAlldata().indexOf("C.1("), getAlldata().indexOf(")")).trim().replace("C.1(", "");

                String manufacture_name = getSerialno().substring(1, 3).trim();

                String voltage = getAlldata().substring(getAlldata().indexOf("12.7("), getAlldata().indexOf("V)")).trim().replace("12.7(", "");

                String current = getAlldata().substring(getAlldata().indexOf("11.7.1("), getAlldata().indexOf("11.7.2")).trim().replace("11.7.1(", "").replace("A)", "");

                String manufacture_year = getAlldata().substring(getAlldata().indexOf("0.9.3("), getAlldata().indexOf("0.9.1(")).split(";")[5].replace(")", "").trim();

                String rtc = getAlldata().substring(getAlldata().indexOf("0.9.1("), getAlldata().indexOf("12.7(")).replace(")", "").trim().replace("0.9.1(", "");


                values = new String[]{serial_no, manufacture_name, voltage, current, manufacture_year};

            } else {
                values = instant_values2();
            }
        }
        return values;
    }


    public String[] instant_values2() {
        String values[] = null;
        if (getAlldata() != null) {
            String serial_no = getAlldata().substring(getAlldata().indexOf("C.1("), getAlldata().indexOf(")")).trim().replace("C.1(", "");

            String manufacture_name = getSerialno().substring(1, 3).trim();

            String voltage = getAlldata().substring(getAlldata().indexOf("12.7("), getAlldata().indexOf("V)")).trim().replace("*", "").replace("12.7(", "");


            values = new String[]{serial_no, manufacture_name, voltage};
        }
        return values;
    }


    public String[] energy_parsing() {
        String[] data = new String[13];

        if (getSerialno()!=null && !getSerialno().contains("ZCE")) {
            String rtc = getAlldata().substring(getAlldata().indexOf("0.9.1("), getAlldata().indexOf("12.7(")).replace(")", "").replace("0.9.1(", "").trim().split(";")[1].replace("-", "/") + " " + getAlldata().substring(getAlldata().indexOf("0.9.1("), getAlldata().indexOf("12.7(")).replace(")", "").replace("0.9.1(", "").trim().split(";")[0];

            String current_month_active_energy = getAlldata().substring(getAlldata().indexOf("1.8.0("), getAlldata().indexOf("1.8.1(")).replace("kWh)", "").replace("1.8.0(", "").trim();

            String current_month_kvah_energy = getAlldata().substring(getAlldata().indexOf("1.8.7("), getAlldata().indexOf("kVAh)")).replace("1.8.7(", "").trim();

            String last_12_month_kwh_energy = getAlldata().substring(getAlldata().indexOf("1.8.0.01("), getAlldata().indexOf("1.8.0.13("));

            String[] values = last_12_month_kwh_energy.trim().split("\\)");
            for (int i = 0; i < values.length; i++) {

                if (values[i].contains("kWh") && i <= 9) {
                    String energy = values[i].split(";")[0].replace("1.8.0.0" + (i + 1) + "(", "").replace("kWh", "").trim();
                    String billing_date_and_time = values[i].split(";")[2].replace("-", "/") + " " + values[i].split(";")[1];
                    values[i] = billing_date_and_time + "," + energy;
                }
                if (values[i].contains("kWh") && i > 9) {

                    String energy = values[i].split(";")[0].replace("1.8.0." + (i + 1) + "(", "").replace("kWh", "").trim();
                    String billing_date_and_time = values[i].split(";")[2].replace("-", "/") + " " + values[i].split(";")[1];
                    values[i] = billing_date_and_time + "," + energy;

                }


            }


            data[0] = rtc + "," + current_month_active_energy;

            for (int i = 0; i < values.length; i++) {

                data[i + 1] = values[i] + ";" + max_demand_parsing()[i];

            }
            if (data.length >= 10) {
                data[10] = data[10].replace("1.8.0.10(", "");
            }


        } else {


            data = month_wise_data();

        }
        return data;


        /*
         * this method will return current month energy and billing history of 12 month. at 0 index there will be current month then last ....11 last month
         * */
    }


    public String[] max_demand_parsing() {

        String current_month_max_demand_kw = getAlldata().substring(getAlldata().indexOf("1.4.0("), getAlldata().indexOf("2.8.3(")).split(";")[0].replace("1.4.0(", "").replace("kW", "").trim();

        String current_month_max_demand_kw_date_and_time = getAlldata().substring(getAlldata().indexOf("1.4.0("), getAlldata().indexOf("2.8.3(")).split(";")[2].replace("-", "/").replace(")", "").trim() + " " + getAlldata().substring(getAlldata().indexOf("1.4.0("), getAlldata().indexOf("2.8.3(")).split(";")[1].trim();


        String last_12_month_kw_max_demand = getAlldata().substring(getAlldata().indexOf("1.6.0.01("), getAlldata().indexOf("C.8.0.01("));

        String[] values = last_12_month_kw_max_demand.trim().split("\\)");

        for (int i = 0; i < values.length; i++) {

            if (values[i].contains("kW") && i < 9) {
                String energy = values[i].split(";")[0].replace("1.6.0.0" + (i + 1) + "(", "").replace("kW", "").trim();
                String max_kw_date_and_time = values[i].split(";")[2].replace("-", "/") + " " + values[i].split(";")[1];
                values[i] = max_kw_date_and_time + "," + energy;
            }
            if (values[i].contains("kW") && i >= 9) {

                String energy = values[i].split(";")[0].replace("1.6.0." + (i + 1) + "(", "").replace("kW", "").trim();
                String max_kw_date_and_time = values[i].split(";")[2].replace("-", "/") + " " + values[i].split(";")[1];
                values[i] = max_kw_date_and_time + "," + energy;

            }

        }

        String[] data = new String[13];
        data[0] = current_month_max_demand_kw_date_and_time + "," + current_month_max_demand_kw;

        for (int i = 0; i < values.length; i++) {

            data[i + 1] = values[i];

        }


        return data;

        /*
         * this method will return current month maximumd demand and history of maximum demand. at 0 index there will be current month then last ....11 last month
         * */

    }


    public String[] month_wise_data() {


        String[] a = getAlldata().split("\\)");

        String current_month_active_energy = a[1].substring(a[1].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "") + ";" + " ";

        String current_month_demand_kw = a[5].substring(a[5].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");

        String current_month_values = current_month_active_energy + "" + current_month_demand_kw;


        // since we ara not getting date and time means rtc from meter. so i am not displaying date and time for current month. we can use device date and time.


        String last_month_active_energy = a[6].substring(a[6].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");

        String last_month_demand_kw = a[18].substring(a[18].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String last_month_values = last_month_active_energy + ";" + last_month_demand_kw;


        String second_month_active_energy = a[7].substring(a[7].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String second_month_demand_kw = a[19].substring(a[19].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String second_month_values = second_month_active_energy + ";" + second_month_demand_kw;


        String third_month_active_energy = a[8].substring(a[8].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String third_month_demand_kw = a[20].substring(a[20].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String third_month_values = third_month_active_energy + ";" + third_month_demand_kw;


        String fourth_month_active_energy = a[9].substring(a[9].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String fourth_month_demand_kw = a[21].substring(a[21].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String fourth_month_values = fourth_month_active_energy + ";" + fourth_month_demand_kw;


        String fifth_month_active_energy = a[10].substring(a[10].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String fifth_month_demand_kw = a[22].substring(a[22].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String fifth_month_values = fifth_month_active_energy + ";" + fifth_month_demand_kw;


        String sixth_month_active_energy = a[11].substring(a[11].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String sixth_month_demand_kw = a[23].substring(a[23].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String sixth_month_values = sixth_month_active_energy + ";" + sixth_month_demand_kw;


        String seventh_month_active_energy = a[12].substring(a[12].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String seventh_month_demand_kw = a[24].substring(a[24].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String seventh_month_values = seventh_month_active_energy + ";" + seventh_month_demand_kw;


        String eighth_month_active_energy = a[13].substring(a[13].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String eighth_month_demand_kw = a[25].substring(a[25].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String eighth_month_values = eighth_month_active_energy + ";" + eighth_month_demand_kw;


        String ninth_month_active_energy = a[14].substring(a[14].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String ninth_month_demand_kw = a[26].substring(a[26].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String ninth_month_values = ninth_month_active_energy + ";" + ninth_month_demand_kw;


        String tenth_month_active_energy = a[15].substring(a[15].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String tenth_month_demand_kw = a[27].substring(a[27].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String tenth_month_values = tenth_month_active_energy + ";" + tenth_month_demand_kw;


        String eleventh_month_active_energy = a[16].substring(a[16].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");
        String eleventh_month_demand_kw = a[28].substring(a[28].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String eleventh_month_values = eleventh_month_active_energy + ";" + eleventh_month_demand_kw;

        String twelveth_month_active_energy = a[17].substring(a[17].indexOf("(")).trim().replace("(", "").replace("kWh", "").replace("*", "");

        String twelveth_month_demand_kw = a[29].substring(a[29].indexOf("(")).trim().replace("(", "").replace("kW", "").replace("*", "");
        String twelveth_month_values = twelveth_month_active_energy + ";" + twelveth_month_demand_kw;


        String[] data = {current_month_values, last_month_values, second_month_values, third_month_values, fourth_month_values, fifth_month_values, sixth_month_values, seventh_month_values, eighth_month_values, ninth_month_values, tenth_month_values, eleventh_month_values, twelveth_month_values};


        return data;
    }


    public String[] month_values(String[] data, int index) {
        /**
         * will return 12 month energy , billing date , maximum demand, maximum demand date. you need to pass data[] from month_wise_data;
         */
        String[] values = data[index].split(";");
        values[3] = values[4].replaceAll("-", "/") + " " + values[3];
        return values;
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

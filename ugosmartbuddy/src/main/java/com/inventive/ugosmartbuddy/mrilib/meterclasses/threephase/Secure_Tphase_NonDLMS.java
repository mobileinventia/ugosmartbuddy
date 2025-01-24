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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Secure_Tphase_NonDLMS {
    int Utility_Code;

    public Secure_Tphase_NonDLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;

    }

    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try {
            read(serial);
            read_billing(serial);
            String[] billing = billing_history_energy_values();
            String[] current_billing_data = billing[0].split(",");
            String[] max_Demand_history_kw = max_demand_kw_tod_parsing()[1].split(",");
            String[] max_Demand_history_kv = max_demand_kv_tod_parsing()[1].split(",");
            String date = Utils.correctDateFormatWithTime(getRtc());
            non_dlms_parameter.setMETER_NO(getSerial_no());
            non_dlms_parameter.setMANUFACTURE_NAME("SECURE");
            //non_dlms_parameter.setYEAR_OF_MANUFACTURE();
            non_dlms_parameter.setCURRENT_R_PHASE(getCurrent_phase1());
            non_dlms_parameter.setCURRENT_Y_PHASE(getCurrent_phase2());
            non_dlms_parameter.setCURRENT_B_PHASE(getCurrent_phase3());
            non_dlms_parameter.setVOLTAGE_R_PHASE(getVoltage_phase1());
            non_dlms_parameter.setVOLTAGE_Y_PHASE(getVoltage_phase2());
            non_dlms_parameter.setVOLTAGE_B_PHASE(getVoltage_phase3());
            non_dlms_parameter.setBILLING_DATE(date);
            non_dlms_parameter.setACTIVE_ENERGY(current_billing_data[0]);
            non_dlms_parameter.setAPPARENT_ENERGY(current_billing_data[3]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY("" + Double.parseDouble(max_Demand_history_kv[0]) / 100);
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY("" + Double.parseDouble(max_Demand_history_kw[0]) / 100);
        } catch (Exception e) {
            e.getMessage();

        }finally {
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

    public String[] res_pckts_bill = new String[2000];
    String[] billing_and_tod_data;

    public String[] getBilling_and_tod_data() {
        return billing_and_tod_data;
    }

    public void setBilling_and_tod_data(String[] billing_and_tod_data) {
        this.billing_and_tod_data = billing_and_tod_data;
    }


    int[] param_count = {11, 10, 10, 10, 10, 10, 10, 9, 11, 11, 11, 10, 10, 10, 10, 10, 10, 10, 10, 10};

    public byte[][] commnds_arr = {{0x53, 0x0d}, {0x56, 0x31, 0x0d}, {0x56, 0x32, 0x0d}, {0x56, 0x33, 0x0d}, {0x49, 0x31, 0x0D}, {0x49, 0x32, 0x0D}
            , {0x49, 0x33, 0x0D}};

    public String[] res_pckts = new String[commnds_arr.length];

    String[] instantaneous_parameter = new String[7];

    public String[] getInstantaneous_parameter() {
        return instantaneous_parameter;
    }

    public void setInstantaneous_parameter(String[] instantaneous_parameter) {
        this.instantaneous_parameter = instantaneous_parameter;
    }

    String serial_no;
    String voltage_phase1;
    String voltage_phase2;
    String voltage_phase3;
    String current_phase1;
    String current_phase2;

    public String getVoltage_phase1() {
        return voltage_phase1;
    }

    public void setVoltage_phase1(String voltage_phase1) {
        this.voltage_phase1 = voltage_phase1;
    }

    public String getVoltage_phase2() {
        return voltage_phase2;
    }

    public void setVoltage_phase2(String voltage_phase2) {
        this.voltage_phase2 = voltage_phase2;
    }

    public String getVoltage_phase3() {
        return voltage_phase3;
    }

    public void setVoltage_phase3(String voltage_phase3) {
        this.voltage_phase3 = voltage_phase3;
    }

    public String getCurrent_phase1() {
        return current_phase1;
    }

    public void setCurrent_phase1(String current_phase1) {
        this.current_phase1 = current_phase1;
    }

    public String getCurrent_phase2() {
        return current_phase2;
    }

    public void setCurrent_phase2(String current_phase2) {
        this.current_phase2 = current_phase2;
    }

    public String getCurrent_phase3() {
        return current_phase3;
    }

    public void setCurrent_phase3(String current_phase3) {
        this.current_phase3 = current_phase3;
    }

    String current_phase3;

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    String rtc;

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public void read(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_1200);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(1200, 8, 0));
                    this.wait(1000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                }
            }

            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {

                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setWaitTime(10000);
                p.setAllData(false);
                p.setCount(11);
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        byte[] serial_no = {0x53, 0x0d};
                        serial.send(serial_no);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setSerial_no(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(2).trim());
                    p.setReply(null);

                    p.setCount(10);
                    synchronized (this) {
                        byte[] voltage1 = {0x56, 0x31, 0x0d};
                        serial.send(voltage1);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setVoltage_phase1(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(3).trim());

                    p.setReply(null);
                    p.setCount(10);
                    synchronized (this) {
                        byte[] voltage2 = {0x56, 0x32, 0x0d};
                        serial.send(voltage2);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setVoltage_phase2(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(3).trim());

                    p.setReply(null);
                    p.setCount(10);
                    synchronized (this) {
                        byte[] voltage3 = {0x56, 0x33, 0x0d};
                        serial.send(voltage3);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setVoltage_phase3(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(3).trim());


                    p.setReply(null);
                    p.setCount(10);
                    synchronized (this) {
                        byte[] current1 = {0x49, 0x31, 0x0D};
                        serial.send(current1);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setCurrent_phase1(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(3).trim());


                    p.setReply(null);
                    p.setCount(10);
                    synchronized (this) {
                        byte[] current2 = {0x49, 0x32, 0x0D};
                        serial.send(current2);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setCurrent_phase2(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(3).trim());


                    p.setReply(null);
                    p.setCount(10);
                    synchronized (this) {
                        byte[] current3 = {0x49, 0x33, 0x0D};
                        serial.send(current3);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }
                    setCurrent_phase3(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")).substring(3).trim());


                    p.setReply(null);
                    p.setCount(10);
                    synchronized (this) {
                        byte[] rtc = {(byte) 0xac};
                        serial.send(rtc);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type. ");
                    }

                    String rtc_value = maximum_demand_date_and_time(Integer.parseInt(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(16, 18) + GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(14, 16) +
                            GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(12, 14) + GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "").substring(10, 12), 16) / 60);


                    setRtc(rtc_value);
                }


            }


        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void read_billing(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_1200);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi())
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(1200, 8, 0));
                this.wait(1000);
                WifiConnectivity.mReceiver.resetBytesReceived();
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {

                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setWaitTime(1000);
                p.setAllData(false);
                p.setCount(11);
                byte[] data = {0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x0d};

                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    p.setReply(null);

                    p.setCount(1);
                    byte[] data_ = {(byte) 0x80};

                    synchronized (this) {

                        serial.send(data_, null);
                        this.wait(100);

                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type 2.");
                    }
                    p.setReply(null);

                    for (int i = 0; i < 688; i++) {
                        p.setCount(10);
                        byte[] data_f1 = {(byte) 0xF1};

                        synchronized (this) {

                            serial.send(data_f1, null);
                            this.wait(10);

                        }
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        res_pckts_bill[i] = GXCommon.bytesToHex(p.getReply());
                        setBilling_and_tod_data(res_pckts_bill);
                        p.setReply(null);
                    }
                }

            }

        }
        catch (Exception e) {
            e.getMessage();
        }
    }

    public String[] billing_dates_and_time_parsed_values() throws Exception {

        String[] data = getBilling_and_tod_data();

       /* int total_days= hex2decimal(data[45].replaceAll(" ","").substring(10,12)+data[45].replaceAll(" ","").substring(8,10)+data[45].replaceAll(" ","").substring(6,8));  // here we will get current month date and time.
        int year_of_meter=(total_days/365)+1988;
        String current_month_and_day=billing_date_and_time((total_days/365),((total_days%365)-(total_days/4)));
*/

        String[] dates = new String[13];
        if (data != null) {
            dates[0] = getRtc();
            int i = 52, j = 0;
            do {

                // dates[j+1]=
                //  String total_no_of_days = "" +hex2decimal(data[i].replaceAll(" ", "").substring(8, 10) + data[i].replaceAll(" ", "").substring(6, 8));
                String time_in_seconds = "" + hex2decimal(data[i].replaceAll(" ", "").substring(12, 14) + data[i].replaceAll(" ", "").substring(10, 12)) / 5;

                String time_hh = "" + Integer.parseInt(time_in_seconds) / (60 * 60);
                String time_mm = "" + (Integer.parseInt(time_in_seconds) % (60 * 60)) / 60;

                // String year = "" + ((Integer.parseInt(total_no_of_days) / 512) + 1988);
                // String day_and_month= billing_date_and_time( ((Integer.parseInt(total_no_of_days) / 512) + 1988),Integer.parseInt(total_no_of_days)%512);
                dates[j + 1] = GetPactFormatDate(hex2decimal(data[i].replaceAll(" ", "").substring(8, 10) + data[i].replaceAll(" ", "").substring(6, 8)))
                        + " " + time_hh + ":" + time_mm;


                i = i + 1;
                j = j + 1;
            } while (dates[12] == null);
        }
        return dates;

        // current month date will obtain at array index 45 and billing history dates will start from at array index 52. till 63

    }


    public static String GetPactFormatDate(int Value) {
        int num = (Value & 511) + 1;
        int num2 = num;
        int num3 = Value >> 9;
        int year;
        if (num3 == 0) {
            year = 1988;
        } else {
            year = 1988 + num3;
        }
        GregorianCalendar cal = new GregorianCalendar(year, 0, 1);


        Date date = cal.getTime();

        if (num == 366 && !cal.isLeapYear(year)) {
            num--;
        }
        int num4 = 1;

        while (num4 <= 366 && num != 1) {
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();

            num4++;
            --num;
        }
        if (!cal.isLeapYear(year) && cal.getTime().getMonth() > 2) {

            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            date = cal.getTime();
        }
        if (num2 == 366 && !cal.isLeapYear(year)) {

            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }


        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");

        return dt1.format(date).toString();

        //return cal.getTime().getDay()+"/"+cal.getTime().getMonth()+"/"+cal.getTime().getYear();
        //return date.toString();
    }

    public String[] billing_history_energy_values() throws Exception {

        // will return 13 month energy. and sequence will be first current month..last..second.....twelve month tod.
        String[] energy = new String[13];
        String[] data = getBilling_and_tod_data();
        if (data != null) {
            int i = 64, j = 0;
            do {
                String values = data[i].replaceAll(" ", "");
                String active_energy = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String reactive_lag = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String reactive_lead = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String apparent = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                energy[j] = active_energy + "," + reactive_lag + "," + reactive_lead + "," + apparent;
                i = i + 3;
                j = j + 1;
            } while (energy[12] == null);
        }
        return energy;
    }


    public String[] active_kw_tod_parsing() throws Exception {

// will return 13 month tod. and sequence will be first current month..last..second.....twelve month tod.
        String[] acive_energy_tod = new String[13];
        String[] data = getBilling_and_tod_data();
        if (data != null) {
            int i = 142, j = 0;
            do {
                if (data[i] != null) {
                    String values = data[i].replaceAll(" ", "");
                    String kwh_tod1 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod2 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod3 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod4 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod5 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod6 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod7 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod8 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));

                    acive_energy_tod[j] = kwh_tod1 + "," + kwh_tod2 + "," + kwh_tod3 + "," + kwh_tod4 + "," + kwh_tod5 + "," + kwh_tod6 + "," + kwh_tod7 + "," + kwh_tod8 + ",";
                    i = i + 17;
                    j = j + 1;
                }
            } while (acive_energy_tod[12] == null);
        }
        return acive_energy_tod;
    }

    public String[] apparent_kv_tod_parsing() throws Exception {

        //// will return 13 month tod. and sequence will be first current month..last..second.....twelve month tod.
        String[] acive_energy_tod = new String[13];
        String[] data = getBilling_and_tod_data();
        if (data != null) {
            int i = 150, j = 0;
            do {
                if (data[i] != null) {
                    String values = data[i].replaceAll(" ", "");
                    String kwh_tod1 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod2 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod3 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod4 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod5 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod6 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod7 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod8 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));

                    acive_energy_tod[j] = kwh_tod1 + "," + kwh_tod2 + "," + kwh_tod3 + "," + kwh_tod4 + "," + kwh_tod5 + "," + kwh_tod6 + "," + kwh_tod7 + "," + kwh_tod8 + ",";
                    i = i + 17;
                    j = j + 1;
                }
            } while (acive_energy_tod[12] == null);
        }
        return acive_energy_tod;
    }


    public String[] Reactive_lag_tod_parsing() {


        //// will return 13 month tod. and sequence will be first current month..last..second.....twelve month tod.

        // at array index 0...12. first will be maximum demand of that month then 8 tod's.
        // date and time pending
        String[] acive_energy_tod = new String[13];
        String[] data = getBilling_and_tod_data();
        int i = 158, j = 0;
        if (data != null) {
            do {
                if (data[i] != null) {
                    String values = data[i].replaceAll(" ", "");
                    String kwh_tod1 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod2 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod3 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod4 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod5 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod6 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod7 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));
                    i = i + 1;
                    values = data[i].replaceAll(" ", "");
                    String kwh_tod8 = "" + hex2decimal(values.substring(12, 14) + values.substring(10, 12) + values.substring(8, 10));

                    acive_energy_tod[j] = kwh_tod1 + "," + kwh_tod2 + "," + kwh_tod3 + "," + kwh_tod4 + "," + kwh_tod5 + "," + kwh_tod6 + "," + kwh_tod7 + "," + kwh_tod8 + ",";
                    i = i + 17;
                    j = j + 1;
                }
            } while (acive_energy_tod[12] == null);
        }
        return acive_energy_tod;
    }


    public String[] max_demand_kw_tod_parsing() throws Exception {


        //// will return 13 month tod. and sequence will be first current month..last..second.....twelve month tod. at index

        // at array index 0...12. first will be maximum demand of that month then 8 tod's.


        //since all tod has zero value so date and time for tod is pending maximum demand is done.

        String[] acive_energy_tod = new String[13];
        String[] data = getBilling_and_tod_data();
        if (data != null) {
            int i = 454, j = 0;
            do {
                String values = data[i].replaceAll(" ", "");
                String max_demand_kw = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                String date_and_time = maximum_demand_date_and_time(hex2decimal(values.substring(16, 18) + values.substring(14, 16) + values.substring(12, 14)));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod1 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod2 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod3 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod4 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod5 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod6 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod7 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod8 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));

                acive_energy_tod[j] = max_demand_kw + "," + date_and_time + "," + kwh_tod1 + "," + kwh_tod2 + "," + kwh_tod3 + "," + kwh_tod4 + "," + kwh_tod5 + "," + kwh_tod6 + "," + kwh_tod7 + "," + kwh_tod8 + ",";
                i = i + 10;
                j = j + 1;

            } while (acive_energy_tod[12] == null && data[i] != null);
        }
        return acive_energy_tod;
    }


    public String[] max_demand_kv_tod_parsing() throws Exception {


        //// will return 13 month tod. and sequence will be first current month..last..second.....twelve month tod.

        // since all the tod has 0 value so date and time for tod is pending. maximum demand date and time done.

        String[] data = getBilling_and_tod_data();

        String[] acive_energy_tod = new String[13];
        if (data != null) {
            int i = 463, j = 0;
            do {
                String values = data[i].replaceAll(" ", "");
                String max_demand_kw = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                String date_and_time = maximum_demand_date_and_time(hex2decimal(values.substring(16, 18) + values.substring(14, 16) + values.substring(12, 14)));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod1 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod2 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod3 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod4 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod5 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod6 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod7 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));
                i = i + 1;
                values = data[i].replaceAll(" ", "");
                String kwh_tod8 = "" + hex2decimal(values.substring(10, 12) + values.substring(8, 10));

                acive_energy_tod[j] = max_demand_kw + "," + date_and_time + "," + kwh_tod1 + "," + kwh_tod2 + "," + kwh_tod3 + "," + kwh_tod4 + "," + kwh_tod5 + "," + kwh_tod6 + "," + kwh_tod7 + "," + kwh_tod8 + ",";
                i = i + 10;
                j = j + 1;

            } while (acive_energy_tod[12] == null && data[i] != null);
        }
        return acive_energy_tod;
    }


    public String parseDateTime(String pkt, int a, int b, int c, int d, int type) {
        // created by shubham for rtc date and time parsing.

        String retVal = "";

        String[] parse = new String[25];

        parse = pkt.split(" ");

        int da = Integer.parseInt(parse[a] + parse[b], 16);

        double ti = Integer.parseInt(parse[c] + parse[d], 16);

        double time = 0;

        if (type == 1) {
            time = ti / 3600.0;
        } else if (type == 2) {
            time = (ti * 5) / 3600.0;
        }

        time = ti / 3600.0;

        Arrays.fill(parse, null);

        if (time != 0) {

            String hour = "" + time;

            parse = hour.split("\\.");

            hour = parse[0];

            String minutes = "" + Double.parseDouble("0." + parse[1]) * 60;

            Arrays.fill(parse, null);

            parse = minutes.split("\\.");

            minutes = parse[0];

            String seconds = "" + Math.round(Double.parseDouble("0." + parse[1]) * 60);

            Arrays.fill(parse, null);

            String getDate = "12/08/2018"; //calcDate(da);

            retVal = getDate + " " + hour + " : " + minutes + " : " + seconds;

        }

        return retVal;

    }

    public String maximum_demand_date_and_time(int minutes) {

        int no_of_days = minutes / (24 * 60);

        int no_of_years = (no_of_days / 365);

        String meter_year = "" + (no_of_years + 1988);

        String month_and_day = billing_date_and_time((no_of_years + 1988), ((no_of_days % 365) - (no_of_years / 4)));

        String no_of_hours = "" + (minutes % (24 * 60)) / 60;

        String no_of_minutes = "" + (minutes % (24 * 60)) % 60;

        return month_and_day + "/" + meter_year + " " + no_of_hours + ":" + no_of_minutes;

    }

    public String billing_date_and_time(int year, int no_of_days) {

        int month = 0;
        int time = 0;
        int day = 0;

        if (year % 4 == 0) {

            if (no_of_days <= 31) {
                month = 1;
                day = no_of_days;

            } else if (no_of_days >= 32 && no_of_days <= 60) {
                month = 2;
                day = no_of_days - 31;
            } else if (no_of_days >= 61 && no_of_days <= 91) {
                month = 3;
                day = no_of_days - 60;

            } else if (no_of_days >= 92 && no_of_days <= 121) {
                month = 4;
                day = no_of_days - 91;
            } else if (no_of_days >= 122 && no_of_days <= 152) {
                month = 5;
                day = no_of_days - 121;

            } else if (no_of_days >= 153 && no_of_days <= 182) {
                month = 6;
                day = no_of_days - 152;

            } else if (no_of_days >= 183 && no_of_days <= 213) {
                month = 7;
                day = no_of_days - 182;

            } else if (no_of_days >= 214 && no_of_days <= 244) {
                month = 8;
                day = no_of_days - 213;

            } else if (no_of_days >= 245 && no_of_days <= 274) {
                month = 9;
                day = no_of_days - 244;

            } else if (no_of_days >= 275 && no_of_days <= 305) {
                month = 10;
                day = no_of_days - 274;

            } else if (no_of_days >= 306 && no_of_days <= 335) {
                month = 11;

                day = no_of_days - 305;

            } else if (no_of_days >= 336 && no_of_days <= 366) {
                month = 12;
                day = no_of_days - 335;

            }
        } else {
            if (no_of_days <= 31) {
                month = 1;
                day = no_of_days;

            } else if (no_of_days >= 32 && no_of_days <= 59) {
                month = 2;
                day = no_of_days - 31;
            } else if (no_of_days >= 60 && no_of_days <= 90) {
                month = 3;
                day = no_of_days - 59;

            } else if (no_of_days >= 91 && no_of_days <= 120) {
                month = 4;
                day = no_of_days - 90;
            } else if (no_of_days >= 121 && no_of_days <= 151) {
                month = 5;
                day = no_of_days - 120;

            } else if (no_of_days >= 152 && no_of_days <= 181) {
                month = 6;
                day = no_of_days - 151;

            } else if (no_of_days >= 182 && no_of_days <= 212) {
                month = 7;
                day = no_of_days - 181;

            } else if (no_of_days >= 213 && no_of_days <= 243) {
                month = 8;
                day = no_of_days - 212;

            } else if (no_of_days >= 244 && no_of_days <= 273) {
                month = 9;
                day = no_of_days - 243;

            } else if (no_of_days >= 274 && no_of_days <= 304) {
                month = 10;
                day = no_of_days - 273;

            } else if (no_of_days >= 305 && no_of_days <= 334) {
                month = 11;

                day = no_of_days - 304;

            } else if (no_of_days >= 335 && no_of_days <= 365) {
                month = 12;
                day = no_of_days - 334;

            }


        }

        return day + "/" + month;
    }

// this is from secure that will be used  to convert date of billing.




/*
    public static DateTime GetPactFormatDate(int Value)
{
	int num = (Value & 511) + 1;
	int num2 = num;
	int num3 = Value >> 9;
	int year;
	if (num3 == 0)
	{
		year = 1988;
	}
	else
	{
		year = 1988 + num3;
	}
	DateTime result = new DateTime(year, 1, 1);
	if (num == 366 && !DateTime.IsLeapYear(result.Year))
	{
		num--;
	}
	int num4 = 1;
	while (num4 <= 366 && result.DayOfYear != num)
	{
		result = result.AddDays(1.0);
		num4++;
	}
	if (!DateTime.IsLeapYear(result.Year) && result.Month > 2)
	{
		result = result.AddDays(-1.0);
	}
	if (num2 == 366 && !DateTime.IsLeapYear(result.Year))
	{
		result = result.AddDays(1.0);
	}
	return result;
}

*/


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

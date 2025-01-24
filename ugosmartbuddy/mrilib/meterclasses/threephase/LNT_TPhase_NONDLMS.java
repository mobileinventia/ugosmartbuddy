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

import java.text.DecimalFormat;


public class LNT_TPhase_NONDLMS {
    int Utility_Code;

    public LNT_TPhase_NONDLMS(int Utility_Code){
        this.Utility_Code=Utility_Code;

    }
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try{
            synchronized (this) {
                read(serial);
            }
            String instantenousData[] = parsing_of_data();
            String current_billing_data[] = current_month_billing_data();
            String History_billing_data[] = max_demand_kv()[0].split(";");
            Double max_demand_kv=Utils.find_Largest(History_billing_data);
            String date = Utils.correctDateFormatWithTime(current_billing_data[0]);
            non_dlms_parameter.setMETER_NO(instantenousData[0]);
            non_dlms_parameter.setMANUFACTURE_NAME(instantenousData[1].split(",")[0]);
            non_dlms_parameter.setYEAR_OF_MANUFACTURE(instantenousData[1].split(",")[2]);
            non_dlms_parameter.setCURRENT_R_PHASE(instantenousData[2]);
            non_dlms_parameter.setCURRENT_Y_PHASE(instantenousData[3]);
            non_dlms_parameter.setCURRENT_B_PHASE(instantenousData[4]);
            non_dlms_parameter.setVOLTAGE_R_PHASE(instantenousData[5]);
            non_dlms_parameter.setVOLTAGE_Y_PHASE(instantenousData[6]);
            non_dlms_parameter.setVOLTAGE_B_PHASE(instantenousData[7]);
            non_dlms_parameter.setBILLING_DATE(date);
            non_dlms_parameter.setACTIVE_ENERGY(current_billing_data[1]);
            non_dlms_parameter.setAPPARENT_ENERGY(current_billing_data[10]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY(max_demand_kv.toString());
           // non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY();
        }   catch (Exception e){
            e.getMessage();

        }
        return  non_dlms_parameter;
    }

    public String getSerialno() {
        return serialno;
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

    public void read(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_300);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);
            synchronized (this) {
                serial.open();
            }

            synchronized (this) {
                if(WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(300, 7, 2));
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
                  //  synchronized (this) {
                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);
                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setSerialno(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")));
                        this.wait(100);

                  //  }
                        p.setReply(null);

                          synchronized (this){
                            byte[] second_packet = {0x06, 0x30, 0x35, 0x30, 0x0D, 0x0A};
                           /// p.setCount(96746);
                           p.setEop((byte) 0x21);
                            serial.send(second_packet, null);
                            this.wait(100);
                            serial.close();
                            this.wait(100);
                              if(WifiConnectivity.getIsWifi()){
                                  WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(getBaudRate().getValue(),7,2));
                                  this.wait(1000);
                                  WifiConnectivity.mReceiver.resetBytesReceived();
                              }
                              else {
                                  serial.setBaudRate(getBaudRate());
                                  this.wait(100);
                                  serial.open();
                                  this.wait(100);
                              }
                            if (!serial.receive(p)) {
                                throw new Exception("Invalid meter type.");
                            }
                            setAlldata(hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ", "")));
                        }
                    }



            }

        } catch (Exception e) {

             e.getMessage();
        }
    }

    public String[] parsing_of_data(){

        String parse_data[]=null, allDatas = getAlldata();
        if(allDatas!=null) {
            String R_phase_voltage = "", Y_phase_voltage = "", B_phase_voltage = "";
            String R_phase_current = "", Y_phase_current = "", B_phase_current = "";
            String[] data = allDatas.trim().replaceAll("\\(", "").split("\\)");

            String serial_no = data[1].replace("H", "").trim();

            String manufacture_name = data[0].replace("H", "").trim(); // manufacture name contain year of manufacture
            if (allDatas.contains("U")) {
                String instantaneous_parameter_string = allDatas.substring(allDatas.indexOf("U"), allDatas.lastIndexOf("U"));

                R_phase_voltage = instantaneous_parameter_string.split("\\)")[0].replace("U(", "").split(" ")[0];

                Y_phase_voltage = instantaneous_parameter_string.split("\\)")[0].replace("U(", "").split(" ")[1];

                B_phase_voltage = instantaneous_parameter_string.split("\\)")[0].replace("U(", "").split(" ")[2];


                R_phase_current = instantaneous_parameter_string.split("\\)")[1].replace("U(", "").split(" ")[0].trim();

                Y_phase_current = instantaneous_parameter_string.split("\\)")[1].replace("U(", "").split(" ")[1];

                B_phase_current = instantaneous_parameter_string.split("\\)")[1].replace("U(", "").split(" ")[2];

                parse_data = new String[]{serial_no,manufacture_name,R_phase_current,Y_phase_current,B_phase_current,R_phase_voltage,
                        Y_phase_voltage,B_phase_voltage};
            }
        }
        return parse_data;

    }


    public String billing_date( String data){

        String date_and_time=data.substring(3,5)+"/"+data.substring(5,7)+"/"+data.substring(7,9)+" "+data.substring(9,11)+":"+data.substring(11,13);

        return date_and_time;

    }


    public String[] current_month_billing_data(){
        String data = getAlldata();
        String[] values = null;
        if(data!=null && data.length()>0) {
            String rtc =data.
            trim().replaceAll("\\(", "").split("\\)")[10].substring(3, 5) + "/" + getAlldata().trim().replaceAll("\\(", "").split("\\)")[10].substring(5, 7) + "/" + getAlldata().trim().replaceAll("\\(", "").split("\\)")[10].substring(7, 9) + " " + getAlldata().trim().replaceAll("\\(", "").split("\\)")[10].substring(11, 13) + ":" + getAlldata().trim().replaceAll("\\(", "").split("\\)")[10].substring(13, 15) + ":" + getAlldata().trim().replaceAll("\\(", "").split("\\)")[10].substring(15, 17);


            String current_month_apparent = data.substring(getAlldata().indexOf("II"), getAlldata().indexOf("IE")).split("\\.")[0].replace("II(", "");

            String current_month_active = data.substring(data.indexOf("II"), data.indexOf("IE")).split("\\.")[1];

            String current_month_reactive_lag = data.substring(data.indexOf("II"), data.indexOf("IE")).split("\\.")[2];

            String current_month_reactive_lead = data.substring(data.indexOf("II"), data.indexOf("IE")).split("\\.")[3];


            String current_month_tod_apparent_tarrif1 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[0].replace("(", "");
            String current_month_tod_apparent_tarrif2 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[1];
            String current_month_tod_apparent_tarrif3 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[2];
            String current_month_tod_apparent_tarrif4 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[3];
            String current_month_tod_apparent_tarrif5 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[4];
            String current_month_tod_apparent_tarrif6 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[5];
            String current_month_tod_apparent_tarrif7 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[6];
            String current_month_tod_apparent_tarrif8 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[1].split("\\.")[7];


            String current_month_tod_active_tarrif1 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[0].replace("(", "");
            String current_month_tod_active_tarrif2 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[1];
            String current_month_tod_active_tarrif3 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[2];
            String current_month_tod_active_tarrif4 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[3];
            String current_month_tod_active_tarrif5 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[4];
            String current_month_tod_active_tarrif6 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[5];
            String current_month_tod_active_tarrif7 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[6];
            String current_month_tod_active_tarrif8 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[2].split("\\.")[7];


            String current_month_tod_reactive_lag_tarrif1 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[0].replace("(", "");
            String current_month_tod_reactive_lag_tarrif2 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[1];
            String current_month_tod_reactive_lag_tarrif3 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[2];
            String current_month_tod_reactive_lag_tarrif4 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[3];
            String current_month_tod_reactive_lag_tarrif5 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[4];
            String current_month_tod_reactive_lag_tarrif6 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[5];
            String current_month_tod_reactive_lag_tarrif7 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[6];
            String current_month_tod_reactive_lag_tarrif8 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[3].split("\\.")[7];


            String current_month_tod_reactive_lead_tarrif1 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[0].replace("(", "");
            String current_month_tod_reactive_lead_tarrif2 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[1];
            String current_month_tod_reactive_lead_tarrif3 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[2];
            String current_month_tod_reactive_lead_tarrif4 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[3];
            String current_month_tod_reactive_lead_tarrif5 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[4];
            String current_month_tod_reactive_lead_tarrif6 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[5];
            String current_month_tod_reactive_lead_tarrif7 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[6];
            String current_month_tod_reactive_lead_tarrif8 = data.substring(data.indexOf("JI"), data.indexOf("JE")).split("JI")[4].split("\\.")[7];


            String[] current_month_tod_max_demand_kva = data.substring(data.indexOf("L("), data.indexOf("M(")).split("\\)");

            String current_month_tod_max_demand_tarrif1 = current_month_tod_max_demand_kva[0].split(" ")[0].replace("L(", "");
            String current_month_tod_max_demand_tarrif2 = current_month_tod_max_demand_kva[0].split(" ")[1];
            String current_month_tod_max_demand_tarrif3 = current_month_tod_max_demand_kva[0].split(" ")[2];
            String current_month_tod_max_demand_tarrif4 = current_month_tod_max_demand_kva[0].split(" ")[3];
            String current_month_tod_max_demand_tarrif5 = current_month_tod_max_demand_kva[0].split(" ")[4];
            String current_month_tod_max_demand_tarrif6 = current_month_tod_max_demand_kva[0].split(" ")[5];
            String current_month_tod_max_demand_tarrif7 = current_month_tod_max_demand_kva[0].split(" ")[6];
            String current_month_tod_max_demand_tarrif8 = current_month_tod_max_demand_kva[0].split(" ")[7];


            String current_month_max_demand_date_and_time = current_month_tod_max_demand_kva[2].replace("L(", "").trim();


            String year = current_month_max_demand_date_and_time.substring(0, 2);

            String month_and_day = current_month_max_demand_date_and_time.substring(2, 5);

            String time_hh = current_month_max_demand_date_and_time.substring(5, 6);

            String day_and_month_and_time = date_and_time(hex2decimal(month_and_day), time_hh);

            String day = day_and_month_and_time.split(",")[0];

            String month = day_and_month_and_time.split(",")[1];

            String time = day_and_month_and_time.split(",")[2];

            String time_mm = current_month_max_demand_date_and_time.substring(6, 8);


            String max_demand_date_kva = day + "/" + month + "/" + year + " " + time + ":" + time_mm;
            // since this meter gives only maximum demand date and time from all the tod's. remaining values it will not give date and time.


            values = new String[] {rtc, current_month_active, current_month_tod_active_tarrif1
                    , current_month_tod_active_tarrif2, current_month_tod_active_tarrif3,
                    current_month_tod_active_tarrif4, current_month_tod_active_tarrif5, current_month_tod_active_tarrif6,
                    current_month_tod_active_tarrif7, current_month_tod_active_tarrif8, current_month_apparent,
                    current_month_tod_apparent_tarrif1, current_month_tod_apparent_tarrif2,
                    current_month_tod_apparent_tarrif3, current_month_tod_apparent_tarrif4, current_month_tod_apparent_tarrif5,
                    current_month_tod_apparent_tarrif6, current_month_tod_apparent_tarrif7, current_month_tod_apparent_tarrif8,
                    current_month_reactive_lag, current_month_tod_reactive_lag_tarrif1, current_month_tod_reactive_lag_tarrif2,
                    current_month_tod_reactive_lag_tarrif3, current_month_tod_reactive_lag_tarrif4,
                    current_month_tod_reactive_lag_tarrif5, current_month_tod_reactive_lag_tarrif6,
                    current_month_tod_reactive_lag_tarrif7, current_month_tod_reactive_lag_tarrif8, current_month_reactive_lead,
                    current_month_tod_reactive_lead_tarrif1, current_month_tod_reactive_lead_tarrif2,
                    current_month_tod_reactive_lead_tarrif3, current_month_tod_reactive_lead_tarrif4,
                    current_month_tod_reactive_lead_tarrif5, current_month_tod_reactive_lead_tarrif6,
                    current_month_tod_reactive_lead_tarrif7, current_month_tod_reactive_lead_tarrif8,
                    max_demand_date_kva,
                    current_month_tod_max_demand_tarrif1, current_month_tod_max_demand_tarrif2, current_month_tod_max_demand_tarrif3,
                    current_month_tod_max_demand_tarrif4, current_month_tod_max_demand_tarrif5, current_month_tod_max_demand_tarrif6,
                    current_month_tod_max_demand_tarrif7, current_month_tod_max_demand_tarrif8};
            // String[] values={rtc,current_month_active,current_month_tod_active_tarrif1,current_month_tod_active_tarrif2,
            // current_month_tod_active_tarrif3,current_month_tod_active_tarrif4,current_month_tod_active_tarrif5,
            // current_month_tod_active_tarrif6,current_month_tod_active_tarrif7,current_month_tod_active_tarrif8,
            // current_month_apparent,current_month_tod_apparent_tarrif1,current_month_tod_apparent_tarrif2,
            // current_month_tod_apparent_tarrif3,current_month_tod_apparent_tarrif4,current_month_tod_apparent_tarrif5,
            // current_month_tod_apparent_tarrif6,current_month_tod_apparent_tarrif7,current_month_tod_apparent_tarrif8,
            // current_month_reactive_lag,current_month_tod_reactive_lag_tarrif1,current_month_tod_reactive_lag_tarrif2,
            // current_month_tod_reactive_lag_tarrif3,current_month_tod_reactive_lag_tarrif4,current_month_tod_reactive_lag_tarrif5,
            // current_month_tod_reactive_lag_tarrif6,current_month_tod_reactive_lag_tarrif7,current_month_tod_reactive_lag_tarrif8,
            // current_month_reactive_lead,current_month_tod_reactive_lead_tarrif1,current_month_tod_reactive_lead_tarrif2,
            // current_month_tod_reactive_lead_tarrif3,current_month_tod_reactive_lead_tarrif4,current_month_tod_reactive_lead_tarrif5,
            // current_month_tod_reactive_lead_tarrif6,current_month_tod_reactive_lead_tarrif7,current_month_tod_reactive_lead_tarrif8,
            // current_month_tod_max_demand_kva[0]};


        }
        return values;
    }


    public String[] billing_history_data(){
        String allData = getAlldata();
        String[] data = null;
        if(allData!=null && allData.length()>0) {
            String last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[12]);

            String second_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[13]);

            String third_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[14]);

            String fourth_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[15]);

            String fifth_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[16]);

            String sixth_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[17]);

            String seventh_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[18]);

            String eighth_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[19]);

            String ninth_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[20]);

            String tenth_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[21]);

            String eleventh_last_month_date = billing_date(allData.trim().replaceAll("\\(", "").split("\\)")[22]);


            //String[] billing_history=allData.substring(allData.indexOf("KI"),allData.lastIndexOf("KE")).split("\\)");

            // since we are not getting energy values for billing profile data. we need to add all tod's and find respective energy.
            // for example: for active energy we need to that month all active tod's of active energy.


            String last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[0]);
            String last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[1]);
            String last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[2]);
            String last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[3]);


            String last_month_data = last_month_date + "," + last_month_active + "," + last_month_apparent + "," + last_month_reactive_lag + "," + last_month_reactive_lead;


            String second_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[8]);
            String second_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[9]);
            String second_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[10]);
            String second_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[11]);

            String second_month_data = second_last_month_date + "," + second_last_month_active + "," + second_last_month_apparent + "," + second_last_month_reactive_lag + "," + second_last_month_reactive_lead;


            String third_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[16]);
            String third_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[17]);
            String third_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[18]);
            String third_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[19]);

            String third_last_month_data = third_last_month_date + "," + third_last_month_active + "," + third_last_month_apparent + "," + third_last_month_reactive_lag + "," + third_last_month_reactive_lead;


            String fourth_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[24]);
            String fourth_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[25]);
            String fourth_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[26]);
            String fourth_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[27]);

            String fourth__month_data = fourth_last_month_date + "," + fourth_last_month_active + "," + fourth_last_month_apparent + "," + fourth_last_month_reactive_lag + "," + fourth_last_month_reactive_lead;


            String fifth_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[32]);
            String fifth_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[33]);
            String fifth_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[34]);
            String fifth_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[35]);

            String fifth_last_month_data = fifth_last_month_date + "," + fifth_last_month_active + "," + fifth_last_month_apparent + "," + fifth_last_month_reactive_lag + "," + fifth_last_month_reactive_lead;


            String sixth_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[40]);
            String sixth_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[41]);
            String sixth_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[42]);
            String sixth_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[43]);

            String sixth_last_month_data = sixth_last_month_date + "," + sixth_last_month_active + "," + sixth_last_month_apparent + "," + sixth_last_month_reactive_lag + "," + sixth_last_month_reactive_lead;


            String seventh_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[48]);
            String seventh_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[49]);
            String seventh_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[50]);
            String seventh_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[51]);

            String seventh_last_month_data = seventh_last_month_date + "," + seventh_last_month_active + "," + seventh_last_month_apparent + "," + seventh_last_month_reactive_lag + "," + seventh_last_month_reactive_lead;


            String eighth_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[56]);
            String eighth_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[57]);
            String eighth_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[58]);
            String eighth_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[59]);


            String eighth_last_month_data = eighth_last_month_date + "," + eighth_last_month_active + "," + eighth_last_month_apparent + "," + eighth_last_month_reactive_lag + "," + eighth_last_month_reactive_lead;


            String ninth_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[64]);
            String ninth_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[65]);
            String ninth_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[66]);
            String ninth_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[67]);

            String ninth_last_month_data = ninth_last_month_date + "," + ninth_last_month_active + "," + ninth_last_month_apparent + "," + ninth_last_month_reactive_lag + "," + ninth_last_month_reactive_lead;


            String tenth_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[72]);
            String tenth_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[73]);
            String tenth_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[74]);
            String tenth_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[75]);


            String tenth_last_month_data = tenth_last_month_date + "," + tenth_last_month_active + "," + tenth_last_month_apparent + "," + tenth_last_month_reactive_lag + "," + tenth_last_month_reactive_lead;


            String eleventh_last_month_apparent = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[80]);
            String eleventh_last_month_active = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[81]);
            String eleventh_last_month_reactive_lag = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[82]);
            String eleventh_last_month_reactive_lead = billing_parsing(allData.substring(allData.indexOf("KI"), allData.lastIndexOf("KE")).split("\\)")[83]);

            String eleventh_last_month_data = eleventh_last_month_date + "," + eleventh_last_month_active + "," + eleventh_last_month_apparent + "," + eleventh_last_month_reactive_lag + "," + eleventh_last_month_reactive_lead;


            data = new String[]{last_month_data, second_month_data, third_last_month_data, fourth__month_data, fifth_last_month_data,
                    sixth_last_month_data, seventh_last_month_data, eighth_last_month_data, ninth_last_month_data, tenth_last_month_data,
                    eleventh_last_month_data};
        }
        return data;


        // will return comma separated billing date and tod of all four energies.
    }

    public String billing_parsing(String data){

                String[] values=data.replace("KI(","").split("\\.");
                String tarrif1=values[0];
                String tarrif2=values[1];
                String tarrif3=values[2];
                String tarrif4=values[3];
                String tarrif5=values[4];
                String tarrif6=values[5];
                String tarrif7=values[6];
                String tarrif8=values[7];
              //  String energy=""+(Integer.parseInt(values[0])+Integer.parseInt(values[1])+Integer.parseInt(values[2])+Integer.parseInt(values[3])+Integer.parseInt(values[4])+Integer.parseInt(values[5])+Integer.parseInt(values[6])+Integer.parseInt(values[7]));
                return tarrif1+";"+tarrif2+";"+tarrif3+";"+tarrif4+";"+tarrif5+";"+tarrif6+";"+tarrif7+";"+tarrif8;
    }

    public String[] max_demand_kv(){

        String[] data=getAlldata().substring(getAlldata().indexOf("M("),getAlldata().indexOf("N(")).split("\\)");

        String[] values=new String[11];

            String last_month_max_kv_tarrif1 = data[0].split(" ")[0].replace("M(","");
            String last_month_max_kv_tarrif2 = data[0].split(" ")[1];
            String last_month_max_kv_tarrif3 = data[0].split(" ")[2];
            String last_month_max_kv_tarrif4 = data[0].split(" ")[3];
            String last_month_max_kv_tarrif5 = data[0].split(" ")[4];
            String last_month_max_kv_tarrif6 = data[0].split(" ")[5];
            String last_month_max_kv_tarrif7 = data[0].split(" ")[6];
            String last_month_max_kv_tarrif8 = data[0].split(" ")[7];
            values[0]=last_month_max_kv_tarrif1+";"+last_month_max_kv_tarrif2+";"+last_month_max_kv_tarrif3+";"
                    +last_month_max_kv_tarrif4+";"+last_month_max_kv_tarrif5+";"+last_month_max_kv_tarrif6+";"
                    +last_month_max_kv_tarrif7+";"+last_month_max_kv_tarrif8;


            String second_last_max_kv_tarrif1 = data[1].split(" ")[0].replace("M(","");
            String second_last_max_kv_tarrif2 = data[1].split(" ")[1];
            String second_last_max_kv_tarrif3 = data[1].split(" ")[2];
            String second_last_max_kv_tarrif4 = data[1].split(" ")[3];
            String second_last_max_kv_tarrif5 = data[1].split(" ")[4];
            String second_last_max_kv_tarrif6 = data[1].split(" ")[5];
            String second_last_max_kv_tarrif7 = data[1].split(" ")[6];
            String second_last_max_kv_tarrif8 = data[1].split(" ")[7];
            values[1]=second_last_max_kv_tarrif1+";"+second_last_max_kv_tarrif2+";"+second_last_max_kv_tarrif3+";"+second_last_max_kv_tarrif4+";"+second_last_max_kv_tarrif5+";"+second_last_max_kv_tarrif6+";"+second_last_max_kv_tarrif7+";"+second_last_max_kv_tarrif8;


        String third_last_month_max_kv_tarrif1 = data[2].split(" ")[0].replace("M(","");
        String third_last_month_max_kv_tarrif2 = data[2].split(" ")[1];
        String third_last_month_max_kv_tarrif3 = data[2].split(" ")[2];
        String third_last_month_max_kv_tarrif4 = data[2].split(" ")[3];
        String third_last_month_max_kv_tarrif5 = data[2].split(" ")[4];
        String third_last_month_max_kv_tarrif6 = data[2].split(" ")[5];
        String third_last_month_max_kv_tarrif7 = data[2].split(" ")[6];
        String third_last_month_max_kv_tarrif8 = data[2].split(" ")[7];
        values[2]=third_last_month_max_kv_tarrif1+";"+third_last_month_max_kv_tarrif2+";"+third_last_month_max_kv_tarrif3+";"+third_last_month_max_kv_tarrif4+";"+third_last_month_max_kv_tarrif5+";"+third_last_month_max_kv_tarrif6+";"+third_last_month_max_kv_tarrif7+";"+third_last_month_max_kv_tarrif8;



        String fourth_last_month_max_kv_tarrif1 = data[3].split(" ")[0].replace("M(","");
        String fourth_last_month_max_kv_tarrif2 = data[3].split(" ")[1];
        String fourth_last_month_max_kv_tarrif3 = data[3].split(" ")[2];
        String fourth_last_month_max_kv_tarrif4 = data[3].split(" ")[3];
        String fourth_last_month_max_kv_tarrif5 = data[3].split(" ")[4];
        String fourth_last_month_max_kv_tarrif6 = data[3].split(" ")[5];
        String fourth_last_month_max_kv_tarrif7 = data[3].split(" ")[6];
        String fourth_last_month_max_kv_tarrif8 = data[3].split(" ")[7];
        values[3]=fourth_last_month_max_kv_tarrif1+";"+fourth_last_month_max_kv_tarrif2+";"+fourth_last_month_max_kv_tarrif3+";"+fourth_last_month_max_kv_tarrif4+";"+fourth_last_month_max_kv_tarrif5+";"+fourth_last_month_max_kv_tarrif6+";"+fourth_last_month_max_kv_tarrif7+";"+fourth_last_month_max_kv_tarrif8;

        String fifth_last_month_max_kv_tarrif1 = data[4].split(" ")[0].replace("M(","");
        String fifth_last_month_max_kv_tarrif2 = data[4].split(" ")[1];
        String fifth_last_month_max_kv_tarrif3 = data[4].split(" ")[2];
        String fifth_last_month_max_kv_tarrif4 = data[4].split(" ")[3];
        String fifth_last_month_max_kv_tarrif5 = data[4].split(" ")[4];
        String fifth_last_month_max_kv_tarrif6 = data[4].split(" ")[5];
        String fifth_last_month_max_kv_tarrif7 = data[4].split(" ")[6];
        String fifth_last_month_max_kv_tarrif8 = data[4].split(" ")[7];
        values[4]=fifth_last_month_max_kv_tarrif1+";"+fifth_last_month_max_kv_tarrif2+";"+fifth_last_month_max_kv_tarrif3+";"+fifth_last_month_max_kv_tarrif4+";"+fifth_last_month_max_kv_tarrif5+";"+fifth_last_month_max_kv_tarrif6+";"+fifth_last_month_max_kv_tarrif7+";"+fifth_last_month_max_kv_tarrif8;

        String sixth_last_month_max_kv_tarrif1 = data[5].split(" ")[0].replace("M(","");
        String sixth_last_month_max_kv_tarrif2 = data[5].split(" ")[1];
        String sixth_last_month_max_kv_tarrif3 = data[5].split(" ")[2];
        String sixth_last_month_max_kv_tarrif4 = data[5].split(" ")[3];
        String sixth_last_month_max_kv_tarrif5 = data[5].split(" ")[4];
        String sixth_last_month_max_kv_tarrif6 = data[5].split(" ")[5];
        String sixth_last_month_max_kv_tarrif7 = data[5].split(" ")[6];
        String sixth_last_month_max_kv_tarrif8 = data[5].split(" ")[7];
        values[5]=sixth_last_month_max_kv_tarrif1+";"+sixth_last_month_max_kv_tarrif2+";"+sixth_last_month_max_kv_tarrif3+";"+sixth_last_month_max_kv_tarrif4+";"+sixth_last_month_max_kv_tarrif5+";"+sixth_last_month_max_kv_tarrif6+";"+sixth_last_month_max_kv_tarrif7+";"+sixth_last_month_max_kv_tarrif8;



        String seventh_last_month_max_kv_tarrif1 = data[6].split(" ")[0].replace("M(","");
        String seventh_last_month_max_kv_tarrif2 = data[6].split(" ")[1];
        String seventh_last_month_max_kv_tarrif3 = data[6].split(" ")[2];
        String seventh_last_month_max_kv_tarrif4 = data[6].split(" ")[3];
        String seventh_last_month_max_kv_tarrif5 = data[6].split(" ")[4];
        String seventh_last_month_max_kv_tarrif6 = data[6].split(" ")[5];
        String seventh_last_month_max_kv_tarrif7 = data[6].split(" ")[6];
        String seventh_last_month_max_kv_tarrif8 = data[6].split(" ")[7];
        values[6]=seventh_last_month_max_kv_tarrif1+";"+seventh_last_month_max_kv_tarrif2+";"+seventh_last_month_max_kv_tarrif3+";"+seventh_last_month_max_kv_tarrif4+";"+seventh_last_month_max_kv_tarrif5+";"+seventh_last_month_max_kv_tarrif6+";"+seventh_last_month_max_kv_tarrif7+";"+seventh_last_month_max_kv_tarrif8;

        String eighth_last_month_max_kv_tarrif1 = data[7].split(" ")[0].replace("M(","");
        String eighth_last_month_max_kv_tarrif2 = data[7].split(" ")[1];
        String eighth_last_month_max_kv_tarrif3 = data[7].split(" ")[2];
        String eighth_last_month_max_kv_tarrif4 = data[7].split(" ")[3];
        String eighth_last_month_max_kv_tarrif5 = data[7].split(" ")[4];
        String eighth_last_month_max_kv_tarrif6 = data[7].split(" ")[5];
        String eighth_last_month_max_kv_tarrif7 = data[7].split(" ")[6];
        String eighth_last_month_max_kv_tarrif8 = data[7].split(" ")[7];
        values[7]=eighth_last_month_max_kv_tarrif1+";"+eighth_last_month_max_kv_tarrif2+";"+eighth_last_month_max_kv_tarrif3+";"+eighth_last_month_max_kv_tarrif4+";"+eighth_last_month_max_kv_tarrif5+";"+eighth_last_month_max_kv_tarrif6+";"+eighth_last_month_max_kv_tarrif7+";"+eighth_last_month_max_kv_tarrif8;

        String ninth_last_month_max_kv_tarrif1 = data[8].split(" ")[0].replace("M(","");
        String ninth_last_month_max_kv_tarrif2 = data[8].split(" ")[1];
        String ninth_last_month_max_kv_tarrif3 = data[8].split(" ")[2];
        String ninth_last_month_max_kv_tarrif4 = data[8].split(" ")[3];
        String ninth_last_month_max_kv_tarrif5 = data[8].split(" ")[4];
        String ninth_last_month_max_kv_tarrif6 = data[8].split(" ")[5];
        String ninth_last_month_max_kv_tarrif7 = data[8].split(" ")[6];
        String ninth_last_month_max_kv_tarrif8 = data[8].split(" ")[7];
        values[8]=ninth_last_month_max_kv_tarrif1+";"+ninth_last_month_max_kv_tarrif2+";"+ninth_last_month_max_kv_tarrif3+";"+ninth_last_month_max_kv_tarrif4+";"+ninth_last_month_max_kv_tarrif5+";"+ninth_last_month_max_kv_tarrif6+";"+ninth_last_month_max_kv_tarrif7+";"+ninth_last_month_max_kv_tarrif8;

        String tenth_last_month_max_kv_tarrif1 = data[9].split(" ")[0].replace("M(","");
        String tenth_last_month_max_kv_tarrif2 = data[9].split(" ")[1];
        String tenth_last_month_max_kv_tarrif3 = data[9].split(" ")[2];
        String tenth_last_month_max_kv_tarrif4 = data[9].split(" ")[3];
        String tenth_last_month_max_kv_tarrif5 = data[9].split(" ")[4];
        String tenth_last_month_max_kv_tarrif6 = data[9].split(" ")[5];
        String tenth_last_month_max_kv_tarrif7 = data[9].split(" ")[6];
        String tenth_last_month_max_kv_tarrif8 = data[9].split(" ")[7];
        values[9]=tenth_last_month_max_kv_tarrif1+";"+tenth_last_month_max_kv_tarrif2+";"+tenth_last_month_max_kv_tarrif3+";"+tenth_last_month_max_kv_tarrif4+";"+tenth_last_month_max_kv_tarrif5+";"+tenth_last_month_max_kv_tarrif6+";"+tenth_last_month_max_kv_tarrif7+";"+tenth_last_month_max_kv_tarrif8;

        String eleventh_last_month_max_kv_tarrif1 = data[10].split(" ")[0].replace("M(","");
        String eleventh_last_month_max_kv_tarrif2 = data[10].split(" ")[1];
        String eleventh_last_month_max_kv_tarrif3 = data[10].split(" ")[2];
        String eleventh_last_month_max_kv_tarrif4 = data[10].split(" ")[3];
        String eleventh_last_month_max_kv_tarrif5 = data[10].split(" ")[4];
        String eleventh_last_month_max_kv_tarrif6 = data[10].split(" ")[5];
        String eleventh_last_month_max_kv_tarrif7 = data[10].split(" ")[6];
        String eleventh_last_month_max_kv_tarrif8 = data[10].split(" ")[7];
        values[10]=eleventh_last_month_max_kv_tarrif1+";"+eleventh_last_month_max_kv_tarrif2+";"+eleventh_last_month_max_kv_tarrif3+";"+eleventh_last_month_max_kv_tarrif4+";"+eleventh_last_month_max_kv_tarrif5+";"+eleventh_last_month_max_kv_tarrif6+";"+eleventh_last_month_max_kv_tarrif7+";"+eleventh_last_month_max_kv_tarrif8;


        return values;
    }

    public String billing_history_max_demand_kv_date_and_time(){

        String[] data= getAlldata().substring(getAlldata().indexOf("N("),getAlldata().lastIndexOf("N(")).split("\\)");

        String max_kv_date_and_time= "";
        for(int i=3;i<=13;i++) {
            String date_and_time = data[i].replace("N(", "").trim();

            if(date_and_time.contains("162F2230")) {

                max_kv_date_and_time = max_kv_date_and_time +"0"+",";
            }
            else{

                String year = date_and_time.substring(0, 2);

                String month_and_day = date_and_time.substring(2, 5);

                String time_hh = date_and_time.substring(5, 6);

                String day_and_month_and_time = date_and_time(hex2decimal(month_and_day), time_hh);

                String day = day_and_month_and_time.split(",")[0];

                String month = day_and_month_and_time.split(",")[1];

                String time = day_and_month_and_time.split(",")[2];
                String time_mm = date_and_time.substring(6, 8);
                max_kv_date_and_time = max_kv_date_and_time + "" + day + "/" + month + "/" + year + " " + time + ":" + time_mm + ",";

            }
        }
        return max_kv_date_and_time;

    }

    public String tamper_data(){

        String data= getAlldata().substring(getAlldata().indexOf("O"),getAlldata().indexOf("SC("));

        String[] power_related_events=data.substring(data.indexOf("O"),data.indexOf("O$")).split("\\)");

        String power_related_events_values="";

        String total_power_on_time=power_related_events[0].replace("O","").replace("(","").trim();

        String billing_power_on_time=power_related_events[1].replace("O","").replace("(","").trim();

       // String total_count="";

      //  String total_power_off_time="";

        for(int i=0;i<power_related_events.length;i++){

               if(power_related_events[i].contains(" ")) {
                String[] power_events = power_related_events[i].split(" ");
                   if( i!=2 && power_events.length==2){

                       String date_and_time=power_events[0].replace("O","").replace("(","").trim();

                       date_and_time=date_and_time.substring(0,2)+"/"+date_and_time.substring(2,4)+"/"+date_and_time.substring(4,6)+" "+date_and_time.substring(6,8)+":"+date_and_time.substring(8,10);

                       String duration_in_minutes=power_events[1].trim();

                       power_related_events_values=power_related_events_values+"["+date_and_time.trim()+","+duration_in_minutes.trim()+"]";
                   }
                 /*  if(i==2){

                       // not using these variables. can be used according to need. they are part of tamper count.


                       total_count=power_events[0].replace("O","").replace("(","").trim();

                       total_power_off_time=power_events[1];

                   }
                   if(power_events.length==3){

                       // not using these variable. can be used according to need. these are part of tamper count.

                       String event_status=power_events[0].replace("O","").replace("(","").trim();

                       String count=power_events[1];

                       String duration_in_seconds=power_events[2];

                       // from here three values will be in array i.e. event_status, count,duration_in_seconds
                       power_related_events_values=power_related_events_values+"["+event_status+","+count+","+duration_in_seconds+"]";

                   }*/


            }




        }

        String[] voltage_related_events=data.substring(data.indexOf("O$"),data.lastIndexOf("O*")).split("\\)");

            String voltage_ralated_values="";
            String current_related_values="";
            for(int i=1;i<=516;i=i+6) {
                /*
                * 1 to 258 value will fall in category of voltage related events.
                * current related events will start from array index 259. we are merging all. according to requirement we can separate.
                * value will be same as in voltage_related_events.
                * */
                String voltage_date_and_time = voltage_related_events[i].split(" ")[1];

                String year = voltage_date_and_time.substring(0, 2);

                String month_and_day = voltage_date_and_time.substring(2, 5);

                String time_hh = voltage_date_and_time.substring(5, 6);

                String day_and_month_and_time = date_and_time(hex2decimal(month_and_day), time_hh);

                String day = day_and_month_and_time.split(",")[0];

                String month = day_and_month_and_time.split(",")[1];

                String time = day_and_month_and_time.split(",")[2];
                String time_mm = voltage_date_and_time.substring(6, 8);
                voltage_date_and_time = day + "/" + month + "/" + year + " " + time + ":" + time_mm;


                String duration_in_seconds = voltage_related_events[i].split(" ")[2];
                String occurance_of_kwh = voltage_related_events[i + 1].split("\\.")[0].replace("O*(", "");
                String recovery_of_kwh = voltage_related_events[i + 1].split("\\.")[1];

                String occurance_of_kvh = voltage_related_events[i + 2].split("\\.")[0].replace("O*(", "");
                String recovery_of_kvh = voltage_related_events[i + 2].split("\\.")[1];


                DecimalFormat df = new DecimalFormat("#.##");

                String R_phase_voltage_occurance = voltage_related_events[i + 3].split(" ")[0].replace("O*(", "");
                R_phase_voltage_occurance = "" + df.format(Float.parseFloat(R_phase_voltage_occurance) * 1.1765);

                String R_phase_current_occurance = voltage_related_events[i + 3].split(" ")[1];
                String R_phase_power_factor_occurance = voltage_related_events[i + 3].split(" ")[2];
                String R_phase_voltage_recovery = voltage_related_events[i + 3].split(" ")[3];
                R_phase_voltage_recovery = "" + df.format(Float.parseFloat(R_phase_voltage_recovery) * 1.1765);
                String R_phase_current_recovery = voltage_related_events[i + 3].split(" ")[4];
                String R_phase_power_factor_recovery = voltage_related_events[i + 3].split(" ")[5];


                String Y_phase_voltage_occurance = voltage_related_events[i + 4].split(" ")[0].replace("O*(", "");

                Y_phase_voltage_occurance = df.format(Float.parseFloat(Y_phase_voltage_occurance) * 1.1765);
                String Y_phase_current_occurance = voltage_related_events[i + 4].split(" ")[1];
                String Y_phase_power_factor_occurance = voltage_related_events[i + 4].split(" ")[2];
                String Y_phase_voltage_recovery = voltage_related_events[i + 4].split(" ")[3];

                Y_phase_voltage_recovery = df.format(Float.parseFloat(Y_phase_voltage_recovery) * 1.1765);
                String Y_phase_current_recovery = voltage_related_events[i + 4].split(" ")[4];
                String Y_phase_power_factor_recovery = voltage_related_events[i + 4].split(" ")[5];


                String B_phase_voltage_occurance = voltage_related_events[i + 5].split(" ")[0].replace("O*(", "");

                B_phase_voltage_occurance = df.format(Float.parseFloat(B_phase_voltage_occurance) * 1.1765);
                String B_phase_current_occurance = voltage_related_events[i + 5].split(" ")[1];
                String B_phase_power_factor_occurance = voltage_related_events[i + 5].split(" ")[2];
                String B_phase_voltage_recovery = voltage_related_events[i + 5].split(" ")[3];

                B_phase_voltage_recovery = df.format(Float.parseFloat(B_phase_voltage_recovery) * 1.1765);
                String B_phase_current_recovery = voltage_related_events[i + 5].split(" ")[4];
                String B_phase_power_factor_recovery = voltage_related_events[i + 5].split(" ")[5];

                if (i <= 258) {
                    voltage_ralated_values = voltage_ralated_values + "[" + voltage_date_and_time.trim() + "," + duration_in_seconds.trim() + ","
                            + occurance_of_kwh.trim() + "," + recovery_of_kwh.trim() + "," + occurance_of_kvh.trim() + "," + recovery_of_kvh.trim() + ","
                            + R_phase_voltage_occurance.trim() + "," + R_phase_current_occurance.trim() + "," + R_phase_power_factor_occurance.trim() + ","
                            + R_phase_voltage_recovery.trim() + "," + R_phase_current_recovery.trim() + "," + R_phase_power_factor_recovery.trim() + ","
                            + Y_phase_voltage_occurance.trim() + "," + Y_phase_current_occurance.trim() + "," + Y_phase_power_factor_occurance.trim() + ","
                            + Y_phase_voltage_recovery.trim() + "," + Y_phase_current_recovery.trim() + "," + Y_phase_power_factor_recovery.trim() + ","
                            + B_phase_voltage_occurance.trim() + "," + B_phase_current_occurance.trim() + "," + B_phase_power_factor_occurance.trim() + ","
                            + B_phase_voltage_recovery.trim() + "," + B_phase_current_recovery.trim() + "," + B_phase_power_factor_recovery.trim() + "]";


                    // current related events will start from array index 259. we are merging all. according to requirement we can separate.
                }

            else{
                 current_related_values=current_related_values+"[" + voltage_date_and_time.trim() + "," + duration_in_seconds.trim() + ","
                         + occurance_of_kwh.trim() + "," + recovery_of_kwh.trim() + "," + occurance_of_kvh.trim() + "," + recovery_of_kvh.trim() + ","
                         + R_phase_voltage_occurance.trim() + "," + R_phase_current_occurance.trim() + "," + R_phase_power_factor_occurance.trim() + ","
                         + R_phase_voltage_recovery.trim() + "," + R_phase_current_recovery.trim() + "," + R_phase_power_factor_recovery.trim() + ","
                         + Y_phase_voltage_occurance.trim() + "," + Y_phase_current_occurance.trim() + "," + Y_phase_power_factor_occurance.trim() + ","
                         + Y_phase_voltage_recovery.trim() + "," + Y_phase_current_recovery.trim() + "," + Y_phase_power_factor_recovery.trim() + ","
                         + B_phase_voltage_occurance.trim() + "," + B_phase_current_occurance.trim() + "," + B_phase_power_factor_occurance.trim() + ","
                         + B_phase_voltage_recovery.trim() + "," + B_phase_current_recovery.trim() + "," + B_phase_power_factor_recovery.trim() + "]";

                }
            }
        return  power_related_events_values+"__"+voltage_ralated_values+"__"+current_related_values;

    }

    public String[] load_survey(){
        String[] data=getAlldata().substring(getAlldata().indexOf("V("),getAlldata().indexOf("X(")).split("\\)");
        String total_no_of_interval= data[0];
        String date_and_time_of_reading=data[1];

String[] data2= new String[data.length-2];

        /*
        *  since this meter gives 65 days load survey. and according to reading time it reduces most last day intervals
        *  like if you are reading at 11:20 then total number of interval from 00:00 to 11:20 will be 22 . so 22 intervals will
        *  be from current day and 48-22= 26 interval will be from most last date of load survey.
        *  you will get data in reverse order means first will get most last day intervals.... in last current day intervals.
        *  while date and time order is vice versa. means first you will get current day date and time then previous day .. so on
        * */
        DecimalFormat df = new DecimalFormat("#.##");
        try{
        for(int i=2;i<data.length-1;i++) {

            if (data[i].contains("V(")) {
                data[i] = data[i].replace("V(", "").trim();

               // data[i - 2] = df.format(Float.parseFloat("" + hex2decimal(data[i].split(" ")[0])) * .0020408) + "," + data[i].split(" ")[1] + "," + data[i].split(" ")[2];

                data2[i-2] = df.format(Float.parseFloat("" + hex2decimal(data[i].split(" ")[0])) * .0020408) + "," + data[i].split(" ")[1] + "," + data[i].split(" ")[2];


            }


        }
        }catch (Exception e){
            e.getMessage();

        }


 return data2;
    }

    public String[] load_survey_dates()
    {
        String[] load_survey_dates=getAlldata().substring(getAlldata().indexOf("X("),getAlldata().indexOf("!")).split("\\)");

      String[] load_survey_dates_value=new String[load_survey_dates.length-1];
        for(int i=1;i<load_survey_dates.length;i++) {
                load_survey_dates[i-1] = "" +(hex2decimal(load_survey_dates[i].replace("X(", "").trim())-33);
                 String year=""+Integer.parseInt(load_survey_dates[i-1])/512;

                 String no_of_days=""+Integer.parseInt(load_survey_dates[i-1])%512;

                String month=""+((Integer.parseInt(no_of_days)/32)+1);

                String day=""+((Integer.parseInt(no_of_days)%32)+1);

               load_survey_dates_value[i-1]="["+day+"/"+month+"/"+year+"]";
        }

        return load_survey_dates_value;
    }

    public String date_and_time(int month_value, String time_value){

        int month=0;
        int time=0;
        int date=0;
        if(month_value%2==1){

            month_value=month_value-1;
            time_value=1+""+time_value;
            time=hex2decimal(time_value);
        }
        else{

            time=hex2decimal(time_value);
        }
        if(month_value >=66 && month_value<=129){
            month=1;
            date=((month_value-66)/2)+1;
        }

        else  if(month_value >=130 &&month_value<=193){
            month=2;
            date=((month_value-130)/2)+1;
        }

        else  if(month_value >=194 &&month_value<=257){
            month=3;
            date=((month_value-194)/2)+1;
        }
        else  if(month_value >=258 &&month_value<=321){
            month=4;
            date=((month_value-258)/2)+1;
        }
        else  if(month_value >=322 && month_value<=385){
            month=5;
            date=((month_value-322)/2)+1;
        }
        else  if(month_value >=386 && month_value<=449){
            month=6;
            date=((month_value-386)/2)+1;
        }
        else  if(month_value >=450 && month_value<=513){
            month=7;
            date=((month_value-450)/2)+1;
        }
        else  if(month_value>=514 && month_value<=577){
            month=8;
            date=((month_value-514)/2)+1;
        }
        else  if(month_value >=578 &&month_value<=641){
            month=9;
            date=((month_value-578)/2)+1;
        }
        else  if(month_value >=642 && month_value<=705){
            month=10;
            date=((month_value-642)/2)+1;
        }
        else  if(month_value >=706 && month_value<=769){
            month=11;
            date=((month_value-706)/2)+1;
        }
        else  if(month_value >=770 &&month_value<=834){
            month=12;
            date=((month_value-770)/2)+1;
        }


        return date+","+month+","+time;
    }

    private static String hexToAscii(String hxStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hxStr.length(); i += 2) {
            String str = hxStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public  int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    private  BaudRate getBaudRate() {
        try {
            String value = getSerialno().substring(4, 5);
            if (value != null) {
                switch (value.trim()) {
                    case "0":
                        return BaudRate.BAUD_RATE_300;
                    case "1":
                        return BaudRate.BAUD_RATE_600;
                    case "2":
                        return BaudRate.BAUD_RATE_1200;
                    case "3":
                        return BaudRate.BAUD_RATE_2400;
                    case "4":
                        return BaudRate.BAUD_RATE_4800;
                    case "5":
                        return BaudRate.BAUD_RATE_9600;
                    default:
                        return BaudRate.BAUD_RATE_9600;
                }

            }
        }catch (Exception e){
            e.getMessage();

        }
        return  BaudRate.BAUD_RATE_9600;
    }

}

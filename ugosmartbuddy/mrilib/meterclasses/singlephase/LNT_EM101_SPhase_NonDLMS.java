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

public class LNT_EM101_SPhase_NonDLMS {
    int Utility_Code;

    public LNT_EM101_SPhase_NonDLMS(int Utility_Code){
        this.Utility_Code=Utility_Code;

    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    String serialno, Alldata;
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial){
        Non_DLMS_Parameter non_dlms_parameter= new Non_DLMS_Parameter();
        try {
            read(serial);
            String s[] = required_data();
            String billingPeriod[]= s[1].split(" ");
            String billingDateTime = billingPeriod[0].substring(0, 2) + "/" + billingPeriod[0].substring(2, 4) + "/" + Utils.padString(billingPeriod[0].substring(4, 6)) + " " + billingPeriod[1].substring(0, 2) + ":" + billingPeriod[1].substring(2, 4) + ":" + billingPeriod[1].substring(4, 6);
            String date = Utils.correctDateFormatWithTime(billingDateTime);
            non_dlms_parameter.setMETER_NO(s[6]);
            non_dlms_parameter.setMANUFACTURE_NAME(s[0].split(",")[0]);
            non_dlms_parameter.setYEAR_OF_MANUFACTURE(s[0].split(",")[2]);
            non_dlms_parameter.setCURRENT_R_PHASE(s[5]);
            non_dlms_parameter.setVOLTAGE_R_PHASE(s[2]);
            non_dlms_parameter.setBILLING_DATE(date);
            non_dlms_parameter.setACTIVE_ENERGY(s[3]);
            // non_dlms_parameter.setAPPARENT_ENERGY();
            // non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY();
            double maxDemandKw = Utils.getDoubleValue(s[4]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(String.valueOf(maxDemandKw));
        }catch (Exception e){
            e.getMessage();
        }
        finally {
            try {
                if (WifiConnectivity.getIsWifi())

                    if (WifiConnectivity.getConnectivity() != null)
                        WifiConnectivity.getConnectivity().DisconnectWifi();

            } catch (Exception e) {
                e.getMessage();
            }
        }
        return  non_dlms_parameter;
    }

    public String getAlldata() throws Exception {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }

    public void read(GXSerial serial) {
        LNT_EM101_96_SPhase_Non_DLMS lnt_em101_96_sPhase_non_dlms = new LNT_EM101_96_SPhase_Non_DLMS();
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_4800);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);
            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(4800, 7, 2));
                    this.wait(1000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                }
            }

            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<>(byte[].class);
                if (WifiConnectivity.getIsWifi())
                    WifiConnectivity.mReceiver.resetBytesReceived();
                p.setAllData(false);
                p.setEop(null);
                p.setCount(1);
                p.setWaitTime(30000);
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
                    }
                    if (!serial.receive(p)) {
                        synchronized (lnt_em101_96_sPhase_non_dlms){
                            lnt_em101_96_sPhase_non_dlms.read(serial,p);
                            String a= lnt_em101_96_sPhase_non_dlms.read_instantaneous(serial,p);
                            lnt_em101_96_sPhase_non_dlms.read(serial,p);
                            String b= lnt_em101_96_sPhase_non_dlms.read_billing(serial,p);
                            if(a!=null && b!=null) {
                                setAlldata(a + b);
                            }else {
                                throw new Exception("Please Try Again");
                            }
                        }
                    }
                    else{
                        synchronized (this) {
                            read_(serial, p);
                        }

                    }
                }

            }


        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void read_(GXSerial serial,ReceiveParameters<byte[]> p) {
        try {
            serial.close();
            serial.setBaudRate(BaudRate.BAUD_RATE_4800);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);
            synchronized (this) {
                serial.open();
            }

            synchronized (this) {
                if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(4800, 7, 2));
                    this.wait(30000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                }
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                p.setAllData(false);
                p.setEop(null);
                p.setCount(1);
                p.setWaitTime(1000);
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

                    }
                    p.setReply(null);
                    synchronized (this) {
                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);
                        p.setCount(0);
                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setSerialno(GXCommon.bytesToHex(p.getReply()));
                    }
                    p.setReply(null);
                    synchronized (this) {
                     //   byte[] second_packet={0x06, 0x30, 0x34, 0x33, 0x0D, 0x0A};
                        byte[] second_packet={0x06, 0x30, 0x34, 0x30, 0x0D, 0x0A};
                        serial.send(second_packet, null);
                        p.setEop(new byte[]{0x0d,0x0a,0x21,0x0d,0x0a});
                        p.setCount(0);
                        serial.send(second_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                       setAlldata(GXCommon.bytesToHex(p.getReply()));

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
            synchronized (this) {
                p.setReply(null);
                byte eop = 0x21;
                p.setEop(eop);
                byte current_billing = 0x50;
                serial.send(current_billing, null);
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


    public String  read_tamper_data(GXSerial serial){
        StringBuilder tamper_builder= new StringBuilder();
        try{
            serial.setBaudRate(BaudRate.BAUD_RATE_4800);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);
            synchronized (this) {
                serial.open();
            }
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop(null);
                p.setCount(1);
                p.setWaitTime(1000);
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
                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);
                        p.setCount(0);
                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        GXCommon.bytesToHex(p.getReply());
                    }
                    p.setReply(null);
                    synchronized (this) {
                        p.setEop(null);
                        p.setCount(2752);
                        byte[] second_packet = {0x06, 0x30, 0x34, 0x33, 0x0D, 0x0A};
                        serial.send(second_packet, null);
                        byte tamper_packet = 0x54;
                        serial.send(tamper_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        tamper_builder.append( hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ","")));





                    }




                }


            }

        }catch(Exception e){

            e.getMessage();
        }
        return all_tamper_data(tamper_builder.toString());

    }

    public String all_tamper_data(String tamper_value){

        String [] all_tamper_events=tamper_value.toString().split("O");

        String power_related_events=""; // will get occurance and restoration date and time.
        String neutral_missing_count_events="";
        String current_reversal_count_events="";
        String neutral_disturbance_count_events="";

        /* for last three events
        *   will get duration in minute, date, time, occurance kwh, voltage, current, powerfactor
            then restoration kwh, voltage, current, powerfactor
             divide all by 100
        * */

        for(int i=3;i<=93;i++) {


            if(i>=3 && i<=22 )
                power_related_events = power_related_events+all_tamper_events[i].trim().replaceAll("\\(","").replaceAll("\\)"," ");

            if(i>=42 && i<=56)
                neutral_missing_count_events = neutral_missing_count_events+all_tamper_events[i].trim().replaceAll("\\(","").replaceAll("\\)"," ");
            if(i>=59 && i<=73)
                neutral_disturbance_count_events =neutral_disturbance_count_events+all_tamper_events[i].trim().replaceAll("\\(","").replaceAll("\\)"," ").replaceAll("\\!","");
            if(i>=79 && i<=93)
                current_reversal_count_events = current_reversal_count_events+all_tamper_events[i].trim().replaceAll("\\(","").replaceAll("\\)"," ").replaceAll("\\!","");


        }
        // split all values by " " i.e space

        return power_related_events+"new"+neutral_missing_count_events+"new"+neutral_disturbance_count_events+"new"+current_reversal_count_events.trim();

    }

    public String  read_load_survey_data(GXSerial serial){
        StringBuilder tamper_builder= new StringBuilder();
        try{
            serial.setBaudRate(BaudRate.BAUD_RATE_4800);
            serial.setDataBits(7);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.EVEN);
            synchronized (this) {
                serial.open();
            }
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop(null);
                p.setCount(1);
                p.setWaitTime(1000);
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
                        byte[] first_packet = {0x2F, 0x3F, 0x21, 0x0D, 0x0A};
                        p.setEop((byte) 0x0A);
                        p.setCount(0);
                        serial.send(first_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        GXCommon.bytesToHex(p.getReply());
                    }
                    p.setReply(null);
                    synchronized (this) {
                        p.setEop((byte)0x21);
                        //  p.setCount(2752);
                        byte[] second_packet = {0x06, 0x30, 0x34, 0x32, 0x0D, 0x0A};
                        serial.send(second_packet, null);

                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        tamper_builder.append( hexToAscii(GXCommon.bytesToHex(p.getReply()).replaceAll(" ","")));

                        String dates=   convert_load_date(tamper_builder.toString().substring(tamper_builder.indexOf("X")).split("X"));

                        String load_values=  load_survey_values((tamper_builder.toString().substring(tamper_builder.indexOf("Z"),tamper_builder.indexOf("X"))).trim().split("Z"));

                        tamper_builder= new StringBuilder();

                        tamper_builder.append(dates+"new"+load_values);

                    }
                }


            }

        }catch(Exception e){

            e.getMessage();
        }
        return tamper_builder.toString();

    }

    public String convert_load_date(String[] dates){

        String values ="";

        for(int i=2;i<dates.length;i++){

            String binary= hexToBinary(dates[i].replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\!","").toString().trim());

            values=values+""+ Integer.parseInt(binary.substring(9,14),2) +"/"+ Integer.parseInt(binary.substring(5,9),2)+"/"+
                    Integer.parseInt(binary.substring(0,5),2)+",";

        }

        return values;

    }


    public  String load_survey_values(String[] load){

        String values="", last_interval_datetime="";

        String last_interval_date_time=load[2].replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\!","").toString().trim();
        last_interval_datetime = last_interval_datetime+""+ Integer.parseInt(last_interval_date_time.substring(0,2)) +"/"+ Integer.parseInt(last_interval_date_time.substring(2,4))+"/"+
                Integer.parseInt(last_interval_date_time.substring(4,6))+" "+Integer.parseInt(last_interval_date_time.substring(6,8))+":"+
                Integer.parseInt(last_interval_date_time.substring(8,10))+":"+Integer.parseInt(last_interval_date_time.substring(10,12));

        for(int i=3;i<load.length;i++){

            values=values+""+Double.parseDouble(""+Integer.parseInt(load[i].replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\!","").toString().trim(),16))/100+",";

        }

        return values+"new"+last_interval_datetime;

    }

    String hexToBinary(String hex) {
        int i = Integer.parseInt(hex, 16);
        String bin = Integer.toBinaryString(i);
        return bin;
    }

    public String serialno(){
        String s_no= getSerialno();
        s_no=s_no.replaceAll(" ","");
        s_no= hexToAscii(s_no);
        s_no=s_no.substring(5);
        return s_no;
    }

    public  String[] required_data() throws Exception {
        //all_data.split("M\\(")[1].split(" ")[1].split("\\)")[0]
        //value-1;
        String[] perfect_data = null;
        String all_data= getAlldata();
        if(all_data!=null) {
            try {
                String Instant_voltage = null;
                String Instant_current = null;
                String active_value_KWH = null;
                String max_Demand_Kw_history = null;
                all_data = all_data.replaceAll(" ", "");
                all_data = hexToAscii(all_data);
                all_data = all_data.replaceAll("\\(", "");
                String[] data_array = all_data.split("\\)");
                String[] parsed_values=  parse(all_data,Integer.parseInt(data_array[6].split(" ")[0].trim().replace("H","")),Integer.parseInt(data_array[6].split(" ")[0].trim().replace("H","")));
                String manufacture_name = data_array[0].replace("H", "").trim();
                String serial_no = data_array[1].replace("H", "").trim();
                String reading_date_time = data_array[2].replace("H", "").trim();
                int mf_KWH=  Integer.parseInt(data_array[8].substring(6,8).trim());
                active_value_KWH= Mulitply_with_MF(mf_KWH,parsed_values[0]);
                int mf_MaxD= Integer.parseInt(data_array[8].substring(9,11).trim());// for maxD
                max_Demand_Kw_history=Mulitply_with_MF(mf_MaxD,parsed_values[1]);
                Instant_current = "" + Double.parseDouble(parsed_values[3]) / 100;
                Double instant_voltage = Double.parseDouble(parsed_values[2]) / 100;
                Instant_voltage = "" + instant_voltage;

                while (active_value_KWH.length() > 0 && active_value_KWH.charAt(0) == '0') {
                    active_value_KWH = active_value_KWH.substring(1);
                }

                perfect_data = new String[]{manufacture_name, reading_date_time, Instant_voltage, active_value_KWH, max_Demand_Kw_history, Instant_current, serial_no};
            }catch (Exception e){
                e.getMessage();
            }
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

    private int getMf(int value){

        switch (value){
            case 0:
                        return 0;
            case 1:
                         return 10;
            case 2:
                return 100;
            case 3:
                return 1000;
            case 4:
                return 10000;
            case 5:
                return 100000;
            default:
                return 0;
        }
    }

    private String Mulitply_with_MF( int mf, String value){
        String val=null;
        try {
            if(mf!=0)
            mf= getMf(mf);
            if(value!=null){
                if(mf!=0)
                return ""+Double.parseDouble(value)/mf;
                else
                  return   ""+Double.parseDouble(value);
            }
        }
        catch (Exception e){

            e.getMessage();
        }

        return val;
    }

    private String[] parse(String alldata,int no_of_tod, int tod_sequence){
        String[] returnValues=null;
        try{
         String[] instantaneous_array=alldata.substring(alldata.indexOf("U"),alldata.lastIndexOf("U")).split("\\)");
            String voltage=   instantaneous_array[0].replace("U","").trim();
            String current=   instantaneous_array[1].replace("U","").trim();
            if(current.contains(" "))
                current=current.split(" ")[0];
            String[] MaxD_array= alldata.substring(alldata.indexOf("M12"),alldata.indexOf("PF")).split("\\)");
            int maxD_index= Integer.parseInt(alldata.substring(alldata.indexOf("M12"),alldata.indexOf("PF")).split("\\)")[0].split(" ")[1]);
            int max_Tod=0;
            for(int i=0;i<no_of_tod;i++){
                int Tod  =Integer.parseInt(MaxD_array[maxD_index+i].split(" ")[0].trim().replace("M",""));
                if(max_Tod<Tod)
                    max_Tod=Tod;
            }
            String MaxD_KW=""+max_Tod;
            String active_energy=alldata.substring(alldata.indexOf("II"),alldata.indexOf("JI")).replace("II","").split("\\)")[0];
            returnValues=new String[]{active_energy,MaxD_KW,voltage,current};
        }catch (Exception e){
            e.getMessage();
        }

        return returnValues;
    }
}
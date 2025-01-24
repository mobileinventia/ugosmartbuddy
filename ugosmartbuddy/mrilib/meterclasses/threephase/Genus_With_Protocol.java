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

public class Genus_With_Protocol {
    byte header_byte=0x01;
    byte sign_on_byte=0x3f;
    byte blank_byte=(byte)0xff;
    byte[] utility_code_byte={0x50, 0x56, 0x4e, 0x4c}; //PVNL
    byte ascii_byte=0x42;
    byte disc_byte=0x1b;
    byte[] dataValue={0x01,0x03,0x04,0x05,0x06,0x12};
    private int Utility_Code;

    public Genus_With_Protocol(int Utility_Code){
        this.Utility_Code=Utility_Code;
    }

    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial){
        Non_DLMS_Parameter non_dlms_parameter=null;
        try {
             String response_data = read(serial);
             non_dlms_parameter=Parser(response_data);
            non_dlms_parameter.setMANUFACTURE_NAME("GENUS");
        }catch (Exception e){
            e.getMessage();
        }
        return non_dlms_parameter;
    }

    private byte[] add_Header(byte[] arr_val){
        byte[] complete_packet=new byte[arr_val.length+1];
        complete_packet[0]=0x01;
        System.arraycopy(arr_val,0,complete_packet,1,complete_packet.length-1);
       return complete_packet;
    }

    private byte[] sign_on(){
        byte[] singOn_packet=null;
        try{
             singOn_packet=new byte[]{
                     sign_on_byte,
                     utility_code_byte[0],
                     utility_code_byte[1],
                     utility_code_byte[2],
                     utility_code_byte[3],
                     blank_byte,
                     blank_byte,
                     blank_byte,
                     blank_byte};

            singOn_packet[singOn_packet.length-1]=(byte) BinaryToInt(CalculateTwosCompliment(hexToBinary(getSum(singOn_packet))));
           return add_Header(singOn_packet);
        }
        catch (Exception ex){

            ex.getMessage();
            return singOn_packet;
        }

    }

    private byte[] connection_packet(String respone){
        byte[] connection_packet=null;
        try {
            respone=respone.replaceAll(" ","").substring(18,20);
            connection_packet= new byte[]{
                    ascii_byte,
                    (byte) Integer.parseInt(respone,16),
                    (byte) 0xEF,
                    (byte) 0xEF,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte
            };
            connection_packet[connection_packet.length-1]=(byte)BinaryToInt(CalculateTwosCompliment(hexToBinary(getSum(connection_packet))));
            return add_Header(connection_packet);
        }catch (Exception e){
            e.getMessage();
            return connection_packet;
        }


    }

    private byte[] disconnection_packet(){
        byte[] disc_packet=null;
        try{
            disc_packet=new byte[]{
                    disc_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte};
            disc_packet[disc_packet.length-1]=(byte) BinaryToInt(CalculateTwosCompliment(hexToBinary(getSum(disc_packet))));
            return add_Header(disc_packet);
        }
        catch (Exception ex){

            ex.getMessage();
            return disc_packet;
        }

    };

    private byte[] Single_respone__packet(byte rValue,byte dataValue){
        byte [] reading_packet=null;
        try {
            reading_packet=new byte[]{
                    rValue,
                    0x01,
                    dataValue,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte
            };
            reading_packet[reading_packet.length-1]=(byte)BinaryToInt(CalculateTwosCompliment(hexToBinary(getSum(reading_packet))));
            return add_Header(reading_packet);
        }catch (Exception e){

            e.getMessage();
            return reading_packet;
        }



    }

    private byte[] group_respone__packet(byte rValue,byte dataValue){
        byte [] reading_packet=null;
        try {
            reading_packet=new byte[]{
                    rValue,
                    0x01,
                    dataValue,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte,
                    blank_byte
            };
            reading_packet[reading_packet.length-1]=(byte)BinaryToInt(CalculateTwosCompliment(hexToBinary(getSum(reading_packet))));
            return add_Header(reading_packet);

        }catch (Exception e){

            e.getMessage();
            return reading_packet;
        }

    }

    private String read(GXSerial serial) {
        StringBuilder builder= new StringBuilder();
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
                p.setEop(null);
                p.setCount(20);
                p.setWaitTime(3000);
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        serial.send(sign_on(), null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    byte[] connection_packet= connection_packet(GXCommon.bytesToHex(p.getReply()));
                    p.setReply(null);
                    synchronized (this) {
                        this.wait(100);
                        p.setCount(2);
                        serial.send(connection_packet, null);
                        this.wait(150);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    if(GXCommon.bytesToHex(p.getReply()).replaceAll(" ","").contains("6"))
                    {
                        synchronized (this) {
                            for (int i = 0; i < dataValue.length; i++) {
                                this.wait(100);
                                p.setReply(null);
                                byte[] second_packet = Single_respone__packet((byte) 0x72, dataValue[i]);
                                p.setCount(20);
                                serial.send(second_packet, null);
                                this.wait(150);

                            if (!serial.receive(p)) {

                            }
                            builder.append(GXCommon.bytesToHex(p.getReply())+"new");
                            }
                            byte[] billing_history_packet = {0x01, 0x72, 0x03, 0x00, 0x00, 0x00, (byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0x0D};
                            p.setReply(null);
                            p.setCount(2433);
                            serial.send(billing_history_packet, null);
                            this.wait(150);
                            if (!serial.receive(p)) {
                            }
                            builder.append(GXCommon.bytesToHex(p.getReply())+"new");
                        }
                    }

                }

            }

        } catch (Exception e)
        {
            e.getMessage();
        }

       return builder.toString();
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

    private static String hexToBinary(String hex) {
        String bin=null;
        if(hex!=null) {
            int i = Integer.parseInt(hex, 16);
             bin = Integer.toBinaryString(i);
            if (bin.length() < 8) {
                int length = 8 - bin.length();
                for (int j = 0; j < length; j++) {
                    bin = "0" + bin;
                }
            }
        }
        return bin;
    }

    private static int BinaryToInt(String binary){
       int crc=0;
        if(binary!=null) {
            int decimal = Integer.parseInt(binary, 2);
            //String hexStr = Integer.toString(decimal, 16);
             // crc =  Integer.parseInt(hexStr);
            crc=decimal;
        }
        return crc;
    }

    private static String getSum(byte[] arrayToSum){
        String hexVal=null;
        int sum=0;
        if(arrayToSum.length>0) {
            for (int i = 0; i < arrayToSum.length-1; i++) {
                sum = sum + (int) arrayToSum[i];
            }
        }
        if(sum>0) {
             hexVal = Integer.toHexString(sum);
            if (hexVal.length() > 2){
             hexVal= hexVal.substring(1);
            }
        }
        return hexVal;
    }

    private Non_DLMS_Parameter Parser(String data){
        Non_DLMS_Parameter non_dlms_parameter=new Non_DLMS_Parameter();
        try {
            if (data != null) {
                String[] packets= data.split("new");
                for(int i=0;i<packets.length;i++){
                  String identifer= packets[i].substring(6,8);
                    switch(identifer){
                        case "01":
                            String meter_no= ""+ Utils.hex2decimal(packets[i].substring(12,23).replaceAll(" ",""));
                            non_dlms_parameter.setMETER_NO(meter_no);
                            break;
                        case "03":
                            String meter_rtc = packets[i].replaceAll(" ", "").substring(8, 20);
                            meter_rtc = meter_rtc.substring(10, 12) + "/" + meter_rtc.substring(8, 10) + "/" + meter_rtc.substring(6, 8) + " " + meter_rtc.substring(4, 6) + ":" + meter_rtc.substring(2, 4) + ":" + meter_rtc.substring(0, 2);
                            non_dlms_parameter.setBILLING_DATE(Utils.correctDateFormatWithTime(meter_rtc));
                            break;
                        case "04":
                            String Vb= ""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(12,17).replaceAll(" ","")))/10;
                            String Vy= ""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(18,23).replaceAll(" ","")))/10;
                            String Vr= ""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(24,29).replaceAll(" ","")))/10;
                            String Ib= ""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(30,35).replaceAll(" ","")))/100;
                            String Iy= ""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(36,41).replaceAll(" ","")))/100;
                            String Ir= ""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(42,47).replaceAll(" ","")))/100;
                            non_dlms_parameter.setVOLTAGE_B_PHASE(Vb);
                            non_dlms_parameter.setVOLTAGE_Y_PHASE(Vy);
                            non_dlms_parameter.setVOLTAGE_R_PHASE(Vr);
                            non_dlms_parameter.setCURRENT_B_PHASE(Ib);
                            non_dlms_parameter.setCURRENT_Y_PHASE(Iy);
                            non_dlms_parameter.setCURRENT_R_PHASE(Ir);
                            break;
                        case "05":
                            String kwh=""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(12,21).replaceAll(" ","")))/10;
                            non_dlms_parameter.setACTIVE_ENERGY(kwh);
                            break;
                        case "06":
                            String kvah=""+Double.parseDouble(""+ Utils.hex2decimal(packets[i].substring(12,21).replaceAll(" ","")))/10;
                            non_dlms_parameter.setAPPARENT_ENERGY(kvah);
                            break;
                        case "12":
                            // not using current month demand...
                           // String MD_KW=""+Utility.hex2decimal(packets[i].substring(12,17).replaceAll(" ",""));
                           // String MD_KVA=""+Utility.hex2decimal(packets[i].substring(36,41).replaceAll(" ",""));
                          /*  String MD_KW_Date=packets[i].substring(18,20);
                            packets[i].substring(21,23);
                            packets[i].substring(24,26);
                            packets[i].substring(27,29);
                            packets[i].substring(30,32);
                            packets[i].substring(33,35);*/
                           /* String MD_KVA_Date=packets[i].substring(42,44); // min
                            packets[i].substring(45,47); //hour
                            packets[i].substring(48,50); // year
                            packets[i].substring(51,53); // month
                            packets[i].substring(54,56); // date
                            packets[i].substring(57,59);*/
                            break;
                        case "00":
                            String[] historyData = billing_history_data(packets[i]).split("new");
                            String[] kw_values = historyData[0].split(",");
                            String[] kva_values = historyData[1].split(",");
                            non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY(kva_values[1]);
                            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(kw_values[2]);
                            break;
                    }
                }
            }

        }catch (Exception e){

            e.getMessage();
        }
 return non_dlms_parameter;
    }

    private String billing_history_data(String response) {

        String[] data = billing_history_all(response);

        if(data!=null) {
            String billing_date;
            String maximum_demand_kw = "", maximum_demand_kw_Date_and_time = "", maximum_demand_kv, maximum_demand_kv_date;
            String active_energy_kw = "", apparent_energy, power_factor;


            String kw_values = "";
            String kv_values = "";
            for (int i = 0; i < 12; i++) {
                maximum_demand_kw = "" + Utils.hex2decimal(data[i].substring(6, 10));
                maximum_demand_kw = "" + Double.parseDouble(maximum_demand_kw) / 1000;
                maximum_demand_kw_Date_and_time = data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + " " + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);
                active_energy_kw = "" + Utils.hex2decimal(data[i].substring(20, 26));
                active_energy_kw = "" + Double.parseDouble(active_energy_kw) / 10;
                billing_date = data[i].substring(32, 34) + "/" + data[i].substring(30, 32) + "/" + data[i].substring(28, 30);
                kw_values = kw_values + billing_date + "," + active_energy_kw + "," + maximum_demand_kw + "," + maximum_demand_kw_Date_and_time + ",";

            }

            for (int i = 16; i < 28; i++) {
                maximum_demand_kv = "" + Utils.hex2decimal(data[i].substring(6, 10));
                maximum_demand_kv = "" + Double.parseDouble(maximum_demand_kv) / 1000;
                maximum_demand_kv_date = data[i].substring(18, 20) + "/" + data[i].substring(16, 18) + "/" + data[i].substring(14, 16) + " " + data[i].substring(12, 14) + ":" + data[i].substring(10, 12);
                apparent_energy = "" + Utils.hex2decimal(data[i].substring(20, 26));
                apparent_energy = "" + Double.parseDouble(apparent_energy) / 10;
                power_factor = "" + Double.parseDouble("" + Utils.hex2decimal(data[i].substring(26, 30))) / 1000;
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

}

package com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase;

import com.inventive.ugosmartbuddy.mrilib.common.Non_DLMS_Parameter;
import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.CRC16;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

public class HPL_TPhase_JVVNL {

    byte STX=(byte) 0xFE;
    byte ETX=(byte) 0xFF;
    byte ZERO=0x00;
    byte ONE=0x01;
    byte TWO= 0x02;
    int Utility_Code;
    String current_month, last_month, second_last_month, third_last_month, fourth_last_month, fifth_last_month, sixth_last_month,
            seventh_last_month, eighth_last_month, ninth_last_month, tenth_last_month, eleventh_last_month;
    int no_of_month_data_available;
    String first_packet_response;
    String fourth_packet_formation_packet;
    String fourth_packet_response;
    String third_packet_formation_packet;
    String third_packet_response;
    String serialno;
    public HPL_TPhase_JVVNL(int Utility_Code){
        this.Utility_Code=Utility_Code;

    }
    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try{
            read(serial);
            String[] general_data= serial_no_year_of_mfg();
            String[] instantenous_data = instantaneous_parameters();
            String[] current_data = billing_history_parser(getCurrent_month());
            String[] history_data = billing_history_parser(getLast_month());
            String date = Utils.correctDateFormatWithTime(general_data[2]);
            non_dlms_parameter.setMETER_NO(general_data[0]);
            non_dlms_parameter.setMANUFACTURE_NAME("HPL");
            non_dlms_parameter.setYEAR_OF_MANUFACTURE(general_data[1]);
            non_dlms_parameter.setCURRENT_R_PHASE(instantenous_data[0]);
            non_dlms_parameter.setCURRENT_Y_PHASE(instantenous_data[1]);
            non_dlms_parameter.setCURRENT_B_PHASE(instantenous_data[2]);
            non_dlms_parameter.setVOLTAGE_R_PHASE(instantenous_data[3]);
            non_dlms_parameter.setVOLTAGE_Y_PHASE(instantenous_data[4]);
            non_dlms_parameter.setVOLTAGE_B_PHASE(instantenous_data[5]);
            non_dlms_parameter.setBILLING_DATE(date);

            non_dlms_parameter.setACTIVE_ENERGY(current_data[0]);
            non_dlms_parameter.setAPPARENT_ENERGY(current_data[1]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY(history_data[6]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(history_data[4]);
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

    public String getThird_packet_response() {
        return third_packet_response;
    }

    public void setThird_packet_response(String third_packet_response) {
        this.third_packet_response = third_packet_response;
    }

    public String getThird_packet_formation_packet() {
        return third_packet_formation_packet;
    }

    public void setThird_packet_formation_packet(String third_packet_formation_packet) {
        this.third_packet_formation_packet = third_packet_formation_packet;
    }

    public String getFourth_packet_response() {
        return fourth_packet_response;
    }

    public void setFourth_packet_response(String fourth_packet_response) {
        this.fourth_packet_response = fourth_packet_response;
    }

    public String getFourth_packet_formation_packet() {
        return fourth_packet_formation_packet;
    }

    public void setFourth_packet_formation_packet(String fourth_packet_formation_packet) {
        this.fourth_packet_formation_packet = fourth_packet_formation_packet;
    }

    public String getFirst_packet_response() {
        return first_packet_response;
    }

    public void setFirst_packet_response(String first_packet_response) {
        this.first_packet_response = first_packet_response;
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
                p.setCount(13);
                p.setWaitTime(3000);
                byte[] data = {STX, 0x40, ZERO, ONE, ZERO, ETX};
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Meter is not Responding.");
                    }
                    setFirst_packet_response(GXCommon.bytesToHex(p.getReply()));

                    p.setReply(null);
                    synchronized (this) {
                        p.setCount(315);
                        serial.send(prepare_second_packet(ZERO), null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Meter is not Responding.");
                    }
                    setSerialno(GXCommon.bytesToHex(p.getReply()));

                    p.setReply(null);
                    synchronized (this) {
                        byte[] secondPacket = {STX, TWO, TWO, TWO, TWO, ETX};
                        p.setCount(32);
                        serial.send(secondPacket, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Meter is not Responding.");
                    }
                    setFirst_packet_response(GXCommon.bytesToHex(p.getReply()).substring(3));
                    p.setReply(null);
                    synchronized (this) {
                        p.setCount(391);
                        serial.send(prepare_second_packet(TWO), null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Meter is not Responding.");
                    }
                    setFourth_packet_response(GXCommon.bytesToHex(p.getReply()));
                }
            }

            billing_data_JVVNL();
            billing_history_parser_JVVNL(getCurrent_month());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void read_billing_data(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_9600);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);
            synchronized (this) {
                if (!serial.isOpen()) {
                    serial.open();
                }
            }
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);

                p.setWaitTime(10000);
                synchronized (serial.getSynchronous()) {
                    // fourth packet for billing data
                    synchronized (this) {
                        byte[] fourth_packet = {(byte) 0xFE, 0x02, 0x02, 0x02, 0x02, (byte) 0xFF};
                        p.setCount(42);
                        if(Utility_Code==2)
                            p.setCount(32);
                        serial.send(fourth_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setFourth_packet_formation_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    p.setReply(null);

                    synchronized (this) {
                        p.setCount(615);
                        if(Utility_Code==2)
                            p.setCount(395);
                        serial.send(prepare_fourth_packet(), null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setFourth_packet_response(GXCommon.bytesToHex(p.getReply()));
                    }
                    billing_data();
                }
            }

        } catch (Exception e) {
        }
    }

    public String[] serial_no_year_of_mfg() throws Exception{

        String[] data=null;
        if(getSerialno()!=null) {
            String rtc = "" + hex2decimal(getSerialno().replaceAll(" ", "").substring(10, 12)) + "/" + hex2decimal(getSerialno().replaceAll(" ", "").substring(12, 14))
                    + "/" + hex2decimal(getSerialno().replaceAll(" ", "").substring(14, 16)) + " " +
                    hex2decimal(getSerialno().replaceAll(" ", "").substring(16, 18)) + ":" +
                    hex2decimal(getSerialno().replaceAll(" ", "").substring(18, 20));

            String[] first_packet_data = getFirst_packet_response().split(" ");
            String s_no = "" + hex2decimal(first_packet_data[3].trim() + first_packet_data[4].trim() + first_packet_data[5].trim() + first_packet_data[6].trim());
            //String year_of_mfg = "M/Yr." + " " + hex2decimal(first_packet_data[8].trim() + first_packet_data[9].trim());
            String year_of_mfg ="" + hex2decimal(first_packet_data[8].trim() + first_packet_data[9].trim());
            data = new String[]{s_no, year_of_mfg, rtc};
        }
        return data;
    }

    public byte[] prepare_second_packet(byte packetID) {
        String[] first_packet_data = getFirst_packet_response().split(" ");
        String Challenge_Byte=first_packet_data[1].trim();
        int second_packet_d = calculate_ChallengeByte(Challenge_Byte);
        byte b = (byte) (second_packet_d);
        int[] data_to_calculate_crc = {STX, b, packetID};
        byte[] second_packet = {STX, b, packetID, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0],ETX};
        return second_packet;
    }

    public byte[] prepare_third_packet() {
        String[] first_packet_data = getThird_packet_formation_packet().split(" ");
        int value = hex2decimal(first_packet_data[1].trim());
        int second_packet_d = (261 - value);
        byte b = (byte) (second_packet_d);

        int[] data_to_calculate_crc = {0xFE, b, 0x01};
        byte[] third_packet = {(byte) 0xFE, b, 0x01, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0], (byte) 0xFF};
        return third_packet;
    }

    public byte[] prepare_fourth_packet() {
        String[] first_packet_data = getFourth_packet_formation_packet().split(" ");
        int value = hex2decimal(first_packet_data[1].trim());
        if(Utility_Code==2)
            value=hex2decimal(first_packet_data[2].trim());

        int second_packet_d = (261 - value);
        byte b = (byte) (second_packet_d);

        int[] data_to_calculate_crc = {0xFE, b, 0x02};
        byte[] fourth_packet = {(byte) 0xFE, b, 0x02, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0], (byte) 0xFF};
        return fourth_packet;
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

    public byte[] crc16mod(int[] data_to_calculate_crc) {

        CRC16 crc16 = new CRC16();

        for (int d : data_to_calculate_crc) {
            crc16.update(d);
        }

        byte lower = (byte) ((crc16.getValue() & 0x000000ff));
        byte upper = (byte) ((crc16.getValue() & 0x0000ff00) >>> 8);
        byte[] crcarray = {upper, lower};

        return crc16.getCrcBytes();

    }


    // now starting parsing.....

    public String[] instantaneous_parameters() {

        String[] instantaneous = getThird_packet_response().split(" ");

        String voltage_phase1 = "" + hex2decimal(instantaneous[1].trim() + instantaneous[2].trim());

        voltage_phase1 = "" + Double.parseDouble(voltage_phase1) / 100;

        String voltage_phase2 = "" + hex2decimal(instantaneous[3].trim() + instantaneous[4].trim());

        voltage_phase2 = "" + Double.parseDouble(voltage_phase2) / 100;

        String voltage_phase3 = "" + hex2decimal(instantaneous[5].trim() + instantaneous[6].trim());

        voltage_phase3 = "" + Double.parseDouble(voltage_phase3) / 100;

        String current_phase1 = "" + hex2decimal(instantaneous[9].trim() + instantaneous[10].trim());

        current_phase1 = "" + Double.parseDouble(current_phase1) / 1000;

        String current_phase2 = "" + hex2decimal(instantaneous[13].trim() + instantaneous[14].trim());
        current_phase2 = "" + Double.parseDouble(current_phase2) / 1000;

        String current_phase3 = "" + hex2decimal(instantaneous[17].trim() + instantaneous[18].trim());

        current_phase3 = "" + Double.parseDouble(current_phase3) / 1000;

        String[] data = {current_phase1, current_phase2, current_phase3, voltage_phase1, voltage_phase2, voltage_phase3};
        return data;


    }

    public void setNo_of_month_data_available(int no_of_month_data_available) {
        this.no_of_month_data_available = no_of_month_data_available;
    }

    public String getCurrent_month() {
        return current_month;
    }

    public void setCurrent_month(String current_month) {
        this.current_month = current_month;
    }

    public String getLast_month() {
        return last_month;
    }

    public void setLast_month(String last_month) {
        this.last_month = last_month;
    }

    public void setSecond_last_month(String second_last_month) {
        this.second_last_month = second_last_month;
    }

    public void setThird_last_month(String third_last_month) {
        this.third_last_month = third_last_month;
    }

    public void setFourth_last_month(String fourth_last_month) {
        this.fourth_last_month = fourth_last_month;
    }

    public void setFifth_last_month(String fifth_last_month) {
        this.fifth_last_month = fifth_last_month;
    }

    public void setSixth_last_month(String sixth_last_month) {
        this.sixth_last_month = sixth_last_month;
    }

    public void setSeventh_last_month(String seventh_last_month) {
        this.seventh_last_month = seventh_last_month;
    }

    public void setEighth_last_month(String eighth_last_month) {
        this.eighth_last_month = eighth_last_month;
    }

    public void setNinth_last_month(String ninth_last_month) {
        this.ninth_last_month = ninth_last_month;
    }

    public void setTenth_last_month(String tenth_last_month) {
        this.tenth_last_month = tenth_last_month;
    }

    public void setEleventh_last_month(String eleventh_last_month) {
        this.eleventh_last_month = eleventh_last_month;
    }

    public void billing_data() {

        if(Utility_Code!=2) {
            String billing_data = getFourth_packet_response().replaceAll(" ", "").substring(2);
            setNo_of_month_data_available(billing_data.length() / 94);

            int billing_data_length = billing_data.length() / 94;


            if (billing_data_length > 11) {
                setCurrent_month(billing_data.substring(0, 94));
                setLast_month(billing_data.substring(94, 188));
                setSecond_last_month(billing_data.substring(188, 282));
                setThird_last_month(billing_data.substring(282, 376));
                setFourth_last_month(billing_data.substring(376, 470));
                setFifth_last_month(billing_data.substring(470, 564));
                setSixth_last_month(billing_data.substring(564, 658));
                setSeventh_last_month(billing_data.substring(658, 752));
                setEighth_last_month(billing_data.substring(752, 846));
                setNinth_last_month(billing_data.substring(846, 940));
                setTenth_last_month(billing_data.substring(940, 1034));
                setEleventh_last_month(billing_data.substring(1034, 1128));
            } else {


                if (billing_data_length == 1) {

                    setCurrent_month(billing_data.substring(0, 94));


                } else if (billing_data_length == 2) {

                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));


                } else if (billing_data_length == 3) {

                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));


                } else if (billing_data_length == 4) {
                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));


                } else if (billing_data_length == 5) {
                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));

                } else if (billing_data_length == 6) {

                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));
                    setFifth_last_month(billing_data.substring(470, 564));


                } else if (billing_data_length == 7) {
                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));
                    setFifth_last_month(billing_data.substring(470, 564));
                    setSixth_last_month(billing_data.substring(564, 658));


                } else if (billing_data_length == 8) {
                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));
                    setFifth_last_month(billing_data.substring(470, 564));
                    setSixth_last_month(billing_data.substring(564, 658));
                    setSeventh_last_month(billing_data.substring(658, 752));


                } else if (billing_data_length == 9) {
                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));
                    setFifth_last_month(billing_data.substring(470, 564));
                    setSixth_last_month(billing_data.substring(564, 658));
                    setSeventh_last_month(billing_data.substring(658, 752));
                    setEighth_last_month(billing_data.substring(752, 846));


                } else if (billing_data_length == 10) {
                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));
                    setFifth_last_month(billing_data.substring(470, 564));
                    setSixth_last_month(billing_data.substring(564, 658));
                    setSeventh_last_month(billing_data.substring(658, 752));
                    setEighth_last_month(billing_data.substring(752, 846));
                    setNinth_last_month(billing_data.substring(846, 940));


                } else if (billing_data_length == 11) {

                    setCurrent_month(billing_data.substring(0, 94));
                    setLast_month(billing_data.substring(94, 188));
                    setSecond_last_month(billing_data.substring(188, 282));
                    setThird_last_month(billing_data.substring(282, 376));
                    setFourth_last_month(billing_data.substring(376, 470));
                    setFifth_last_month(billing_data.substring(470, 564));
                    setSixth_last_month(billing_data.substring(564, 658));
                    setSeventh_last_month(billing_data.substring(658, 752));
                    setEighth_last_month(billing_data.substring(752, 846));
                    setNinth_last_month(billing_data.substring(846, 940));
                    setTenth_last_month(billing_data.substring(940, 1034));


                }
            }

        }
        else{
            billing_data_JVVNL();
        }
    }

    public void billing_data_JVVNL() {

        String billing_data = getFourth_packet_response().replaceAll(" ", "").substring(4);
        setNo_of_month_data_available(billing_data.length() / 60);

        int billing_data_length = billing_data.length() / 60;

        if (billing_data_length > 11) {
            setCurrent_month(billing_data.substring(0, 60));
            setLast_month(billing_data.substring(60, 120));
            setSecond_last_month(billing_data.substring(120, 180));
            setThird_last_month(billing_data.substring(180, 240));
            setFourth_last_month(billing_data.substring(240, 300));
            setFifth_last_month(billing_data.substring(300, 360));
            setSixth_last_month(billing_data.substring(360, 420));
            setSeventh_last_month(billing_data.substring(420, 480));
            setEighth_last_month(billing_data.substring(480, 540));
            setNinth_last_month(billing_data.substring(540, 600));
            setTenth_last_month(billing_data.substring(600, 660));
            setEleventh_last_month(billing_data.substring(660, 720));
        } else {


            if (billing_data_length == 1) {

                setCurrent_month(billing_data.substring(0, 60));


            } else if (billing_data_length == 2) {

                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));


            } else if (billing_data_length == 3) {

                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));


            } else if (billing_data_length == 4) {
                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));


            } else if (billing_data_length == 5) {
                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));

            } else if (billing_data_length == 6) {

                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));
                setFifth_last_month(billing_data.substring(300, 360));



            } else if (billing_data_length == 7) {
                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));
                setFifth_last_month(billing_data.substring(300, 360));
                setSixth_last_month(billing_data.substring(360, 420));

            } else if (billing_data_length == 8) {
                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));
                setFifth_last_month(billing_data.substring(300, 360));
                setSixth_last_month(billing_data.substring(360, 420));
                setSeventh_last_month(billing_data.substring(420, 480));


            } else if (billing_data_length == 9) {
                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));
                setFifth_last_month(billing_data.substring(300, 360));
                setSixth_last_month(billing_data.substring(360, 420));
                setSeventh_last_month(billing_data.substring(420, 480));
                setEighth_last_month(billing_data.substring(480, 540));

            } else if (billing_data_length == 10) {
                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));
                setFifth_last_month(billing_data.substring(300, 360));
                setSixth_last_month(billing_data.substring(360, 420));
                setSeventh_last_month(billing_data.substring(420, 480));
                setEighth_last_month(billing_data.substring(480, 540));
                setNinth_last_month(billing_data.substring(540, 600));

            } else if (billing_data_length == 11) {

                setCurrent_month(billing_data.substring(0, 60));
                setLast_month(billing_data.substring(60, 120));
                setSecond_last_month(billing_data.substring(120, 180));
                setThird_last_month(billing_data.substring(180, 240));
                setFourth_last_month(billing_data.substring(240, 300));
                setFifth_last_month(billing_data.substring(300, 360));
                setSixth_last_month(billing_data.substring(360, 420));
                setSeventh_last_month(billing_data.substring(420, 480));
                setEighth_last_month(billing_data.substring(480, 540));
                setNinth_last_month(billing_data.substring(540, 600));
                setTenth_last_month(billing_data.substring(600, 660));
            }
        }

    }

    public String[] billing_history_parser(String month) {

            return  billing_history_parser_JVVNL(month);

    }

    public String[] billing_history_parser_JVVNL(String month) {

        String data = month;
        String active_energy = "" + hex2decimal(data.substring(0, 8));

        active_energy = "" + Double.parseDouble(active_energy) / 1000;

        String apparent_energy = "" + hex2decimal(data.substring(8, 16));

        apparent_energy = "" + Double.parseDouble(apparent_energy) / 1000;

        // String lag = "" + hex2decimal(data.substring(16, 24));
        // lag = "" + Double.parseDouble(lag) / 1000;

        // String lead = "" + hex2decimal(data.substring(24, 32));
        // lead = "" + Double.parseDouble(lead) / 1000;

        String maximum_demand_KW = "" + hex2decimal(data.substring(16, 20));
        maximum_demand_KW = "" + Double.parseDouble(maximum_demand_KW) / 1000;
        String maximum_demand_KW_date_time = "" + hex2decimal(data.substring(20, 22)) + "/"
                + hex2decimal(data.substring(22, 24)) + "/" + hex2decimal(data.substring(24, 26)) + " "
                + hex2decimal(data.substring(26, 28)) + ":" + hex2decimal(data.substring(28, 30));


        String maximum_demand_KV = "" + hex2decimal(data.substring(30, 34));
        maximum_demand_KV = "" + Double.parseDouble(maximum_demand_KV) / 1000;
        String maximum_demand_KV_date_time = "" + hex2decimal(data.substring(34, 36)) + "/" + hex2decimal(data.substring(36, 38)) + "/" + hex2decimal(data.substring(38, 40)) + " " + hex2decimal(data.substring(40, 42)) + ":" + hex2decimal(data.substring(42, 44));

        // String billing_date_time = "" + hex2decimal(data.substring(76, 78)) + "/" + hex2decimal(data.substring(78, 80)) + "/" + hex2decimal(data.substring(80, 82)) + " " + hex2decimal(data.substring(82, 84)) + ":" + hex2decimal(data.substring(84, 86));

        String[] data1 = {active_energy, apparent_energy, maximum_demand_KW, maximum_demand_KW_date_time, maximum_demand_KV, maximum_demand_KV_date_time};

        return data1;

    }


    public static byte calculate_ChallengeByte(String responseByte){

        String fixedByte="FA";

        int firstValue= Integer.parseInt(fixedByte,16);

        int secondValue=Integer.parseInt(responseByte,16);

        int sum= firstValue+secondValue;

        String bin= Integer.toBinaryString(sum);

        String Ones_bin=CalculateOnesCompliment(bin);

        int complemented= Integer.parseInt(Ones_bin,2);

        return  (byte)complemented;
    }
    private static String CalculateOnesCompliment(String bin) {

        String  ones = "";
        if(bin!=null) {
            for (int i = 0; i < bin.length(); i++) {
                ones += change(bin.charAt(i));
            }

        }
        return ones;

    }

    private static char change(char c) {
        return (c == '0') ? '1' : '0';
    }


}

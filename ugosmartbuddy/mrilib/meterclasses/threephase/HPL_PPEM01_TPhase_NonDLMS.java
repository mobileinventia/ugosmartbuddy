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

public class HPL_PPEM01_TPhase_NonDLMS {

    int Utility_Code;

    public HPL_PPEM01_TPhase_NonDLMS(int Utility_Code){
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
        if(Utility_Code==2) {
            // for jvvnl
            non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY(history_data[4]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(history_data[2]);
        }
        else{
            non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY(history_data[6]);
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(history_data[4]);
        }
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

    String serialno;
    String Alldata;

    String second_packet_response;

    public String getSecond_packet_response() {
        return second_packet_response;
    }

    public void setSecond_packet_response(String second_packet_response) {
        this.second_packet_response = second_packet_response;
    }

    public String getThird_packet_response() {
        return third_packet_response;
    }

    public void setThird_packet_response(String third_packet_response) {
        this.third_packet_response = third_packet_response;
    }

    String third_packet_response;

    public String getThird_packet_formation_packet() {
        return third_packet_formation_packet;
    }

    public void setThird_packet_formation_packet(String third_packet_formation_packet) {
        this.third_packet_formation_packet = third_packet_formation_packet;
    }

    String third_packet_formation_packet;

    public String getFourth_packet_response() {
        return fourth_packet_response;
    }

    public void setFourth_packet_response(String fourth_packet_response) {
        this.fourth_packet_response = fourth_packet_response;
    }

    String fourth_packet_response;

    public String getFourth_packet_formation_packet() {
        return fourth_packet_formation_packet;
    }

    public void setFourth_packet_formation_packet(String fourth_packet_formation_packet) {
        this.fourth_packet_formation_packet = fourth_packet_formation_packet;
    }

    String fourth_packet_formation_packet;

    public String getFirst_packet_response() {
        return first_packet_response;
    }

    public void setFirst_packet_response(String first_packet_response) {
        this.first_packet_response = first_packet_response;
    }

    String first_packet_response;

    String TOD_packet_formation_packet;

    public String getTOD_packet_formation_packet() {
        return TOD_packet_formation_packet;
    }

    public void setTOD_packet_formation_packet(String TOD_packet_formation_packet) {
        this.TOD_packet_formation_packet = TOD_packet_formation_packet;
    }

    public String getTod_packet() {
        return Tod_packet;
    }

    public void setTod_packet(String tod_packet) {
        Tod_packet = tod_packet;
    }

    String Tod_packet;

    String Tod2_packet;
    String Tod3_packet;
    String Tod4_packet;
    String Tod5_packet;
    String Tod6_packet;

    String load_survey_formation_packet, load_survey_packet;
    String previous_day_load_packet;
    String tamper_data_formation_packet, tamper_data_packet;

    public String getTamper_data_formation_packet() {
        return tamper_data_formation_packet;
    }

    public void setTamper_data_formation_packet(String tamper_data_formation_packet) {
        this.tamper_data_formation_packet = tamper_data_formation_packet;
    }

    public String getTamper_data_packet() {
        return tamper_data_packet;
    }

    public void setTamper_data_packet(String tamper_data_packet) {
        this.tamper_data_packet = tamper_data_packet;
    }

    public String getPrevious_day_load_packet() {
        return previous_day_load_packet;
    }

    public void setPrevious_day_load_packet(String previous_day_load_packet) {
        this.previous_day_load_packet = previous_day_load_packet;
    }

    public String getLoad_survey_formation_packet() {
        return load_survey_formation_packet;
    }

    public void setLoad_survey_formation_packet(String load_survey_formation_packet) {
        this.load_survey_formation_packet = load_survey_formation_packet;
    }

    public String getLoad_survey_packet() {
        return load_survey_packet;
    }

    public void setLoad_survey_packet(String load_survey_packet) {
        this.load_survey_packet = load_survey_packet;
    }

    public String getTod8_packet() {
        return Tod8_packet;
    }

    public void setTod8_packet(String tod8_packet) {
        Tod8_packet = tod8_packet;
    }

    String Tod8_packet;

    public String getTod2_packet() {
        return Tod2_packet;
    }

    public void setTod2_packet(String tod2_packet) {
        Tod2_packet = tod2_packet;
    }

    public String getTod3_packet() {
        return Tod3_packet;
    }

    public void setTod3_packet(String tod3_packet) {
        Tod3_packet = tod3_packet;
    }

    public String getTod4_packet() {
        return Tod4_packet;
    }

    public void setTod4_packet(String tod4_packet) {
        Tod4_packet = tod4_packet;
    }

    public String getTod5_packet() {
        return Tod5_packet;
    }

    public void setTod5_packet(String tod5_packet) {
        Tod5_packet = tod5_packet;
    }

    public String getTod6_packet() {
        return Tod6_packet;
    }

    public void setTod6_packet(String tod6_packet) {
        Tod6_packet = tod6_packet;
    }

    public String getTod7_packet() {
        return Tod7_packet;
    }

    public void setTod7_packet(String tod7_packet) {
        Tod7_packet = tod7_packet;
    }

    String Tod7_packet;

    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
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
                // p.setEop(0xFF);
                p.setCount(14);
                p.setWaitTime(10000);

                // first packet
                byte[] data = {(byte) 0xFE, 0x40, 0x00, 0x01, 0x00, (byte) 0xFF};
                synchronized (serial.getSynchronous()) {
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setFirst_packet_response(GXCommon.bytesToHex(p.getReply()));


                    p.setReply(null);

                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(315);
                        serial.send(prepare_second_packet(), null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setSerialno(GXCommon.bytesToHex(p.getReply()));


                    // third packet for instantaneous


                    p.setReply(null);

                    synchronized (this) {
                        byte[] third_packet = {(byte) 0xFE, 0x01, 0x01, 0x01, 0x01, (byte) 0xFF};
                        p.setCount(62);
                        if(Utility_Code==2)
                            p.setCount(60);
                        serial.send(third_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setThird_packet_formation_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    p.setReply(null);

                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(81);
                        if(Utility_Code==2)
                            p.setCount(79);
                        serial.send(prepare_third_packet(), null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        // setThird_packet_response(GXCommon.bytesToHex(p.getReply()));
                    }
                    setThird_packet_response(GXCommon.bytesToHex(p.getReply()));

                    read_billing_data(serial);
                   // read_Tod_data(serial);

                }
            }

        } catch (Exception e) {
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

    public void read_Tod_data(GXSerial serial) {
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
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);

                p.setWaitTime(30000);
                synchronized (serial.getSynchronous()) {
                    // fourth packet for billing data
                    synchronized (this) {
                        byte[] fifth_packet = {(byte) 0xFE, 0x03, 0x03, 0x03, 0x03, (byte) 0xFF};
                        // p.setEop((byte)0xFF);
                        p.setCount(30);
                        serial.send(fifth_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setTOD_packet_formation_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    p.setReply(null);

                    // tod1 packet
                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(391);
                        serial.send(prepare_fifth_packet(), null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod_packet(GXCommon.bytesToHex(p.getReply()));

                    }

                    p.setReply(null);
// tod2 packet
                    byte next_tod_packet = (byte) 0xBB;

                    synchronized (this) {
                        this.wait(500);
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod2_packet(GXCommon.bytesToHex(p.getReply()));
                    }


// tod3 packet
                    p.setReply(null);

                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod3_packet(GXCommon.bytesToHex(p.getReply()));
                        this.wait(100);
                    }


// tod4 packet
                    p.setReply(null);
                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod4_packet(GXCommon.bytesToHex(p.getReply()));
                    }


// tod5 packet
                    p.setReply(null);

                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod5_packet(GXCommon.bytesToHex(p.getReply()));
                    }

// tod6 packet
                    p.setReply(null);
                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod6_packet(GXCommon.bytesToHex(p.getReply()));
                    }


// tod7 packet


                    p.setReply(null);
                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod7_packet(GXCommon.bytesToHex(p.getReply()));
                    }


// tod8 packet

                    p.setReply(null);
                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(390);
                        serial.send(next_tod_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTod8_packet(GXCommon.bytesToHex(p.getReply()));
                    }

                }
            }

        } catch (Exception e) {
        }
    }


    public void read_load_survey_data(GXSerial serial) {
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
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);

                p.setWaitTime(30000);
                synchronized (serial.getSynchronous()) {
                    // fourth packet for billing data
                    synchronized (this) {
                        byte[] fourth_packet = {(byte) 0xFE, 0x05, 0x05, 0x05, 0x05, (byte) 0xFF};
                        // p.setEop((byte)0xFF);
                        p.setCount(46);
                        serial.send(fourth_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setLoad_survey_formation_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    p.setReply(null);

// current day

                    synchronized (this) {
                        // p.setEop((byte)0xFF);
                        p.setCount(793);
                        serial.send(prepare_load_survey_packet(), null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setLoad_survey_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    // for more load survey data we need to send bb. maximum limit is 60


                    String load_survey_data_all_packet_data = getLoad_survey_packet();


                    byte previous_day_load = (byte) 0xBB;

                    synchronized (this) {
                        for (int i = 0; i < 61; i++) {
                            p.setCount(792);
                            p.setReply(null);
                            serial.send(previous_day_load, null);
                            this.wait(100);
                            if (!serial.receive(p)) {
                                throw new Exception("Invalid meter type.");
                            }
                            load_survey_data_all_packet_data = load_survey_data_all_packet_data + GXCommon.bytesToHex(p.getReply());
                            setLoad_survey_packet(load_survey_data_all_packet_data);

                        }
                    }


                }
            }

        } catch (Exception e) {
        }
    }


    public void read_tamper_data(GXSerial serial) {
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
            if (serial.isOpen()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);

                p.setWaitTime(30000);
                synchronized (serial.getSynchronous()) {
                    // fourth packet for billing data
                    synchronized (this) {
                        byte[] tamper_formation_packet = {(byte) 0xFE, 0x04, 0x04, 0x04, 0x04, (byte) 0xFF};
                        // p.setEop((byte)0xFF);
                        p.setCount(70);
                        serial.send(tamper_formation_packet, null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        setTamper_data_formation_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    p.setReply(null);


                    synchronized (this) {
                        p.setCount(601);
                        serial.send(prepare_tamper_packet(), null);
                        this.wait(100);
                        if (!serial.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        setTamper_data_packet(GXCommon.bytesToHex(p.getReply()));
                    }


                    // for more tamper  data we need to send bb.

                    String tamper_data_all_packet_data = getTamper_data_packet();

                    byte more_tamper_data = (byte) 0xBB;
                    synchronized (this) {
                        for (int i = 0; i < 12; i++) {
                            p.setCount(600);
                            p.setReply(null);
                            serial.send(more_tamper_data, null);
                            this.wait(100);
                            if (!serial.receive(p)) {
                                throw new Exception("Invalid meter type.");
                            }
                            //setTamper_data_packet(GXCommon.bytesToHex(p.getReply()));
                            tamper_data_all_packet_data = tamper_data_all_packet_data + GXCommon.bytesToHex(p.getReply());
                            setTamper_data_packet(tamper_data_all_packet_data);
                        }
                    }


                }
            }

        } catch (Exception e) {
        }
    }


    public byte[] prepare_second_packet() {


        String[] first_packet_data = getFirst_packet_response().split(" ");
        int value = hex2decimal(first_packet_data[1].trim());
        int second_packet_d = (261 - value);
        byte b = (byte) (second_packet_d);
        int[] data_to_calculate_crc = {0xFE, b, 0x00};
        byte[] second_packet = {(byte) 0xFE, b, 0x00, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0], (byte) 0xFF};
        //   byte[] data_to_calculate_crc = {(byte) 0xFE,b, 0x00};

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

    public byte[] prepare_fifth_packet() {
        String[] first_packet_data = getTOD_packet_formation_packet().split(" ");
        int value = hex2decimal(first_packet_data[1].trim());
        int second_packet_d = (261 - value);
        byte b = (byte) (second_packet_d);

        int[] data_to_calculate_crc = {0xFE, b, 0x03};
        byte[] fifth_packet = {(byte) 0xFE, b, 0x03, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0], (byte) 0xFF};
        return fifth_packet;
    }

    public byte[] prepare_load_survey_packet() {
        String[] first_packet_data = getLoad_survey_formation_packet().split(" ");
        int value = hex2decimal(first_packet_data[1].trim());
        int second_packet_d = (261 - value);
        byte b = (byte) (second_packet_d);

        int[] data_to_calculate_crc = {0xFE, b, 0x05};
        byte[] load_packet = {(byte) 0xFE, b, 0x05, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0], (byte) 0xFF};
        return load_packet;
    }

    public byte[] prepare_tamper_packet() {
        String[] first_packet_data = getTamper_data_formation_packet().split(" ");
        int value = hex2decimal(first_packet_data[1].trim());
        int second_packet_d = (261 - value);
        byte b = (byte) (second_packet_d);

        int[] data_to_calculate_crc = {0xFE, b, 0x04};
        byte[] tamper_packet = {(byte) 0xFE, b, 0x04, crc16mod(data_to_calculate_crc)[1], crc16mod(data_to_calculate_crc)[0], (byte) 0xFF};
        return tamper_packet;
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


    int no_of_month_data_available;

    public int getNo_of_month_data_available() {
        return no_of_month_data_available;
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

    public String getSecond_last_month() {
        return second_last_month;
    }

    public void setSecond_last_month(String second_last_month) {
        this.second_last_month = second_last_month;
    }

    public String getThird_last_month() {
        return third_last_month;
    }

    public void setThird_last_month(String third_last_month) {
        this.third_last_month = third_last_month;
    }

    public String getFourth_last_month() {
        return fourth_last_month;
    }

    public void setFourth_last_month(String fourth_last_month) {
        this.fourth_last_month = fourth_last_month;
    }

    public String getFifth_last_month() {
        return fifth_last_month;
    }

    public void setFifth_last_month(String fifth_last_month) {
        this.fifth_last_month = fifth_last_month;
    }

    public String getSixth_last_month() {
        return sixth_last_month;
    }

    public void setSixth_last_month(String sixth_last_month) {
        this.sixth_last_month = sixth_last_month;
    }

    public String getSeventh_last_month() {
        return seventh_last_month;
    }

    public void setSeventh_last_month(String seventh_last_month) {
        this.seventh_last_month = seventh_last_month;
    }

    public String getEighth_last_month() {
        return eighth_last_month;
    }

    public void setEighth_last_month(String eighth_last_month) {
        this.eighth_last_month = eighth_last_month;
    }

    public String getNinth_last_month() {
        return ninth_last_month;
    }

    public void setNinth_last_month(String ninth_last_month) {
        this.ninth_last_month = ninth_last_month;
    }

    public String getTenth_last_month() {
        return tenth_last_month;
    }

    public void setTenth_last_month(String tenth_last_month) {
        this.tenth_last_month = tenth_last_month;
    }

    public String getEleventh_last_month() {
        return eleventh_last_month;
    }

    public void setEleventh_last_month(String eleventh_last_month) {
        this.eleventh_last_month = eleventh_last_month;
    }

    String current_month, last_month, second_last_month, third_last_month, fourth_last_month, fifth_last_month, sixth_last_month,
            seventh_last_month, eighth_last_month, ninth_last_month, tenth_last_month, eleventh_last_month;

    public void billing_data() {
      //  Utility.saveJsonFile("byte_file",getFourth_packet_response());
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

        boolean isFF=false;
       // setFourth_packet_response("D7 22 FF FE 00 63 B7 A3 00 69 15 F7 0E 1A 04 08 14 10 00 0E 38 04 08 14 10 00 03 AB 00 00 00 00 2B CC 00 00 00 00 00 00 62 14 75 00 67 57 FF 09 42 08 07 14 0A 00 09 74 08 07 14 0A 00 03 8A 00 00 00 01 1F 9E 01 08 14 00 00 00 5B 41 EF 00 5F D1 CE 08 3E 05 06 14 0A 00 08 52 05 06 14 0A 00 03 8D 00 00 00 01 18 14 01 07 14 00 00 00 54 2A F4 00 58 05 7A 0A 8C 1F 05 14 0A 00 0A B4 1F 05 14 0A 00 03 96 00 00 00 01 20 CB 01 06 14 00 00 00 4C 7D 31 00 4F A8 DF 0A 78 0D 04 14 0A 1E 0A A0 0D 04 14 0A 1E 03 AA 00 00 00 01 19 15 01 05 14 00 00 00 43 E9 B4 00 46 85 C0 0F 64 02 03 14 08 1E 0F 6E 02 03 14 08 1E 03 B6 00 00 00 01 21 37 01 04 14 00 00 00 3A B7 D6 00 3C D9 17 0C BC 0E 02 14 0A 1E 0C BC 0E 02 14 0A 1E 03 B9 00 00 00 01 0E 64 01 03 14 00 00 00 33 A6 F8 00 35 6F 1F 0C DA 1B 01 14 17 00 0C E4 1B 01 14 17 00 03 C5 00 00 00 01 22 0B 01 02 14 00 00 00 2B CE C4 00 2D 4F 60 0F 1E 1E 0C 13 0A 1E 0F 28 1E 0C 13 0A 1E 03 BB 00 00 00 01 21 EF 01 01 14 00 00 00 24 EF B4 00 26 1E 0C 10 B8 04 0B 13 0A 1E 10 C2 04 0B 13 0A 1E 03 C1 00 00 00 01 19 15 01 0C 13 00 00 00 1B C6 A5 00 1C 98 50 13 60 1B 0A 13 14 00 13 6A 1B 0A 13 14 00 03 CD 00 00 00 01 21 80 01 0B 13 00 00 00 0E E3 04 00 0F");
       // Utility.saveJsonFile("byte_file",getFourth_packet_response());

        String billing_data = getFourth_packet_response().replaceAll(" ", "").substring(4);
        if(billing_data.substring(0,2).equalsIgnoreCase("ff")) {
            billing_data = billing_data.substring(4);
            isFF=true;
        }
        setNo_of_month_data_available(billing_data.length() / 60);
        int billing_data_length = billing_data.length() / 60;


        if (billing_data_length > 11) {
            setCurrent_month(billing_data.substring(0, 60));
            if(isFF)
                billing_data=billing_data.substring(10);
            setLast_month(billing_data.substring(60, 120));
            if(isFF)
                billing_data=billing_data.substring(10);
            setSecond_last_month(billing_data.substring(120, 180));
            if(isFF)
                billing_data=billing_data.substring(10);
            setThird_last_month(billing_data.substring(180, 240));
            if(isFF)
                billing_data=billing_data.substring(10);
            setFourth_last_month(billing_data.substring(240, 300));
            if(isFF)
                billing_data=billing_data.substring(10);
            setFifth_last_month(billing_data.substring(300, 360));
            if(isFF)
                billing_data=billing_data.substring(10);
            setSixth_last_month(billing_data.substring(360, 420));
            if(isFF)
                billing_data=billing_data.substring(10);
            setSeventh_last_month(billing_data.substring(420, 480));
            if(isFF)
                billing_data=billing_data.substring(10);
            setEighth_last_month(billing_data.substring(480, 540));
            if(isFF)
                billing_data=billing_data.substring(10);
            setNinth_last_month(billing_data.substring(540, 600));
            if(isFF)
                billing_data=billing_data.substring(10);
            setTenth_last_month(billing_data.substring(600, 660));
            if(isFF)
                billing_data=billing_data.substring(10);
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

        if(Utility_Code!=2) {
            String data = month;
            String active_energy = "" + hex2decimal(data.substring(0, 8));

            active_energy = "" + Double.parseDouble(active_energy) / 1000;

            String apparent_energy = "" + hex2decimal(data.substring(8, 16));

            apparent_energy = "" + Double.parseDouble(apparent_energy) / 1000;

            String lag = "" + hex2decimal(data.substring(16, 24));
            lag = "" + Double.parseDouble(lag) / 1000;

            String lead = "" + hex2decimal(data.substring(24, 32));
            lead = "" + Double.parseDouble(lead) / 1000;

            String maximum_demand_KW = "" + hex2decimal(data.substring(32, 36));
            maximum_demand_KW = "" + Double.parseDouble(maximum_demand_KW) / 1000;
            String maximum_demand_KW_date_time = "" + hex2decimal(data.substring(36, 38)) + "/"
                    + hex2decimal(data.substring(38, 40)) + "/" + hex2decimal(data.substring(40, 42)) + " "
                    + hex2decimal(data.substring(42, 44)) + ":" + hex2decimal(data.substring(44, 46));


            String maximum_demand_KV = "" + hex2decimal(data.substring(46, 50));
            maximum_demand_KV = "" + Double.parseDouble(maximum_demand_KV) / 1000;
            String maximum_demand_KV_date_time = "" + hex2decimal(data.substring(50, 52)) + "/" + hex2decimal(data.substring(52, 54)) + "/" + hex2decimal(data.substring(54, 56)) + " " + hex2decimal(data.substring(56, 58)) + ":" + hex2decimal(data.substring(58, 60));

            String billing_date_time = "" + hex2decimal(data.substring(76, 78)) + "/" + hex2decimal(data.substring(78, 80)) + "/" + hex2decimal(data.substring(80, 82)) + " " + hex2decimal(data.substring(82, 84)) + ":" + hex2decimal(data.substring(84, 86));

            String[] data1 = {active_energy, apparent_energy, lag, lead, maximum_demand_KW, maximum_demand_KW_date_time, maximum_demand_KV, maximum_demand_KV_date_time, billing_date_time};

            return data1;
        }
        else{
         return  billing_history_parser_JVVNL(month);
        }

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

    public String tod_value_parse(int packet) {
        String data = "";
        if (packet == 1) {
            data = getTod_packet().replaceAll(" ", "");
            data = data.substring(2);
        } else if (packet == 2) {
            data = getTod2_packet().replaceAll(" ", "");

        } else if (packet == 3) {
            data = getTod3_packet().replaceAll(" ", "");

        } else if (packet == 4) {
            data = getTod4_packet().replaceAll(" ", "");

        } else if (packet == 5) {
            data = getTod5_packet().replaceAll(" ", "");

        } else if (packet == 6) {
            data = getTod6_packet().replaceAll(" ", "");

        } else if (packet == 7) {
            data = getTod7_packet().replaceAll(" ", "");

        } else if (packet == 8) {
            data = getTod8_packet().replaceAll(" ", "");

        }
        return data;
    }

    public String[] tod_parser(String data) {

        String current_month_tod = data.substring(0, 60);
        String last_month_tod = data.substring(60, 120);
        String second_last_month_tod = data.substring(120, 180);
        String third_last_month_tod = data.substring(180, 240);
        String fourth_last_month_tod = data.substring(240, 300);
        String fifth_last_month_tod = data.substring(300, 360);
        String sixth_last_month_tod = data.substring(360, 420);
        String seventh_last_month_tod = data.substring(420, 480);
        String eighth_last_month_tod = data.substring(480, 540);
        String ninth_last_month_tod = data.substring(540, 600);
        String tenth_last_month_tod = data.substring(600, 660);
        String eleventh_last_month_tod = data.substring(660, 720);

        String[] data1 = {current_month_tod, last_month_tod, second_last_month_tod, third_last_month_tod, fourth_last_month_tod, fifth_last_month_tod, sixth_last_month_tod, seventh_last_month_tod, eighth_last_month_tod, ninth_last_month_tod, tenth_last_month_tod, eleventh_last_month_tod};

        return data1;

    }

    public String[] tod_parsed_values(String data) {
        data = data.trim();

        String active_tod = "" + hex2decimal(data.substring(0, 8));

        active_tod = "" + Double.parseDouble(active_tod) / 1000;

        String apparent_tod = "" + hex2decimal(data.substring(8, 16));
        apparent_tod = "" + Double.parseDouble(apparent_tod) / 1000;

        String lag_tod = "" + hex2decimal(data.substring(16, 24));

        lag_tod = "" + Double.parseDouble(lag_tod) / 1000;
        String lead_tod = "" + hex2decimal(data.substring(24, 32));

        lead_tod = "" + Double.parseDouble(lead_tod) / 1000;

        String kw_tod = "" + hex2decimal(data.substring(32, 36));

        String kw_tod_date = "" + hex2decimal(data.substring(36, 38)) + "/" + hex2decimal(data.substring(38, 40)) + "/" + hex2decimal(data.substring(40, 42)) + " " + hex2decimal(data.substring(42, 44)) + ":" + hex2decimal(data.substring(44, 46));

        String kv_tod = "" + hex2decimal(data.substring(46, 50));

        String kv_tod_date = "" + hex2decimal(data.substring(50, 52)) + "/" + hex2decimal(data.substring(52, 54)) + "/" + hex2decimal(data.substring(54, 56)) + " " + hex2decimal(data.substring(56, 58)) + ":" + hex2decimal(data.substring(58, 60));


        String[] data1 = {active_tod, apparent_tod, lag_tod, lead_tod, kw_tod, kw_tod_date, kv_tod, kv_tod_date};

        return data1;
    }


    public String[] all_tod_values() {

        String all_tod_active = "";
        String all_tod_apparent = "";
        String all_tod_lag = "";
        String all_tod_lead = "";
        String all_tod_kw = "";
        String all_date_kw = "";
        String all_tod_kv = "";
        String all_date_kv = "";

        for (int i = 1; i <= 8; i++) {

            for (int j = 0; j < 12; j++) {

                // one string  value contains  12 month all tod's .like all_tod_active contains 12 month 8 tod and sequence is tod1 current month, tod2 will be last month and so on.

                all_tod_active = all_tod_active + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[0] + ",";
                all_tod_apparent = all_tod_apparent + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[1] + ",";
                all_tod_lag = all_tod_lag + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[2] + ",";
                all_tod_lead = all_tod_lead + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[3] + ",";
                all_tod_kw = all_tod_kw + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[4] + ",";
                all_date_kw = all_date_kw + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[5] + ",";
                all_tod_kv = all_tod_kv + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[6] + ",";
                all_date_kv = all_date_kv + tod_parsed_values(tod_parser(tod_value_parse(i))[j])[7] + ",";

            }


        }

        String[] data = {all_tod_active, all_tod_apparent, all_tod_lag, all_tod_lead, all_tod_kw, all_date_kw, all_tod_kv, all_date_kv};

        return data;
    }

    public String[] tamper_data_parsing() {
        String data = getTamper_data_packet().replaceAll(" ", "");
        data = data.substring(2);
        int i = 0, j = 60;
        String[] data1 = new String[data.length() / 60];

        int length_data = data.length() / 60;


        for (int k = 0; k < length_data; k++) {
            data1[k] = data.substring(i, j);
            i = i + 60;
            j = j + 60;
        }

        return data1;
    }

    public String[] tamper_data_values(String[] data) {

        String[] data1 = new String[data.length];
        for (int i = 0; i < data.length; i++) {

            String event_code = "" + hex2decimal(data[i].substring(0, 2));

            String date_and_time = "" + hex2decimal(data[i].substring(2, 4)) + "/" + hex2decimal(data[i].substring(4, 6)) + "/" + hex2decimal(data[i].substring(6, 8)) + " " + hex2decimal(data[i].substring(8, 10)) + ":" + hex2decimal(data[i].substring(10, 12));


            String current_R_phase = "" + hex2decimal(data[i].substring(12, 16));
            current_R_phase = "" + Double.parseDouble(current_R_phase) / 100;

            String current_Y_phase = "" + hex2decimal(data[i].substring(16, 20));
            current_Y_phase = "" + Double.parseDouble(current_Y_phase) / 100;

            String current_B_phase = "" + hex2decimal(data[i].substring(20, 24));
            current_B_phase = "" + Double.parseDouble(current_B_phase) / 100;

            String voltage_R_phase = "" + hex2decimal(data[i].substring(24, 28));
            voltage_R_phase = "" + Double.parseDouble(voltage_R_phase) / 100;

            String voltage_Y_phase = "" + hex2decimal(data[i].substring(28, 32));
            voltage_Y_phase = "" + Double.parseDouble(voltage_Y_phase) / 100;

            String voltage_B_phase = "" + hex2decimal(data[i].substring(32, 36));
            voltage_B_phase = "" + Double.parseDouble(voltage_B_phase) / 100;

            String power_R_factor = "" + hex2decimal(data[i].substring(36, 38));
            power_R_factor = "" + Double.parseDouble(power_R_factor) / 100;

            String power_Y_factor = "" + hex2decimal(data[i].substring(38, 40));
            power_Y_factor = "" + Double.parseDouble(power_Y_factor) / 100;

            String power_B_factor = "" + hex2decimal(data[i].substring(40, 42));
            power_B_factor = "" + Double.parseDouble(power_B_factor) / 100;

            String power_System_factor = "" + hex2decimal(data[i].substring(42, 44));
            power_System_factor = "" + Double.parseDouble(power_System_factor) / 100;


            String active_power_kwh = "" + hex2decimal(data[i].substring(44, 52));
            active_power_kwh = "" + Double.parseDouble(active_power_kwh) / 1000;

            String apparent_power_kva = "" + hex2decimal(data[i].substring(52, 60));
            apparent_power_kva = "" + Double.parseDouble(apparent_power_kva) / 1000;

            data1[i] = event_code + "," + date_and_time + "," + current_R_phase + "," + current_Y_phase + "," + current_B_phase + ","
                    + voltage_R_phase + "," + voltage_Y_phase + "," + voltage_B_phase + "," + power_R_factor + "," + power_Y_factor + ","
                    + power_B_factor + "," + power_System_factor + "," + active_power_kwh + "," + apparent_power_kva;
        }

        return data1;

    }

    public String[] load_survey_parse() {
        String[] data1 = null;
        String str = getLoad_survey_packet();
        if (str != null) {
            String data = str.replaceAll(" ", "");
            data = data.substring(2);
            int i = 0, j = 1584;
            data1 = new String[data.length() / 1584];

            int length_data = data.length() / 1584;


            for (int k = 0; k < length_data; k++) {
                data1[k] = data.substring(i, j);
                i = i + 1584;
                j = j + 1584;
            }
        }
        return data1;

    }

    public String[] load_survey_values(String[] data) {
        String[] data1 = new String[data.length];
        for (int i = 0; i < data.length; i++) {

            String date = "" + hex2decimal(data[i].substring(0, 2)) + "/" + hex2decimal(data[i].substring(2, 4)) + "/" + hex2decimal(data[i].substring(4, 6));

            // these are used as load profile in hpl bcs. but we are not displaying these
//            String active_power_kwh = "" + hex2decimal(data[i].substring(6, 14));
//
//            String apparent_power_kvh = "" + hex2decimal(data[i].substring(14, 22));
//
//            String lag = "" + hex2decimal(data[i].substring(22, 30));
//
//            String lead = "" + hex2decimal(data[i].substring(30, 38));
//
//            String total_tamper_count = "" + hex2decimal(data[i].substring(38, 42));
//
//            String power_on_hours = "" + hex2decimal(data[i].substring(42, 46));


            //  use only interval and date for load survey

            String interval1 = load_survey_interval(data[i].substring(48, 80));

            String interval2 = load_survey_interval(data[i].substring(80, 112));

            String interval3 = load_survey_interval(data[i].substring(112, 144));

            String interval4 = load_survey_interval(data[i].substring(144, 176));
            String interval5 = load_survey_interval(data[i].substring(176, 208));

            String interval6 = load_survey_interval(data[i].substring(208, 240));

            String interval7 = load_survey_interval(data[i].substring(240, 272));

            String interval8 = load_survey_interval(data[i].substring(272, 304));

            String interval9 = load_survey_interval(data[i].substring(304, 336));

            String interval10 = load_survey_interval(data[i].substring(336, 368));

            String interval11 = load_survey_interval(data[i].substring(368, 400));

            String interval12 = load_survey_interval(data[i].substring(400, 432));

            String interval13 = load_survey_interval(data[i].substring(432, 464));

            String interval14 = load_survey_interval(data[i].substring(464, 496));

            String interval15 = load_survey_interval(data[i].substring(496, 528));

            String interval16 = load_survey_interval(data[i].substring(528, 560));

            String interval17 = load_survey_interval(data[i].substring(560, 592));

            String interval18 = load_survey_interval(data[i].substring(592, 624));

            String interval19 = load_survey_interval(data[i].substring(624, 656));

            String interval20 = load_survey_interval(data[i].substring(656, 688));

            String interval21 = load_survey_interval(data[i].substring(688, 720));

            String interval22 = load_survey_interval(data[i].substring(720, 752));

            String interval23 = load_survey_interval(data[i].substring(752, 784));

            String interval24 = load_survey_interval(data[i].substring(784, 816));

            String interval25 = load_survey_interval(data[i].substring(816, 848));

            String interval26 = load_survey_interval(data[i].substring(848, 880));

            String interval27 = load_survey_interval(data[i].substring(880, 912));

            String interval28 = load_survey_interval(data[i].substring(912, 944));

            String interval29 = load_survey_interval(data[i].substring(944, 976));

            String interval30 = load_survey_interval(data[i].substring(976, 1008));

            String interval31 = load_survey_interval(data[i].substring(1008, 1040));

            String interval32 = load_survey_interval(data[i].substring(1040, 1072));

            String interval33 = load_survey_interval(data[i].substring(1072, 1104));

            String interval34 = load_survey_interval(data[i].substring(1104, 1136));

            String interval35 = load_survey_interval(data[i].substring(1136, 1168));

            String interval36 = load_survey_interval(data[i].substring(1168, 1200));

            String interval37 = load_survey_interval(data[i].substring(1200, 1232));

            String interval38 = load_survey_interval(data[i].substring(1232, 1264));

            String interval39 = load_survey_interval(data[i].substring(1264, 1296));

            String interval40 = load_survey_interval(data[i].substring(1296, 1328));

            String interval41 = load_survey_interval(data[i].substring(1328, 1360));

            String interval42 = load_survey_interval(data[i].substring(1360, 1392));

            String interval43 = load_survey_interval(data[i].substring(1392, 1424));

            String interval44 = load_survey_interval(data[i].substring(1424, 1456));

            String interval45 = load_survey_interval(data[i].substring(1456, 1488));

            String interval46 = load_survey_interval(data[i].substring(1488, 1520));

            String interval47 = load_survey_interval(data[i].substring(1520, 1552));

            String interval48 = load_survey_interval(data[i].substring(1552, 1584));

// if interval value contains 65535 or 65.535. it means there is no value at that place i.e. empty

//            data1[i]="["+date+"]"+"["+interval1+"]"+"["+interval2+"]"+"["+interval3+"]"+"["+interval4+"]"+"["+interval5+"]"+"["+interval6+"]"+"["+interval7+"]"+ "["+interval8+"]"+
//                    "["+interval9+"]"+"["+interval10+"]"+"["+interval11+"]"+"["+interval12+"]"+"["+interval13+"]"+"["+interval14+"]"+"["+interval15+"]"+"["+interval16+"]"+
//                    "["+interval17+"]"+"["+interval18+"]"+"["+interval19+"]"+"["+interval20+"]"+"["+interval21+"]"+"["+interval22+"]"+"["+interval23+"]"+"["+interval24+"]"+
//                    "["+interval25+"]"+"["+interval26+"]"+"["+interval27+"]"+"["+interval28+"]"+"["+interval29+"]"+"["+interval30+"]"+"["+interval31+"]"+"["+interval32+"]"+
//                    "["+interval33+"]"+"["+interval34+"]"+"["+interval35+"]"+"["+interval36+"]"+"["+interval37+"]"+"["+interval38+"]"+"["+interval39+"]"+"["+interval40+"]"+
//                    "["+interval41+"]"+"["+interval42+"]"+"["+interval43+"]"+"["+interval44+"]"+"["+interval45+"]"+"["+interval46+"]"+"["+interval47+"]"+"["+interval48+"]";
            data1[i] = "[" + date + " 00:30:00," + interval1 + "]" + "[" + date + " 01:00:00," + interval2 + "]" + "[" + date + " 01:30:00," + interval3 + "]" + "[" + date + " 02:00:00," + interval4 + "]" +
                    "[" + date + " 02:30:00," + interval5 + "]" + "[" + date + " 03:00:00," + interval6 + "]" + "[" + date + " 03:30:00," + interval7 + "]" + "[" + date + " 04:00:00," + interval8 + "]" +
                    "[" + date + " 04:30:00," + interval9 + "]" + "[" + date + " 05:00:00," + interval10 + "]" + "[" + date + " 05:30:00," + interval11 + "]" + "[" + date + " 06:00:00," + interval12 + "]" +
                    "[" + date + " 06:30:00," + interval13 + "]" + "[" + date + " 07:00:00," + interval14 + "]" + "[" + date + " 07:30:00," + interval15 + "]" + "[" + date + " 08:00:00," + interval16 + "]" +
                    "[" + date + " 08:30:00," + interval17 + "]" + "[" + date + " 09:00:00," + interval18 + "]" + "[" + date + " 09:30:00," + interval19 + "]" + "[" + date + " 10:00:00," + interval20 + "]" +
                    "[" + date + " 10:30:00," + interval21 + "]" + "[" + date + " 11:00:00," + interval22 + "]" + "[" + date + " 11:30:00," + interval23 + "]" + "[" + date + " 12:00:00," + interval24 + "]" +
                    "[" + date + " 12:30:00," + interval25 + "]" + "[" + date + " 13:00:00," + interval26 + "]" + "[" + date + " 13:30:00," + interval27 + "]" + "[" + date + " 14:00:00," + interval28 + "]" +
                    "[" + date + " 14:30:00," + interval29 + "]" + "[" + date + " 15:00:00," + interval30 + "]" + "[" + date + " 15:30:00," + interval31 + "]" + "[" + date + " 16:00:00," + interval32 + "]" +
                    "[" + date + " 16:30:00," + interval33 + "]" + "[" + date + " 17:00:00," + interval34 + "]" + "[" + date + " 17:30:00," + interval35 + "]" + "[" + date + " 18:00:00," + interval36 + "]" +
                    "[" + date + " 18:30:00," + interval37 + "]" + "[" + date + " 19:00:00," + interval38 + "]" + "[" + date + " 19:30:00," + interval39 + "]" + "[" + date + " 20:00:00," + interval40 + "]" +
                    "[" + date + " 20:30:00," + interval41 + "]" + "[" + date + " 21:00:00," + interval42 + "]" + "[" + date + " 21:30:00," + interval43 + "]" + "[" + date + " 22:00:00," + interval44 + "]" +
                    "[" + date + " 22:30:00," + interval45 + "]" + "[" + date + " 23:00:00," + interval46 + "]" + "[" + date + " 23:30:00," + interval47 + "]" + "[" + date + " 00:00:00," + interval48 + "]";
        }

        return data1;
    }

    public String load_survey_interval(String data) {

        String active_power_kwh = "" + hex2decimal(data.substring(0, 4));
        active_power_kwh = "" + Double.parseDouble(active_power_kwh) / 1000;

        String apparent_power_kvh = "" + hex2decimal(data.substring(4, 8));
        apparent_power_kvh = "" + Double.parseDouble(apparent_power_kvh) / 1000;

        String voltage_R_phase = "" + hex2decimal(data.substring(8, 12));

        String voltage_Y_phase = "" + hex2decimal(data.substring(12, 16));

        String voltage_B_phase = "" + hex2decimal(data.substring(16, 20));

        String current_R_phase = "" + hex2decimal(data.substring(20, 24));

        current_R_phase = "" + Double.parseDouble(current_R_phase) / 10;

        String current_Y_phase = "" + hex2decimal(data.substring(24, 28));

        current_Y_phase = "" + Double.parseDouble(current_Y_phase) / 10;
        String current_B_phase = "" + hex2decimal(data.substring(28, 32));

        current_B_phase = "" + Double.parseDouble(current_B_phase) / 10;

        String data1 = active_power_kwh + "," + apparent_power_kvh + "," + voltage_R_phase + "," + voltage_Y_phase + "," + voltage_B_phase + "," + current_R_phase + "," + current_Y_phase + "," + current_B_phase;

        return data1;
    }


}

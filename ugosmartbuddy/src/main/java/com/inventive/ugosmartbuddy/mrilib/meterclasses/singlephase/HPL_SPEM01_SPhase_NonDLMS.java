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

public class HPL_SPEM01_SPhase_NonDLMS {

    int Utility_Code;

    public HPL_SPEM01_SPhase_NonDLMS(int Utility_Code) {
        this.Utility_Code = Utility_Code;
    }

    private String Alldata;
    private String instantaneous_parameter;

    public String getInstantaneous_parameter() {
        return instantaneous_parameter;
    }

    public void setInstantaneous_parameter(String instantaneous_parameter) {
        this.instantaneous_parameter = instantaneous_parameter;
    }

    public Non_DLMS_Parameter get_JsonParameters(GXSerial serial) {
        Non_DLMS_Parameter non_dlms_parameter = new Non_DLMS_Parameter();
        try {
            read(serial);
            Double[] instantParam = instantanteous_parameter();
            String s[] = required_data();
            String date = Utils.correctDateFormatWithTime(s[0]);
            non_dlms_parameter.setMETER_NO(s[1]);
            non_dlms_parameter.setMANUFACTURE_NAME("HPL");
            non_dlms_parameter.setYEAR_OF_MANUFACTURE(s[2]);
            non_dlms_parameter.setCURRENT_R_PHASE(instantParam[1].toString());
            non_dlms_parameter.setVOLTAGE_R_PHASE(instantParam[0].toString());
            non_dlms_parameter.setBILLING_DATE(date);
            non_dlms_parameter.setACTIVE_ENERGY(s[3]);
            // non_dlms_parameter.setAPPARENT_ENERGY();
            // non_dlms_parameter.setMAXIMUM_DEMAND_KVA_HISTORY();
            non_dlms_parameter.setMAXIMUM_DEMAND_KW_HISTORY(s[4]);
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


    public String getAlldata() {
        return Alldata;
    }

    public void setAlldata(String alldata) {
        Alldata = alldata;
    }

    public void read(GXSerial serial) {
        try {
            serial.setBaudRate(BaudRate.BAUD_RATE_4800);
            serial.setDataBits(8);
            serial.setStopBits(StopBits.ONE);
            serial.setParity(Parity.NONE);

            synchronized (this) {
                serial.open();
            }
            synchronized (this) {
                if (WifiConnectivity.getIsWifi()) {
                    WifiConnectivity.getDataOutputStream().write(Utils.ConfigureCommunicationParameters(4800, 8, 0));
                    this.wait(1000);
                    WifiConnectivity.mReceiver.resetBytesReceived();
                }
            }


            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);
                synchronized (serial.getSynchronous()) {
                    // p.setEop(0x03);
                    if(Utility_Code==2)
                        p.setCount(223);
                        else
                        p.setCount(194);
                    p.setWaitTime(3000);
                    p.setReply(null);
                    byte[] data = {0x31};
                   // byte[] data=  {(byte)0xEF, 0x03, 0x30, 0x04, (byte)0xFA, 0x01, 0x31};
                    synchronized (this) {
                        serial.send(data, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setAlldata(GXCommon.bytesToHex(p.getReply()));
                }

                synchronized (this) {
                    this.wait(200);
                    read_instant(serial);
                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void read_instant(GXSerial serial) {

        try {

            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);

                synchronized (serial.getSynchronous()) {
                    p.setCount(30);
                    p.setWaitTime(10000);
                    p.setReply(null);
                    byte first_packet = 0x3D;
                    synchronized (this) {
                        serial.send(first_packet, null);
                        this.wait(100);
                    }
                    if (!serial.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    setInstantaneous_parameter(GXCommon.bytesToHex(p.getReply()));
                }


            }

        } catch (Exception e) {
        }

    }


    public Double[] instantanteous_parameter() throws Exception {
        Double[] vol_curr = null;
        if (getInstantaneous_parameter() != null) {
            String[] instantaneous = getInstantaneous_parameter().split(" ");

            if (!instantaneous[0].trim().equals("02")) {


                instantaneous = getInstantaneous_parameter().substring(5).trim().split(" ");
            }

            String instant_voltage = instantaneous[2].trim() + instantaneous[1].trim();

            Double voltage = Double.parseDouble(hex2decimal(instant_voltage)) / 10;


            String instant_current = instantaneous[4].trim() + instantaneous[3].trim();
            Double current = Double.parseDouble(hex2decimal(instant_current)) / 100;

            vol_curr = new Double[]{voltage, current};
        }
        return vol_curr;
    }

    public String[] required_data() throws Exception {
        String[] data = null;
        if (getAlldata() != null) {
            String[] all_data = getAlldata().split(" ");

            if (!all_data[0].trim().equals("02")) {


                all_data = getAlldata().substring(2).trim().split(" ");
            }


            String date = hex2decimal(all_data[1].trim()) + "/" + hex2decimal(all_data[2].trim()) + "/" + hex2decimal(all_data[3].trim());
            String time = hex2decimal(all_data[5].trim()) + ":" + hex2decimal(all_data[4]);
            date = date + " " + time;

            String serial_no = hex2decimal(all_data[9].trim() + all_data[8].trim() + all_data[7].trim() + all_data[6].trim());
            String year_manufacutre = "20" + hex2decimal(all_data[14].trim());


            String kwh = "" + Double.parseDouble(hex2decimal(all_data[26].trim() + all_data[25].trim() + all_data[24].trim())) / 10;

            String mkw = "" + Double.parseDouble(hex2decimal(all_data[42].trim() + all_data[41].trim())) / 100; // this is previous month maximum demand and date
            String mkw_date = hex2decimal(all_data[43].trim()) + "/" + hex2decimal(all_data[44].trim()) + "/" + hex2decimal(all_data[45].trim()) + " " + hex2decimal(all_data[46].trim()) + ":" + hex2decimal(all_data[47].trim());

            if (Utility_Code == 2) {
                year_manufacutre="20" + hex2decimal(all_data[16].trim());
                kwh = "" + Double.parseDouble(hex2decimal(all_data[28].trim() + all_data[27].trim() + all_data[26].trim())) / 10;
                mkw = "" + Double.parseDouble(hex2decimal(all_data[44].trim() + all_data[43].trim())) / 100; // this is previous month maximum demand and date
                mkw_date = hex2decimal(all_data[45].trim()) + "/" + hex2decimal(all_data[46].trim()) + "/" + hex2decimal(all_data[47].trim()) + " " + hex2decimal(all_data[48].trim()) + ":" + hex2decimal(all_data[49].trim());

            }

            // this is previous month maximum demand and date. we are just reading current month energy and previous month maximum demand.

            data = new String[]{date, serial_no, year_manufacutre, kwh, mkw, mkw_date};
        }
        return data;
    }

    private static String hexToAscii(String hxStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hxStr.length(); i += 2) {
            String str = hxStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }


    public String hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return String.valueOf(val);
    }

}

package com.inventive.ugosmartbuddy.mrilib.common;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.inventive.ugosmartbuddy.mrilib.readData.Authentication;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSClient;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSSecureClient;
import com.inventive.ugosmartbuddy.mrilib.readData.InterfaceType;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.GXCommon;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.IGXMedia;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.BaudRate;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static Date addDays(Date date, int days) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.DAY_OF_MONTH, days);
        return calender.getTime();
    }

    public static String dateStringWithTime(Date date) {
        String date1 = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
            date1 = formatter.format(date);
        } catch (Exception pe) {
            pe.printStackTrace();
        }
        return date1;
    }

    public static Date dateStringWithTime(String date) {
        Date date1 = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
            date1 = formatter.parse(date);
        } catch (Exception pe) {
            pe.printStackTrace();
        }
        return date1;
    }

    public static String padString(String str) {
        String d1 = null;
        if (str != null && !str.isEmpty())
            d1 = (str.length() == 1) ? "0" + str : str;
        return d1;
    }

    public static int getIntegerValue(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return i;
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static double getDoubleValue(String value) {
        double i = 0.0;
        try {
            value = value.trim();
            i = Double.parseDouble(value);
        } catch (Exception e) {
        }
        return i;
    }

    public static String dateToString_withTime(Date date) {
        String date1 = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
            date1 = formatter.format(date);
        } catch (Exception pe) {
            pe.printStackTrace();
        }
        return date1;
    }

    // Old IbillApp
    public static String correctDateFormatWithTime(String date) {
        /*
         * 3/5/18 02:04, 3/5/18 2:4, 3/5/1802:04, 3/5/201802:04, 3/5/18 02:04:AM, 3/5/1802:04:PM, 3/5/1802:04:AM
         * 3/5/2018 2:04, 3/5/2018 02:04, 28/05/18 5:00:00:PM, 28/05/185:00:00:PM, 28/05/2018 5:00:00:PM, 28/05/20185:00:00:PM
         * 28/05/2018 5:00:AM, 28/05/20185:00:AM, 28/05/2018 5:00:PM, 28/05/20185:00:PM, 28/05/18 5:00:00:000
         * 28/05/18 5:00:00, 28/05/18 5:00:00:000:PM, 28/05/185:00:00:000:AM, 28/05/2018 5:00:00:000:PM, 28/05/185:00:00:000:PM
         */

        String date1 = date;
        if (date != null && !date.isEmpty()) {
            if (date.equalsIgnoreCase("12:00AM") || date.equalsIgnoreCase("12:AM")
                    || date.equalsIgnoreCase("00:00:00") || date.equalsIgnoreCase("0/0/0 0:0")
                    || date.equalsIgnoreCase("0/0/0 0:0:0")
                    || date.equalsIgnoreCase("00/00/00 00:00") || date.equalsIgnoreCase("00/00/00 00:00:00")) {
                return "00-000-0000 00:00:00";
            }
            if (date.contains("/")) {
                String a[] = date.split("/");
                if (a.length == 3) {
                    if (!a[2].contains(" "))
                        date1 = padString(a[0]) + "-" + padString(a[1]) + "-" + correctYearTimeFormat(a[2]);
                    else {
                        String b[] = a[2].split(" ");
                        if (b.length == 3) {
                            if (b[0].length() == 2)
                                b[0] = "20" + b[0];
                            b[1] = timeAMPMFormat(b[1], b[2]);
                            date1 = padString(a[0]) + "-" + padString(a[1]) + "-" + b[0] + " " + b[1];
                        } else if (b.length == 2) {
                            if (b[0].length() == 2)
                                b[0] = "20" + b[0];
                            b[1] = timeAMPMFormat(b[1]);
                            date1 = padString(a[0]) + "-" + padString(a[1]) + "-" + b[0] + " " + b[1];
                        }
                    }
                }
            } else if (date.contains("-")) {
                String a[] = date.split("-");
                if (a.length == 3) {
                    String b[] = a[2].split(" ");
                    if (b[0].length() == 4) {
                        String d = b[0].substring(2);
                        date1 = padString(a[0]) + "-" + padString(a[1]) + "-20" + d + " " + timeAMPMFormat(b[1]);
                    } else
                        date1 = padString(a[0]) + "-" + padString(a[1]) + "-" + correctYearTimeFormat(a[2]);
                }
            }
        }
        return dateStringWithTime(stringToDateWithTime(date1));
    }

    // Old IbillApp
    private static String timeAMPMFormat(String a, String b) {
        //5:00:00:000:PM or 5:00:00:PM or 2:00 PM
        String str = null;
        if (a.contains(":")) {
            String k[] = a.split(":");
            switch (k.length) {
                case 0:
                    break;
                case 1:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    str = k[0];
                    break;

                case 2:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    if (!k[1].contains("AM")) {
                        if (k[1].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            k[1] = "";
                        } else
                            k[1] = k[1].length() > 1 ? k[1] : padString(k[1]) + ":" + padString("0");
                    }
                    if (b.contains("PM"))
                        str = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12) + ":" + k[1];
                    else
                        str = getIntegerValue(k[0]) == 12 ? padString("0") : k[0] + ":" + k[1];
                    break;

                case 3:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                    if (!k[2].contains("AM")) {
                        if (k[2].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            k[2] = "";
                            str = k[0] + ":" + k[1];
                        } else {
                            k[2] = k[2].length() > 1 ? k[2] : padString(k[2]);
                            str = k[0] + ":" + k[1] + ":" + k[2];
                        }
                    } else {
                        if (b.contains("PM"))
                            str = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12) + ":" + k[1];
                        else
                            str = getIntegerValue(k[0]) == 12 ? padString("0") : k[0] + ":" + k[1];
                    }
                    break;
                case 4:
                    //5:00:00:000 or 5:00:00:PM
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                    k[2] = k[2].length() > 1 ? k[2] : padString(k[2]);
                    if (!k[3].contains("AM")) {
                        if (k[3].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            k[3] = "";
                            str = k[0] + ":" + k[1] + ":" + k[2];
                        } else {
                            if (k[3].length() != 3) {
                                k[3] = k[3].length() > 1 ? k[3] : padString(k[3]);
                                str = k[0] + ":" + k[1] + ":" + k[2] + ":" + k[3];
                            } else
                                str = k[0] + ":" + k[1] + ":" + k[2];
                        }
                    } else
                        str = getIntegerValue(k[0]) == 12 ? padString("0") : k[0] + ":" + k[1] + ":" + k[2];

                    break;
                case 5:
                    //5:00:00:000:PM
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                    k[2] = k[2].length() > 1 ? k[2] : padString(k[2]);
                    k[3] = k[3].length() > 1 ? k[3] : padString(k[3]);
                    if (!k[4].contains("AM")) {
                        if (k[4].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            // k[4] = "";
                            // str = k[0]+":"+k[1]+":"+k[2]+":"+k[3];
                            str = k[0] + ":" + k[1] + ":" + k[2];
                        }
                        //  else {
                        //    k[4] = k[4].length() > 1 ? k[4] : padString(k[4]);
                        //  str = k[0]+":"+k[1]+":"+k[2]+":"+k[3]+":"+k[4];
                        // }
                    } else
                        str = getIntegerValue(k[0]) == 12 ? padString("0") : k[0] + ":" + k[1] + ":" + k[2];

                    break;
            }
        }
        return str;
    }

    // Old IbillApp
    private static String timeAMPMFormat(String a) {
        //5:00:00:000:PM or 5:00:00:PM or 2:00 PM or 12:00:AM
        String str = null;
        if (a.contains(":")) {
            String k[] = a.split(":");
            switch (k.length) {
                case 0:
                    str = "00:00:00";
                    break;
                case 1:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    str = k[0] + ":00:00";
                    break;

                case 2:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    if (!k[1].contains("AM")) {
                        if (k[1].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            k[1] = "";
                            str = (getIntegerValue(k[0]) == 12 ? padString("0") : k[0]) + ":00:00";
                        } else {
                            k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                            str = k[0] + ":" + k[1] + ":" + padString("0");
                        }
                    } else
                        str = (getIntegerValue(k[0]) == 12 ? padString("0") : k[0]) + ":00:00";
                    break;

                case 3:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                    if (!k[2].contains("AM")) {
                        if (k[2].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            k[2] = "";
                            str = k[0] + ":" + k[1] + ":00";
                        } else {
                            k[2] = k[2].length() > 1 ? k[2] : padString(k[2]);
                            str = k[0] + ":" + k[1] + ":" + k[2];
                        }
                    } else {
                        k[0] = getIntegerValue(k[0]) == 12 ? padString("0") : k[0];
                        str = k[0] + ":" + k[1] + ":00";
                        Log.e("Str", "" + str);
                    }
                    break;
                case 4:
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                    k[2] = k[2].length() > 1 ? k[2] : padString(k[2]);
                    if (!k[3].contains("AM")) {
                        if (k[3].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            k[3] = "";
                            str = k[0] + ":" + k[1] + ":" + k[2];
                        } else {
                            if (k[3].length() != 3) {
                                k[3] = k[3].length() > 1 ? k[3] : padString(k[3]);
                                str = k[0] + ":" + k[1] + ":" + k[2] + ":" + k[3];
                            } else
                                str = k[0] + ":" + k[1] + ":" + k[2];
                        }
                    } else {
                        k[0] = getIntegerValue(k[0]) == 12 ? padString("0") : k[0];
                        str = k[0] + ":" + k[1] + ":" + k[2];
                    }
                    break;
                case 5:
                    //5:00:00:000:PM
                    k[0] = k[0].length() > 1 ? k[0] : padString(k[0]);
                    k[1] = k[1].length() > 1 ? k[1] : padString(k[1]);
                    k[2] = k[2].length() > 1 ? k[2] : padString(k[2]);
                    k[3] = k[3].length() > 1 ? k[3] : padString(k[3]);
                    if (!k[4].contains("AM")) {
                        if (k[4].contains("PM")) {
                            k[0] = String.valueOf(getIntegerValue(k[0]) == 12 ? 12 : getIntegerValue(k[0]) + 12);
                            // k[4] = "";
                            // str = k[0]+":"+k[1]+":"+k[2]+":"+k[3];
                            str = k[0] + ":" + k[1] + ":" + k[2];
                        }
                        //  else {
                        //    k[4] = k[4].length() > 1 ? k[4] : padString(k[4]);
                        //  str = k[0]+":"+k[1]+":"+k[2]+":"+k[3]+":"+k[4];
                        // }
                    } else {
                        k[0] = getIntegerValue(k[0]) == 12 ? padString("0") : k[0];
                        str = k[0] + ":" + k[1] + ":" + k[2];
                    }
                    break;
            }
        }
        return str;
    }

    // Old IbillApp
    public static Date stringToDateWithTime(String Date) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

            date = formatter.parse(Date);
        } catch (ParseException pe) {
            // pe.printStackTrace();
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);

                date = formatter.parse(Date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }


    public static Date stringToDate_withTimeMM(String Date) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

            date = formatter.parse(Date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

    public static String makeDateTime(String day, String month, String year, String hh, String mm, String ss) {
        String date = null;
        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(month) && !TextUtils.isEmpty(year)) {
            if (TextUtils.isEmpty(hh))
                hh = "00";
            else hh = padString(hh);
            if (TextUtils.isEmpty(mm))
                mm = "00";
            else
                mm = padString(mm);
            if (TextUtils.isEmpty(ss))
                ss = "00";
            else ss = padString(ss);

            date = padString(day) + "-" + padString(month) + "-" + padString(year) + " " + hh + ":" + mm + ":" + ss;
        }
        if (date != null) {
            try {

                Date date1 = Utils.stringToDate_withTimeMM(date);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

                date = formatter.format(date1);
            } catch (Exception pe) {
                pe.printStackTrace();
            }

        }

        return date;
    }

    public static String correctYearTimeFormat(String str) {
        // date = 181:30:00:PM
        String str1 = str;
        if (str != null && !str.isEmpty()) {
            if (str.contains(":")) {
                String a[] = str.split(":");
                String year = a[0];
                String hour = "";
                if (a[0].length() > 2) {
                    if (a[0].length() > 4) {
                        year = a[0].substring(0, 4);
                        hour = a[0].substring(4, a[0].length());
                    } else {
                        year = a[0].substring(0, 2);
                        year = "20" + year;
                        hour = a[0].substring(2, a[0].length());
                    }

                    if (a[a.length - 1].equals("AM") || a[a.length - 1].equals("PM")) {
                        if (a[a.length - 1].equals("PM"))
                            hour = String.valueOf(getIntegerValue(hour) + 12);
                        a[a.length - 1] = "";
                    }
                }

                str1 = year + " " + padString(hour);
                for (int i = 1; i < a.length; i++) {
                    if (a[i].length() > 0)
                        str1 = str1 + ":" + a[i];
                }
            } else if (str.length() == 2)
                str1 = "20" + str + " 00:00:00";
        }

        return str1;
    }

    public static void playBeep(Context context, String beepSoundFile) {
        MediaPlayer m = new MediaPlayer();
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = context.getAssets().openFd(beepSoundFile != null ? beepSoundFile : "beep.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setClient(GXDLMSClient client, IGXMedia serial, String authentication, String password, int clientaddress, int serveraddress, String interfaceType, boolean logicalNameReferencing) throws Exception {
        if (WifiConnectivity.getIsWifi() || serial.isOpen()) {
            client.setAuthentication(Authentication.valueOf(authentication));
            client.setPassword(password.getBytes());
            client.setClientAddress(clientaddress);
            client.setServerAddress(serveraddress);
            client.setInterfaceType(InterfaceType.valueOf(interfaceType));
            client.setUseLogicalNameReferencing(logicalNameReferencing);
        }

    }

    public static void setClient(GXDLMSSecureClient client, GXSerial serial, String password, int clientaddress, int serveraddress, String interfaceType, boolean logicalNameReferencing) throws Exception {
        if (WifiConnectivity.getIsWifi() || serial.isOpen()) {
            client.setPassword(password.getBytes());
            client.setClientAddress(clientaddress);
            client.setServerAddress(serveraddress);
            client.setInterfaceType(InterfaceType.valueOf(interfaceType));
            client.setUseLogicalNameReferencing(logicalNameReferencing);
        }
    }

    public static void openSerialPort(GXSerial serial, int baudRate, int databit, String stopBits, String parity) throws Exception {
        try {
            serial.setBaudRate(BaudRate.forValue(baudRate));
            serial.setDataBits(databit);
            serial.setStopBits(StopBits.valueOf(stopBits));
            serial.setParity(Parity.valueOf(parity));
            serial.open();
        } catch (Exception ex) {

            ex.getMessage();
        }
    }

    public static GXSerial findPorts(Context context) {

        GXSerial serial = new GXSerial(context);
        if (!WifiConnectivity.getIsWifi()) {
            if (serial.getPorts().length > 0) {
                serial.setPort(serial.getPorts()[0]);
            } else
                serial = null;
        }
/* if (ports.size() == 0) {
            throw new Exception("Please check the cable and try again.");
        }*/
        return serial;
    }

    public static String get_Milli_To_Time(long millis) {
        String time = "";
        try {
            time = String.format(Locale.ENGLISH, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        } catch (Exception e) {
            e.getMessage();

        }
        return time;

    }

    public static String getValue(Object val) {
        String value = null;
        try {
            if (val instanceof byte[]) {
                String hexValue = GXCommon.bytesToHex((byte[]) val).replaceAll(" ", "");
                // if character is falls in the range of non printable character than we are converting that one into decimal otherwise in ascii.
                if (Integer.parseInt(hexValue.substring(0, 2), 16) < 0x20 || Integer.parseInt(hexValue.substring(0, 2), 16) > 0x7f)
                    value = String.valueOf(Integer.parseInt(hexValue, 16));
                else
                    value = hexToAscii(hexValue);
            } else {
                value = val.toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Date stringToDate_WithSlash(String Date) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            date = formatter.parse(Date);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

            if (date != null)
                date = dateFormat.parse(dateFormat.format(date));


        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

    public static String hexToAscii(String hxStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hxStr.length(); i += 2) {
            String str = hxStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String Multiply_By_K_Parameter(String value) {
        try {
            if (value != null) {
                value = BigDecimal.valueOf(Double.parseDouble(value) * Math.pow(10.0, -3.0)).toString();
            }
        } catch (Exception ex) {

            ex.getMessage();
        }

        return value;
    }

    public static String MilliToDate(long miliSec) {
        String date = "";
        try {
            DateFormat simple = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
            Date result = new Date(miliSec);
            date = simple.format(result).toString();
        } catch (Exception e) {
            e.getMessage();
        }
        return date;
    }

    public static byte[] AuthPassword() {
        return PacketFormation((byte) 0x16, (byte) 0x14, "Indi@123".getBytes());
    }

    public static byte[] ConfigureCommunicationParameters(int baudrate, int databits, int parity) {
        byte[] BaudRate = getBaudrate(baudrate);
        byte[] DataBits = getDataBits(databits);
        byte[] StopBits = new byte[]{0x31};
        byte[] Parity = getParity(parity);
        byte[] Packet = new byte[]{BaudRate[0], BaudRate[1], BaudRate[2], BaudRate[3], DataBits[0], StopBits[0], Parity[0]};
        return PacketFormation((byte) 0x0D, (byte) 0x12, Packet);

    }

    public static byte[] getBaudrate(int baudrate) {
        switch (baudrate) {
            case 300:
                return new byte[]{(byte) 0x2C, 0x01, 0x00, 0x00};
            case 4800:
                return new byte[]{(byte) 0xC0, 0x12, 0x00, 0x00};
            default:
                return new byte[]{(byte) 0x80, 0x25, 0x00, 0x00};
        }
    }

    public static byte[] getDataBits(int databits) {
        switch (databits) {
            case 7:
                return new byte[]{0x37};
            default:
                return new byte[]{0x38};
        }
    }

    public static byte[] getParity(int parity) {
        switch (parity) {
            case 1:
                return new byte[]{0x31};
            case 2:
                return new byte[]{0x32};
            default:
                return new byte[]{0x30};
        }
    }

    public static byte[] PacketFormation(byte packet_length, byte packet_id, byte[] dataPacket) {
        byte[] header_bytes = new byte[]{0x55, (byte) 0xAA};
        byte[] Packet = new byte[packet_length - 2];
        Packet[0] = header_bytes[0];
        Packet[1] = header_bytes[1];
        Packet[2] = packet_length;
        Packet[3] = packet_id;
        for (int i = 4; i < dataPacket.length + 4; i++) {
            Packet[i] = dataPacket[i - 4];
        }
        byte[] crcByte = Calculate_Crc(Packet, Packet.length);
        byte[] PacketWithCRC = new byte[Packet.length + 2];
        System.arraycopy(Packet, 0, PacketWithCRC, 0, Packet.length);
        PacketWithCRC[Packet.length] = crcByte[0];
        PacketWithCRC[Packet.length + 1] = crcByte[1];
        return PacketWithCRC;
    }

    public static byte[] Calculate_Crc(byte[] bytes_to_calculate_crc, int length) {
        int i = 0;
        byte crc_lower_byte = (byte) 0xFF;
        byte crc_upper_byte = (byte) 0xFF;

        for (i = 0; i < length; i++) {
            if (i % 2 == 0)
                crc_lower_byte ^= bytes_to_calculate_crc[i];
            else
                crc_upper_byte ^= bytes_to_calculate_crc[i];
        }

        return new byte[]{crc_lower_byte, crc_upper_byte};
    }

    public static byte[] EnableTransparenetMode() {

        return PacketFormation((byte) 0x07, (byte) 0x13, new byte[]{0x01});
    }

 /*   public static byte[] EnableCommandMode() {

        return PacketFormation((byte) 0x07, (byte) 0x14, new byte[]{0x01});

    }*/

    public static byte[] EnableCommandMode() {

        return PacketFormation((byte) 0x07, (byte) 0x13, new byte[]{0x00});

    }

    public static byte[] disConnect() {

        return PacketFormation((byte) 0x06, (byte) 0x17, new byte[]{});

    }

    public static double find_Largest(String[] arr) {
        int i;
        double max = Double.parseDouble(arr[0]);
        for (i = 1; i < arr.length; i++)
            if (Double.parseDouble(arr[i]) > max)
                max = Double.parseDouble(arr[i]);

        return max;
    }

    public static int hex2decimal(String s) {
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

    public static void OwnSleep(long milli) {
        try {
            sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static byte[] BatteryVoltageData() {

        return PacketFormation((byte) 0x06, (byte) 0x16, new byte[]{});

    }

    public static float HexToFloat(String hexValue) {
        Float floatValue = null;
        try {
            Long i = Long.parseLong(hexValue, 16);
            floatValue = Float.intBitsToFloat(i.intValue());
        } catch (Exception e) {

            e.getMessage().toString();
        }
        return floatValue;
    }

    public static String ByteArrayToHexString(byte[] byteArray) {
        String hexString = "";
        for (int i = 0; i < byteArray.length; i++) {
            String thisByte = "".format("%x", byteArray[i]);
            if (thisByte.length() == 1) {
                thisByte = "0" + thisByte;
            }
            hexString += thisByte;
        }

        return hexString;
    }
}
package com.inventive.ugosmartbuddy.mrilib.mri;

import static com.inventive.ugosmartbuddy.mrilib.common.Utils.Multiply_By_K_Parameter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.inventive.ugosmartbuddy.mrilib.common.Non_DLMS_Parameter;
import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.listener.MeterCallbackListener;
import com.inventive.ugosmartbuddy.mrilib.listener.MeterDataListener;
import com.inventive.ugosmartbuddy.mrilib.listener.MeterMisMatchListener;
import com.inventive.ugosmartbuddy.mrilib.listener.UpdateMeterProfileListener;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase.Avon_SPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase.Genus_SinglePhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase.HPL_SPEM01_SPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase.LNT_EM101_SPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase.Landis_gyr_Nondlms;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.singlephase.VisionTex_SPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase.Avon_TPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase.Genus_With_Protocol;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase.HPL_PPEM01_TPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase.LNT_TPhase_NONDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase.Secure_Tphase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.meterclasses.threephase.VisionTex_TPhase_NonDLMS;
import com.inventive.ugosmartbuddy.mrilib.models.Devices;
import com.inventive.ugosmartbuddy.mrilib.models.OBIS;
import com.inventive.ugosmartbuddy.mrilib.models.Readings;
import com.inventive.ugosmartbuddy.mrilib.readData.Constants;
import com.inventive.ugosmartbuddy.mrilib.readData.GXCommunicate;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSClient;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSClock;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSData;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSProfileGeneric;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSReader;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDLMSRegister;
import com.inventive.ugosmartbuddy.mrilib.readData.GXDateTime;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.enums.TraceLevel;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXReceiveThread;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * this class have all t methods to read data from meter, saving data and parsing of data.
 */
public class MRIAsync extends AsyncTask<Void, Integer, Boolean> {
    private static final String TAG = MRIAsync.class.getSimpleName();
    Boolean is_BillingClicked, is_TamperClicked, is_LoadClicked;
    GXSerial serial;
    private String EmployeeCode;
    String Load_Days;
    private JSONArray Load_survey_s = null;
    @SuppressLint("StaticFieldLeak")
    Context mActivity;
    String companyName, protocol, MeterMakeId;
    private String accountNo;
    boolean switchBilling;
    boolean switchLoad;
    boolean switchTamper;
    private Readings readings = null;
    MeterCallbackListener meterCallbackListener;
    WeakReference<TextView> txtPleaseWait;
    Handler handler = new Handler(Looper.getMainLooper());
    private Date startDate = null, endDate = null;
    private static UpdateMeterProfileListener updateMeterProfileListener;
    String connectionMedium = "Probe";
    private long start_time = 0;
    long stop_time = 0;
    int protocolCode;
    boolean IsSinglePhase;
    ArrayList<OBIS> general;
    ArrayList<OBIS> instantaneous;
    ArrayList<OBIS> billing;
    ArrayList<OBIS> loadsurvey;
    ArrayList<OBIS> tamper;
    int baudRate;
    int databit;
    String stopBits;
    String parity;
    String authentication, password;
    int clientaddress, serveraddress;
    String interfaceType;
    String blockCipherKey;
    String authKey;
    String systemTittle;
    String manufecturer;
    String phaseType;
    boolean logicalNameReferencing;
    int Utility_Code;
    Devices devices;
    private static MeterMisMatchListener meterMisMatchListener;
    private Object[] cells = new Object[1];
    private List<String> obisCodeList;

    public MRIAsync(int Utility_Code, int protocolCode, boolean IsSinglePhase, TextView txtPleaseWait, Context context,
                    String MeterMakeId,boolean switchBilling, boolean switchLoad,
                    boolean switchTamper, String Load_Days, GXSerial serial, Boolean is_BillingClicked,
                    Boolean is_TamperClicked, Boolean is_LoadClicked,
                    ArrayList<OBIS> general, ArrayList<OBIS> instantaneous, ArrayList<OBIS> billing,
                    ArrayList<OBIS> loadsurvey, ArrayList<OBIS> tamper, int baudRate, int databit,
                    String stopBits, String parity, String authentication, String password, int clientaddress,
                    int serveraddress, String interfaceType, boolean logicalNameReferencing,String blockCipherKey,
                    String authKey,String systemTittle,String manufecturer,String phaseType,
                    MeterCallbackListener meterCallbackListener) {
        this.protocolCode = protocolCode;
        this.IsSinglePhase = IsSinglePhase;
        this.txtPleaseWait = new WeakReference<>(txtPleaseWait);
        this.mActivity = context;
        this.switchBilling = switchBilling;
        this.switchLoad = switchLoad;
        this.switchTamper = switchTamper;
        this.meterCallbackListener = meterCallbackListener;
        this.Load_Days = Load_Days;
        this.serial = serial;
        this.is_BillingClicked = is_BillingClicked;
        this.is_TamperClicked = is_TamperClicked;
        this.is_LoadClicked = is_LoadClicked;
        this.MeterMakeId = MeterMakeId;
        this.general = general;
        this.instantaneous = instantaneous;
        this.billing = billing;
        this.loadsurvey = loadsurvey;
        this.tamper = tamper;
        this.baudRate = baudRate;
        this.databit = databit;
        this.stopBits = stopBits;
        this.parity = parity;
        this.authentication = authentication;
        this.password = password;
        // this.password = "1234567898765432";
        this.clientaddress = clientaddress;
        this.serveraddress = serveraddress;
        //this.interfaceType = interfaceType;
        this.interfaceType = interfaceType;
        this.logicalNameReferencing = logicalNameReferencing;
        this.blockCipherKey = blockCipherKey;
        this.authKey = authKey;
        this.systemTittle = systemTittle;
        this.manufecturer = manufecturer;
        this.phaseType = phaseType;
        this.Utility_Code = Utility_Code;
        if (readings == null)
            readings = new Readings();
    }

    public static void setMeterMisMatchListener(MeterMisMatchListener meterMisMatchListener1) {
        meterMisMatchListener = meterMisMatchListener1;
    }

    public static void setUpdateMeterProfileListener(UpdateMeterProfileListener updateMeterProfileListener1) {
        updateMeterProfileListener = updateMeterProfileListener1;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        try {
            if (readings != null) {
                updateMeterProfileListener.updateMeterProfile(readings, readings.getBILLING_READING() != null, false, false, false, false, false, true);
            } else
                updateMeterProfileListener.updateMeterProfile(readings, false, true, false, true, false, true, true);

            //  if(aVoid)
            Utils.playBeep(mActivity, "beep1.wav");
            super.onPostExecute(aVoid);
        } catch (Exception ex) {
            readings.setEXCEPTION_MESSAGE(ex.getMessage());
            ex.getMessage();
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        txtPleaseWait.get().setText("");
        start_time = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isRead = false;


        MeterDataListener listener = new MeterDataListener() {
            @Override
            public void onReceive(String operation, String data) {
                publishProgress(Integer.parseInt(data));
            }
        };

        GXReceiveThread.setMeterDataListener(listener);
        try {

            if (protocolCode == 0 || protocolCode == 1)
                isRead = readData();

            if (!isRead && protocolCode != 1) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        updateMeterProfileListener.updateProgress(true);
                    }
                });

                if (IsSinglePhase)
                    isRead = receivedNew_NonDLMS_Data_1Phase(serial);
                else
                    isRead = receivedNew_NonDLMS_Data_3Phase(serial);

            }

        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
            e.getMessage();
        } finally {
            if (serial != null && serial.isOpen())
                serial.close();
        }
        return isRead;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        long millis = (System.currentTimeMillis() - start_time);
        txtPleaseWait.get().setText("Reading Time: " + Utils.get_Milli_To_Time(millis) + " \n" + "Received_bytes " + values[0]);
    }

    private boolean receivedNew_NonDLMS_Data_1Phase(GXSerial serial) {
        boolean isRead = false;
        Non_DLMS_Parameter non_dlms_parameter = null;
        if (is_BillingClicked) {
            try {
                switch (MeterMakeId) {
                    case "4":
                        LNT_EM101_SPhase_NonDLMS lnt_1Phase = new LNT_EM101_SPhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = lnt_1Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "3":
                        HPL_SPEM01_SPhase_NonDLMS hpl_1Phase = new HPL_SPEM01_SPhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = hpl_1Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "5":
                        Landis_gyr_Nondlms landis_1Phase = new Landis_gyr_Nondlms(Utility_Code);
                        non_dlms_parameter = landis_1Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "2":
                        Genus_SinglePhase_NonDLMS genus_1Phase = new Genus_SinglePhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = genus_1Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "8":
                        Avon_SPhase_NonDLMS avon_SPhase_NonDLMS = new Avon_SPhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = avon_SPhase_NonDLMS.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "14":
                        VisionTex_SPhase_NonDLMS visionTex_SPhase_NonDLMS = new VisionTex_SPhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = visionTex_SPhase_NonDLMS.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                }
            } catch (Exception e) {
                readings.setEXCEPTION_MESSAGE(e.getMessage());
                e.printStackTrace();
            }
        }
        return isRead;
    }

    private boolean receivedNew_NonDLMS_Data_3Phase(GXSerial serial) {
        boolean isRead = false;
        Non_DLMS_Parameter non_dlms_parameter = null;
        if (is_BillingClicked) {
            try {
                switch (MeterMakeId) {
                    case "4":
                        LNT_TPhase_NONDLMS lnt_threePhase = new LNT_TPhase_NONDLMS(Utility_Code);
                        non_dlms_parameter = lnt_threePhase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;
                    case "3":
                        HPL_PPEM01_TPhase_NonDLMS hpl_3Phase = new HPL_PPEM01_TPhase_NonDLMS(Utility_Code);
                        // HPL_TPhase_JVVNL hpl_3Phase = new HPL_TPhase_JVVNL(Utility_Code);
                        non_dlms_parameter = hpl_3Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;
                    case "5":
                        break;
                    case "2":
                        Genus_With_Protocol genus_3Phase = new Genus_With_Protocol(Utility_Code);
                        non_dlms_parameter = genus_3Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "1":
                        Secure_Tphase_NonDLMS secure_3Phase = new Secure_Tphase_NonDLMS(Utility_Code);
                        non_dlms_parameter = secure_3Phase.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;

                    case "8":
                        Avon_TPhase_NonDLMS avon_TPhase_NonDLMS = new Avon_TPhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = avon_TPhase_NonDLMS.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;
                    case "14":
                        VisionTex_TPhase_NonDLMS visionTex_TPhase_NonDLMS = new VisionTex_TPhase_NonDLMS(Utility_Code);
                        non_dlms_parameter = visionTex_TPhase_NonDLMS.get_JsonParameters(serial);
                        isRead = saveMeterData(non_dlms_parameter);
                        break;
                }
            } catch (Exception e) {
                readings.setEXCEPTION_MESSAGE(e.getMessage());

            }
        }
        return isRead;
    }

    void updateBillingsExceptions() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
            }
        });
    }

    void updateBillings(boolean isBilling) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateMeterProfileListener.updateMeterProfile(readings, readings.getBILLING_READING() != null ? true : false, false, false, false, false, false, false);
            }
        });
    }

    boolean saveMeterData(Non_DLMS_Parameter non_dlms_parameter) {
        boolean isRead = false;
        String meterNumber = non_dlms_parameter.getMETER_NO();
        String active_energy = non_dlms_parameter.getACTIVE_ENERGY();
        try {
            if (meterNumber != null && meterNumber.length() > 4 && !meterNumber.trim().startsWith("-") && active_energy != null && active_energy != "") {
                if (readings == null)
                    readings = new Readings();

                readings.setGENERAL_READING(non_dlms_parameter.getGeneral(non_dlms_parameter).toString());
                readings.setINSTANTENOUS_READING(non_dlms_parameter.getInstantaneous(non_dlms_parameter).toString());
                readings.setBILLING_READING(non_dlms_parameter.getBilling(non_dlms_parameter).toString());
                readings.setLOADSURVEY_READING(null);
                readings.setTAMPER_READING(null);
                readings.setLOADSURVEY_SCALER(null);
                meterCallbackListener.updateOnSuccess(non_dlms_parameter.getMETER_NO());
                readings.setMETER_NO(non_dlms_parameter.getMETER_NO());
                readings.setRTC(non_dlms_parameter.getBILLING_DATE());
                readings.setKWH(non_dlms_parameter.getACTIVE_ENERGY());
                readings.setKVAH(non_dlms_parameter.getAPPARENT_ENERGY());
                readings.setKW(non_dlms_parameter.getMAXIMUM_DEMAND_KW_HISTORY());
                readings.setKVA(non_dlms_parameter.getMAXIMUM_DEMAND_KVA_HISTORY());

                if (readings.getGENERAL_READING() != null && readings.getINSTANTENOUS_READING() != null && readings.getBILLING_READING() != null)
                    updateBillings(true);
                else
                    updateBillingsExceptions();

                stop_time = (System.currentTimeMillis() - start_time);

                readings.setTIMESTAMP(Utils.dateToString_withTime(new Date()));
                isRead = true;
            } else {
                updateBillingsExceptions();
                readings.setEXCEPTION_MESSAGE("Meter Number Not Found.");
            }
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
            e.printStackTrace();
        }
        return isRead;
    }

    /*
     * setting the serial port configuration and initializationing connection with meter.
     *
     * @return true if data reading done successfully otherwise false.*/

    public boolean readData() throws Exception {
        boolean Is_MRI_Done = false;
        GXCommunicate reader = null;
        try {
            if (serial != null || WifiConnectivity.getIsWifi()) {
                Utils.openSerialPort(serial, baudRate, databit, stopBits, parity);
                if (WifiConnectivity.getIsWifi() || serial.isOpen()) {

                    if (authentication.equalsIgnoreCase("High")) {
                        /*password = "0TMP4GSM01035820";
                        devices = new Devices();
                        devices.setAuthKey("1234567890123456");
                        devices.setCipheringKey("1234567890123456");
                        devices.setCipheringMode("YES");
                        devices.setSystemTitle("LTCLIENT");
                        devices.setAuthMode("High");
                        devices.setHighPwd(password);
                        devices.setLowPwd("lnt1");
                        devices.setManufacturer("LNT");
                        devices.setDeviceType("Single Phase");*/


                        devices = new Devices();
                        devices.setAuthKey(authKey);
                        devices.setCipheringKey(blockCipherKey);
                        devices.setCipheringMode("YES");
                        devices.setSystemTitle(systemTittle);
                        devices.setAuthMode(authentication);
                        devices.setHighPwd(password);
                        devices.setLowPwd(password);
                        devices.setManufacturer(manufecturer);
                        devices.setDeviceType(phaseType);



                        try {
                            reader = new GXCommunicate().getManufactureSettings(serial, "High", mActivity.getAssets(), mActivity);
                            reader.initializeConnection(Constants.InitializationMode.HIGH, devices);
                            Log.d("TAG", "Connection Established");
                            Thread.sleep(200);
                            //reader.checkStatus();
                            Is_MRI_Done = readMeter(reader, serial);
                        } catch (Exception e) {
                            readings.setEXCEPTION_MESSAGE(e.getMessage());
                            if (e.getMessage() != null && e.getMessage().equals("Invalid tag.")) {
                                Is_MRI_Done = readMeter(reader, serial);
                            } else {
                                handler.post(() -> updateMeterProfileListener.updateMeterProfile(readings, false, true, false, true, false, true, false));
                            }
                        }
                    }
                    else {
                        GXDLMSClient client = new GXDLMSClient();
                        GXDLMSReader reader1 = new GXDLMSReader(client, serial, TraceLevel.OFF);
                        if (MeterMakeId.equals("4")) {
                            if (WifiConnectivity.getIsWifi())
                                reader1.setWaitTime(5000);
                            else
                                reader1.setWaitTime(1000);
                        }
                        Utils.setClient(client, serial, authentication, password, clientaddress, serveraddress, interfaceType, logicalNameReferencing);

                        try {
                            reader1.initializeConnection();
                            Is_MRI_Done = readMeter(reader1, serial);

                        } catch (Exception e) {
                            readings.setEXCEPTION_MESSAGE(e.getMessage());
                            if (e.getMessage() != null && e.getMessage().equals("Invalid tag.")) {
                                Is_MRI_Done = readMeter(reader1, serial);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, true, false, true, false, true, false);
                                    }
                                });
                            }

                        }

                    }


                }
            }
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
        } finally {
            if (WifiConnectivity.getIsWifi() || (serial != null && serial.isOpen())) {
                if (reader != null) {
                    try {
                        reader.close();
                        serial.close();
                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }
                }
            }
        }
        return Is_MRI_Done;
    }

    /*
     * in this method, reading and saving of  all data will be done.
     *
     * @param reader object of reader class to access reading methods
     * @param serial object of serial class to access serial methods
     * @return true if reading is done successfully otherwise false.
     * @throws Exception*/
    private boolean readMeter(GXCommunicate reader, GXSerial serial) throws Exception {
        boolean Is_Reading_Done = false;

        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                if (readings == null)
                    readings = new Readings();
                Log.d("TAG", "Request send for RTC: ");
                String rtc = Utils.dateStringWithTime(((GXDateTime) reader.readObject(new GXDLMSClock("0.0.1.0.0.255"), 2)).getLocalCalendar().getTime());
                readings.setRTC(rtc);
                Log.d("TAG", "Response  for RTC: " + rtc);
                Log.d("TAG", "Request send for Serial NO: ");

                /*GXDLMSData gxdlmsData = new GXDLMSData("0.0.94.96.19.255");
                Object cell = reader.readAllRowsPrepayData(gxdlmsData);
                if(cell instanceof byte[]) {
                    cells[0] = GXDLMSClient.changeType((byte[]) cell, DataType.DATETIME);
                }*/

          /*      GXDLMSProfileGeneric pg = new GXDLMSProfileGeneric("0.0.94.91.10.255");
                obisCodeList = reader.readObisCodeListObjects(pg);

                cells = reader.readAllRowsData(pg);

*/


                String serialNo = Read_Data_Type(reader, "0.0.96.1.0.255").trim();
                readings.setMETER_NO(serialNo);
                Log.d("TAG", "Response for Serial NO: " + serialNo);
                readings.setISDLMS(true);

                meterCallbackListener.updateOnSuccess(serialNo);

                if (is_BillingClicked) {
                    try {
                        if (switchBilling) {
                            try {
                                JSONArray array = Read_General(general, reader);
                                if (array.length() > 0) {
                                    readings.setGENERAL_READING(array.toString());
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }
                            try {
                                JSONArray array = Read_Instantaneous(instantaneous, reader);
                                if (array.length() > 0) {
                                    readings.setINSTANTENOUS_READING(array.toString());
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }

                            try {
                                JSONArray array = Read_Billing(billing, reader);
                                if (array.length() > 0) {
                                    readings.setBILLING_READING(array.toString());
                                    parseJsonData(readings);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //comment by Prahlad
                                            updateMeterProfileListener.updateMeterProfile(readings, true, false, false, false, false, false, false);
                                        }
                                    });
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                        }
                                    });

                                }

                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }
                        }
                } catch (Exception e) {
                    readings.setEXCEPTION_MESSAGE(e.getMessage());
                }
                }

                if (is_TamperClicked) {
                    try {
                        if (switchTamper) {
                            JSONArray array = Read_Tamper(tamper, reader);
                            if (array.length() > 0) {
                                readings.setTAMPER_READING(array.toString());

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //comment by Prahlad
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, false, true, false, false);
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, false, false, true, false);
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }

                if (is_LoadClicked) {
                    try {
                        if (switchLoad) {

                            try {

                                this.endDate = Utils.stringToDate_WithSlash(rtc);

                                this.startDate = Utils.stringToDate_WithSlash(new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss").format(Utils.addDays(endDate, -Integer.parseInt(Load_Days))));

                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }

                            JSONArray array1 = Read_LoadSurvey(loadsurvey, reader);

                            if (array1.length() > 0) {

                                readings.setLOADSURVEY_READING(array1.toString());
                                readings.setLOADSURVEY_SCALER(Load_survey_s.toString());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //comment by Prahlad
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, true, false, false, false, false);
                                    }
                                });
                            } else {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, true, false, false, false);
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }
            }


        } catch (Exception ex) {
            readings.setEXCEPTION_MESSAGE(ex.getMessage());
        } finally {
            if (serial != null) {
                try {
                    reader.close();
                    serial.close();
                } catch (Exception e) {
                    StringWriter stackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTrace));
                    //Utils.saveError(mActivity, accountNo, "", companyName, "", login, "readMeter finally", stackTrace.toString());
                }
            }
        }
        return Is_Reading_Done;
    }

   /* private boolean readMeter(GXDLMSReader reader, GXSerial serial) throws Exception {
        boolean Is_Reading_Done = false;
        int attempt = 0;
        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                if (readings == null)
                    readings = new Readings();
                String rtc = null;
                try{
                 //   reader.checkStatus();
                    if (reader.read(new GXDLMSClock("0.0.1.0.0.255"), 2) instanceof GXDateTime) {
                        GXDateTime date = null;
                        date = (GXDateTime) reader.read(new GXDLMSClock("0.0.1.0.0.255"), 2);
                        if (date != null) {
                            rtc = Utils.dateStringWithTime(date.getLocalCalendar().getTime());
                        }
                    }
                    String serialNo = Read_Data_Type(reader, "0.0.96.1.0.255").trim();
                    readings.setRTC(rtc);
                    if (serialNo != null) {
                        readings.setMETER_NO(serialNo);
                        meterCallbackListener.updateOnSuccess(serialNo);
                    }
                    readings.setISDLMS(true);



                }catch (Exception e){
                    reader.checkStatus();
                }


                if (is_BillingClicked) {
                    try {

                        JSONArray array = Read_General(general, reader);
                        if (array.length() > 0) {
                            readings.setGENERAL_READING(array.toString());
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                }
                            });

                        }
                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }
                    try {
                        JSONArray array = Read_Instantaneous(instantaneous, reader);
                        if (array.length() > 0) {
                            readings.setINSTANTENOUS_READING(array.toString());
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                }
                            });

                        }
                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                    try {
                        JSONArray array = Read_Billing(billing, reader);
                        if (array.length() > 0) {
                            readings.setBILLING_READING(array.toString());
                            parseJsonData(readings);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //comment by Prahlad
                                    updateMeterProfileListener.updateMeterProfile(readings, true, false, false, false, false, false, false);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                }
                            });

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }
                }

                if (is_TamperClicked) {
                    try {
                        if (switchTamper) {
                            JSONArray array = Read_Tamper(tamper, reader);
                            if (array.length() > 0) {
                                readings.setTAMPER_READING(array.toString());

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //comment by Prahlad
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, false, true, false, false);
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, false, false, true, false);
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }

                if (is_LoadClicked) {
                    try {
                        if (switchLoad) {

                            try {

                                if (rtc != null) {
                                    this.endDate = Utils.stringToDate_WithSlash(rtc);

                                    this.startDate = Utils.stringToDate_WithSlash(new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss").format(Utils.addDays(endDate, -Integer.parseInt(Load_Days))));
                                }
                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }

                            JSONArray array1 = Read_LoadSurvey(loadsurvey, reader);

                            if (array1.length() > 0) {

                                readings.setLOADSURVEY_READING(array1.toString());
                                readings.setLOADSURVEY_SCALER(Load_survey_s.toString());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //comment by Prahlad
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, true, false, false, false, false);
                                    }
                                });
                            } else {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, true, false, false, false);
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }
            }


        } catch (Exception ex) {
            readings.setEXCEPTION_MESSAGE(ex.getMessage());
        } finally {
                if (serial != null) {
                    try {
                        //reader.close();
                        //serial.close();
                        //reader.checkStatus();
                    } catch (Exception e) {
                        StringWriter stackTrace = new StringWriter();
                        e.printStackTrace(new PrintWriter(stackTrace));
                        //Utils.saveError(mActivity, accountNo, "", companyName, "", login, "readMeter finally", stackTrace.toString());
                    }
                }
        }
        return Is_Reading_Done;
    }*/


    // For Non Smart DLMS Meters
    private boolean readMeter(GXDLMSReader reader, GXSerial serial) throws Exception {
        boolean Is_Reading_Done = false;

        try {
            if (serial.isOpen() || WifiConnectivity.getIsWifi()) {
                if (readings == null)
                    readings = new Readings();
                String rtc = Utils.dateStringWithTime(((GXDateTime) reader.read(new GXDLMSClock("0.0.1.0.0.255"), 2)).getLocalCalendar().getTime());
                String serialNo = Read_Data_Type(reader, "0.0.96.1.0.255").trim();
                readings.setRTC(rtc);
                readings.setMETER_NO(serialNo);
                readings.setISDLMS(true);

                meterCallbackListener.updateOnSuccess(serialNo);

                if (is_BillingClicked) {
                    try {
                        if (switchBilling) {
                            try {

                                JSONArray array = Read_General(general, reader);
                                if (array.length() > 0) {
                                    readings.setGENERAL_READING(array.toString());
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }
                            try {
                                JSONArray array = Read_Instantaneous(instantaneous, reader);
                                if (array.length() > 0) {
                                    readings.setINSTANTENOUS_READING(array.toString());
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }

                            try {
                                JSONArray array = Read_Billing(billing, reader);
                                if (array.length() > 0) {
                                    readings.setBILLING_READING(array.toString());
                                    parseJsonData(readings);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //comment by Prahlad
                                            updateMeterProfileListener.updateMeterProfile(readings, true, false, false, false, false, false, false);
                                        }
                                    });
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateMeterProfileListener.updateMeterProfile(readings, false, true, false, false, false, false, false);
                                        }
                                    });

                                }

                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }
                        }

                    }
                    catch (Exception e)
                    {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }

                if (is_TamperClicked) {
                    try {
                        if (switchTamper) {
                            JSONArray array = Read_Tamper(tamper, reader);
                            if (array.length() > 0) {
                                readings.setTAMPER_READING(array.toString());

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //comment by Prahlad
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, false, true, false, false);
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, false, false, true, false);
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }

                if (is_LoadClicked) {
                    try {
                        if (switchLoad) {

                            try {

                                this.endDate = Utils.stringToDate_WithSlash(rtc);

                                this.startDate = Utils.stringToDate_WithSlash(new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss").format(Utils.addDays(endDate, -Integer.parseInt(Load_Days))));

                            } catch (Exception e) {
                                readings.setEXCEPTION_MESSAGE(e.getMessage());
                            }

                            JSONArray array1 = Read_LoadSurvey(loadsurvey, reader);

                            if (array1.length() > 0) {

                                readings.setLOADSURVEY_READING(array1.toString());
                                readings.setLOADSURVEY_SCALER(Load_survey_s.toString());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //comment by Prahlad
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, true, false, false, false, false);
                                    }
                                });
                            } else {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMeterProfileListener.updateMeterProfile(readings, false, false, false, true, false, false, false);
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }

                }
            }


        } catch (Exception ex) {
            readings.setEXCEPTION_MESSAGE(ex.getMessage());
        } finally {
            if (serial != null) {
                try {
                    reader.close();
                    serial.close();
                } catch (Exception e) {
                    StringWriter stackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTrace));
                    //Utils.saveError(mActivity, accountNo, "", companyName, "", login, "readMeter finally", stackTrace.toString());
                }
            }
        }
        return Is_Reading_Done;
    }


    private JSONArray Read_General(ArrayList<OBIS> arrayList, GXDLMSReader reader) {
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            if (arrayList.size() > 0) {
                for (OBIS obisCodes : arrayList) {
                    object.put("OC", obisCodes.getOBIS_VALUE());
                    if (obisCodes.getOBJECT_TYPE().equals("Data")) {

                        object.put("OV", Read_Data_Type(reader, obisCodes.getOBIS_VALUE()));
                    } else if (obisCodes.getOBJECT_TYPE().equals("Register")) {

                        object.put("OV", Read_Register_Type(reader, obisCodes.getOBIS_VALUE()));
                    }
                    jsonArray.put(object);
                    object = new JSONObject();
                }
                object = new JSONObject();
                object.put("OBIS", jsonArray);

                jsonArray = new JSONArray();
                jsonArray.put(object);
            }

        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            // Utility.saveError(mActivity, accountNo, "", companyName, "",  "Read_General", stackTrace.toString());

        }
        return jsonArray;
    }


    /*
     * abstract method to read billing.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of billing*/

    private JSONArray Read_Billing(ArrayList<OBIS> arrayList, GXDLMSReader reader) {
        return Read(arrayList, reader);
    }

    /*
     * abstract method to read instantaneous.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of instantaneous*/

    private JSONArray Read_Instantaneous(ArrayList<OBIS> arrayList, GXDLMSReader reader) {
        return Read(arrayList, reader);
    }

    /*
     * abstract method to read Loadsurvey.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of Loadsurvey*/

    private JSONArray Read_LoadSurvey(ArrayList<OBIS> arrayList, GXDLMSReader reader) {
        return Read(arrayList, reader);
    }

    /*
     * abstract method to read tamper.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of tamper*/

    private JSONArray Read_Tamper(ArrayList<OBIS> arrayList, GXDLMSReader reader) {
        return Read(arrayList, reader);
    }


    /*
     * for register type objects
     *
     * @param reader     object of reader
     * @param obis_value obis code to read
     * @return string value against obis code after reading*/

    public String Read_Register_Type(GXDLMSReader reader, String obis_value) {
        try {
            GXDLMSRegister register = new GXDLMSRegister(obis_value);
            reader.read(register, 3);
            return Utils.getValue(reader.read(register, 2));
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
            return null;
        }
    }


    /**
     * this is the main method where actual reading and jason formation done.
     * json array is formed after reading and multification except loadsurvey.
     *
     * @param all_obis arraylist of obis codes to read
     * @param reader   object of reader
     * @return json arrayi
     */


    private JSONArray Read(ArrayList<OBIS> all_obis, GXDLMSReader reader) {
        Object[] pgval = null;
        JSONObject jSONObject = new JSONObject();
        JSONArray values_array = new JSONArray();
        JSONArray scaler_array = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        try {
            if (all_obis.size() > 0) {
                GXDLMSProfileGeneric obis_scaler = new GXDLMSProfileGeneric(all_obis.get(0).getOBIS_SCALER());
                reader.read(obis_scaler, 3);
                Object scaler_value = reader.read(obis_scaler, 2);
                String[] Scaler_Obis = new String[obis_scaler.getCaptureObjects().size()];
                for (int i = 0; i < obis_scaler.getCaptureObjects().size(); i++) {
                    Scaler_Obis[i] = obis_scaler.getCaptureObjects().get(i).getKey().getName().toString();
                }
                for (OBIS obisCodes : all_obis) {
                    try {
                        GXDLMSProfileGeneric obis_value = new GXDLMSProfileGeneric(obisCodes.getOBIS_VALUE());
                        reader.read(obis_value, 3);
                        String[] Value_Obis = new String[obis_value.getCaptureObjects().size()];
                        for (int i = 0; i < obis_value.getCaptureObjects().size(); i++) {
                            Value_Obis[i] = obis_value.getCaptureObjects().get(i).getKey().getName().toString();
                        }
                        if (MeterMakeId.equals("4") || MeterMakeId.equals("6") || MeterMakeId.equals("8") || (startDate == null && endDate == null)) {
                            pgval = (Object[]) reader.read(obis_value, 2);
                        } else {
                            pgval = reader.readRowsByRange(obis_value, startDate, endDate);
                        }
                        for (int k = 0; k < pgval.length; k++) {
                            try {
                                for (int i = 0; i < Value_Obis.length; i++) {
                                    jSONObject.put("OC", Value_Obis[i]);
                                    if (((Object[]) pgval[k])[i] instanceof byte[]) {
                                        byte[] date_bytes = (byte[]) (((Object[]) pgval[k])[i]);
                                        ((Object[]) pgval[k])[i] = Utils.makeDateTime(Byte.toString(date_bytes[3]).trim(), Byte.toString(date_bytes[2]).trim(),
                                                "" + Integer.parseInt(Byte.toString(date_bytes[0]).trim() + Long.toHexString(Integer.parseInt(Byte.toString(date_bytes[1]).trim()) & 0x00000000ffffffffL).substring(6, 8), 16),
                                                Byte.toString(date_bytes[5]).trim(), Byte.toString(date_bytes[6]).trim(), Byte.toString(date_bytes[7]).trim());
                                        jSONObject.put("OV", ((Object[]) pgval[k])[i]);
                                    } else {
                                        if (!obisCodes.getPROFILER_GROUP().equals("Loadsurvey")) {
                                            if (Arrays.asList(Scaler_Obis).contains(Value_Obis[i]) && !(((Object[]) pgval[k])[i] instanceof GXDateTime)) {
                                                int mf = ((Byte) ((Object[]) ((Object[]) ((Object[]) scaler_value)[0])[Arrays.asList(Scaler_Obis).indexOf(Value_Obis[i])])[0]).byteValue();
                                                if (mf != 0) {
                                                    jSONObject.put("OV", Double.valueOf(((Object[]) pgval[k])[i].toString()) * Math.pow(10.0d, Double.parseDouble(String.valueOf(mf)))).toString();
                                                } else {
                                                    jSONObject.put("OV", ((Object[]) pgval[k])[i].toString());
                                                }
                                            } else {
                                                if ((((Object[]) pgval[k])[i] instanceof GXDateTime)) {
                                                    jSONObject.put("OV", Utils.MilliToDate(((GXDateTime) (((Object[]) pgval[k]))[i]).getLocalCalendar().getTimeInMillis()));

                                                } else
                                                    jSONObject.put("OV", ((Object[]) pgval[k])[i].toString());
                                            }
                                        } else {
                                            try {
                                                if ((((Object[]) pgval[k])[i] instanceof GXDateTime)) {
                                                    jSONObject.put("OV", Utils.MilliToDate(((GXDateTime) (((Object[]) pgval[k]))[i]).getLocalCalendar().getTimeInMillis()));

                                                } else
                                                    jSONObject.put("OV", ((Object[]) pgval[k])[i].toString());
                                            } catch (Exception e) {
                                                e.getMessage();
                                            }

                                        }

                                    }

                                    values_array.put(jSONObject);
                                    jSONObject = new JSONObject();
                                }
                            } catch (Exception ex) {
                                ex.getMessage();
                            }
                            JSONObject object = new JSONObject();
                            object.put("OBIS", values_array);
                            jsonArray.put(object);
                            values_array = new JSONArray();
                        }
                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }
                }
                try {
                    if (all_obis.get(0).getPROFILER_GROUP().equalsIgnoreCase("LoadSurvey")) {
                        JSONObject scaler_object = new JSONObject();
                        for (int i = 0; i < ((Object[]) scaler_value).length; i++) {
                            for (int j = 0; j < Scaler_Obis.length; j++) {
                                scaler_object.put("OC", Scaler_Obis[j]);
                                scaler_object.put("OV", ((Object[]) ((Object[]) ((Object[]) scaler_value)[0])[j])[0].toString());
                                scaler_array.put(scaler_object);
                                scaler_object = new JSONObject();
                            }
                            JSONObject object = new JSONObject();
                            object.put("OBIS", scaler_array);
                            Load_survey_s = new JSONArray();
                            Load_survey_s.put(object);
                        }
                    }
                } catch (Exception e) {
                    readings.setEXCEPTION_MESSAGE(e.getMessage());
                }
            }
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
        }
        return jsonArray;
    }

    private void parseJsonData(Readings readings) {
        try {
            if (readings != null && readings.getBILLING_READING() != null) {

                JSONArray array = new JSONArray(readings.getBILLING_READING());

                int counter = 0;   // counter will be used to terminate loop. to avoid data repetation.
                if (array.length() > 1) {

                    JSONArray First_Entry = array.getJSONObject(0).getJSONArray("OBIS");

                    JSONArray Last_Entry = array.getJSONObject(array.length() - 1).getJSONArray("OBIS");

                    for (int i = 0; i < First_Entry.length(); i++) {

                        if (First_Entry.getJSONObject(i).get("OC").equals("0.0.0.1.2.255")) {

                            Date date1 = Utils.dateStringWithTime(First_Entry.getJSONObject(i).get("OV").toString());

                            Date date2 = Utils.dateStringWithTime(Last_Entry.getJSONObject(i).get("OV").toString());

                            if (date1.compareTo(date2) > 0) {

                                // when first entry is current month

                                for (int j = 0; j < First_Entry.length(); j++) {

                                    if (First_Entry.getJSONObject(j).get("OC").equals("1.0.1.8.0.255")) {
                                        readings.setKWH(Multiply_By_K_Parameter(First_Entry.getJSONObject(j).get("OV").toString()));
                                        counter++;
                                    }
                                    if (First_Entry.getJSONObject(j).get("OC").equals("1.0.9.8.0.255")) {
                                        readings.setKVAH(Multiply_By_K_Parameter(First_Entry.getJSONObject(j).get("OV").toString()));

                                        counter++;
                                    }
                                    if (array.getJSONObject(1).getJSONArray("OBIS").getJSONObject(j).get("OC").equals("1.0.1.6.0.255")) {
                                        if (!array.getJSONObject(1).getJSONArray("OBIS").getJSONObject(j).get("OV").toString().contains("-")) {
                                            readings.setKW(Multiply_By_K_Parameter(array.getJSONObject(1).getJSONArray("OBIS").getJSONObject(j).get("OV").toString()));

                                            counter++;
                                        }
                                    }

                                    if (array.getJSONObject(1).getJSONArray("OBIS").getJSONObject(j).get("OC").equals("1.0.9.6.0.255")) {
                                        if (!array.getJSONObject(1).getJSONArray("OBIS").getJSONObject(j).get("OV").toString().contains("-")) {
                                            readings.setKVA(Multiply_By_K_Parameter(array.getJSONObject(1).getJSONArray("OBIS").getJSONObject(j).get("OV").toString()));
                                            counter++;
                                        }
                                    }

                                    if (counter == 4)
                                        break;

                                }


                            } else {
                                // when last entry is current month.

                                for (int j = 0; j < Last_Entry.length(); j++) {

                                    if (Last_Entry.getJSONObject(j).get("OC").equals("1.0.1.8.0.255")) {
                                        readings.setKWH(Multiply_By_K_Parameter(Last_Entry.getJSONObject(j).get("OV").toString()));
                                        counter++;
                                    }
                                    if (Last_Entry.getJSONObject(j).get("OC").equals("1.0.9.8.0.255")) {
                                        readings.setKVAH(Multiply_By_K_Parameter(Last_Entry.getJSONObject(j).get("OV").toString()));

                                        counter++;
                                    }
                                    if (array.getJSONObject(array.length() - 2).getJSONArray("OBIS").getJSONObject(j).get("OC").equals("1.0.1.6.0.255")) {
                                        if (!array.getJSONObject(array.length() - 2).getJSONArray("OBIS").getJSONObject(j).get("OV").toString().contains("-")) {
                                            readings.setKW(Multiply_By_K_Parameter(array.getJSONObject(array.length() - 2).getJSONArray("OBIS").getJSONObject(j).get("OV").toString()));
                                            counter++;
                                        }
                                    }

                                    if (array.getJSONObject(array.length() - 2).getJSONArray("OBIS").getJSONObject(j).get("OC").equals("1.0.9.6.0.255")) {
                                        if (!array.getJSONObject(array.length() - 2).getJSONArray("OBIS").getJSONObject(j).get("OV").toString().contains("-")) {
                                            readings.setKVA(Multiply_By_K_Parameter(array.getJSONObject(array.length() - 2).getJSONArray("OBIS").getJSONObject(j).get("OV").toString()));
                                            counter++;
                                        }
                                    }

                                    if (counter == 4)
                                        break;

                                }


                            }
                            break;
                        }
                    }


                } else {
                    // only have single month data.

                    JSONArray First_Entry = array.getJSONObject(0).getJSONArray("OBIS");
                    for (int j = 0; j < First_Entry.length(); j++) {
                        if (First_Entry.getJSONObject(j).get("OC").equals("1.0.1.8.0.255")) {
                            readings.setKWH(Multiply_By_K_Parameter(First_Entry.getJSONObject(j).get("OV").toString()));
                            counter++;
                        }
                        if (First_Entry.getJSONObject(j).get("OC").equals("1.0.9.8.0.255")) {
                            readings.setKVAH(Multiply_By_K_Parameter(First_Entry.getJSONObject(j).get("OV").toString()));
                            counter++;
                        }
                        if (counter == 2)
                            break;
                    }
                }
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
        }
    }

    public String Read_Data_Type(GXDLMSReader reader, String obis_value) {
        try {
            return Utils.getValue(reader.read(new GXDLMSData(obis_value), 2));
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
            return null;
        }
    }

    /*
     * this method is only for general parameter reading
     *
     * @param arrayList list of obis codes to read
     * @param reader    object of reader to access reading methods
     * @return jsonarray of all general paramters*/

    private JSONArray Read_General(ArrayList<OBIS> arrayList, GXCommunicate reader) {
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            if (arrayList.size() > 0) {
                for (OBIS obisCodes : arrayList) {
                    object.put("OC", obisCodes.getOBIS_VALUE());
                    if (obisCodes.getOBJECT_TYPE().equals("Data")) {
                        object.put("OV", Read_Data_Type(reader, obisCodes.getOBIS_VALUE()));
                    } else if (obisCodes.getOBJECT_TYPE().equals("Register")) {
                        object.put("OV", Read_Register_Type(reader, obisCodes.getOBIS_VALUE()));
                    }
                    jsonArray.put(object);
                    object = new JSONObject();
                }
                object = new JSONObject();
                object.put("OBIS", jsonArray);
                jsonArray = new JSONArray();
                jsonArray.put(object);
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            // Utility.saveError(mActivity, accountNo, "", companyName, "",  "Read_General", stackTrace.toString());
        }
        return jsonArray;
    }

    /*
     * abstract method to read billing.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of billing*/

    private JSONArray Read_Billing(ArrayList<OBIS> arrayList, GXCommunicate reader) {
        return Read(arrayList, reader);
    }

    /*
     * abstract method to read instantaneous.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of instantaneous*/

    private JSONArray Read_Instantaneous(ArrayList<OBIS> arrayList, GXCommunicate reader) {
        return Read(arrayList, reader);
    }

    /*
     * abstract method to read Loadsurvey.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of Loadsurvey*/

    private JSONArray Read_LoadSurvey(ArrayList<OBIS> arrayList, GXCommunicate reader) {
        return Read(arrayList, reader);
    }

    /*
     * abstract method to read tamper.
     *
     * @param arrayList obis code list
     * @param reader    reader object
     * @return json array of tamper*/

    private JSONArray Read_Tamper(ArrayList<OBIS> arrayList, GXCommunicate reader) {
        return Read(arrayList, reader);
    }

    /*
     * for register type objects
     *
     * @param reader     object of reader
     * @param obis_value obis code to read
     * @return string value against obis code after reading*/

    public String Read_Register_Type(GXCommunicate reader, String obis_value) {
        try {
            GXDLMSRegister register = new GXDLMSRegister(obis_value);
            reader.readObject(register, 3);
            return Utils.getValue(reader.readObject(register, 2));
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
            return null;
        }
    }

    /*
     * for data type objects
     *
     * @param reader     object of reader
     * @param obis_value obis code to read
     * @return string value against obis code after reading*/

    public String Read_Data_Type(GXCommunicate reader, String obis_value) {
        try {
            Object obj = reader.readObject(new GXDLMSData(obis_value), 2);
            Log.d(TAG, "Read_Data_Type: " + obj);
            return Utils.getValue(obj);
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
            return null;
        }
    }

    /**
     * this is the main method where actual reading and jason formation done.
     * json array is formed after reading and multification except loadsurvey.
     *
     * @param all_obis arraylist of obis codes to read
     * @param reader   object of reader
     * @return json arrayi
     */


    private JSONArray Read(ArrayList<OBIS> all_obis, GXCommunicate reader) {
        Object[] pgval = null;
        JSONObject jSONObject = new JSONObject();
        JSONArray values_array = new JSONArray();
        JSONArray scaler_array = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        try {
            if (all_obis.size() > 0) {
                GXDLMSProfileGeneric obis_scaler = new GXDLMSProfileGeneric(all_obis.get(0).getOBIS_SCALER());
                reader.readObject(obis_scaler, 3);
                Object scaler_value = reader.readObject(obis_scaler, 2);
                String[] Scaler_Obis = new String[obis_scaler.getCaptureObjects().size()];
                for (int i = 0; i < obis_scaler.getCaptureObjects().size(); i++) {
                    Scaler_Obis[i] = obis_scaler.getCaptureObjects().get(i).getKey().getName().toString();
                }
                for (OBIS obisCodes : all_obis) {
                    try {
                        GXDLMSProfileGeneric obis_value = new GXDLMSProfileGeneric(obisCodes.getOBIS_VALUE());
                        reader.readObject(obis_value, 3);
                        String[] Value_Obis = new String[obis_value.getCaptureObjects().size()];
                        for (int i = 0; i < obis_value.getCaptureObjects().size(); i++) {
                            Value_Obis[i] = obis_value.getCaptureObjects().get(i).getKey().getName().toString();
                        }
                        if (MeterMakeId.equals("4") || MeterMakeId.equals("6") || MeterMakeId.equals("8") || (startDate == null && endDate == null)) {
                            pgval = (Object[]) reader.readObject(obis_value, 2);
                        } else {
                            pgval = reader.readRowsByRange(obis_value, startDate, endDate);
                        }
                        for (int k = 0; k < pgval.length; k++) {
                            try {
                                for (int i = 0; i < Value_Obis.length; i++) {
                                    jSONObject.put("OC", Value_Obis[i]);
                                    if (((Object[]) pgval[k])[i] instanceof byte[]) {
                                        byte[] date_bytes = (byte[]) (((Object[]) pgval[k])[i]);
                                        ((Object[]) pgval[k])[i] = Utils.makeDateTime(Byte.toString(date_bytes[3]).trim(), Byte.toString(date_bytes[2]).trim(),
                                                "" + Integer.parseInt(Byte.toString(date_bytes[0]).trim() + Long.toHexString(Integer.parseInt(Byte.toString(date_bytes[1]).trim()) & 0x00000000ffffffffL).substring(6, 8), 16),
                                                Byte.toString(date_bytes[5]).trim(), Byte.toString(date_bytes[6]).trim(), Byte.toString(date_bytes[7]).trim());
                                        jSONObject.put("OV", ((Object[]) pgval[k])[i]);
                                    } else {
                                        if (!obisCodes.getPROFILER_GROUP().equals("Loadsurvey")) {
                                            if (Arrays.asList(Scaler_Obis).contains(Value_Obis[i]) && !(((Object[]) pgval[k])[i] instanceof GXDateTime)) {
                                                int mf = ((Byte) ((Object[]) ((Object[]) ((Object[]) scaler_value)[0])[Arrays.asList(Scaler_Obis).indexOf(Value_Obis[i])])[0]).byteValue();
                                                if (mf != 0) {
                                                    jSONObject.put("OV", Double.valueOf(((Object[]) pgval[k])[i].toString()) * Math.pow(10.0d, Double.parseDouble(String.valueOf(mf)))).toString();
                                                } else {
                                                    jSONObject.put("OV", ((Object[]) pgval[k])[i].toString());
                                                }
                                            } else {
                                                if ((((Object[]) pgval[k])[i] instanceof GXDateTime)) {
                                                    jSONObject.put("OV", Utils.MilliToDate(((GXDateTime) (((Object[]) pgval[k]))[i]).getLocalCalendar().getTimeInMillis()));

                                                } else
                                                    jSONObject.put("OV", ((Object[]) pgval[k])[i].toString());
                                            }
                                        } else {
                                            try {
                                                if ((((Object[]) pgval[k])[i] instanceof GXDateTime)) {
                                                    jSONObject.put("OV", Utils.MilliToDate(((GXDateTime) (((Object[]) pgval[k]))[i]).getLocalCalendar().getTimeInMillis()));

                                                } else
                                                    jSONObject.put("OV", ((Object[]) pgval[k])[i].toString());
                                            } catch (Exception e) {
                                                e.getMessage();
                                            }

                                        }

                                    }

                                    values_array.put(jSONObject);
                                    jSONObject = new JSONObject();
                                }
                            } catch (Exception ex) {
                                ex.getMessage();
                            }
                            JSONObject object = new JSONObject();
                            object.put("OBIS", values_array);
                            jsonArray.put(object);
                            values_array = new JSONArray();
                        }
                    } catch (Exception e) {
                        readings.setEXCEPTION_MESSAGE(e.getMessage());
                    }
                }
                try {
                    if (all_obis.get(0).getPROFILER_GROUP().equalsIgnoreCase("LoadSurvey")) {
                        JSONObject scaler_object = new JSONObject();
                        for (int i = 0; i < ((Object[]) scaler_value).length; i++) {
                            for (int j = 0; j < Scaler_Obis.length; j++) {
                                scaler_object.put("OC", Scaler_Obis[j]);
                                scaler_object.put("OV", ((Object[]) ((Object[]) ((Object[]) scaler_value)[0])[j])[0].toString());
                                scaler_array.put(scaler_object);
                                scaler_object = new JSONObject();
                            }
                            JSONObject object = new JSONObject();
                            object.put("OBIS", scaler_array);
                            Load_survey_s = new JSONArray();
                            Load_survey_s.put(object);
                        }
                    }
                } catch (Exception e) {
                    readings.setEXCEPTION_MESSAGE(e.getMessage());
                }
            }
        } catch (Exception e) {
            readings.setEXCEPTION_MESSAGE(e.getMessage());
        }
        return jsonArray;
    }
 /*private  void setWifiWait(){
        try {
            if (WifiConnectivity.getIsWifi()) {
                synchronized (this) {
                    this.wait(3000);
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

 }*/

}


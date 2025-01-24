package com.inventive.ugosmartbuddy.mrilib.common;

import org.json.JSONArray;
import org.json.JSONObject;

public class Non_DLMS_Parameter {


    String  METER_NO;
    String MANUFACTURE_NAME;
    String YEAR_OF_MANUFACTURE;
    String CURRENT_R_PHASE;
    String CURRENT_Y_PHASE;
    String CURRENT_B_PHASE;
    String VOLTAGE_R_PHASE;
    String VOLTAGE_Y_PHASE;
    String VOLTAGE_B_PHASE;
    String BILLING_DATE;
    String ACTIVE_ENERGY;
    String APPARENT_ENERGY;
    String MAXIMUM_DEMAND_KW_HISTORY;
    String MAXIMUM_DEMAND_KVA_HISTORY;


    public static JSONArray getGeneral(Non_DLMS_Parameter non_dlms_parameter){
        JSONArray general = new JSONArray();
        JSONObject object = new JSONObject();
        try {

            object.put("OBIS", general.put(
                    new JSONObject().put("OC", "0.0.96.1.0.255").put("OV", non_dlms_parameter.METER_NO))
                    .put(new JSONObject().put("OC", "0.0.96.1.4.255").put("OV", non_dlms_parameter.YEAR_OF_MANUFACTURE))
                    .put(new JSONObject().put("OC", "0.0.96.1.1.255").put("OV", non_dlms_parameter.MANUFACTURE_NAME)));


        }catch (Exception e){
            e.getMessage();
        }
        general = new JSONArray();
        general.put(object);
        return  general;
    }

    public static JSONArray getInstantaneous(Non_DLMS_Parameter non_dlms_parameter){
        JSONObject object = new JSONObject();
        JSONArray instantenous = new JSONArray();
        try {
            object.put("OBIS", instantenous.put(
                    new JSONObject().put("OC", "1.0.31.7.0.255").put("OV", non_dlms_parameter.CURRENT_R_PHASE))
                    .put(new JSONObject().put("OC", "1.0.51.7.0.255").put("OV", non_dlms_parameter.CURRENT_Y_PHASE))
                    .put(new JSONObject().put("OC", "1.0.71.7.0.255").put("OV", non_dlms_parameter.CURRENT_B_PHASE))
                    .put(new JSONObject().put("OC", "1.0.32.7.0.255").put("OV", non_dlms_parameter.VOLTAGE_R_PHASE))
                    .put(new JSONObject().put("OC", "1.0.52.7.0.255").put("OV", non_dlms_parameter.VOLTAGE_Y_PHASE))
                    .put(new JSONObject().put("OC", "1.0.72.7.0.255").put("OV", non_dlms_parameter.VOLTAGE_B_PHASE))
                    .put(new JSONObject().put("OC", "0.0.1.0.0.255").put("OV", non_dlms_parameter.BILLING_DATE))
            );
        }catch (Exception e){
            e.getMessage();
        }
        instantenous = new JSONArray();
        instantenous.put(object);
        return  instantenous;
    }

    public static JSONArray getBilling(Non_DLMS_Parameter non_dlms_parameter){
        JSONObject object = new JSONObject();
        JSONArray billings = new JSONArray();
        try {
            object.put("OBIS", billings.put(new JSONObject().put("OC", "0.0.0.1.2.255").put("OV", non_dlms_parameter.BILLING_DATE))
                    .put(new JSONObject().put("OC", "1.0.1.8.0.255").put("OV", non_dlms_parameter.ACTIVE_ENERGY))
                    .put(new JSONObject().put("OC", "1.0.9.8.0.255").put("OV", non_dlms_parameter.APPARENT_ENERGY))
                    .put(new JSONObject().put("OC", "1.0.1.6.0.255").put("OV", non_dlms_parameter.MAXIMUM_DEMAND_KW_HISTORY))
                    .put(new JSONObject().put("OC", "1.0.9.6.0.255").put("OV", non_dlms_parameter.MAXIMUM_DEMAND_KVA_HISTORY))
            );
        }catch (Exception e){
            e.getMessage();
        }
        billings = new JSONArray();
        billings.put(object);
        return  billings;
    }


    public String getMETER_NO() {
        return METER_NO;
    }

    public void setMETER_NO(String METER_NO) {
        this.METER_NO = METER_NO;
    }

    public String getMANUFACTURE_NAME() {
        return MANUFACTURE_NAME;
    }

    public void setMANUFACTURE_NAME(String MANUFACTURE_NAME) {
        this.MANUFACTURE_NAME = MANUFACTURE_NAME;
    }

    public String getYEAR_OF_MANUFACTURE() {
        return YEAR_OF_MANUFACTURE;
    }

    public void setYEAR_OF_MANUFACTURE(String YEAR_OF_MANUFACTURE) {
        this.YEAR_OF_MANUFACTURE = YEAR_OF_MANUFACTURE;
    }

    public String getCURRENT_R_PHASE() {
        return CURRENT_R_PHASE;
    }

    public void setCURRENT_R_PHASE(String CURRENT_R_PHASE) {
        this.CURRENT_R_PHASE = CURRENT_R_PHASE;
    }

    public String getCURRENT_Y_PHASE() {
        return CURRENT_Y_PHASE;
    }

    public void setCURRENT_Y_PHASE(String CURRENT_Y_PHASE) {
        this.CURRENT_Y_PHASE = CURRENT_Y_PHASE;
    }

    public String getCURRENT_B_PHASE() {
        return CURRENT_B_PHASE;
    }

    public void setCURRENT_B_PHASE(String CURRENT_B_PHASE) {
        this.CURRENT_B_PHASE = CURRENT_B_PHASE;
    }

    public String getVOLTAGE_R_PHASE() {
        return VOLTAGE_R_PHASE;
    }

    public void setVOLTAGE_R_PHASE(String VOLTAGE_R_PHASE) {
        this.VOLTAGE_R_PHASE = VOLTAGE_R_PHASE;
    }

    public String getVOLTAGE_Y_PHASE() {
        return VOLTAGE_Y_PHASE;
    }

    public void setVOLTAGE_Y_PHASE(String VOLTAGE_Y_PHASE) {
        this.VOLTAGE_Y_PHASE = VOLTAGE_Y_PHASE;
    }

    public String getVOLTAGE_B_PHASE() {
        return VOLTAGE_B_PHASE;
    }

    public void setVOLTAGE_B_PHASE(String VOLTAGE_B_PHASE) {
        this.VOLTAGE_B_PHASE = VOLTAGE_B_PHASE;
    }

    public String getBILLING_DATE() {
        return BILLING_DATE;
    }

    public void setBILLING_DATE(String BILLING_DATE) {
        this.BILLING_DATE = BILLING_DATE;
    }

    public String getACTIVE_ENERGY() {
        return ACTIVE_ENERGY;
    }

    public void setACTIVE_ENERGY(String ACTIVE_ENERGY) {
        this.ACTIVE_ENERGY = ACTIVE_ENERGY;
    }

    public String getAPPARENT_ENERGY() {
        return APPARENT_ENERGY;
    }

    public void setAPPARENT_ENERGY(String APPARENT_ENERGY) {
        this.APPARENT_ENERGY = APPARENT_ENERGY;
    }

    public String getMAXIMUM_DEMAND_KW_HISTORY() {
        return MAXIMUM_DEMAND_KW_HISTORY;
    }

    public void setMAXIMUM_DEMAND_KW_HISTORY(String MAXIMUM_DEMAND_KW_HISTORY) {
        this.MAXIMUM_DEMAND_KW_HISTORY = MAXIMUM_DEMAND_KW_HISTORY;
    }

    public String getMAXIMUM_DEMAND_KVA_HISTORY() {
        return MAXIMUM_DEMAND_KVA_HISTORY;
    }

    public void setMAXIMUM_DEMAND_KVA_HISTORY(String MAXIMUM_DEMAND_KVA_HISTORY) {
        this.MAXIMUM_DEMAND_KVA_HISTORY = MAXIMUM_DEMAND_KVA_HISTORY;
    }

}

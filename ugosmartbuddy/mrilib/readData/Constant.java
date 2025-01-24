package com.inventive.ugosmartbuddy.mrilib.readData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author Nitin Sethi
 *
 */
public class Constant {
	
	public static class DeviceTypes{
		public static final String SINGLE_PHASE = "Single Phase";
		public static final String THREE_PHASE = "Three Phase";
		public static final String LT_METER = "LT Meter";
		public static final String CT_METER = "CT Meter";
	}
	
	public static class CommandType{
		public static final String CMD_READ = "CMD_READ";
		public static final String CMD_WRITE = "CMD_WRITE";
		public static final String CFG_READ = "CFG_READ";
		public static final String CFG_WRITE = "CFG_WRITE";
	}
	
	public final class Mios {
		public static final String UTILITY_TYPE = "UTILITYTYPE";
		public static final String G1 = "G1";
		public static final String G2 = "G2";
		public static final String G3 = "G3";
		public static final String DATE = "DATE";
		public static final String SNAPSHOT = "SNAPSHOT";
		public static final String PARAMCODE = "PARAMCODE";
		public static final String VALUE = "VALUE";
		public static final String CummEnergy_Kwh_Import = "P7-1-5-2-0";
		public static final String CummEnergy_Kvah_Import = "P7-3-5-2-0";
		public static final String CummEnergy_Kwh_Export = "P7-1-6-2-0";
		public static final String CummEnergy_Kvah_Export = "P7-3-6-2-0";
		public static final String INST_PARAM = "INSTPARAM";
		public static final String CODE = "CODE";
		public static final String DATETIME = "DATETIME";
		public static final String Current_IR = "P2-1-1-1-0";
		public static final String Current_IY = "P2-1-2-1-0";
		public static final String Current_IB = "P2-1-3-1-0";
		public static final String Voltage_VRN = "P1-1-1-1-0";
		public static final String Voltage_VYN = "P1-1-2-1-0";
		public static final String Voltage_VBN = "P1-1-3-1-0";
		public static final String Power_Factor_R_Phase = "P4-1-1-0-0";
		public static final String Power_Factor_Y_Phase = "P4-2-1-0-0";
		public static final String Power_Factor_B_Phase = "P4-3-1-0-0";
		public static final String Power_Factor = "P4-4-1-0-0";
		public static final String Frequency = "P9-1-0-0-0";
		public static final String Apparent_Power_KVA = "P3-4-4-1-0";
		public static final String Active_Power_KW = "P3-2-4-1-0";
		public static final String Signed_Reactive_Power_KVAR_Lag_Lead = "P3-3-4-1-0";
		public static final String Number_Of_Power_Failures = "POWERFAILCOUNT";
		public static final String Cumulative_Power_OFF_Duration_In_Minutes = "P11-1-0-0-0";
		public static final String Cumulative_Tamper_Count = "TEMPERCOUNT";
		public static final String Cummulative_Billing_Count = "BILLCOUNT";
		public static final String Cummulative_Programming_Count = "PROGRAMCOUNT";
		public static final String Maximum_Demand_KW = "P7-4-13-2-0";
		public static final String Maximum_Demand_KVA = "P7-6-13-2-0";
		public static final String Enable_Disable_Load_Limit_Function = "ENABLE_DISABLE_LOAD_LIMIT_FUNCTION";
		public static final String Load_Limit_W = "LOAD_LIMIT_W";
		public static final String EVENT = "EVENT";
		public static final String TIME = "TIME";
		

		public static final String Avg_Voltage_VRN = "P1-2-1-4-0";
		public static final String Avg_Voltage_VYN = "P1-2-2-4-0";
		public static final String Avg_Voltage_VBN = "P1-2-3-4-0";
		public static final String Block_Energy_kWh_Import = "P7-1-5-0-0";
		public static final String Block_Energy_kVARH_lag = "P7-2-5-1-0";
		public static final String Block_Energy_kVARH_lead = "P7-2-6-1-0";
		public static final String Block_Energy_kvah_Import = "P7-3-5-0-0";
		public static final String Block_Energy_kWh_Export = "P7-1-6-0-0";
		public static final String Block_Energy_kVAH_Export = "P7-3-6-2-0";
		public static final String Load_Current_IR = "P2-1-1-4-0";
		public static final String Load_Current_IY = "P2-1-2-4-0";
		public static final String Load_Current_IB = "P2-1-3-4-0";
		public static final String DAYPROFILE = "DAYPROFILE";
		public static final String INTERVAL = "INTERVAL";
		public static final String Reactive_Energy_Q1 = "P7-2-1-2-0";
		public static final String Reactive_Energy_Q2 = "P7-2-2-2-0";
		public static final String Reactive_Energy_Q3 = "P7-2-3-2-0";
		public static final String Reactive_Energy_Q4 = "P7-2-4-2-0";
		public static final String Billing_Date = "Billing_date";
		
		
		//single phase event parameter
		public static final String CURRENT = "P2-2-5-1-0";
		public static final String AVG_VOLATGE = "P1-2-7-1-0";

	}

	public static  class DLMSTABLE {
		public static final String Prepay_KEYSPACE = "prepay_db";
		public static String KEYSPACE="dlms_db";
		public static final String Daily_Load_Profile_Data = "daily_load_profile_data";
		public static final String Dlms_Instantaneous_Data = "dlms_instantaneous_data";
		public static final String Event_Data = "event_data";
		public static final String Load_Profile_Data = "load_profile_data";
		public static final String Billing_Data = "billing_data";
		public static final String COMMANDS_LOG = "commands_log";
		public static final String DEVICES_INFO = "devices_info";
		public static final String Meter_type_info = "meter_type_info";
		public static final String MODULE_ACCESS_LOGS = " dlms_moduleaccess_logs";
		public static final String Dlms_Instantaneous_Data_SinglePhase = " instantaneous_data_singlephase";
		public static final String Instantaneous_Data_SinglePhase = "instantaneous_data_singlephase";
		public static final String Daily_Load_Profile_SinglePhase = "daily_load_profile_singlephase";
		public static final String Load_Profile_Data_SinglePhase = "load_profile_data_singlephase";
		public static final String Billing_Data_SinglePhase = "billing_data_singlephase";
		public static final String Event_Data_SinglePhase = "event_data_singlephase";
		public static final String Dlms_Push_Data = "dlms_push_data";
		public static final String Last_Billing_Data = "last_billing_data";
		public static final String Last_Billing_Data_SinglePhase = "last_billing_data_singlephase";
		public static final String Dlms_Configuration = "dlms_configuration";
		public static final String Dlms_Configuration_Status = "dlms_configuration_status";
		public static final String Devices_History = "devices_history";
		public static final String Delta_Load_Profile_Dataset = "delta_load_profile_dataset";
		public static final String Delta_Load_Profile_Processed_Data = "del_load_profile_processed_data";
		public static final String Instantaneous_Push_SinglePhase = "instantaneous_push_singlephase";
		public static final String Instantaneous_Push_ThreePhase = "instantaneous_push_threephase";
		public static final String InstDataReadTable = "prepay_Instantaneous_data";
		public static final String prepayInstantEnergyTable = "prepay_Instantaneous_energy";
		public static final String TransactionDetails = "transaction_details";
		public static final String CurrentBilling = "current_billing";
		
		
		

	}

	public final class EventsTable {

		// General Configuration
		public static final String DeviceSrNo = "device_serial_number TEXT";
		public static final String EventCode = "event_code INT";
		public static final String EventDateTime = "event_datetime TEXT";
		public static final String CumulativeEnergyKwhExport = "cumulative_energy_kwh_export DOUBLE";
		public static final String CumulativeEnergyKwhImport = "cumulative_energy_kwh_import DOUBLE";
		public static final String CurrentIb = "current_ib DOUBLE";
		public static final String CurrentIr = "current_ir DOUBLE";
		public static final String CurrentIy = "current_iy DOUBLE";
		public static final String EventType = "event_type TEXT";
		public static final String MdasDateTime = "mdas_datetime TEXT";
		public static final String MeterDateTime = "meter_datetime TEXT";
		public static final String PowerFactorBPhase = "power_factor_b_phase DOUBLE";
		public static final String PowerFactorRPhase = "power_factor_r_phase DOUBLE";
		public static final String PowerFactorYPhase = "power_factor_y_phase DOUBLE";
		public static final String VoltageVbn = "voltage_vbn DOUBLE";
		public static final String VoltageVRn = "voltage_vrn DOUBLE";
		public static final String VoltageVyn = "voltage_vyn DOUBLE";

		public static final String COMMAND_COMPLETION_DATETIME = "COMMAND_COMPLETION_DATETIME TIMESTAMP";
		public static final String Voltage_Related = "Voltage Related";
		public static final String Others_Related = "Others";
		public static final String Power_Related = "Power Related";
		public static final String Transaction_Related = "Transaction Related";
		public static final String Current_Related = "Current Related";
		public static final String Non_RollOver_Related = "Non RollOver Related";
		public static final String Control_Related = "Control Related";
		public static final String Event_Category = "event_category TEXT";
	}
	
	public final class GeneralFields {
		// General Configuration
		
		public static final String ACCESS1 = "ACCESS SET";
		public static final String SUBDEVISION_NAME = "SUBDEVISION_NAME TEXT";
		public static final String FEEDER_NAME="FEEDER_NAME TEXT";
		public static final String SUBSTATION_NAME = "SUBSTATION_NAME TEXT";
		public static final String DT_NAME="DT_NAME TEXT";
		public static final String DCU_SERIAL_NUMBER="DCU_SERIAL_NUMBER TEXT";
		public static final String DEVICE_SERIAL_NUMBER="DEVICE_SERIAL_NUMBER TEXT";
		public static final String OWNER_NAME="OWNER_NAME TEXT";
	
		
		

		
	}

	public final class LoadProfileData {
		// General Configuration
		public static final String DeviceSrNo = "device_serial_number TEXT";
		public static final String IntervalDateTime = "interval_datetime TEXT";
		public static final String CurrentIb = "current_ib DOUBLE";
		public static final String CurrentIr = "current_ir DOUBLE";
		public static final String CurrentIy = "current_iy DOUBLE";
		public static final String MdasDateTime = "mdas_datetime TEXT";
		public static final String MeterDateTime = "meter_datetime TEXT";
		public static final String Block_Energy_Kvah_Export = "block_energy_kvah_export DOUBLE";
		public static final String Block_Energy_Kvah_Import = "block_energy_kvah_import DOUBLE";
		public static final String Block_Energy_Kvarh_Lag = "block_energy_kvarh_lag DOUBLE";
		public static final String Block_Energy_Kvarh_Lead = "block_energy_kvarh_lead DOUBLE";
		public static final String Block_Energy_Kwh_Export = "block_energy_kwh_export DOUBLE";
		public static final String Block_Energy_Kwh_Import = "block_energy_kwh_import DOUBLE";
		public static final String lp_blockFrequency = "frequency DOUBLE";
		public static final String AvgVoltageVbn = "avg_voltage_vbn DOUBLE";
		public static final String AvgVoltageVRn = "avg_voltage_vrn DOUBLE";
		public static final String AvgVoltageVyn = "avg_voltage_vyn DOUBLE";
		public static final String Average_Voltage = "average_voltage DOUBLE";
		public static final String Average_Current = "average_current DOUBLE";

	}

	public final class BillingTable {
		public static final String Device_serial_number = "device_serial_number TEXT";
		public static final String Meter_datetime = "meter_datetime TEXT";
		public static final String Mdas_datetime = "mdas_datetime TEXT";
		public static final String Billing_datetime = "billing_datetime TEXT";
		public static final String Average_power_factor_billing_period = "average_power_factor_billing_period DOUBLE";
		public static final String Cumulative_energy_kWh_import = "cumulative_energy_kWh_import DOUBLE";
		public static final String Cumulative_energy_kWh_tier1 = "cumulative_energy_kWh_tier1 DOUBLE";
		public static final String Cumulative_energy_kWh_tier2 = "cumulative_energy_kWh_tier2 DOUBLE";
		public static final String Cumulative_energy_kWh_tier3 = "cumulative_energy_kWh_tier3 DOUBLE";
		public static final String Cumulative_energy_kWh_tier4 = "cumulative_energy_kWh_tier4 DOUBLE";
		public static final String Cumulative_energy_kWh_tier5 = "cumulative_energy_kWh_tier5 DOUBLE";
		public static final String Cumulative_energy_kWh_tier6 = "cumulative_energy_kWh_tier6 DOUBLE";
		public static final String Cumulative_energy_kWh_tier7 = "cumulative_energy_kWh_tier7 DOUBLE";
		public static final String Cumulative_energy_kWh_tier8 = "cumulative_energy_kWh_tier8 DOUBLE";
		public static final String Cumulative_energy_kVAh_import = "cumulative_energy_kVAh_import DOUBLE";
		public static final String Cumulative_energy_kVAh_tier1 = "cumulative_energy_kVAh_tier1 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier2 = "cumulative_energy_kVAh_tier2 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier3 = "cumulative_energy_kVAh_tier3 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier4 = "cumulative_energy_kVAh_tier4 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier5 = "cumulative_energy_kVAh_tier5 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier6 = "cumulative_energy_kVAh_tier6 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier7 = "cumulative_energy_kVAh_tier7 DOUBLE";
		public static final String Cumulative_energy_kVAh_tier8 = "cumulative_energy_kVAh_tier8 DOUBLE";
		public static final String Maximum_demand_kw = "maximum_demand_kw DOUBLE";
		public static final String Maximum_demand_kw_date = "maximum_demand_kw_date TEXT";
		public static final String Maximum_demand_kw_tier1 = "maximum_demand_kw_tier1 DOUBLE";
		public static final String Maximum_demand_kw_tier1_date = "maximum_demand_kw_tier1_date TEXT";
		public static final String Maximum_demand_kw_tier2 = "maximum_demand_kw_tier2 DOUBLE";
		public static final String Maximum_demand_kw_tier2_date = "maximum_demand_kw_tier2_date TEXT";
		public static final String Maximum_demand_kw_tier3 = "maximum_demand_kw_tier3 DOUBLE";
		public static final String Maximum_demand_kw_tier3_date = "maximum_demand_kw_tier3_date TEXT";
		public static final String Maximum_demand_kw_tier4 = "maximum_demand_kw_tier4 DOUBLE";
		public static final String Maximum_demand_kw_tier4_date = "maximum_demand_kw_tier4_date TEXT";
		public static final String Maximum_demand_kw_tier5 = "maximum_demand_kw_tier5 DOUBLE";
		public static final String Maximum_demand_kw_tier5_date = "maximum_demand_kw_tier5_date TEXT";
		public static final String Maximum_demand_kw_tier6 = "maximum_demand_kw_tier6 DOUBLE";
		public static final String Maximum_demand_kw_tier6_date = "maximum_demand_kw_tier6_date TEXT";
		public static final String Maximum_demand_kw_tier7 = "maximum_demand_kw_tier7 DOUBLE";
		public static final String Maximum_demand_kw_tier7_date = "maximum_demand_kw_tier7_date TEXT";
		public static final String Maximum_demand_kw_tier8 = "maximum_demand_kw_tier8 DOUBLE";
		public static final String Maximum_demand_kw_tier8_date = "maximum_demand_kw_tier8_date TEXT";
		public static final String Maximum_demand_kva = "maximum_demand_kva DOUBLE";
		public static final String Maximum_demand_kva_date = "maximum_demand_kva_date TEXT";
		public static final String Maximum_demand_kva_tier1 = "maximum_demand_kva_tier1 DOUBLE";
		public static final String Maximum_demand_kva_tier1_date = "maximum_demand_kva_tier1_date TEXT";
		public static final String Maximum_demand_kva_tier2 = "maximum_demand_kva_tier2 DOUBLE";
		public static final String Maximum_demand_kva_tier2_date = "maximum_demand_kva_tier2_date TEXT";
		public static final String Maximum_demand_kva_tier3 = "maximum_demand_kva_tier3 DOUBLE";
		public static final String Maximum_demand_kva_tier3_date = "maximum_demand_kva_tier3_date TEXT";
		public static final String Maximum_demand_kva_tier4 = "maximum_demand_kva_tier4 DOUBLE";
		public static final String Maximum_demand_kva_tier4_date = "maximum_demand_kva_tier4_date TEXT";
		public static final String Maximum_demand_kva_tier5 = "maximum_demand_kva_tier5 DOUBLE";
		public static final String Maximum_demand_kva_tier5_date = "maximum_demand_kva_tier5_date TEXT";
		public static final String Maximum_demand_kva_tier6 = "maximum_demand_kva_tier6 DOUBLE";
		public static final String Maximum_demand_kva_tier6_date = "maximum_demand_kva_tier6_date TEXT";
		public static final String Maximum_demand_kva_tier7 = "maximum_demand_kva_tier7 DOUBLE";
		public static final String Maximum_demand_kva_tier7_date = "maximum_demand_kva_tier7_date TEXT";
		public static final String Maximum_demand_kva_tier8 = "maximum_demand_kva_tier8 DOUBLE";
		public static final String Maximum_demand_kva_tier8_date = "maximum_demand_kva_tier8_date TEXT";
		public static final String Power_on_duration = "power_on_duration INT";
		public static final String Power_off_duration = "power_off_duration INT";
		public static final String Cumulative_energy_kwh_export = "cumulative_energy_kwh_export DOUBLE";
		public static final String Cumulative_energy_kvah_export = "cumulative_energy_kvah_export DOUBLE";
		
		public static final String Cumulative_Energy_Kvarh_Q1 = "cumulative_energy_kvarh_q1 DOUBLE";
		public static final String Cumulative_Energy_Kvarh_Q2 = "cumulative_energy_kvarh_q2 DOUBLE";
		public static final String Cumulative_Energy_Kvarh_Q3 = "cumulative_energy_kvarh_q3 DOUBLE";
		public static final String Cumulative_Energy_Kvarh_Q4 = "cumulative_energy_kvarh_q4 DOUBLE";
	}

	public final class CommandName {
		public static final String Billing_Data = "BillingCommand";
		public static final String Control_Related_Event = "ControlRelatedEventCommand";
		public static final String Current_Related_Event = "CurrentRelatedEventCommand";
		public static final String Daily_Load_Profile = "DailyLoadProfileCommand";
		public static final String Instantaneous_Data = "InstantaneousDataCommand";
		public static final String Load_Profile_Data = "LoadProfileDataCommand";
		public static final String Non_RollOver_Event = "NonRollOverEventCommand";
		public static final String Other_Related_Event = "OtherRelatedEventCommand";
		public static final String Power_Related_Event = "PowerRelatedEventCommand";
		public static final String Transaction_Related_Event = "TransactionRelatedEventCommand";
		public static final String Voltage_Related_Event = "VoltageRelatedEventCommand";
		public static final String Full_Billing_Data = "FullBillingDataCommand";
		public static final String PING = "Ping";
		public static final String Last_Recharge_Amount  = "LastRechargeAmount";
		public static final String Last_Recharge_Datetime  = "LastRechargeDatetime";
		public static final String Current_Recharge_Amount  = "CurrentRechargeAmount";
		public static final String Current_Recharge_Datetime  = "CurrentRechargeDatetime";
		//public static final String Connect  = "connect";
		//public static final String Disconnect  = "disconnect";
		
		public static final String Connect  = "Connect";
		public static final String Disconnect  = "DisConnect";
		
		public static final String ReadFirmware  = "ReadFirmware";
		public static final String WriteFirmware  = "WriteFirmware";
		
		public static final String Conf_WriteProfileCapturePeriod  = "ProfileCapturePeriod";
		public static final String Conf_WriteDemandIntegrationPeriod  = "DemandIntegrationPeriod";
		public static final String Conf_WriteBillDate  = "BillDate";
		public static final String Conf_WriteActivityCalendar  = "ActivityCalendar";
		public static final String Conf_SetLoadLimitValue  = "LoadLimit";
		
		public static final String ReadRtc  = "ReadRtc";
		public static final String WriteRtc  = "WriteRtc";
		
		public static final String DEVICE_SR_READ = "DeviceRead";
	
	}
	
	public final class CommandNameList {
		public static final String Control_Related_Event = "Control Related";
		public static final String Non_RollOver_Event = "Non RollOver Related";
		public static final String Power_Related_Event = "Power Related";
		public static final String Transaction_Related_Event = "Transaction Related";
		public static final String Others_Event = "Others";
		public static final String Current_Related_Event = "Current Related";
		
	}

	public static ConcurrentHashMap<String, Object> deviceCommandsMap = new ConcurrentHashMap<String, Object>();
	public static ConcurrentHashMap<String, Object> deviceProcessing = new ConcurrentHashMap<String, Object>();
	//added by gourav 
	
	public static ConcurrentHashMap<String, Object> deviceIpPortConfigMap = new ConcurrentHashMap<String, Object>();
	
	
	public static CopyOnWriteArrayList<String> deviceList1=new CopyOnWriteArrayList<String>();
	//public static CopyOnWriteArrayList<DLMSMeterGenricInfoBean> masterDataList=new CopyOnWriteArrayList<DLMSMeterGenricInfoBean>();
	public static CopyOnWriteArrayList<String> threadExecuteList=new CopyOnWriteArrayList<String>();
	public static HashMap<String, Boolean> threadCheckMap1 = new LinkedHashMap<String, Boolean>();
	
	public static Properties MiosProperties =  new Properties();
	
	public static Properties MiosPropertiesEVI =  new Properties();
	public static Properties MiosPropertiesIHM =  new Properties();
	public static Properties MiosPropertiesINISH =  new Properties();
	
	public static Properties ObisProperties =  new Properties();

	public final class AuthenticationConstant {

		public static final String SUCCESS = "Success";
		public static final String AUTHENTICATED = "Authenticated";
		public static final String SESSION_IS_EXPIRED = "Session Is Expired";
		public static final String KEY_IS_NOTVALID = "Key Is Not Valid";
		public static final String API_KEY = "apiKey";
		public static final String DATA = "data";

	}

	public final class Commands {
		// General Configuration
		public static final String TRACKING_ID = "TRACKING_ID TEXT";
		public static final String COMMAND_NAME = "COMMAND_NAME TEXT";
		public static final String DEVICE_SERIAL_NUMBER = "DEVICE_SERIAL_NUMBER TEXT";
		public static final String COMMAND_PRIORITY = "COMMAND_PRIORITY TEXT";
		public static final String COMMAND_DATETIME = "COMMAND_DATETIME TIMESTAMP";
		public static final String COMMAND_COMPLETE_DATETIME = "COMMAND_COMPLETE_DATETIME TIMESTAMP";
		public static final String COMMAND_STATUS = "COMMAND_STATUS TEXT";
		public static final String USERNAME = "USERNAME TEXT";
		public static final String SYSTEM_IP = "SYSTEM_IP TEXT";
	}

	/**
	 * used to find the table names for dlms application.
	 */
	public final class DvcTables {
		public static final String OwnNo = "OWNER_NAME:TEXT";
		public static final String socNo = "SOCIEITY_NAME:TEXT";
		public static final String Dvcno = "DEVICE_SERIAL_NUMBER:TEXT";
		public static final String DvcName = "DEVICE_NAME:TEXT";
		public static final String macAddress = "NIC_MAC_ADDRESS:TEXT";
		public static final String macSNO = "NIC_MAC_SERIAL_NUMBER:TEXT";
		public static final String macMfg = "NIC_MAC_MFG_NAME:TEXT";
		public static final String nicModel = "NIC_MODEL:TEXT";
		public static final String trans = "TRANSFORMER:TEXT";
		public static final String gtySno = "GATEWAY_SERIAL_NUMBER:TEXT";
		public static final String phType = "PHASE_TYPE:TEXT";
		public static final String DvcType = "DEVICE_TYPE:TEXT";
		public static final String flatNo = "FLAT_NUMBER:TEXT";
		public static final String locAdd = "LOCATION_ADDRESS:TEXT";
		public static final String emailId = "EMAIL_ID:TEXT";
		public static final String phoneNo = "PHONE_NUMBER:TEXT";
		public static final String conName = "CONSUMER_NAME:TEXT";
		public static final String lattitude = "LATITUDE:TEXT";
		public static final String caNo = "CONSUMER_CA_NUMBER:TEXT";
		public static final String longitude = "LONGITUDE:TEXT";
		public static final String DvcSwVersion = "DEVICE_SOFTWARE_VERSION:TEXT";
		public static final String DvcHwVersion = "DEVICE_HARDWARE_VERSION:TEXT";
		public static final String mfgDvcId = "MFG_DEVICE_ID:TEXT";
		public static final String DvcMfgName = "DEVICE_MFG_NAME:TEXT";
		public static final String DvcMfgModel = "DEVICE_MFG_MODEL:TEXT";
		public static final String DvcMfgDate = "DEVICE_MFG_DATE:TIMESTAMP";
		public static final String insDate = "INSTALLATION_DATETIME:TIMESTAMP";
		public static final String DvcNwStatus = "DEVICE_NETWORK_STATUS:TEXT";
		public static final String DvcStat = "DEVICE_STATUS:TEXT";
		public static final String gridSancLoad = "Grid_SANCTIONED_LOAD:DOUBLE";
		public static final String dgSancLoad = "DG_SANCTIONED_LOAD:DOUBLE";
		public static final String Desc = "DESCRIPTION:TEXT";
		public static final String Group = "GROUPS:TEXT";
		public static final String City = "CITY:TEXT";
		public static final String State = "STATE:TEXT";
		public static final String ZIP = "ZIP:TEXT";
		public static final String Country = "COUNTRY:TEXT";
		public static final String Pwd = "password:TEXT";
		public static final String EB_DG_Sensor_Name = "eb_dg_sensor_name:TEXT";
		public static final String EB_DG_Sensor_No = "eb_dg_sensor_no:TEXT";
		public static final String ConsumerType = "consumer_type:TEXT";
		public static final String area = "area:DOUBLE";
		public static final String serviceTaxNumber = "SERVICE_TAX_NUMBER:TEXT";
		public static final String loadType = "load_type:TEXT";
		public static final String dgRelay = "dgRelay:TEXT";
	}

	public final class AbitsTables {
		public static final String HIER_LEVEL_DATA = "hier_level_data";

	}

	public final class KeyConstant {
		public static final String Load_Profile = "Load Profile";
		public static final String Daily_Load_Profile = "Daily Load Profile";
		public static final String Billing = "Billing";
		public static final String No_Network = "No Network";
		
	}
	
	public final class MiosSinglePhase
	{
		public static final String UTILITY_TYPE = "UTILITYTYPE";
		public static final String G1 = "G1";
		public static final String G2 = "G2";
		public static final String G3 = "G3";
		public static final String DATE = "DATE";
		public static final String SNAPSHOT = "SNAPSHOT";
		public static final String PARAMCODE = "PARAMCODE";
		public static final String VALUE = "VALUE";
		public static final String CummEnergy_Kwh_Import =  "P7-1-5-2-0";
		public static final String CummEnergy_Kvah_Import = "P7-3-5-2-0";
		public static final String CummEnergy_Kwh_Export =  "P7-1-6-2-0";
		public static final String CummEnergy_Kvah_Export =  "P7-3-6-2-0";
		public static final String INST_PARAM =  "INSTPARAM";
		public static final String CODE =  "CODE";
		public static final String DATETIME =  "DATETIME";
		public static final String Current_IR =  "P2-1-1-1-0";
		public static final String Current_IY =  "P2-1-2-1-0";
		public static final String Current_IB =  "P2-1-2-1-0";
		public static final String Voltage_VRN =  "P1-1-1-1-0";
		public static final String Voltage_VYN =  "P1-1-2-1-0";
		public static final String Voltage_VBN =  "P1-1-3-1-0";
		public static final String Power_Factor_R_Phase =  "P4-1-1-0-0";
		public static final String Power_Factor_Y_Phase =  "P4-2-1-0-0";
		public static final String Power_Factor_B_Phase =  "P4-3-1-0-0";
		public static final String Power_Factor =  "P4-4-1-0-0";
		public static final String Frequency =  "P9-1-0-0-0";
		public static final String Apparent_Power_KVA =  "P3-4-4-1-0";
		public static final String Active_Power_KW =  "P3-2-4-1-0";
		public static final String Signed_Reactive_Power_KVAR_Lag_Lead =  "P3-3-4-1-0";
		public static final String Number_Of_Power_Failures =  "POWERFAILCOUNT";
		public static final String Cumulative_Power_OFF_Duration_In_Minutes =  "P11-1-0-0-0";
		public static final String Cumulative_Tamper_Count =  "TEMPERCOUNT";
		public static final String Cummulative_Billing_Count =  "BILLCOUNT";
		public static final String Cummulative_Programming_Count =  "PROGRAMCOUNT";
		public static final String Maximum_Demand_KW =  "P7-4-13-2-0";
		public static final String Maximum_Demand_KVA =  "P7-6-13-2-0";
		public static final String Enable_Disable_Load_Limit_Function =  "ENABLE_DISABLE_LOAD_LIMIT_FUNCTION";
		public static final String Load_Limit_KW =  "LOAD_LIMIT_KW";
		public static final String EVENT =  "EVENT";
		public static final String TIME =  "TIME";
		
		public static final String Avg_Voltage_VRN =  "P1-2-1-4-0";
		public static final String Avg_Voltage_VYN =  "P1-2-2-4-0";
		public static final String Avg_Voltage_VBN =  "P1-2-3-4-0";
		public static final String Block_Energy_kWh_Import =  "P7-1-5-0-0";
		public static final String Lp_block_frequency =  "P9-1-0-0-0";
		public static final String Block_Energy_kVARH_lag =  "P7-2-5-1-0";
		public static final String Block_Energy_kVARH_lead =  "P7-2-6-1-0";
		public static final String Block_Energy_kvah_Import =  "P7-3-5-0-0";
		public static final String Block_Energy_kWh_Export =  "P7-1-6-0-0";
		public static final String Block_Energy_kVAH_Export =  "P7-3-6-2-0";
		public static final String Load_Current_IR =  "P2-1-1-4-0";
		public static final String Load_Current_IY =  "P2-1-2-4-0";
		public static final String Load_Current_IB =  "P2-1-3-4-0";
		public static final String DAYPROFILE =  "DAYPROFILE";
		public static final String INTERVAL =  "INTERVAL";
		public static final String AVG_VOLTAGE =  "P1-2-7-1-0";
		public static final String PHASE_CURRENT ="P2-1-5-1-0";
		public static final String Neutral_Current ="P2-1-4-1-0";
		public static final String Cumulative_Power_ON_duration_in_minutes ="P11-2-0-0-0";
		public static final String LOAD_LIMIT_W ="LOAD_LIMIT_W";
		
		public static final String Average_Power_Factor_for_Billing_period ="P4-4-4-0-0";
		public static final String Cumulative_Energy_kWh_Import ="P7-1-5-2-0";
		public static final String Cumulative_Energy_kVAh_Import ="P7-3-5-2-0";
		public static final String Maximum_Demand_kW ="P7-4-13-2-0";
		public static final String Maximum_Demand_kVA ="P7-6-13-2-0";
		public static final String Cumulative_Energy_kWh_export ="P7-1-6-2-0";
		public static final String Cumulative_Energy_kVAh_Export ="P7-3-6-2-0";
		public static final String AVERAGE_VOLTAGE ="P1-2-7-1-0";
		public static final String AVERAGE_CURRENT ="P2-2-5-1-0";
		
	}

	public final class BillingDataThreePhase {
		public static final String Device_Serial_Number = "device_serial_number TEXT";
		public static final String Meter_DateTime = "meter_DateTime TIMESTAMP";
		public static final String Mdas_DateTime = "mdas_DateTime TIMESTAMP";
		public static final String Billing_DateTime = "billing_datetime TIMESTAMP";
		public static final String Average_Power_Factor_Billing_Period = "average_power_factor_billing_period DOUBLE";
		public static final String Cumulative_Energy_kWh_Import = "Cumulative_Energy_kWh_Import DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier1 = "cumulative_energy_kWh_tier1 DOUBLE";
		public static final String Cumulative_energy_kWh_Tier2 = "cumulative_energy_kWh_tier2 DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier3 = "cumulative_energy_kWh_tier3 DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier4 = "cumulative_energy_kWh_tier4 DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier5 = "cumulative_energy_kWh_tier5 DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier6 = "cumulative_energy_kWh_tier6 DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier7 = "cumulative_energy_kWh_tier7 DOUBLE";
		public static final String Cumulative_Energy_kWh_Tier8 = "cumulative_energy_kWh_tier8 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Import = "Cumulative_Energy_kVAh_Import DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier1 = "cumulative_energy_kVAh_tier1 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier2 = "cumulative_energy_kVAh_tier2 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier3 = "cumulative_energy_kVAh_tier3 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier4 = "cumulative_energy_kVAh_tier4 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier5 = "cumulative_energy_kVAh_tier5 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier6 = "cumulative_energy_kVAh_tier6 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier7 = "cumulative_energy_kVAh_tier7 DOUBLE";
		public static final String Cumulative_Energy_kVAh_Tier8 = "cumulative_energy_kVAh_tier8 DOUBLE";
		public static final String Maximum_Demand_kw = "maximum_demand_kw DOUBLE";
		public static final String Maximum_Demand_kw_date = "maximum_demand_kw_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier1 = "maximum_demand_kw_tier1 DOUBLE";
		public static final String Maximum_Demand_kw_Tier1_Date = "maximum_demand_kw_tier1_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier2 = "maximum_demand_kw_tier2 DOUBLE";
		public static final String Maximum_Demand_kw_Tier2_Date = "maximum_demand_kw_tier2_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier3 = "maximum_demand_kw_tier3 DOUBLE";
		public static final String Maximum_Demand_kw_Tier3_Date = "maximum_demand_kw_tier3_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier4 = "maximum_demand_kw_tier4 DOUBLE";
		public static final String Maximum_Demand_kw_Tier4_Date = "maximum_demand_kw_tier4_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier5 = "maximum_demand_kw_tier5 DOUBLE";
		public static final String Maximum_Demand_kw_Tier5_Date = "maximum_demand_kw_tier5_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier6_Date = "maximum_demand_kw_tier6_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier6 = "maximum_demand_kw_tier6 DOUBLE";
		public static final String Maximum_Demand_kw_Tier7_Date = "maximum_demand_kw_tier7_date TIMESTAMP";
		public static final String Maximum_Demand_kw_Tier7 = "maximum_demand_kw_tier7 DOUBLE";
		public static final String Maximum_Demand_kw_Tier8 = "maximum_demand_kw_tier8 DOUBLE";
		public static final String Maximum_Demand_kw_Tier8_Date = "maximum_demand_kw_tier8_date TIMESTAMP";
		public static final String Maximum_Demand_kva = "maximum_demand_kva DOUBLE";
		public static final String Maximum_Demand_kva_Date = "maximum_demand_kva_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier1 = "maximum_demand_kva_tier1 DOUBLE";
		public static final String Maximum_Demand_kva_Tier1_Date = "maximum_demand_kva_tier1_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier2 = "maximum_demand_kva_tier2 DOUBLE";
		public static final String Maximum_Demand_kva_Tier2_Date = "maximum_demand_kva_tier2_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier3 = "maximum_demand_kva_tier3 DOUBLE";
		public static final String Maximum_Demand_kva_Tier3_Date = "maximum_demand_kva_tier3_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier4 = "maximum_demand_kva_tier4 DOUBLE";
		public static final String Maximum_Demand_kva_Tier4_Date = "maximum_demand_kva_tier4_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier5 = "maximum_demand_kva_tier5 DOUBLE";
		public static final String Maximum_Demand_kva_Tier5_Date = "maximum_demand_kva_tier5_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier6 = "maximum_demand_kva_tier6 DOUBLE";
		public static final String Maximum_Demand_kva_Tier6_Date = "maximum_demand_kva_tier6_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier7 = "maximum_demand_kva_tier7 DOUBLE";
		public static final String Maximum_Demand_kva_Tier7_Date = "maximum_demand_kva_tier7_date TIMESTAMP";
		public static final String Maximum_Demand_kva_Tier8 = "maximum_demand_kva_tier8 DOUBLE";
		public static final String Maximum_Demand_kva_Tier8_Date = "maximum_demand_kva_tier8_date TIMESTAMP";
		public static final String Power_On_Duration = "power_on_duration INT";
		public static final String Cumulative_Energy_kWh_export = "Cumulative_Energy_kWh_export DOUBLE";
		public static final String Cumlative_Energy_kVAh_Export = "Cumulative_Energy_kVAh_Export DOUBLE";
		public static final String Cumlative_Energy_kvarh_Q1 = "cumulative_energy_kvarh_q1 DOUBLE";
		public static final String Cumlative_Energy_kvarh_Q2 = "cumulative_energy_kvarh_q2 DOUBLE";
		public static final String Cumlative_Energy_kvarh_Q3= "cumulative_energy_kvarh_q3 DOUBLE";
		public static final String Cumlative_Energy_kvarh_Q4= "cumulative_energy_kvarh_q4 DOUBLE";
		

	}
	
	
	public final class BillingTableSinglePhase {
		
		public static final String Device_serial_number = "device_serial_number TEXT";
		public static final String Meter_datetime = "meter_datetime TEXT";
		public static final String Mdas_datetime = "mdas_datetime TEXT";
		public static final String Billing_datetime = "billing_datetime TEXT";
		public static final String Average_power_factor_billing_period = "average_power_factor_for_billing_period DOUBLE";
		
		public static final String Billing_power_on_duration_in_billing = "billing_power_on_duration_in_billing DOUBLE";
		public static final String Billing_power_off_duration_in_billing = "billing_power_off_duration_in_billing DOUBLE";
		public static final String Cumulative_energy_kvah_export = "cumulative_energy_kvah_export DOUBLE";
		public static final String Cumulative_energy_kvah_import = "cumulative_energy_kvah_import DOUBLE";
		public static final String Cumulative_energy_kvah_tier1 = "cumulative_energy_kvah_tier1 DOUBLE";
		public static final String Cumulative_energy_kvah_tier2 = "cumulative_energy_kvah_tier2 DOUBLE";
		public static final String Cumulative_energy_kvah_tier3 = "cumulative_energy_kvah_tier3 DOUBLE";
		public static final String Cumulative_energy_kvah_tier4 = "Cumulative_energy_kvah_tier4 DOUBLE";
		
		public static final String Cumulative_energy_kvah_tier5 = "cumulative_energy_kvah_tier5 DOUBLE";
		public static final String Cumulative_energy_kvah_tier6 = "cumulative_energy_kvah_tier6 DOUBLE";
		public static final String Cumulative_energy_kvah_tier7 = "cumulative_energy_kvah_tier7 DOUBLE";
		public static final String Cumulative_energy_kvah_tier8 = "Cumulative_energy_kvah_tier8 DOUBLE";
		
		
		public static final String Cumulative_energy_kwh_export = "cumulative_energy_kwh_export DOUBLE";
		public static final String Cumulative_energy_kwh_import = "cumulative_energy_kwh_import DOUBLE";
		public static final String Cumulative_energy_kwh_tier1 = "cumulative_energy_kwh_tier1 DOUBLE";
		public static final String Cumulative_energy_kwh_tier2 = "cumulative_energy_kwh_tier2 DOUBLE";
		public static final String Cumulative_energy_kwh_tier3 = "cumulative_energy_kwh_tier3 DOUBLE";
		public static final String Cumulative_energy_kwh_tier4 = "cumulative_energy_kwh_tier4 DOUBLE";
		
		public static final String Cumulative_energy_kwh_tier5 = "cumulative_energy_kwh_tier5 DOUBLE";
		public static final String Cumulative_energy_kwh_tier6 = "cumulative_energy_kwh_tier6 DOUBLE";
		public static final String Cumulative_energy_kwh_tier7 = "cumulative_energy_kwh_tier7 DOUBLE";
		public static final String Cumulative_energy_kwh_tier8 = "cumulative_energy_kwh_tier8 DOUBLE";
		
		
		public static final String Maximum_demand_kva = "maximum_demand_kva DOUBLE";
		public static final String Maximum_demand_kva_datetime = "maximum_demand_kva_datetime TEXT";
		public static final String Maximum_demand_kw = "maximum_demand_kw DOUBLE";
		public static final String Maximum_demand_kw_datetime = "maximum_demand_kw_datetime TEXT";
		
		
		
		
		
	}
	
	
	
	public final class InstantaneousTableSinglePhase {
		public static final String Device_serial_number = "device_serial_number TEXT";
		public static final String Meter_datetime = "meter_datetime TIMESTAMP";
		public static final String Mdas_datetime = "mdas_datetime TIMESTAMP";
		public static final String Load_Limit = "load_limit INT";
		public static final String Tracking_Id = "tracking_id TEXT";
		public static final String Load_Limit_Status = "load_limit_status INT";
		public static final String Cumulative_Bill_Count = "cumulative_bill_count INT";
		public static final String Cumulative_Program_Count = "cumulative_program_count INT";
		public static final String Cumulative_Energy_Kvah_Export = "cumulative_energy_kvah_export DOUBLE";
		public static final String Cumulative_energy_kwh_Export = "cumulative_energy_kwh_export DOUBLE";
		public static final String Cumulative_Tamper_Count = "cumulative_tamper_count INT";
		public static final String Cumulative_Power_On_Duration = "cumulative_power_on_duration INT";
		public static final String Maximum_Demand_Kva_DateTime = "maximum_demand_kva_datetime TIMESTAMP";
		public static final String Maximum_Demand_Kva = "maximum_demand_kva DOUBLE";
		public static final String Maximum_Demand_Kw = "maximum_demand_kw DOUBLE";
		public static final String Maximum_Demand_Kw_DateTime = "maximum_demand_kw_datetime TIMESTAMP";
		public static final String Cumulative_Energy_Kvah_Import = "cumulative_energy_kvah_import DOUBLE";
		public static final String Cumulative_Energy_Kwh_Import = "cumulative_energy_kwh_import DOUBLE";
		public static final String Active_Power_Kw = "active_power_kw DOUBLE";
		public static final String Apparent_Power_Kva = "apparent_power_kva DOUBLE";
		public static final String Frequency = "frequency DOUBLE";
		public static final String Power_Factor = "power_factor DOUBLE";
		public static final String Neutral_Current = "neutral_current DOUBLE";
		public static final String Phase_Current = "phase_current DOUBLE";
		public static final String Instant_Voltage = "instant_voltage DOUBLE";
		public static final String DateTime = "datetime TIMESTAMP";

	}

	public final class DailyLoadProfileTableSinglePhase {
		public static final String Device_serial_number = "device_serial_number TEXT";
		public static final String Meter_datetime = "meter_datetime TIMESTAMP";
		public static final String Mdas_datetime = "mdas_datetime TIMESTAMP";
		public static final String Cumulative_Energy_Kvah_Export = "cumulative_energy_kvah_export DOUBLE";
		public static final String Cumulative_Energy_Kwh_Export = "cumulative_energy_kwh_export DOUBLE";
		public static final String Cumulative_Energy_Kwh_Import = "cumulative_energy_kwh_import DOUBLE";
		public static final String Cumulative_Energy_Kvah_Import = "cumulative_energy_kvah_import DOUBLE";
		public static final String DateTime = "datetime TIMESTAMP";
	}

	public final class LoadProfileTableSinglePhase {
		public static final String Device_serial_number = "device_serial_number TEXT";
		public static final String Meter_datetime = "meter_datetime TIMESTAMP";
		public static final String Mdas_datetime = "mdas_datetime TIMESTAMP";
		public static final String Interval_datetime = "interval_datetime TIMESTAMP";
		public static final String Average_Current = "average_current DOUBLE";
		public static final String Block_Energy_Kvah_Export = "block_energy_kvah_export DOUBLE";
		public static final String Block_Energy_Kwh_Export = "block_energy_kwh_export DOUBLE";
		public static final String Average_Voltage = "average_voltage DOUBLE";
		public static final String Block_Energy_Kwh_Import = "block_energy_kwh_import DOUBLE";
		public static final String Block_Energy_Kvah_Import = "block_energy_kvah_import DOUBLE";

	}

	public final class BillingDataTableSinglePhase {
		public static final String Device_serial_number = "device_serial_number TEXT";
		public static final String Meter_datetime = "meter_datetime TIMESTAMP";
		public static final String Mdas_datetime = "mdas_datetime TIMESTAMP";
		public static final String Billing_Power_On_Duration_In_Billing = "billing_power_on_duration_in_billing INT";
		public static final String Cumulative_Energy_Kvah_Export = "cumulative_energy_kvah_export DOUBLE";
		public static final String Cumulative_Energy_Kwh_Export = "cumulative_energy_kwh_export DOUBLE";
		public static final String Maximum_Demand_Kva = "maximum_demand_kva DOUBLE";
		public static final String Maximum_Demand_Kw = "maximum_demand_kw DOUBLE";
		public static final String Cumulative_Energy_Kvah_Tier1 = "cumulative_energy_kvah_tier1 DOUBLE";
		public static final String Cumulative_Energy_Kvah_Tier2 = "cumulative_energy_kvah_tier2 DOUBLE";
		public static final String Cumulative_Energy_Kvah_Tier3 = "cumulative_energy_kvah_tier3 DOUBLE";
		public static final String Cumulative_Energy_Kvah_Tier4 = "cumulative_energy_kvah_tier4 DOUBLE";
		public static final String Cumulative_Energy_Kvah_Import = "cumulative_energy_kvah_import DOUBLE";
		public static final String Cumulative_Energy_Kwh_Import = "cumulative_energy_kwh_import DOUBLE";
		public static final String Cumulative_Energy_Kwh_Tier1 = "cumulative_energy_kwh_tier1 DOUBLE";
		public static final String Cumulative_Energy_Kwh_Tier2 = "cumulative_energy_kwh_tier2 DOUBLE";
		public static final String Cumulative_Energy_Kwh_Tier3 = "cumulative_energy_kwh_tier3 DOUBLE";
		public static final String Cumulative_Energy_Kwh_Tier4 = "cumulative_energy_kwh_tier4 DOUBLE";
		public static final String Billing_DateTime = "billing_datetime TIMESTAMP";
		public static final String Average_Power_Factor_For_Billing_Period = "average_power_factor_for_billing_period DOUBLE";
		public static final String Maximum_Demand_Kw_DateTime="maximum_demand_kw_datetime TIMESTAMP";
		public static final String Maximum_Demand_Kvah_DateTime= "maximum_demand_kvah_datetime TIMESTAMP";
	}
	
	
	public final class EventsTableSinglePhase {

		// General Configuration
		public static final String DeviceSrNo = "device_serial_number TEXT";
		public static final String EventCode = "event_code INT";
		public static final String EventDateTime = "event_datetime TEXT";
		public static final String EventType = "event_type TEXT";
		public static final String MdasDateTime = "mdas_datetime TEXT";
		public static final String MeterDateTime = "meter_datetime TEXT";
		
		public static final String CURRENT = "current DOUBLE";
		public static final String cumulativeEnergy = "cumulative_energy DOUBLE";
		public static final String powerFactor = "power_factor DOUBLE";
		public static final String TamperCount = "tamper_count INT";
		public static final String Voltage = "voltage DOUBLE";
		public static final String Event_Category = "event_category TEXT";
	}
	
	
	
	public final class ConfigurationCommands {

		// General Configuration
		public static final String Profile_Capture_Period = "ProfileCapturePeriod";
		public static final String Demand_Integration = "DemandIntegrationPeriod";
		public static final String Bill_Date = "BillDate";
		public static final String Activity_Calendar = "ActivityCalendar";
		public static final String Load_Limit = "LoadLimit";
		
		
	}
	
	public final class AssociationLogicalNameCommands{
		public static final String MR_ASSOCIATION = "MRLLS";
		public static final String US_ASSOCIATION = "USHLS";
		public static final String PUSH_ASSOCIATION = "PUSHHLS";
		public static final String FWM_ASSOCIATION = "FWMHLS";
	}
}
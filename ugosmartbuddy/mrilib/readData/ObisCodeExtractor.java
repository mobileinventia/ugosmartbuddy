package com.inventive.ugosmartbuddy.mrilib.readData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObisCodeExtractor {
    /**
     * used to extract the obis as per the inputs given.
     * @param objects
     * @param obisCode
     * @return
     */
    public static GXDLMSObjectCollection getSpecificObisCode(GXDLMSObjectCollection objects, String obisCode)
    {
        try {

            if(obisCode == null)
            {
                return null;
            }

            String obisCodes[] = obisCode.split(",");
            List<String> obisCodeList = Arrays.asList(obisCodes);

            int i = 0;
            List<Integer> pos = new ArrayList<Integer>();
            Iterator<GXDLMSObject> it =  objects.iterator();
            while(it.hasNext())
            {
                GXDLMSObject s = it.next();
                //System.out.println(s.getName().toString());
                if(!obisCodeList.contains(s.getName().toString()))
                {
                    pos.add(i);
                }

			 	/*if(!s.getName().toString().equalsIgnoreCase(obisCode))
			 	{
			 		pos.add(i);
			 	}*/
                i++;
            }
            int j =0;
            for(i = 0 ; i < pos.size(); i++)
            {
                int a = pos.get(i);
                objects.remove(a-j);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * used to get Register obis code parameters.
     * @param electedParam
     * @return
     */
    public String getRegisterObisCodes(String electedParam)
    {
        StringBuilder parameters = null;
        try
        {
            String[] param = electedParam.split(",");

            parameters = new StringBuilder();
            for(int i = 0 ;i < param.length ; i++)
            {
                if(param[i].equals("1"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.ACTIVEPOWERKW);
                }
                else if(param[i].equals("2"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.APPARENTPOWERKVA);
                }
                else if(param[i].equals("3"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.CUMULATIVEENERGYKVAH);
                }
                else if(param[i].equals("4"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.CUMULATIVEENERGYKWH);
                }
                else if(param[i].equals("5"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.CUMULATIVEPOWERONDURATIONINMINUTES);
                }
                else if(param[i].equals("6"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.FREQUENCYHZ);
                }
                else if(param[i].equals("7"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.NEUTRALCURRENT);
                }
                else if(param[i].equals("8"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.PHASECURRENT);
                }
                else if(param[i].equals("9"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.POWERFACTOR);
                }
                else if(param[i].equals("10"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.SIGNEDREACTIVEPOWERKVARLAGLEAD);
                }
                else if(param[i].equals("11"))
                {
                    parameters.append(ObisCodeConstants.RegisterObisCodes.VOLTAGE);
                }
                parameters.append(",");
            }
            parameters.delete(parameters.length()-1, parameters.length());
        }
        catch(Exception exception)
        {
            try {
                throw exception;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parameters.toString();
    }

    /**
     * used to get the obis code index values.
     * @param ObisCode
     * @param index
     */
    public static String getObisCodesIndexVal(String ObisCode, int index)
    {
        String indexName = null;
        if(ObisCodeConstants.MasterObisCodes.CLOCK_DATA.equals(ObisCode))
        {
            switch (index) {
                case 2: indexName = "Time";
                    break;
                case 3: indexName = "TimeZone";
                    break;
                case 4: indexName = "Status";
                    break;
                case 9: indexName = "ClockBase";
                    break;
                default:
                    break;
            }
        }
        else if(ObisCodeConstants.MasterObisCodes.CONNECT_DISCONNECT_CONTROL.equals(ObisCode))
        {
            switch (index) {
                case 2: indexName = "OutputState";
                    break;
                case 3: indexName = "ControlState";
                    break;
                case 4: indexName = "ControlMode";
                    break;
                default:
                    break;
            }
        }
        return indexName;
    }
}

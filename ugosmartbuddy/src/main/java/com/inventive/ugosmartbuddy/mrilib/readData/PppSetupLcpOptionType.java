//
// --------------------------------------------------------------------------
//  Gurux Ltd
// 
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License 
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// More information of Gurux products: https://www.gurux.org
//
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

//
// --------------------------------------------------------------------------
//  Gurux Ltd
// 
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
package com.inventive.ugosmartbuddy.mrilib.readData;

import java.util.HashMap;

public enum PppSetupLcpOptionType {
    MAX_REC_UNIT(1), ASYNC_CONTROL_CHAR_MAP(2), AUTH_PROTOCOL(3),
    MAGIC_NUMBER(5), PROTOCOL_FIELD_COMPRESSION(7),
    ADDRESS_AND_CTRL_COMPRESSION(8), FCS_ALTERNATIVES(9), CALLBACK(13);

    private int intValue;
    private static HashMap<Integer, PppSetupLcpOptionType> mappings;

    private static HashMap<Integer, PppSetupLcpOptionType>
            getMappings() {
        if (mappings == null) {
            synchronized (PppSetupLcpOptionType.class) {
                if (mappings == null) {
                    mappings =
                            new HashMap<Integer, PppSetupLcpOptionType>();
                }
            }
        }
        return mappings;
    }

    PppSetupLcpOptionType(final int value) {
        intValue = value;
        getMappings().put(new Integer(value), this);
    }

    public int getValue() {
        return intValue;
    }

    public static PppSetupLcpOptionType forValue(final int value) {
        return getMappings().get(new Integer(value));
    }
}
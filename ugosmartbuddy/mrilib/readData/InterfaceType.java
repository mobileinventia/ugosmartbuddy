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

package com.inventive.ugosmartbuddy.mrilib.readData;

/**
 * InterfaceType enumerates the usable types of connection in GuruxDLMS.
 */
public enum InterfaceType {
    /**
     * By default, the interface type is HDLC.
     */
    HDLC,
    /**
     * The interface type is TCP/IP or UDP wrapper, can be used with devices
     * that support IEC 62056-47.
     */
    WRAPPER,
    /**
     * Plain PDU is returned.
     */
    PDU,
    /**
     * Wireless M-Bus frame.
     */
    WIRELESS_MBUS;
    /**
     * @return Get integer value for enumeration.
     */
    public int getValue() {
        return this.ordinal();
    }

    /*
     * Convert integer for enumeration value.
     */
    public static InterfaceType forValue(final int value) {
        return values()[value];
    }
}
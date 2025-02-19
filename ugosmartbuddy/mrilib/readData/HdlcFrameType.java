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
 * HDLC frame types.
 */
enum HdlcFrameType {
    /**
     * I-Frame. Information frames are used to transport user data.
     */
    I_FRAME(0x0),
    /**
     * S-frame. Supervisory Frames are used for flow and error control.
     * Rejected, RNR and RR.
     */
    S_FRAME(0x1),
    /**
     * U-frame. Unnumbered frames are used for link management. Example SNRM and
     * UA.
     */
    U_FRAME(0x3);

    private final int intValue;
    private static java.util.HashMap<Integer, HdlcFrameType> mappings;

    private static java.util.HashMap<Integer, HdlcFrameType> getMappings() {
        synchronized (AssociationResult.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, HdlcFrameType>();
            }
        }
        return mappings;
    }

    HdlcFrameType(final int value) {
        intValue = value;
        getMappings().put(new Integer(value), this);
    }

    /*
     * Get integer value for enum.
     */
    public int getValue() {
        return intValue;
    }

    /*
     * Convert integer for enum value.
     */
    public static HdlcFrameType forValue(final int value) {
        return getMappings().get(new Integer(value));
    }
}
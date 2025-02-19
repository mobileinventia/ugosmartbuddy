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

import java.util.HashMap;

/**
 * Enumerates Translator output types.
 */
public enum TranslatorOutputType {
    /**
     * Simple XML format.
     */
    SIMPLE_XML(0),
    /**
     * Standard XML format.
     */
    STANDARD_XML(1);

    /**
     * Integer value of enumerator.
     */
    private int intValue;
    /**
     * Collection of enumerator values.
     */
    private static HashMap<Integer, TranslatorOutputType> mappings;

    /**
     * Returns collection of enumerator values.
     *
     * @return Enumerator values.
     */
    private static HashMap<Integer, TranslatorOutputType>
            getMappings() {
        if (mappings == null) {
            synchronized (TranslatorOutputType.class) {
                if (mappings == null) {
                    mappings = new HashMap<Integer, TranslatorOutputType>();
                }
            }
        }
        return mappings;
    }

    /**
     * Constructor.
     * 
     * @param value
     *            Integer value of enumerator.
     */
    TranslatorOutputType(final int value) {
        intValue = value;
        getMappings().put(new Integer(value), this);
    }

    /**
     * Get integer value for enumerator.
     * 
     * @return Enumerator integer value.
     */
    public int getValue() {
        return intValue;
    }

    /**
     * Returns enumerator value from an integer value.
     * 
     * @param value
     *            Integer value.
     * @return Enumeration value.
     */
    public static TranslatorOutputType forValue(final int value) {
        return getMappings().get(new Integer(value));
    }
}

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

public class GXAsn1Ia5String {

    private String ia5String;

    /**
     * Constructor.
     */
    public GXAsn1Ia5String() {

    }

    /**
     * Constructor.
     * 
     * @param str
     *            IA5 string.
     */
    public GXAsn1Ia5String(final String str) {
        ia5String = str;
    }

    public final String getValue() {
        return ia5String;
    }

    public final void setValue(final String value) {
        ia5String = value;
    }

    @Override
    public final String toString() {
        return ia5String;
    }
}

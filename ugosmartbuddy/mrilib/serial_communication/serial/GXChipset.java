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
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2.
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package com.inventive.ugosmartbuddy.mrilib.serial_communication.serial;

import android.hardware.usb.UsbDeviceConnection;

import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.enums.Chipset;

import java.io.IOException;

;



/**
 * Serial port settings. Settings vary between vendors.
 */
abstract class GXChipset {

    public abstract Chipset getChipset();

    /**
     * @return Is status header filtered.
     */
    public boolean isfilterStatus() {
        return false;
    }

    /**
     * Remove status bytes from data.
     * @param data Received data.
     * @param size Data length.
     * @param maxSize Max packet length.
     * @return Data size.
     */
    public int removeStatus(byte[] data, int size, int maxSize) {
        throw new RuntimeException("removeStatus is not implemented.");
    }

    /**
     * Is vendor using this chip set.
     *
     * @param vendor  Vendor ID.
     * @param product Product ID.
     * @return True, if used chipset.
     */
    static boolean isUsing(final String stringManufacturer, final int vendor, final int product) {
        throw new RuntimeException("isUsing is not implemented.");
    }

    abstract boolean open(GXSerial serial, UsbDeviceConnection connection, byte[] rawDescriptors) throws IOException;
}

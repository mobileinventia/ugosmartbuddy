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

package com.inventive.ugosmartbuddy.mrilib.serial_communication.common;

/**
 * Argument class for IGXMedia data received events.
 */
public class ReceiveEventArgs {
    /**
     * Received data.
     */
    private Object mReceivedData;
    /**
     * Sender information.
     */
    private String mSenderInformation;

    /**
     * Constructor.
     */
    public ReceiveEventArgs() {

    }

    /**
     * Constructor.
     *
     * @param data       Received data.
     * @param senderInfo Sender information.
     */
    public ReceiveEventArgs(final Object data, final String senderInfo) {
        setData(data);
        setSenderInfo(senderInfo);
    }

    /**
     * Get received data from the device.
     *
     * @return Received data.
     */
    public final Object getData() {
        return mReceivedData;
    }

    /**
     * Set received data from the device.
     *
     * @param value Received data.
     */
    public final void setData(final Object value) {
        mReceivedData = value;
    }

    /**
     * Get media depend sender information. Example sender's TCP/IP address.
     *
     * @return Media depend sender information.
     */
    public final String getSenderInfo() {
        return mSenderInformation;
    }

    /**
     * Set media depend sender information. Example sender's TCP/IP address.
     *
     * @param value Media depend sender information.
     */
    public final void setSenderInfo(final String value) {
        mSenderInformation = value;
    }

    @Override
    public final String toString() {
        if (mReceivedData instanceof byte[]) {
            return mSenderInformation + ":"
                    + GXCommon.bytesToHex((byte[]) mReceivedData);
        }
        return mSenderInformation + ":" + String.valueOf(mReceivedData);
    }
}
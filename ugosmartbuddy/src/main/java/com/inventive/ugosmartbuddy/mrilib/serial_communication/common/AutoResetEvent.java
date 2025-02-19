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
 * Auto reset event is used to wait until there is new data received from the
 * media.
 *
 * @author Gurux Ltd.
 */
public class AutoResetEvent {
    /**
     * Monitor is used for synchronize class.
     */
    private final Object mMonitor = new Object();

    /**
     * Is new data received.
     */
    private volatile boolean mIsOpen = false;

    /**
     * Constructor.
     *
     * @param open Is current state set.
     */
    public AutoResetEvent(final boolean open) {
        mIsOpen = open;
    }

    /**
     * Wait until event occurs.
     *
     * @return True when event occurs or false if event is closed.
     */
    public final boolean waitOne() {
        synchronized (mMonitor) {
            while (!mIsOpen) {
                try {
                    mMonitor.wait();
                } catch (InterruptedException ex) {
                    return false;
                }
            }
            mIsOpen = false;
            return true;
        }
    }

    /**
     * Wait until event occurs.
     *
     * @param timeout Wait time in mill seconds.
     * @return True when event occurs or false if event is closed.
     */
    public final boolean waitOne(final long timeout) {
        synchronized (mMonitor) {
            boolean ret = true;
            long t = System.currentTimeMillis();
            while (!mIsOpen) {
                try {
                    mMonitor.wait(timeout);
                } catch (InterruptedException ex) {
                    return false;
                }
                // Check for timeout
                if (System.currentTimeMillis() - t >= timeout) {
                    ret = false;
                    break;
                }
            }
            mIsOpen = false;
            return ret;
        }
    }

    /**
     * Set synchronized object value.
     */
    public final void set() {
        synchronized (mMonitor) {
            mIsOpen = true;
            mMonitor.notify();
        }
    }

    /**
     * Reset synchronized object value.
     */
    public final void reset() {
        mIsOpen = false;
    }
}
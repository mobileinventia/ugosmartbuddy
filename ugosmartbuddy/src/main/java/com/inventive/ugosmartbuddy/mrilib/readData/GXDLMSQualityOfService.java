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

public class GXDLMSQualityOfService {
    private int precedence;
    private int delay;
    private int reliability;
    private int peakThroughput;
    private int meanThroughput;

    public final int getPrecedence() {
        return precedence;
    }

    public final void setPrecedence(final int value) {
        precedence = value;
    }

    public final int getDelay() {
        return delay;
    }

    public final void setDelay(final int value) {
        delay = value;
    }

    public final int getReliability() {
        return reliability;
    }

    public final void setReliability(final int value) {
        reliability = value;
    }

    public final int getPeakThroughput() {
        return peakThroughput;
    }

    public final void setPeakThroughput(final int value) {
        peakThroughput = value;
    }

    public final int getMeanThroughput() {
        return meanThroughput;
    }

    public final void setMeanThroughput(final int value) {
        meanThroughput = value;
    }
}

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


public class GXReplyData {

    /**
     * Is received frame echo.
     */
    private boolean echo = false;

    /**
     * Is more data available.
     */
    private RequestTypes moreData;

    /**
     * Received command.
     */
    private int command;

    /**
     * Received ciphered command.
     */
    private int cipheredCommand;

    /**
     * Received command type.
     */
    private int commandType;

    /**
     * Received data.
     */
    private GXByteBuffer data = new GXByteBuffer();

    /**
     * Is frame complete.
     */
    private boolean complete;

    /**
     * HDLC frame ID.
     */
    private short frameId;

    /**
     * Received error.
     */
    private int error;

    /**
     * Read value.
     */
    private Object dataValue = null;

    /**
     * Expected count of element in the array.
     */
    private int totalCount = 0;

    /**
     * Last read position. This is used in peek to solve how far data is read.
     */
    private int readPosition;

    /**
     * Packet length.
     */
    private int packetLength = 0;

    /**
     * Try get value.
     */
    private boolean peek;

    /**
     * Data type.
     */
    private DataType dataType = DataType.NONE;

    /**
     * Cipher index is position where data is decrypted.
     */
    private int cipherIndex = 0;

    /**
     * Data notification date time.
     */
    private GXDateTime time = null;

    /**
     * XML settings.
     */
    private GXDLMSTranslatorStructure xml;

    /**
     * Invoke ID.
     */
    private long invokeId;

    /**
     * GBT block number.
     */
    private int blockNumber;
    /**
     * GBT block number ACK.
     */
    private int blockNumberAck;
    /**
     * Is GBT streaming in use.
     */
    private boolean streaming;
    /**
     * GBT Window size. This is for internal use.
     */
    private byte windowSize;

    /**
     * Client address of the notification message. Notification message sets
     * this.
     */
    private int clientAddress;

    /**
     * Server address of the notification message. Notification message sets
     * this.
     */
    private int serverAddress;

    /**
     * Gateway information.
     */
    private GXDLMSGateway gateway;

    /**
     * Constructor.
     * 
     * @param more
     *            Is more data available.
     * @param cmd
     *            Received command.
     * @param buff
     *            Received data.
     * @param forComplete
     *            Is frame complete.
     * @param err
     *            Received error ID.
     */
    GXReplyData(final RequestTypes more, final short cmd,
            final GXByteBuffer buff, final boolean forComplete,
            final byte err) {
        clear();
        moreData = more;
        command = cmd;
        data = buff;
        complete = forComplete;
        error = err;
    }

    /**
     * Constructor.
     */
    public GXReplyData() {
        clear();
    }

    public final DataType getValueType() {
        return dataType;
    }

    public final void setValueType(final DataType value) {
        dataType = value;
    }

    public final Object getValue() {
        return dataValue;
    }

    public final void setValue(final Object value) {
        dataValue = value;
    }

    public final int getReadPosition() {
        return readPosition;
    }

    public final void setReadPosition(final int value) {
        readPosition = value;
    }

    /**
     * @return Packet length.
     */
    public final int getPacketLength() {
        return packetLength;
    }

    /**
     * @param value
     *            Packet length.
     */
    public final void setPacketLength(final int value) {
        packetLength = value;
    }

    public final void setCommand(final int value) {
        command = value;
    }

    public final void setData(final GXByteBuffer value) {
        data = value;
    }

    public final void setComplete(final boolean value) {
        complete = value;
    }

    /**
     * @param value
     *            Received error.
     */
    public final void setError(final int value) {
        error = value;
    }

    public final void setTotalCount(final int value) {
        totalCount = value;
    }

    /**
     * @param value
     *            Is received frame echo.
     */
    final void setEcho(final boolean value) {
        echo = value;
    }

    /**
     * @return Is received frame echo.
     */
    public final boolean isEcho() {
        return echo;
    }

    /**
     * Reset data values to default.
     */
    public final void clear() {
        moreData = RequestTypes.NONE;
        cipheredCommand = command = Command.NONE;
        commandType = 0;
        data.capacity(0);
        complete = false;
        error = 0;
        totalCount = 0;
        dataValue = null;
        readPosition = 0;
        packetLength = 0;
        dataType = DataType.NONE;
        cipherIndex = 0;
        time = null;
        if (xml != null) {
            xml.setXmlLength(0);
        }
        invokeId = 0;
    }

    /**
     * @return Is more data available.
     */
    public final boolean isMoreData() {
        return moreData != RequestTypes.NONE && error == 0;
    }

    /**
     * @return Is notify message.
     */
    public boolean isNotify() {
        return command == Command.EVENT_NOTIFICATION
                || command == Command.DATA_NOTIFICATION
                || command == Command.INFORMATION_REPORT;
    }

    /**
     * Is more data available.
     * 
     * @return Return None if more data is not available or Frame or Block type.
     */
    public final RequestTypes getMoreData() {
        return moreData;
    }

    public final void setMoreData(final RequestTypes forValue) {
        moreData = forValue;
    }

    /**
     * Get received command.
     * 
     * @return Received command.
     */
    public final int getCommand() {
        return command;
    }

    /**
     * Get received data.
     * 
     * @return Received data.
     */
    public final GXByteBuffer getData() {
        return data;
    }

    /**
     * Is frame complete.
     * 
     * @return Returns true if frame is complete or false if bytes is missing.
     */
    public final boolean isComplete() {
        return complete;
    }

    /**
     * Get Received error. Value is zero if no error has occurred.
     * 
     * @return Received error.
     */
    public final int getError() {
        return error;
    }

    public final String getErrorMessage() {
        return GXDLMS.getDescription(error);
    }

    /**
     * Get total count of element in the array. If this method is used peek must
     * be set true.
     * 
     * @return Count of element in the array.
     * @see #setPeek
     * @see #getCount
     */
    public final int getTotalCount() {
        return totalCount;
    }

    /**
     * Get count of read elements. If this method is used peek must be set true.
     * 
     * @return Count of read elements.
     * @see #setPeek
     * @see #getTotalCount
     */
    public final int getCount() {
        if (dataValue instanceof Object[]) {
            return ((Object[]) dataValue).length;
        }
        return 0;
    }

    /**
     * Get is value try to peek.
     * 
     * @return Is value try to peek.
     * @see #getCount
     * @see #getTotalCount
     */
    public final boolean getPeek() {
        return peek;
    }

    /**
     * Set is value try to peek.
     * 
     * @param forValue
     *            Is value try to peek.
     */
    public final void setPeek(final boolean forValue) {
        peek = forValue;
    }

    /**
     * @return Cipher index is position where data is decrypted.
     */
    public final int getCipherIndex() {
        return cipherIndex;
    }

    /**
     * @param value
     *            Cipher index is position where data is decrypted.
     */
    public final void setCipherIndex(final int value) {
        cipherIndex = value;
    }

    /**
     * @return Data notification date time.
     */
    public final GXDateTime getTime() {
        return time;
    }

    /**
     * @param value
     *            Data notification date time.
     */
    public final void setTime(final GXDateTime value) {
        time = value;
    }

    /**
     * @return Received command type.
     */
    public final int getCommandType() {
        return commandType;
    }

    /**
     * @param value
     *            Received command type.
     */
    public final void setCommandType(final int value) {
        commandType = value;
    }

    /**
     * @return XML settings.
     */
    final GXDLMSTranslatorStructure getXml() {
        return xml;
    }

    /**
     * @param value
     *            XML settings.
     */
    public void setXml(final GXDLMSTranslatorStructure value) {
        xml = value;
    }

    /**
     * @return HDLC frame ID.
     */
    public final short getFrameId() {
        return frameId;
    }

    /**
     * @param value
     *            HDLC frame ID.
     */
    public final void setFrameId(final short value) {
        frameId = value;
    }

    /**
     * @return Invoke ID.
     */
    public final long getInvokeId() {
        return invokeId;
    }

    /**
     * @param value
     *            Invoke ID.
     */
    public final void setInvokeId(final long value) {
        invokeId = value;
    }

    /**
     * @return GBT block number.
     */
    public final int getBlockNumber() {
        return blockNumber;
    }

    /**
     * @param value
     *            GBT block number.
     */
    final void setBlockNumber(final int value) {
        blockNumber = value;
    }

    /**
     * @return GBT block number ACK.
     */
    public final int getBlockNumberAck() {
        return blockNumberAck;
    }

    /**
     * @param value
     *            GBT block number ACK.
     */
    final void setBlockNumberAck(final int value) {
        blockNumberAck = value;
    }

    /**
     * @return Is GBT streaming in use.
     */
    public final boolean getStreaming() {
        return streaming;
    }

    /**
     * @param value
     *            Is GBT streaming in use.
     */
    final void setStreaming(boolean value) {
        streaming = value;
    }

    /**
     * @return GBT Window size. This is for internal use.
     */
    public final byte getWindowSize() {
        return windowSize;
    }

    /**
     * @param value
     *            GBT Window size. This is for internal use.
     */
    public final void setWindowSize(byte value) {
        windowSize = value;
    }

    /**
     * @return Is GBT streaming.
     * @deprecated use {@link #isStreaming} instead.
     */
    @Deprecated
    public final boolean IsStreaming() {
        return isStreaming();
    }

    /**
     * @return Is GBT streaming.
     */
    public final boolean isStreaming() {
        return getStreaming() && (getBlockNumberAck() * getWindowSize())
                + 1 > getBlockNumber();
    }

    /**
     * @return Client address of the notification message. Notification message
     *         sets this.
     */
    public final int getClientAddress() {
        return clientAddress;
    }

    /**
     * @param value
     *            Client address of the notification message. Notification
     *            message sets this.
     */
    public final void setClientAddress(final int value) {
        clientAddress = value;
    }

    /**
     * @return Server address of the notification message. Notification message
     *         sets this.
     */
    public final int getServerAddress() {
        return serverAddress;
    }

    /**
     * @param value
     *            Server address of the notification message. Notification
     *            message sets this.
     */
    public final void setServerAddress(int value) {
        serverAddress = value;
    }

    /**
     * @return Gateway information.
     */
    public final GXDLMSGateway getGateway() {
        return gateway;
    }

    /**
     * @param value
     *            Gateway information.
     */
    public final void setGateway(final GXDLMSGateway value) {
        gateway = value;
    }

    @Override
    public String toString() {
        if (xml != null) {
            return xml.toString();
        }
        if (data == null) {
            return "";
        }
        return data.toString();
    }

    /**
     * @return Received ciphered command.
     */
    public int getCipheredCommand() {
        return cipheredCommand;
    }

    /**
     * @param value
     *            Received ciphered command.
     */
    public void setCipheredCommand(final int value) {
        cipheredCommand = value;
    }
}

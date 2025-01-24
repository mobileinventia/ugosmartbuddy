package com.inventive.ugosmartbuddy.mrilib.readData;


//----------------------------------------------------------------------------
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

import android.util.Log;

import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.IGXMedia;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.enums.TraceLevel;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.Parity;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.io.StopBits;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

public class GXDLMSReader {
    IGXMedia Media;
    TraceLevel Trace;
    GXDLMSClient dlms;
    boolean iec;
    java.nio.ByteBuffer replyBuff;
    int WaitTime = 10000;
    char reconnect;

    public void setWaitTime(int waitTime) {
        WaitTime = waitTime;
    }


    public GXDLMSReader(GXDLMSClient client, IGXMedia media, TraceLevel trace)
            throws Exception {
        Trace = trace;
        Media = media;
        dlms = client;
        if (trace.ordinal() > TraceLevel.WARNING.ordinal()) {

        }
        if (dlms.getInterfaceType() == InterfaceType.WRAPPER) {
            replyBuff = java.nio.ByteBuffer.allocate(8 + 1024);
        } else {
            replyBuff = java.nio.ByteBuffer.allocate(100);
        }
    }

    public void close() throws Exception {
        if (WifiConnectivity.getIsWifi() || (Media != null && Media.isOpen())) {
            GXReplyData reply = new GXReplyData();
            /*try {
                readDataBlock(dlms.releaseRequest(), reply);
            }
            catch (Exception e) {
                // All meters don't support release.
            }*/
            reply.clear();
            readDLMSPacket(dlms.disconnectRequest(), reply);
            Media.close();
        }
    }

    String now() {
        return new SimpleDateFormat("HH:mm:ss.SSS")
                .format(java.util.Calendar.getInstance().getTime());
    }


    public void readDLMSPacket(byte[][] data) throws Exception {
        GXReplyData reply = new GXReplyData();
        for (byte[] it : data) {
            reply.clear();
            readDLMSPacket(it, reply);
        }
    }

    /**
     * Handle received notify messages.
     *
     * @param reply Received data.
     * @throws Exception
     */
    private void handleNotifyMessages(final GXReplyData reply)
            throws Exception {
        List<Entry<GXDLMSObject, Integer>> items =
                new ArrayList<Entry<GXDLMSObject, Integer>>();
        Object value = dlms.parseReport(reply, items);
        // If Event notification or Information report.
        if (value == null) {
            for (Entry<GXDLMSObject, Integer> it : items) {

            }
        } else // Show data notification.
        {
            if (value instanceof Object[]) {
                for (Object it : (Object[]) value) {

                }
            } else {

            }

        }
        reply.clear();
    }

    /**
     * Read DLMS Data from the device. If access is denied return null.
     */
    public void readDLMSPacket(byte[] data, GXReplyData reply)
            throws Exception {
        if (!reply.getStreaming() && (data == null || data.length == 0)) {
            return;
        }
        GXReplyData notify = new GXReplyData();
        reply.setError((short) 0);
        Object eop = (byte) 0x7E;
        // In network connection terminator is not used.
       /* if (dlms.getInterfaceType() == InterfaceType.WRAPPER
                && Media instanceof GXNet) {
            eop = null;
        }*/
        Integer pos = 0;
        boolean succeeded = false;
        ReceiveParameters<byte[]> p =
                new ReceiveParameters<byte[]>(byte[].class);
        p.setEop(eop);
        if (dlms.getInterfaceType() == InterfaceType.WRAPPER) {
            p.setCount(8);
        } else {
            p.setCount(5);
        }
        p.setWaitTime(WaitTime);
        GXByteBuffer rd = new GXByteBuffer();
        synchronized (Media.getSynchronous()) {
            while (!succeeded) {
                if (!reply.isStreaming()) {

                    Media.send(data, null);
                }
                if (p.getEop() == null) {
                    p.setCount(1);
                }
                succeeded = Media.receive(p);
                if (!succeeded) {
                    // Try to read again...
                    if (pos++ == 1) {
                        throw new RuntimeException("Failed to receive reply.");
                    }

                }
            }
            rd = new GXByteBuffer(p.getReply());
            int msgPos = 0;
            // Loop until whole DLMS packet is received.
            try {
                while (!dlms.getData(rd, reply, notify)) {
                    p.setReply(null);
                    if (notify.getData().getData() != null) {
                        // Handle notify.
                        if (!notify.isMoreData()) {
                            // Show received push message as XML.
                          /*  GXDLMSTranslator t = new GXDLMSTranslator(
                                    TranslatorOutputType.SIMPLE_XML);*/
                            //   String xml = t.dataToXml(notify.getData());
                            //  System.out.println(xml);
                            notify.clear();
                            msgPos = rd.position();
                        }
                        continue;
                    }

                    if (p.getEop() == null) {
                        p.setCount(dlms.getFrameSize(rd));
                    }
                    while (!Media.receive(p)) {
                        // If echo.
                        if (reply.isEcho()) {
                            Media.send(data, null);
                        }
                        // Try to read again...
                        if (++pos == 3) {
                            throw new Exception(
                                    "Failed to receive reply from the device in given time.");
                        }

                    }
                    rd.position(msgPos);
                    rd.set(p.getReply());
                }
            } catch (Exception e) {

                throw e;
            }
        }
        if (reply.getError() != 0) {
            Log.v("Error Code","" + reply.getError());
            if (reply.getError() == ErrorCode.REJECTED.getValue()) {
                Thread.sleep(1000);
                readDLMSPacket(data, reply);
            } else {
                throw new GXDLMSException(reply.getError());
            }
        }
    }

    void readDataBlock(byte[][] data, GXReplyData reply) throws Exception {
        if (data != null) {
            for (byte[] it : data) {
                reply.clear();
                readDataBlock(it, reply);
            }
        }
    }

    /**
     * Reads next data block.
     *
     * @param data
     * @return
     * @throws Exception
     */
    void readDataBlock(byte[] data, GXReplyData reply) throws Exception {
        if (data != null && data.length != 0) {
            readDLMSPacket(data, reply);
            while (reply.isMoreData()) {
                if (reply.isStreaming()) {
                    data = null;
                } else {
                    data = dlms.receiverReady(reply.getMoreData());
                }
                readDLMSPacket(data, reply);
            }
        }
    }

    /**
     * Initializes connection.
     *
     * @param
     * @throws InterruptedException
     * @throws Exception
     */
  /*  public void initializeConnection() throws Exception, InterruptedException {

        if (Media instanceof GXSerial) {
            GXSerial serial = (GXSerial) Media;

            if (iec) {
                ReceiveParameters<byte[]> p =
                        new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop((byte) '\n');
                p.setWaitTime(WaitTime);
                String data;
                String replyStr;
                synchronized (Media.getSynchronous()) {
                    data = "/?!\r\n";

                    Media.send(data, null);
                    if (!Media.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    // If echo is used.
                    replyStr = new String(p.getReply());
                    if (data.equals(replyStr)) {
                        p.setReply(null);
                        if (!Media.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        replyStr = new String(p.getReply());
                    }
                }
                if (replyStr.length() == 0 || replyStr.charAt(0) != '/') {
                    throw new Exception("Invalid responce : " + replyStr);
                }
                int bitrate = 0;
                char baudrate = replyStr.charAt(4);
                switch (baudrate) {
                    case '0':
                        bitrate = 300;
                        break;
                    case '1':
                        bitrate = 600;
                        break;
                    case '2':
                        bitrate = 1200;
                        break;
                    case '3':
                        bitrate = 2400;
                        break;
                    case '4':
                        bitrate = 4800;
                        break;
                    case '5':
                        bitrate = 9600;
                        break;
                    case '6':
                        bitrate = 19200;
                        break;
                    default:
                        throw new Exception("Unknown baud rate.");
                }

                // Send ACK
                // Send Protocol control character
                byte controlCharacter = (byte) '2';// "2" HDLC protocol
                // procedure (Mode E)
                // Send Baudrate character
                // Mode control character
                byte ModeControlCharacter = (byte) '2';// "2" //(HDLC protocol
                // procedure) (Binary
                // mode)
                // Set mode E.
                byte[] tmp = new byte[]{0x06, controlCharacter,
                        (byte) baudrate, ModeControlCharacter, 13, 10};
                p.setReply(null);
                synchronized (Media.getSynchronous()) {
                    Media.send(tmp, null);

                    p.setWaitTime(100);
                    if (Media.receive(p)) {

                    }
                    Media.close();
                    // This sleep make sure that all meters can be read.
                    Thread.sleep(400);
                    serial.setDataBits(8);
                    serial.setParity(Parity.NONE);
                    serial.setStopBits(StopBits.ONE);
                    //serial.setBaudRate(BaudRate.forValue(bitrate));
                    Media.open();
                    Thread.sleep(400);
                }
            }
        }
        GXReplyData reply = new GXReplyData();

        byte[] data = dlms.snrmRequest();
        if (data.length != 0) {
            readDLMSPacket(data, reply);
            // Has server accepted client.
            dlms.parseUAResponse(reply.getData());

            // Allocate buffer to same size as transmit buffer of the meter.
            // Size of replyBuff is payload and frame (Bop, EOP, crc).
            int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                    .intValue() & 0xFFFFFFFFL) + 40);
            replyBuff = java.nio.ByteBuffer.allocate(size);
        }
        reply.clear();
        readDataBlock(dlms.aarqRequest(), reply);
        // Parse reply.
        dlms.parseAareResponse(reply.getData());
        reply.clear();

        // Get challenge Is HLS authentication is used.
        if (dlms.getAuthentication().getValue() > Authentication.LOW
                .getValue()) {
            for (byte[] it : dlms.getApplicationAssociationRequest()) {
                readDLMSPacket(it, reply);
            }
            dlms.parseApplicationAssociationResponse(reply.getData());
        }
    }*/

    public boolean initializeConnection(boolean reConnect) throws Exception, InterruptedException {

        if (Media instanceof GXSerial) {
            GXSerial serial = (GXSerial) Media;

            if (iec) {
                ReceiveParameters<byte[]> p =
                        new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop((byte) '\n');
                p.setWaitTime(WaitTime);
                String data;
                String replyStr;
                synchronized (Media.getSynchronous()) {
                    data = "/?!\r\n";

                    Media.send(data, null);
                    if (!Media.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    // If echo is used.
                    replyStr = new String(p.getReply());
                    if (data.equals(replyStr)) {
                        p.setReply(null);
                        if (!Media.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        replyStr = new String(p.getReply());
                    }
                }
                if (replyStr.length() == 0 || replyStr.charAt(0) != '/') {
                    throw new Exception("Invalid responce : " + replyStr);
                }
                char baudrate = replyStr.charAt(4);

                // Send ACK
                // Send Protocol control character
                byte controlCharacter = (byte) '2';// "2" HDLC protocol
                // procedure (Mode E)
                // Send Baudrate character
                // Mode control character
                byte ModeControlCharacter = (byte) '2';// "2" //(HDLC protocol
                // procedure) (Binary
                // mode)
                // Set mode E.
                byte[] tmp = new byte[]{0x06, controlCharacter,
                        (byte) baudrate, ModeControlCharacter, 13, 10};
                p.setReply(null);
                synchronized (Media.getSynchronous()) {
                    Media.send(tmp, null);

                    p.setWaitTime(100);

                    Media.close();
                    // This sleep make sure that all meters can be read.
                    Thread.sleep(400);
                    serial.setDataBits(8);
                    serial.setParity(Parity.NONE);
                    serial.setStopBits(StopBits.ONE);
                    //serial.setBaudRate(BaudRate.forValue(bitrate));
                    Media.open();
                    Thread.sleep(400);
                }
            }
        }
        GXReplyData reply = new GXReplyData();

        byte[] data = dlms.snrmRequest();
        if (data.length != 0) {
            readDLMSPacket(data, reply);
            // Has server accepted client.
            dlms.parseUAResponse(reply.getData());

            // Allocate buffer to same size as transmit buffer of the meter.
            // Size of replyBuff is payload and frame (Bop, EOP, crc).
            int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                    .intValue() & 0xFFFFFFFFL) + 40);
            replyBuff = java.nio.ByteBuffer.allocate(size);
        }
        reply.clear();
        readDataBlock(dlms.aarqRequest(), reply);
        // Parse reply.
        dlms.parseAareResponse(reply.getData());
        reply.clear();

        // Get challenge Is HLS authentication is used.
        if (dlms.getAuthentication().getValue() > Authentication.LOW
                .getValue()) {
            for (byte[] it : dlms.getApplicationAssociationRequest()) {
                readDLMSPacket(it, reply);
            }
            dlms.parseApplicationAssociationResponse(reply.getData());
        }

        GXDLMSDisconnectControl dc = new GXDLMSDisconnectControl();

        if (reConnect &&  dc.getControlState() != null &&  dc.getControlState().name().equals("DISCONNECTED")) {
            readDataBlock(dc.remoteReconnect(dlms), reply);
            return true;
        } else {
            readDataBlock(dc.remoteDisconnect(dlms), reply);
            return false;
        }
    }


    public String initializeConnectionStatus() throws Exception, InterruptedException {

        if (Media instanceof GXSerial) {
            GXSerial serial = (GXSerial) Media;

            if (iec) {
                ReceiveParameters<byte[]> p =
                        new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop((byte) '\n');
                p.setWaitTime(WaitTime);
                String data;
                String replyStr;
                synchronized (Media.getSynchronous()) {
                    data = "/?!\r\n";

                    Media.send(data, null);
                    if (!Media.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    // If echo is used.
                    replyStr = new String(p.getReply());
                    if (data.equals(replyStr)) {
                        p.setReply(null);
                        if (!Media.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        replyStr = new String(p.getReply());
                    }
                }
                if (replyStr.length() == 0 || replyStr.charAt(0) != '/') {
                    throw new Exception("Invalid responce : " + replyStr);
                }
                char baudrate = replyStr.charAt(4);

                // Send ACK
                // Send Protocol control character
                byte controlCharacter = (byte) '2';// "2" HDLC protocol
                // procedure (Mode E)
                // Send Baudrate character
                // Mode control character
                byte ModeControlCharacter = (byte) '2';// "2" //(HDLC protocol
                // procedure) (Binary
                // mode)
                // Set mode E.
                byte[] tmp = new byte[]{0x06, controlCharacter,
                        (byte) baudrate, ModeControlCharacter, 13, 10};
                p.setReply(null);
                synchronized (Media.getSynchronous()) {
                    Media.send(tmp, null);

                    p.setWaitTime(100);

                    Media.close();
                    // This sleep make sure that all meters can be read.
                    Thread.sleep(400);
                    serial.setDataBits(8);
                    serial.setParity(Parity.NONE);
                    serial.setStopBits(StopBits.ONE);
                    //serial.setBaudRate(BaudRate.forValue(bitrate));
                    Media.open();
                    Thread.sleep(400);
                }
            }
        }
        GXReplyData reply = new GXReplyData();

        byte[] data = dlms.snrmRequest();
        if (data.length != 0) {
            readDLMSPacket(data, reply);
            // Has server accepted client.
            dlms.parseUAResponse(reply.getData());

            // Allocate buffer to same size as transmit buffer of the meter.
            // Size of replyBuff is payload and frame (Bop, EOP, crc).
            int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                    .intValue() & 0xFFFFFFFFL) + 40);
            replyBuff = java.nio.ByteBuffer.allocate(size);
        }
        reply.clear();
        readDataBlock(dlms.aarqRequest(), reply);
        // Parse reply.
        dlms.parseAareResponse(reply.getData());
        reply.clear();

        // Get challenge Is HLS authentication is used.
        if (dlms.getAuthentication().getValue() > Authentication.LOW
                .getValue()) {
            for (byte[] it : dlms.getApplicationAssociationRequest()) {
                readDLMSPacket(it, reply);
            }
            dlms.parseApplicationAssociationResponse(reply.getData());
        }

        GXDLMSDisconnectControl dc = new GXDLMSDisconnectControl();

        return dc.getControlState().name();
       /* if (reConnect &&  dc.getControlState() != null &&  dc.getControlState().name().equals("DISCONNECTED")) {
            readDataBlock(dc.remoteReconnect(dlms), reply);
            return true;
        } else {
            readDataBlock(dc.remoteDisconnect(dlms), reply);
            return false;
        }*/
    }

    /**
     * Reads selected DLMS object with selected attribute index.
     *
     * @param item
     * @param attributeIndex
     * @return
     * @throws Exception
     */
    public Object read(GXDLMSObject item, int attributeIndex) throws Exception {
        byte[] data = dlms.read(item.getName(), item.getObjectType(),
                attributeIndex)[0];
        GXReplyData reply = new GXReplyData();

        readDataBlock(data, reply);
        /*if (reconnect == 'R') {
            GXDLMSDisconnectControl dc = new GXDLMSDisconnectControl();
            readDataBlock(dc.remoteReconnect(dlms), reply);
        } else if (reconnect == 'D') {
            GXDLMSDisconnectControl dc = new GXDLMSDisconnectControl();
            readDataBlock(dc.remoteDisconnect(dlms), reply);
        }*/
        // Update data type on read.
        if (item.getDataType(attributeIndex) == DataType.NONE) {
            item.setDataType(attributeIndex, reply.getValueType());
        }
        return dlms.updateValue(item, attributeIndex, reply.getValue());
    }

    /*
     * Read list of attributes.
     */
    public void readList(List<Entry<GXDLMSObject, Integer>> list)
            throws Exception {
        if (list.size() != 0) {
            byte[][] data = dlms.readList(list);
            GXReplyData reply = new GXReplyData();
            List<Object> values = new ArrayList<Object>(list.size());
            for (byte[] it : data) {
                readDataBlock(it, reply);
                // Value is null if data is send in multiple frames.
                if (reply.getValue() != null) {
                    values.addAll(Arrays.asList((Object[]) reply.getValue()));
                }
                reply.clear();
            }
            if (values.size() != list.size()) {
                throw new Exception(
                        "Invalid reply. Read items count do not match.");
            }
            dlms.updateValues(list, values);
        }
    }

    /**
     * Read Profile Generic's data by range (start and end time).
     *
     * @param pg
     * @param
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public Object[] readRowsByRange(final GXDLMSProfileGeneric pg,
                                    final Date start, final Date end) throws Exception {
        GXReplyData reply = new GXReplyData();
        byte[][] data = dlms.readRowsByRange(pg, start, end);
        readDataBlock(data, reply);
        return (Object[]) dlms.updateValue(pg, 2, reply.getValue());
    }

    /**
     * Read association view.
     *
     * @throws Exception
     */
    public void getAssociationView() throws Exception {
        GXReplyData reply = new GXReplyData();
        // Get Association view from the meter.
        readDataBlock(dlms.getObjectsRequest(), reply);
        GXDLMSObjectCollection objects =
                dlms.parseObjects(reply.getData(), true);
        // Get description of the objects.
        GXDLMSConverter converter = new GXDLMSConverter();
        converter.updateOBISCodeInformation(objects);
    }


    public void initializeConnection() throws Exception, InterruptedException {

        if (Media instanceof GXSerial) {
            GXSerial serial = (GXSerial) Media;

            if (iec) {
                ReceiveParameters<byte[]> p =
                        new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop((byte) '\n');
                p.setWaitTime(WaitTime);
                String data;
                String replyStr;
                synchronized (Media.getSynchronous()) {
                    data = "/?!\r\n";

                    Media.send(data, null);
                    if (!Media.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }

                    // If echo is used.
                    replyStr = new String(p.getReply());
                    if (data.equals(replyStr)) {
                        p.setReply(null);
                        if (!Media.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }

                        replyStr = new String(p.getReply());
                    }
                }
                if (replyStr.length() == 0 || replyStr.charAt(0) != '/') {
                    throw new Exception("Invalid responce : " + replyStr);
                }
                int bitrate = 0;
                char baudrate = replyStr.charAt(4);
                switch (baudrate) {
                    case '0':
                        bitrate = 300;
                        break;
                    case '1':
                        bitrate = 600;
                        break;
                    case '2':
                        bitrate = 1200;
                        break;
                    case '3':
                        bitrate = 2400;
                        break;
                    case '4':
                        bitrate = 4800;
                        break;
                    case '5':
                        bitrate = 9600;
                        break;
                    case '6':
                        bitrate = 19200;
                        break;
                    default:
                        throw new Exception("Unknown baud rate.");
                }

                // Send ACK
                // Send Protocol control character
                byte controlCharacter = (byte) '2';// "2" HDLC protocol
                // procedure (Mode E)
                // Send Baudrate character
                // Mode control character
                byte ModeControlCharacter = (byte) '2';// "2" //(HDLC protocol
                // procedure) (Binary
                // mode)
                // Set mode E.
                byte[] tmp = new byte[]{0x06, controlCharacter,
                        (byte) baudrate, ModeControlCharacter, 13, 10};
                p.setReply(null);
                synchronized (Media.getSynchronous()) {
                    Media.send(tmp, null);

                    p.setWaitTime(100);
                    if (Media.receive(p)) {

                    }
                    Media.close();
                    // This sleep make sure that all meters can be read.
                    Thread.sleep(400);
                    serial.setDataBits(8);
                    serial.setParity(Parity.NONE);
                    serial.setStopBits(StopBits.ONE);
                    //serial.setBaudRate(BaudRate.forValue(bitrate));
                    Media.open();
                    Thread.sleep(400);
                }
            }
        }
        GXReplyData reply = new GXReplyData();

        byte[] data = dlms.snrmRequest();
        if (data.length != 0) {
            readDLMSPacket(data, reply);
            // Has server accepted client.
            dlms.parseUAResponse(reply.getData());

            // Allocate buffer to same size as transmit buffer of the meter.
            // Size of replyBuff is payload and frame (Bop, EOP, crc).
            int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                    .intValue() & 0xFFFFFFFFL) + 40);
            replyBuff = java.nio.ByteBuffer.allocate(size);
        }
        reply.clear();
        readDataBlock(dlms.aarqRequest(), reply);
        // Parse reply.
        dlms.parseAareResponse(reply.getData());
        reply.clear();

        // Get challenge Is HLS authentication is used.
        if (dlms.getAuthentication().getValue() > Authentication.LOW
                .getValue()) {
            for (byte[] it : dlms.getApplicationAssociationRequest()) {
                readDLMSPacket(it, reply);
            }
            dlms.parseApplicationAssociationResponse(reply.getData());
        }
    }


}
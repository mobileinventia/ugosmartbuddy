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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;


/**
 * Online help: <br>
 * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSPushSetup
 */
public class GXDLMSPushSetup extends GXDLMSObject implements IGXDLMSBase {
    private ServiceType service;
    private String destination;
    private MessageType message;

    private List<Entry<GXDLMSObject, GXDLMSCaptureObject>> pushObjectList;
    private GXSendDestinationAndMethod sendDestinationAndMethod;
    private List<Entry<GXDateTime, GXDateTime>> communicationWindow;
    private int randomisationStartInterval;
    private int numberOfRetries;
    private int repetitionDelay;

    /**
     * Constructor.
     */
    public GXDLMSPushSetup() {
        this("0.7.25.9.0.255");
    }

    /**
     * Constructor.
     *
     * @param ln
     *            Logical Name of the object.
     */
    public GXDLMSPushSetup(final String ln) {
        this(ln, 0);
    }

    /**
     * Constructor.
     *
     * @param ln
     *            Logical Name of the object.
     * @param sn
     *            Short Name of the object.
     */
    public GXDLMSPushSetup(final String ln, final int sn) {
        super(ObjectType.PUSH_SETUP, ln, sn);
        pushObjectList =
                new ArrayList<Entry<GXDLMSObject, GXDLMSCaptureObject>>();
        sendDestinationAndMethod = new GXSendDestinationAndMethod();
        communicationWindow =
                new ArrayList<Entry<GXDateTime, GXDateTime>>();
        service = ServiceType.TCP;
        message = MessageType.COSEM_APDU;
    }

    public final ServiceType getService() {
        return service;
    }

    public final void setService(final ServiceType value) {
        service = value;
    }

    public final String getDestination() {
        return destination;
    }

    public final void setDestination(final String value) {
        destination = value;
    }

    public final MessageType getMessage() {
        return message;
    }

    public final void setMessage(final MessageType value) {
        message = value;
    }

    /**
     * @return Defines the list of attributes or objects to be pushed. Upon a
     *         call of the push (data) method the selected attributes are sent
     *         to the destination defined in getSendDestinationAndMethod.
     */
    public final List<Entry<GXDLMSObject, GXDLMSCaptureObject>>
            getPushObjectList() {
        return pushObjectList;
    }

    public final GXSendDestinationAndMethod getSendDestinationAndMethod() {
        return sendDestinationAndMethod;
    }

    /**
     * @return Contains the start and end date/time stamp when the communication
     *         window(s) for the push become active (for the start instant), or
     *         inactive (for the end instant).
     */
    public final List<Entry<GXDateTime, GXDateTime>>
            getCommunicationWindow() {
        return communicationWindow;
    }

    /**
     * @return To avoid simultaneous network connections of a lot of devices at
     *         ex-actly the same point in time, a randomisation interval in
     *         seconds can be defined. This means that the push operation is not
     *         started imme-diately at the beginning of the first communication
     *         window but started randomly delayed.
     */
    public final int getRandomisationStartInterval() {
        return randomisationStartInterval;
    }

    public final void setRandomisationStartInterval(final int value) {
        randomisationStartInterval = value;
    }

    /**
     * @return The maximum number of re-trials in case of unsuccessful push
     *         attempts. After a successful push no further push attempts are
     *         made until the push setup is triggered again. A value of 0 means
     *         no repetitions, i.e. only the initial connection attempt is made.
     */
    public final int getNumberOfRetries() {
        return numberOfRetries;
    }

    /**
     * @param value
     *            The maximum number of re-trials in case of unsuccessful push
     *            attempts. After a successful push no further push attempts are
     *            made until the push setup is triggered again. A value of 0
     *            means no repetitions, i.e. only the initial connection attempt
     *            is made.
     */
    public final void setNumberOfRetries(final byte value) {
        numberOfRetries = value;
    }

    public final int getRepetitionDelay() {
        return repetitionDelay;
    }

    public final void setRepetitionDelay(final int value) {
        repetitionDelay = value;
    }

    @Override
    public final Object[] getValues() {
        return new Object[] { getLogicalName(), pushObjectList,
                sendDestinationAndMethod, communicationWindow,
                new Integer(randomisationStartInterval),
                new Integer(numberOfRetries), new Integer(repetitionDelay) };
    }

    @Override
    public final byte[] invoke(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        if (e.getIndex() != 1) {
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
        return null;
    }

    /*
     * Activates the push process.
     */
    public final byte[][] activate(final GXDLMSClient client) {
        return client.method(getName(), getObjectType(), 1, new Integer(0),
                DataType.INT8);
    }

    @Override
    public final int[] getAttributeIndexToRead(final boolean all) {
        ArrayList<Integer> attributes =
                new ArrayList<Integer>();
        // LN is static and read only once.
        if (all || getLogicalName() == null
                || getLogicalName().compareTo("") == 0) {
            attributes.add(new Integer(1));
        }
        // PushObjectList
        if (all || canRead(2)) {
            attributes.add(new Integer(2));
        }
        // SendDestinationAndMethod
        if (all || canRead(3)) {
            attributes.add(new Integer(3));
        }
        // CommunicationWindow
        if (all || canRead(4)) {
            attributes.add(new Integer(4));
        }
        // RandomisationStartInterval
        if (all || canRead(5)) {
            attributes.add(new Integer(5));
        }
        // NumberOfRetries
        if (all || canRead(6)) {
            attributes.add(new Integer(6));
        }
        // RepetitionDelay
        if (all || canRead(7)) {
            attributes.add(new Integer(7));
        }
        return GXDLMSObjectHelpers.toIntArray(attributes);
    }

    /*
     * Returns amount of attributes.
     */
    @Override
    public final int getAttributeCount() {
        return 7;
    }

    /*
     * Returns amount of methods.
     */
    @Override
    public final int getMethodCount() {
        return 1;
    }

    @Override
    public final DataType getDataType(final int index) {
        if (index == 1) {
            return DataType.OCTET_STRING;
        }
        if (index == 2) {
            return DataType.ARRAY;
        }
        if (index == 3) {
            return DataType.STRUCTURE;
        }
        if (index == 4) {
            return DataType.ARRAY;
        }
        if (index == 5) {
            return DataType.UINT16;
        }
        if (index == 6) {
            return DataType.UINT8;
        }
        if (index == 7) {
            return DataType.UINT16;
        }
        throw new IllegalArgumentException(
                "getDataType failed. Invalid attribute index.");
    }

    /*
     * Returns value of given attribute.
     */
    @Override
    public final Object getValue(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            return GXCommon.logicalNameToBytes(getLogicalName());
        }
        GXByteBuffer buff = new GXByteBuffer();
        if (e.getIndex() == 2) {
            buff.setUInt8(DataType.ARRAY.getValue());
            GXCommon.setObjectCount(pushObjectList.size(), buff);
            for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : pushObjectList) {
                buff.setUInt8(DataType.STRUCTURE.getValue());
                buff.setUInt8(4);
                GXCommon.setData(settings, buff, DataType.UINT16,
                        new Integer(it.getKey().getObjectType().getValue()));
                GXCommon.setData(settings, buff, DataType.OCTET_STRING, GXCommon
                        .logicalNameToBytes(it.getKey().getLogicalName()));
                GXCommon.setData(settings, buff, DataType.INT8,
                        new Integer(it.getValue().getAttributeIndex()));
                GXCommon.setData(settings, buff, DataType.UINT16,
                        new Integer(it.getValue().getDataIndex()));
            }
            return buff.array();
        }
        if (e.getIndex() == 3) {
            buff.setUInt8(DataType.STRUCTURE.getValue());
            buff.setUInt8(3);
            GXCommon.setData(settings, buff, DataType.ENUM, new Integer(
                    sendDestinationAndMethod.getService().getValue()));
            if (sendDestinationAndMethod.getDestination() != null) {
                GXCommon.setData(settings, buff, DataType.OCTET_STRING,
                        sendDestinationAndMethod.getDestination().getBytes());
            } else {
                GXCommon.setData(settings, buff, DataType.OCTET_STRING, null);
            }
            GXCommon.setData(settings, buff, DataType.ENUM,
                    sendDestinationAndMethod.getMessage().getValue());
            return buff.array();
        }
        if (e.getIndex() == 4) {
            buff.setUInt8(DataType.ARRAY.getValue());
            GXCommon.setObjectCount(communicationWindow.size(), buff);
            for (Entry<GXDateTime, GXDateTime> it : communicationWindow) {
                buff.setUInt8(DataType.STRUCTURE.getValue());
                buff.setUInt8(2);
                GXCommon.setData(settings, buff, DataType.OCTET_STRING,
                        it.getKey());
                GXCommon.setData(settings, buff, DataType.OCTET_STRING,
                        it.getValue());
            }
            return buff.array();
        }
        if (e.getIndex() == 5) {
            return new Integer(randomisationStartInterval);
        }
        if (e.getIndex() == 6) {
            return new Integer(numberOfRetries);
        }
        if (e.getIndex() == 7) {
            return new Integer(repetitionDelay);
        }
        e.setError(ErrorCode.READ_WRITE_DENIED);
        return null;
    }

    /*
     * Set value of given attribute.
     */
    @Override
    public final void setValue(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            setLogicalName(GXCommon.toLogicalName(e.getValue()));
        } else if (e.getIndex() == 2) {
            pushObjectList.clear();
            Entry<GXDLMSObject, GXDLMSCaptureObject> ent;
            if (e.getValue() instanceof Object[]) {
                for (Object it : (Object[]) e.getValue()) {
                    Object[] tmp = (Object[]) it;
                    ObjectType type =
                            ObjectType.forValue(((Number) tmp[0]).intValue());
                    String ln = GXCommon.toLogicalName(tmp[1]);
                    GXDLMSObject obj = settings.getObjects().findByLN(type, ln);
                    if (obj == null) {
                        obj =GXDLMSClient.createObject(type);
                        obj.setLogicalName(ln);
                    }
                    GXDLMSCaptureObject co = new GXDLMSCaptureObject();
                    co.setAttributeIndex(((Number) tmp[2]).intValue());
                    co.setDataIndex(((Number) tmp[3]).intValue());
                    ent = new GXSimpleEntry<GXDLMSObject, GXDLMSCaptureObject>(
                            obj, co);
                    pushObjectList.add(ent);
                }
            }
        } else if (e.getIndex() == 3) {
            Object[] tmp = (Object[]) e.getValue();
            if (tmp != null) {
                sendDestinationAndMethod.setService(
                        ServiceType.forValue(((Number) tmp[0]).intValue()));
                sendDestinationAndMethod
                        .setDestination(new String((byte[]) tmp[1]));
                sendDestinationAndMethod.setMessage(
                        MessageType.forValue(((Number) tmp[2]).intValue()));
            }
        } else if (e.getIndex() == 4) {
            communicationWindow.clear();
            if (e.getValue() instanceof Object[]) {
                for (Object it : (Object[]) e.getValue()) {
                    Object[] tmp = (Object[]) it;
                    GXDateTime start = (GXDateTime) GXDLMSClient
                            .changeType((byte[]) tmp[0], DataType.DATETIME);
                    GXDateTime end = (GXDateTime) GXDLMSClient
                            .changeType((byte[]) tmp[1], DataType.DATETIME);
                    communicationWindow.add(
                            new GXSimpleEntry<GXDateTime, GXDateTime>(start,
                                    end));
                }
            }
        } else if (e.getIndex() == 5) {
            randomisationStartInterval = ((Number) e.getValue()).intValue();
        } else if (e.getIndex() == 6) {
            numberOfRetries = ((Number) e.getValue()).intValue();
        } else if (e.getIndex() == 7) {
            repetitionDelay = ((Number) e.getValue()).intValue();
        } else {
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
    }


    public final void load(final GXXmlReader reader)  {
        pushObjectList.clear();
        /*if (reader.isStartElement("ObjectList", true)) {
            while (reader.isStartElement("Item", true)) {
                ObjectType ot = ObjectType
                        .forValue(reader.readElementContentAsInt("ObjectType"));
                String ln = reader.readElementContentAsString("LN");
                int ai = reader.readElementContentAsInt("AI");
                int di = reader.readElementContentAsInt("DI");
                reader.readEndElement("ObjectList");
                GXDLMSCaptureObject co = new GXDLMSCaptureObject(ai, di);
                GXDLMSObject obj = reader.getObjects().findByLN(ot, ln);
                pushObjectList.add(
                        new GXSimpleEntry<GXDLMSObject, GXDLMSCaptureObject>(
                                obj, co));
            }
            reader.readEndElement("ObjectList");
        }

        service =
                ServiceType.forValue(reader.readElementContentAsInt("Service"));
        destination = reader.readElementContentAsString("Destination");
        message =
                MessageType.forValue(reader.readElementContentAsInt("Message"));
        communicationWindow.clear();
        if (reader.isStartElement("CommunicationWindow", true)) {
            while (reader.isStartElement("Item", true)) {
                GXDateTime start = new GXDateTime(
                        reader.readElementContentAsString("Start"));
                GXDateTime end = new GXDateTime(
                        reader.readElementContentAsString("End"));
                communicationWindow.add(
                        new GXSimpleEntry<GXDateTime, GXDateTime>(start, end));
            }
            reader.readEndElement("CommunicationWindow");
        }
        randomisationStartInterval =
                reader.readElementContentAsInt("RandomisationStartInterval");
        numberOfRetries = reader.readElementContentAsInt("NumberOfRetries");
        repetitionDelay = reader.readElementContentAsInt("RepetitionDelay");*/
    }


    public final void save(final GXXmlWriter writer)  {
        /*if (pushObjectList != null) {
            writer.writeStartElement("ObjectList");
            for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : pushObjectList) {
                writer.writeStartElement("Item");
                writer.writeElementString("ObjectType",
                        it.getKey().getObjectType().getValue());
                writer.writeElementString("LN", it.getKey().getLogicalName());
                writer.writeElementString("AI",
                        it.getValue().getAttributeIndex());
                writer.writeElementString("DI", it.getValue().getDataIndex());
                writer.writeEndElement();
            }
            writer.writeEndElement();*/
       /* }
        if (service != null) {
            writer.writeElementString("Service", service.getValue());
        }
        writer.writeElementString("Destination", destination);
        if (message != null) {
            writer.writeElementString("Message", message.getValue());
        }
        if (communicationWindow != null) {
            writer.writeStartElement("CommunicationWindow");
            for (Entry<GXDateTime, GXDateTime> it : communicationWindow) {
                writer.writeStartElement("Item");
                writer.writeElementString("Start",
                        it.getKey().toFormatString());
                writer.writeElementString("End",
                        it.getValue().toFormatString());
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        writer.writeElementString("RandomisationStartInterval",
                randomisationStartInterval);
        writer.writeElementString("NumberOfRetries", numberOfRetries);
        writer.writeElementString("RepetitionDelay", repetitionDelay);*/
    }

    @Override
    public final void postLoad(final GXXmlReader reader) {
    }
}
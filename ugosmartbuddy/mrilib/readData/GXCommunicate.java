package com.inventive.ugosmartbuddy.mrilib.readData;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.inventive.ugosmartbuddy.mrilib.models.Devices;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.IGXMedia;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.common.ReceiveParameters;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GXCommunicate {

    private static final String LOG = GXCommunicate.class.getSimpleName();
    public IGXMedia media;
    public boolean Trace = false;
    public GXDLMSSecureClient dlms;
    boolean iec;
    int WaitTime = 10000;
    GXManufacturer manufacturer;
    java.nio.ByteBuffer replyBuff;

    public GXCommunicate(int waitTime, GXDLMSSecureClient dlms,
                         GXManufacturer manufacturer, boolean iec, Authentication auth,
                         String pw, IGXMedia media) throws Exception {
        this.media = media;
        WaitTime = waitTime;
        this.dlms = dlms;
        this.manufacturer = manufacturer;
        this.iec = iec;

        boolean useIec47 =
                manufacturer.getUseIEC47() && media instanceof GXSerial;
        dlms.setUseLogicalNameReferencing(
                manufacturer.getUseLogicalNameReferencing());
        int value = manufacturer.getAuthentication(auth).getClientAddress();
        System.out.println("Association:" + auth.getValue());
        /*if(auth.getValue() == 2)
        {
        	dlms.setClientAddress(80);
        }
        else
        {
        	dlms.setClientAddress(value);
        }*/
        useIec47 = false; // enable for wrapper Nitin
        dlms.setClientAddress(value);
        GXServerAddress serv = manufacturer.getServer(HDLCAddressType.DEFAULT);
        //Added by Nitin For HDLC

        if (useIec47) {
            dlms.setInterfaceType(InterfaceType.WRAPPER);
            value = serv.getPhysicalAddress();
        } else {
            dlms.setInterfaceType(InterfaceType.HDLC);
            value = GXDLMSClient.getServerAddress(serv.getLogicalAddress(),
                    serv.getPhysicalAddress());
        }
        dlms.setServerAddress(value);
        dlms.setAuthentication(auth);
        dlms.setPassword(pw.getBytes("ASCII"));
//        TextFileHandler.loggedData("Authentication: " + auth);
//        TextFileHandler.loggedData("ClientAddress: 0x" + Integer.toHexString(dlms.getClientAddress()));
//        TextFileHandler.loggedData("ServerAddress: 0x"+ Integer.toHexString(dlms.getServerAddress()));

        //dlms.setServerAddress(22);
        //System.out.println(dlms.getServerAddress());

        if (dlms.getInterfaceType() == InterfaceType.WRAPPER) {
            replyBuff = java.nio.ByteBuffer.allocate(8 + 1024);
        } else {
            replyBuff = java.nio.ByteBuffer.allocate(100);
        }
    }

    /**
     * Get manufacturer settings from Gurux web service if not installed yet.
     * This is something that you do not necessary seed. You can // hard code
     * the settings. This is only for demonstration. Use hard coded settings
     * like this:
     * <p/>
     * GXDLMSClient cl = new GXDLMSClient(true, 16, 1, Authentication.NONE,
     * null, InterfaceType.HDLC);
     * Command line argumens.
     *
     * @return
     * @throws Exception
     */
    public GXCommunicate getManufactureSettings(GXSerial media, String securityMode, AssetManager assetManager, Context context)
            throws Exception {
        String[] args = new String[3];
        args[0] = "/m=agt";
        args[1] = "/s=DLMS";
        args[2] = securityMode;

        //String path = TextFileHandler.getManufacturerFilePath();

        ////////////////////////////////////////
        // Initialize connection settings.
        if (media instanceof IGXMedia) {
            this.media = media;
        }

        // 4059 is Official DLMS port.
        String id = "", pw = "";
        boolean trace = false, iec = true;
        Authentication auth = Authentication.NONE;
        //int startBaudRate = 9600;
        //String number = null;
        for (String it : args) {
            String item = it.trim();
            if (item.compareToIgnoreCase("/u") == 0) {
                // Update
                // Get latest manufacturer settings from Gurux web server.
                GXManufacturerCollection.updateManufactureSettings(context);
            } else if (item.startsWith("/m=")) {
                // Manufacturer
                id = item.replaceFirst("/m=", "");
            } else if (item.startsWith("/h=")) {
                // Host
                //host = item.replaceFirst("/h=", "");
            } else if (item.startsWith("/p=")) {
                // TCP/IP Port
                media = new GXSerial(context);
                //port = item.replaceFirst("/p=", "");
            } else if (item.startsWith("/b=")) {
                // Baud rate
                //startBaudRate = Integer.parseInt(item.replaceFirst("/b=", ""));
            } else if (item.startsWith("/t")) {
                // Are messages traced.
                trace = true;
            } else if (item.startsWith("/s=")) {
                // Start
                String tmp = item.replaceFirst("/s=", "");
                iec = !tmp.toLowerCase().equals("dlms");
            } else if (item.startsWith("/a=")) {
                // Authentication
                auth = Authentication.valueOf(
                        it.trim().replaceFirst("/a=", "").toUpperCase());
            } else if (item.startsWith("/pw=")) {
                // Password
                pw = it.trim().replaceFirst("/pw=", "");
            }
        }

        GXDLMSSecureClient dlms = new GXDLMSSecureClient();
        GXManufacturerCollection items = new GXManufacturerCollection();
        GXManufacturerCollection.readManufacturerSettings(items, "ManufacturerSettings", assetManager);
        GXManufacturer man = items.findByIdentification(id);
        if (man == null) {
            throw new RuntimeException("Invalid manufacturer.");
        }
        dlms.setObisCodes(man.getObisCodes());
        GXCommunicate com = new GXCommunicate(3000, dlms, man, iec, auth, pw, (GXSerial) media);
        com.Trace = trace;

        dlms.setUseLogicalNameReferencing(true);

        return com;
    }

    public GXCommunicate() {
    }

    public void close() throws ConnectionBrokenException, Exception {
        if (media != null) {
            //TextFileHandler.loggedData("DisconnectRequest");
            GXReplyData reply = new GXReplyData();
            readDLMSPacket(dlms.disconnectRequest(), reply);
            media.close(); // Nitin need to close media in Server mode
        }
    }

    public void readDLMSPacket(byte[][] data) throws ConnectionBrokenException, Exception {
        GXReplyData reply = new GXReplyData();
        for (byte[] it : data) {
            reply.clear();
            readDLMSPacket(it, reply);
        }
    }

    /**
     * Read DLMS Data from the device. If access is denied return null.
     */
//    public void readDLMSPacket(byte[] data, GXReplyData reply)
//            throws ConnectionBrokenException,Exception {
//        if (data == null || data.length == 0) {
//            return;
//        }
//		IGXMedia igxMedia = media;
//        reply.setError((short) 0);
//        Object eop = (byte) 0x7E;
//        // In network connection terminator is not used.
//        if (dlms.getInterfaceType() == InterfaceType.WRAPPER
//                && media instanceof GXSerial) {
//            eop = null;
//        }
//        Integer pos = 0;
//        boolean succeeded = false;
//        ReceiveParameters<byte[]> p =
//                new ReceiveParameters<byte[]>(byte[].class);
//        p.setEop(eop);
//        p.setCount(5);
//        p.setWaitTime(WaitTime);
//        synchronized (media.getSynchronous()) {
//            while (!succeeded) {
//                //writeTrace("<- " + now() + "\t" + GXCommon.bytesToHex(data));
//				Log.d("TAG","TX: " + "\t" + GXCommon.bytesToHex(data));
//				media.send(data, null);
//                if (p.getEop() == null) {
//                    p.setCount(1);
//                }
//                succeeded = media.receive(p);
//                if (!succeeded) {
//                	throw new ConnectionBrokenException();
//                }
//            }
//            // Loop until whole DLMS packet is received.
//            try {
//                while (!dlms.getData(p.getReply(), reply)) {
//                    if (p.getEop() == null) {
//                        p.setCount(1);
//                    }
//                    if (!media.receive(p)) {
//                        // If echo.
//                        if (reply.isEcho()) {
//                            media.send(data, null);
//                        }
//                        // Try to read again...
//                        if (++pos == 3) {
//                            throw new Exception(
//                                    "Failed to receive reply from the device in given time.");
//                        }
//                        //TextFileHandler.loggedData("Data send failed. Try to resend " + pos.toString() + "/3");
//                    }
//                }
//            } catch (Exception e) {
//                /*writeTrace("-> " + now() + "\t"
//                        + GXCommon.bytesToHex(p.getReply()));*/
//                throw e;
//            }
//        }
//        //writeTrace("-> " + now() + "\t" + GXCommon.bytesToHex(p.getReply()));
//		Log.d("TAG","RX: " + "\t" + GXCommon.bytesToHex(p.getReply()));
//        if (reply.getError() != 0) {
//            if (reply.getError() == ErrorCode.REJECTED.getValue()) {
//            	//TextFileHandler.loggedData("1000 ms sleep........");
//            	System.out.println("1000 ms sleep........");
//                Thread.sleep(1000);
//                try{
//                	readDLMSPacket(data, reply);
//                }catch(ConnectionBrokenException connectionBrokenException)
//                {
//                	throw connectionBrokenException;
//                }
//            } else {
//            	//Access Error : Device reports Read-Write denied.
//            	//TextFileHandler.loggedData(reply.getErrorMessage());
//                //throw new GXDLMSException(reply.getError());
//            }
//        }
//    }
    public void readDLMSPacket(byte[] data, GXReplyData reply)
            throws Exception {
        if (data == null || data.length == 0) {
            return;
        }
        reply.setError((short) 0);
        Object eop = (byte) 0x7E;
        Integer pos = 0;
        boolean succeeded = false;
        ReceiveParameters<byte[]> p =
                new ReceiveParameters<byte[]>(byte[].class);
        p.setEop(eop);
        p.setCount(5);
        p.setWaitTime(WaitTime);
        synchronized (media.getSynchronous()) {
            while (!succeeded) {
                Log.d("TAG", "TX: " + "\t" + GXCommon.bytesToHex(data));
                media.send(data, null);
                if (p.getEop() == null) {
                    p.setCount(1);
                }
                succeeded = media.receive(p);
                if (!succeeded) {
                    // Try to read again...
                    if (pos++ == 3) {
                        throw new RuntimeException(
                                "Failed to receive reply from the device in given time.");
                    }
                    System.out.println("Data send failed. Try to resend "
                            + pos.toString() + "/3");
                }
            }
            // Loop until whole DLMS packet is received.
            try {
                while (!dlms.getData(p.getReply(), reply)) {
                    if (p.getEop() == null) {
                        p.setCount(1);
                    }
                    if (!media.receive(p)) {
                        // If echo.
                        if (reply.isEcho()) {
                            media.send(data, null);
                        }
                        // Try to read again...
                        if (++pos == 3) {
                            throw new Exception(
                                    "Failed to receive reply from the device in given time.");
                        }
                        System.out.println("Data send failed. Try to resend "
                                + pos.toString() + "/3");
                    }
                }
            } catch (Exception e) {
                Log.d("TAG", "RX: " + "\t" + GXCommon.bytesToHex(p.getReply()));
                throw e;
            }
        }
        Log.d("TAG", "RX: " + "\t" + GXCommon.bytesToHex(p.getReply()));
        if (reply.getError() != 0) {
            if (reply.getError() == ErrorCode.REJECTED.getValue()) {
                Thread.sleep(1000);
                readDLMSPacket(data, reply);
            } else {
                throw new GXDLMSException(reply.getError());
            }
        }
    }

    public void readDataBlock(byte[][] data, GXReplyData reply) throws ConnectionBrokenException, Exception {
        for (byte[] it : data) {
            reply.clear();
            try {
                readDataBlock(it, reply);
            } catch (ConnectionBrokenException connectionBrokenException) {
                throw connectionBrokenException;
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
    public void readDataBlock(byte[] data, GXReplyData reply) throws ConnectionBrokenException, Exception {
        try {
            RequestTypes rt;
            if (data.length != 0) {
                Log.d("TAG", "Reading Data Tx: " + GXCommon.bytesToHex(data).replaceAll("\\s", ""));
                readDLMSPacket(data, reply);
                while (reply.isMoreData()) {
                    rt = reply.getMoreData();
                    Log.d("TAG", "Reading Data Block....");
                    data = dlms.receiverReady(rt);
                    Log.d("TAG", "Rx: " + rt + " --- " + GXCommon.bytesToHex(data).replaceAll("\\s", ""));
                    readDLMSPacket(data, reply);
                }
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean initializeConnection(String initialisationMode, Devices devices) throws ConnectionBrokenException,
            Exception, InterruptedException {
        boolean resFlag = false;
        try {
            if (initialisationMode.equals(Constants.InitializationMode.NONE)) {
                dlms.setAuthentication(Authentication.NONE);
                dlms.getCiphering().setSecurity(Security.NONE);
            } else if (initialisationMode.equals(Constants.InitializationMode.HIGH)) {
                long value = System.currentTimeMillis();
                long onejanmili = 1577817000000L;
                value = value - onejanmili;
                value = value / 1000;
                if (devices.getManufacturer().equals("Allied")) {
                    dlms.getCiphering().setInvocationCounter(value);
                } else {
                    dlms.getCiphering().setInvocationCounter(0xFFFFFFFF);
                }

                dlms.setAuthentication(Authentication.HIGH);

                if (ObisCodeConstants.MasterObisCodes.IMAGE_TRANSFER.equals(devices.getObisCode())) {
                    dlms.setClientAddress(80);
                    dlms.setPassword(devices.getFirwarePwd().getBytes("ASCII"));
                } else {
                    dlms.setClientAddress(48);
                    dlms.setPassword(devices.getHighPwd().getBytes("ASCII"));
                }
                dlms.getCiphering().setAuthenticationKey(devices.getAuthKey().getBytes("ASCII"));
                dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));

                if (devices.getCipheringMode().equalsIgnoreCase("YES")) {
                    dlms.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));
                    dlms.getCiphering().setBlockCipherKey(devices.getCipheringKey().getBytes("ASCII"));
                    dlms.setInterfaceType(InterfaceType.HDLC);
                    dlms.setServerAddress(1);
                    dlms.setUseLogicalNameReferencing(true);
                    dlms.setAutoIncreaseInvokeID(true);
                    dlms.setStandard(Standard.DLMS);
                    dlms.setUseUtc2NormalTime(true);
                } else {
                    dlms.getCiphering().setSecurity(Security.NONE);
                }
            } else if (initialisationMode.equals(Constants.InitializationMode.LOW)) {
                long currentTimeInMillsec = System.currentTimeMillis();
                long randomMillisec = 1577817000000L;
                currentTimeInMillsec = currentTimeInMillsec - randomMillisec;
                currentTimeInMillsec = currentTimeInMillsec / 1000;

                dlms.getCiphering().setInvocationCounter(currentTimeInMillsec);
                dlms.setAuthentication(Authentication.LOW);
                dlms.setPassword(devices.getLowPwd().getBytes("ASCII"));
                dlms.getCiphering().setAuthenticationKey(devices.getAuthKey().getBytes("ASCII"));
                dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));

                if (devices.getCipheringMode().equalsIgnoreCase("YES")) {
                    if (devices.getManufacturer().equalsIgnoreCase(Constants.ManufacturerCodes.HEXING_MANUFACTURER)
                            || devices.getManufacturer().equalsIgnoreCase(Constants.ManufacturerCodes.ZEN))//Change Nitin for ZEN/Hexing Meter
                    {
                        dlms.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    } else {
                        dlms.getCiphering().setSecurity(Security.ENCRYPTION);
                    }
                    dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));
                    dlms.getCiphering().setBlockCipherKey(devices.getCipheringKey().getBytes("ASCII"));
                } else {
                    dlms.getCiphering().setSecurity(Security.NONE);
                }
            }

            resFlag = true;

        } catch (Exception e) {
            e.printStackTrace();
            resFlag = false;
        }
        System.out.println(devices.getSystemTitle());

        GXReplyData reply = new GXReplyData();
        try {
            byte[] data = dlms.snrmRequest();

            Log.d("TAG", "Sending SNMR Request for  --> ");
            if (data.length != 0) {
                readDLMSPacket(data, reply);

                Log.d("TAG", "SNMR Success  --> ");
                //TextFileHandler.loggedData("Parsing SNMR Response for ---> "+ dlmsMeterGenricInfoBean.getSystemTitle()+" :"+ GXCommon.bytesToHex(reply.getData().array()));
                //TextFileHandler.loggedData("SNMR Reply Error of" + dlmsMeterGenricInfoBean.getSystemTitle()+": "+reply.getError());
                dlms.parseUAResponse(reply.getData());
                // Allocate buffer to same size as transmit buffer of the meter.
                // Size of replyBuff is payload and frame (Bop, EOP, crc).
                int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                        .intValue() & 0xFFFFFFFFL) + 40);
                replyBuff = java.nio.ByteBuffer.allocate(size);
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        reply.clear();
        try {
            // Generate AARQ request.
            // Split requests to multiple packets if needed.
            // If password is used all data might not fit to one packet.
            for (byte[] it : dlms.aarqRequest()) {
                Log.d("TAG", "Sending AARQ Request for  --> ");

                //TextFileHandler.loggedData("Sending AARQ Request for......" + dlmsMeterGenricInfoBean.getSystemTitle() );
                try {
                    //TextFileHandler.loggedData(GXCommon.bytesToHex(reply.getData().array()));
                    readDLMSPacket(it, reply);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw exception;
                }
            }
            try {
                // Parse reply.
                dlms.parseAareResponse(reply.getData());
                Log.d("TAG", "Parsing AARE Response For--->" + GXCommon.bytesToHex(reply.getData().array()));
                //TextFileHandler.loggedData("AARE Reply Error of" + dlmsMeterGenricInfoBean.getSystemTitle()+": "+reply.getError());
                Log.d("TAG", " Parsing AARE reply succeeded.  --> ");
                reply.clear();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Get challenge Is HLS authentication is used.
            if (dlms.getIsAuthenticationRequired()) {
                //byte a[] = dlms.getApplicationAssociationRequest()[0];
                Log.d("TAG", "Parsing HLS authentication For: --->");
                for (byte[] it : dlms.getApplicationAssociationRequest()) {
//				 Log.d("TAG" , "readDLMSPacket ki It value : - " + it);
                    readDLMSPacket(it, reply);
                }
                dlms.parseApplicationAssociationResponse(reply.getData());
                Log.d("TAG", "Parsing HLS authentication For Rx: --->" + GXCommon.bytesToHex(reply.getData().array()));
            }
            resFlag = true;
            //flag = true;
        } catch (ConnectionBrokenException e) {
            resFlag = false;
            throw e;
        }


        return resFlag;
    }


    public boolean initializeConnection(String initialisationMode, Devices devices, boolean reConnect) throws ConnectionBrokenException,
            Exception, InterruptedException {
        boolean resFlag = false;
        try {
            if (initialisationMode.equals(Constants.InitializationMode.NONE)) {
                dlms.setAuthentication(Authentication.NONE);
                dlms.getCiphering().setSecurity(Security.NONE);
            }
            else if (initialisationMode.equals(Constants.InitializationMode.HIGH)) {
                //Thread.sleep(2000);
                long value = System.currentTimeMillis();
                long onejanmili = 1577817000000L;
                value = value - onejanmili;
                value = value / 1000;

                if (devices.getManufacturer().equals("Allied")) {
                    dlms.getCiphering().setInvocationCounter(value);
                } else {
                    dlms.getCiphering().setInvocationCounter(0xFFFFFFFF);
                }

                // dlms.getCiphering().setInvocationCounter(0xFFFFFFFF);
                dlms.setAuthentication(Authentication.HIGH);

                if (ObisCodeConstants.MasterObisCodes.IMAGE_TRANSFER.equals(devices.getObisCode())) {
                    dlms.setClientAddress(80);
                    dlms.setPassword(devices.getFirwarePwd().getBytes("ASCII"));
                } else {
                    dlms.setClientAddress(48);
                    dlms.setPassword(devices.getHighPwd().getBytes("ASCII"));
                }
                dlms.getCiphering().setAuthenticationKey(devices.getAuthKey().getBytes("ASCII"));
                dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));

                if (devices.getCipheringMode().equalsIgnoreCase("YES")) {
                    dlms.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));
                    dlms.getCiphering().setBlockCipherKey(devices.getCipheringKey().getBytes("ASCII"));
                    dlms.setInterfaceType(InterfaceType.HDLC);
                    dlms.setServerAddress(1);
                    dlms.setUseLogicalNameReferencing(true);
                    dlms.setAutoIncreaseInvokeID(true);
                    dlms.setStandard(Standard.DLMS);
                    dlms.setUseUtc2NormalTime(true);
                } else {
                    dlms.getCiphering().setSecurity(Security.NONE);
                }
            } else if (initialisationMode.equals(Constants.InitializationMode.LOW)) {
                long currentTimeInMillsec = System.currentTimeMillis();
                long randomMillisec = 1577817000000L;
                currentTimeInMillsec = currentTimeInMillsec - randomMillisec;
                currentTimeInMillsec = currentTimeInMillsec / 1000;

                dlms.getCiphering().setInvocationCounter(currentTimeInMillsec);
                dlms.setAuthentication(Authentication.LOW);
                dlms.setPassword(devices.getLowPwd().getBytes("ASCII"));
                dlms.getCiphering().setAuthenticationKey(devices.getAuthKey().getBytes("ASCII"));
                dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));

                if (devices.getCipheringMode().equalsIgnoreCase("YES")) {
                    if (devices.getManufacturer().equalsIgnoreCase(Constants.ManufacturerCodes.HEXING_MANUFACTURER)
                            || devices.getManufacturer().equalsIgnoreCase(Constants.ManufacturerCodes.ZEN))//Change Nitin for ZEN/Hexing Meter
                    {
                        dlms.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    } else {
                        dlms.getCiphering().setSecurity(Security.ENCRYPTION);
                    }
                    dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));
                    dlms.getCiphering().setBlockCipherKey(devices.getCipheringKey().getBytes("ASCII"));
                } else {
                    dlms.getCiphering().setSecurity(Security.NONE);
                }
            }

            resFlag = true;

        } catch (Exception e) {
            e.printStackTrace();
            resFlag = false;
        }
        System.out.println(devices.getSystemTitle());

        GXReplyData reply = new GXReplyData();
        try {
            byte[] data = dlms.snrmRequest();

            Log.d("TAG", "Sending SNMR Request for  --> ");
            if (data.length != 0) {
                readDLMSPacket(data, reply);

                Log.d("TAG", "SNMR Success  --> ");
                //TextFileHandler.loggedData("Parsing SNMR Response for ---> "+ dlmsMeterGenricInfoBean.getSystemTitle()+" :"+ GXCommon.bytesToHex(reply.getData().array()));
                //TextFileHandler.loggedData("SNMR Reply Error of" + dlmsMeterGenricInfoBean.getSystemTitle()+": "+reply.getError());
                dlms.parseUAResponse(reply.getData());
                // Allocate buffer to same size as transmit buffer of the meter.
                // Size of replyBuff is payload and frame (Bop, EOP, crc).
                int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                        .intValue() & 0xFFFFFFFFL) + 40);
                replyBuff = java.nio.ByteBuffer.allocate(size);
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        reply.clear();
        try {
            // Generate AARQ request.
            // Split requests to multiple packets if needed.
            // If password is used all data might not fit to one packet.
            for (byte[] it : dlms.aarqRequest()) {
                Log.d("TAG", "Sending AARQ Request for  --> ");

                //TextFileHandler.loggedData("Sending AARQ Request for......" + dlmsMeterGenricInfoBean.getSystemTitle() );
                try {
                    //TextFileHandler.loggedData(GXCommon.bytesToHex(reply.getData().array()));
                    readDLMSPacket(it, reply);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw exception;
                }
            }
            try {
                // Parse reply.
                dlms.parseAareResponse(reply.getData());
                Log.d("TAG", "Parsing AARE Response For--->" + GXCommon.bytesToHex(reply.getData().array()));
                //TextFileHandler.loggedData("AARE Reply Error of" + dlmsMeterGenricInfoBean.getSystemTitle()+": "+reply.getError());
                Log.d("TAG", " Parsing AARE reply succeeded.  --> ");
                reply.clear();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Get challenge Is HLS authentication is used.
            if (dlms.getIsAuthenticationRequired()) {
                //byte a[] = dlms.getApplicationAssociationRequest()[0];
                Log.d("TAG", "Parsing HLS authentication For: --->");
                for (byte[] it : dlms.getApplicationAssociationRequest()) {
//				 Log.d("TAG" , "readDLMSPacket ki It value : - " + it);
                    readDLMSPacket(it, reply);
                }
                dlms.parseApplicationAssociationResponse(reply.getData());
                Log.d("TAG", "Parsing HLS authentication For Rx: --->" + GXCommon.bytesToHex(reply.getData().array()));
            }
            //GXDLMSDisconnectControl dc = new GXDLMSDisconnectControl("1.0.96.3.10.255", 1);

            GXDLMSDisconnectControl dc = new GXDLMSDisconnectControl("0.0.96.3.10.255");
//			byte[][] data = dlms.read(dc.getLogicalName(), ObjectType.DISCONNECT_CONTROL, 4);
//			com.inventive.mrilib.serial_communication.common.GXCommon.bytesToHex(data[0]);
            if (reConnect && dc.getControlState() != null && dc.getControlState().name().equals("DISCONNECTED")) {
                readDataBlock(dc.remoteReconnect(dlms), reply);
                return true;
            } else {
                readDataBlock(dc.remoteDisconnect(dlms), reply);
                return false;
            }
            // resFlag = true;
            //flag = true;

        } catch (ConnectionBrokenException e) {
            resFlag = false;
            throw e;
        }
        //	return resFlag;
    }



    public boolean initializeFirmwareConnection(String initialisationMode, Devices devices) throws ConnectionBrokenException,
            Exception, InterruptedException {
        boolean resFlag = false;
        try {
            if (initialisationMode.equals(Constants.InitializationMode.NONE)) {
                dlms.setAuthentication(Authentication.NONE);
                dlms.getCiphering().setSecurity(Security.NONE);
            } else if (initialisationMode.equals(Constants.InitializationMode.HIGH)) {
                long value = System.currentTimeMillis();
                long onejanmili = 1577817000000L;
                value = value - onejanmili;
                value = value / 1000;
                if (devices.getManufacturer().equals("Allied")) {
                    dlms.getCiphering().setInvocationCounter(value);
                } else {
                    dlms.getCiphering().setInvocationCounter(0xFFFFFFFF);
                }

                dlms.setAuthentication(Authentication.HIGH);

                if (ObisCodeConstants.MasterObisCodes.IMAGE_TRANSFER.equals(devices.getObisCode())) {
                    dlms.setClientAddress(80);
                    dlms.setPassword(devices.getFirwarePwd().getBytes("ASCII"));
                } else {
                    dlms.setClientAddress(48);
                    dlms.setPassword(devices.getHighPwd().getBytes("ASCII"));
                }
                dlms.getCiphering().setAuthenticationKey(devices.getAuthKey().getBytes("ASCII"));
                dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));

                if (devices.getCipheringMode().equalsIgnoreCase("YES")) {
                    dlms.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));
                    dlms.getCiphering().setBlockCipherKey(devices.getCipheringKey().getBytes("ASCII"));
                    dlms.setInterfaceType(InterfaceType.HDLC);
                    dlms.setServerAddress(1);
                    dlms.setUseLogicalNameReferencing(true);
                    dlms.setAutoIncreaseInvokeID(true);
                    dlms.setStandard(Standard.DLMS);
                    dlms.setUseUtc2NormalTime(true);
                } else {
                    dlms.getCiphering().setSecurity(Security.NONE);
                }
            } else if (initialisationMode.equals(Constants.InitializationMode.LOW)) {
                long currentTimeInMillsec = System.currentTimeMillis();
                long randomMillisec = 1577817000000L;
                currentTimeInMillsec = currentTimeInMillsec - randomMillisec;
                currentTimeInMillsec = currentTimeInMillsec / 1000;

                dlms.getCiphering().setInvocationCounter(currentTimeInMillsec);
                dlms.setAuthentication(Authentication.LOW);
                dlms.setPassword(devices.getLowPwd().getBytes("ASCII"));
                dlms.getCiphering().setAuthenticationKey(devices.getAuthKey().getBytes("ASCII"));
                dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));

                if (devices.getCipheringMode().equalsIgnoreCase("YES")) {
                    if (devices.getManufacturer().equalsIgnoreCase(Constants.ManufacturerCodes.HEXING_MANUFACTURER)
                            || devices.getManufacturer().equalsIgnoreCase(Constants.ManufacturerCodes.ZEN))//Change Nitin for ZEN/Hexing Meter
                    {
                        dlms.getCiphering().setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    } else {
                        dlms.getCiphering().setSecurity(Security.ENCRYPTION);
                    }
                    dlms.getCiphering().setSystemTitle(devices.getSystemTitle().getBytes("ASCII"));
                    dlms.getCiphering().setBlockCipherKey(devices.getCipheringKey().getBytes("ASCII"));
                } else {
                    dlms.getCiphering().setSecurity(Security.NONE);
                }
            }

            resFlag = true;

        } catch (Exception e) {
            e.printStackTrace();
            resFlag = false;
        }
        System.out.println(devices.getSystemTitle());

        GXReplyData reply = new GXReplyData();
        try {
            byte[] data = dlms.snrmRequest();

            Log.d("TAG", "Sending SNMR Request for  --> ");
            if (data.length != 0) {
                readDLMSPacket(data, reply);

                Log.d("TAG", "SNMR Success  --> ");
                //TextFileHandler.loggedData("Parsing SNMR Response for ---> "+ dlmsMeterGenricInfoBean.getSystemTitle()+" :"+ GXCommon.bytesToHex(reply.getData().array()));
                //TextFileHandler.loggedData("SNMR Reply Error of" + dlmsMeterGenricInfoBean.getSystemTitle()+": "+reply.getError());
                dlms.parseUAResponse(reply.getData());
                // Allocate buffer to same size as transmit buffer of the meter.
                // Size of replyBuff is payload and frame (Bop, EOP, crc).
                int size = (int) ((((Number) dlms.getLimits().getMaxInfoTX())
                        .intValue() & 0xFFFFFFFFL) + 40);
                replyBuff = java.nio.ByteBuffer.allocate(size);
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        reply.clear();
        try {
            // Generate AARQ request.
            // Split requests to multiple packets if needed.
            // If password is used all data might not fit to one packet.
            for (byte[] it : dlms.aarqRequest()) {
                Log.d("TAG", "Sending AARQ Request for  --> ");

                //TextFileHandler.loggedData("Sending AARQ Request for......" + dlmsMeterGenricInfoBean.getSystemTitle() );
                try {
                    //TextFileHandler.loggedData(GXCommon.bytesToHex(reply.getData().array()));
                    readDLMSPacket(it, reply);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw exception;
                }
            }
            try {
                // Parse reply.
                dlms.parseAareResponse(reply.getData());
                Log.d("TAG", "Parsing AARE Response For--->" + GXCommon.bytesToHex(reply.getData().array()));
                //TextFileHandler.loggedData("AARE Reply Error of" + dlmsMeterGenricInfoBean.getSystemTitle()+": "+reply.getError());
                Log.d("TAG", " Parsing AARE reply succeeded.  --> ");
                reply.clear();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Get challenge Is HLS authentication is used.
            if (dlms.getIsAuthenticationRequired()) {
                //byte a[] = dlms.getApplicationAssociationRequest()[0];
                Log.d("TAG", "Parsing HLS authentication For: --->");
                for (byte[] it : dlms.getApplicationAssociationRequest()) {
//				 Log.d("TAG" , "readDLMSPacket ki It value : - " + it);
                    readDLMSPacket(it, reply);
                }
                dlms.parseApplicationAssociationResponse(reply.getData());
                Log.d("TAG", "Parsing HLS authentication For Rx: --->" + GXCommon.bytesToHex(reply.getData().array()));
            }



            resFlag = true;
            //flag = true;
        } catch (ConnectionBrokenException e) {
            resFlag = false;
            throw e;
        }


        return resFlag;
    }

    /**
     * used to read the dlms values, like meter, system title and invocation counter.
     *
     * @param obiscode
     * @return
     * @throws ConnectionBrokenException
     * @throws Exception
     */
    public Object readDeviceNumber(String obiscode) throws ConnectionBrokenException, Exception {
        //TextFileHandler.loggedData("Reading Devices Data....");

        GXDLMSObjectCollection objects = new GXDLMSObjectCollection();
        GXDLMSData gp = new GXDLMSData(obiscode);
        objects.add(gp);

        // Read all attributes from all objects.
        Object ob = null;
        try {
            ob = readValues(objects);
            if (obiscode.equalsIgnoreCase(ObisCodeConstants.EntityCode.SYSTEM_TITLE)) {
                String sysTitle = ByteConverter.hex2text(ob.toString());
                StringBuilder systemTitle = new StringBuilder();
                systemTitle.append(sysTitle.substring(0, 3));
                systemTitle.append(sysTitle.substring(sysTitle.length() - 5, sysTitle.length()));
                ob = systemTitle.toString();
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return ob;
    }


    public boolean checkStatus() {
        int connected = (int) dlms.settings.getConnected();
        if (connected != 0) {
            Log.d("TAG", "initializeConnection: Connected");
            return true;
        } else {
            Log.d("TAG", "initializeConnection: Disconnected");
            return false;
        }
    }

    /**
     * Reads selected DLMS object with selected attribute index.
     *
     * @param item
     * @param attributeIndex
     * @return
     * @throws Exception
     */
    public Object readObject(GXDLMSObject item, int attributeIndex)
            throws ConnectionBrokenException, Exception {
        GXReplyData reply = null;
        try {
            byte[] data = dlms.read(item.getName(), item.getObjectType(),
                    attributeIndex)[0];
            reply = new GXReplyData();

            readDataBlock(data, reply);
            // Update data type on read.
            if (item.getDataType(attributeIndex) == DataType.NONE) {
                item.setDataType(attributeIndex, reply.getValueType());
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception exception) {
            throw exception;
        }
        return dlms.updateValue(item, attributeIndex, reply.getValue());
    }

    /**
     * This is used to upgrade and activate firmware.
     *
     * @param target
     * @param attributeIndex
     * @param dataLenth
     * @param imageBlockValue
     * @param imageIdentifier
     * @return
     * @throws ConnectionBrokenException
     * @throws Exception
     */
    public Object writeFirmwareObject(GXDLMSImageTransfer target,
                                      int attributeIndex, long dataLenth,
                                      byte[] imageBlockValue, String imageIdentifier)
            throws ConnectionBrokenException, Exception {
        GXReplyData reply = null;
        Object ob = null;
        String imageTransferStatus = Constants.Status.FAILURE;
        try {
            reply = new GXReplyData();

            //Read image transfer status.
            ob = readObject(target, 6);
            if (!ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL.equals(ob)) {
                // transfer all file and verify and activate

                // Step 2: Initiate the Image transfer process.
                readDataBlock(target.imageTransferInitiate(dlms, imageIdentifier, imageBlockValue.length), reply);

                // Step 3: Transfers ImageBlocks.
                int[] imageBlockCount = new int[((int) imageBlockValue.length / (int) dataLenth) + 1];
                byte b[][] = target.imageBlockTransfer(dlms, imageBlockValue, imageBlockCount);

                // step 4: Transfer File
                readDataBlock(b, reply);

                //Verify File
                byte[][] verifyBytes = target.imageVerify(dlms);
                readDataBlock(verifyBytes, reply);

                //Check the Transfer Status of the Image.
                ob = readObject(target, 6);
                Log.d("TAG", "Image Transfer Status After Transferring Image : " + ob);
                if (ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL.equals(ob)) {
                    //Activate Image
                    byte[][] imageActivate = target.imageActivate(dlms);
                    readDataBlock(imageActivate, reply);

                    imageTransferStatus = Constants.Status.SUCCESS;
                    Log.d("TAG", "Image transfer status is " + target.getImageTransferStatus().toString());
                } else {
                    imageTransferStatus = (String) ob.toString();
                }
            } else if (ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL.equals(ob)) {
                // image activate only
                byte[][] imageActivate = target.imageActivate(dlms);
                readDataBlock(imageActivate, reply);

                imageTransferStatus = Constants.Status.SUCCESS;
                Log.d("TAG", "Image transfer status is " + target.getImageTransferStatus().toString());
            }

        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return imageTransferStatus;
    }

    /*
     * /// Read list of attributes.
     */
    public void readList(List<Entry<GXDLMSObject, Integer>> list)
            throws Exception {
        byte[][] data = dlms.readList(list);
        GXReplyData reply = new GXReplyData();
        readDataBlock(data, reply);
        dlms.updateValues(list, Arrays.asList(reply.getValue()));
    }

    /**
     * Writes value to DLMS object with selected attribute index.
     *
     * @param item
     * @param attributeIndex
     * @throws Exception
     */
    public void writeObject(GXDLMSObject item, int attributeIndex)
            throws Exception {
        byte[][] data = dlms.write(item, attributeIndex);
        readDLMSPacket(data);
    }

    /**
     * Read Profile Generic's data by entry start and count.
     *
     * @param pg
     * @param index
     * @param count
     * @return
     * @throws Exception
     */
    public Object[] readRowsByEntry(GXDLMSProfileGeneric pg, int index,
                                    int count) throws Exception {
        byte[][] data = dlms.readRowsByEntry(pg, index, count);
        GXReplyData reply = new GXReplyData();
        readDataBlock(data, reply);
        return (Object[]) dlms.updateValue(pg, 2, reply.getValue());
    }

    /**
     * Read Profile Generic's data by range (start and end time).
     *
     * @param pg
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


    /*
     * Read profile generic columns from the meter.
     */
    void readProfileGenericColumns(final GXDLMSObjectCollection objects,
                                   final PrintWriter logFile) {
        GXDLMSObjectCollection profileGenerics =
                objects.getObjects(ObjectType.PROFILE_GENERIC);
        for (GXDLMSObject it : profileGenerics) {
            //traceLn(logFile, "Profile Generic " + it.getName() + " Columns:");
            GXDLMSProfileGeneric pg = (GXDLMSProfileGeneric) it;
            // Read columns.
            try {
                readObject(pg, 3);
                boolean first = true;
                StringBuilder sb = new StringBuilder();
                for (Entry<GXDLMSObject, GXDLMSCaptureObject> col : pg
                        .getCaptureObjects()) {
                    if (!first) {
                        sb.append(" | ");
                    }
                    sb.append(col.getKey().getName());
                    sb.append(" ");
                    String desc = col.getKey().getDescription();
                    if (desc != null) {
                        sb.append(desc);
                    }
                    first = false;
                }
                //traceLn(logFile, sb.toString());
            } catch (Exception ex) {
                /*traceLn(logFile,
                        "Err! Failed to read columns:" + ex.getMessage());*/
                // Continue reading.
            }
        }
    }

    /**
     * Read all data from the meter except profile generic (Historical) data.
     *
     * @throws ConnectionBrokenException
     */
    public Object readValues(final GXDLMSObjectCollection objects)
            throws ConnectionBrokenException {
        Object val = null;
        for (GXDLMSObject it : objects) {
            if (!(it instanceof IGXDLMSBase)) {
                // If interface is not implemented.
                Log.d("TAG", "Unknown Interface: " + it.getObjectType().toString());
                continue;
            }

            if (it instanceof GXDLMSProfileGeneric) {
                // Profile generic are read later
                // because it might take so long time
                // and this is only a example.
                continue;
            }
            /*traceLn(logFile,
                    "-------- Reading " + it.getClass().getSimpleName() + " "
                            + it.getName().toString() + " "
                            + it.getDescription());*/
            for (int pos : ((IGXDLMSBase) it).getAttributeIndexToRead(true)) {
                try {
                    val = readObject(it, pos);
                    if (val instanceof byte[]) {
                        val = GXCommon.bytesToHex((byte[]) val);
                    } else if (val instanceof Double) {
                        NumberFormat formatter =
                                NumberFormat.getNumberInstance();
                        val = formatter.format(val);
                    } else if (val != null && val.getClass().isArray()) {
                        String str = "";
                        for (int pos2 = 0; pos2 != Array
                                .getLength(val); ++pos2) {
                            if (!str.equals("")) {
                                str += ", ";
                            }
                            Object tmp = Array.get(val, pos2);
                            if (tmp instanceof byte[]) {
                                str += GXCommon.bytesToHex((byte[]) tmp);
                            } else {
                                str += String.valueOf(tmp);
                            }
                        }
                        val = str;
                    }
                    /*traceLn(logFile,
                            "Index: " + pos + " Value: " + String.valueOf(val));*/
                } catch (ConnectionBrokenException e) {
                    throw e;
                } catch (Exception ex) {
                    /*traceLn(logFile,
                            "Error! Index: " + pos + " " + ex.getMessage());*/
                    // Continue reading.
                }
            }
        }
        return val;
    }


    /**
     * Read all data from the meter except profile generic (Historical) data.
     *
     * @throws ConnectionBrokenException
     */
    public Object readBillingScheduleAction(final GXDLMSObjectCollection objects)
            throws ConnectionBrokenException, Exception {
        Object val = null;
        List<GXDateTime> gxDateTimeList = new LinkedList<GXDateTime>();
        for (GXDLMSObject it : objects) {
            if (!(it instanceof IGXDLMSBase)) {
                // If interface is not implemented.
                Log.d("TAG", "Unknown Interface: " + it.getObjectType().toString());
                continue;
            }

            if (it instanceof GXDLMSProfileGeneric) {
                // Profile generic are read later
                // because it might take so long time
                // and this is only a example.
                continue;
            }

            for (int pos : ((IGXDLMSBase) it).getAttributeIndexToRead(true)) {
                try {
                    val = readObject(it, pos);
                    //System.out.println(it.getLogicalName()+":"+val);
                    if (val instanceof byte[]) {
                        val = GXCommon.bytesToHex((byte[]) val);
                    } else if (val instanceof Double) {
                        NumberFormat formatter =
                                NumberFormat.getNumberInstance();
                        val = formatter.format(val);
                    } else if (val != null && val.getClass().isArray()) {
                        String str = "";
                        for (int pos2 = 0; pos2 != Array
                                .getLength(val); ++pos2) {
                            if (!str.equals("")) {
                                str += ", ";
                            }
                            Object tmp = Array.get(val, pos2);
                            if (tmp instanceof byte[]) {
                                str += GXCommon.bytesToHex((byte[]) tmp);
                            } else if (tmp instanceof GXDateTime) {
                                gxDateTimeList.add((GXDateTime) tmp);
                            } else {
                                str += String.valueOf(tmp);
                            }
                        }
                    }
                    /*traceLn(logFile,
                            "Index: " + pos + " Value: " + String.valueOf(val));*/
                } catch (ConnectionBrokenException e) {
                    throw e;
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
        return gxDateTimeList;
    }


    /**
     * Read all data from the meter except profile generic (Historical) data.
     *
     * @throws ConnectionBrokenException
     */
    public Object readActiveCalender(final GXDLMSObjectCollection objects)
            throws ConnectionBrokenException, Exception {
        Object val = null;
        Map<Object, Object> gxObjectList = new LinkedHashMap<Object, Object>();
        //List<GXTime> gxTimeList = new LinkedList<GXTime>();
        for (GXDLMSObject it : objects) {
            for (int pos : ((IGXDLMSBase) it).getAttributeIndexToRead(true)) {
                try {
                    val = readObject(it, pos);
                    //System.out.println(it.getLogicalName()+":"+val);
                    gxObjectList.put(pos, val);
                } catch (ConnectionBrokenException e) {
                    throw e;
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
        return gxObjectList;
    }


    /**
     * Read profile generic (Historical) data.
     */
    public Object[] readRangeWiseEventsData(final GXDLMSObjectCollection objects, int startIndex, int endIndex)
            throws Exception {
        Object[] cells = null;
        GXDLMSObjectCollection profileGenerics =
                objects.getObjects(ObjectType.PROFILE_GENERIC);
        for (GXDLMSObject it : profileGenerics) {

            GXDLMSProfileGeneric pg = (GXDLMSProfileGeneric) it;

            // Read last day.
            try {
                //cells = readRowsByEntry(pg, 2, count);
                cells = readRowsByEntry(pg, startIndex, endIndex);
            } catch (ConnectionBrokenException connectionBrokenException) {
                throw connectionBrokenException;
            } catch (Exception ex) {
                throw ex;
            }
        }
        return cells;
    }


    /**
     * Read profile generic (Historical) data.
     */
    public Object[] readRangeWiseData(final GXDLMSObjectCollection objects, String startDate, String endDate) throws Exception {
        Object[] cells = null;
        GXDLMSObjectCollection profileGenerics =
                objects.getObjects(ObjectType.PROFILE_GENERIC);
        for (GXDLMSObject it : profileGenerics) {

            //long entriesInUse = ((Number) readObject(it, 7)).longValue();
            //long entries = ((Number) readObject(it, 8)).longValue();

            // Read last day.
            try {
                java.util.Calendar start = DateExtractor.convertDateToIstCal(startDate);
                System.out.println("Range start date: " + start.getTime());

                java.util.Calendar end = DateExtractor.convertDateToIstCal(endDate);
                System.out.println("Range end date: " + end.getTime());


                System.out.println("Range date read started.");
                cells = readRowsByRange((GXDLMSProfileGeneric) it,
                        start.getTime(), end.getTime());

                for (Object rows : cells) {
                    for (Object cell : (Object[]) rows) {
                        if (cell instanceof byte[]) {
                            System.out.println(
                                    GXCommon.bytesToHex((byte[]) cell) + " | ");
                        } else {

                        }
                    }
                }
            } catch (Exception ex) {

                // Continue reading if device returns access denied error.
            }
        }
        return cells;
    }


    /*
     * Read all objects from the meter. This is only example. Usually there is
     * no need to read all data from the meter.
     */
    public List<String> readObisCodeListObjects(GXDLMSProfileGeneric gp2) throws ConnectionBrokenException, Exception {

        //TextFileHandler.loggedData("Reading obis code list ---------------->");

        GXDLMSObjectCollection objects = new GXDLMSObjectCollection();
        objects.add(gp2);

        // Read Profile Generic columns.
        List<String> ob = null;
        try {
            ob = readProfileGenericColumns(objects);
        } catch (ConnectionBrokenException e) {
            throw e;
        }

        return ob;
    }


    /*
     * Read profile generic columns from the meter.
     */
    public List<String> readProfileGenericColumns(final GXDLMSObjectCollection objects) throws ConnectionBrokenException {
        List<String> subObisCode = new LinkedList<String>();
        GXDLMSObjectCollection profileGenerics =
                objects.getObjects(ObjectType.PROFILE_GENERIC);
        for (GXDLMSObject it : profileGenerics) {

            GXDLMSProfileGeneric pg = (GXDLMSProfileGeneric) it;
            // Read columns.
            try {
                try {
                    readObject(pg, 3);
                } catch (ConnectionBrokenException e) {
                    throw e;
                }
                boolean first = true;
                StringBuilder sb = new StringBuilder();
                for (Entry<GXDLMSObject, GXDLMSCaptureObject> col : pg
                        .getCaptureObjects()) {
                    if (!first) {
                        sb.append(" | ");
                    }
                    subObisCode.add(col.getKey().getName().toString());
                    sb.append(col.getKey().getName());
                    sb.append(" ");
                    String desc = col.getKey().getDescription();
                    if (desc != null) {
                        sb.append(desc);
                    }
                    first = false;
                }
                //traceLn(logFile, sb.toString());
            } catch (ConnectionBrokenException e) {
                throw e;
            } catch (Exception ex) {
                ex.printStackTrace();
                /*traceLn(logFile,
                        "Err! Failed to read columns:" + ex.getMessage());*/
                // Continue reading.
            }
        }
        return subObisCode;
    }


    /**
     * added by Nitin Sethi, to read full data.
     *
     * @param pg
     * @return
     * @throws Exception
     */
    public Object[] readAllRowsData(final GXDLMSProfileGeneric pg) throws ConnectionBrokenException, Exception {
        GXReplyData reply = null;
        try {
            reply = new GXReplyData();
            GXDLMSObject it = (GXDLMSObject) pg;
            byte[][] data = dlms.read(it.getLogicalName(), it.getObjectType(), 2);
            try {
                readDataBlock(data, reply);
            } catch (ConnectionBrokenException e) {
                throw e;
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Object[]) dlms.updateValue(pg, 2, reply.getValue());
    }


    /**
     * added by Nitin Sethi, to read full data.
     *
     * @param pg
     * @return
     * @throws Exception
     */
    public Object readAllRowsPrepayData(final GXDLMSObject pg) throws ConnectionBrokenException, Exception {
        GXReplyData reply = null;
        try {
            reply = new GXReplyData();
            GXDLMSObject it = (GXDLMSObject) pg;
            byte[][] data = dlms.read(it.getLogicalName(), it.getObjectType(), 2);
            try {
                readDataBlock(data, reply);
            } catch (ConnectionBrokenException e) {
                throw e;
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply.getValue();
    }

    /**
     * added by Nitin Sethi, to read full data.
     *
     * @param pg
     * @return
     * @throws Exception
     */
    public Object readconfigurationsData(final GXDLMSObject pg, int attributeIndex) throws ConnectionBrokenException, Exception {
        GXReplyData reply = null;
        try {
            reply = new GXReplyData();
            GXDLMSObject it = (GXDLMSObject) pg;
            byte[][] data = dlms.read(it.getLogicalName(), it.getObjectType(), attributeIndex);
            try {
                readDataBlock(data, reply);
            } catch (ConnectionBrokenException e) {
                throw e;
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply.getValue();
    }

    /**
     * added by Nitin Sethi, to read full data.
     *
     * @param pg
     * @return
     * @throws Exception
     */
    public Object[] readMeterTypeData(final GXDLMSProfileGeneric pg) throws ConnectionBrokenException, Exception {
        GXReplyData reply = null;
        try {
            reply = new GXReplyData();
            GXDLMSObject it = (GXDLMSObject) pg;
            byte[][] data = dlms.read(it.getLogicalName(), it.getObjectType(), 2);
            try {
                readDataBlock(data, reply);
            } catch (ConnectionBrokenException e) {
                throw e;
            }
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Object[]) dlms.updateValue(pg, 2, reply.getValue());
    }


    /**
     * Read all data from the meter except profile generic (Historical) data.
     */
    public Object readClock(final GXDLMSObjectCollection objects,
                            final PrintWriter logFile) throws ConnectionBrokenException {
        Map<Object, Object> electricalParamData = new LinkedHashMap<Object, Object>();
        Object val = null;
        StringBuilder stringBuilder = new StringBuilder();
        for (GXDLMSObject it : objects) {
            if (!(it instanceof IGXDLMSBase)) {
                // If interface is not implemented.
                Log.d("TAG", "Unknown Interface: " + it.getObjectType().toString());
                continue;
            }

            for (int pos : ((IGXDLMSBase) it).getAttributeIndexToRead(false)) {
                try {
                    try {
                        val = readObject(it, pos);
                    } catch (ConnectionBrokenException e) {
                        throw e;
                    }
                    if (val instanceof byte[]) {
                        val = GXCommon.bytesToHex((byte[]) val);
                    } else if (val instanceof Double) {
                        NumberFormat formatter =
                                NumberFormat.getNumberInstance();
                        val = formatter.format(val);
                    } else if (val != null && val.getClass().isArray()) {
                        String str = "";
                        for (int pos2 = 0; pos2 != Array
                                .getLength(val); ++pos2) {
                            if (!str.equals("")) {
                                str += ", ";
                            }
                            Object tmp = Array.get(val, pos2);
                            if (tmp instanceof byte[]) {
                                str += GXCommon.bytesToHex((byte[]) tmp);
                            } else {
                                str += String.valueOf(tmp);
                            }
                        }
                        val = str;
                    }
                    electricalParamData.put(ObisCodeExtractor.getObisCodesIndexVal(it.getLogicalName()
                            , pos), val);
                    stringBuilder.append(val).append(",");
                } catch (ConnectionBrokenException e) {
                    throw e;
                } catch (Exception ex) {
                    // Continue reading.
                }
            }
        }
        return electricalParamData;
    }


    /*
     * Read Scalers and units from the register objects.
     */
    public void readScalerAndUnits(final GXDLMSObjectCollection objects,
                                   final PrintWriter logFile) {
        GXDLMSObjectCollection objs = objects.getObjects(new ObjectType[]{
                ObjectType.REGISTER, ObjectType.DEMAND_REGISTER, ObjectType.PROFILE_GENERIC,
                ObjectType.EXTENDED_REGISTER});

        try {
            List<Entry<GXDLMSObject, Integer>> list =
                    new ArrayList<Entry<GXDLMSObject, Integer>>();
            for (GXDLMSObject it : objs) {
                if (it instanceof GXDLMSRegister) {
                    list.add(new GXSimpleEntry<GXDLMSObject, Integer>(it, 3));
                }
                if (it instanceof GXDLMSDemandRegister) {
                    list.add(new GXSimpleEntry<GXDLMSObject, Integer>(it, 4));
                }
                if (it instanceof GXDLMSProfileGeneric) {
                    list.add(new GXSimpleEntry<GXDLMSObject, Integer>(it, 3));
                }
            }
            readList(list);
        } catch (Exception ex) {
            for (GXDLMSObject it : objs) {
                try {
                    if (it instanceof GXDLMSRegister) {
                        readObject(it, 3);
                    } else if (it instanceof GXDLMSDemandRegister) {
                        readObject(it, 4);
                    } else if (it instanceof GXDLMSProfileGeneric) {
                        readObject(it, 3);
                    }
                } catch (Exception e) {
                    /*traceLn(logFile,
                            "Err! Failed to read scaler and unit value: "
                                    + e.getMessage());*/
                    // Continue reading.
                }
            }
        }
    }


    /**
     * Writes value to DLMS object with selected attribute index.
     *
     * @param item
     * @param attributeIndex
     * @throws Exception
     */
    public void writeDataObject(GXDLMSObject item, int attributeIndex,
                                String obisCode, Object val, String manufacturer) throws ConnectionBrokenException, Exception {
        try {
            byte[][] data = null;
            if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.MasterObisCodes.CLOCK_DATA)) {
                data = dlms.write(obisCode, val, DataType.OCTET_STRING, ObjectType.CLOCK, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.MasterObisCodes.CONNECT_DISCONNECT_CONTROL)) {
                if (attributeIndex == 4) {
                    Integer value = Integer.parseInt((String) val);
                    data = dlms.write(obisCode, value.shortValue(), DataType.ENUM, ObjectType.DISCONNECT_CONTROL, attributeIndex);
                } else
                    data = dlms.method(obisCode, ObjectType.DISCONNECT_CONTROL, (int) val, 0, DataType.INT8);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.WrittingCode.DEMAND_INTEGRATION_PERIOD)
                    || item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.WrittingCode.PROFILE_CAPTURE_PERIOD)) {
                data = dlms.write(obisCode, Integer.parseInt((String) val) & 0xFFFF, DataType.UINT16, ObjectType.DATA, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.CURRENT_BALANCE_TIME)
                    || item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.LAST_TOKEN_RECHARGE_TIME)) {
                data = dlms.write(obisCode, val, DataType.OCTET_STRING, ObjectType.DATA, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.PAYMENT_MODE)
                    || item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.METERING_MODE)) {
                data = dlms.write(obisCode, Integer.parseInt((String) val), DataType.UINT8, ObjectType.DATA, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.LAST_TOKEN_RECHARGE_AMOUNT)) {
                if ("HPL".equalsIgnoreCase(manufacturer)) {
                    data = dlms.write(obisCode, Integer.parseInt((String) val) & 0xFFFF, DataType.INT32, ObjectType.DATA, attributeIndex);
                } else {
                    data = dlms.write(obisCode, Integer.parseInt((String) val) & 0xFFFF, DataType.UINT32, ObjectType.DATA, attributeIndex);
                }
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.TOTAL_AMOUNT_AT_LAST_RECHARGE)) {
                if ("RML".equals(manufacturer)) {
                    data = dlms.write(obisCode, Integer.parseInt((String) val) & 0xFFFF, DataType.UINT32, ObjectType.DATA, attributeIndex);
                } else {
                    data = dlms.write(obisCode, Integer.parseInt((String) val) & 0xFFFF, DataType.INT32, ObjectType.DATA, attributeIndex);
                }
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PrepayObisCode.CURRENT_BALANCE_AMOUNT)) {

                if ("RML".equals(manufacturer)) {
                    data = dlms.write(obisCode, Integer.parseInt((String) val), DataType.UINT32, ObjectType.DATA, attributeIndex);
                } else {
                    data = dlms.write(obisCode, Integer.parseInt((String) val), DataType.INT32, ObjectType.DATA, attributeIndex);
                }
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.MasterObisCodes.Limiter)) {
                if (attributeIndex == 4) {
                    if ("JPM".equalsIgnoreCase(manufacturer) || "IHM".equalsIgnoreCase(manufacturer)) {
                        data = dlms.write(obisCode, (Integer) val & 0xFFFF, DataType.FLOAT32, ObjectType.LIMITER, attributeIndex);
                    } else
                        data = dlms.write(obisCode, (Integer) val & 0xFFFF, DataType.UINT32, ObjectType.LIMITER, attributeIndex);
                } else if (attributeIndex == 10) {
                    boolean flag = false;
                    if ((Integer) val == 1) {
                        flag = true;
                    }
                    data = dlms.write(obisCode, flag, DataType.BOOLEAN, ObjectType.LIMITER, attributeIndex);
                }
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.ActionSchedule.BILLING_DATES)) {
                //data = dlms.write(obisCode, val, DataType.ARRAY, ObjectType.ACTION_SCHEDULE, 4);
                data = dlms.write(item, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.ActionSchedule.PUSH)) {
                data = dlms.write(item, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PushSetup.INSTANT_PUSH)
                    || item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.PushSetup.ALERT_PUSH)) {
                data = dlms.write(item, 3);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.WrittingCode.ACTIVITY_CALENDER_ZONE)) {
                data = dlms.write(item, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.ScriptTable.COVER_OPEN)
                    || item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.ScriptTable.MD_RESET)) {
                data = dlms.method(obisCode, ObjectType.SCRIPT_TABLE, 1, (int) val, DataType.UINT16);
            } else if (obisCode.equalsIgnoreCase(ObisCodeConstants.SecuritySetup.LLS)) {
                data = dlms.method(obisCode, ObjectType.SECURITY_SETUP, (int) val, 0, DataType.OCTET_STRING);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.AssociationLogicalName.MR_LLS)) {
                data = dlms.write(obisCode, val, DataType.OCTET_STRING, ObjectType.ASSOCIATION_LOGICAL_NAME, attributeIndex);
            } else if (item.getLogicalName().equalsIgnoreCase(ObisCodeConstants.AssociationLogicalName.US_HLS)) {
                data = dlms.method(item, 2, val, DataType.OCTET_STRING);
            }
            readDLMSPacket(data);
        } catch (ConnectionBrokenException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}
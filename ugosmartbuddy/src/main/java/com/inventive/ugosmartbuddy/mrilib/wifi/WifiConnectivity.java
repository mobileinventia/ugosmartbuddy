package com.inventive.ugosmartbuddy.mrilib.wifi;


import android.content.Context;
import android.os.AsyncTask;

import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXReceiveThread;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

public class WifiConnectivity {
    public  static   Socket socket=null;
    public  static  DataOutputStream dataOutputStream;
    public   static  DataInputStream dataInputStream;
    public  static boolean isConnected;
    public Context context;
    GXSerial serial;
    Thread thread;

    static boolean  isWifi=false;

    public WifiConnectivity(Context context){
        this.context=context;
    }
    public static final boolean getIsWifi(){
        return isWifi;
    }

    public static final void setIsWifi(boolean medium){

        isWifi=medium;
    }

    public static GXReceiveThread mReceiver;

    public static  WifiConnectivity connectivity;

    public static WifiConnectivity getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(WifiConnectivity connectivity) {
        this.connectivity = connectivity;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public static DataInputStream getDataInputStream() {
        return dataInputStream;
    }


    public  boolean ConnectWifi(GXSerial serial) {
        try {
            synchronized (this) {
                this.serial=serial;
            }
            mReceiver = new GXReceiveThread(serial, null, null);
            new MySocketAsync().EstablishConnection();
            mReceiver.resetBytesReceived();

        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

        }

        return isConnected;
    }

    public  void DisconnectWifi(){

        new MySocketAsync().CloseConnection();
    }

    public class MySocketAsync extends AsyncTask<Void, Void, Socket> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Socket socket) {
            super.onPostExecute(socket);

        }

        @Override
        protected Socket doInBackground(Void... arg) {

            return EstablishConnection();

        }

        private Socket EstablishConnection(){
            try {
                if(socket==null) {
                    socket = new Socket("192.168.4.1", 80);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream.write(Utils.AuthPassword());
                    Utils.OwnSleep(500);
                    if (dataInputStream.available() > 0) {
                        byte[] received_data = new byte[dataInputStream.available()];
                        int received_length = dataInputStream.read(received_data);
                        if (received_data[2] != 0x6) {
                            //  Utility.showDialog(context,"WiFi Authentication","WiFi Authentication Failure.");
                            // return socket=null;
                        }

                    }
                    dataOutputStream.write(Utils.ConfigureCommunicationParameters(9600,8,0));
                    Utils.OwnSleep(500);
                    if (dataInputStream.available() > 0) {
                        byte[] received_data = new byte[dataInputStream.available()];
                        int received_length = dataInputStream.read(received_data);
                    }
                    Utils.OwnSleep(500);
                    dataOutputStream.write(Utils.EnableTransparenetMode());
                    Utils.OwnSleep(500);
                    if (dataInputStream.available() > 0) {
                        byte[] received_data = new byte[dataInputStream.available()];
                        int received_length = dataInputStream.read(received_data);

                    }
                    // Utility.OwnSleep(500);

                    isConnected = socket.isConnected();
                    if (thread == null) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public final void run() {
                                byte[] buff = new byte[8192];
                                while (!Thread.currentThread().isInterrupted()) {
                                    try {
                                        int len = 0;
                                        len = WifiConnectivity.socket.getInputStream().read(buff);
                                        if (len == 0 && Thread.currentThread().isInterrupted()) {
                                            break;
                                        }

                                        if (len > 0) {
                                            mReceiver.handleReceivedData(buff, len);
                                        }
                                    } catch (Exception ex) {
                                        if (!Thread.currentThread().isInterrupted()) {
                                            new RuntimeException(ex.getMessage());
                                        }
                                    }
                                }
                            }

                        });
                        if (!thread.isAlive())
                            thread.start();
                    }
                }
                else {

                    isConnected=true;
                }

                while (!isConnected){
                }

            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
            }
            return socket;
        }

        public void CloseConnection(){
            try {

                if( socket !=null && socket.isConnected()){
                    dataOutputStream.write(Utils.EnableCommandMode());
                    dataInputStream=null;
                    dataOutputStream=null;
                    socket.close();
                    isConnected=false;
                    socket=null;
                }
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
            }

        }

    }



}
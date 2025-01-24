package com.inventive.ugosmartbuddy.mrilib.readData;

public class ConnectionBrokenException extends Exception{

    private static String errorMsg = "Connection has broken.";

    public ConnectionBrokenException()
    {
        super(errorMsg);
    }

}

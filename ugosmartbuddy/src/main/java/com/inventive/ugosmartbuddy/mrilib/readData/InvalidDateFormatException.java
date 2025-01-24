package com.inventive.ugosmartbuddy.mrilib.readData;

public class InvalidDateFormatException extends Exception{
    private static final long serialVersionUID = 1315023276821933953L;

    private static String errorMsg = "Invalid Date format Exception.";

    public InvalidDateFormatException()
    {
        super(errorMsg);
    }
}

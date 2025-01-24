package com.inventive.ugosmartbuddy.mrilib.readData;

import java.math.BigInteger;

public class ByteConverter {
    public static byte[] hex2decimal(String s)
    {
        byte[] b = null;
        if(s != null)
        {
            s = s.replaceAll(" ", "");
            b = new BigInteger(s,16).toByteArray();
        }

        return b;
    }

    public static void main(String args[])
    {

        //byte b[] = ByteConverter.hex2decimal("41 47 54 30 30 33 2D 30 31 31 37 2D 30 30 38 35");
        System.out.print(hex2text("07E1"));
    }

    public static String hex2text(String hex) {
        hex = hex.replaceAll(" ", "");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        System.out.println(output);
        return output.toString();
    }

    public static int hex2Decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public static String string2Decimal(String s) {
        StringBuffer sb = new StringBuffer();
        //Converting string to character array
        char ch[] = s.toCharArray();
        for(int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        return result;
    }
}

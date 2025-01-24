package com.inventive.ugosmartbuddy.mrilib.readData;

import org.xmlpull.v1.XmlPullParser;

public class XMLStreamException extends org.xmlpull.v1.XmlPullParserException {

    public XMLStreamException(String s) {
        super(s);
    }

    public XMLStreamException(String msg, XmlPullParser parser, Throwable chain) {
        super(msg, parser, chain);
    }
}

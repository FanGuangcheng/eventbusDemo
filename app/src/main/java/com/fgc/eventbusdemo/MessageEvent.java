package com.fgc.eventbusdemo;

public class MessageEvent {
    private String mDesc;

    MessageEvent(String desc) {
        mDesc = desc;
    }

    public String getDesc() {
        return mDesc;
    }
}

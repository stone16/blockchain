package com.myblockchain.model;

public class Msg {
    public String type;
    public String body;

    public Msg() {}

    public Msg(String type, String body) {
        this.type = type;
        this.body = body;
    }
}

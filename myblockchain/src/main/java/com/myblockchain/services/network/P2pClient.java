package com.myblockchain.services.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblockchain.model.Data;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class P2pClient implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String host;
    private int port;
    private String msg;

    public P2pClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 1000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    private void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Data d) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        msg = mapper.writeValueAsString(d);
        Thread t = new Thread(this);
        t.start();
    }

    public synchronized void run() {
        connect();
        try {
            if (out == null) {
                return;
            }
            out.write(msg);
            // TODO: Do we need any response?
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }
}

package com.myblockchain.services.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblockchain.model.Msg;
import lombok.Data;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Data
public class Connection implements Runnable {

    private Socket s;
    private ServerSocket ss;
    private BufferedReader in;
    private BufferedWriter out;

    public Connection (Socket s, ServerSocket ss) {
        this.s = s;
        this.ss = ss;
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException e) {
            try {
                s.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Handle incoming message
     * @param s incoming json string
     */
    private void msgHandler(String s) {
        try {
            ObjectMapper om = new ObjectMapper();
            Msg msg = om.readValue(s, Msg.class);
            if (msg.type.equals("chain")) {
                System.out.println("Receive chain");
            } else if (msg.type.equals("transaction")) {
                System.out.println("Receive transaction");
            } else if (msg.type.equals("clear_transactions")) {

            } else if (msg.type.equals("Registration")) {
                P2pServer.Pair(msg.body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Thread entrance, read the json message from TCP connection
     */
    public synchronized void run() {
        try {
            StringBuilder sb = new StringBuilder();
            String l;
            while (!s.isClosed() && s.isConnected()) {
                while ((l = in.readLine()) != null) {
                    sb.append(l);
                }
                msgHandler(sb.toString());
                //TODO: Do we need any response?
                in.close();
                out.close();
                s.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


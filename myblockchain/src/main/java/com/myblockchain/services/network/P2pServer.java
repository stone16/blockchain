package com.myblockchain.services.network;

import com.myblockchain.model.Data;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class P2pServer implements Runnable {

    static HashMap<String, P2pClient> clients = new HashMap<>();
    //Hard code
    private int port = 8888;

    private ServerSocket ss;
    private boolean run;
    private List<Connection> connections = new LinkedList<>();

    private String ip;

    public P2pServer() {}

    public P2pServer(int port) {
        this.port = port;
    }

    public static void Pair(String peerIp) {
        try {
            if (clients.containsKey(peerIp)) {
                return;
            }
            // Hard code
            System.out.println(peerIp);
            P2pClient client = new P2pClient(peerIp, 8888);
            clients.put(peerIp, client);

            String localIp = InetAddress.getLocalHost().getHostAddress();
            client.sendMsg(new Data("Registration", localIp));
        } catch (IOException e) {

        }
    }

    private void findPeer() {
        try {
            String broadcastAddr = "";
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            NetworkInterface netInterface = NetworkInterface.getByInetAddress(address);
            if (!netInterface.isLoopback() && netInterface.isUp()) {
                List<InterfaceAddress> interfaceAddresses = netInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    if (interfaceAddress.getBroadcast() == null) {
                        continue;
                    }
                    broadcastAddr = interfaceAddress.getBroadcast().getHostAddress();
                }
            }
            Data d = new Data("Registration", ip);
            String[] part = broadcastAddr.trim().split("\\.");
            for (int i = 0; i < 255; i++) {
                for (int j = 0; j < 255; j++) {
                    for (int k = 0; k < 255; k++) {
                        for (int l = 0; l < 255; l++) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(part[0].equals("255") ? i : part[0] + ".");
                            sb.append(part[1].equals("255") ? j : part[1] + ".");
                            sb.append(part[2].equals("255") ? k : part[2] + ".");
                            sb.append(part[3].equals("255") ? l : part[3]);
                            String receiver = sb.toString();
                            if (receiver.equals(ip)) {
                                continue;
                            }
                            // System.out.println(receiver);
                            P2pClient s = new P2pClient(receiver, port);
                            s.sendMsg(d);
                            if (!part[3].equals("255")) {
                                break;
                            }
                        }
                        if (!part[2].equals("255")) {
                            break;
                        }
                    }
                    if (!part[1].equals("255")) {
                        break;
                    }
                }
                if (!part[0].equals("255")) {
                    break;
                }
            }

        } catch (IOException e) {
            // e.printStackTrace();
        }

    }


    public void init() {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        run = true;
        System.out.println("P2P server is listening on port: " + port);
        Thread t = new Thread(this);
        t.start();
        findPeer();
    }

    public void close() {
        run = false;
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (run) {
                if (ss.isClosed()) {
                    return;
                }
                Socket s = ss.accept();
                Connection c = new Connection(s, ss);
                connections.add(c);
                //TODO: check connections status
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


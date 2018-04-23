package com.myblockchain.services.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblockchain.model.Block;
import com.myblockchain.model.Msg;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.Configuration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

@Data
@Component
public class P2pServer implements Runnable {

    public static HashMap<String, P2pClient> clients = new HashMap<>();
    private List<Connection> connections = new LinkedList<>();
    private String ip;
    private int port = Configuration.P2PConfig.P2P_PORT;
    private ServerSocket ss;
    private boolean run;

    @Autowired
    private TransactionPool pool;

    @Autowired
    private BlockChain blockchain;

    @Autowired
    private Wallet wallet;

    public P2pServer() {}

    public P2pServer(int port, BlockChain blockchain, TransactionPool pool, Wallet wallet) {
        this.port = port;
        this.blockchain = blockchain;
        this.pool = pool;
        this.wallet = wallet;
    }

    /**
     * Pairing with the the new P2P server
     * @param peerIp
     */
    public static void Pair(String peerIp) {
        try {
            if (clients.containsKey(peerIp)) {
                return;
            }
            System.out.println("PeerId: " + peerIp);
            P2pClient client = new P2pClient(peerIp);
            clients.put(peerIp, client);

            String localIp = P2pServer.getlocalAndBroadcastIP()[0];

            client.sendMsg(new Msg(Configuration.MessageType.REGISTRATION, localIp));

        } catch (IOException e) {

        }
    }

    private static String[] getlocalAndBroadcastIP() {
        String[] res = new String[2];
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements();)
            {
                NetworkInterface e = n.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();)
                {
                    InetAddress addr = a.nextElement();
                    NetworkInterface netInterface = NetworkInterface.getByInetAddress(addr);
                    if (!netInterface.isLoopback() &&
                            netInterface.isUp() &&
                            !addr.getHostAddress().equals("127.0.0.1") &&
                            addr.getHostAddress().split("\\.").length == 4) {
                        res[0] = addr.getHostAddress();
                        List<InterfaceAddress> interfaceAddresses = netInterface.getInterfaceAddresses();
                        for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                            if (interfaceAddress.getBroadcast() == null) {
                                continue;
                            }
                            res[1] = interfaceAddress.getBroadcast().getHostAddress();
                            return res;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return res;
    }

    /**
     * Broadcast the pair request to all subnet ip address
     */
    private void findPeer() {
        try {
            String[] addr = getlocalAndBroadcastIP();
            ip = addr[0];
            String broadcastAddr = addr[1];
            Msg d = new Msg(Configuration.MessageType.REGISTRATION, ip);
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
                            P2pClient s = new P2pClient(receiver);
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

    /**
     * Broadcast the updated blockchain to peers
     */
    public void broadcastChains(List<Block> chain) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, chain);
            broadcastMsg(new Msg(Configuration.MessageType.CHAIN, new String(out.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Broadcast the latest transaction to peers
     */
    public void broadcastTransaction(Transaction t) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            broadcastMsg(new Msg(Configuration.MessageType.TRANSACTION, mapper.writeValueAsString(t)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Broadcast to clear the transaction pool to peers
     */
    public void broadcastClearTransaction(ArrayList<Transaction> validTransactions) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, validTransactions);
            broadcastMsg(new Msg(Configuration.MessageType.CLEAR, new String(out.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void broadcastMsg(Msg msg) {
        clients.forEach((peerIp, client) -> {
            try {
                client.sendMsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Starting the P2P server
     */
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

    /**
     * Stoping the P2P server
     */
    public void close() {
        run = false;
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread entrance
     */
    public void run() {
        try {
            while (run) {
                if (ss.isClosed()) {
                    return;
                }
                Socket s = ss.accept();
                Connection c = new Connection(s, ss, pool, blockchain, wallet);
                connections.add(c);
                connections.removeIf(Connection::isClosed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


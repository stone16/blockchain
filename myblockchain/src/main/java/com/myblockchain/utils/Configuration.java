package com.myblockchain.utils;

public class Configuration {

    public static int DIFFICULTY = 4;
    public static int MINE_RATE = 3000;
    public static int INITIAL_BALANCE = 500;
    public static int MINING_REWARD = 50;
    public static int TRANSACTION_NUM = 10;

    public static class P2PConfig {
        public static int P2P_PORT = 8888;
        public static int SOCKET_TIMEOUT = 1000;
    }

    public static class MessageType {
        public static String CHAIN = "CHAIN";
        public static String TRANSACTION = "TRANSACTION";
        public static String REGISTRATION = "REGISTRATION";
        public static String CLEAR = "CLEAR";
    }
}

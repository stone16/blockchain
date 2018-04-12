package com.myblockchain.model;


import com.myblockchain.utils.BlockChainUtil;

import java.util.ArrayList;
import java.util.Date;

@lombok.Getter
@lombok.Setter
public class Block {
    private long timeStamp;
    private String lastHash;
    private String hash;
    private long nonce;
    private long difficulty;
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    //private String merkleTree;

//    public Block(String lastHash) {
//        this.lastHash = lastHash;
//        this.timeStamp = new Date().getTime();
//
//        this.hash = calculateHash();
//    }

    public Block(long timeStamp, String lastHash, String hash, long nonce,
                 long difficulty, ArrayList<Transaction> transactions) {
        this.timeStamp = timeStamp;
        this.lastHash = lastHash;
        this.hash = hash;
        this.nonce = nonce;
        this.difficulty = difficulty;
        this.transactions = transactions;
    }

    // TODO: consider the security of calling this method
    // TODO: how to put nonce and difficulty into this without hardcode but in configure way
    public static Block genesis() {
        long timeStamp = new Date().getTime();
        String lastHash = "";
        long difficulty = 0;
        long nonce = 0;
        ArrayList<Transaction> transactions = new ArrayList<>();
        String hash = calculateHash(timeStamp, lastHash, difficulty, nonce, transactions);

        return new Block(timeStamp, lastHash, hash, nonce, difficulty, transactions);
    }


    public static String calculateHash(long timeStamp, String lastHash, long difficulty, long nonce, ArrayList<Transaction> transactions) {
        return BlockChainUtil.getSHA256Hash(Long.toString(timeStamp) +
                                        lastHash +
                                        Long.toString(difficulty) +
                                        Long.toString(nonce) +
                                        transactions
        );
    }
    @Override
    public String toString() {
        return "Block -" +
                "\n timeStamp: " + String.valueOf(timeStamp) +
                "\n lastHash: " + String.valueOf(lastHash) +
                "\n hash: " + String.valueOf(hash) +
                "\n nonce: " + String.valueOf(nonce) +
                "\n difficulty: " + String.valueOf(difficulty) +
                "\n transactions: " + String.valueOf(transactions);
    }
}

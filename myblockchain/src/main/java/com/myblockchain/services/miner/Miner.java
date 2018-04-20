package com.myblockchain.services.miner;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.network.P2pServer;
import com.myblockchain.services.wallet.Wallet;

import java.util.ArrayList;

public class Miner implements Runnable {
    private BlockChain blockchain;
    private TransactionPool pool;
    private Wallet wallet;
    private P2pServer p2p;
    private Block latestBlock;


    public Miner(BlockChain blockchain, TransactionPool pool, Wallet wallet, P2pServer p2p) {
        this.blockchain = blockchain;
        this.pool = pool;
        this.wallet = wallet;
        this.p2p = p2p;
    }

    public Block mine() {
        Thread thread = new Thread(this);
        thread.start();
        return latestBlock;
    }

    public synchronized void run() {
        ArrayList<Transaction> validTransaction = pool.getTransactionList(5);
        validTransaction.add(Transaction.rewardMinner(wallet, 50));
        latestBlock = blockchain.addBlock(validTransaction);
        p2p.broadcastChains(blockchain.getChain());
        pool.updateTransactionPool(validTransaction);
        p2p.broadcastClearTransaction(validTransaction);
    }
}

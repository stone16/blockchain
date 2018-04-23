package com.myblockchain.services.miner;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.network.P2pServer;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.Configuration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
@Data
@Component
public class Miner implements Runnable {
    private BlockChain blockchain;
    private TransactionPool pool;
    private Wallet wallet;
    private P2pServer p2p;
    private boolean run;

    @Autowired
    public Miner(BlockChain blockchain, TransactionPool pool, Wallet wallet, P2pServer p2p) {
        this.blockchain = blockchain;
        this.pool = pool;
        this.wallet = wallet;
        this.p2p = p2p;
    }

    public void startMine() {
        if(run) {
            return;
        }
        run = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stopMine() {
        run = false;
    }

    /**
     * Thread Entrance, Miner try to mine block
     */
    public void run() {
        while (run) {
            ArrayList<Transaction> validTransaction = pool.getTransactionList(Configuration.TRANSACTION_NUM);
            validTransaction.add(Transaction.rewardMinner(wallet, Configuration.MINING_REWARD));
            Block latestBlock = blockchain.addBlock(validTransaction);
            if (latestBlock == null) {
                continue;
            }
            p2p.broadcastChains(blockchain.getChain());
            try{
                Thread.sleep(1000);
            } catch(Exception e) {
                e.printStackTrace();
            }
            pool.updateTransactionPool(validTransaction);
            p2p.broadcastClearTransaction(validTransaction);
            wallet.updateUTXOsFromWholeBlockChain(blockchain);
        }
    }
}
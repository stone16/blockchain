package com.myblockchain.services.miner;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.network.P2pServer;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.Configuration;

import java.util.ArrayList;

public class Miner {
    private BlockChain blockchain;
    private TransactionPool pool;
    private Wallet wallet;
    private P2pServer p2p;


    public Miner(BlockChain blockchain, TransactionPool pool, Wallet wallet, P2pServer p2p) {
        this.blockchain = blockchain;
        this.pool = pool;
        this.wallet = wallet;
        this.p2p = p2p;
    }

    /**
     * Miner try to mine block
     * @return
     */
    public Block mine() {
        ArrayList<Transaction> validTransaction = pool.getTransactionList(Configuration.TRANSACTION_NUM);
        validTransaction.add(Transaction.rewardMinner(wallet, Configuration.MINING_REWARD));
        Block latestBlock = blockchain.addBlock(validTransaction);
        p2p.broadcastChains(blockchain.getChain());
        pool.updateTransactionPool(validTransaction);
        p2p.broadcastClearTransaction(validTransaction);
        return latestBlock;
    }
}

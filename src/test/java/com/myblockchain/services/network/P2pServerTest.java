package com.myblockchain.services.network;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionInput;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.BlockChainUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class P2pServerTest {

    P2pServer p2p = new P2pServer();
    BlockChain blockChain = BlockChain.newBlockchain();
    Wallet sender = new Wallet();
    Wallet recipient = new Wallet();
    TransactionInput txInput;
    List<Transaction> txArrays;
    Block minedBlock;
    TransactionPool txPool;

    @Before
    public void setUp() throws Exception {
        txArrays = new ArrayList<>();
        txArrays.add(Transaction.rewardMinner(sender, 100));
        txArrays.add(Transaction.rewardMinner(sender, 200));
        txPool = new TransactionPool();
        txArrays.forEach(transaction -> txPool.updateOrAddTransaction(transaction));
        minedBlock = blockChain.addBlock(txPool.getTransactionList(5));
        sender.updateUTXOsFromMinnedBlock(minedBlock);
        txArrays.clear();
        txArrays.add(Transaction.newTransaction(sender, BlockChainUtils.convertKeytoString(recipient.getPublicKey()), 50));
        txArrays.forEach(transaction -> txPool.updateOrAddTransaction(transaction));
        minedBlock = blockChain.addBlock(txPool.getTransactionList(5));
        sender.updateUTXOsFromMinnedBlock(minedBlock);
        recipient.updateUTXOsFromMinnedBlock(minedBlock);
    }
    @Test
    public void broadcastChains() throws Exception {
        p2p.broadcastChains(blockChain.getChain());
    }

    @Test
    public void broadcastTransaction() throws Exception {

    }

    @Test
    public void broadcastClearTransaction() throws Exception {

    }

}
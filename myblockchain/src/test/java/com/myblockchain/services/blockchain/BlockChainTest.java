package com.myblockchain.services.blockchain;

import com.myblockchain.model.*;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.BlockChainUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlockChainTest {

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
    public void BlockChain_addBlock() throws Exception {
        System.out.println(minedBlock.toString());
    }

    @Test
    public void BlockChain_getAllTransactions() throws Exception {
        System.out.println(blockChain.getAllTransactions());
    }

    @Test
    public void Wallet() throws Exception {
        System.out.println(sender.toString());
    }

    @Test
    public void Wallet_getBalance() throws Exception {
        System.out.println("Sender: " + sender.getBalance());
        System.out.println("Recipient:  " + recipient.getBalance());
    }

    @Test
    public void newBlockchain() throws Exception {
        System.out.println(blockChain.toString());
    }

    @Test
    public void initBlockChainFromJSON() throws Exception {
        blockChain = BlockChain.initBlockChainFromJSON("BlockChain.json");
        System.out.println(blockChain.toString());
    }

    @Test
    public void storeBlockChain() throws Exception {
        BlockChain.storeBlockChain(blockChain);
        initBlockChainFromJSON();
    }

    @Test
    public void getAllSpentTXOs() throws Exception {
        Map<String, TransactionOutput> map = blockChain.getAllSpentTXOs();
        System.out.println(map);
    }

    @Test
    public void findAllUTXOs() throws Exception {
    }

    @Test
    public void updateWalletUTXOs() throws Exception {
    }

    @Test
    public void findTransaction() throws Exception {
    }

    @Test
    public void addBlock() throws Exception {
    }

    @Test
    public void isValidChain() throws Exception {
        Assert.assertEquals(blockChain.isValidChain(blockChain.getChain()), true);
    }

    @Test
    public void replaceChain() throws Exception {
    }

}
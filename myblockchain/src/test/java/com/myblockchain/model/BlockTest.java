package com.myblockchain.model;

import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.wallet.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlockTest {
    BlockChain blockChain;
    Wallet sender;
    Wallet recipient;
    ArrayList<Transaction> transactions;
    @Before
    public void setUp() throws Exception {
        blockChain = BlockChain.newBlockchain();
        sender = new Wallet();
        recipient = new Wallet();
        float amount = 10;
        transactions = new ArrayList<>();
//        Transaction tx = Transaction.newTransaction(sender, recipient.getPublicKey(), amount);
//        transactions.add(tx);
    }

    @Test
    public void getMerkleRoot() throws Exception {

    }

    @Test
    public void mineBlock() throws Exception {
        //Get genesis block
        Block lastBlock = blockChain.getChain().get(0);
        System.out.println("Gensis Block: " + lastBlock.toString());
        System.out.println("Mined Block: " + Block.mineBlock(lastBlock, transactions).toString());
    }

    @Test
    public void adjustDifficulty() throws Exception {

    }

    @Test
    public void genesisTest() throws Exception {
        System.out.println(Block.genesis().toString());
    }

}
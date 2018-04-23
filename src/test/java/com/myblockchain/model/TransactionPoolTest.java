package com.myblockchain.model;

import com.myblockchain.services.wallet.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionPoolTest {
    Wallet walletA;
    Wallet walletB;
    TransactionPool pool;
    @Before
    public void setUp() throws Exception {
//        walletA = new Wallet();
//        walletB = new Wallet();
//        pool = new TransactionPool();
//
//        Transaction transactionA = new Transaction(walletA, walletB.getPublicKey(),
//                5, null);
//        transactionA.signTransaction(walletA.getPrivateKey());
//
//        Transaction transactionB = new Transaction(walletA, walletB.getPublicKey(),
//                5, null);
//        transactionB.signTransaction(walletA.getPrivateKey());
//
//        pool.updateOrAddTransaction(transactionA);
//        pool.updateOrAddTransaction(transactionB);


    }

    @Test
    public void getTransactions() throws Exception {

        for(Transaction t : pool.getTransactionList(2)) {
            t.toString();
        }
    }

}
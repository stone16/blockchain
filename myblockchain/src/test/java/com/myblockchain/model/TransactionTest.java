package com.myblockchain.model;

import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.BlockChainUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Security;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionTest {
    Wallet walletA;
    Wallet walletB;
    @Before
    public void setUp() throws Exception {
        //Set Bouncy Castle as a security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        System.out.println("Private and Public Keys:");
        System.out.println(BlockChainUtils.getStringFromKey(walletA.getPrivateKey()));
        System.out.println(BlockChainUtils.getStringFromKey(walletA.getPublicKey()));
    }

    @Test
    public void verifyTransactionTrue() throws Exception {
//        Transaction transaction = new Transaction(walletA, walletB.getPublicKey(),
//                5, null);
//        transaction.signTransaction(walletA.getPrivateKey());
//        Assert.assertEquals(transaction.verifyTransaction(), true);
    }

}
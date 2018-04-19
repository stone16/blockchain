package com.myblockchain.services.wallet;


import com.myblockchain.model.*;
import com.myblockchain.services.blockchain.BlockChain;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wallet contains two fields: PublicKey and PrivateKey
 */
@Data
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    //TODO: Fix this
    @Autowired
    private BlockChain blockChain;

    private Map<String, TransactionOutput> UTXOs;

    /**
     * Constructor
     */
    public Wallet() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        genKeyPair();
    }

    /**
     * get Balance from a blockchain
     * @return float
     */
    public float getBalance() {
        float balance[] = {0};
        ArrayList<Transaction> transactions = new ArrayList<>();
        for(Block block : blockChain.getChain()) {
            block.getTransactions().forEach(transaction -> transactions.add(transaction));
        }

        ArrayList<Transaction> walletOutputTs = new ArrayList<>();
        transactions.forEach(transaction -> {
            transaction.getOutputs().forEach(transactionOutput -> {
                if(transactionOutput.getRecipient() == this.publicKey) {
                   UTXOs.put(transactionOutput.getId(), transactionOutput);
                   balance[0] += transactionOutput.getAmount();
                   walletOutputTs.add(transaction);
                }
            });
        });

        return balance[0];

    }

    /**
     * Override toString() method
     */
    @Override
    public String toString() {
        return "Wallet -" +
                "\n publicKey: " + publicKey.toString() +
                "\n privateKey: " + privateKey.toString();
    }

    /**
     * Using Elliptic Curve Algorithm to generate KeyPair
     */
    private void genKeyPair() {
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "SunEC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp192k1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * create a new transaction
     * @param recipient
     * @param amount
     * @param blockChain
     * @param transactionPool
     * @return Transaction
     */
    public Transaction createTransaction(PublicKey recipient, float amount,
                                         BlockChain blockChain, TransactionPool transactionPool) {
        float balance = getBalance();

        if(amount > balance) {
            //TODO: handle this situation
            System.out.println("Amount exceceds current balance: " + amount + " > " + balance);
        }

        Transaction transaction = transactionPool.existingTransaction(publicKey);
        if(transaction != null) {
            //transaction.update(this, recipient, amount);
        } else {
            transaction = Transaction.newTransaction(this, recipient, amount);
            transactionPool.updateOrAddTransaction(transaction);
        }
        return transaction;
    }

}

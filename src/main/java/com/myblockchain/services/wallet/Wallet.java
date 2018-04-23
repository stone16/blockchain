package com.myblockchain.services.wallet;


import com.myblockchain.model.*;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.utils.BlockChainUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Wallet contains two fields: PublicKey and PrivateKey
 */
@Data
@Component
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
        this.UTXOs = new HashMap<>();
    }

    /**
     * get Balance from a blockchain
     * @return float
     */
    public float getBalance() {
        float balance = 0;
        if(this.getUTXOs().isEmpty()) return balance;
        for(TransactionOutput txOutput : this.getUTXOs().values()) {
            balance += txOutput.getAmount();
        }
        return balance;
    }

    /**
     * Update UTXOs from whole block chain
     */
    public void updateUTXOsFromWholeBlockChain(BlockChain blockChain) {
        this.setUTXOs(blockChain.findWalletUTXOs(this.getPublicKey()));
    }

    /**
     * Update UTXOs from a minned block
     * @param minnedBlock
     */
    public void updateUTXOsFromMinnedBlock(Block minnedBlock) {
        for(Transaction tx : minnedBlock.getTransactions()) {
            for(TransactionOutput txOutput : tx.getOutputs()) {
                PublicKey publicKey = BlockChainUtils.convertStringtoKey(txOutput.getRecipient());
                if(publicKey.equals(this.getPublicKey())) {
                    this.UTXOs.put(txOutput.getId(), txOutput);
                }
            }
        }
    }

    /**
     * Override toString() method
     */
    @Override
    public String toString() {
        return "Wallet -" +
                "\n publicKey: " + String.valueOf(this.getPublicKey()) +
                "\n privateKey: " + String.valueOf(this.getPrivateKey()) +
                "\n UTXOs: " + String.valueOf(this.getUTXOs()) + "\n";
    }

    /**
     * Using Elliptic Curve Algorithm to generate KeyPair
     */
    private void genKeyPair() {
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC","SunEC");
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
     * @param transactionPool
     * @return Transaction
     */
    public Transaction createTransaction(String[] recipient, float amount,
                                         TransactionPool transactionPool) {
        float balance = getBalance();

        if(amount > balance) {
            //TODO: handle this situation
            System.out.println("Amount exceceds current balance: " + amount + " > " + balance);
        }

        Transaction transaction = Transaction.newTransaction(this, recipient, amount);
        transactionPool.updateOrAddTransaction(transaction);

        return transaction;
    }

}

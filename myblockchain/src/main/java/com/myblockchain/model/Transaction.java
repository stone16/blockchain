package com.myblockchain.model;

import com.myblockchain.utils.BlockChainUtil;
import com.myblockchain.services.wallet.Wallet;
import lombok.Data;

import java.security.PublicKey;
import java.util.ArrayList;

@Data
public class Transaction {
    private String transactionId;
    private PublicKey sender;
    private PublicKey reciepient;
    private float amount;
    private byte[] signature;

//    //private ArrayList<TransactionInput> inputs = new ArrayList<>();
//    //private ArrayList<TransactionOutput> outputs = new ArrayList<>();
//
//    public Transaction(PublicKey sender, PublicKey reciepient,
//                       float amount, ArrayList<TransactionInput> inputs) {
//        this.transactionId = BlockChainUtil.generateTransactionId();
//        this.sender = sender;
//        this.reciepient = reciepient;
//        this.amount = amount;
//        this.inputs = inputs;
//    }


    // TODO: complete this part
    public void update(Wallet senderWallet, PublicKey reciepient, float amount) {
        return;
    }

    // TODO: complete this part
    public static Transaction newTransaction(Wallet senderWallet, PublicKey reciepient, float amount) {

        //TODO: using autowired?
//        Transaction transaction = new Transaction(senderWallet.getPublicKey(), );
        return new Transaction();
    }



}

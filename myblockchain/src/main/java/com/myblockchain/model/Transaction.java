package com.myblockchain.model;

import com.myblockchain.utils.BlockChainUtils;
import com.myblockchain.services.wallet.Wallet;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class Transaction {
    private String transactionId;
    private PublicKey sender;
    private PublicKey recipient;
    private float amount;
    private byte[] signature;

    private ArrayList<TransactionInput> inputs;
    private ArrayList<TransactionOutput> outputs;

    public Transaction() {
        this.transactionId = BlockChainUtils.generateTransactionId();
    }

    public Transaction(Wallet senderWallet, PublicKey recipient,
                       float amount, ArrayList<TransactionInput> inputs) {
        this.transactionId = BlockChainUtils.generateTransactionId();
        this.recipient = recipient;
        this.sender = senderWallet.getPublicKey();
        this.amount = amount;
        this.inputs = inputs;
    }


    /**
     * launch a new transaction
     * @param senderWallet
     * @param recipient
     * @param amount
     * @return Transaction
     */
    public static Transaction newTransaction(Wallet senderWallet, PublicKey recipient, float amount) {

        //Check the balance of sender
        //TODO: fix hardcoded minimumValue

        if(amount < 0.1f) {
            System.out.println("Transaction Inputs too small: " + amount);
            System.out.println("Please enter the amount greater than " + 0.1f);
            return null;
        } else if(senderWallet.getBalance() < amount) {
            System.out.println("There is not enough balance in the wallet");
            return null;
        }

        /*
         *Put senderWallet's UTXOs as input in Transaction
         */

        //Until the total amount of UTXOs is >= amount
        float total = 0;
        ArrayList<TransactionInput> newInputs = new ArrayList<>();
        for(TransactionOutput UTXO : senderWallet.getUTXOs().values()) {
            total += UTXO.getAmount();
            if(total < amount) {
                TransactionInput newInput = new TransactionInput(UTXO);
                newInputs.add(newInput);
            } else {
                break;
            }
        }

        //Generate transaction outputs:
        float balance = total - amount; //get value of inputs then the balance
        Transaction transaction = new Transaction(senderWallet, recipient, amount, newInputs);
        TransactionOutput txOp1 = new TransactionOutput( recipient, amount, transaction.getTransactionId()); //send value to recipient
        TransactionOutput txOp2 = new TransactionOutput( senderWallet.getPublicKey(), balance,transaction.getTransactionId()); //send the 'change' back to sender
        transaction.getOutputs().add(txOp1);
        transaction.getOutputs().add(txOp2);


        //Sign the transaction with signature
        transaction.signTransaction(senderWallet.getPrivateKey());

        return transaction;
    }



    /**
     * Generate Signature of transactions with privateKey
     * @param privateKey
     */
    public void signTransaction(PrivateKey privateKey) {
        String data = BlockChainUtils.getStringFromKey(sender) +
                      BlockChainUtils.getStringFromKey(recipient) +
                      outputs.toString();
        signature = BlockChainUtils.applySignature(privateKey, data);
    }

    /**
     * Verify transaction with signature and own publicKey
     * @return boolean
     */
    public boolean verifyTransaction() {
        String data = BlockChainUtils.getStringFromKey(sender) +
                BlockChainUtils.getStringFromKey(recipient) +
                outputs.toString();
        return BlockChainUtils.verifySignature(sender, data, signature);
    }

    /**
     * Reward minner
     * @param minnerWallet
     * @param reward
     * @return Transaction
     */
    public static Transaction rewardMinner(Wallet minnerWallet, float reward) {

        //Create a new TransactionOutput with reward amount
        Transaction rewardTransaction = new Transaction(minnerWallet, minnerWallet.getPublicKey(), reward, null);
        TransactionOutput newOutput = new TransactionOutput(minnerWallet.getPublicKey(),
                reward, rewardTransaction.getTransactionId());
        ArrayList<TransactionOutput> newOutputs = new ArrayList<>();
        newOutputs.add(newOutput);
        rewardTransaction.setOutputs(newOutputs);

        //Sign this transactions
        rewardTransaction.signTransaction(minnerWallet.getPrivateKey());

        return rewardTransaction;
    }

    /**
     * get the total amount of inputs
     * @return float
     */
    public float getInputsSum() {
        float sum = 0;
        for(TransactionInput ti : inputs) {
            if(ti.getUTXO() == null) continue;
            sum += ti.getUTXO().getAmount();
        }
        return sum;
    }

    /**
     * get the total amount of outputs
     * @return
     */
    public float getOutputsSum() {
        float sum = 0;
        for(TransactionOutput to : outputs) {
            sum += to.getAmount();
        }
        return sum;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", amount=" + amount +
                ", signature=" + Arrays.toString(signature) +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}
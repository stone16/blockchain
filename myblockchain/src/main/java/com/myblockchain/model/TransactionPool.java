package com.myblockchain.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.*;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@Data
public class TransactionPool {
    private LinkedHashMap<String, Transaction> transactions;

    public TransactionPool() {
        this.transactions = new LinkedHashMap<>();
    }

    /**
     * clear the transaction pool
     */
    public void clear() {
        transactions.clear();
    }

    /**
     * update or add a transaction into transaction pool
     * @param transaction
     */
    public void updateOrAddTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
    }

    //TODO: enhance this part using stram()
    /**
     * Find out the existing transaction in transaction pool
     * @param publicKey
     * @return Transaction
     */
    public Transaction existingTransaction(PublicKey publicKey) {
        for(Map.Entry<String, Transaction> entry : transactions.entrySet()) {
            if(entry.getValue().getSender() == publicKey) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * get N of transactions by indicating the num from the transaction pool
     * @param num
     * @return List of Transaction
     */
    public ArrayList<Transaction> getTransactionList(int num) {
        ArrayList<Transaction> res = new ArrayList<>(num);
        int i = 0;
        try {
            Iterator<Map.Entry<String, Transaction>> itr = transactions.entrySet().iterator();
            while(itr.hasNext()) {
                Map.Entry<String, Transaction> entry = itr.next();
                Transaction cur = entry.getValue();
                if (i < num && cur.verifyTransaction()) {
                    res.add(cur);
                    itr.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /** Remove transactions that have been proved in minned block in transactions pool.
     * @param validTransactions
     */
    public void updateTransactionPool(List<Transaction> validTransactions) {
        for(Transaction transaction : validTransactions) {
            if(this.transactions.containsKey(transaction.getTransactionId())) {
                this.transactions.remove(transaction.getTransactionId());
            }
        }
    }
}

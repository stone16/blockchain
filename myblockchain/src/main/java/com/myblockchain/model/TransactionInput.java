package com.myblockchain.model;

import lombok.Data;


@Data
public class TransactionInput {
    private String transactionOutputId;
    private TransactionOutput UTXO;

    public TransactionInput(TransactionOutput UTXO) {
        this.UTXO = UTXO;
        this.transactionOutputId = UTXO.getId();
    }
}

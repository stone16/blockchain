package com.myblockchain.services.blockchain;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionInput;
import com.myblockchain.model.TransactionOutput;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.PersistentUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BlockChain {

    private List<Block> chain;

    /**
     * Create a new block chain with a genesis block
     * @return BlockChain
     */
    public static BlockChain newBlockchain() {
        List<Block> chain = new LinkedList<>();
        chain.add(Block.genesis());
        return new BlockChain(chain);
    }

    /**
     * Initiate BlockChain from JSON file
     * @param path
     * @return BlockChain
     */
    public static BlockChain initBlockChainFromJSON(String path) {
            return PersistentUtils.readBlockChain(path);
    }

    /**
     * Store BlockChain to JSON file
     * @param blockChain
     */
    public static void storeBlockChain(BlockChain blockChain) {
        PersistentUtils.storeBlockChain(blockChain);
    }

    /**
     * Search all spentTXOs in inputs of all transactions and
     * save as HashMap.
     * @return Map
     */
    public Map<String, TransactionOutput> getAllSpentTXOs() {

        Map<String, TransactionOutput> spentTXOs = new HashMap<>();
        for (Block block : chain) {
            for (Transaction transaction : block.getTransactions()) {
                //If it is a rewarded transaction, jump over it
                if (transaction.getInputs() == null) {
                    continue;
                }
                for (TransactionInput txInput : transaction.getInputs()) {
                    spentTXOs.put(txInput.getTransactionOutputId(), txInput.getUTXO());
                }
            }
        }
        return spentTXOs;
    }

    /**
     * Search all UTXOs in outputs of all transactions and save as HashMap.
     * @return Map
     */
    public Map<String, TransactionOutput> findAllUTXOs() {
        Map<String, TransactionOutput> allSpentTXOs = this.getAllSpentTXOs();
        Map<String, TransactionOutput> allUTXOs = new HashMap<>();

        for (Block block : chain) {
            for (Transaction transaction : block.getTransactions()) {
                for(TransactionOutput txOutput : transaction.getOutputs()) {
                    if(allSpentTXOs.containsKey(txOutput.getId())) {
                        continue;
                    } else {
                        allUTXOs.put(txOutput.getId(), txOutput);
                    }
                }
            }
        }
        return allUTXOs;
    }

    /**
     * Update a user's wallet's UTXOs
     * @param wallet
     * @param allUTXOs
     */
    public void updateWalletUTXOs(Wallet wallet, Map<String, TransactionOutput> allUTXOs) {
        wallet.setUTXOs(
                new HashMap<>(
                        allUTXOs.values().stream()
                        .filter(transactionOutput -> transactionOutput.getRecipient().equals(wallet.getPublicKey()))
                        .collect(Collectors.toMap(TransactionOutput::getId, Function.identity()))
                )
        );
    }

    /**
     * Find transaction in block chain using transaction Id
     * @param transactionId
     * @return Transaction
     */
    public Transaction findTransaction(String transactionId) {
        for(Block block : chain) {
            for(Transaction tx : block.getTransactions()) {
                if(tx.getTransactionId().equals(transactionId)) return tx;
            }
        }
        return null;
    }

    /**
     * Add a new mined block into block chain
     * @param transactions
     * @return Block
     */
    public Block addBlock(ArrayList<Transaction> transactions) {
        Block block = Block.mineBlock(chain.get(chain.size() - 1), transactions);
        chain.add(block);
        return block;
    }

    public boolean isValidChain(List<Block> bc) {
        if (!bc.get(0).toString().equals(Block.genesis().toString())) {
            return false;
        }
        for (int i = 0; i < bc.size(); i++) {
            Block curtBlock = bc.get(i);
            Block prevBlock = bc.get(i - 1);
            String curtHash = Block.calculateHash(curtBlock.getTimeStamp(), curtBlock.getLastHash(), curtBlock.getDifficulty(),
                    curtBlock.getNonce(), curtBlock.getTransactions());
            if (!curtBlock.getLastHash().equals(prevBlock.getHash()) || !curtBlock.getHash().equals(curtHash)) {
                return false;
            }
        }
        return true;
    }

    public void replaceChain(List<Block> newChain) {
        if (newChain.size() < chain.size()) {
            return;
        } else if (!isValidChain(newChain)) {
            return;
        }
        chain = newChain;
    }


}


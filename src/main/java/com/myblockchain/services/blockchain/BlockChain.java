package com.myblockchain.services.blockchain;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionInput;
import com.myblockchain.model.TransactionOutput;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.BlockChainUtils;
import com.myblockchain.utils.PersistentUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
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
     * Return a wallet's UTXOs
     * @param walletAddress
     * @return Map
     */
    public Map<String, TransactionOutput> findWalletUTXOs(PublicKey walletAddress) {
        Map<String, TransactionOutput> allUTXOs = this.findAllUTXOs();
        Map<String, TransactionOutput> walletUTXOs = new HashMap<>();

        for(TransactionOutput txOutput : allUTXOs.values()) {
            if(BlockChainUtils.convertStringtoKey(txOutput.getRecipient()).equals(walletAddress)) {
                walletUTXOs.put(txOutput.getId(), txOutput);
            }
        }
        return walletUTXOs;
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
     * Get all transactions contained in block chain
     * @return
     */
    public List<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for(Block block : this.getChain()) {
            transactions.addAll(block.getTransactions());
        }
        return transactions;
    }

    /**
     * Add a new mined block into block chain
     * @param transactions
     * @return Block
     */
    public Block addBlock(ArrayList<Transaction> transactions) {
        Block.minable = true;
        Block block = Block.mineBlock(chain.get(chain.size() - 1), transactions);
        if (block != null) {
            chain.add(block);
        }
        return block;
    }

    /**
     * Validate all blocks in this block chain
     * @param bc
     * @return
     */
    public boolean isValidChain(List<Block> bc) {
        for (int i = 1; i < bc.size(); i++) {
            Block curtBlock = bc.get(i);
            Block prevBlock = bc.get(i - 1);
            String curtHash = Block.calculateHash(curtBlock.getTimeStamp(), curtBlock.getLastHash(), curtBlock.getDifficulty(),
                    curtBlock.getNonce(), curtBlock.getMerkleRoot());
            if (!curtBlock.getLastHash().equals(prevBlock.getHash()) || !curtBlock.getHash().equals(curtHash)) {
                return false;
            }
        }
        return true;
    }

    /**
     * replace current chain with new chain
     * @param newChain
     */
    public boolean replaceChain(List<Block> newChain) {
        if (newChain.size() < chain.size()) {
            return false;
        } else if (!isValidChain(newChain)) {
            return false;
        }
        chain = newChain;
        Block.minable = false;
        return true;
    }

    /**
     * Get the last block
     * @return Block
     */
    public Block getLastBlock() {
        return this.chain.get(chain.size() - 1);
    }

}


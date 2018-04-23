package com.myblockchain.model;


import com.myblockchain.utils.BlockChainUtils;
import com.myblockchain.utils.Configuration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Component
public class Block {
    private long timeStamp; //Time of this block generated
    private String lastHash; //Hash of previous block
    private String hash; //Hash of this block
    private long nonce; //Counter of POW
    private int difficulty; //Difficulty of generating hash
    private List<Transaction> transactions; //Transactions
    private String merkleRoot;

    public static boolean minable;

    public Block(long timeStamp, String lastHash, String hash, long nonce,
                 int difficulty, List<Transaction> transactions, String merkleRoot) {
        this.timeStamp = timeStamp;
        this.lastHash = lastHash;
        this.hash = hash;
        this.nonce = nonce;
        this.difficulty = difficulty;
        this.transactions = transactions;
        this.merkleRoot = merkleRoot;
    }

    // TODO: consider the security of calling this method
    // TODO: how to put nonce and difficulty into this without hardcode but in configure way

    /**
     * Create genesis block
     * @return Block
     */
    public static Block genesis() {
        long timeStamp = new Date().getTime();
        String lastHash = "";
        int difficulty = Configuration.DIFFICULTY;
        long nonce = 0;
        ArrayList<Transaction> transactions = new ArrayList<>();
        String merkleRoot = "";
        String hash = calculateHash(timeStamp, lastHash, difficulty, nonce, merkleRoot);

        return new Block(timeStamp, lastHash, hash, nonce, difficulty, transactions, merkleRoot);
    }

    /**
     * Mining block
     * @param lastBlock
     * @param transactions
     * @return Block
     */
    public static Block mineBlock(Block lastBlock, List<Transaction> transactions) {
        long timeStamp = new Date().getTime();
        long nonce = 0;
        String lastHash = lastBlock.getHash();
        int difficulty = lastBlock.getDifficulty();
        String merkleRoot = Block.getMerkleRoot(transactions);
        String hash = "";
        while (hash.length() < difficulty || !hash.substring(0, difficulty).equals(new String(new char[difficulty]).replace('\0', '0'))) {
            timeStamp = new Date().getTime();
            difficulty = Block.adjustDifficulty(lastBlock, timeStamp);
            hash = Block.calculateHash(timeStamp, lastHash, difficulty, nonce, merkleRoot);
            nonce++;
            if (!minable) {
                return null;
            }
        }
        return new Block(timeStamp, lastHash, hash, nonce - 1, difficulty, transactions, merkleRoot);
    }

    /**
     * Adjust mining difficulty according to control the mining speed
     * @param lastBlock
     * @param currentTime
     * @return
     */
    //TODO: Fix the hardcoded mine rate
    public static int adjustDifficulty(Block lastBlock, long currentTime) {
        int difficulty = lastBlock.getDifficulty();
        difficulty = lastBlock.getTimeStamp() + Configuration.MINE_RATE > currentTime ? ++difficulty : --difficulty;
        return difficulty;
    }

    /**
     * Calculate SHA256 of block
     * @param timeStamp
     * @param lastHash
     * @param difficulty
     * @param nonce
     * @param merkleRoot
     * @return String
     */
    public static String calculateHash(long timeStamp, String lastHash, long difficulty, long nonce, String merkleRoot) {
        return BlockChainUtils.getSHA256Hash(Long.toString(timeStamp) +
                                        lastHash +
                                        Long.toString(difficulty) +
                                        Long.toString(nonce) +
                                        merkleRoot);
    }

    /**
     * Get the merkle root by hashing all transactions
     * @return
     */
    public static String getMerkleRoot(List<Transaction> transactions) {
        String[] txArrays = new String[transactions.size()];
        for (int i = 0; i < transactions.size(); i++) {
             txArrays[i]= transactions.get(i).getTransactionId();
        }
        return new MerkleTree(txArrays).getRoot().getHash();
    }


    @Override
    public String toString() {
        return "Block -" +
                "\n timeStamp: " + String.valueOf(timeStamp) +
                "\n lastHash: " + String.valueOf(lastHash) +
                "\n hash: " + String.valueOf(hash) +
                "\n nonce: " + String.valueOf(nonce) +
                "\n difficulty: " + String.valueOf(difficulty) +
                "\n transactions: " + String.valueOf(transactions) +
                "\n merkleRoot: " + String.valueOf(merkleRoot) + "\n";
    }
}

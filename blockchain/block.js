// Use ES6 Standard
const SHA256 = require('crypto-js/sha256');
const {
    DIFFICULTY,
    MINE_RATE
} = require('../config');

class Block {
    // this means a unique object of this class
    constructor(timestamp, lastHash, hash, data, nonce, difficulty) {
        this.timestamp = timestamp;
        this.lastHash = lastHash;
        this.hash = hash;
        this.data = data;
        this.nonce = nonce;
        // if nothing is specified, use the default value(for genesis block)
        this.difficulty = difficulty || DIFFICULTY;
    }

    // usually used in debugging, give the image what the object looks like
    // use subString() function merely cause we do not need a long hash value
    toString() {
        return `Block -
            Timestamp : ${this.timestamp}
            LastHash  : ${this.lastHash.substring(0,10)}
            Hash      : ${this.hash.substring(0,10)}
            Nonce     : ${this.nonce}
            Difficulty: ${this.difficulty}
            data      : ${this.data}`;
    }

    // the 1st block do not have prev block, thus create one
    static genesis() {
        return new this('Genesis time', '-----', 'f1r57-h46h', [], 0, DIFFICULTY);
    }

    // to create a new block, we need lastBlock's hash, and the data we want to be included in new block
    // have proof-of-work system, miner need to do some computation to generate a new block
    // More peers, faster, thus need to have dynamic difficulty, to keep the same rate
    static mineBlock(lastBlock, data) {
        const lastHash = lastBlock.hash;
        let {
            difficulty
        } = lastBlock;
        let timestamp;
        let nonce = 0;
        let hash;
        do {
            timestamp = Date.now();
            nonce++;
            difficulty = Block.adjustDifficulty(lastBlock, timestamp);
            hash = Block.hash(timestamp, lastHash, data, nonce, difficulty);
        } while (hash.substring(0, difficulty) !== '0'.repeat(difficulty));

        return new this(timestamp, lastHash, hash, data, nonce, difficulty);
    }

    /**
     * Block hash
     * Should be generated from timestamp, lastHash and stored data
     * Algorithm: SHA-256  (32 bytes)
     * Features:
     * 1) Unique hash for unique data inputs 
     * 2) One way, data -------> hash, impossible: hash -----> data
     */
    static hash(timestamp, lastHash, data, nonce, difficulty) {
        return SHA256(`${timestamp}${lastHash}${data}${nonce}${difficulty}`).toString();
    }

    static blockHash(block) {
        const {
            timestamp,
            lastHash,
            data,
            nonce,
            difficulty
        } = block;
        return Block.hash(timestamp, lastHash, data, nonce, difficulty);
    }

    static adjustDifficulty(lastBlock, currentTime) {
        let {
            difficulty
        } = lastBlock;
        difficulty = currentTime - lastBlock.timestamp > MINE_RATE ?
            --difficulty : ++difficulty;

        return difficulty;
    }
}
module.exports = Block;
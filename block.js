// Use ES6 Standard
const SHA256 = require('crypto-js/sha256');


class Block {
    // this means a unique object of this class
    constructor(timeStamp, lastHash, hash, data) {
        this.timeStamp = timeStamp;
        this.lastHash = lastHash;
        this.hash = hash;
        this.data = data;
    }

    // usually used in debugging, give the image what the object looks like
    // use subString() function merely cause we do not need a long hash value
    toString() {
        return `Block -
            Timestamp: ${this.timeStamp}
            LastHash : ${this.lastHash.substring(0,10)}
            Hash     : ${this.hash.substring(0,10)}
            data     : ${this.data}
        `;
    }

    // the 1st block do not have prev block, thus create one
    static genesis() {
        return new this('Genesis time', '-----', 'f1r68kd', []);
    }

    // to create a new block, we need lastBlock's hash, and the data we want to be included in new block
    static mineBlock(lastBlock, data) {
        const timestamp = Date.now();
        const lastHash = lastBlock.hash;
        const hash = Block.hash(timestamp, lastHash, data);
        return new this(timestamp, lastHash, hash, data);
    }

    /**
     * Block hash
     * Should be generated from timestamp, lastHash and stored data
     * Algorithm: SHA-256  (32 bytes)
     * Features:
     * 1) Unique hash for unique data inputs
     * 2) One way, data -------> hash, impossible hash -----> data
     */
    static hash(timestamp, lastHash, data) {
        return SHA256(`${timestamp}${lastHash}${data}`).toString();
    }
}
module.exports = Block;
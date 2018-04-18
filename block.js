// Use ES6 Standard
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
}
module.exports = Block;
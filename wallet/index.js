const { INITIAL_BALANCE } = require('../config');
const ChainUtil = require('../chain-util');

class Wallet {
    constructor () {
        this.balance = INITIAL_BALANCE;
        this.keyPair = ChainUtil.genKeyPair();
        this.publicKey = this.keyPair.getPublic().encode('hex');
    }

    toString() {
        return `Wallet -
            publicKey:  ${this.publicKey.toString()}
            balance  :  ${this.balance.toString()}`
    }

    // to generate the signature(private key + data)
    sign(dataHash) {
        return this.keyPair.sign(dataHash);
    }
}

module.exports = Wallet;
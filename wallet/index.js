const { INITIAL_BALANCE } = require('../config');
const ChainUtil = require('../chain-util');
const Transaction = require('./transaction');

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

    // wallet can create transactions, and give it to the transaction pool
    createTransaction(recipient, amount, transactionPool) {
        if (amount > this.balance) {
            console.log(`Amount: ${amount} exceeds current balance: ${this.balance}`);
            return;
        }
        // check if a transaction attributed to the sender already exists in the pool
        let transaction = transactionPool.existingTransaction(this.publicKey);

        if(transaction) {
            transaction.update(this, recipient, amount);
        } else {
            transaction = Transaction.newTransaction(this, recipient, amount);
            transactionPool.updateOrAddTransaction(transaction);
        }
        return transaction;
    }
}

module.exports = Wallet;
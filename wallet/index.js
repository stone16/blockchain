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
    createTransaction(recipient, amount, blockchain, transactionPool) {
        this.balance = this.calculateBalance(blockchain);

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

    calculateBalance(blockchain) {
        let balance = this.balance;
        let transactions = [];
        blockchain.chain.forEach(block => block.data.forEach(transaction => 
            transactions.push(transaction)));

        const walletInputTransactions = transactions.filter(transaction => 
            transaction.input.address === this.publicKey);

        let startTime = 0;

        if(walletInputTransactions.length > 0) {
            const recentInputTransaction = walletInputTransactions.reduce(
                (prev, current) => prev.input.timestamp > current.input.timestamp ?
                    prev: current
            );
            balance = recentInputTransaction.outputs.find(
                output => output.address === this.publicKey).amount;
            startTime = recentInputTransaction.input.timestamp;
        }

        transactions.forEach(transaction => {
            if(transaction.input.timestamp > startTime) {
                transaction.outputs.find(output => {
                        if(output.address === this.publicKey) {
                            balance += output.amount;
                        }
                    });
            }
        });
        return balance;

    }

    static blockchainWallet() {
        const blockchainWallet = new this();
        blockchainWallet.address = 'blockchain-wallet';
        return blockchainWallet; 
    }
}

module.exports = Wallet;
// this class ties the blockchain with the transaction pool
// each miner have their own wallet, containing their balance
// p2pServer, thus they could communicate with other end points in the network
// const Blockchain = require('../blockchain');
// const TransactionPool = require('../wallet/transaction-pool');
const Wallet = require('../wallet');
const Transaction = require('../wallet/transaction');
// const P2pServer =require('./p2p-server');

class Miner {
    constructor(blockchain, transactionPool, wallet, p2pServer) {
        this.blockchain = blockchain;
        this.transactionPool = transactionPool;
        this.wallet = wallet;
        this.p2pServer = p2pServer;
    }

    /**
     * 1) Grab transactions from the pool
     * 2) Create a block whose data contain these transactions
     * 3) tell the p2pServer to synchronize the chain 
     * 4) clear the blockchain since they are in blockchain now
     */
    mine() {
        // get the valid data(transactions) from transaction pool
        const validTransactions = this.transactionPool.validTransactions();
        // include a reward for the miner
        validTransactions.push(
            Transaction.rewardTransaction(this.wallet, Wallet.blockchainWallet())
        );
        // create a block consisting of valid transactions
        const block = this.blockchain.addBlock(validTransactions);
        // synchronize chains in the p2p server
        this.p2pServer.syncChains();
        // clear their transaction pool
        this.transactionPool.clear();
        // and broadcast to other miners to clear their transaction pools as well
        this.p2pServer.broadcastClearTransactions();

        return block;

    }
}

module.exports = Miner;
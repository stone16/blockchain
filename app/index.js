const express = require('express'); 
const bodyParser = require('body-parser');
const Blockchain = require('../blockchain');
const P2pServer = require('./p2p-server');
const Wallet = require('../wallet');
const path = require('path');
// the transactionPool need to be shared by all the nodes
const TransactionPool = require('../wallet/transaction-pool');
const Miner = require('./miner');
// define the port we want to listen to
// process.env.HTTP_PORT means user can choose their preferred port on command line
const HTTP_PORT = process.env.HTTP_PORT || 3001;

// create an express application
const app = express();
const bc = new Blockchain();
const wallet = new Wallet();
const tp = new TransactionPool();
const p2pServer = new P2pServer(bc, tp);
const miner = new Miner(bc, tp, wallet, p2pServer);

app.use(express.static(__dirname + '/View'));
app.use(express.static(__dirname + '/Script'));
// allow us to receive json with post request 
app.use(bodyParser.json());

// return the blocks of current blockchain
app.get('/blocks',(req, res) => {
    res.sendFile(path.join(__dirname + '/../View/index.html'));
    // res.json(bc.chain);
});

app.post('/mine', (req, res) => {
    // the body parser help us the decode the body
    const block = bc.addBlock(req.body.data);
    // see the new block
    console.log(`New block added: ${block.toString()}`);

    p2pServer.syncChains();
    
    // respond back the updated chain of blocks that includes the new added one
    res.redirect('/blocks');
});

// let every user can see all the transactions
app.get('/transactions', (req, res) => {
    res.json(tp.transactions);
});

// create a transaction with user's wallet 
app.post('/transact', (req, res) => {
    const {recipient, amount } = req.body;
    const transaction = wallet.createTransaction(recipient, amount, bc ,tp);
    p2pServer.broadcastTransaction(transaction);
    res.redirect('/transactions');
});

app.get('/mine-transactions', (req, res) => {
    const block = miner.mine();
    console.log(`New block added: ${block.toString()}`);
    res.redirect('/blocks');
});

// to expose your own public key
app.get('/public-key', (req, res) => {
    res.json({ publicKey: wallet.publicKey });
});

// run the application 
app.listen(HTTP_PORT, () => {
    console.log(`Listening on port ${HTTP_PORT}`);
});

p2pServer.listen();

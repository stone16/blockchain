/**
 * Every miner mines on their own blockchain, and they need to update thier chain properly. 
 */

// access the websocket module
const Websocket = require('ws');
// give user option to set their own port
const P2P_PORT = process.env.P2P_PORT || 5001;
//check if a peer variable has been declared, contains a list of websocket address
// HTTP_PORT=3002 P2P_PORT=5003 ws://localhost:5001,ws://localhost:5002 npm run dev
const peers = process.env.PEERS ? process.env.PEERS.split(',') : [];

class P2pServer {
    constructor(blockchain) {
        this.blockchain = blockchain;
        //contain a list of connected websocket servers 
        this.sockets = [];
    }
    //start the server, create the first web socket  generate the original one
    listen() {
        const server = new Websocket.Server({
            port: P2P_PORT
        });
        //event listener, listen for incoming type of messages, send to the web socket server
        server.on('connection', socket => this.connectSocket(socket));
        //lat the other recognized as peer connect to the original one
        this.connectToPeers();
        console.log(`Listening for peer-to-peer connections on: ${P2P_PORT}`);
    }
    // make connections 
    connectToPeers() {
        peers.forEach(peer => {
            //ws://localhost:5001
            const socket = new Websocket(peer);
            socket.on('open', () => {
                this.connectSocket(socket);
            });
        });
    }

    connectSocket(socket) {
        this.sockets.push(socket);
        console.log('Socket connected');
        // make sockets ready to receive message event
        this.messageHandler(socket);
        // send the message that contains blockchain information
        this.sendChain(socket);
    }
    // work as an event listener to handle messages
    messageHandler(socket) {
        socket.on('message', message => {
            const data = JSON.parse(message);

            this.blockchain.replaceChain(data);
        });
    }

    sendChain(socket) {
        socket.send(JSON.stringify(this.blockchain.chain));
    }

// send the updated blockchain of current instance to all socket peers 
    syncChains() {
        this.sockets.forEach(socket => {
            this.sendChain(socket);
        })
    }
}

module.exports = P2pServer;
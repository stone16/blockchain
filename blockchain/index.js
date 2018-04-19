/**
 * A chain of block
 * support add a block, based on last block,
 * create a link across blocks
 * 
 */

 const Block = require('./block');

 class Blockchain {
     constructor() {
        // give this blockchain a genesis block, can be started 
        this.chain = [Block.genesis()];
     }

    //  add a new block to the chain
     addBlock(data) {
        const block = Block.mineBlock(this.chain[this.chain.length - 1], data);
        this.chain.push(block);

        return block;
     }

     /**
      * The blockchain will have multiple contributors, thus need to validate the chain
      * Which means a miner may add a new block, and want to add it to blockchain, this 
      * operation need to be accepted by others
      * 
      * ----> Longer chain
      * To resolve the conflict when several miners try to add blocks to blockchain
      * ----> Check the hash value
      * To check if data has been tampered. 
      */


      isValidChain(chain) {
        //check if the chain starts at proper genesis block  object can not be equal in js  ===> stringfy
        if (JSON.stringify(chain[0]) !== JSON.stringify(Block.genesis())) {
            return false;
        }

        for (let i = 1; i < chain.length; i++) {
            const block = chain[i];
            const lastBlock = chain[i-1];
            // check the hash
            // check the hash itself, to see if data has been tampered
            if(block.lastHash !== lastBlock.hash || 
                block.hash !== Block.blockHash(block)) {
                return false;
            }
        }
        return true;
      }

      replaceChain(newChain) {
          // check the length of the chain, make comparison
          if (newChain.length <= this.chain.length) {
              console.log('Recieved chain is not longer than the current chain');
              return;
          } else if (!this.isValidChain(newChain)) {
              console.log('The received chain is not valid');
              return;
          } 
          console.log('Replacing blockchain with the new chain.');

          this.chain = newChain;

      }
 }

 module.exports = Blockchain;
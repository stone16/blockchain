package com.myblockchain.controller;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionOutput;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.miner.Miner;
import com.myblockchain.services.network.P2pServer;
import com.myblockchain.services.wallet.Wallet;
import com.myblockchain.utils.BlockChainUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;

@RestController
public class BlockChainController {

    @Autowired
    private BlockChain blockChain;

    @Autowired
    private Wallet wallet;

    @Autowired
    private P2pServer p2pServer;

    @Autowired
    private Miner miner;

    @Autowired
    TransactionPool transactionPool;

    @Autowired
    public BlockChainController(BlockChain blockChain, Wallet wallet, P2pServer p2pServer, Miner miner, TransactionPool transactionPool) {
        this.blockChain = blockChain;
        this.wallet = wallet;
        this.p2pServer = p2pServer;
        this.miner = miner;
        this.transactionPool = transactionPool;
    }

    /**
     * Index page
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome() {
        return "Welcome to BlockChain World!";
    }

    /**
     * Show all blocks
     * @return
     */
    @RequestMapping(value = "/blocks", method = RequestMethod.GET)
    @ResponseBody
    public List<Block> showBlocks() {
        return blockChain.getChain();
    }

    /**
     * Show all transactions in transaction pool
     * @return
     */
    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TransactionOutput> showTransactionPool() {
        return wallet.getUTXOs();
    }

    /**
     * Launch a new transaction
     * @return
     */
    @RequestMapping(value = "/transact", method = RequestMethod.POST)
    @ResponseBody
    public String launchTransaction(@RequestBody Map<String, Object> payload) {
        Float amount = Float.valueOf((int)payload.get("amount"));
        System.out.println(amount);
        String middle = payload.get("recipient").toString();
        String[] recipient = { middle.substring(1, middle.indexOf(',')),
                middle.substring(middle.indexOf(',') + 2, middle.length() - 1)};
        Transaction newTransaction = wallet.createTransaction(recipient, amount, transactionPool);
        p2pServer.broadcastTransaction(newTransaction);
        return "redirect:transactions";
    }


    /**
     * Show all user's address
     * @return
     */
    @RequestMapping(value = "/public-key", method = RequestMethod.GET)
    @ResponseBody
    public String[] showAddress() {
        return BlockChainUtils.convertKeytoString(wallet.getPublicKey());
    }

    /**
     * Mine block that contains transactions
     * @return
     */
    @RequestMapping(value = "/mine-transactions", method = RequestMethod.GET)
    @ResponseBody
    public Block mineBlock() {
        Block block = miner.mine();
        wallet.updateUTXOsFromMinnedBlock(block);
        return block;
    }


}

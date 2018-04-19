package com.myblockchain.controller;

import com.myblockchain.services.blockchain.BlockChain;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockChainController {

    private BlockChain blockChain;

    @Autowired
    public BlockChainController(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @RequestMapping("/")
    public String hello() {
        return "hello";
    }

    /**
     * Show block chain by reading the block chain JSON file
     * @return
     */
    @RequestMapping(value = "/blockchain", method = RequestMethod.POST)
    public void showBlockChain() {

    }
}

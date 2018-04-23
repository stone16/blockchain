package com.myblockchain.utils;

import com.myblockchain.services.blockchain.BlockChain;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bigtony on 4/18/18.
 */
public class PersistentUtilsTest {
    @Test
    public void readBlockChain() throws Exception {
        BlockChain blockChain = PersistentUtils.readBlockChain("test.json");
        System.out.println(blockChain.toString());
    }

    @Test
    public void storeBlockChain() throws Exception {
        BlockChain blockChain = BlockChain.newBlockchain();
        PersistentUtils.storeBlockChain(blockChain);
    }

}
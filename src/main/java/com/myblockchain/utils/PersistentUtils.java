package com.myblockchain.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblockchain.services.blockchain.BlockChain;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.io.File;

public class PersistentUtils {

    public static void storeBlockChain(BlockChain blockchain) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("BlockChain.json"), blockchain);
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(PersistentUtils.class);
            logger.info(e.getMessage());
        }
    }

    public static BlockChain readBlockChain(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(filePath), BlockChain.class);
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(PersistentUtils.class);
            logger.info(e.getMessage());
            return null;
        }
    }
}

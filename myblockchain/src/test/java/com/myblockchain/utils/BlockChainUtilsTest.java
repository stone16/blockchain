package com.myblockchain.utils;

import com.myblockchain.services.wallet.Wallet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PublicKey;

import static org.junit.Assert.*;

/**
 * Created by bigtony on 4/20/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BlockChainUtilsTest {
    Wallet sender;
    @Before
    public void setUp() throws Exception {
        sender = new Wallet();
    }


}
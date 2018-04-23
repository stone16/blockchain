package com.myblockchain.services.wallet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletTest {



    @Test
    public void toStringTest() throws Exception {
        Wallet w = new Wallet();
        String s = w.toString();
        Assert.assertNotNull(s);
        System.out.println(s);
    }


}
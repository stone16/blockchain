package com.myblockchain.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bigtony on 4/19/18.
 */
public class MerkleTreeTest {
    @Test
    public void getRoot() throws Exception {
        String[] testString = new String[5];
        testString[0] = "Hello0";
        testString[1] = "Hello1";
        testString[2] = "Hello2";
        testString[3] = "Hello3";
        testString[4] = "Hello4";
        System.out.println(new MerkleTree(testString).getRoot().getHash());
    }

}
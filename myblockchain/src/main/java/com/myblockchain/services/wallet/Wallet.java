package com.myblockchain.services.wallet;


import lombok.Data;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * Wallet contains two fields: PublicKey and PrivateKey
 */
@Data
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private int test;

    // call the constructor to generate keypair
    public Wallet() {
        genKeyPair();
    }

    /**
     * Override toString() method
     */
    @Override
    public String toString() {
        return "Wallet -" +
                "\n publicKey: " + publicKey.toString() +
                "\n privateKey: " + privateKey.toString();
    }

    /**
     * Using Elliptic Curve Algorithm to generate KeyPair
     */
    private void genKeyPair() {
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "SunEC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp192k1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            System.out.println(e);
        }
    }




}

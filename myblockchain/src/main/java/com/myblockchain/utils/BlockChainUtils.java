package com.myblockchain.utils;


import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.util.Base64;
import java.util.UUID;

public class BlockChainUtils {
    /**
     * Returns a hexadecimal encoded SHA-256 hash for the input String.
     * @param data
     * @return
     */
    public static String getSHA256Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));

            return DatatypeConverter.printHexBinary(hash);
        } catch (Exception NoSuchAlgorithmException) {
            throw new RuntimeException(NoSuchAlgorithmException);
        }
    }

    /**
     * Returns a UUID(type 4) string
     * @return
     */
    public static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Using privateKey and input data to generate ECDSA signature using Elliptic Curve algorithm
     * @param privateKey
     * @param data
     * @return realSig
     */
    public static byte[] applySignature(PrivateKey privateKey, String data) {
        Signature signature;
        byte[] realSig;
        try {
            signature = Signature.getInstance("ECDSA", "BC");
            signature.initSign(privateKey);
            byte[] strByte = data.getBytes();
            signature.update(strByte);
            realSig = signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realSig;
    }

    /**
     * Verify the signature is valid or not
     * @param publicKey
     * @param data
     * @param signature
     * @return boolean
     */
    public static boolean verifySignature(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return a string from key
     * @param key
     * @return String
     */
    public static String getStringFromKey(Key key) {

        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}

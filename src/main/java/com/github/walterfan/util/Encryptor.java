package com.github.walterfan.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Encryptor {
     
    private static byte[] IV_BYTES = "1234567890123456".getBytes();
    
    public static final String ENC_CBC_NOPADDING = "AES/CBC/NoPadding";
    
    private static final String ENC_ALGORITHM = "AES";

    private String algorithm = ENC_ALGORITHM;
    
    private static final int ENC_KEY_LEN = 16;
    

    public Encryptor() {
        
    }

    public Encryptor(String algorithm) {
        this.algorithm = algorithm;
    }

    public byte[] encode(byte[] bytes, byte[] kbytes) throws Exception {
       /* if(aesKey.getBytes().length % ENC_KEY_LEN != 0) {
            throw new Exception("invalid AES Key length(128, 192, or 256 bits)");
        }*/
        
        SecretKeySpec keySpec = new SecretKeySpec(kbytes, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        if(ENC_CBC_NOPADDING.equals(algorithm)) {
            AlgorithmParameterSpec paraSpec = new IvParameterSpec(IV_BYTES);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paraSpec);
        }
        else {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        }
        return cipher.doFinal(bytes);
    }

    public byte[] decode(byte[] encryptedBytes, byte[] kbytes) throws Exception {
    /*    if(aesKey.getBytes().length % ENC_KEY_LEN != 0) {
            throw new Exception("invalid AES Key length(128, 192, or 256 bits)");
        }*/
        Cipher cipher = Cipher.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(kbytes, algorithm);
        if(ENC_CBC_NOPADDING.equals(algorithm)) {
            AlgorithmParameterSpec paraSpec = new IvParameterSpec(IV_BYTES);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paraSpec);
        }
        else {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        }
        
        return cipher.doFinal(encryptedBytes);
    }

   public  byte[] makeKey() throws NoSuchAlgorithmException {
    	KeyGenerator keygen = KeyGenerator.getInstance(algorithm);    
    	SecretKey skey = keygen.generateKey();
    	byte[] raw = skey.getEncoded();
    	//SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
    	return raw;
    }
    /**
     * simple test codes
     * 
     * @param args
     *            no
     */
    public static void main(String[] args) throws Exception {
        String key = RandomUtils.getRandomChars(16);
        System.out.println("key = " + key);
        String pwd = "pass";
        
        Encryptor encoder = new Encryptor("AES/CBC/NoPadding");
        byte[] encryptPwd = encoder.encode(pwd.getBytes(),key.getBytes());
        System.out.println(pwd + "-->" + EncodeUtils.encodeBase64(encryptPwd));
        
    }

 
}

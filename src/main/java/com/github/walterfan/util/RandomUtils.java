package com.github.walterfan.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class RandomUtils {
	public static final String LETTERS 
    	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    	+ "abcdefghijklmnopqrstuvwxyz";

    public static final String NUMBERS = "0123456789";

    public static final String HEXS = "0123456789abcdef";
    
    public static final String CHARS ;

    public static final String ASCIICHARS ;
    
    private static SecureRandom rand = new SecureRandom();
    
    static {
    	CHARS = getASCIIChars();
    	ASCIICHARS = getASCIIChars(33, 127);
    }
    
    public static String getASCIIChars() {
    	return getASCIIChars(33,127);
    }
    
    public static String getASCIIChars(int begin, int end) {
    	if(begin < 0 || end > 255 || begin >= end) {
    		return "";
    	}
    	StringBuffer chars = new StringBuffer("");
    	for(int i = begin ; i < end ; i++){
    		char c = (char)i; 
    		chars.append(c);
    	}
    	return chars.toString();
    }
    
    public static String getRandomChars(String chars ,int size) {
        StringBuffer sb = new StringBuffer("");
        int nrand;

        for (int i = 0; i < size; i++) {
            nrand = Math.abs(rand.nextInt(chars.length()));
            sb.append(chars.charAt(nrand));
        }
        return sb.toString();
    }
    
    public static List<Object> randomSelect(List<Object> elements,int nCount) {
    	List<Object> results = new ArrayList<Object>(nCount);
        int nSize = elements.size();
    	int nrand;
        
        for (int i = 0; i < nCount; i++) {
            nrand = Math.abs(rand.nextInt(nSize));
            results.add(elements.get(nrand));
        }
        return results;
    }
    
    public static String getRandomHEX(int size) {
        return getRandomChars(HEXS, size);
    }
    
    public static String getRandomChars(int size) {
    	return getRandomChars(CHARS, size);
    }
    
    public static byte[] getRandomBytes(String chars ,int size) {
    	String str = getRandomChars(chars, size);
    	byte[] ret = new byte[size];
    	System.arraycopy(str.getBytes(), 0, ret, 0, size);
    	return ret;
    }
    
    public static String getRandomNumbers(int size) {
    	return getRandomChars(NUMBERS, size);
    }
    
    public int getRandomInteger(int min, int max) {
    	return 0;
    }
    
    public static String getRandomLetters(int size) {
    	return getRandomChars(LETTERS, size);
    }


    public static void main(String[] args) {
    	System.out.println("Random String: " + getRandomChars(8));
    	System.out.println("Random ASCII chars: " + getASCIIChars());
    }
}

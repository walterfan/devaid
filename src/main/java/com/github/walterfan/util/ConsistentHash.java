package com.github.walterfan.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;



interface HashFunction {
    Integer hash(String keyStr);
}

class MD5Hash implements HashFunction {

    public Integer hash(String keyStr) {
        byte[] bKey=mac("MD5", keyStr);
        return ((int) (bKey[3] & 0xFF) << 24)
                | ((int) (bKey[2] & 0xFF) << 16)
                | ((int) (bKey[1] & 0xFF) << 8)
                | (bKey[0] & 0xFF);
    }


    public static byte[] mac(String alga, String str) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(alga);
            return md.digest(str.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

    }

}

public class ConsistentHash<T> {
    private static Log logger = LogFactory.getLog(ConsistentHash.class);
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
                          Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }

    public T get(String keyStr) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(keyStr);

        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap
                    .firstKey();
        }
        return circle.get(hash);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for(Map.Entry<Integer, T> entry : circle.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        java.util.List<String> nodes = new java.util.ArrayList<String>(3);
        nodes.add("1");
        nodes.add("2");

        ConsistentHash<String> continuum = new ConsistentHash<String>(new MD5Hash(), 8, nodes);
        String strID = "18580";

        String node = continuum.get(strID);
        System.out.println("select node: " + node + " for " + strID);

        java.util.Map<String, Integer> statMap = testDistribution(continuum);
        System.out.println(statMap);

        continuum.add("3");
        node = continuum.get(strID);
        System.out.println("add node 3, select node: " + node + " for " + strID);

        java.util.Map<String, Integer> statMap1 = testDistribution(continuum);
        System.out.println(statMap1);

        continuum.remove("3");
        node = continuum.get(strID);
        System.out.println("remove node 3, select node: " + node + " for " + strID);
        java.util.Map<String, Integer> statMap2 = testDistribution(continuum);
        System.out.println(statMap2);
    }

    private static java.util.Map<String, Integer> testDistribution(
            ConsistentHash<String> continuum) {
        java.util.Map<String, Integer> statMap = new java.util.HashMap<String, Integer>();

        for(int i = 10000; i < 20000; i++) {
            String svr = continuum.get("server" + i);
            //System.out.println(i + ". server: " + svr);
            Integer cnt = statMap.get(svr);
            if(null == cnt) {
                statMap.put(svr, 1);
            } else {
                statMap.put(svr, cnt + 1);
            }
        }
        return statMap;
    }

}


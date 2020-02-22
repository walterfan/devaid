package com.github.walterfan.devaid.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @Author: Walter Fan
 * @Date: 6/1/2020, Mon
 **/
public class HashCodeTest {

    static enum FlowType {
        FLOW_A,
        FLOW_B

    }

    @Data
    @AllArgsConstructor
    static class Pair {
        private String key;
        private String value;


    }



    @Test(enabled=false)
    public void testValueOf() {
        FlowType aFlowType = FlowType.FLOW_A;//.valueOf("FlOW_A");
        assertEquals(aFlowType.name(), "FlOW_A");

        FlowType bFlowType = FlowType.valueOf("FlOW_B");

        assertEquals(aFlowType, FlowType.FLOW_A);
        assertEquals(bFlowType, FlowType.FLOW_B);

        //Exception occur
        //FlowType cFlowType = FlowType.valueOf("FlOW_C");


    }

    @Test
    public void testHashCode() {
        Pair pair1 = new Pair("a", "1");
        Pair pair2 = new Pair("b", "2");

        Pair pair3 = new Pair("a", "1");
        Pair pair4 = new Pair("b", "2");

        Set<Pair> set1 = new HashSet<>();
        set1.add(pair1);
        set1.add(pair2);

        HashSet<Pair> set2 = new HashSet<>();
        set2.add(pair3);
        set2.add(pair4);

        assertEquals(pair1.hashCode(), pair3.hashCode());

        assertEquals(set1.hashCode(), set2.hashCode());

        ArrayList<Pair> list1 = new ArrayList<>();
        list1.add(pair1);
        list1.add(pair2);

        assertNotEquals(list1.hashCode(), set2.hashCode());

    }
}

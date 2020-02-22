package com.github.walterfan;

import org.testng.annotations.Test;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @Author: Walter Fan
 * @Date: 13/11/2019, Wed
 **/
public class WalterTest {

    @Test
    public void testString() {


        int unusedCapacity = 21300;
        int totalCapacity = 22000;

        int percent = (int)(100 * (double)unusedCapacity/(double)totalCapacity);
        System.out.println("percent=" + percent);

        for(Locale locale: Locale.getAvailableLocales()) {
            System.out.println(locale.toString());
        }

        for(String tz: TimeZone.getAvailableIDs()) {
            System.out.println(tz);
        }
    }
}

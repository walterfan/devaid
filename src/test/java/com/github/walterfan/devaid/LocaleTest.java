package com.github.walterfan.devaid;

/**
 * Created by yafan on 14/6/2018.
 */


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleTest {

    @Test
    public void testLocalization() {
        doTest(new Locale("en", "US"));
        doTest(new Locale("de"));
        doTest(new Locale("de", "DE"));
    }

    private void doTest(Locale locale) {
        //final ResourceBundle bundle = ResourceBundle.getBundle("demo", locale);



        System.out.println("");

        System.out.println("Locale: " + locale.toString());

        System.out.println("Language: " + locale.getLanguage());

        System.out.println("country: " + locale.getCountry());
    }
}
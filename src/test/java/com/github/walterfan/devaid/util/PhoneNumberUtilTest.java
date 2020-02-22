package com.github.walterfan.devaid.util;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Slf4j
public class PhoneNumberUtilTest {

    @Test
    public void testIsNumberNsnMatch() {
        String phoneNumberOne = "+86055112345678";
        String phoneNumberTwo = "86055112345678";
        PhoneNumberUtil.MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(phoneNumberOne, phoneNumberTwo);
        log.info("matchType is {}", matchType);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NO_MATCH);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NOT_A_NUMBER);
        assertEquals(matchType , PhoneNumberUtil.MatchType.NSN_MATCH);



    }


    @Test
    public void testIsNumberShortMatch() {
        String phoneNumberOne = "+86055112345678";
        String phoneNumberTwo = "086(0551)1234-5678";
        PhoneNumberUtil.MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(phoneNumberOne, phoneNumberTwo);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NO_MATCH);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NOT_A_NUMBER);
        assertEquals(matchType , PhoneNumberUtil.MatchType.SHORT_NSN_MATCH);
    }

    @Test
    public void testGetCountryCode() {

        String strPhoneNumber = "+918061204395";
        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(strPhoneNumber, "US");
            log.info("phoneNumber.getCountryCode() is {}", phoneNumber.getCountryCode());
            assertTrue(phoneNumber.getCountryCode() == 91);
        } catch (NumberParseException e) {
            fail(e.getMessage());
        }


    }
}

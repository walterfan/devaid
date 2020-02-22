package com.github.walterfan.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.MatchType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberUtils {

    private static PhoneNumberUtil theInstance = PhoneNumberUtil.getInstance();

    public static PhoneNumber parsePhoneNumber(String phoneNumber) throws NumberParseException {

            Phonenumber.PhoneNumber parsedPhoneNumber = theInstance.parse(phoneNumber, null);
            return parsedPhoneNumber;

    }

}

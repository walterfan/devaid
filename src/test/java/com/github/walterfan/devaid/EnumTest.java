package com.github.walterfan.devaid;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * @Author: Walter Fan
 * @Date: 21/5/2019, Tue
 **/

enum IvrScriptResult {
    SUCCESS(0),
    FAILURE(1),
    TIMEOUT(2);

    private final int value;

    IvrScriptResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
@Slf4j
public class EnumTest {

    @Test
    public void testEnumValueOf() {
        Integer a = 1;
        IvrScriptResult ret = IvrScriptResult.valueOf("SUCCESS");
        log.info("ret = {}" , ret);

    }
}

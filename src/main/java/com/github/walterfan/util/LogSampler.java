package com.github.walterfan.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.BooleanSupplier;

@Slf4j
public class LogSampler implements BooleanSupplier {

    private static final ThreadLocal<Random> randomNumberGenerator = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new SecureRandom();
        }
    };

    private final int logPercentage;

    public LogSampler(int logPercentage) {
        this.logPercentage = logPercentage;
    }

    @Override
    public boolean getAsBoolean() {
        if(logPercentage >= 100) return true;
        if(logPercentage <= 0) return false;

        int randomNumber = randomNumberGenerator.get().nextInt(100) ;
        //log.debug("randomNumber: {}", randomNumber);
        return randomNumber < logPercentage;
    }

    public static void main(String args[]) {
        log.info("begin...");
        LogSampler logSampler = new LogSampler(10);
        for(int i=0;i<100;i++) {
            if(logSampler.getAsBoolean()) {
                log.info("write log {}", i);
            }
        }
    }
}


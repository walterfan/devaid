package com.github.walterfan.util.ratelimit;

/**
 * @Author: Walter Fan
 * @Date: 25/5/2019, Sat
 **/
public class LeakyBucket extends AbstractRateLimiter {

    private long nextAllowedTime;

    private final long REQUEST_INTERVAL_MILLIS;

    protected LeakyBucket(int maxRequestPerSec) {
        super(maxRequestPerSec);
        REQUEST_INTERVAL_MILLIS = 1000 / maxRequestPerSec;
        nextAllowedTime = System.currentTimeMillis();
    }

    @Override
    public boolean allow() {
        long curTime = System.currentTimeMillis();
        synchronized (this) {
            if (curTime >= nextAllowedTime) {
                nextAllowedTime = curTime + REQUEST_INTERVAL_MILLIS;
                return true;
            }
            return false;
        }
    }
}
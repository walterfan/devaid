package com.github.walterfan.util.ratelimit;

/**
 * @Author: Walter Fan
 * @Date: 25/5/2019, Sat
 **/
public abstract class AbstractRateLimiter implements RateLimiter {
    protected final int maxRequestPerSec;

    protected AbstractRateLimiter(int maxRequestPerSec) {
        this.maxRequestPerSec = maxRequestPerSec;
    }


}

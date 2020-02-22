package com.github.walterfan.util.ratelimit;

/**
 * @Author: Walter Fan
 * @Date: 24/5/2019, Fri
 *
 * @see "https://hechao.li/2018/06/25/Rate-Limiter-Part1"
 *
 **/
public interface RateLimiter {
    boolean allow();
}


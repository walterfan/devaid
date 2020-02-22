package com.github.walterfan.util.ratelimit;

import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Walter Fan
 * @Date: 26/5/2019, Sun
 **/
@Slf4j
public class FixedWindow implements RateLimiter {
    private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();
    private int maxRequestsPerSecond;
    private int windowSizeInMs;

    public FixedWindow(int maxReqPerSec, int windowSizeInMs) {
        this.maxRequestsPerSecond = maxReqPerSec;
        this.windowSizeInMs = windowSizeInMs;
    }
    @Override
    public boolean allow() {
        long windowKey = System.currentTimeMillis() / windowSizeInMs;
        windows.putIfAbsent(windowKey, new AtomicInteger(0));
        //log.debug("counter of {} --> {}", windowKey,  windows.get(windowKey));
        return windows.get(windowKey).incrementAndGet() <= maxRequestsPerSecond;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for(Map.Entry<Long, AtomicInteger> entry:  windows.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" --> ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        FixedWindow fixedWindow = new FixedWindow(10, 1000);
        for(int i=0;i<20;i++) {
            boolean ret = fixedWindow.allow();
            Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
            if(!ret)
                log.info("{}, ret={}", i, ret);
        }

        log.info(fixedWindow.toString());
        System.out.println(System.currentTimeMillis()/1000);
    }

}

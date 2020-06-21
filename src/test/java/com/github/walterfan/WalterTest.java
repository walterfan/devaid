package com.github.walterfan;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @Author: Walter Fan
 * @Date: 13/11/2019, Wed
 **/
@Slf4j
public class WalterTest {

    @Data
    static class Apdexes {
        long timestamp;
        int timeslotDuration;
        Map<Long, Map<String, String>> timeslots = new HashMap<>();

    }

    @Test
    public void testString() throws IOException {

        Apdexes apdexes = new Apdexes();
        apdexes.setTimestamp(System.currentTimeMillis());
        apdexes.setTimeslotDuration(60);
        apdexes.getTimeslots().put(System.currentTimeMillis(), ImmutableMap.of("10.224.2.1", "1,3,3,4", "10.224.2.2", "10,30,30,40"));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(apdexes);
        log.info(jsonStr);

        Apdexes apdexes2 = objectMapper.readValue(jsonStr, Apdexes.class);

        log.info("apdexes2:{}", apdexes2);
    }

    @Test
    public void testInstant() {

        long timestamp = System.currentTimeMillis();
        log.info("{}, {}", timestamp, timestamp/60000);
        log.info("{}, {}", timestamp/1000, timestamp/1000/60);
        log.info("{}", Instant.now().toString());
    }

    @Test
    public void testCache() {
             long ttl = 0;
        Cache<String, String> cache = CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .expireAfterWrite(ttl, TimeUnit.SECONDS)
                    .build();
        cache.put("a", "a");
        assertTrue(cache.getIfPresent("a") == null);

        double[] arr = {1.0, 0.0, 0.223424, 0.245322};
        for(double num: arr) {
            log.info("num: {}", String.format("%.3f", num));
        }
    }
}

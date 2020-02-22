package com.github.walterfan.devaid;

import com.google.common.util.concurrent.Uninterruptibles;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@Slf4j
@Data
class Caller {

    private String phoneNumber;

    public Executor executor = Executors.newFixedThreadPool(10);

    public void onSuccess(String resp) {
        log.info("call success, resp is {}", resp);
    }

    public Void onError(Throwable exp) {
        log.info("error: ", exp);
        return null;
    }

    public void writeMetrics(Void nothing) {
        log.info("metrics: sent call request");
    }

    public String sendReqeust() {
        log.info("send request to call  {}", phoneNumber);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        return "success";

    }
}

@Slf4j
public class CompletableFutureTest {


    @Test
    public void testFuture() {


        Caller caller = new Caller();
        caller.setPhoneNumber("13011166888");

        CompletableFuture<String>  promise = CompletableFuture.supplyAsync(caller::sendReqeust);
        promise.thenAccept(strResp -> caller.onSuccess(strResp))
                .exceptionally(exp ->  caller.onError(exp) )
                .thenAccept(caller::writeMetrics);
        log.info("need not wait here, just for test to sleep 2 seconds");

        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
    }
}



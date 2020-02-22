package com.github.walterfan.util.algorithm;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @Author: Walter Fan
 * @Date: 21/2/2020, Fri
 **/
@Slf4j
public class SortDemo {
    private static <T> void sortAndWatch(List<Poker.Card> cards1, BiConsumer<List<Poker.Card>, Comparator<Poker.Card>> biconsumer) {
        //cards1.stream().forEach(x -> log.info("{}", x));
        Stopwatch stopwatch = Stopwatch.createStarted();
        biconsumer.accept(cards1, new Poker.CardComparator());

        stopwatch.stop(); // optional
        log.info("--- sorted list ---");
        cards1.stream().forEach(x -> log.info("{}", x));

        long millis = stopwatch.elapsed(MILLISECONDS);
        log.info("{} milliseconds spent: {}" , millis, stopwatch); // formatted string like "12.3 ms"

    }


    public static void main(String[] args) {
        log.info("--- bubble sort  ---");
        List<Poker.Card> cards1 = Poker.createCardList(2);
        sortAndWatch(cards1, (t,u) -> SortUtils.bubbleSort(t, u));
        log.info("--- insert sort ---");
        List<Poker.Card> cards2 = Poker.createCardList(2);
        sortAndWatch(cards2, (t,u) -> SortUtils.insertSort(t, u));

        log.info("--- insert sort ---");
        Comparator<Integer> integerComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };

        List<Integer> numbers = Arrays.asList(5,2,4,6,1,3);
        log.info("* to sort numbers: {}", numbers.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        SortUtils.insertSort(numbers, integerComparator);
        log.info("* sorted numbers: {}", numbers.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }
}

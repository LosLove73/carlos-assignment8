package com.coderscampus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataService {
    private final Assignment8Starter assignment8;
    private final ExecutorService executor;
    private final List<Integer> allNumbers;

    public DataService() {
        this.assignment8 = new Assignment8Starter();
        this.executor = Executors.newFixedThreadPool(75); // Further increased thread pool for faster execution
        this.allNumbers = Collections.synchronizedList(new ArrayList<>());
    }

    public void processData() {
        List<CompletableFuture<Void>> futures = IntStream.range(0, 1000)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    List<Integer> numbersBatch = assignment8.getNumbers();
                    if (!numbersBatch.isEmpty()) {
                        allNumbers.addAll(numbersBatch);
                    }
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        countOccurrences();
        executor.shutdown();
    }

    private void countOccurrences() {
        Map<Integer, Long> countMap = allNumbers.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));

        IntStream.rangeClosed(1, 10)
                .forEach(i -> System.out.println(i + "=" + countMap.getOrDefault(i, 0L)));
    }
}
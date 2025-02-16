package com.coderscampus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DataService {
    private final Assignment8Starter assignment8;
    private final ExecutorService executor;
    private final List<Integer> allNumbers;

    public DataService() {
        this.assignment8 = new Assignment8Starter();
        this.executor = Executors.newCachedThreadPool(); // Auto-scales threads more efficient than using a fixedThreadPool.
        this.allNumbers = Collections.synchronizedList(new ArrayList<>());
    }

    public void processData() {
        List<CompletableFuture<List<Integer>>> tasks = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            CompletableFuture<List<Integer>> task = CompletableFuture.supplyAsync(() -> assignment8.getNumbers(), executor);
            tasks.add(task);
        }

        // Collect results after all tasks finish
        List<Integer> allFetchedNumbers = tasks.stream()
            .map(CompletableFuture::join) // Waits for each future to complete
            .flatMap(List::stream) // Flattens the lists into a single list
            .collect(Collectors.toList());

        allNumbers.addAll(allFetchedNumbers); // Add safely after processing

        countOccurrences();
        executor.shutdown();
    }


    private void countOccurrences() {
        Map<Integer, Long> countMap = allNumbers.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));

        for (int i = 1; i <= 10; i++) { // Standard for-loop instead of IntStream
            System.out.println(i + "=" + countMap.getOrDefault(i, 0L));
        }
    }
}

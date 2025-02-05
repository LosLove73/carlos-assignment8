package com.coderscampus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CLAssignment8 {
    private List<Integer> numbers = null;
    private AtomicInteger i = new AtomicInteger(0);

    public CLAssignment8() {
        try {
            // Load numbers from the file
            numbers = Files.readAllLines(Paths.get("output.txt")).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns 1,000 numbers at a time.
     * It ensures it does not exceed the list size.
     */
    public List<Integer> getNumbers() {
        int start = i.getAndAdd(1000);
        int end = Math.min(start + 1000, numbers.size()); // Prevent out-of-bounds access

        // Stop if start exceeds available numbers
        if (start >= numbers.size()) {
            return Collections.emptyList();
        }

        System.out.println("Fetching records " + start + " to " + end);

        // Simulate API delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }

        System.out.println("Done fetching records " + start + " to " + end);
        return new ArrayList<>(numbers.subList(start, end)); // Use subList for efficiency
    }

    public void fetchDataAsynchronously() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Integer> allNumbers = Collections.synchronizedList(new ArrayList<>());

        // Fetch numbers asynchronously 1000 times
        List<CompletableFuture<Void>> futures = IntStream.range(0, 1000) 
                .mapToObj(i -> CompletableFuture.supplyAsync(this::getNumbers, executor)
                        .thenAccept(allNumbers::addAll))
                .collect(Collectors.toList());

        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Count occurrences of each number
        Map<Integer, Long> countMap = allNumbers.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));

        // Print results
        IntStream.rangeClosed(1, 10)
                .forEach(i -> System.out.println(i + "=" + countMap.getOrDefault(i, 0L)));

        executor.shutdown();
    }

    public static void main(String[] args) {
        CLAssignment8 carlosAssignment = new CLAssignment8();
        carlosAssignment.fetchDataAsynchronously();
    }

}

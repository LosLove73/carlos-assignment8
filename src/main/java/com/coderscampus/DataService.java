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
	private final Assignment8Starter dataProvider;
	
	public DataService() {
		this.dataProvider = new Assignment8Starter();
	}
	
	public void fetchDataAsynchronously() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
		List<Integer> allNumbers = Collections.synchronizedList(new ArrayList<>());
		
		// Estimate number of fetch calls based on data size
		int totalNumbers = dataProvider.getNumbers().size();
		int batchSize = 1000;
		int totalBatches = (int) Math.ceil((double) totalNumbers / batchSize);
		
		// Create asynchronous tasks for each batch
		List<CompletableFuture<Void>> futures = IntStream.range(0, totalBatches)
				.mapToObj(i -> CompletableFuture.supplyAsync(dataProvider::getNumbers, executor)
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

}

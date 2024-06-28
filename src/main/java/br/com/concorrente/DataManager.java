package br.com.concorrente;

import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DataManager implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Map<String, Integer> userIdToIdx;
    private final Map<String, Integer> bookTitleToIdx;
    private final List<String> bookTitles;
    private final Map<Integer, Map<Integer, Double>> ratingsMatrix;
    private final AtomicInteger userIndexCounter;
    private final AtomicInteger bookTitleCounter;

    public DataManager(JavaRDD<String> data) {
        userIdToIdx = new HashMap<>();
        bookTitleToIdx = new HashMap<>();
        bookTitles = new ArrayList<>();
        ratingsMatrix = new ConcurrentHashMap<>();
        userIndexCounter = new AtomicInteger(0);
        bookTitleCounter = new AtomicInteger(0);
        processData(data);
    }

    private void processData(JavaRDD<String> data) {
        data.foreach(line -> {
            String[] parts = line.split(",");
            if (parts.length < 3) {
                System.err.println("Erro: Linha inválida: " + line);
                return;
            }

            String userId = parts[0];
            String ratingStr = parts[1];
            String bookTitle = parts[2];
            Double rating;

            try {
                rating = Double.parseDouble(ratingStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter para Double: " + e.getMessage());
                rating = 0.0;
            }

            int userIdx = userIdToIdx.computeIfAbsent(userId, k -> userIndexCounter.getAndIncrement());
            int bookIdx = bookTitleToIdx.computeIfAbsent(bookTitle, k -> {
                int index = bookTitleCounter.getAndIncrement();
                bookTitles.add(bookTitle);
                return index;
            });

            ratingsMatrix.computeIfAbsent(userIdx, k -> new ConcurrentHashMap<>()).put(bookIdx, rating);
            
        });
        System.out.println("tamanho da matriz depois do foreach: "+ ratingsMatrix.size());
    }

    public Map<String, Integer> getUserIdToIdx() {
        return Collections.unmodifiableMap(userIdToIdx);
    }

    public Map<String, Integer> getBookTitleToIdx() {
        return Collections.unmodifiableMap(bookTitleToIdx);
    }

    public List<String> getBookTitles() {
        return Collections.unmodifiableList(bookTitles);
    }

    public Map<Integer, Map<Integer, Double>> getRatingsMatrix() {
        return Collections.unmodifiableMap(ratingsMatrix);
    }
}

package br.com.concorrente;

import org.apache.spark.api.java.JavaRDD;

import java.util.*;

public class DataManager {
    private Map<String, Integer> userIdToIdx;
    private Map<String, Integer> bookTitleToIdx;
    private List<String> bookTitles;
    private Map<Integer, Map<Integer, Double>> ratingsMatrix;

    public DataManager(JavaRDD<String> data) {
        userIdToIdx = new HashMap<>();
        bookTitleToIdx = new HashMap<>();
        bookTitles = new ArrayList<>();
        ratingsMatrix = new HashMap<>();
        processData(data);
    }

    private void processData(JavaRDD<String> data) {
        List<String> lines = data.collect();
        int userIndex = 0;
        int bookIndex = 0;
        
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 3) {
                continue; 
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
            
            if (!userIdToIdx.containsKey(userId)) {
                userIdToIdx.put(userId, userIndex);
                userIndex++;
            }
            if (!bookTitleToIdx.containsKey(bookTitle)) {
                bookTitleToIdx.put(bookTitle, bookIndex);
                bookTitles.add(bookTitle);
                bookIndex++;
            }
            int userIdx = userIdToIdx.get(userId);
            int bookIdx = bookTitleToIdx.get(bookTitle);
            ratingsMatrix.computeIfAbsent(userIdx, k -> new HashMap<>()).put(bookIdx, rating);
        }
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

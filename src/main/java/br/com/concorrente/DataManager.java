package br.com.concorrente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataManager {
    private Map<String, Integer> userIdToIdx;
    private Map<String, Integer> bookTitleToIdx;
    private List<String> bookTitles;
    private Map<Integer, Map<Integer, Double>> ratingsMatrix;

    public DataManager() {
        userIdToIdx = new HashMap<>();
        bookTitleToIdx = new HashMap<>();
        bookTitles = new ArrayList<>();
        ratingsMatrix = new HashMap<>();
    }

    public void carregarDados(String arquivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String line;
            int userIndex = 0;
            int bookIndex = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String userId = parts[0];
                double rating = Double.parseDouble(parts[1]);
                String bookTitle = parts[2];
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getUserIdToIdx() {
        return userIdToIdx;
    }

    public Map<String, Integer> getBookTitleToIdx() {
        return bookTitleToIdx;
    }

    public List<String> getBookTitles() {
        return bookTitles;
    }

    public Map<Integer, Map<Integer, Double>> getRatingsMatrix() {
        return ratingsMatrix;
    }
}

package br.com.concorrente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataManager implements Runnable {
    private Map<String, Integer> userIdToIdx;
    private Map<String, Integer> bookTitleToIdx;
    private List<String> bookTitles;
    private Map<Integer, Map<Integer, Double>> ratingsMatrix;
    private String arquivo;
    private int inicio;
    private int fim;

    public DataManager() {
        userIdToIdx = new HashMap<>();
        bookTitleToIdx = new HashMap<>();
        bookTitles = new ArrayList<>();
        ratingsMatrix = new HashMap<>();
    }

    public DataManager(String arquivo, int inicio, int fim) {
        this.arquivo = arquivo;
        this.inicio = inicio;
        this.fim = fim;
        userIdToIdx = new HashMap<>();
        bookTitleToIdx = new HashMap<>();
        bookTitles = new ArrayList<>();
        ratingsMatrix = new HashMap<>();
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String line;
            int userIndex = 0;
            int bookIndex = 0;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null && lineNumber < fim) {
                if (lineNumber >= inicio) {
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
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergeData(DataManager otherDataManager) {
        userIdToIdx.putAll(otherDataManager.getUserIdToIdx());
        bookTitleToIdx.putAll(otherDataManager.getBookTitleToIdx());
        bookTitles.addAll(otherDataManager.getBookTitles());

        for (Map.Entry<Integer, Map<Integer, Double>> entry : otherDataManager.getRatingsMatrix().entrySet()) {
            int userIdx = entry.getKey();
            Map<Integer, Double> ratings = entry.getValue();
            Map<Integer, Double> existingRatings = ratingsMatrix.getOrDefault(userIdx, new HashMap<>());
            existingRatings.putAll(ratings);
            ratingsMatrix.put(userIdx, existingRatings);
        }
    }

    public Map<String, Integer> getUserIdToIdx() {
        return Map.copyOf(userIdToIdx);
    }

    public Map<String, Integer> getBookTitleToIdx() {
        return Map.copyOf(bookTitleToIdx);
    }

    public List<String> getBookTitles() {
        return Collections.unmodifiableList(bookTitles);
    }

    public Map<Integer, Map<Integer, Double>> getRatingsMatrix() {
        return Map.copyOf(ratingsMatrix);
    }
}
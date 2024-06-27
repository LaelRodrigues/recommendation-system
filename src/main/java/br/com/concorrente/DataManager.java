package br.com.concorrente;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.*;

public class DataManager {
    private Map<String, Integer> userIdToIdx;
    private Map<String, Integer> bookTitleToIdx;
    private List<String> bookTitles;
    private Map<Integer, Map<Integer, Double>> ratingsMatrix;

    public DataManager(Dataset<Row> data) {
        userIdToIdx = new HashMap<>();
        bookTitleToIdx = new HashMap<>();
        bookTitles = new ArrayList<>();
        ratingsMatrix = new HashMap<>();
        processData(data);
    }

    private void processData(Dataset<Row> data) {
        List<Row> rows = data.collectAsList();
        int userIndex = 0;
        int bookIndex = 0;

        for (Row row : rows) {
            String userId = row.getString(0);
            Double rating = row.getDouble(1);
            String bookTitle = row.getString(2);


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

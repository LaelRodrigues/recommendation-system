package br.com.concorrente;

import java.util.*;

public class RecommenderService {
    private DataManager dataManager;

    public RecommenderService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List<Map.Entry<String, Double>> recommendBooks(String userId, int k) {

        Map<String, Integer> userIdToIdx = dataManager.getUserIdToIdx();
        Map<Integer, Map<Integer, Double>> ratingsMatrix = dataManager.getRatingsMatrix();
        List<String> bookTitles = dataManager.getBookTitles();

        int userIdx = userIdToIdx.getOrDefault(userId, -1);
        if (userIdx == -1) {
            System.out.println("Usuário não encontrado.");
            return new ArrayList<>();
        }

        Map<Integer, Double> userRatings = ratingsMatrix.getOrDefault(userIdx, new HashMap<>());

        Map<Integer, Double> similarities = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Double>> entry : ratingsMatrix.entrySet()) {
            int otherUserIdx = entry.getKey();
            if (otherUserIdx != userIdx) {
                double similarity = calcularSimilaridadeCosseno(userRatings, entry.getValue());
                if (similarity > 0) {
                    similarities.put(otherUserIdx, similarity);
                }
            }
        }

        List<Map.Entry<Integer, Double>> similarUsers = new ArrayList<>(similarities.entrySet());
        similarUsers.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        List<Map.Entry<String, Double>> recommendedBooks = new ArrayList<>();
        for (Map.Entry<Integer, Double> similarUser : similarUsers) {
            Map<Integer, Double> similarUserRatings = ratingsMatrix.get(similarUser.getKey());
            for (Map.Entry<Integer, Double> entry : similarUserRatings.entrySet()) {
                int bookIdx = entry.getKey();
                double rating = entry.getValue();
                if (!userRatings.containsKey(bookIdx)) {
                    recommendedBooks.add(Map.entry(bookTitles.get(bookIdx), rating));
                    if (recommendedBooks.size() == k) {
                        return recommendedBooks;
                    }
                }
            }
        }

        return recommendedBooks;
    }

    private double calcularSimilaridadeCosseno(Map<Integer, Double> vector1, Map<Integer, Double> vector2) {
        double dotProduct = 0;
        double normVector1 = 0;
        double normVector2 = 0;

        for (Map.Entry<Integer, Double> entry : vector1.entrySet()) {
            int bookIdx = entry.getKey();
            double rating1 = entry.getValue();
            double rating2 = vector2.getOrDefault(bookIdx, 0.0);
            dotProduct += rating1 * rating2;
            normVector1 += rating1 * rating1;
            normVector2 += rating2 * rating2;
        }

        if (normVector1 == 0 || normVector2 == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(normVector1) * Math.sqrt(normVector2));
    }
}

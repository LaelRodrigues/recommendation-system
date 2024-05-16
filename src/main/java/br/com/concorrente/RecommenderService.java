package br.com.concorrente;

import java.util.*;
import java.util.concurrent.*;

public class RecommenderService {
    private DataManager dataManager;
    private final Map<Integer, Double> similarities = new HashMap<>();

    public RecommenderService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List<Map.Entry<String, Double>> recommendBooks(String userId, int k) {
        Map<String, Integer> userIdToIdx = dataManager.getUserIdToIdx();
        Map<Integer, Map<Integer, Double>> ratingsMatrix = dataManager.getRatingsMatrix();
        List<String> bookTitles = dataManager.getBookTitles();

        int userIdx = userIdToIdx.getOrDefault(userId, -1);
        if (userIdx == -1) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Map<Integer, Double> userRatings = ratingsMatrix.getOrDefault(userIdx, new HashMap<>());

        System.out.println("tamanho matrix: " + ratingsMatrix.size());

        List<Map.Entry<Integer, Double>> similarUsers;
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            similarUsers = forkJoinPool
                    .invoke(new SimilarityCalculator(ratingsMatrix, userRatings, userIdx, similarities));
        }

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

    private static class SimilarityCalculator extends RecursiveTask<List<Map.Entry<Integer, Double>>> {
        private final Map<Integer, Map<Integer, Double>> ratingsMatrix;
        private final Map<Integer, Double> userRatings;
        private final int userIdx;
        private final Map<Integer, Double> similarities;
        private final int startIdx;
        private final int endIdx;

        public SimilarityCalculator(Map<Integer, Map<Integer, Double>> ratingsMatrix, Map<Integer, Double> userRatings,
                int userIdx, Map<Integer, Double> similarities) {
            this(ratingsMatrix, userRatings, userIdx, similarities, 0, ratingsMatrix.size());
        }

        private SimilarityCalculator(Map<Integer, Map<Integer, Double>> ratingsMatrix, Map<Integer, Double> userRatings,
                int userIdx, Map<Integer, Double> similarities, int startIdx, int endIdx) {
            this.ratingsMatrix = ratingsMatrix;
            this.userRatings = userRatings;
            this.userIdx = userIdx;
            this.similarities = similarities;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        protected List<Map.Entry<Integer, Double>> compute() {
            if (endIdx - startIdx <= ratingsMatrix.size() / 4) {
                List<Map.Entry<Integer, Double>> similarUsers = new ArrayList<>();
                List<Integer> usersInRange = new ArrayList<>(ratingsMatrix.keySet()).subList(startIdx, endIdx);

                for (int userAIdx : usersInRange) {
                    if (userAIdx != userIdx) {
                        Map<Integer, Double> userARatings = ratingsMatrix.get(userAIdx);
                        double similarity = calculateCosineSimilarity(userRatings, userARatings);
                        if (similarity > 0) {
                            similarities.put(userAIdx, similarity);
                            similarUsers.add(new AbstractMap.SimpleEntry<>(userAIdx, similarity));
                        }
                    }
                }

                return similarUsers;
            } else {
                int middle = startIdx + (endIdx - startIdx) / 2;
                SimilarityCalculator leftTask = new SimilarityCalculator(ratingsMatrix, userRatings, userIdx,
                        similarities, startIdx, middle);
                SimilarityCalculator rightTask = new SimilarityCalculator(ratingsMatrix, userRatings, userIdx,
                        similarities, middle, endIdx);
                leftTask.fork();
                List<Map.Entry<Integer, Double>> leftResult = rightTask.compute();
                List<Map.Entry<Integer, Double>> rightResult = leftTask.join();
                leftResult.addAll(rightResult);
                return leftResult;
            }
        }
    }

    private static double calculateCosineSimilarity(Map<Integer, Double> vector1, Map<Integer, Double> vector2) {
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

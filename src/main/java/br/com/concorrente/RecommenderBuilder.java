package br.com.concorrente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommenderBuilder {

    private static Map<String, Integer> userIdToIdx = new HashMap<>();
    private static Map<String, Integer> bookTitleToIdx = new HashMap<>();
    private static List<String> bookTitles = new ArrayList<>();
    private static Map<Integer, Map<Integer, Double>> ratingsMatrix = new HashMap<>();

    public static void main(String[] args) {
        carregarDados("src/main/resources/teste3.csv");

        String userIdAlvo = "A3UH4UZ4RSVO82";
        int k = 5;

        List<Map.Entry<String, Double>> recommendedBooks = recommendBooks(userIdAlvo, k);
        System.out.println("Recomendações de livros para o usuário " + userIdAlvo + ":");
        for (int i = 0; i < recommendedBooks.size(); i++) {
            Map.Entry<String, Double> entry = recommendedBooks.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + " - Rating: " + entry.getValue());
        }
    }

    private static void carregarDados(String arquivo) {
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

    private static List<Map.Entry<String, Double>> recommendBooks(String userId, int k) {
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

    private static double calcularSimilaridadeCosseno(Map<Integer, Double> vector1, Map<Integer, Double> vector2) {
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

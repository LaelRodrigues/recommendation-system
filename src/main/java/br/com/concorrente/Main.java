package br.com.concorrente;

import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DataManager dataManager = new DataManager();

        long startTime = System.currentTimeMillis();

        dataManager.loadData("src/main/resources/teste3.csv");

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        System.out.println("tempo de resposta: " + duration + " em milissegundos");

        RecommenderService recommenderService = new RecommenderService(dataManager);

        String userIdAlvo = "A3UH4UZ4RSVO82";
        int k = 5;

        startTime = System.currentTimeMillis();

        System.out.println("Recomendações de livros para o usuário " + userIdAlvo + ":");
        for (Map.Entry<String, Double> entry : recommenderService.recommendBooks(userIdAlvo, k)) {
            System.out.println(entry.getKey() + " - Rating: " + entry.getValue());
        }
        
        endTime = System.currentTimeMillis();

        duration = endTime - startTime;

        System.out.println("tempo de resposta: " + duration + " em milissegundos");
    }
}

package br.com.concorrente;

import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DataManager dataManager = new DataManager();
        dataManager.carregarDados("src/main/resources/teste3.csv");

        RecommenderService recommenderService = new RecommenderService(dataManager);

        String userIdAlvo = "A3UH4UZ4RSVO82";
        int k = 5;

        System.out.println("Recomendações de livros para o usuário " + userIdAlvo + ":");
        for (Map.Entry<String, Double> entry : recommenderService.recommendBooks(userIdAlvo, k)) {
            System.out.println(entry.getKey() + " - Rating: " + entry.getValue());
        }   
    }
}

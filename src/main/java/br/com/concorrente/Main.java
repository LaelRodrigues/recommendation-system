package br.com.concorrente;

import java.io.IOException;
import java.util.Map;
import java.io.File;

public class Main {
    public static void main(String[] args) throws IOException {

        String caminhoArquivo = "src/main/resources/teste3.csv";

        File file = new File(caminhoArquivo);

        if (file.exists() && file.isFile()) {

            long tamanhoArquivo = file.length();

            int parte = (int) tamanhoArquivo / 4;

            DataManager part1 = new DataManager(caminhoArquivo, 0, parte);
            DataManager part2 = new DataManager(caminhoArquivo, parte, parte * 2);
            DataManager part3 = new DataManager(caminhoArquivo, parte * 2, parte * 2);
            DataManager part4 = new DataManager(caminhoArquivo, parte * 3, (int) tamanhoArquivo);


            long startTime = System.currentTimeMillis();

            Thread thread1 = Thread.ofVirtual().start(part1);
            Thread thread2 = Thread.ofVirtual().start(part2);
            Thread thread3 = Thread.ofVirtual().start(part3);
            Thread thread4 = Thread.ofVirtual().start(part4);


            // Esperando que todas as threads terminem
            try {
                thread1.join();
                thread2.join();
                thread3.join();
                thread4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;

            System.out.println("tempo de resposta: " + duration + " em milissegundos");

            DataManager combinedData = new DataManager();

            combinedData.mergeData(part1);
            combinedData.mergeData(part2);
            combinedData.mergeData(part3);
            combinedData.mergeData(part4);

            startTime = System.currentTimeMillis();

            thread1 = Thread.ofVirtual().start(() -> processRecommendations(combinedData, "A2F6NONFUDB6UK"));
            thread2 = Thread.ofVirtual().start(() -> processRecommendations(combinedData, "A25MD5I2GUIW6W"));
            thread3 = Thread.ofVirtual().start(() -> processRecommendations(combinedData, "A373VVEU6Z9M0N"));
            thread4 = Thread.ofVirtual().start(() -> processRecommendations(combinedData, "A2F6NONFUDB6UK"));
            
            try {
                thread1.join();
                thread2.join();
                thread3.join();
                thread4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Tempo de resposta: " + duration + " milissegundos");


        } else {
            System.out.println("O arquivo especificado não existe ou não é válido.");
        }
    }

    private static void processRecommendations(DataManager dataManager, String userId) {
        RecommenderService recommenderService = new RecommenderService(dataManager);
        int k = 5;

        System.out.println("Recomendações de livros para o usuário " + userId + ":");
        for (Map.Entry<String, Double> entry : recommenderService.recommendBooks(userId, k)) {
            System.out.println(entry.getKey() + " - Rating: " + entry.getValue());
        }
        System.err.println("--------------------");
    }
}

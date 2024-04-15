package br.com.concorrente;

import java.io.IOException;
import java.util.Map;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Main {

    public static final int NUM_THREADS = 4;

    public static void main(String[] args) throws IOException {

        String caminhoArquivo = "src/main/resources/teste3.csv";

        File file = new File(caminhoArquivo);

        if (file.exists() && file.isFile()) {

            long tamanhoArquivo = file.length();

            int parte = (int) tamanhoArquivo / 4;

            DataManager[] parts = new DataManager[4];

            parts[0] = new DataManager(caminhoArquivo, 0, parte);
            parts[1] = new DataManager(caminhoArquivo, parte, parte * 2);
            parts[2] = new DataManager(caminhoArquivo, parte * 2, parte * 2);
            parts[3] = new DataManager(caminhoArquivo, parte * 3, (int) tamanhoArquivo);

            long startTime = System.currentTimeMillis();

            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < NUM_THREADS; i++) {
                threads.add(Thread.ofPlatform().start(parts[i]));
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;

            System.out.println("tempo de resposta: " + duration + " em milissegundos");

            DataManager combinedData = new DataManager();

            for (int i = 0; i < NUM_THREADS; i++) {
                combinedData.mergeData(parts[i]);
            }

            startTime = System.currentTimeMillis();

            threads = new ArrayList<>();

            String[] users = { "A3UH4UZ4RSVO82", "APYJH0HLLK09U", "A1IE9D5RYN9K8", "A19EWUSJSSJZU7", };

            for (int i = 0; i < users.length; i++) {
                int index = i;
                threads.add(Thread.ofPlatform().start(() -> processRecommendations(combinedData, users[index])));
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
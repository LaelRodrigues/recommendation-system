package br.com.concorrente;

import java.io.IOException;
import java.util.Map;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Main {

    public static final int NUM_THREADS = 3;

    public static void main(String[] args) throws IOException {

        String caminhoArquivo = "src/main/resources/teste3.csv";

        File file = new File(caminhoArquivo);

        if (file.exists() && file.isFile()) {

            long tamanhoArquivo = file.length();

            long parte = tamanhoArquivo / NUM_THREADS;

            DataManager[] parts = new DataManager[NUM_THREADS];

            long inicioParte = 0;
            long fimParte = parte;

            for (int i = 0; i < NUM_THREADS; i++) {
                parts[i] = new DataManager(caminhoArquivo, (int) inicioParte, (int) fimParte);
                inicioParte = fimParte;
                fimParte += parte;
                if (i == NUM_THREADS - 2) {
                    fimParte = tamanhoArquivo;
                }
            }
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

            processRecommendations(combinedData, "A3UH4UZ4RSVO82");

            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Tempo de resposta: " + duration + " milissegundos");

        } else {
            System.out.println("O arquivo especificado não existe ou não é válido.");
        }
    }

    private static void processRecommendations(DataManager dataManager, String userId) {
        try {
            RecommenderService recommenderService = new RecommenderService(dataManager);
            int k = 5;

            System.out.println("Recomendações de livros para o usuário " + userId + ":");
            for (Map.Entry<String, Double> entry : recommenderService.recommendBooks(userId, k)) {
                System.out.println(entry.getKey() + " - Rating: " + entry.getValue());
            }
            System.err.println("--------------------");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao recomendar livros: " + e.getMessage());
        }
    }
}
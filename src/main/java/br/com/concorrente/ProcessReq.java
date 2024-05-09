package br.com.concorrente;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ProcessReq {

    public static final int NUM_THREADS = 3;

    public List<Map.Entry<String, Double>> ProcessDataReq(String user, int numRecomendations) {

        try {
            System.err.println("aaaa");
            String caminhoArquivo = "src/main/resources/teste2.csv";

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

                DataManager combinedData = new DataManager();

                for (int i = 0; i < NUM_THREADS; i++) {
                    combinedData.mergeData(parts[i]);
                }

                RecommenderService recommenderService = new RecommenderService(combinedData);
                List<Map.Entry<String, Double>> result = recommenderService.recommendBooks(user, numRecomendations);
                System.err.println("bbbb");
                return result;
            } else {
                System.out.println("O arquivo especificado não existe ou não é válido.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao recomendar livros: " + e.getMessage());
        }
        return null;

    }
}

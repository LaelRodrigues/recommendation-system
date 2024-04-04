package br.com.concorrente;

import java.io.IOException;
import java.util.Map;
import java.io.File;

public class Main {
    public static void main(String[] args) throws IOException {

        String caminhoArquivo = "src/main/resources/teste2.csv";

        // Crie um objeto File com o caminho do arquivo
        File file = new File(caminhoArquivo);

        // Verifique se o arquivo existe e é um arquivo válido
        if (file.exists() && file.isFile()) {
            // Obtenha o tamanho do arquivo em bytes
            long tamanhoArquivo = file.length();

            // Dividindo o arquivo em 3 partes
            int parte = (int) tamanhoArquivo / 3;

            // Criando threads para ler cada parte do arquivo
            DataManager dataManager1 = new DataManager(caminhoArquivo, 0, parte);
            DataManager dataManager2 = new DataManager(caminhoArquivo, parte, parte * 2);
            DataManager dataManager3 = new DataManager(caminhoArquivo, parte * 2, (int) tamanhoArquivo);

            long startTime = System.currentTimeMillis();

            Thread thread1 = Thread.ofPlatform().start(dataManager1);
            Thread thread2 = Thread.ofPlatform().start(dataManager2);
            Thread thread3 = Thread.ofPlatform().start(dataManager3);

            // Esperando que todas as threads terminem
            try {
                thread1.join();
                thread2.join();
                thread3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Agora você pode usar os dados carregados
            System.out.println("tamanho da matriz: " + dataManager1.getRatingsMatrix().size());

            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;

            System.out.println("tempo de resposta: " + duration + " em milissegundos");

            DataManager dataManager = new DataManager();

            dataManager.mergeData(dataManager1);
            dataManager.mergeData(dataManager2);
            dataManager.mergeData(dataManager3);

            startTime = System.currentTimeMillis();

            Thread thread11 = Thread.ofPlatform().start(() -> processRecommendations(dataManager, "A2F6NONFUDB6UK"));
            Thread thread22 = Thread.ofPlatform().start(() -> processRecommendations(dataManager, "A25MD5I2GUIW6W"));
            Thread thread33 = Thread.ofPlatform().start(() -> processRecommendations(dataManager, "A373VVEU6Z9M0N"));

            try {
                thread11.join();
                thread22.join();
                thread33.join();
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

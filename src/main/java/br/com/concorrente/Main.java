package br.com.concorrente;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
    	
    	
        String caminhoArquivo = "src/main/resources/dataframe_csv_v2.csv";

        SparkConf conf = new SparkConf().setAppName("RecomendacaoDeLivros")
                .setMaster("local[*]");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> csvData = sc.textFile(caminhoArquivo);
        
        // processamento do algoritmo de recomendação

        long startTime = System.currentTimeMillis();

        DataManager dataManager = new DataManager(csvData);
        
        System.out.println("matriz: "+ dataManager.getRatingsMatrix());

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
	
        System.out.println("tempo de resposta: " + duration + " em milissegundos");
	
        processRecommendations(dataManager, "A3UH4UZ4RSVO82");
        
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted!");
        }

        
        sc.close();
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

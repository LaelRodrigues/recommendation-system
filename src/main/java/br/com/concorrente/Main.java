package br.com.concorrente;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
    	String caminhoArquivo = "src/main/resources/teste3.csv";

        SparkConf conf = new SparkConf().setAppName("RecomendacaoDeLivros")
                .setMaster("local[*]");

        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

        Dataset<Row> csvData = spark.read().option("header", "true").csv(caminhoArquivo);

        long startTime = System.currentTimeMillis();

        DataManager dataManager = new DataManager(csvData);

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
	
        System.out.println("tempo de resposta: " + duration + " em milissegundos");
	
//        startTime = System.currentTimeMillis();

//        processRecommendations(dataManager, "A3UH4UZ4RSVO82");
//
//        endTime = System.currentTimeMillis();
//        duration = endTime - startTime;
//        System.out.println("Tempo de resposta: " + duration + " milissegundos");
        

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted!");
        }

        
        spark.close();
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

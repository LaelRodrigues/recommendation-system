package br.com.concorrente;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Map;

public class Main {

    public static void main(String[] args) throws InterruptedException {
    	String caminhoArquivo = "src/main/resources/dataframe_csv_v1.csv";

        SparkSession spark = SparkSession.builder()	
                .appName("RecomendacaoDeLivros")
                .master("local[*]")
                .getOrCreate();
        
        StructType schema = DataTypes.createStructType(new StructField[] {
        		DataTypes.createStructField(
        				"user_id", 
        				DataTypes.StringType, 
        				false),
          		DataTypes.createStructField(
        				"rating", 
        				DataTypes.DoubleType, 
        				false),
          		DataTypes.createStructField(
        				"book_id", 
        				DataTypes.StringType, 
        				false), });
        

        Dataset<Row> df = spark.read().format("csv")
        		.option("header", "true")
        		.option("multiline", "true")
        		.option("sep", ",")
        		.option("quote", "\"")
        		.schema(schema)
        		.load(caminhoArquivo);
        
        df.show(5); 
        


        long startTime = System.currentTimeMillis();
        

        DataManager dataManager = new DataManager(df);

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
	
        System.out.println("tempo de resposta: " + duration + " milissegundos");

        processRecommendations(dataManager, "A3UH4UZ4RSVO82");
        
        Thread.sleep(60000);

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

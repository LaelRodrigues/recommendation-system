package br.com.concorrente;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class RecommenderServiceTest extends AbstractJavaSamplerClient implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final int NUM_THREADS = 4;

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        String userId = "A3UH4UZ4RSVO82";
        int k = 1; //

        SampleResult result = new SampleResult();
        result.sampleStart();
        result.setSampleLabel("Recommender Service Test");

        String caminhoArquivo = "C:\\Users\\Laelr\\OneDrive\\Área de Trabalho\\programacao_concorrente\\concorrente\\src\\main\\resources\\teste2.csv";

        File file = new File(caminhoArquivo);

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

        try {
            List<Map.Entry<String, Double>> recommendations = recommenderService.recommendBooks(userId, k);

            if (recommendations.size() == 1) {
                Map.Entry<String, Double> recommendation = recommendations.get(0);
                String expectedBook = "Farmer McPeepers and His Missing Milk Cows";
                double expectedRating = 5.0;

                if (recommendation.getKey().equals(expectedBook) && recommendation.getValue().equals(expectedRating)) {
                    result.setResponseCode("200");
                    result.setResponseMessage("OK");
                    result.setSuccessful(true);
                } else {
                    result.setResponseCode("500");
                    result.setResponseMessage("Unexpected recommendation");
                    result.setSuccessful(false);
                }
            } else {
                result.setResponseCode("500");
                result.setResponseMessage("Unexpected number of recommendations");
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            result.setResponseCode("500");
            result.setResponseMessage("Error: " + e.getMessage());
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
        }

        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("userId", "A3UH4UZ4RSVO82");
        defaultParameters.addArgument("k", "1");
        return defaultParameters;
    }
}

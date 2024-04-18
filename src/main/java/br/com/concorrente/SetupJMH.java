package br.com.concorrente;

import java.io.File;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SetupJMH {

    private static final int K_VALUE = 5;

    private RecommenderService recommenderService;

    public SetupJMH() {
    }

    @Setup
    public void setup() {

        String caminhoArquivo = "src/main/resources/teste3.csv";

        File file = new File(caminhoArquivo);

        long tamanhoArquivo = file.length();

        DataManager dataManager = new DataManager(caminhoArquivo, 0, (int) tamanhoArquivo);

        Thread thread = Thread.ofPlatform().start(dataManager);

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.recommenderService = new RecommenderService(dataManager);
    }

    @Benchmark
    public void recommendBooksBenchmark() {
        recommenderService.recommendBooks("A3UH4UZ4RSVO82", K_VALUE);
    }
}
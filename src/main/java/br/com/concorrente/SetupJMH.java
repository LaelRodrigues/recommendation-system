package br.com.concorrente;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SetupJMH {

    private static final int K_VALUE = 5;

    private RecommenderService recommenderService;
    private DataManager dataManager;
    private String userIdTarget;

    @Setup
    public void setup() {

        this.dataManager = new DataManager();

        dataManager.loadData("src/main/resources/teste3.csv");

        this.recommenderService = new RecommenderService(dataManager);

        this.userIdTarget = "A3UH4UZ4RSVO82";
    }

    @Benchmark
    public void recommendBooksBenchmark() {
        recommenderService.recommendBooks(userIdTarget, K_VALUE);
    }
}

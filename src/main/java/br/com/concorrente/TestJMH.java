package br.com.concorrente;

import java.io.IOException;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class TestJMH {
    public static void main(String[] args) throws RunnerException, IOException {

        Options opt = new OptionsBuilder()
                .include(SetupJMH.class.getSimpleName())
                .warmupIterations(5)
                .shouldDoGC(true)
                .measurementBatchSize(5).forks(1)
                .addProfiler(GCProfiler.class)
                .addProfiler(StackProfiler.class)
                .jvmArgs("-server", "-Xms4G", "-Xmx4G").build();

        new Runner(opt).run();
    }
}
package lab;

import lab.export.Exporter;
import lab.run.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class Executor {

    private final RunPoolExecutor runPoolExecutor;
    private final RunPoolStatsCreator runPoolStatsCreator;
    private final Exporter exporter;

    public void executeAllSingleThread(List<RunPool> runPools) {
        List<RunPoolStats> runPoolStats = runPoolExecutor.executeAllRunPools(runPools);

        System.gc();

        System.out.println("EXPORTING SINGLE RUN POOLS -----------------");
        runPoolStats.forEach(exporter::exportSingleRunPoolStats);

        System.gc();

        System.out.println("EXPORTING SINGLE RUN POOL PLOTS -----------------");

        int size = runPoolStats.size();
        IntStream.range(0, size)
                .forEach(i -> {
                    System.out.println("Plots " + (i + 1) + "/" + size);
                    exporter.exportPlots(runPoolStats.get(i).allRunStats(), runPoolStats.get(i).runConfiguration());
                });

        System.gc();

        // POSSIBLE DUPLICATE
        System.out.println("EXPORTING SINGLE RUN POOL HISTOGRAMS -----------------");
        IntStream.range(0, size)
                .forEach(i -> {
                    System.out.println("Histograms " + (i + 1) + "/" + size);
                    exporter.exportHistograms(runPoolStats.get(i).allRunStats(), runPoolStats.get(i).runConfiguration());
                });

        System.gc();

        System.out.println("EXPORTING ALL RUN POOLS -----------------");
        exporter.exportAllRunPools(runPoolStats);
    }

    public void executeAll(List<RunPool> runPools) {
        List<RunPoolStats> runPoolStats = runPoolExecutor.executeAllRunPoolsParallel(runPools);
        ExecutorService executorService = new ForkJoinPool();

        System.out.println("EXPORTING SINGLE RUN POOLS -----------------");
        runPoolStats.forEach(stats -> executorService.submit(() -> exporter.exportSingleRunPoolStats(stats)));

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.gc();

        ExecutorService plotsExecutorService = new ForkJoinPool();
        System.out.println("EXPORTING SINGLE RUN POOL PLOTS -----------------");
        int size = runPoolStats.size();
        IntStream.range(0, size)
                .forEach(i -> {
                    System.out.println("Plots " + (i + 1) + "/" + size);
                    exporter.exportPlots(runPoolStats.get(i).allRunStats(), runPoolStats.get(i).runConfiguration());
                });

        plotsExecutorService.shutdown();
        try {
            plotsExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("EXPORTING ALL RUN POOLS -----------------");
        exporter.exportAllRunPools(runPoolStats);
    }

    public void executeAllParallel(List<RunPool> runPools) {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableThreads);
        CompletionService<RunStatsWithConfig> completionService = new ExecutorCompletionService<>(executorService);

        Map<RunConfiguration, List<RunStatsWithConfig>> configToRunStats = new HashMap<>();
        Map<RunConfiguration, Integer> configToExpectedCount = new HashMap<>();

        int totalSubmitted = 0;

        System.out.println("SUBMITTING RUNS -----------------");

        for (int poolIndex = 0; poolIndex < runPools.size(); poolIndex++) {
            RunPool runPool = runPools.get(poolIndex);
            RunConfiguration runConfiguration = runPool.runConfiguration();
            List<Run> runs = runPool.runs();
            int runCount = runs.size();

            configToRunStats.put(runConfiguration, new ArrayList<>(runCount));
            configToExpectedCount.put(runConfiguration, runCount);

            for (int runIndex = 0; runIndex < runCount; runIndex++) {
                Run run = runs.get(runIndex);
                int finalRunIndex = runIndex;
                int finalPoolIndex = poolIndex;

                completionService.submit(() -> {
                    RunStats runStats = runPoolExecutor.executeRun(
                            run,
                            finalRunIndex,
                            runCount,
                            finalPoolIndex,
                            runPools.size()
                    );
                    return new RunStatsWithConfig(runConfiguration, runStats, finalRunIndex, finalPoolIndex);
                });

                totalSubmitted++;
            }
        }

        System.out.println("WAITING AND PROCESSING RESULTS -----------------");

        for (int i = 0; i < totalSubmitted; i++) {
            try {
                Future<RunStatsWithConfig> future = completionService.take();
                RunStatsWithConfig runStatsWithConfig = future.get();

                RunConfiguration runConfiguration = runStatsWithConfig.runConfiguration();
                List<RunStatsWithConfig> collectedRunStats = configToRunStats.get(runConfiguration);
                collectedRunStats.add(runStatsWithConfig);

                if (collectedRunStats.size() == configToExpectedCount.get(runConfiguration)) {
                    System.out.println("EXPORTING RUN POOL for run pool " + runStatsWithConfig.runPoolIndex() + 1);

                    List<RunStats> orderedRunStats = collectedRunStats.stream()
                            .sorted(Comparator.comparing(RunStatsWithConfig::runIndex))
                            .map(RunStatsWithConfig::runStats)
                            .toList();

                    RunPoolStats runPoolStats = runPoolStatsCreator.create(orderedRunStats, runConfiguration);

                    exporter.exportSingleRunPoolStats(runPoolStats);
                    exporter.exportPlots(runPoolStats.allRunStats(), runConfiguration);
                    System.out.println("EXPORTING COMPLETED for RUN POOL for run pool " + runStatsWithConfig.runPoolIndex() + 1);

                    System.out.println("EXPORTING RUN POOL TO GENERAL TABLE for run pool " + runStatsWithConfig.runPoolIndex() + 1);
                    exporter.appendRunPoolStatsToAllStatsTable(runPoolStats);
                    System.out.println("EXPORTING RUN POOL TO GENERAL TABLE FINISHED for run pool " + runStatsWithConfig.runPoolIndex() + 1);

                    configToRunStats.remove(runConfiguration);
                    System.gc(); // encourage cleanup of completed pool
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for run completion", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Run failed", e.getCause());
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                System.err.println("Timeout waiting for executor termination.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("ALL RUN POOLS COMPLETED");
    }
}
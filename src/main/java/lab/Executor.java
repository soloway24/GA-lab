package lab;

import lab.export.Exporter;
import lab.run.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        ExecutorService executorService = new ForkJoinPool();

        List<List<Future<RunStats>>> allFutures = new ArrayList<>();

        for (int i = 0; i < runPools.size(); i++) {
            RunPool runPool = runPools.get(i);
            int finalI = i;
            System.out.println("Submitting run pool " + finalI + "/" + runPools.size() + " : " + runPool.runConfiguration());
            List<Future<RunStats>> runPoolFutures = new ArrayList<>();
            List<Run> runs = runPool.runs();
            for (int j = 0; j < runs.size(); j++) {
                Run run = runs.get(j);
                int finalJ = j;

                Future<RunStats> future = executorService.submit(() -> runPoolExecutor.executeRun(run,
                        finalJ, runs.size(), finalI, runPools.size()));
                runPoolFutures.add(future);
            }
            allFutures.add(runPoolFutures);
        }

        List<List<RunStats>> allRunStats = new ArrayList<>();

        System.out.println("WAITING FOR COMPLETION-----------------");

        for (int i = 0; i < runPools.size(); i++) {
            List<RunStats> runPoolStats = new ArrayList<>();
            List<Future<RunStats>> runPoolFutures = allFutures.get(i);
            for (Future<RunStats> runFuture : runPoolFutures) {
                RunStats runStats;
                try {
                    runStats = runFuture.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                runPoolStats.add(runStats);
            }
            allRunStats.add(runPoolStats);
        }


        if (allRunStats.size() != runPools.size()) {
            throw new IllegalStateException("Incorrect number of allRunStats: " + allRunStats.size() + ". Should be " + runPools.size());
        }
        System.out.println("CREATING RUN POOL STATS-----------------");
        List<Future<RunPoolStats>> runPoolStatsFutures = new ArrayList<>();
        for (int i = 0; i < allRunStats.size(); i++) {
            int finalI = i;
            Future<RunPoolStats> future = executorService.submit(() -> runPoolStatsCreator.create(allRunStats.get(finalI), runPools.get(finalI).runConfiguration())
            );
            runPoolStatsFutures.add(future);
        }

        System.out.println("WAITING FOR RUN POOL STATS-----------------");
        List<RunPoolStats> allRunPoolStats = new ArrayList<>();
        for (Future<RunPoolStats> future : runPoolStatsFutures) {
            RunPoolStats runPoolStats;
            try {
                runPoolStats = future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            allRunPoolStats.add(runPoolStats);
        }

        if (allRunPoolStats.size() != runPools.size()) {
            throw new IllegalStateException("Incorrect number of allRunPoolStats: " + allRunPoolStats.size() + ". Should be " + runPools.size());
        }

        System.out.println("EXPORTING SINGLE RUN POOLS -----------------");
        allRunPoolStats.forEach(stats -> executorService.submit(() -> exporter.exportSingleRunPoolStats(stats)));

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int parallelism = Runtime.getRuntime().availableProcessors();
        ExecutorService plotsExecutorService = Executors.newFixedThreadPool(parallelism);

        System.out.println("EXPORTING SINGLE RUN POOL PLOTS -----------------");
        int size = allRunPoolStats.size();

        List<Callable<Void>> tasks = IntStream.range(0, size)
                .mapToObj(i -> (Callable<Void>) () -> {
                    System.out.println("Plots " + (i + 1) + "/" + size);
                    exporter.exportPlots(
                            allRunPoolStats.get(i).allRunStats(),
                            allRunPoolStats.get(i).runConfiguration()
                    );
                    return null;
                })
                .toList();

        try {
            plotsExecutorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException("Plot export interrupted", e);
        } finally {
            plotsExecutorService.shutdown();
            try {
                if (!plotsExecutorService.awaitTermination(1, TimeUnit.HOURS)) {
                    System.err.println("Timeout waiting for tasks to complete.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        System.out.println("EXPORTING ALL RUN POOLS -----------------");
        exporter.exportAllRunPools(allRunPoolStats);
    }
}
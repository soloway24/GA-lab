package lab.v2;

import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.v2.export.Exporter;
import lab.v2.function.*;
import lab.v2.identifier.ConvergenceIdentifier;
import lab.v2.operator.NoneOperator;
import lab.v2.operator.Operator;
import lab.v2.population.PopulationInitializer;
import lab.v2.population.PopulationPoolInitializer;
import lab.v2.population.PopulationTypeValidator;
import lab.v2.run.*;
import lab.v2.selection.*;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MainV2 {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();
    private final RunConfigurationFactory runConfigurationFactory = RunConfigurationFactory.getInstance();
    private final PopulationInitializer populationInitializer = new PopulationInitializer(PopulationTypeValidator.getInstance());
    private final PopulationPoolInitializer populationPoolInitializer = new PopulationPoolInitializer(populationInitializer);
    private final RunPoolCreator runPoolCreator = new RunPoolCreator(populationPoolInitializer);
    private final ConvergenceIdentifier convergenceIdentifier = new ConvergenceIdentifier();
    private final RunPoolStatsCreator runPoolStatsCreator = new RunPoolStatsCreator();
    private final RunPoolExecutor runPoolExecutor = new RunPoolExecutor(convergenceIdentifier, runPoolStatsCreator);
    private final Exporter exporter = new Exporter();

    public static void main(String[] args) throws InterruptedException {
        MainV2 mainV2 = new MainV2();
        mainV2.run();
    }

    private void run() throws InterruptedException {
        List<Integer> populationSizes = getPopulationSizes();
        List<FitnessFunctionV2<?, ?>> functions = getFunctions();
        List<Selector> selectors = getSelectors();
        List<Operator> operators = getOperators();
        List<RunPoolConfiguration> runPoolConfigurations = getRunPoolConfigurations(functions, selectors, operators,
                populationSizes);
        List<RunPool> runPools = getRunPools(runPoolConfigurations);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

//        executeAllSingleThread(runPools);
        executeAll(runPools);
//        executeAllParallel(runPools);

        stopWatch.stop();
        System.out.println("Time Elapsed: " + stopWatch.getTime() / 1000.0);
    }

    private void executeAllSingleThread(List<RunPool> runPools) throws InterruptedException {
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

    private void executeAll(List<RunPool> runPools) {
        List<RunPoolStats> runPoolStats = runPoolExecutor.executeAllRunPoolsParallel(runPools);
        ExecutorService executorService = new ForkJoinPool();

        System.out.println("EXPORTING SINGLE RUN POOLS -----------------");
        runPoolStats.forEach(stats -> executorService.submit(() -> {
            exporter.exportSingleRunPoolStats(stats);
        }));

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.gc();

        ExecutorService plotsExecutorService = Executors.newFixedThreadPool(4);
        System.out.println("EXPORTING SINGLE RUN POOL PLOTS -----------------");
        runPoolStats.forEach(stats -> plotsExecutorService.submit(() -> {
            exporter.exportPlots(stats.allRunStats(), stats.runConfiguration());
        }));

        plotsExecutorService.shutdown();
        try {
            plotsExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("EXPORTING ALL RUN POOLS -----------------");
        exporter.exportAllRunPools(runPoolStats);
    }

    private void executeAllParallel(List<RunPool> runPools) throws InterruptedException {
        ExecutorService executorService = new ForkJoinPool();
        runPools.forEach(runPool -> executorService.submit(() -> {
//            RunPoolStats runPoolStats = runPoolExecutor.executeRunPool(runPool);
            RunPoolStats runPoolStats = runPoolExecutor.executeRunPoolParallel(runPool);
            exporter.exportSingleRunPoolStats(runPoolStats);
        }));
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private List<Integer> getPopulationSizes() {
        return List.of(100);
    }

    private List<FitnessFunctionV2<?, ?>> getFunctions() {
        FitnessFunctionV2<?, ?> constAllFunction = FConstAllFunction.getInstance();
        FitnessFunctionV2<?, ?> fhFunction = FhFunction.getInstance();
        FitnessFunctionV2<?, ?> quadraticFunction = new PowerFunction(10, 0, 10.23, 2, 2);
        FitnessFunctionV2<?, ?> quadratic512Function = new PowerFunction5_12(10, -5.12, 5.11, 2,
                5.12, 2, 2);
        FitnessFunctionV2<?, ?> exponent025 = new ExponentialFunction(10, 0, 10.23, 2, 0.25);
        FitnessFunctionV2<?, ?> exponent1 = new ExponentialFunction(10, 0, 10.23, 2, 1);
        FitnessFunctionV2<?, ?> exponent2 = new ExponentialFunction(10, 0, 10.23, 2, 2);

//        return List.of(constAllFunction);
//        return List.of(fhFunction);
//        return List.of(quadraticFunction);
//        return List.of(quadratic512Function);
//        return List.of(exponent025);
//        return List.of(exponent1);
        return List.of(exponent2);
//        return List.of(constAllFunction, quadraticFunction);
    }

    private List<Selector> getSelectors() {
        RwsSelector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
        SusSelector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);

        ScalingSelector scalingSelector = new ScalingSelector();
        PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, 1.1);
        PowerScalingSelector powerScalingSelector2 = new PowerScalingSelector(scalingSelector, 2);

        PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);
        PowerScalingRwsSelector powerScalingRwsSelector2 = new PowerScalingRwsSelector(powerScalingSelector2, rwsSelector);
        PowerScalingSusSelector powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);
        PowerScalingSusSelector powerScalingSusSelector2 = new PowerScalingSusSelector(powerScalingSelector2, susSelector);

        DynamicPowerScalingSelector dynamicPowerScalingSelector0p9to1p1 = new DynamicPowerScalingSelector(scalingSelector, 0.9, 1.1);
        DynamicPowerScalingSelector dynamicPowerScalingSelector0p8to1p2 = new DynamicPowerScalingSelector(scalingSelector, 0.8, 1.2);

        DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector0p9to1p1 =
                new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector0p9to1p1, rwsSelector);
        DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector0p8to1p2 =
                new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector0p8to1p2, rwsSelector);

        DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector0p9to1p1 =
                new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector0p9to1p1, susSelector);
        DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector0p8to1p2 =
                new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector0p8to1p2, susSelector);

        return List.of(
                rwsSelector
//                ,
//                susSelector
//                ,
//                powerScalingRwsSelector
//                ,
//                powerScalingRwsSelector2
//                ,
//                powerScalingSusSelector,
//                powerScalingSusSelector2,
//                dynamicPowerScalingRwsSelector0p9to1p1
//                ,
//                dynamicPowerScalingRwsSelector0p8to1p2
//                ,
//                dynamicPowerScalingSusSelector0p9to1p1,
//                dynamicPowerScalingSusSelector0p8to1p2
        );
    }

    private List<Operator> getOperators() {
        Operator noneOperator = new NoneOperator();

        return List.of(noneOperator);
    }

    private List<RunPoolConfiguration> getRunPoolConfigurations(List<FitnessFunctionV2<?, ?>> functions,
                                                                List<Selector> selectors,
                                                                List<Operator> operators,
                                                                List<Integer> populationSizes) {
        return runConfigurationFactory.createPoolConfigurations(functions, selectors, operators, populationSizes);
    }

    private List<RunPool> getRunPools(List<RunPoolConfiguration> runPoolConfigurations) {
        return runPoolCreator.createAll(runPoolConfigurations);
    }
}

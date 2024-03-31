package lab.v2;

import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.v2.function.FConstAllFunction;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.function.PowerFunction;
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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

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
        int runPoolSize = getRunPoolSize();
        List<RunPoolConfiguration> runPoolConfigurations = getRunPoolConfigurations(functions, selectors, operators,
                populationSizes, runPoolSize);
        List<RunPool> runPools = getRunPools(runPoolConfigurations);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

//        executeAll(runPools);
        executeAllParallel(runPools);

        stopWatch.stop();
        System.out.println("Time Elapsed: " + stopWatch.getTime() / 1000.0);
    }

    private void executeAll(List<RunPool> runPools) {
        List<RunPoolStats> runPoolStats = runPoolExecutor.executeAllRunPools(runPools);
        runPoolStats.forEach(exporter::exportRunPoolStats);
    }

    private void executeAllParallel(List<RunPool> runPools) throws InterruptedException {
        ExecutorService executorService = new ForkJoinPool();
        runPools.forEach(runPool -> executorService.submit(() -> {
//            RunPoolStats runPoolStats = runPoolExecutor.executeRunPool(runPool);
            RunPoolStats runPoolStats = runPoolExecutor.executeRunPoolParallel(runPool);
            exporter.exportRunPoolStats(runPoolStats);
        }));
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private List<Integer> getPopulationSizes() {
        return List.of(100);
    }

    private List<FitnessFunctionV2<?, ?>> getFunctions() {
        FitnessFunctionV2<?, ?> constAllFunction = FConstAllFunction.getInstance();
        FitnessFunctionV2<?, ?> quadraticFunction = new PowerFunction(10, 0, 10.23, 2, 2);

        return List.of(constAllFunction);
//        return List.of(quadraticFunction);
//        return List.of(constAllFunction, quadraticFunction);
    }

    private List<Selector> getSelectors() {
        RwsSelector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
        SusSelector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);

        ScalingSelector scalingSelector = new ScalingSelector();
        PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, 1.1);

        PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);
        PowerScalingSusSelector powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);

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
                rwsSelector,
                susSelector,
                powerScalingRwsSelector,
                powerScalingSusSelector,
                dynamicPowerScalingRwsSelector0p9to1p1,
                dynamicPowerScalingRwsSelector0p8to1p2,
                dynamicPowerScalingSusSelector0p9to1p1,
                dynamicPowerScalingSusSelector0p8to1p2
        );
    }

    private List<Operator> getOperators() {
        Operator noneOperator = new NoneOperator();

        return List.of(noneOperator);
    }

    private int getRunPoolSize() {
        return 100;
    }

    private List<RunPoolConfiguration> getRunPoolConfigurations(List<FitnessFunctionV2<?, ?>> functions,
                                                                List<Selector> selectors,
                                                                List<Operator> operators,
                                                                List<Integer> populationSizes,
                                                                int runPoolSize) {
        return runConfigurationFactory.createPoolConfigurations(functions, selectors, operators, populationSizes, runPoolSize);
    }

    private List<RunPool> getRunPools(List<RunPoolConfiguration> runPoolConfigurations) {
        return runPoolCreator.createAll(runPoolConfigurations);
    }

//    public static void main(String[] args) {
//       /* N = 100;
//        FITNESS_FUNCTION = FitnessFunction.F2;
//        ENCODING = Encoding.STANDARD;
//        CONTEST_TYPE = ContestType.MULTIPLE_ENTRY ;
//        P_SWAP = 0.8f;
//        GENETIC_OPERATORS = GeneticOperatorsApplication.CROSSOVER_MUTATION;
//
//        List<Individual> initialPopulation = initPopulation(FITNESS_FUNCTION, ENCODING);
//        List<RunPoolStatsData> all_pools = new ArrayList<>();
//
//        run(initialPopulation, all_pools);*/
//
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
//        List<RunPoolStatsData> all_pools = new ArrayList<>();
//
//        for (Integer populationSize : POPULATION_SIZES) {
//            N = populationSize;
//            List<RunPoolStatsData> n_pools = new ArrayList<>();
//            for (FitnessFunction function : FitnessFunction.values()) {
//                GeneticUtils.FITNESS_FUNCTION = function;
//                Encoding[] values;
//                if (function == FitnessFunction.QUAD || function == FitnessFunction.QUAD_SYM)
//                    values = Encoding.values();
//                else
//                    values = new Encoding[]{Encoding.STANDARD};
//
//                for (Encoding encoding : values) {
//                    GeneticUtils.ENCODING = encoding;
//                    List<Individual> initialPopulation = initPopulation(function, encoding);
//                    for (ContestType contestType : ContestType.values()) {
//                        GeneticUtils.CONTEST_TYPE = contestType;
//                        for (float sel_prob : SELECTION_PROBABILITY) {
//                            GeneticUtils.P_SWAP = sel_prob;
//                            for (GeneticOperatorsApplication operators : GeneticOperatorsApplication.values()) {
//                                GENETIC_OPERATORS = operators;
//                                run(initialPopulation, all_pools, n_pools);
//                            }
//                        }
//                    }
//                }
//            }
//            Export.exportRunPools(n_pools, N + "_");
//        }
//        Export.exportRunPools(all_pools, "");
//    }
//
//    public static void run(List<Individual> initialPopulation, List<RunPoolStatsData> all_pools, List<RunPoolStatsData> n_pools) {
//        Mutation.MUTATION_PROBABILITY = getMutationProbability();
//
//        long startTime = System.currentTimeMillis();
//        Date start = new Date();
//
//        System.err.println(start + " ------ Configuration started: " + (all_pools.size() + 1));
//
//        RunPool pool = new RunPool(N_RUNS, ENCODING, CONTEST_TYPE, FITNESS_FUNCTION, GENETIC_OPERATORS, initialPopulation);
//        pool.runProcesses();
//        pool.calculateIterationsStatistics();
//        System.err.println("Exporting results, calculation time : " + (System.currentTimeMillis() - startTime) / 1000 + " sec");
//        Export.exportStandaloneRuns(pool);
//
//        all_pools.add(new RunPoolStatsData(pool));
//        n_pools.add(new RunPoolStatsData(pool));
//        System.err.println("Configuration Succeed: " + all_pools.size() + ", total time : " +
//                (System.currentTimeMillis() - startTime) / 1000 + " sec");
//
//    }
//
//    public static List<Individual> initPopulation(FitnessFunction function, Encoding encoding) {
//        List<Individual> population = new ArrayList<>();
//        for (int i = 0; i < N - 1; i++) {
//            Individual individual = new Individual();
//            individual.fillRandomly(function.getLength());
//            population.add(individual);
//        }
//        population.add(function.getBest());
//        return population;
//    }
//
//    private static float getMutationProbability() {
//        float res;
//        if (GeneticUtils.FITNESS_FUNCTION.getLength() == 10) {
//            res = 0.0001f / (GeneticUtils.N / 100.f);
//        } else {
//            res = 0.00001f / (GeneticUtils.N / 100.f);
//        }
//        return res;
//    }
}

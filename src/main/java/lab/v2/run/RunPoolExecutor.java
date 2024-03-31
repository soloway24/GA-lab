package lab.v2.run;

import lab.parameters.Encoding;
import lab.v2.Individual;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.identifier.ConvergenceIdentifier;
import lab.v2.operator.Operator;
import lab.v2.population.Population;
import lab.v2.selection.Selector;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lab.v2.identifier.ConvergenceIdentifier.getEqualQuantity;
import static lab.v2.identifier.SuccessfulRunIdentifier.getBestIndividual;
import static lab.v2.selection.SelectorType.SUS;
import static lab.v2.util.CalculationUtils.getAverage;
import static lab.v2.util.CalculationUtils.getAverageFitness;
import static lab.v2.util.MetricUtils.*;

@RequiredArgsConstructor
public class RunPoolExecutor {

    private static final int MAX_ITERATIONS = 10000000;
    private static final int CONST_SUS_MAX_ITERATIONS = 10000;

    private final ConvergenceIdentifier convergenceIdentifier;
    private final RunPoolStatsCreator runPoolStatsCreator;

    public List<RunPoolStats> executeAllRunPools(List<RunPool> runPools) {
        return runPools.stream()
                .map(this::executeRunPool)
                .toList();
    }

    public RunPoolStats executeRunPool(RunPool runPool) {
        System.out.println("Executing RunPool: " + runPool.runConfiguration());
        ExecutorService executorService = new ForkJoinPool();
        List<Future<RunStats>> futures = IntStream.range(0, runPool.getSize())
                .mapToObj(i -> executorService.submit(() -> {
                    System.out.println("Run " + i);
                    return executeRun(runPool.runs().get(i));
                }))
                .toList();
        List<RunStats> allRunStats = new ArrayList<>();
        for (Future<RunStats> runStatsFuture : futures) {
            RunStats runStats;
            try {
                runStats = runStatsFuture.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            allRunStats.add(runStats);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        List<RunStats> runPoolStats = IntStream.range(0, runPool.getSize())
//                .mapToObj(i -> {
//                    System.out.println("Run " + i);
//                    return executeRun(runPool.runs().get(i));
//                })
//                .toList();
        return runPoolStatsCreator.create(allRunStats, runPool.runConfiguration());
    }

    public RunStats executeRun(Run run) {
        RunConfiguration runConfiguration = run.runConfiguration();
        FitnessFunctionV2<?, ? extends Number> function = runConfiguration.function();
        Population population = run.population();
        Selector selector = runConfiguration.selector();
        Operator operator = runConfiguration.operator();
        Encoding encoding = population.populationConfiguration().encoding();
        Individual optimal = function.getOptimalIndividual(encoding)
                .orElseThrow(() -> new IllegalStateException("Function " + function + " does not support encoding " + encoding + " !"));

        List<Individual> currentIndividuals = population.individuals();
        List<Individual> parentPool;
        List<Individual> offsprings;
        Map<Individual, ? extends Number> individualToFitness;


        int i = 0;

        // metrics for all functions
        double currentRR = 0;
        double currentTeta = 0;
        Map<Integer, Double> iterationToRR = new HashMap<>();
        Map<Integer, Double> iterationToTeta = new HashMap<>();
        int uniqueXStart = getUniqueBinaryCodes(currentIndividuals).size();


        // metrics for all functions except FConstAll
        int niLoose = 0;
        int numLoose = 0;
        int currentOptimalQuantity = getEqualQuantity(currentIndividuals, optimal);
        int maxOptimalQuantity = currentOptimalQuantity;
        int optSavedNILoose = 0;
        int maxOptSavedNILoose = 0;
        boolean hadOptimal = hasOptimal(currentIndividuals, optimal);

        double currentS = 0;
        Map<Integer, Double> iterationToS = new HashMap<>();

        int maxIterations = getMaxIterations(function, selector);

        while (hasNotConverged(currentIndividuals, operator) && i < maxIterations) {
            individualToFitness = getIndividualToFitness(currentIndividuals, function);
            parentPool = selector.select(individualToFitness);
            offsprings = operator.apply(parentPool);


            // metrics for all functions
            currentRR = getReproductionRate(currentIndividuals, parentPool);
            currentTeta = getLostOfDiversity(currentRR);
            iterationToRR.put(i + 1, currentRR);
            iterationToTeta.put(i + 1, currentTeta);


            // metrics for all functions except FConstAll
            if (!function.isConstant()) {
                // convergence metrics
                boolean hasOptimal = hasOptimal(offsprings, optimal);
                if (hadOptimal && !hasOptimal) {
                    niLoose = i + 1;
                    numLoose++;
                    optSavedNILoose = currentOptimalQuantity;
                    maxOptSavedNILoose = maxOptimalQuantity;
                    hadOptimal = false;
                } else {
                    hadOptimal = hasOptimal;
                    int offspringOptimalQuantity = getEqualQuantity(offsprings, optimal);
                    maxOptimalQuantity = max(offspringOptimalQuantity, maxOptimalQuantity);
                    currentOptimalQuantity = offspringOptimalQuantity;
                }

                // difference metrics
                Map<Individual, ? extends Number> parentPoolToFitness = getIndividualToFitness(parentPool, function);
                currentS = getDifference(individualToFitness, parentPoolToFitness);
                iterationToS.put(i + 1, currentS);
            }

            // metrics only for FConstAll function


            currentIndividuals = offsprings;
            i++;
        }


        individualToFitness = getIndividualToFitness(currentIndividuals, function);

        // metrics for all functions
        int ni = i;
        boolean hasConverged = convergenceIdentifier.hasConverged(currentIndividuals, operator.getOperatorType());
        boolean isSuc = function.isSuccessful(individualToFitness, operator.getOperatorType(), hasConverged);

        double rrStart = iterationToRR.get(1);
        double rrFin = currentRR;
        double rrAvg = getAverage(iterationToRR.values());

        Map.Entry<Integer, ? extends Number> minIterationRR = getMinIteratedValue(iterationToRR);
        Map.Entry<Integer, ? extends Number> maxIterationRR = getMaxIteratedValue(iterationToRR);
        double rrMin = minIterationRR.getValue().doubleValue();
        int niRrMin = minIterationRR.getKey();
        double rrMax = maxIterationRR.getValue().doubleValue();
        int niRrMax = maxIterationRR.getKey();

        double tetaStart = iterationToTeta.get(1);
        double tetaFin = currentTeta;
        double tetaAvg = getAverage(iterationToTeta.values());

        Map.Entry<Integer, ? extends Number> minIterationTeta = getMinIteratedValue(iterationToTeta);
        Map.Entry<Integer, ? extends Number> maxIterationTeta = getMaxIteratedValue(iterationToTeta);
        double tetaMin = minIterationTeta.getValue().doubleValue();
        int niTetaMin = minIterationTeta.getKey();
        double tetaMax = maxIterationTeta.getValue().doubleValue();
        int niTetaMax = maxIterationTeta.getKey();

        int uniqueXFin = getUniqueBinaryCodes(currentIndividuals).size();


        // metrics for all functions except FConstAll
        if (numLoose == 0) {
            maxOptSavedNILoose = getEqualQuantity(currentIndividuals, optimal);
        }
        Individual best = getBestIndividual(individualToFitness);
        double fFound = individualToFitness.get(best).doubleValue();
        double fAvg = getAverageFitness(individualToFitness);

        double sStart = 0, sFin = 0, sAvg = 0, sMin = 0, sMax = 0;
        int niSMin = 0, niSMax = 0;

        if (!function.isConstant()) {
            sStart = iterationToS.get(1);
            sFin = currentS;
            sAvg = getAverage(iterationToS.values());
            Map.Entry<Integer, ? extends Number> minIterationS = getMinIteratedValue(iterationToS);
            Map.Entry<Integer, ? extends Number> maxIterationS = getMaxIteratedValue(iterationToS);
            sMin = minIterationS.getValue().doubleValue();
            niSMin = minIterationS.getKey();
            sMax = maxIterationS.getValue().doubleValue();
            niSMax = maxIterationS.getKey();
        }


        return RunStats.builder()
                .withFinalPopulation(individualToFitness)

                // metrics for all functions
                .withNi(ni)
                .withHasConverged(hasConverged)
                .withIsSuc(isSuc)

                .withRrStart(rrStart)
                .withRrFin(rrFin)
                .withRrAvg(rrAvg)
                .withRrMin(rrMin)
                .withNiRrMin(niRrMin)
                .withRrMax(rrMax)
                .withNiRrMax(niRrMax)

                .withTetaStart(tetaStart)
                .withTetaFin(tetaFin)
                .withTetaAvg(tetaAvg)
                .withTetaMin(tetaMin)
                .withNiTetaMin(niTetaMin)
                .withTetaMax(tetaMax)
                .withNiTetaMax(niTetaMax)

                .withUniqueXStart(uniqueXStart)
                .withUniqueXFin(uniqueXFin)

                // metrics for all functions except FConstAll
                .withFFound(fFound)
                .withFAvg(fAvg)
                .withNiLoose(niLoose)
                .withNumLoose(numLoose)
                .withOptSavedNILoose(optSavedNILoose)
                .withMaxOptSavedNILoose(maxOptSavedNILoose)

                // metrics only for FConstAll function
                .withSStart(sStart)
                .withSFin(sFin)
                .withSAvg(sAvg)
                .withSMin(sMin)
                .withNiSMin(niSMin)
                .withSMax(sMax)
                .withNiSMax(niSMax)

                .build();
    }

    private int getMaxIterations(FitnessFunctionV2<?, ? extends Number> function, Selector selector) {
        return function.isConstant() && selector.getSelectorType() == SUS
                ? CONST_SUS_MAX_ITERATIONS
                : MAX_ITERATIONS;
    }

    private boolean hasOptimal(Collection<Individual> individuals, Individual optimal) {
        return individuals.stream()
                .map(Individual::getBinaryCode)
                .anyMatch(code -> optimal.getBinaryCode().equals(code));
    }

    private boolean hasNotConverged(List<Individual> individuals, Operator operator) {
        return !convergenceIdentifier.hasConverged(individuals, operator.getOperatorType());
    }

    private <ARG_T extends Number, RES_T extends Number> Map<Individual, RES_T> getIndividualToFitness(List<Individual> individuals,
                                                                                                       FitnessFunctionV2<ARG_T, RES_T> function) {
        return individuals
                .stream()
                .collect(toUnmodifiableMap(identity(), function::evaluate));
    }
}

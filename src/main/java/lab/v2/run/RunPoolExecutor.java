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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lab.v2.identifier.ConvergenceIdentifier.getEqualQuantity;
import static lab.v2.identifier.SuccessfulRunIdentifier.getBestIndividual;
import static lab.v2.selection.SelectorType.SUS;
import static lab.v2.util.CalculationUtils.*;
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

    public List<RunPoolStats> executeAllRunPoolsParallel(List<RunPool> runPools) {
        return runPools.stream()
                .map(this::executeRunPoolParallel)
                .toList();
    }

    public RunPoolStats executeRunPool(RunPool runPool) {
        System.out.println("Executing RunPool: " + runPool.runConfiguration());
        List<RunStats> allRunStats = executeAndGetAllRunStats(runPool);
        return runPoolStatsCreator.create(allRunStats, runPool.runConfiguration());
    }

    public RunPoolStats executeRunPoolParallel(RunPool runPool) {
        System.out.println("Executing RunPool: " + runPool.runConfiguration());

        ExecutorService executorService = new ForkJoinPool();
        List<Future<RunStats>> futures = executeParallelAndGetAllRunStats(runPool, executorService);
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
        double currentI = 0;
        double currentPr = 0;

        Map<Integer, Double> iterationToS = new HashMap<>();
        Map<Integer, Double> iterationToI = new HashMap<>();
        Map<Integer, Double> generationToPr = new HashMap<>();
        Map<Integer, Double> iterationToGr = new HashMap<>();

        int previousBestQ = Integer.MIN_VALUE;
        double previousBestF = Double.MIN_VALUE;
        double currentGr = 0;
        double grLate = Double.MIN_VALUE;
        int niGrLate = Integer.MIN_VALUE;

        Map<Integer, Double> avgFs = new HashMap<>();
        Map<Integer, Double> maxFs = new HashMap<>();
        Map<Integer, Double> sigmaFs = new HashMap<>();
        Map<Integer, Integer> uniques = new HashMap<>();
        Map<Integer, Double> optimalRatios = new HashMap<>();
        Map<Integer, Double> bestRatios = new HashMap<>();

        int maxIterations = getMaxIterations(function, selector);

        while (hasNotConverged(currentIndividuals, operator) && i < maxIterations) {
            individualToFitness = getIndividualToFitness(currentIndividuals, function);
            parentPool = selector.select(individualToFitness);
            offsprings = operator.apply(parentPool);

            double avgF = getAverageFitness(individualToFitness);
            avgFs.put(i, avgF);
            Individual best = getBestIndividual(individualToFitness);
            double maxF = individualToFitness.get(best).doubleValue();
            maxFs.put(i, maxF);
            double sigmaF = getStandardDeviation(individualToFitness.values(), avgF);
            sigmaFs.put(i, sigmaF);
            int unique = getUniqueBinaryCodes(currentIndividuals).size();
            uniques.put(i, unique);

            int optimalQ = getEqualQuantity(currentIndividuals, optimal);
            double optimalRatio = (double) optimalQ / currentIndividuals.size();
            optimalRatios.put(i, optimalRatio);
            int bestQ = getEqualQuantity(currentIndividuals, best);
            double bestRatio = (double) bestQ / currentIndividuals.size();
            bestRatios.put(i, bestRatio);


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

                double parentAvgF = getAverageFitness(parentPoolToFitness);
                currentI = sigmaF == 0
                        ? 1
                        : (parentAvgF - avgF) / sigmaF;
                iterationToI.put(i + 1, currentI);

                Map<Individual, Double> scaledIndividuals = selector.scale(individualToFitness);
                double bestScaledF = getMaxDouble(scaledIndividuals.values());
                double avgScaledF = getAverage(scaledIndividuals.values());
                currentPr = bestScaledF / avgScaledF;
                generationToPr.put(i, currentPr);

                currentGr = maxF >= previousBestF
                        ? (double) bestQ / previousBestQ
                        : 0;
                iterationToGr.put(i, currentGr);
                if (optimalQ >= 0.5 * runConfiguration.populationSize() && grLate == Double.MIN_VALUE) {
                    grLate = currentGr;
                    niGrLate = i;
                }
            }

            // metrics only for FConstAll function

            previousBestF = maxF;
            previousBestQ = bestQ;
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
        uniques.put(ni, uniqueXFin);

        // metrics for all functions except FConstAll
        if (numLoose == 0) {
            maxOptSavedNILoose = getEqualQuantity(currentIndividuals, optimal);
        }
        Individual best = getBestIndividual(individualToFitness);
        double fFound = individualToFitness.get(best).doubleValue();
        double fAvg = getAverageFitness(individualToFitness);
        maxFs.put(ni, fFound);
        avgFs.put(ni, fAvg);

        int optimalQ = getEqualQuantity(currentIndividuals, optimal);
        double optimalRatio = (double) optimalQ / currentIndividuals.size();
        optimalRatios.put(ni, optimalRatio);
        int bestQ = getEqualQuantity(currentIndividuals, best);
        double bestRatio = (double) bestQ / currentIndividuals.size();
        bestRatios.put(ni, bestRatio);

        double sigmaFFin = getStandardDeviation(individualToFitness.values(), fAvg);
        sigmaFs.put(ni, sigmaFFin);

        double prFin = fFound / fAvg;
        generationToPr.put(ni, prFin);

        double sStart = 0;
        double sFin = 0;
        double sMin = 0;
        int niSMin = 0;
        double sMax = 0;
        int niSMax = 0;
        double sAvg = 0;

        double iStart = 0;
        double iMin = 0;
        int niImin = 0;
        double iMax = 0;
        int niImax = 0;
        double iAvg = 0;

        double grStart = 0;
        double grEarly = 0;
        double grAvg = 0;

        double prStart = 0;
        double prMin = 0;
        int niPrMin = 0;
        double prMax = 0;
        int niPrMax = 0;
        double prAvg = 0;

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

            Map.Entry<Integer, ? extends Number> minIterationI = getMinIteratedValue(iterationToI);
            Map.Entry<Integer, ? extends Number> maxIterationI = getMaxIteratedValue(iterationToI);
            iStart = iterationToI.get(1);
            iMin = minIterationI.getValue().doubleValue();
            niImin = minIterationI.getKey();
            iMax = maxIterationI.getValue().doubleValue();
            niImax = maxIterationI.getKey();
            iAvg = getAverage(iterationToI.values());

            iterationToGr.remove(0);
            currentGr = fFound >= previousBestF
                    ? (double) bestQ / previousBestQ
                    : 0;
            iterationToGr.put(ni, currentGr);
            grStart = iterationToGr.get(1);
            grEarly = iterationToGr.get(3);
            grAvg = getAverage(iterationToGr.values());

            Map.Entry<Integer, ? extends Number> minGenerationPr = getMinIteratedValue(generationToPr);
            Map.Entry<Integer, ? extends Number> maxGenerationPr = getMaxIteratedValue(generationToPr);
            prStart = generationToPr.get(1);
            prMin = minGenerationPr.getValue().doubleValue();
            niPrMin = minGenerationPr.getKey();
            prMax = maxGenerationPr.getValue().doubleValue();
            niPrMax = maxGenerationPr.getKey();
            prAvg = getAverage(generationToPr.values());
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

                .withIStart(iStart)
                .withIMin(iMin)
                .withNiImin(niImin)
                .withIMax(iMax)
                .withNiImax(niImax)
                .withIAvg(iAvg)

                .withGrStart(grStart)
                .withGrEarly(grEarly)
                .withGrLate(grLate)
                .withNiGrLate(niGrLate)
                .withGrAvg(grAvg)

                .withPrStart(prStart)
                .withPrMin(prMin)
                .withNiPrMin(niPrMin)
                .withPrMax(prMax)
                .withNiPrMax(niPrMax)
                .withPrAvg(prAvg)

                .withAvgFs(getOrderedValues(avgFs))
                .withMaxFs(getOrderedValues(maxFs))
                .withSigmaFs(getOrderedValues(sigmaFs))
                .withOptimalRatios(getOrderedValues(optimalRatios))
                .withBestRatios(getOrderedValues(bestRatios))
                .withSs(getOrderedValuesPlus1(iterationToS))
                .withRrs(getOrderedValuesPlus1(iterationToRR))
                .withTetas(getOrderedValuesPlus1(iterationToTeta))
                .withUniques(getOrderedIntValues(uniques))
                .withIs(getOrderedValuesPlus1(iterationToI))
                .withPrs(getOrderedValues(generationToPr))
                .withGrs(getOrderedValuesPlus1(iterationToGr))

                .build();
    }

    private List<Double> getOrderedValues(Map<Integer, Double> iteratedValues) {
        return IntStream.range(0, iteratedValues.size())
                .mapToObj(iteratedValues::get)
                .collect(toList());
    }

    private List<Double> getOrderedValuesPlus1(Map<Integer, Double> iteratedValues) {
        return IntStream.rangeClosed(1, iteratedValues.size())
                .mapToObj(iteratedValues::get)
                .collect(toList());
    }

    private List<Integer> getOrderedIntValues(Map<Integer, Integer> iteratedValues) {
        return IntStream.range(0, iteratedValues.size())
                .mapToObj(iteratedValues::get)
                .collect(toList());
    }

    private List<RunStats> executeAndGetAllRunStats(RunPool runPool) {
        return IntStream.range(0, runPool.getSize())
                .mapToObj(i -> {
                    System.out.println("Run " + i);
                    return executeRun(runPool.runs().get(i));
                })
                .toList();
    }

    private List<Future<RunStats>> executeParallelAndGetAllRunStats(RunPool runPool, ExecutorService executorService) {
        return IntStream.range(0, runPool.getSize())
                .mapToObj(i -> executorService.submit(() -> {
                    System.out.println("Run " + i);
                    return executeRun(runPool.runs().get(i));
                }))
                .toList();
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

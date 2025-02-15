package lab.run;

import lab.Individual;
import lab.encoding.Encoding;
import lab.export.Homogeneity;
import lab.function.FitnessFunction;
import lab.identifier.ConvergenceIdentifier;
import lab.identifier.SuccessfulRunIdentifier;
import lab.metric.IndividualMetrics;
import lab.operator.Operator;
import lab.operator.OperatorType;
import lab.population.Population;
import lab.selection.Selector;
import lab.util.CalculationUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.util.Collections.shuffle;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static lab.encoding.Decoder.decode;
import static lab.identifier.ConvergenceIdentifier.getEqualQuantity;
import static lab.identifier.ConvergenceIdentifier.isHomogenous;
import static lab.selection.SelectorType.SUS;
import static lab.util.MetricUtils.*;

@Component
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

        try {
            RunConfiguration runConfiguration = run.runConfiguration();
            FitnessFunction<?, ? extends Number> function = runConfiguration.function();
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
            double currentFish = 0;
            double currentKendall = 0;

            Map<Integer, Double> iterationToS = new HashMap<>();
            Map<Integer, Double> iterationToI = new HashMap<>();
            Map<Integer, Double> generationToPr = new HashMap<>();
            Map<Integer, Double> iterationToGr = new HashMap<>();
            Map<Integer, Double> iterationToFish = new HashMap<>();
            Map<Integer, Double> iterationToKendall = new HashMap<>();

            int previousBestQ = Integer.MIN_VALUE;
            double previousBestF = Double.MIN_VALUE;
            double currentGr = 0;
            double grLate = Double.MIN_VALUE;
            int niGrLate = Integer.MIN_VALUE;

            Map<Integer, Double> generationToAvgF = new HashMap<>();
            Map<Integer, Double> generationToMaxF = new HashMap<>();
            Map<Integer, Double> generationToSigmaF = new HashMap<>();
            Map<Integer, Integer> generationToUniqueX = new HashMap<>();
            Map<Integer, Double> generationToOptimalRatio = new HashMap<>();
            Map<Integer, Double> generationToBestRatio = new HashMap<>();

            Map<Integer, List<IndividualMetrics>> generationToIndMetrics = new HashMap<>();
            Map<Homogeneity, List<IndividualMetrics>> homogeneityToIndMetrics = new HashMap<>();

            int maxIterations = getMaxIterations(function, selector);
            individualToFitness = getIndividualToFitness(currentIndividuals, function);

            while (hasNotConverged(individualToFitness, operator) && i < maxIterations) {
                if (i % 10000 == 0 && i > 0) {
                    System.out.println(i);
                }
                if (i % 50000 == 0 && i > 0) {
                    Map<Pair<String, Double>, Long> fitnessToCount = individualToFitness.entrySet()
                            .stream()
                            .collect(groupingBy(entry -> Pair.of(entry.getKey().getBinaryCode(), entry.getValue().doubleValue()), counting()));
                    System.out.println(fitnessToCount);
                }

                parentPool = selector.select(individualToFitness);
                List<Individual> parentCopies = new ArrayList<>(parentPool);
                shuffle(parentCopies);
                offsprings = operator.apply(parentPool);

                double avgF = CalculationUtils.getAverageFitness(individualToFitness);
                generationToAvgF.put(i, avgF);
                Individual best = SuccessfulRunIdentifier.getBestIndividual(individualToFitness);
                double maxF = individualToFitness.get(best).doubleValue();
                generationToMaxF.put(i, maxF);
                double sigmaF = getStandardDeviation(individualToFitness.values(), avgF);
                generationToSigmaF.put(i, sigmaF);
                int unique = getUniqueBinaryCodes(currentIndividuals).size();
                generationToUniqueX.put(i, unique);

                int optimalQ = getEqualQuantity(currentIndividuals, optimal);
                double optimalRatio = (double) optimalQ / currentIndividuals.size();
                generationToOptimalRatio.put(i, optimalRatio);
                int bestQ = getEqualQuantity(currentIndividuals, best);
                double bestRatio = (double) bestQ / currentIndividuals.size();
                generationToBestRatio.put(i, bestRatio);


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

                    double parentAvgF = CalculationUtils.getAverageFitness(parentPoolToFitness);
                    currentI = sigmaF == 0
                            ? 1
                            : (parentAvgF - avgF) / sigmaF;
                    iterationToI.put(i + 1, currentI);

                    Map<Individual, Double> scaledIndividuals = selector.scale(individualToFitness);
                    double bestScaledF = CalculationUtils.getMaxDouble(scaledIndividuals.values());
                    double avgScaledF = CalculationUtils.getAverage(scaledIndividuals.values());
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


                    currentFish = computePFET(individualToFitness, parentPool);
                    iterationToFish.put(i + 1, currentFish);

                    currentKendall = computeKendallTauB(individualToFitness, parentPool);
                    iterationToKendall.put(i + 1, currentKendall);
                }

                populateHistogramData(function, currentIndividuals, i, generationToIndMetrics, homogeneityToIndMetrics);

                previousBestF = maxF;
                previousBestQ = bestQ;
                currentIndividuals = offsprings;
                individualToFitness = getIndividualToFitness(currentIndividuals, function);
                i++;
            }


            individualToFitness = getIndividualToFitness(currentIndividuals, function);

            // metrics for all functions
            int ni = i;
            boolean hasConverged = convergenceIdentifier.hasConverged(individualToFitness, operator.getOperatorType());
            boolean isSuc = function.isSuccessful(individualToFitness, operator.getOperatorType(), hasConverged);

            double rrStart = iterationToRR.get(1);
            double rrFin = currentRR;
            double rrAvg = CalculationUtils.getAverage(iterationToRR.values());

            Map.Entry<Integer, ? extends Number> minIterationRR = getMinIteratedValue(iterationToRR);
            Map.Entry<Integer, ? extends Number> maxIterationRR = getMaxIteratedValue(iterationToRR);
            double rrMin = minIterationRR.getValue().doubleValue();
            int niRrMin = minIterationRR.getKey();
            double rrMax = maxIterationRR.getValue().doubleValue();
            int niRrMax = maxIterationRR.getKey();

            double tetaStart = iterationToTeta.get(1);
            double tetaFin = currentTeta;
            double tetaAvg = CalculationUtils.getAverage(iterationToTeta.values());

            Map.Entry<Integer, ? extends Number> minIterationTeta = getMinIteratedValue(iterationToTeta);
            Map.Entry<Integer, ? extends Number> maxIterationTeta = getMaxIteratedValue(iterationToTeta);
            double tetaMin = minIterationTeta.getValue().doubleValue();
            int niTetaMin = minIterationTeta.getKey();
            double tetaMax = maxIterationTeta.getValue().doubleValue();
            int niTetaMax = maxIterationTeta.getKey();

            int uniqueXFin = getUniqueBinaryCodes(currentIndividuals).size();
            generationToUniqueX.put(ni, uniqueXFin);

            // metrics for all functions except FConstAll
            if (numLoose == 0) {
                maxOptSavedNILoose = getEqualQuantity(currentIndividuals, optimal);
            }
            Individual best = SuccessfulRunIdentifier.getBestIndividual(individualToFitness);
            double fFound = individualToFitness.get(best).doubleValue();
            double fAvg = CalculationUtils.getAverageFitness(individualToFitness);
            generationToMaxF.put(ni, fFound);
            generationToAvgF.put(ni, fAvg);

            int optimalQ = getEqualQuantity(currentIndividuals, optimal);
            double optimalRatio = (double) optimalQ / currentIndividuals.size();
            generationToOptimalRatio.put(ni, optimalRatio);
            int bestQ = getEqualQuantity(currentIndividuals, best);
            double bestRatio = (double) bestQ / currentIndividuals.size();
            generationToBestRatio.put(ni, bestRatio);

            double sigmaFFin = getStandardDeviation(individualToFitness.values(), fAvg);
            generationToSigmaF.put(ni, sigmaFFin);

            double prFin = fFound / fAvg;
            generationToPr.put(ni, prFin);

            List<IndividualMetrics> individualMetrics = buildAllIndividualMetrics(currentIndividuals, function);
            generationToIndMetrics.put(ni, individualMetrics);

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

            double fishStart = 0;
            double fishMin = 0;
            int niFishMin = 0;
            double fishMax = 0;
            int niFishMax = 0;
            double fishAvg = 0;

            double kendallStart = 0;
            double kendallMin = 0;
            int niKendallMin = 0;
            double kendallMax = 0;
            int niKendallMax = 0;
            double kendallAvg = 0;


            if (!function.isConstant()) {
                sStart = iterationToS.get(1);
                sFin = currentS;
                sAvg = CalculationUtils.getAverage(iterationToS.values());
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
                iAvg = CalculationUtils.getAverage(iterationToI.values());

                iterationToGr.remove(0);
                currentGr = fFound >= previousBestF
                        ? (double) bestQ / previousBestQ
                        : 0;
                iterationToGr.put(ni, currentGr);
                grStart = iterationToGr.get(1);
                grEarly = ofNullable(iterationToGr.get(2)).orElse(Double.MIN_VALUE);
                grAvg = CalculationUtils.getAverage(iterationToGr.values());

                Map.Entry<Integer, ? extends Number> minGenerationPr = getMinIteratedValue(generationToPr);
                Map.Entry<Integer, ? extends Number> maxGenerationPr = getMaxIteratedValue(generationToPr);
                prStart = generationToPr.get(1);
                prMin = minGenerationPr.getValue().doubleValue();
                niPrMin = minGenerationPr.getKey();
                prMax = maxGenerationPr.getValue().doubleValue();
                niPrMax = maxGenerationPr.getKey();
                prAvg = CalculationUtils.getAverage(generationToPr.values());

                Map.Entry<Integer, ? extends Number> minIterationFish = getMinIteratedValue(iterationToFish);
                Map.Entry<Integer, ? extends Number> maxIterationFish = getMaxIteratedValue(iterationToFish);
                fishStart = iterationToFish.get(1);
                fishMin = minIterationFish.getValue().doubleValue();
                niFishMin = minIterationFish.getKey();
                fishMax = maxIterationFish.getValue().doubleValue();
                niFishMax = maxIterationFish.getKey();
                fishAvg = CalculationUtils.getAverage(iterationToFish.values());

                Map.Entry<Integer, ? extends Number> minIterationKendall = getMinIteratedValue(iterationToKendall);
                Map.Entry<Integer, ? extends Number> maxIterationKendall = getMaxIteratedValue(iterationToKendall);
                kendallStart = iterationToKendall.get(1);
                kendallMin = minIterationKendall.getValue().doubleValue();
                niKendallMin = minIterationKendall.getKey();
                kendallMax = maxIterationKendall.getValue().doubleValue();
                niKendallMax = maxIterationKendall.getKey();
                kendallAvg = CalculationUtils.getAverage(iterationToKendall.values());
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

                    .withFishStart(fishStart)
                    .withFishMin(fishMin)
                    .withNiFishMin(niFishMin)
                    .withFishMax(fishMax)
                    .withNiFishMax(niFishMax)
                    .withFishAvg(fishAvg)

                    .withKendallStart(kendallStart)
                    .withKendallMin(kendallMin)
                    .withNiKendallMin(niKendallMin)
                    .withKendallMax(kendallMax)
                    .withNiKendallMax(niKendallMax)
                    .withKendallAvg(kendallAvg)

                    .withAvgFs(getOrderedValues(generationToAvgF))
                    .withMaxFs(getOrderedValues(generationToMaxF))
                    .withSigmaFs(getOrderedValues(generationToSigmaF))
                    .withOptimalRatios(getOrderedValues(generationToOptimalRatio))
                    .withBestRatios(getOrderedValues(generationToBestRatio))
                    .withSs(getOrderedValuesPlus1(iterationToS))
                    .withRrs(getOrderedValuesPlus1(iterationToRR))
                    .withTetas(getOrderedValuesPlus1(iterationToTeta))
                    .withUniques(getOrderedIntValues(generationToUniqueX))
                    .withIs(getOrderedValuesPlus1(iterationToI))
                    .withPrs(getOrderedValues(generationToPr))
                    .withGrs(getOrderedValuesPlus1(iterationToGr))
                    .withFishes(getOrderedValuesPlus1(iterationToFish))
                    .withKendalls(getOrderedValuesPlus1(iterationToKendall))

                    .withGenerationToIndMetrics(generationToIndMetrics)
                    .withHomogeneityToIndMetrics(homogeneityToIndMetrics)

                    .build();
        } catch (Exception e) {
            System.out.println(Thread.currentThread() + " exception: " + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    public RunStats executeRun(Run run, int runIndex, int runPoolSize, int runPoolIndex, int runPoolQuantity) {
        System.out.println("Executing run " + (runIndex + 1) + "/" + runPoolSize + ", run pool " + (runPoolIndex + 1) + "/" + runPoolQuantity);

        try {
            RunConfiguration runConfiguration = run.runConfiguration();
            FitnessFunction<?, ? extends Number> function = runConfiguration.function();
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
            double currentFish = 0;
            double currentKendall = 0;

            Map<Integer, Double> iterationToS = new HashMap<>();
            Map<Integer, Double> iterationToI = new HashMap<>();
            Map<Integer, Double> generationToPr = new HashMap<>();
            Map<Integer, Double> iterationToGr = new HashMap<>();
            Map<Integer, Double> iterationToFish = new HashMap<>();
            Map<Integer, Double> iterationToKendall = new HashMap<>();

            int previousBestQ = Integer.MIN_VALUE;
            double previousBestF = Double.MIN_VALUE;
            double currentGr = 0;
            double grLate = Double.MIN_VALUE;
            int niGrLate = Integer.MIN_VALUE;

            Map<Integer, Double> generationToAvgF = new HashMap<>();
            Map<Integer, Double> generationToMaxF = new HashMap<>();
            Map<Integer, Double> generationToSigmaF = new HashMap<>();
            Map<Integer, Integer> generationToUniqueX = new HashMap<>();
            Map<Integer, Double> generationToOptimalRatio = new HashMap<>();
            Map<Integer, Double> generationToBestRatio = new HashMap<>();

            Map<Integer, List<IndividualMetrics>> generationToIndMetrics = new HashMap<>();
            Map<Homogeneity, List<IndividualMetrics>> homogeneityToIndMetrics = new HashMap<>();

            int maxIterations = getMaxIterations(function, selector);
            individualToFitness = getIndividualToFitness(currentIndividuals, function);

            while (hasNotConverged(individualToFitness, operator) && i < maxIterations && shouldNotStop(individualToFitness, operator, selector, optimal)) {
                if (i % 10000 == 0 && i > 0) {
                    System.out.println("run " + (runIndex + 1) + "/" + runPoolSize + ", run pool " + (runPoolIndex + 1) + "/" + runPoolQuantity
                            + " iteration " + i);
                }
                if (i % 50000 == 0 && i > 0) {
                    Map<Pair<String, Double>, Long> fitnessToCount = individualToFitness.entrySet()
                            .stream()
                            .collect(groupingBy(entry -> Pair.of(entry.getKey().getBinaryCode(), entry.getValue().doubleValue()), counting()));
                    System.out.println("run " + (runIndex + 1) + "/" + runPoolSize + ", run pool " + (runPoolIndex + 1) + "/" + runPoolQuantity
                            + " iteration " + i + ", fitnessToCount = " + fitnessToCount);
                }

                parentPool = selector.select(individualToFitness);
                List<Individual> parentCopies = new ArrayList<>(parentPool);
                shuffle(parentCopies);
                offsprings = operator.apply(parentPool);

                double avgF = CalculationUtils.getAverageFitness(individualToFitness);
                generationToAvgF.put(i, avgF);
                Individual best = SuccessfulRunIdentifier.getBestIndividual(individualToFitness);
                double maxF = individualToFitness.get(best).doubleValue();
                generationToMaxF.put(i, maxF);
                double sigmaF = getStandardDeviation(individualToFitness.values(), avgF);
                generationToSigmaF.put(i, sigmaF);
                int unique = getUniqueBinaryCodes(currentIndividuals).size();
                generationToUniqueX.put(i, unique);

                int optimalQ = getEqualQuantity(currentIndividuals, optimal);
                double optimalRatio = (double) optimalQ / currentIndividuals.size();
                generationToOptimalRatio.put(i, optimalRatio);
                int bestQ = getEqualQuantity(currentIndividuals, best);
                double bestRatio = (double) bestQ / currentIndividuals.size();
                generationToBestRatio.put(i, bestRatio);


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

                    double parentAvgF = CalculationUtils.getAverageFitness(parentPoolToFitness);
                    currentI = sigmaF == 0
                            ? 1
                            : (parentAvgF - avgF) / sigmaF;
                    iterationToI.put(i + 1, currentI);

                    Map<Individual, Double> scaledIndividuals = selector.scale(individualToFitness);
                    double bestScaledF = CalculationUtils.getMaxDouble(scaledIndividuals.values());
                    double avgScaledF = CalculationUtils.getAverage(scaledIndividuals.values());
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


                    currentFish = computePFET(individualToFitness, parentPool);
                    iterationToFish.put(i + 1, currentFish);

                    currentKendall = computeKendallTauB(individualToFitness, parentPool);
                    iterationToKendall.put(i + 1, currentKendall);
                }

                populateHistogramData(function, currentIndividuals, i, generationToIndMetrics, homogeneityToIndMetrics);

                previousBestF = maxF;
                previousBestQ = bestQ;
                currentIndividuals = offsprings;
                individualToFitness = getIndividualToFitness(currentIndividuals, function);
                i++;
            }


            individualToFitness = getIndividualToFitness(currentIndividuals, function);

            // metrics for all functions
            int ni = i;
            boolean hasConverged = convergenceIdentifier.hasConverged(individualToFitness, operator.getOperatorType());
            boolean isSuc = function.isSuccessful(individualToFitness, operator.getOperatorType(), hasConverged);

            double rrStart = iterationToRR.get(1);
            double rrFin = currentRR;
            double rrAvg = CalculationUtils.getAverage(iterationToRR.values());

            Map.Entry<Integer, ? extends Number> minIterationRR = getMinIteratedValue(iterationToRR);
            Map.Entry<Integer, ? extends Number> maxIterationRR = getMaxIteratedValue(iterationToRR);
            double rrMin = minIterationRR.getValue().doubleValue();
            int niRrMin = minIterationRR.getKey();
            double rrMax = maxIterationRR.getValue().doubleValue();
            int niRrMax = maxIterationRR.getKey();

            double tetaStart = iterationToTeta.get(1);
            double tetaFin = currentTeta;
            double tetaAvg = CalculationUtils.getAverage(iterationToTeta.values());

            Map.Entry<Integer, ? extends Number> minIterationTeta = getMinIteratedValue(iterationToTeta);
            Map.Entry<Integer, ? extends Number> maxIterationTeta = getMaxIteratedValue(iterationToTeta);
            double tetaMin = minIterationTeta.getValue().doubleValue();
            int niTetaMin = minIterationTeta.getKey();
            double tetaMax = maxIterationTeta.getValue().doubleValue();
            int niTetaMax = maxIterationTeta.getKey();

            int uniqueXFin = getUniqueBinaryCodes(currentIndividuals).size();
            generationToUniqueX.put(ni, uniqueXFin);

            // metrics for all functions except FConstAll
            if (numLoose == 0) {
                maxOptSavedNILoose = getEqualQuantity(currentIndividuals, optimal);
            }
            Individual best = SuccessfulRunIdentifier.getBestIndividual(individualToFitness);
            double fFound = individualToFitness.get(best).doubleValue();
            double fAvg = CalculationUtils.getAverageFitness(individualToFitness);
            generationToMaxF.put(ni, fFound);
            generationToAvgF.put(ni, fAvg);

            int optimalQ = getEqualQuantity(currentIndividuals, optimal);
            double optimalRatio = (double) optimalQ / currentIndividuals.size();
            generationToOptimalRatio.put(ni, optimalRatio);
            int bestQ = getEqualQuantity(currentIndividuals, best);
            double bestRatio = (double) bestQ / currentIndividuals.size();
            generationToBestRatio.put(ni, bestRatio);

            double sigmaFFin = getStandardDeviation(individualToFitness.values(), fAvg);
            generationToSigmaF.put(ni, sigmaFFin);

            double prFin = fFound / fAvg;
            generationToPr.put(ni, prFin);

            List<IndividualMetrics> individualMetrics = buildAllIndividualMetrics(currentIndividuals, function);
            generationToIndMetrics.put(ni, individualMetrics);

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

            double fishStart = 0;
            double fishMin = 0;
            int niFishMin = 0;
            double fishMax = 0;
            int niFishMax = 0;
            double fishAvg = 0;

            double kendallStart = 0;
            double kendallMin = 0;
            int niKendallMin = 0;
            double kendallMax = 0;
            int niKendallMax = 0;
            double kendallAvg = 0;


            if (!function.isConstant()) {
                sStart = iterationToS.get(1);
                sFin = currentS;
                sAvg = CalculationUtils.getAverage(iterationToS.values());
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
                iAvg = CalculationUtils.getAverage(iterationToI.values());

                iterationToGr.remove(0);
                currentGr = fFound >= previousBestF
                        ? (double) bestQ / previousBestQ
                        : 0;
                iterationToGr.put(ni, currentGr);
                grStart = iterationToGr.get(1);
                grEarly = ofNullable(iterationToGr.get(2)).orElse(Double.MIN_VALUE);
                grAvg = CalculationUtils.getAverage(iterationToGr.values());

                Map.Entry<Integer, ? extends Number> minGenerationPr = getMinIteratedValue(generationToPr);
                Map.Entry<Integer, ? extends Number> maxGenerationPr = getMaxIteratedValue(generationToPr);
                prStart = generationToPr.get(1);
                prMin = minGenerationPr.getValue().doubleValue();
                niPrMin = minGenerationPr.getKey();
                prMax = maxGenerationPr.getValue().doubleValue();
                niPrMax = maxGenerationPr.getKey();
                prAvg = CalculationUtils.getAverage(generationToPr.values());

                Map.Entry<Integer, ? extends Number> minIterationFish = getMinIteratedValue(iterationToFish);
                Map.Entry<Integer, ? extends Number> maxIterationFish = getMaxIteratedValue(iterationToFish);
                fishStart = iterationToFish.get(1);
                fishMin = minIterationFish.getValue().doubleValue();
                niFishMin = minIterationFish.getKey();
                fishMax = maxIterationFish.getValue().doubleValue();
                niFishMax = maxIterationFish.getKey();
                fishAvg = CalculationUtils.getAverage(iterationToFish.values());

                Map.Entry<Integer, ? extends Number> minIterationKendall = getMinIteratedValue(iterationToKendall);
                Map.Entry<Integer, ? extends Number> maxIterationKendall = getMaxIteratedValue(iterationToKendall);
                kendallStart = iterationToKendall.get(1);
                kendallMin = minIterationKendall.getValue().doubleValue();
                niKendallMin = minIterationKendall.getKey();
                kendallMax = maxIterationKendall.getValue().doubleValue();
                niKendallMax = maxIterationKendall.getKey();
                kendallAvg = CalculationUtils.getAverage(iterationToKendall.values());
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

                    .withFishStart(fishStart)
                    .withFishMin(fishMin)
                    .withNiFishMin(niFishMin)
                    .withFishMax(fishMax)
                    .withNiFishMax(niFishMax)
                    .withFishAvg(fishAvg)

                    .withKendallStart(kendallStart)
                    .withKendallMin(kendallMin)
                    .withNiKendallMin(niKendallMin)
                    .withKendallMax(kendallMax)
                    .withNiKendallMax(niKendallMax)
                    .withKendallAvg(kendallAvg)

                    .withAvgFs(getOrderedValues(generationToAvgF))
                    .withMaxFs(getOrderedValues(generationToMaxF))
                    .withSigmaFs(getOrderedValues(generationToSigmaF))
                    .withOptimalRatios(getOrderedValues(generationToOptimalRatio))
                    .withBestRatios(getOrderedValues(generationToBestRatio))
                    .withSs(getOrderedValuesPlus1(iterationToS))
                    .withRrs(getOrderedValuesPlus1(iterationToRR))
                    .withTetas(getOrderedValuesPlus1(iterationToTeta))
                    .withUniques(getOrderedIntValues(generationToUniqueX))
                    .withIs(getOrderedValuesPlus1(iterationToI))
                    .withPrs(getOrderedValues(generationToPr))
                    .withGrs(getOrderedValuesPlus1(iterationToGr))
                    .withFishes(getOrderedValuesPlus1(iterationToFish))
                    .withKendalls(getOrderedValuesPlus1(iterationToKendall))

                    .withGenerationToIndMetrics(generationToIndMetrics)
                    .withHomogeneityToIndMetrics(homogeneityToIndMetrics)

                    .build();
        } catch (Exception e) {
            System.out.println(Thread.currentThread() + " exception: " + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    private boolean shouldNotStop(Map<Individual, ? extends Number> individualToFitness, Operator operator, Selector selector, Individual optimal) {
        if (selector.getSelectorType() != SUS || operator.getOperatorType() != OperatorType.CROSSOVER) {
            return true;
        }

        Map<Pair<String, Double>, Long> fitnessToCount = individualToFitness.entrySet()
                .stream()
                .collect(groupingBy(entry -> Pair.of(entry.getKey().getBinaryCode(), entry.getValue().doubleValue()), counting()));
        if (fitnessToCount.size() > 2) {
            return true;
        }

        var iterator = fitnessToCount.entrySet().iterator();
        String code1 = iterator.next().getKey().getKey();
        String code2 = iterator.next().getKey().getKey();

        if (code1.equals(optimal.getBinaryCode()) || code2.equals(optimal.getBinaryCode())) {
            return true;
        }
        int hammingDist = hammingDist(code1, code2);
        return hammingDist != 1;
    }

    private int hammingDist(String str1, String str2) {
        int i = 0, count = 0;
        while (i < str1.length()) {
            if (str1.charAt(i) != str2.charAt(i))
                count++;
            i++;
        }
        return count;
    }

    private void populateHistogramData(FitnessFunction<?, ? extends Number> function, List<Individual> currentIndividuals, int i, Map<Integer, List<IndividualMetrics>> generationToIndMetrics, Map<Homogeneity, List<IndividualMetrics>> homogeneityToIndMetrics) {
        if (i < 3 || (i + 1) % 500000 == 0) {
            List<IndividualMetrics> individualMetrics = buildAllIndividualMetrics(currentIndividuals, function);
            generationToIndMetrics.put(i + 1, individualMetrics);
        }

        Arrays.stream(Homogeneity.values())
                .forEach(h -> {
                    if (!homogeneityToIndMetrics.containsKey(h) && isHomogenous(currentIndividuals, h.getPercentage())) {
                        homogeneityToIndMetrics.put(h, buildAllIndividualMetrics(currentIndividuals, function));
                    }
                });
    }

    private List<IndividualMetrics> buildAllIndividualMetrics(List<Individual> individuals, FitnessFunction<?, ?> function) {
        return individuals.stream()
                .map(individual -> buildIndividualMetrics(individual, function))
                .toList();
    }

    private IndividualMetrics buildIndividualMetrics(Individual individual, FitnessFunction<?, ?> function) {
        return function.isConstant()
                ? new IndividualMetrics(individual, getOnesCount(individual), null, null)
                : new IndividualMetrics(individual, getOnesCount(individual),
                function.supportsDecoding() ? decode(individual, function).doubleValue() : null,
                function.evaluate(individual).doubleValue());
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

    private int getMaxIterations(FitnessFunction<?, ? extends Number> function, Selector selector) {
        return function.isConstant() && selector.getSelectorType() == SUS
                ? CONST_SUS_MAX_ITERATIONS
                : MAX_ITERATIONS;
    }

    private boolean hasOptimal(Collection<Individual> individuals, Individual optimal) {
        return individuals.stream()
                .map(Individual::getBinaryCode)
                .anyMatch(code -> optimal.getBinaryCode().equals(code));
    }

    private boolean hasNotConverged(Map<Individual, ? extends Number> individualToFitness, Operator operator) {
        return !convergenceIdentifier.hasConverged(individualToFitness, operator.getOperatorType());
    }

    private <ARG_T extends Number, RES_T extends Number> Map<Individual, RES_T> getIndividualToFitness(List<Individual> individuals,
                                                                                                       FitnessFunction<ARG_T, RES_T> function) {
        return individuals
                .stream()
                .collect(toUnmodifiableMap(identity(), function::evaluate));
    }
}

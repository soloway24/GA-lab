package lab.run;

import lab.Individual;
import lab.encoding.Encoding;
import lab.export.Homogeneity;
import lab.export.Optimality;
import lab.function.FitnessFunction;
import lab.identifier.ConvergenceIdentifier;
import lab.metric.IndividualMetrics;
import lab.metric.SingleHomogeneityMetrics;
import lab.operator.Operator;
import lab.operator.OperatorType;
import lab.population.*;
import lab.selection.SelectionContext;
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
import static lab.export.Homogeneity.NINETY_FIVE;
import static lab.export.Homogeneity.NINETY_NINE;
import static lab.export.Optimality.*;
import static lab.identifier.ConvergenceIdentifier.*;
import static lab.identifier.SuccessfulRunIdentifier.getBestIndividual;
import static lab.selection.AdditionalSelectorProperty.NI;
import static lab.selection.SelectorType.SUS;
import static lab.util.CalculationUtils.getAverageFitness;
import static lab.util.MetricUtils.*;

@Component
@RequiredArgsConstructor
public class RunPoolExecutor {

    private static final int MAX_ITERATIONS = 10000000;
    private static final int CONST_SUS_MAX_ITERATIONS = 10000;

    private final ConvergenceIdentifier convergenceIdentifier;
    private final RunPoolStatsCreator runPoolStatsCreator;
    private final PopulationTimingTypeIdentifier timingTypeIdentifier;

    public List<RunPoolStats> executeAllRunPools(List<RunPool> runPools) {
        return IntStream.range(0, runPools.size())
                .mapToObj(index -> executeRunPool(runPools.get(index), index, runPools.size()))
                .toList();
    }

    public List<RunPoolStats> executeAllRunPoolsParallel(List<RunPool> runPools) {
        return IntStream.range(0, runPools.size())
                .mapToObj(index -> executeRunPoolParallel(runPools.get(index), index, runPools.size()))
                .toList();
    }

    public RunPoolStats executeRunPool(RunPool runPool, int runPoolIndex, int runPoolQuantity) {
        System.out.println("Executing RunPool: " + runPool.runConfiguration());
        List<RunStats> allRunStats = executeAndGetAllRunStats(runPool, runPoolIndex, runPoolQuantity);
        return runPoolStatsCreator.create(allRunStats, runPool.runConfiguration());
    }

    public RunPoolStats executeRunPoolParallel(RunPool runPool, int runPoolIndex, int runPoolQuantity) {
        System.out.println("Executing RunPool: " + runPool.runConfiguration());

        ExecutorService executorService = new ForkJoinPool();
        List<Future<RunStats>> futures = executeParallelAndGetAllRunStats(runPool, executorService, runPoolIndex, runPoolQuantity);
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

            int niAlH = -1;
            double fAlH = -1;

            Map<Integer, Double> generationToAvgF = new HashMap<>();
            Map<Integer, Double> generationToMaxF = new HashMap<>();
            Map<Integer, Double> generationToSigmaF = new HashMap<>();
            Map<Integer, Integer> generationToUniqueX = new HashMap<>();
            Map<Integer, Double> generationToOptimalRatio = new HashMap<>();
            Map<Integer, Integer> generationToOptimalQ = new HashMap<>();
            Map<Integer, Double> generationToBestRatio = new HashMap<>();

            Map<Integer, List<IndividualMetrics>> generationToIndMetrics = new HashMap<>();
            Map<Homogeneity, List<IndividualMetrics>> homogeneityToIndMetrics = new HashMap<>();
            Map<Homogeneity, Integer> homogeneityToGeneration = new HashMap<>();
            Map<PopulationTimingType, PopulationSnapshot> timingTypeToPopulationSnapshot = new HashMap<>();

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

                SelectionContext selectionContext = buildSelectionContext(selector, individualToFitness, i);

                parentPool = selector.select(selectionContext);
                List<Individual> parentCopies = new ArrayList<>(parentPool);
                shuffle(parentCopies);
                offsprings = operator.apply(parentPool);

                double avgF = getAverageFitness(individualToFitness);
                generationToAvgF.put(i, avgF);

                Individual best = getBestIndividual(individualToFitness);
                double maxF = individualToFitness.get(best).doubleValue();
                generationToMaxF.put(i, maxF);

                double sigmaF = getStandardDeviation(individualToFitness.values(), avgF);
                generationToSigmaF.put(i, sigmaF);

                int unique = getUniqueBinaryCodes(currentIndividuals).size();
                generationToUniqueX.put(i, unique);

                int optimalQ = getEqualQuantity(currentIndividuals, optimal);
                generationToOptimalQ.put(i, optimalQ);

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

                    double parentAvgF = getAverageFitness(parentPoolToFitness);
                    currentI = sigmaF == 0
                            ? 1
                            : (parentAvgF - avgF) / sigmaF;
                    iterationToI.put(i + 1, currentI);

                    Map<Individual, Double> scaledIndividuals = selector.scale(selectionContext);
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

                if (isAtLeastOneAlleleHomogenous(currentIndividuals) && niAlH == -1) {
                    niAlH = i;
                    fAlH = avgF;
                }

                populateHomogeneityToGeneration(currentIndividuals, i, homogeneityToGeneration);
                populateHistogramData(function, currentIndividuals, i, generationToIndMetrics, homogeneityToIndMetrics);
                populateSnapshotData(population.populationConfiguration(), individualToFitness, timingTypeToPopulationSnapshot, i, avgF, maxF);

                previousBestF = maxF;
                previousBestQ = bestQ;
                currentIndividuals = offsprings;
                individualToFitness = getIndividualToFitness(currentIndividuals, function);
                i++;
            }

            individualToFitness = getIndividualToFitness(currentIndividuals, function);

            populateHomogeneityToGeneration(currentIndividuals, i, homogeneityToGeneration);
            populateHistogramData(function, currentIndividuals, i, generationToIndMetrics, homogeneityToIndMetrics);

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

            Individual best = getBestIndividual(individualToFitness);
            double fFound = individualToFitness.get(best).doubleValue();
            generationToMaxF.put(ni, fFound);

            double fAvg = getAverageFitness(individualToFitness);
            generationToAvgF.put(ni, fAvg);

            double optimalF = function.getMaxFitness().doubleValue();
            int niFHM = getNiFHM(generationToAvgF, optimalF);

            double startFAvg = generationToAvgF.get(0);
            int niFHSM = getNiFHSM(generationToAvgF, optimalF, startFAvg);

            int optimalQ = getEqualQuantity(currentIndividuals, optimal);
            generationToOptimalQ.put(ni, optimalQ);

            double optimalRatio = (double) optimalQ / currentIndividuals.size();
            generationToOptimalRatio.put(ni, optimalRatio);

            int ni25of = getNiOptimalRatioOf(generationToOptimalRatio, TWENTY_FIVE);
            int ni50of = getNiOptimalRatioOf(generationToOptimalRatio, FIFTY);
            int ni75of = getNiOptimalRatioOf(generationToOptimalRatio, SEVENTY_FIVE);
            int ni90of = getNiOptimalRatioOf(generationToOptimalRatio, NINETY);

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

            int ni75h = ofNullable(homogeneityToGeneration.get(Homogeneity.SEVENTY_FIVE)).orElse(-1);
            double avg75h = ofNullable(generationToAvgF.get(ni75h)).orElse(-1.0);
            int numOpt75h = ofNullable(generationToOptimalQ.get(ni75h)).orElse(-1);

            int ni90h = ofNullable(homogeneityToGeneration.get(Homogeneity.NINETY)).orElse(-1);
            double avg90h = ofNullable(generationToAvgF.get(ni90h)).orElse(-1.0);
            int numOpt90h = ofNullable(generationToOptimalQ.get(ni90h)).orElse(-1);

            int ni95h = ofNullable(homogeneityToGeneration.get(NINETY_FIVE)).orElse(-1);
            double avg95h = ofNullable(generationToAvgF.get(ni95h)).orElse(-1.0);
            int numOpt95h = ofNullable(generationToOptimalQ.get(ni95h)).orElse(-1);

            if (isAtLeastOneAlleleHomogenous(currentIndividuals) && niAlH == -1) {
                niAlH = ni;
                fAlH = fAvg;
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

                    .withNiFHM(niFHM)
                    .withNiFHSM(niFHSM)

                    .withNi25of(ni25of)
                    .withNi50of(ni50of)
                    .withNi75of(ni75of)
                    .withNi90of(ni90of)

                    .withNiAlH(niAlH)
                    .withFAlH(fAlH)

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
                    .withTimingTypeToPopulationSnapshot(timingTypeToPopulationSnapshot)

                    .withSingleHomogeneityMetrics(SingleHomogeneityMetrics.builder()
                            .withNi75h(ni75h)
                            .withAvg75h(avg75h)
                            .withNumOpt75h(numOpt75h)

                            .withNi90h(ni90h)
                            .withAvg90h(avg90h)
                            .withNumOpt90h(numOpt90h)

                            .withAvg95h(avg95h)
                            .withNi95h(ni95h)
                            .withNumOpt95h(numOpt95h)

                            .build())

                    .build();
        } catch (Exception e) {
            System.out.println(Thread.currentThread() + " exception: " + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    private SelectionContext buildSelectionContext(Selector selector,
                                                   Map<Individual, ? extends Number> individualToFitness,
                                                   int ni) {
        SelectionContext.SelectionContextBuilder builder = SelectionContext.builder()
                .withIndividualToFitness(individualToFitness);

        if (selector.getAdditionalSelectorProperties().contains(NI)) {
            builder.withNi(ni);
        }
        return builder.build();
    }

    private void populateHomogeneityToGeneration(List<Individual> currentIndividuals,
                                                 int i,
                                                 Map<Homogeneity, Integer> homogeneityToGeneration) {
        List<Homogeneity> exportedHomogeneities = List.of(Homogeneity.SEVENTY_FIVE, Homogeneity.NINETY, NINETY_FIVE);
        exportedHomogeneities
                .forEach(h -> {
                    if (!homogeneityToGeneration.containsKey(h) && isHomogenous(currentIndividuals, h.getPercentage())) {
                        homogeneityToGeneration.put(h, i);
                    }
                });
    }

    private int getNiFHM(Map<Integer, ? extends Number> generationToAvgF, double optimalF) {
        for (int i = 0; i < generationToAvgF.size(); i++) {
            if (generationToAvgF.get(i).doubleValue() >= optimalF * 0.5) {
                return i;
            }
        }
        return -1;
    }

    private int getNiFHSM(Map<Integer, ? extends Number> generationToAvgF, double optimalF, double startFAvg) {
        for (int i = 0; i < generationToAvgF.size(); i++) {
            if (generationToAvgF.get(i).doubleValue() >= startFAvg + 0.5 * (optimalF - startFAvg)) {
                return i;
            }
        }
        return -1;
    }

    private int getNiOptimalRatioOf(Map<Integer, Double> generationToOptimalRatio, Optimality optimality) {
        for (int i = 0; i < generationToOptimalRatio.size(); i++) {
            if (generationToOptimalRatio.get(i) >= optimality.getPercentage()) {
                return i;
            }
        }
        return -1;
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

    private void populateHistogramData(FitnessFunction<?, ? extends Number> function,
                                       List<Individual> currentIndividuals,
                                       int i,
                                       Map<Integer, List<IndividualMetrics>> generationToIndMetrics,
                                       Map<Homogeneity, List<IndividualMetrics>> homogeneityToIndMetrics) {
        if (i < 3 || (i) % 10000 == 0) {
            List<IndividualMetrics> individualMetrics = buildAllIndividualMetrics(currentIndividuals, function);
            generationToIndMetrics.put(i, individualMetrics);
        }

        List<Homogeneity> exportedHomogeneities = List.of(Homogeneity.SEVENTY_FIVE, Homogeneity.NINETY, NINETY_FIVE, NINETY_NINE);
        exportedHomogeneities
                .forEach(h -> {
                    if (!homogeneityToIndMetrics.containsKey(h) && isHomogenous(currentIndividuals, h.getPercentage())) {
                        homogeneityToIndMetrics.put(h, buildAllIndividualMetrics(currentIndividuals, function));
                    }
                });
    }

    private void populateSnapshotData(PopulationConfiguration populationConfiguration,
                                      Map<Individual, ? extends Number> individualToFitness,
                                      Map<PopulationTimingType, PopulationSnapshot> timingTypeToPopulationSnapshot,
                                      int iteration,
                                      double avgF,
                                      double maxF) {
        Arrays.stream(PopulationTimingType.values())
                .forEach(timingType -> {
                    if (shouldAddTiming(timingType, timingTypeToPopulationSnapshot, iteration, avgF, maxF)) {
                        addTiming(populationConfiguration, timingType, individualToFitness, timingTypeToPopulationSnapshot, iteration, avgF, maxF);
                    }
                });
    }

    private boolean shouldAddTiming(PopulationTimingType timingType,
                                    Map<PopulationTimingType, PopulationSnapshot> timingTypeToPopulationSnapshot,
                                    int iteration,
                                    double avgF,
                                    double maxF) {
        return !timingTypeToPopulationSnapshot.containsKey(timingType)
                && timingTypeIdentifier.isTiming(timingType, iteration, avgF, maxF);
    }

    private void addTiming(PopulationConfiguration populationConfiguration,
                           PopulationTimingType timingType,
                           Map<Individual, ? extends Number> individualToFitness,
                           Map<PopulationTimingType, PopulationSnapshot> timingTypeToPopulationSnapshot,
                           int iteration,
                           double avgF,
                           double maxF) {
        PopulationTiming populationTiming = new PopulationTiming(iteration, avgF / maxF);
        Map<Individual, Double> sortedIndividualToFitness = individualToFitness.entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue().doubleValue()))
                .sorted(Map.Entry.comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
        timingTypeToPopulationSnapshot.put(timingType, new PopulationSnapshot(populationTiming, populationConfiguration, sortedIndividualToFitness));
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

    private List<RunStats> executeAndGetAllRunStats(RunPool runPool, int runPoolIndex, int runPoolQuantity) {
        return IntStream.range(0, runPool.getSize())
                .mapToObj(i -> {
                    System.out.println("Run " + i);
                    return executeRun(runPool.runs().get(i), i, runPool.getSize(), runPoolIndex, runPoolQuantity);
                })
                .toList();
    }

    private List<Future<RunStats>> executeParallelAndGetAllRunStats(RunPool runPool, ExecutorService executorService, int runPoolIndex, int runPoolQuantity) {
        return IntStream.range(0, runPool.getSize())
                .mapToObj(i -> executorService.submit(() -> {
                    System.out.println("Run " + i);
                    return executeRun(runPool.runs().get(i), i, runPool.getSize(), runPoolIndex, runPoolQuantity);
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

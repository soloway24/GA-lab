package lab.v2.util;

import lab.v2.Individual;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

import static java.lang.Double.isNaN;
import static java.util.Map.entry;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static lab.v2.util.CalculationUtils.getAverage;
import static lab.v2.util.CalculationUtils.getMedian;

public class MetricUtils {

    private static final StandardDeviation STANDARD_DEVIATION = new StandardDeviation();

    public static Entry<Integer, ? extends Number> getMaxIteratedValue(Map<Integer, ? extends Number> iterationToValue) {
        return iterationToValue.entrySet().stream()
                .map(entry -> entry(entry.getKey(), entry.getValue().doubleValue()))
                .max(Entry.comparingByValue())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find max value of the iterated value map: " + iterationToValue));
    }

    public static Entry<Integer, ? extends Number> getMinIteratedValue(Map<Integer, ? extends Number> iterationToValue) {
        return iterationToValue.entrySet().stream()
                .map(entry -> entry(entry.getKey(), entry.getValue().doubleValue()))
                .min(Entry.comparingByValue())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find max value of the iterated value map: " + iterationToValue));
    }

    public static double getDifference(Map<Individual, ? extends Number> individualToFitness,
                                       Map<Individual, ? extends Number> parentPoolToFitness) {
        double currentFAvg = getAverage(individualToFitness.values());
        double parentPoolFAvg = getAverage(parentPoolToFitness.values());
        return parentPoolFAvg - currentFAvg;
    }

    public static double getStandardDeviation(Collection<? extends Number> values, double avgValue) {
        double[] toEvaluate = values.stream()
                .mapToDouble(Number::doubleValue)
                .toArray();

        return STANDARD_DEVIATION.evaluate(toEvaluate, avgValue);
    }

    public static double getReproductionRate(Collection<Individual> before, Collection<Individual> after) {
        Set<Integer> uniqueIndexesBefore = getUniqueIndexes(before);
        Set<Integer> uniqueIndexesAfter = getUniqueIndexes(after);

        if (uniqueIndexesAfter.size() == 0) {
            throw new IllegalStateException("Cannot get RR for the empty parent pool. Population before: " + before + " .");
        }
        return (double) uniqueIndexesAfter.size() / uniqueIndexesBefore.size();
    }

    public static double getLostOfDiversity(double reproductionRate) {
        return 1 - reproductionRate;
    }

    public static Set<String> getUniqueBinaryCodes(Collection<Individual> individuals) {
        return individuals.stream()
                .map(Individual::getBinaryCode)
                .collect(toUnmodifiableSet());
    }

    private static Set<Integer> getUniqueIndexes(Collection<Individual> individuals) {
        return individuals.stream()
                .map(Individual::getIndex)
                .collect(toUnmodifiableSet());
    }

    private static Map<Integer, Long> getIndexToCount(Collection<Individual> individuals) {
        return individuals.stream()
                .collect(groupingBy(Individual::getIndex, counting()));
    }


    public static double computePFET(Map<Individual, ? extends Number> individualToFitness, List<Individual> parentPool) {
        if (individualToFitness.size() != parentPool.size()) {
            throw new IllegalArgumentException("fitnesses and offspringCounts must have the same size.");
        }

        Map<Integer, Double> indexToFitness = individualToFitness.entrySet()
                .stream()
                .map(entry -> entry(entry.getKey().getIndex(), entry.getValue().doubleValue()))
                .collect(toUnmodifiableMap(Entry::getKey, Entry::getValue));

        int n = individualToFitness.size();
        List<Double> fitnesses = IntStream.rangeClosed(1, n)
                .mapToObj(i -> ofNullable(indexToFitness.get(i)).orElseThrow())
                .toList();

        Map<Integer, Long> parentPoolIndexToCount = getIndexToCount(parentPool);
        List<Long> offspringCounts = IntStream.rangeClosed(1, n)
                .mapToObj(i -> ofNullable(parentPoolIndexToCount.get(i)).orElse(0L))
                .toList();

        return computePFET(fitnesses, offspringCounts);
    }

    public static double computePFET(List<Double> fitnesses, List<Long> offspringCounts) {
        if (fitnesses.size() != offspringCounts.size()) {
            throw new IllegalArgumentException("fitnesses and offspringCounts lists must have the same size.");
        }

        int n = fitnesses.size();
        double medianFitness = getMedian(fitnesses);
        double medianOffspringCount = getMedian(offspringCounts);
        if (medianOffspringCount == 0) {
            medianOffspringCount = 1;
        }

        int a = 0, b = 0, c = 0, d = 0;
        for (int i = 0; i < n; i++) {
            if (fitnesses.get(i) <= medianFitness && offspringCounts.get(i) <= medianOffspringCount) a++;
            if (fitnesses.get(i) > medianFitness && offspringCounts.get(i) <= medianOffspringCount) b++;
            if (fitnesses.get(i) <= medianFitness && offspringCounts.get(i) > medianOffspringCount) c++;
            if (fitnesses.get(i) > medianFitness && offspringCounts.get(i) > medianOffspringCount) d++;
        }

        HypergeometricDistribution distribution = new HypergeometricDistribution(a + b + c + d, a + c, a + b);
        double pRandom = distribution.upperCumulativeProbability(a);

        return -Math.log10(pRandom);
    }

    public static double computeKendallTauB(Map<Individual, ? extends Number> individualToFitness, List<Individual> parentPool) {
        if (individualToFitness.size() != parentPool.size()) {
            throw new IllegalArgumentException("fitnesses and offspringCounts must have the same size.");
        }

        Map<Integer, Double> indexToFitness = individualToFitness.entrySet()
                .stream()
                .map(entry -> entry(entry.getKey().getIndex(), entry.getValue().doubleValue()))
                .collect(toUnmodifiableMap(Entry::getKey, Entry::getValue));

        int n = individualToFitness.size();
        double[] fitnesses = IntStream.rangeClosed(1, n)
                .mapToDouble(i -> ofNullable(indexToFitness.get(i)).orElseThrow())
                .toArray();

        Map<Integer, Long> parentPoolIndexToCount = getIndexToCount(parentPool);
        double[] offspringCounts = IntStream.rangeClosed(1, n)
                .mapToDouble(i -> ofNullable(parentPoolIndexToCount.get(i)).orElse(0L))
                .toArray();

        return computeKendallTauB(fitnesses, offspringCounts);
    }

    public static double computeKendallTauB(double[] x, double[] y) {
        KendallsCorrelation kendallsCorrelation = new KendallsCorrelation();
        double kendall = kendallsCorrelation.correlation(x, y);
        return isNaN(kendall)
                ? 0
                : kendall;
    }

    public static long getOnesCount(Individual individual) {
        return individual.getBinaryCode().chars()
                .filter(ch -> ch == '1')
                .count();
    }

    public static void main(String[] args) {
        double[] x = {12, 2, 1, 12, 2};
        double[] y = {1, 4, 7, 1, 0};

        double tauB = computeKendallTauB(x, y);
        System.out.println("Kendall's τ-b: " + tauB);
    }

}

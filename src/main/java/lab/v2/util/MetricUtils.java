package lab.v2.util;

import lab.v2.Individual;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static lab.v2.util.CalculationUtils.getAverage;

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
}

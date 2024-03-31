package lab.v2.util;

import lab.v2.Individual;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Map.entry;
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

    public static double getStandardDeviation(List<? extends Number> values, double avgValue) {
        double[] toEvaluate = values.stream()
                .mapToDouble(Number::doubleValue)
                .toArray();

        return STANDARD_DEVIATION.evaluate(toEvaluate, avgValue);
    }
}

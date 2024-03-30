package lab.v2.util;

import lab.v2.Individual;

import java.util.Map;
import java.util.Map.Entry;

import static lab.v2.util.CalculationUtils.getAverage;

public class MetricUtils {

    public static Entry<Integer, Double> getMaxIteratedValue(Map<Integer, Double> iterationToValue) {
        return iterationToValue.entrySet().stream()
                .max(Entry.comparingByValue())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find max value of the iterated value map: " + iterationToValue));
    }

    public static Entry<Integer, Double> getMinIteratedValue(Map<Integer, Double> iterationToValue) {
        return iterationToValue.entrySet().stream()
                .min(Entry.comparingByValue())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find max value of the iterated value map: " + iterationToValue));
    }

    public static double getDifference(Map<Individual, ? extends Number> individualToFitness,
                                       Map<Individual, ? extends Number> parentPoolToFitness) {
        double currentFAvg = getAverage(individualToFitness.values());
        double parentPoolFAvg = getAverage(parentPoolToFitness.values());
        return parentPoolFAvg - currentFAvg;
    }
}

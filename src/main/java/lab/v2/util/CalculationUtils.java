package lab.v2.util;

import lab.v2.Individual;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CalculationUtils {

    public static <T extends Number> double getAverageFitness(Map<Individual, T> individualToFitness) {
        return getAverage(individualToFitness.values());
    }

    public static <T extends Number> double getAverage(Collection<T> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElseThrow(() -> new IllegalStateException("Cannot calculate average of the values: " + values + " !"));
    }

    public static <T extends Number> double getMedian(Collection<T> values) {
        List<T> sortedValues = values.stream()
                .sorted()
                .toList();
        int size = sortedValues.size();
        if (size % 2 == 1) {
            return sortedValues.get(size / 2).doubleValue();
        }
        return getAverage(sortedValues.get(size / 2 - 1), sortedValues.get(size / 2));
    }

    private static <T extends Number> double getAverage(T first, T second) {
        return (first.doubleValue() + second.doubleValue()) / 2.0;
    }

    public static Integer getMinInt(Collection<Integer> values) {
        return values.stream()
                .min(Integer::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get min value of an empty collection."));
    }

    public static Integer getMaxInt(Collection<Integer> values) {
        return values.stream()
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get min value of an empty collection."));
    }

    public static Double getMinDouble(Collection<Double> values) {
        return values.stream()
                .min(Double::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get min value of an empty collection."));
    }

    public static Double getMaxDouble(Collection<Double> values) {
        return values.stream()
                .max(Double::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get min value of an empty collection."));
    }

    public static List<Double> getDoubleValues(Map<Individual, ? extends Number> individualToValue) {
        return individualToValue.values().stream()
                .map(Number::doubleValue)
                .toList();
    }

    public static double getValueSum(List<Double> values) {
        return values.stream()
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException("Provided values list is empty! Values = "
                        + values + " ."));
    }

    public static List<Individual> getIndexedIndividuals(List<Individual> individuals) {
        return IntStream.range(0, individuals.size())
                .mapToObj(i -> getIndividualWithIndex(individuals.get(i), i + 1))
                .toList();
    }

    private static Individual getIndividualWithIndex(Individual individual, int index) {
        return new Individual(index, individual.getBinaryCode(), individual.getEncoding());
    }
}

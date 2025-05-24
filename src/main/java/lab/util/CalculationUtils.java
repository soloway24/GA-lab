package lab.util;

import lab.Individual;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class CalculationUtils {

    private static final Object BINOMIAL_DISTRIBUTION_LOCK = new Object();

    private static final Map<Pair<Integer, Integer>, Double> MUTATION_PROBABILITIES = Map.of(
            Pair.of(10, 100), 0.0001,
            Pair.of(100, 100), 0.00001
    );
    private static final Map<Pair<Integer, Integer>, BinomialDistribution> MUTATION_DISTRIBUTIONS = buildDistributions();

    private static Map<Pair<Integer, Integer>, BinomialDistribution> buildDistributions() {
        return MUTATION_PROBABILITIES.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new BinomialDistribution(entry.getKey().getKey() * entry.getKey().getValue(), entry.getValue()))
                );
    }

    public static int getBinomialMutationCount(int chromosomeLength, int populationSize) {
        synchronized (BINOMIAL_DISTRIBUTION_LOCK) {
            BinomialDistribution binomialDistribution = ofNullable(MUTATION_DISTRIBUTIONS.get(Pair.of(chromosomeLength, populationSize)))
                    .orElseThrow(() -> new IllegalArgumentException("No mutation distribution config present for chromosome length: "
                            + chromosomeLength + " and population size: " + populationSize));

            return binomialDistribution.sample();
        }
    }

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
                .filter(value -> value >= 0)
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
        return getDoubleValues(individualToValue.values());
    }

    public static List<Double> getDoubleValues(Collection<? extends Number> values) {
        return values.stream()
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

    public static List<Individual> getIndexedIndividuals(Stream<Individual> individuals) {
        AtomicInteger i = new AtomicInteger(1);
        return individuals
                .map(individual -> getIndividualWithIndex(individual, i.getAndIncrement()))
                .toList();
    }

    public static <T extends Number> double getMaxFitness(Map<Individual, T> individualToFitness) {
        return individualToFitness.values()
                .stream()
                .map(Number::doubleValue)
                .max(Double::compareTo)
                .orElseThrow(() -> new IllegalStateException("No max fitness found during linear scaling."));
    }

    private static Individual getIndividualWithIndex(Individual individual, int index) {
        return new Individual(index, individual.getBinaryCode(), individual.getEncoding());
    }
}

package lab.v2.convertor;

import lab.model.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableMap;

public class ValuesAdjuster {

    public static Map<Individual, Double> getAdjustedIndividualToValue(Map<Individual, Double> individualToProbability,
                                                                       double actualValue,
                                                                       double expectedValue) {
        double adjustment = actualValue - expectedValue;
        List<Individual> individuals = new ArrayList<>(individualToProbability.keySet());
        Individual individualToAdjust = individuals.get(individuals.size() - 1);
        return individualToProbability.entrySet().stream()
                .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> getAdjustedValue(entry, individualToAdjust, adjustment)));
    }

    private static Double getAdjustedValue(Map.Entry<Individual, Double> entry, Individual individualToAdjust, double adjustment) {
        return entry.getKey().equals(individualToAdjust)
                ? entry.getValue() - adjustment
                : entry.getValue();
    }
}

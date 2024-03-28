package lab.v2.convertor;

import lab.v2.Individual;

import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableMap;

public class ValuesAdjuster {

    public static Map<Individual, Double> getAdjustedIndividualToValue(Map<Individual, Double> individualToProbability,
                                                                       double actualValue,
                                                                       double expectedValue) {
        double adjustment = (actualValue - expectedValue) / individualToProbability.size();
        return individualToProbability.entrySet().stream()
                .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> entry.getValue() - adjustment));
    }
}

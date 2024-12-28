package lab.convertor;

import lab.Individual;

import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableMap;

public class ValuesAdjuster {

    public static Map<Individual, Double> getAdjustedIndividualToValue(Map<Individual, Double> individualToValue,
                                                                       double actualValue,
                                                                       double expectedValue) {
        double adjustment = (actualValue - expectedValue) / individualToValue.size();
        return individualToValue.entrySet().stream()
                .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> entry.getValue() - adjustment));
    }
}

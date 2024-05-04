package lab.v2.selection;

import lab.v2.Individual;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Selector {

    SelectorType getSelectorType();

    String getName();

    String getFullName();

    Optional<String> getParam1();

    Optional<String> getParam2();

    List<Individual> select(Map<Individual, ? extends Number> individualToFitness);

    default Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return individualToFitness.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Entry::getKey, entry -> entry.getValue().doubleValue()));
    }

}

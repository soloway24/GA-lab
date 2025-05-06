package lab.selection;

import lab.Individual;

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

    List<Individual> select(SelectionContext selectionContext);

    default Map<Individual, Double> scale(SelectionContext selectionContext) {
        return selectionContext.getIndividualToFitness().entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Entry::getKey, entry -> entry.getValue().doubleValue()));
    }

    default List<AdditionalSelectorProperty> getAdditionalSelectorProperties() {
        return List.of();
    }

}

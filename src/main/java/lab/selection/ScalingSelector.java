package lab.selection;

import lab.Individual;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
@RequiredArgsConstructor
public class ScalingSelector {

    private static final double DEFAULT_SCALED_FITNESS = 0.0001;

    public <T extends Number> List<Individual> select(SelectionContext selectionContext,
                                                      Function<SelectionContext, List<Individual>> selectionFunction,
                                                      BiFunction<T, SelectionContext, Double> scalingFunction) {
        Map<Individual, Double> individualToScaledFitness = getIndividualToScaledFitness(selectionContext, scalingFunction);
        return selectionFunction.apply(new SelectionContext(individualToScaledFitness));
    }

    private LinkedHashMap<Individual, Double> getOrderedMap(List<Entry<Individual, Double>> individuals) {
        return individuals.stream()
                .collect(toMap(Entry::getKey, Entry::getValue,
                        (v1, v2) -> {
                            throw new IllegalStateException("Two same individual objects occurred in one population.");
                        },
                        LinkedHashMap::new));
    }

    @SuppressWarnings("unchecked")
    public <T extends Number> Map<Individual, Double> getIndividualToScaledFitness(SelectionContext selectionContext,
                                                                                   BiFunction<T, SelectionContext, Double> scalingFunction) {
        Map<Individual, Double> individualToScaledFitness = selectionContext.getIndividualToFitness().entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry ->
                        getScaledFitness(selectionContext, scalingFunction, (T) entry.getValue())));

        if (doesNotContainPositiveFitness(individualToScaledFitness)) {
            return getDefaultIndividualToScaledFitness(individualToScaledFitness);
        }

        return individualToScaledFitness;
    }

    private <T extends Number> Double getScaledFitness(SelectionContext selectionContext,
                                                       BiFunction<T, SelectionContext, Double> scalingFunction,
                                                       T value) {
        double scaledFitness = max(scalingFunction.apply(value, selectionContext), 0);
        if (scaledFitness < 0) {
            throw new IllegalArgumentException("Scaled fitness cannot be negative. Fitness = " + scaledFitness);
        }
        return scaledFitness;
    }

    private boolean doesNotContainPositiveFitness(Map<Individual, Double> individualToScaledFitness) {
        return individualToScaledFitness.entrySet().stream()
                .noneMatch(entry -> entry.getValue() > 0);
    }

    private Map<Individual, Double> getDefaultIndividualToScaledFitness(Map<Individual, Double> individualToScaledFitness) {
        return individualToScaledFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry -> DEFAULT_SCALED_FITNESS));
    }
}

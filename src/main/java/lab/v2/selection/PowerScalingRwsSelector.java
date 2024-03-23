package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.pow;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toUnmodifiableMap;

@RequiredArgsConstructor
public class PowerScalingRwsSelector<T extends Number> implements Selector<T> {

    private static final double DEFAULT_FITNESS = 0.0001;
    private final RwsSelector<Double> rwsSelector;
    private final double power;

    @Override
    public String getName() {
        return "PS RWS";
    }

    @Override
    public List<Individual> select(Map<Individual, T> individualToFitness) {
        Map<Individual, Double> individualToScaledFitness = getIndividualToScaledFitness(individualToFitness);
        List<Entry<Individual, Double>> individuals = new ArrayList<>(individualToScaledFitness.entrySet());

        shuffle(individuals);
        Map<Individual, Double> shuffledIndividualToScaledFitness = individuals.stream()
                .collect(toUnmodifiableMap(Entry::getKey, Entry::getValue));

        return rwsSelector.select(shuffledIndividualToScaledFitness);
    }

    private double scaleFitness(T fitness) {
        return pow(fitness.doubleValue(), power);
    }

    private Map<Individual, Double> getIndividualToScaledFitness(Map<Individual, T> individualToFitness) {
        Map<Individual, Double> individualToScaledFitness = individualToFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry -> scaleFitness(entry.getValue())));

        if (doesNotContainPositiveFitness(individualToScaledFitness)) {
            return getDefaultIndividualToScaledFitness(individualToScaledFitness);
        }

        return individualToScaledFitness;
    }

    private boolean doesNotContainPositiveFitness(Map<Individual, Double> individualToScaledFitness) {
        return individualToScaledFitness.entrySet().stream()
                .noneMatch(entry -> entry.getValue() > 0);
    }

    private static Map<Individual, Double> getDefaultIndividualToScaledFitness(Map<Individual, Double> individualToScaledFitness) {
        return individualToScaledFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry -> DEFAULT_FITNESS));
    }
}

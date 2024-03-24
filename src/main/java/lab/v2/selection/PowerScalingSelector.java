package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.pow;

@RequiredArgsConstructor
public class PowerScalingSelector<T extends Number> {

    private final ScalingSelector<T> scalingSelector;
    private final double power;

    public List<Individual> select(Map<Individual, T> individualToFitness,
                                   Function<Map<Individual, Double>, List<Individual>> selectionFunction) {
        return scalingSelector.select(individualToFitness, selectionFunction, getScalingFunction());
    }

    private BiFunction<T, Map<Individual, T>, Double> getScalingFunction() {
        return (T fitness, Map<Individual, T> individualToFitness) -> pow(fitness.doubleValue(), power);
    }
}

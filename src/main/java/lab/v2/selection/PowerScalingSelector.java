package lab.v2.selection;

import lab.v2.Individual;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.pow;

@RequiredArgsConstructor
public class PowerScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final double power;

    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness,
                                   Function<Map<Individual, Double>, List<Individual>> selectionFunction) {
        return scalingSelector.select(individualToFitness, selectionFunction, getScalingFunction());
    }

    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return scalingSelector.getIndividualToScaledFitness(individualToFitness, getScalingFunction());
    }

    private <T extends Number> BiFunction<T, Map<Individual, T>, Double> getScalingFunction() {
        return (fitness, individualToFitness) -> pow(fitness.doubleValue(), power);
    }

}

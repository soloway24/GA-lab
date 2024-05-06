package lab.selection;

import lab.util.CalculationUtils;
import lab.Individual;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.pow;

@RequiredArgsConstructor
public class DynamicPowerScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final double startPower;
    @Getter
    private final double endPower;

    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness,
                                   Function<Map<Individual, Double>, List<Individual>> selectionFunction) {
        return scalingSelector.select(individualToFitness, selectionFunction, getScalingFunction());
    }

    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return scalingSelector.getIndividualToScaledFitness(individualToFitness, getScalingFunction());
    }

    private <T extends Number> BiFunction<T, Map<Individual, T>, Double> getScalingFunction() {
        return (T fitness, Map<Individual, T> individualToFitness) -> scaleFitness(fitness, individualToFitness.values());
    }

    private <T extends Number> double scaleFitness(T fitness, Collection<T> fitnesses) {
        double averageFitness = CalculationUtils.getAverage(fitnesses);
        double medianFitness = CalculationUtils.getMedian(fitnesses);

        if (medianFitness >= averageFitness) {
            return pow(fitness.doubleValue(), endPower);
        }
        return pow(fitness.doubleValue(), startPower);
    }
}

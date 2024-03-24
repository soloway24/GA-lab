package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.pow;
import static lab.v2.CalculationUtils.getAverage;
import static lab.v2.CalculationUtils.getMedian;

@RequiredArgsConstructor
public class DynamicPowerScalingSelector<T extends Number> {

    private final ScalingSelector<T> scalingSelector;
    private final double startPower;
    private final double endPower;

    public List<Individual> select(Map<Individual, T> individualToFitness,
                                   Function<Map<Individual, Double>, List<Individual>> selectionFunction) {
        return scalingSelector.select(individualToFitness, selectionFunction, getScalingFunction());
    }

    private BiFunction<T, Map<Individual, T>, Double> getScalingFunction() {
        return (T fitness, Map<Individual, T> individualToFitness) -> scaleFitness(fitness, individualToFitness.values());
    }

    private double scaleFitness(T fitness, Collection<T> fitnesses) {
        double averageFitness = getAverage(fitnesses);
        double medianFitness = getMedian(fitnesses);

        if (medianFitness >= averageFitness) {
            return pow(fitness.doubleValue(), endPower);
        }
        return pow(fitness.doubleValue(), startPower);
    }
}

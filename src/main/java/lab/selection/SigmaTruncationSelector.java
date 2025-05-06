package lab.selection;

import lab.Individual;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static lab.util.CalculationUtils.getAverageFitness;
import static lab.util.MetricUtils.getStandardDeviation;

@RequiredArgsConstructor
public class SigmaTruncationSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final double c;

    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness,
                                   Function<Map<Individual, Double>, List<Individual>> selectionFunction) {
        return scalingSelector.select(individualToFitness, selectionFunction, getScalingFunction());
    }

    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return scalingSelector.getIndividualToScaledFitness(individualToFitness, getScalingFunction());
    }

    private <T extends Number> BiFunction<T, Map<Individual, T>, Double> getScalingFunction() {
        return this::scale;
    }

    private <T extends Number> double scale(T fitness, Map<Individual, T> individualToFitness) {
        double fAvg = getAverageFitness(individualToFitness);
        double sigmaF = getStandardDeviation(individualToFitness.values(), fAvg);

        return fitness.doubleValue() - (fAvg - c * sigmaF);
    }

}
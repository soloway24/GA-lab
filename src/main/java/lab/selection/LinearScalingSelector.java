package lab.selection;

import lab.Individual;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static lab.identifier.SuccessfulRunIdentifier.getBestIndividual;
import static lab.util.CalculationUtils.*;

@RequiredArgsConstructor
public class LinearScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final double ps;

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
        double fMax = getMaxFitness(individualToFitness);

        double b = -(fAvg * ps - fMax) / (ps - 1);
        return fitness.doubleValue() + b;
    }

}
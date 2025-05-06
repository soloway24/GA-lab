package lab.selection.power;

import lab.Individual;
import lab.selection.ScalingSelector;
import lab.selection.SelectionContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.pow;
import static lab.util.CalculationUtils.getAverageFitness;
import static lab.util.CalculationUtils.getMedian;

@RequiredArgsConstructor
public class DynamicPowerScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final double startPower;
    @Getter
    private final double endPower;

    public List<Individual> select(SelectionContext selectionContext,
                                   Function<SelectionContext, List<Individual>> selectionFunction) {
        return scalingSelector.select(selectionContext, selectionFunction, getScalingFunction());
    }

    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return scalingSelector.getIndividualToScaledFitness(selectionContext, getScalingFunction());
    }

    private <T extends Number> BiFunction<T, SelectionContext, Double> getScalingFunction() {
        return this::scale;
    }

    private <T extends Number> double scale(T fitness, SelectionContext selectionContext) {
        double averageFitness = getAverageFitness(selectionContext.getIndividualToFitness());
        double medianFitness = getMedian(selectionContext.getIndividualToFitness().values());

        if (medianFitness >= averageFitness) {
            return pow(fitness.doubleValue(), endPower);
        }
        return pow(fitness.doubleValue(), startPower);
    }
}

package lab.selection.sigmatruncation;

import lab.Individual;
import lab.selection.ScalingSelector;
import lab.selection.SelectionContext;
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
        double fAvg = getAverageFitness(selectionContext.getIndividualToFitness());
        double sigmaF = getStandardDeviation(selectionContext.getIndividualToFitness().values(), fAvg);

        return fitness.doubleValue() - (fAvg - c * sigmaF);
    }

}
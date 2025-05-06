package lab.selection.spanmethod;

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
import static java.util.Optional.ofNullable;
import static lab.util.CalculationUtils.getAverageFitness;
import static lab.util.MetricUtils.getStandardDeviation;

@RequiredArgsConstructor
public class SpanMethodSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final double G;

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
        // TODO should be + 1 ?
        int g = ofNullable(selectionContext.getNi())
                .map(ni -> ni + 1)
                .orElseThrow(() -> new IllegalArgumentException("Ni value is not present in span method selector."));
        double t = 15 * g / G - 10;
        double W = 20 - 16 * (1 / (1 + pow(Math.E, -t)));

        return (fitness.doubleValue() - fAvg) / sigmaF + W;
    }

}
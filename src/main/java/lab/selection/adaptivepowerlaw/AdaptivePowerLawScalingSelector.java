package lab.selection.adaptivepowerlaw;

import lab.Individual;
import lab.selection.ScalingSelector;
import lab.selection.SelectionContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.*;
import static java.util.Optional.ofNullable;
import static lab.util.CalculationUtils.getAverageFitness;
import static lab.util.MetricUtils.getStandardDeviation;

@RequiredArgsConstructor
public class AdaptivePowerLawScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final int G;

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
        double s0 = selectionContext.getS0();

        double p1 = 0.05;
        double p2 = 0.1;
        double s_star = 0.1;
        double a = 0.1;

        int g = ofNullable(selectionContext.getNi())
                .map(ni -> ni + 1)
                .orElseThrow(() -> new IllegalArgumentException("Ni value is not present in span method selector."));

        double tan = tan(g * PI / ((G + 1) * 2.0));
        double tanExponent = p2 * pow(s0 / s_star, a);
        double k = pow((s_star / s0), p1) * pow(tan, tanExponent);

        return pow(fitness.doubleValue(), k);
    }

}
package lab.selection.window;

import lab.Individual;
import lab.selection.ScalingSelector;
import lab.selection.SelectionContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class WindowScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final int windowSize;

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
        double worstFitness = ofNullable(selectionContext.getWorstFitness())
                .orElseThrow(() -> new IllegalArgumentException("No Worst Fitness found in Window Scaling Selector"));
        return fitness.doubleValue() - worstFitness;
    }

}
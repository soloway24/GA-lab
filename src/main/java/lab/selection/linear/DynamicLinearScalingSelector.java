package lab.selection.linear;

import lab.Individual;
import lab.selection.ScalingSelector;
import lab.selection.SelectionContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public class DynamicLinearScalingSelector {

    private final ScalingSelector scalingSelector;
    @Getter
    private final DynamicLinearScaler dynamicLinearScaler;

    public List<Individual> select(SelectionContext selectionContext,
                                   Function<SelectionContext, List<Individual>> selectionFunction) {
        return scalingSelector.select(selectionContext, selectionFunction, getScalingFunction());
    }

    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return scalingSelector.getIndividualToScaledFitness(selectionContext, getScalingFunction());
    }

    private <T extends Number> BiFunction<T, SelectionContext, Double> getScalingFunction() {
        return dynamicLinearScaler::scale;
    }

}
package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DynamicPowerScalingSusSelector<T extends Number> implements Selector<T> {

    private final DynamicPowerScalingSelector<T> dynamicPowerScalingSelector;
    private final SusSelector<Double> susSelector;

    @Override
    public String getName() {
        return "PS SUS";
    }

    @Override
    public List<Individual> select(Map<Individual, T> individualToFitness) {
        return dynamicPowerScalingSelector.select(individualToFitness, susSelector::select);
    }

}

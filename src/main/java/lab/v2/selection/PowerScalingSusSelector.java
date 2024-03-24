package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PowerScalingSusSelector<T extends Number> implements Selector<T> {

    private final PowerScalingSelector<T> powerScalingSelector;
    private final SusSelector<Double> susSelector;

    @Override
    public String getName() {
        return "PS SUS";
    }

    @Override
    public List<Individual> select(Map<Individual, T> individualToFitness) {
        return powerScalingSelector.select(individualToFitness, susSelector::select);
    }

}

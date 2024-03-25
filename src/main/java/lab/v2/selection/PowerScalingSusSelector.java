package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PowerScalingSusSelector implements Selector {

    private final PowerScalingSelector powerScalingSelector;
    private final SusSelector susSelector;

    @Override
    public String getName() {
        return "PS SUS";
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.select(individualToFitness, susSelector::select);
    }

}

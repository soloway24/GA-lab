package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PowerScalingRwsSelector implements Selector {

    private final PowerScalingSelector powerScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public String getName() {
        return "PS RWS " + powerScalingSelector.getPower();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.select(individualToFitness, rwsSelector::select);
    }
}

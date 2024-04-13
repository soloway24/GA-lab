package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectorType.RWS;

@RequiredArgsConstructor
public class PowerScalingRwsSelector implements Selector {

    private final PowerScalingSelector powerScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return RWS;
    }

    @Override
    public String getName() {
        return "PS RWS " + powerScalingSelector.getPower();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.select(individualToFitness, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.scale(individualToFitness);
    }
}

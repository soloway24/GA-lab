package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectorType.RWS;
import static lab.v2.selection.SelectorType.SUS;

@RequiredArgsConstructor
public class DynamicPowerScalingRwsSelector implements Selector {

    private final DynamicPowerScalingSelector dynamicPowerScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return RWS;
    }

    @Override
    public String getName() {
        return "DPS RWS "
                + dynamicPowerScalingSelector.getStartPower()
                + " -> "
                + dynamicPowerScalingSelector.getEndPower();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicPowerScalingSelector.select(individualToFitness, rwsSelector::select);
    }
}

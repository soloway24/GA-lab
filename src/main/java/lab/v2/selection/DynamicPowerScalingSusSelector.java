package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectorType.SUS;

@RequiredArgsConstructor
public class DynamicPowerScalingSusSelector implements Selector {

    private final DynamicPowerScalingSelector dynamicPowerScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SUS;
    }

    @Override
    public String getName() {
        return "DPS SUS "
                + dynamicPowerScalingSelector.getStartPower()
                + " -> "
                + dynamicPowerScalingSelector.getEndPower();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicPowerScalingSelector.select(individualToFitness, susSelector::select);
    }

}

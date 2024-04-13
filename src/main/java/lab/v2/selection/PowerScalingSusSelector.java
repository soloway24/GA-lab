package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectorType.SUS;

@RequiredArgsConstructor
public class PowerScalingSusSelector implements Selector {

    private final PowerScalingSelector powerScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SUS;
    }

    @Override
    public String getName() {
        return "PS SUS " + powerScalingSelector.getPower();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.select(individualToFitness, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.scale(individualToFitness);
    }

}
